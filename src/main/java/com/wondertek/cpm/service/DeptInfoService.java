package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SystemConfig;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.DeptInfoVo;
import com.wondertek.cpm.domain.vo.DeptTree;
import com.wondertek.cpm.repository.DeptInfoDao;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.SystemConfigRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing DeptInfo.
 */
@Service
@Transactional
public class DeptInfoService {

    private final Logger log = LoggerFactory.getLogger(DeptInfoService.class);
    
    @Inject
    private DeptInfoRepository deptInfoRepository;
    
    @Inject
    private DeptInfoDao deptInfoDao;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private SystemConfigRepository systemConfigRepository;
    /**
     * Save a deptInfo.
     *
     * @param deptInfo the entity to save
     * @return the persisted entity
     */
    public DeptInfo save(DeptInfo deptInfo) {
        log.debug("Request to save DeptInfo : {}", deptInfo);
        DeptInfo result = deptInfoRepository.save(deptInfo);
        return result;
    }

    /**
     *  Get all the deptInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<DeptInfo> findAll(Pageable pageable) {
        log.debug("Request to get all DeptInfos");
        Page<DeptInfo> result = deptInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one deptInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DeptInfo findOne(Long id) {
        log.debug("Request to get DeptInfo : {}", id);
        DeptInfo deptInfo = deptInfoRepository.findOne(id);
        return deptInfo;
    }

    /**
     *  Delete the  deptInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DeptInfo : {}", id);
        DeptInfo deptInfo = deptInfoRepository.findOne(id);
        if(deptInfo != null){
        	deptInfo.setStatus(CpmConstants.STATUS_DELETED);
        	deptInfo.setUpdateTime(ZonedDateTime.now());
        	deptInfo.setUpdator(SecurityUtils.getCurrentUserLogin());
        	deptInfoRepository.save(deptInfo);
        }
    }

    /**
     * Search for the deptInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DeptInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DeptInfos for query {}", query);
        Page<DeptInfo> result = null;
        return result;
    }
    /**
     * 获取用户和部门的树形结构数据
     * @return
     */
    @Transactional(readOnly = true)
	public List<DeptTree> getDeptAndUserTree(Integer selectType, Boolean showChild, Boolean showUser, String name) {
		List<DeptTree> returnList = new ArrayList<DeptTree>();
		//查询出所有的用户
		List<User> allUser = null;
		List<DeptInfo> allDeptInfo = null;
		if(StringUtil.isNullStr(name)){
			if(showUser){
				allUser = userRepository.findAllByActivated(Boolean.TRUE);
			}
			//查询出所有的部门
			allDeptInfo = deptInfoRepository.findAllByStatus(CpmConstants.STATUS_VALID);
		}else{
			//找出所有的存在的人名
			if(showUser){
				allUser = userRepository.findAllByActivated(Boolean.TRUE,"%"+name+"%");
			}
			//找出所有的组织，以及上级组织，直至顶部
			//找出所有的组织，方便后面不需要在每次从数据库获取
			Map<Long,DeptInfo> allDeptMap = new HashMap<Long,DeptInfo>();
			List<DeptInfo> allDept = deptInfoRepository.findAllByStatus(CpmConstants.STATUS_VALID);
			if(allDept != null){
				for(DeptInfo deptInfo : allDept){
					allDeptMap.put(deptInfo.getId(), deptInfo);
				}
			}
			//目前需要的组织
			List<Long> selectDeptIds = deptInfoRepository.findAllByStatus(CpmConstants.STATUS_VALID,"%"+name+"%");
			if(allUser != null){
				for(User user : allUser){
					if(user.getDeptId() != null && !selectDeptIds.contains(user.getDeptId())){
						selectDeptIds.add(user.getDeptId());
					}
				}
			}
			//找出子组织的所有上级组织，并封装到list里面去
			allDeptInfo = new ArrayList<DeptInfo>();
			getRecursionSelectDeptInfo(allDeptInfo,allDeptMap,selectDeptIds);
		}
		//放在map中方便切换
		Map<Long,List<User>> deptUsers = sortUserByDept(allUser);
		Map<Long,List<DeptInfo>> childDepts = sortDeptByParent(allDeptInfo);
		
		//获取树形结构
		getDeptAndUserTree(CpmConstants.DEFAULT_DEPT_TOPID,CpmConstants.DEFAULT_BLANK,returnList,deptUsers,childDepts,selectType,showChild);
		return returnList;
	}
    /**
     * 找出所有的指定部门以及上级部门
     */
	private void getRecursionSelectDeptInfo(List<DeptInfo> returnDeptInfo, Map<Long, DeptInfo> allDeptMap,List<Long> selectDeptIds) {
		Map<Long,Long> selectDeptMap = new HashMap<Long,Long>();
		if(selectDeptIds != null){
			DeptInfo deptInfo = null;
			for(Long selectDeptId : selectDeptIds){
				if(allDeptMap.containsKey(selectDeptId) && !selectDeptMap.containsKey(selectDeptId)){
					//添加当前组织
					deptInfo = allDeptMap.get(selectDeptId);
					returnDeptInfo.add(deptInfo);
					selectDeptMap.put(deptInfo.getId(), deptInfo.getId());
					
					//查找上级组织
					getRecursionSelectDeptInfo(returnDeptInfo,allDeptMap,selectDeptMap,deptInfo.getParentId());
				}
			}
		}
	}
	/**
	 * 递归获取组织信息，直到顶级节点
	 */
	private void getRecursionSelectDeptInfo(List<DeptInfo> returnDeptInfo, Map<Long, DeptInfo> allDeptMap,Map<Long, Long> selectDeptMap, Long selectDeptId) {
		if(selectDeptId != null){//不是顶级节点
			DeptInfo deptInfo = null;
			if(allDeptMap.containsKey(selectDeptId) && !selectDeptMap.containsKey(selectDeptId)){
				//添加当前组织
				deptInfo = allDeptMap.get(selectDeptId);
				returnDeptInfo.add(deptInfo);
				selectDeptMap.put(deptInfo.getId(), deptInfo.getId());
				
				//查找上级组织
				getRecursionSelectDeptInfo(returnDeptInfo,allDeptMap,selectDeptMap,deptInfo.getParentId());
			}
		}
	}

	private void getDeptAndUserTree(Long parentId,String parentName, List<DeptTree> returnList, Map<Long, List<User>> deptUsers, 
			Map<Long, List<DeptInfo>> childDepts, Integer selectType, Boolean showChild) {
		//下面的所有用户
		List<User> deptUser = deptUsers.get(parentId);
		if(deptUser != null){
			for(User user : deptUser){
				//添加用户
				returnList.add(new DeptTree(user.getId(),user.getLastName(),parentId,parentName,Boolean.FALSE,
						(selectType == DeptTree.SELECTTYPE_ALL || selectType == DeptTree.SELECTTYPE_USER),
						showChild));
			}
		}
		//下面的部门
		List<DeptInfo> deptInfos = childDepts.get(parentId);
		if(deptInfos != null){
			for(DeptInfo deptInfo : deptInfos){
				//添加部门，以及部门下的部门和人员
				DeptTree deptTree = new DeptTree(deptInfo.getId(),deptInfo.getName(),parentId,parentName,Boolean.TRUE,
										(selectType == DeptTree.SELECTTYPE_ALL || selectType == DeptTree.SELECTTYPE_DEPT),
										showChild);
				List<DeptTree> childs = new ArrayList<DeptTree>();
				getDeptAndUserTree(deptInfo.getId(),deptInfo.getName(),childs,deptUsers,childDepts,selectType,showChild);
				deptTree.setChildren(childs);
				returnList.add(deptTree);
			}
		}
	}

	/**
	 * 获取父节点下面的所有子节点
	 * @return
	 */
	private Map<Long, List<DeptInfo>> sortDeptByParent(List<DeptInfo> allDeptInfo) {
		Map<Long,List<DeptInfo>> childDepts = new HashMap<Long,List<DeptInfo>>();
		if(allDeptInfo != null){
			for(DeptInfo deptInfo : allDeptInfo){
				if(deptInfo.getParentId() != null){
					if(!childDepts.containsKey(deptInfo.getParentId())){
						childDepts.put(deptInfo.getParentId(), new ArrayList<DeptInfo>());
					}
					childDepts.get(deptInfo.getParentId()).add(deptInfo);
				}else{
					if(!childDepts.containsKey(CpmConstants.DEFAULT_DEPT_TOPID)){
						childDepts.put(CpmConstants.DEFAULT_DEPT_TOPID, new ArrayList<DeptInfo>());
					}
					childDepts.get(CpmConstants.DEFAULT_DEPT_TOPID).add(deptInfo);
				}
			}
		}
		return childDepts;
	}

	private Map<Long, List<User>> sortUserByDept(List<User> allUser) {
		Map<Long,List<User>> deptUsers = new HashMap<Long,List<User>>();
		if(allUser != null){
			for(User user : allUser){
				if(user.getDeptId() != null){
					if(!deptUsers.containsKey(user.getDeptId())){
						deptUsers.put(user.getDeptId(), new ArrayList<User>());
					}
					deptUsers.get(user.getDeptId()).add(user);
				}else{
					if(!deptUsers.containsKey(CpmConstants.DEFAULT_DEPT_TOPID)){
						deptUsers.put(CpmConstants.DEFAULT_DEPT_TOPID, new ArrayList<User>());
					}
					deptUsers.get(CpmConstants.DEFAULT_DEPT_TOPID).add(user);
				}
			}
		}
		return deptUsers;
	}
	@Transactional(readOnly = true)
	public Optional<DeptInfo> findOneByParentName(Long parentId, String name) {
		return deptInfoRepository.findOneByParentName(parentId,name);
	}
	@Transactional(readOnly = true)
	public List<String> getExistUserDeptNameByDeptParent(Long deptId, String idPath) {
		return deptInfoDao.getExistUserDeptNameByDeptParent(deptId, idPath);
	}
	@Transactional(readOnly = true)
	public DeptInfoVo getDeptInfo(Long id) {
		return deptInfoDao.getDeptInfo(id);
	}
	/**
	 * 获取可用公司名
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Long> getUsedCompanyInfos() {
		List<DeptInfo> infos = deptInfoRepository.findCompanyByParentId();
		Map<String,Long> returnMap = new HashMap<String,Long>();
		if(infos != null){
			for(DeptInfo deptInfo : infos){
				returnMap.put(deptInfo.getName(), deptInfo.getId());
			}
		}
		return returnMap;
	}
	/**
	 * 获取公司下的所有可用部门
	 * @param companyIds
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, List<DeptInfo>> getUsedDetpInfos(Collection<Long> companyIds) {
		Map<String, List<DeptInfo>> deptInfos = new HashMap<String,List<DeptInfo>>();
		if(companyIds != null){
			String key = null;
			for(Long companyId : companyIds){
				List<DeptInfo> infos = deptInfoRepository.findByIdPath("/"+companyId+"/%");
				for(DeptInfo deptInfo : infos){
					key = companyId + "_" + deptInfo.getName();
					if(!deptInfos.containsKey(key)){
						deptInfos.put(key, new ArrayList<DeptInfo>());
					}
					deptInfos.get(key).add(deptInfo);
				}
			}
		}
		return deptInfos;
	}
	/**
	 * 获取所有部门信息
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<Long,DeptInfo> getAllDeptInfosMap(){
		List<DeptInfo> deptInfos = deptInfoRepository.findAll();
		Map<Long,DeptInfo> returnMap = new HashMap<Long,DeptInfo>();
		if(deptInfos != null){
			for(DeptInfo info : deptInfos){
				returnMap.put(info.getId(), info);
			}
		}
		return returnMap;
	}

	public DeptInfo findDeptInfo(String user_serial_num) {
		
		return deptInfoDao.findDeptInfo(user_serial_num);
	}
	
	/**
	 * 获取销售部门下的所有一级部门
	 * @param type
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DeptInfoVo> getPrimaryDeptInfosByType(Long type){
		List<DeptInfo> deptInfos = deptInfoRepository.findDeptInfosByType(type);	//所有的销售部门信息
		Map<Long,DeptInfo> deptInfosMap = new HashMap<Long,DeptInfo>();				//所有销售部门Map--key:deptId
		if(deptInfos == null){
			return null;
		}
		for(DeptInfo info : deptInfos){
			deptInfosMap.put(info.getId(), info);
		}
		//根据系统配置获取顶级销售部门id
		List<Long> topSaleDeptIds = new ArrayList<Long>();
		SystemConfig systemConfig = systemConfigRepository.findByKey(CpmConstants.DEFAULT_Dept_SALE_TOPID);
		if(systemConfig != null){
			topSaleDeptIds = StringUtil.stringToLongArray(systemConfig.getValue());
		}
		//各个顶级销售部门下的一级部门
		List<DeptInfoVo> primaryDeptInfoVos = new ArrayList<DeptInfoVo>();
		for(Long topId : topSaleDeptIds){
			if(!deptInfosMap.containsKey(topId)){
				continue;
			}
			List<DeptInfo> primaryDeptInfos = deptInfoRepository.findByIdPath(deptInfosMap.get(topId).getIdPath() + topId + "/");
			if (primaryDeptInfos != null) {
				for(DeptInfo info : primaryDeptInfos){
					primaryDeptInfoVos.add(new DeptInfoVo(info, deptInfosMap.get(topId).getName(), null));
				}
			}
		}
		return primaryDeptInfoVos;
	}
}
