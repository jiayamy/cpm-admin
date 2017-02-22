package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectInfo.
 */
@RestController
@RequestMapping("/api")
public class ProjectInfoResource {

    private final Logger log = LoggerFactory.getLogger(ProjectInfoResource.class);
        
    @Inject
    private ProjectInfoService projectInfoService;

    @PutMapping("/project-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<ProjectInfo> updateProjectInfo(@RequestBody ProjectInfo projectInfo) throws URISyntaxException {
        log.debug("REST request to update ProjectInfo : {}", projectInfo);
        Boolean isNew = projectInfo.getId() == null;
        //基本校验
        if(projectInfo.getBudgetId() == null || projectInfo.getContractId() == null
        		|| StringUtil.isNullStr(projectInfo.getPm()) || StringUtil.isNullStr(projectInfo.getDept())
        		|| StringUtil.isNullStr(projectInfo.getSerialNum()) || StringUtil.isNullStr(projectInfo.getName())
        		|| projectInfo.getStartDay() == null || projectInfo.getEndDay() == null || projectInfo.getBudgetTotal() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.requriedError", "")).body(null);
        }
        if(isNew){
        	if(projectInfo.getPmId() == null || projectInfo.getDeptId() == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.requriedError", "")).body(null);
        	}
        }
        //结束时间校验
        if(projectInfo.getEndDay() != null && projectInfo.getEndDay().isBefore(projectInfo.getStartDay())){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.endDayError", "")).body(null);
        }
        //新增的时候，开始日期应在当天日期之后，暂不校验
        
        //查看项目预算是否已经被使用
        int count = projectInfoService.checkByBudget(projectInfo);
        if(count > 0){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.error" + count, "")).body(null);
        }
        boolean isExist = projectInfoService.checkByProject(projectInfo.getSerialNum(),projectInfo.getId());
        if(isExist){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.existSerialNum" + count, "")).body(null);
        }
        //查看该用户是否有修改的权限
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(!isNew){
        	//是否有权限
        	ProjectInfoVo projectInfoVo = projectInfoService.getUserProjectInfo(projectInfo.getId());
        	if(projectInfoVo == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.noPerm", "")).body(null);
        	}
        	//是否已删除或者已结项
        	if(projectInfoVo.getStatus() == ProjectInfo.STATUS_CLOSED || projectInfoVo.getStatus() == ProjectInfo.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.statusError", "")).body(null);
        	}
        	//
        	ProjectInfo oldProjectInfo = projectInfoService.findOne(projectInfo.getId());
        	projectInfo.setBudgetId(oldProjectInfo.getBudgetId());
        	projectInfo.setContractId(oldProjectInfo.getContractId());
        	if(projectInfo.getPmId() == null){
        		projectInfo.setPmId(oldProjectInfo.getPmId());
        		projectInfo.setPm(oldProjectInfo.getPm());
        	}
        	if(projectInfo.getDeptId() == null){
        		projectInfo.setDeptId(oldProjectInfo.getDeptId());
        		projectInfo.setDept(oldProjectInfo.getDept());
        	}
        	if(oldProjectInfo.getStartDay().isBefore(projectInfo.getStartDay())){
        		projectInfo.setStartDay(oldProjectInfo.getStartDay());
        	}
        	//不变的
        	projectInfo.setCreateTime(oldProjectInfo.getCreateTime());
        	projectInfo.setCreator(oldProjectInfo.getCreator());
        	projectInfo.setStatus(oldProjectInfo.getStatus());
        	projectInfo.setFinishRate(oldProjectInfo.getFinishRate());
        	projectInfo.setContractId(oldProjectInfo.getContractId());
        	projectInfo.setBudgetId(oldProjectInfo.getBudgetId());
        }else{
        	projectInfo.setCreateTime(updateTime);
        	projectInfo.setCreator(updator);
        	projectInfo.setStatus(ProjectInfo.STATUS_ADD);
        	projectInfo.setFinishRate(0d);
        }
        projectInfo.setUpdateTime(updateTime);
        projectInfo.setUpdator(updator);
        
        ProjectInfo result = projectInfoService.save(projectInfo);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("projectInfo", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("projectInfo", projectInfo.getId().toString()))
        			.body(result);
        }
    }

    @GetMapping("/project-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<List<ProjectInfoVo>> getAllProjectInfos(
    		@RequestParam(value = "contractId",required=false) String contractId, 
    		@RequestParam(value = "serialNum",required=false) String serialNum, 
    		@RequestParam(value = "name",required=false) String name, 
    		@RequestParam(value = "status",required=false) String status, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectInfos");
//        Page<ProjectInfo> page = projectInfoService.findAll(pageable);
        
        ProjectInfo projectInfo = new ProjectInfo();
        if(!StringUtil.isNullStr(contractId)){
        	projectInfo.setContractId(StringUtil.nullToLong(contractId));
        }
        if(!StringUtil.isNullStr(serialNum)){
        	projectInfo.setSerialNum(serialNum);
        }
        if(!StringUtil.isNullStr(name)){
        	projectInfo.setName(name);
        }
        if(!StringUtil.isNullStr(status)){
        	projectInfo.setStatus(StringUtil.nullToInteger(status));
        }
        
        Page<ProjectInfoVo> page = projectInfoService.getUserPage(projectInfo, pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/project-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<ProjectInfoVo> getProjectInfo(@PathVariable Long id) {
        log.debug("REST request to get ProjectInfo : {}", id);
//        ProjectInfo projectInfo = projectInfoService.findOne(id);
        ProjectInfoVo projectInfo = projectInfoService.getUserProjectInfo(id);
        
        return Optional.ofNullable(projectInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-infos/:id : delete the "id" projectInfo.
     *
     * @param id the id of the projectInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<Void> deleteProjectInfo(@PathVariable Long id) {
        log.debug("REST request to delete ProjectInfo : {}", id);
        ProjectInfoVo projectInfo = projectInfoService.getUserProjectInfo(id);
        if(projectInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.noPerm", "")).body(null);
        }
        if(projectInfo.getStatus() == ProjectInfo.STATUS_CLOSED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.delete.status2Error", "")).body(null);
        }else if(projectInfo.getStatus() == ProjectInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.delete.status3Error", "")).body(null);
        }
        projectInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectInfo", id.toString())).build();
    }

    @GetMapping("/project-infos/queryUserContract")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException {
        log.debug("REST request to queryUserContract");
        List<LongValue> list = projectInfoService.queryUserContract();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @GetMapping("/project-infos/queryUserContractBudget")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContractBudget(@RequestParam(value = "contractId") String contractId) throws URISyntaxException {
        log.debug("REST request to queryUserContract");
        Long contractIdLong = StringUtil.nullToCloneLong(contractId);
        if(contractIdLong == null){
        	return new ResponseEntity<>(new ArrayList<LongValue>(), null, HttpStatus.OK);
        }
        List<LongValue> list = projectInfoService.queryUserContractBudget(contractIdLong);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @GetMapping("/project-infos/end")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<ProjectInfo> endProjectInfo(@RequestParam(value = "id") Long id) throws URISyntaxException {
    	log.debug("REST request to endProjectInfo");
    	//有没权限
    	ProjectInfoVo projectInfo = projectInfoService.getUserProjectInfo(id);
        if(projectInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.noPerm", "")).body(null);
        }
        if(projectInfo.getStatus() == ProjectInfo.STATUS_CLOSED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.end.status2Error", "")).body(null);
        }else if(projectInfo.getStatus() == ProjectInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.end.status3Error", "")).body(null);
        }
        projectInfoService.endProjectInfo(id);
        
    	return ResponseEntity.ok()
    			.headers(HeaderUtil.createAlert("cpmApp.projectInfo.end.success", projectInfo.getId().toString()))
    			.body(null);
    }
    
    @GetMapping("/project-infos/finish")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<ProjectInfo> finishProjectInfo(
    		@RequestParam(value = "id") Long id,
    		@RequestParam(value = "finishRate") Double finishRate
    		) throws URISyntaxException {
    	log.debug("REST request to endProjectInfo");
    	//有没权限
    	ProjectInfoVo projectInfo = projectInfoService.getUserProjectInfo(id);
        if(projectInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.noPerm", "")).body(null);
        }
        if(finishRate == null || finishRate < 0){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.finish.minError", "")).body(null);
        }
        if(finishRate > 100){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.finish.maxError", "")).body(null);
        }
        if(projectInfo.getStatus() == ProjectInfo.STATUS_CLOSED || projectInfo.getStatus() == ProjectInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.finish.statusError", "")).body(null);
        }
        projectInfoService.finishProjectInfo(id,finishRate);
        
    	return ResponseEntity.ok()
    			.headers(HeaderUtil.createAlert("cpmApp.projectInfo.finish.success", projectInfo.getId().toString()))
    			.body(null);
    }
    
    @GetMapping("/project-infos/queryUserProject")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserProject() throws URISyntaxException {
        log.debug("REST request to queryUserContract");
        List<LongValue> list = projectInfoService.queryUserProject();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
}
