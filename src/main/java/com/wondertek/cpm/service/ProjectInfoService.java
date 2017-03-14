package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.repository.ProjectInfoDao;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectUserDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ProjectInfo.
 */
@Service
@Transactional
public class ProjectInfoService {

    private final Logger log = LoggerFactory.getLogger(ProjectInfoService.class);
    
    @Inject
    private ProjectInfoRepository projectInfoRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProjectFinishInfoRepository projectFinishInfoRepository;
    @Autowired
    private ProjectInfoDao projectInfoDao;
    @Inject
    private ProjectUserDao projectUserDao;

    /**
     * Save a projectInfo.
     *
     * @param projectInfo the entity to save
     * @return the persisted entity
     */
    public ProjectInfo save(ProjectInfo projectInfo) {
        log.debug("Request to save ProjectInfo : {}", projectInfo);
        ProjectInfo result = projectInfoRepository.save(projectInfo);
        return result;
    }

    /**
     *  Get all the projectInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectInfos");
        Page<ProjectInfo> result = projectInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectInfo findOne(Long id) {
        log.debug("Request to get ProjectInfo : {}", id);
        ProjectInfo projectInfo = projectInfoRepository.findOne(id);
        return projectInfo;
    }

    /**
     *  Delete the  projectInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectInfo : {}", id);
        ProjectInfo projectInfo = projectInfoRepository.findOne(id);
        if(projectInfo != null){
        	//更新项目人员的离开日期
    		long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
    		projectUserDao.updateLeaveDayByProject(id,leaveDay,SecurityUtils.getCurrentUserLogin());
    		
        	projectInfo.setStatus(ProjectInfo.STATUS_DELETED);
        	projectInfo.setUpdateTime(ZonedDateTime.now());
        	projectInfo.setUpdator(SecurityUtils.getCurrentUserLogin());
        	projectInfoRepository.save(projectInfo);
        }
    }

    /**
     * Search for the projectInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectInfos for query {}", query);
        Page<ProjectInfo> result = null;
        return result;
    }
    /**
     * 查询用户的所有项目，管理人员能看到部门下面所有人员的项目信息
     */
    @Transactional(readOnly = true)
	public List<LongValue> queryUserContract() {
    	List<LongValue> returnList = new ArrayList<LongValue>();
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = projectInfoDao.queryUserContract(user,deptInfo);
    	}
		return returnList;
	}
    @Transactional(readOnly = true)
    public Page<ProjectInfoVo> getUserPage(ProjectInfo projectInfo, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		Page<ProjectInfoVo> page = projectInfoDao.getUserPage(projectInfo,pageable,user,deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ProjectInfoVo>(), pageable, 0);
    	}
    }
    @Transactional(readOnly = true)
	public ProjectInfoVo getUserProjectInfo(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectInfoDao.getUserProjectInfo(id,user,deptInfo);
    	}
		return null;
	}
    @Transactional(readOnly = true)
	public List<LongValue> queryUserContractBudget(Long contractId) {
		List<LongValue> returnList = new ArrayList<LongValue>();
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = projectInfoDao.queryUserContractBudget(user,deptInfo,contractId);
    	}
		return returnList;
	}
    @Transactional(readOnly = true)
	public int checkByBudget(ProjectInfo projectInfo) {
		return projectInfoDao.checkByBudget(projectInfo);
	}
    @Transactional(readOnly = true)
	public boolean checkByProject(String serialNum, Long id) {
		return projectInfoDao.checkByProject(serialNum,id);
	}
    /**
     * 项目完成率
     */
	public int finishProjectInfo(Long id, Double finishRate) {
		String updator = SecurityUtils.getCurrentUserLogin();
		//保存记录
		ProjectFinishInfo projectFinishInfo = new ProjectFinishInfo();
		projectFinishInfo.setCreateTime(ZonedDateTime.now());
		projectFinishInfo.setCreator(updator);
		projectFinishInfo.setFinishRate(finishRate);
		projectFinishInfo.setId(null);
		projectFinishInfo.setProjectId(id);
		projectFinishInfoRepository.save(projectFinishInfo);
		
		return projectInfoDao.finishProjectInfo(id,finishRate,updator);
	}
	/**
	 * 项目结项
	 */
	public int endProjectInfo(Long id) {
		String updator = SecurityUtils.getCurrentUserLogin();
		//保存记录
		ProjectFinishInfo projectFinishInfo = new ProjectFinishInfo();
		projectFinishInfo.setCreateTime(ZonedDateTime.now());
		projectFinishInfo.setCreator(updator);
		projectFinishInfo.setFinishRate(100d);
		projectFinishInfo.setId(null);
		projectFinishInfo.setProjectId(id);
		projectFinishInfoRepository.save(projectFinishInfo);
		
		//更新项目人员的离开日期
		long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
		projectUserDao.updateLeaveDayByProject(id,leaveDay,updator);
		
		return projectInfoDao.endProjectInfo(id,updator);
	}

	public List<LongValue> queryUserProject() {
		List<LongValue> returnList = new ArrayList<LongValue>();
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = projectInfoDao.queryUserProject(user,deptInfo);
    	}
		return returnList;
	}
}
