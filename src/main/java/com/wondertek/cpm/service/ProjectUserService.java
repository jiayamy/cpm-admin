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
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectUserVo;
import com.wondertek.cpm.repository.ProjectUserDao;
import com.wondertek.cpm.repository.ProjectUserRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ProjectUser.
 */
@Service
@Transactional
public class ProjectUserService {

    private final Logger log = LoggerFactory.getLogger(ProjectUserService.class);
    
    @Inject
    private ProjectUserRepository projectUserRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Autowired
    private ProjectUserDao projectUserDao;

    /**
     * Save a projectUser.
     *
     * @param projectUser the entity to save
     * @return the persisted entity
     */
    public ProjectUser save(ProjectUser projectUser) {
        log.debug("Request to save ProjectUser : {}", projectUser);
        ProjectUser result = projectUserRepository.save(projectUser);
        return result;
    }

    /**
     *  Get all the projectUsers.
     */
    @Transactional(readOnly = true) 
    public Page<ProjectUserVo> getUserPage(ProjectUser projectUser, Pageable pageable) {
        log.debug("Request to get all ProjectUsers");
        
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectUserDao.getUserPage(projectUser,user,deptInfo,pageable);
    	}
    	
    	return new PageImpl(new ArrayList<ProjectUserVo>(), pageable, 0);
    }

    /**
     *  Get one projectUser by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectUser findOne(Long id) {
        log.debug("Request to get ProjectUser : {}", id);
        ProjectUser projectUser = projectUserRepository.findOne(id);
        return projectUser;
    }

    /**
     *  Delete the  projectUser by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectUser : {}", id);
        ProjectUser projectUser = projectUserRepository.findOne(id);
        if(projectUser != null){
        	long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
        	if(projectUser.getLeaveDay() == null || projectUser.getLeaveDay() > leaveDay ){
        		projectUser.setLeaveDay(leaveDay);
        		if(projectUser.getJoinDay() > leaveDay){
        			projectUser.setJoinDay(leaveDay);
        		}
        		projectUser.setUpdateTime(ZonedDateTime.now());
        		projectUser.setUpdator(SecurityUtils.getCurrentUserLogin());
        		projectUserRepository.save(projectUser);
        	}
        }
    }

    /**
     * Search for the projectUser corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectUser> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectUsers for query {}", query);
        Page<ProjectUser> result = null;
        return result;
    }
    @Transactional(readOnly = true)
	public ProjectUserVo getProjectUser(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectUserDao.getProjectUser(user,deptInfo,id);
    	}
    	return null;
	}
	public boolean checkUserExist(ProjectUser projectUser) {
		List<ProjectUser> list = projectUserRepository.findByUserId(projectUser.getUserId(),projectUser.getProjectId());
		if(list != null){
			long joinDay = 0;
			long id = projectUser.getId() == null ? 0 : projectUser.getId().longValue();
			
			for(ProjectUser tmp : list){
				joinDay = tmp.getJoinDay().longValue();
				if(projectUser.getId() != null && id == tmp.getId()){
					continue;
				}else if(joinDay == projectUser.getJoinDay()){
					return true;
				}else if(joinDay < projectUser.getJoinDay() && (tmp.getLeaveDay() == null || tmp.getLeaveDay().longValue() >= projectUser.getJoinDay())){
					return true;
				}else if(joinDay > projectUser.getJoinDay() && (projectUser.getLeaveDay() == null || projectUser.getLeaveDay() >= joinDay)){
					return true;
				}
			}
		}
		return false;
	}

	public List<ProjectUserVo> getProjectUserList(ProjectUser searchParams) {
		  log.debug("Request to get all ProjectUsers");
	        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
	    	if(objs != null && !objs.isEmpty()){
	    		Object[] o = objs.get(0);
	    		User user = (User) o[0];
	    		DeptInfo deptInfo = (DeptInfo) o[1];
	    		return projectUserDao.getProjectUserList(searchParams,user,deptInfo);
	    	}
	    	return null;
	}
}
