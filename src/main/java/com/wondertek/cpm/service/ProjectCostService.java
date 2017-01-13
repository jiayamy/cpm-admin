package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectCostVo;
import com.wondertek.cpm.repository.ProjectCostDao;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ProjectCost.
 */
@Service
@Transactional
public class ProjectCostService {

    private final Logger log = LoggerFactory.getLogger(ProjectCostService.class);
    
    @Inject
    private ProjectCostRepository projectCostRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProjectCostDao projectCostDao;

//    @Inject
//    private ProjectCostSearchRepository projectCostSearchRepository;

    /**
     * Save a projectCost.
     *
     * @param projectCost the entity to save
     * @return the persisted entity
     */
    public ProjectCost save(ProjectCost projectCost) {
        log.debug("Request to save ProjectCost : {}", projectCost);
        ProjectCost result = projectCostRepository.save(projectCost);
//        projectCostSearchRepository.save(result);
        return result;
    }
    /**
     * 查询列表
     * @return
     */
    public Page<ProjectCostVo> getUserPage(ProjectCost projectCost, Pageable pageable) {
    	log.debug("Request to get all ProjectCosts");
        
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectCostDao.getUserPage(projectCost,user,deptInfo,pageable);
    	}
    	
    	return new PageImpl(new ArrayList<ProjectCostVo>(), pageable, 0);
	}

    /**
     *  Get one projectCost by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectCost findOne(Long id) {
        log.debug("Request to get ProjectCost : {}", id);
        ProjectCost projectCost = projectCostRepository.findOne(id);
        return projectCost;
    }

    /**
     *  Delete the  projectCost by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectCost : {}", id);
        ProjectCost cost = projectCostRepository.findOne(id);
        if(cost != null){
        	cost.setStatus(CpmConstants.STATUS_DELETED);
        	cost.setUpdateTime(ZonedDateTime.now());
        	cost.setUpdator(SecurityUtils.getCurrentUserLogin());
        	projectCostRepository.save(cost);
        }
    }

    /**
     * Search for the projectCost corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectCost> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectCosts for query {}", query);
        Page<ProjectCost> result = null;
//        Page<ProjectCost> result = projectCostSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public ProjectCostVo getProjectCost(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectCostDao.getProjectCost(user,deptInfo,id);
    	}
    	return null;
	}
}
