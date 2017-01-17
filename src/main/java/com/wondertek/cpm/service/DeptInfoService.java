package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.DeptInfoVo;
import com.wondertek.cpm.domain.vo.DeptTree;
import com.wondertek.cpm.repository.DeptInfoDao;
import com.wondertek.cpm.repository.DeptInfoRepository;
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
//    @Inject
//    private DeptInfoSearchRepository deptInfoSearchRepository;

    /**
     * Save a deptInfo.
     *
     * @param deptInfo the entity to save
     * @return the persisted entity
     */
    public DeptInfo save(DeptInfo deptInfo) {
        log.debug("Request to save DeptInfo : {}", deptInfo);
        DeptInfo result = deptInfoRepository.save(deptInfo);
//        deptInfoSearchRepository.save(result);
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
//		deptInfoSearchRepository.delete(id);
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
//        Page<DeptInfo> result = deptInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    /**
     * 获取用户和部门的树形结构数据
     * @return
     */
	public List<DeptTree> getDeptAndUserTree(Integer selectType, Boolean showChild, Boolean showUser) {
		List<DeptTree> returnList = new ArrayList<DeptTree>();
		//查询出所有的用户
		List<User> allUser = null;
		if(showUser){
			allUser = userRepository.findAllByActivated(Boolean.TRUE);
		}
		//查询出所有的部门
		List<DeptInfo> allDeptInfo = deptInfoRepository.findAllByStatus(CpmConstants.STATUS_VALID);
		
		//放在map中方便切换
		Map<Long,List<User>> deptUsers = sortUserByDept(allUser);
		Map<Long,List<DeptInfo>> childDepts = sortDeptByParent(allDeptInfo);
		
		//获取树形结构
		getDeptAndUserTree(CpmConstants.DEFAULT_DEPT_TOPID,CpmConstants.DEFAULT_BLANK,returnList,deptUsers,childDepts,selectType,showChild);
		return returnList;
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

	public Optional<DeptInfo> findOneByParentName(Long parentId, String name) {
		return deptInfoRepository.findOneByParentName(parentId,name);
	}

	public List<String> getExistUserDeptNameByDeptParent(Long deptId, String idPath) {
		return deptInfoDao.getExistUserDeptNameByDeptParent(deptId, idPath);
	}

	public DeptInfoVo getDeptInfo(Long id) {
		return deptInfoDao.getDeptInfo(id);
	}
}
