package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
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

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.repository.ProjectInfoDao;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ProjectInfoSearchRepository;
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
    private ProjectInfoSearchRepository projectInfoSearchRepository;
    
    @Autowired
    private ProjectInfoDao projectInfoDao;

    /**
     * Save a projectInfo.
     *
     * @param projectInfo the entity to save
     * @return the persisted entity
     */
    public ProjectInfo save(ProjectInfo projectInfo) {
        log.debug("Request to save ProjectInfo : {}", projectInfo);
        ProjectInfo result = projectInfoRepository.save(projectInfo);
        projectInfoSearchRepository.save(result);
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
        projectInfoRepository.delete(id);
        projectInfoSearchRepository.delete(id);
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
        Page<ProjectInfo> result = projectInfoSearchRepository.search(queryStringQuery(query), pageable);
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
}
