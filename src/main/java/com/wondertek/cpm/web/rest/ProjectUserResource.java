package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.vo.ProjectUserVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ProjectUserService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectUser.
 */
@RestController
@RequestMapping("/api")
public class ProjectUserResource {

    private final Logger log = LoggerFactory.getLogger(ProjectUserResource.class);
        
    @Inject
    private ProjectUserService projectUserService;

    /**
     * PUT  /project-users : Updates an existing projectUser.
     *
     * @param projectUser the projectUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectUser,
     * or with status 400 (Bad Request) if the projectUser is not valid,
     * or with status 500 (Internal Server Error) if the projectUser couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
    public ResponseEntity<Boolean> updateProjectUser(@RequestBody ProjectUser projectUser) throws URISyntaxException {
        log.debug("REST request to update ProjectUser : {}", projectUser);
        Boolean isNew = projectUser.getId() == null;
        if(projectUser.getProjectId() == null || projectUser.getUserId() == null || StringUtil.isNullStr(projectUser.getUserName())
        		 || StringUtil.isNullStr(projectUser.getUserRole()) || projectUser.getJoinDay() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.paramNone", "")).body(null);
        }
        if(projectUser.getLeaveDay() != null && projectUser.getLeaveDay().longValue() < projectUser.getJoinDay()){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.dayError", "")).body(null);
        }
        //查看用户是否被添加
        boolean isExist = projectUserService.checkUserExist(projectUser);
        if(isExist){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.userIdError", "")).body(null);
        }
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(isNew){
        	projectUser.setCreateTime(updateTime);
        	projectUser.setCreator(updator);
        }else{
        	ProjectUserVo projectUserVo = projectUserService.getProjectUser(projectUser.getId());
        	if(projectUserVo == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.noPerm", "")).body(null);
        	}
        	ProjectUser old = projectUserService.findOne(projectUser.getId());
        	if(old == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.idNone", "")).body(null);
        	}else if(old.getProjectId() != projectUser.getProjectId().longValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.projectIdError", "")).body(null);
        	}
        	projectUser.setCreateTime(old.getCreateTime());
        	projectUser.setCreator(old.getCreator());
        }
        projectUser.setUpdateTime(updateTime);
        projectUser.setUpdator(updator);
        
        ProjectUser result = projectUserService.save(projectUser);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("projectUser", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("projectUser", result.getId().toString()))
        			.body(isNew);
        }
    }

    /**
     * GET  /project-users : get all the projectUsers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectUsers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
    public ResponseEntity<List<ProjectUserVo>> getAllProjectUsers(
    		@RequestParam(value = "projectId",required=false) Long projectId, 
    		@RequestParam(value = "userId",required=false) Long userId, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
    	
        log.debug("REST request to get a page of ProjectUsers");
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProjectId(projectId);
        projectUser.setUserId(userId);
        
        Page<ProjectUserVo> page = projectUserService.getUserPage(projectUser,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-users/:id : get the "id" projectUser.
     *
     * @param id the id of the projectUser to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectUser, or with status 404 (Not Found)
     */
    @GetMapping("/project-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
    public ResponseEntity<ProjectUserVo> getProjectUser(@PathVariable Long id) {
        log.debug("REST request to get ProjectUser : {}", id);
        ProjectUserVo projectUserVo = projectUserService.getProjectUser(id);
        return Optional.ofNullable(projectUserVo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-users/:id : delete the "id" projectUser.
     *
     * @param id the id of the projectUser to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
    public ResponseEntity<Void> deleteProjectUser(@PathVariable Long id) {
        log.debug("REST request to delete ProjectUser : {}", id);
        ProjectUserVo projectUserVo = projectUserService.getProjectUser(id);
        if(projectUserVo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectUser.save.noPerm", "")).body(null);
        }
        projectUserService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectUser", id.toString())).build();
    }
    
    
}
