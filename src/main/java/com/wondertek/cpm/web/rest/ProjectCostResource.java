package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelValue;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.ProjectCostVo;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ProjectCostService;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectCost.
 */
@RestController
@RequestMapping("/api")
public class ProjectCostResource {

    private final Logger log = LoggerFactory.getLogger(ProjectCostResource.class);
        
    @Inject
    private ProjectCostService projectCostService;
    
    @Inject
    private ProjectInfoService projectInfoService;
    
    @Inject
    private ProjectInfoRepository projectInfoRepository;

    /**
     * PUT  /project-costs : Updates an existing projectCost.
     *
     * @param projectCost the projectCost to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectCost,
     * or with status 400 (Bad Request) if the projectCost is not valid,
     * or with status 500 (Internal Server Error) if the projectCost couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-costs")
    @Timed	
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<Boolean> updateProjectCost(@RequestBody ProjectCost projectCost) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ProjectCost : {}", projectCost);
        Boolean isNew = projectCost.getId() == null;
        if(projectCost.getProjectId() == null || projectCost.getType() == null || projectCost.getCostDay() == null
        		|| StringUtil.isNullStr(projectCost.getName()) || projectCost.getTotal() == null || projectCost.getTotal() < 0){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.paramNone", "")).body(null);
        }
        if(projectCost.getType() == ProjectCost.TYPE_HUMAN_COST){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.type1Error", "")).body(null);
        }
        //查看项目是否删除或者结项
        ProjectInfo projectInfo = projectInfoService.findOne(projectCost.getProjectId());
        if(projectInfo.getStatus() != ProjectInfo.STATUS_ADD){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.projectError", "")).body(null);
        }
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(isNew){
        	projectCost.setStatus(CpmConstants.STATUS_VALID);
        	projectCost.setCreateTime(updateTime);
        	projectCost.setCreator(updator);
        }else{
        	ProjectCostVo projectCostVo = projectCostService.getProjectCost(projectCost.getId());
        	if(projectCostVo == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.noPerm", "")).body(null);
        	}
        	ProjectCost old = projectCostService.findOne(projectCost.getId());
        	if(old == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.idNone", "")).body(null);
        	}else if(old.getProjectId() != projectCost.getProjectId().longValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.projectIdError", "")).body(null);
        	}else if(old.getStatus() == CpmConstants.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.statue2Error", "")).body(null);
        	}else if(old.getType().intValue() != projectCost.getType()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.type1Error", "")).body(null);
        	}
        	projectCost.setStatus(old.getStatus());
        	projectCost.setCreateTime(old.getCreateTime());
        	projectCost.setCreator(old.getCreator());
        }
        projectCost.setUpdateTime(updateTime);
        projectCost.setUpdator(updator);
        
        ProjectCost result = projectCostService.save(projectCost);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("projectCost", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("projectCost", result.getId().toString()))
        			.body(isNew);
        }
    }

    /**
     * GET  /project-costs : get all the projectCosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectCosts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<List<ProjectCostVo>> getAllProjectCosts(
    		@RequestParam(value = "projectId",required=false) Long projectId, 
    		@RequestParam(value = "type",required=false) Integer type, 
    		@RequestParam(value = "name",required=false) String name, 
    		@RequestParam(value = "pageType",required=true) Integer pageType, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ProjectCosts by projectId : {}, type : {}, "
        		+ "name : {}", projectId, type, name);
        ProjectCost projectCost = new ProjectCost();
        projectCost.setProjectId(projectId);
        projectCost.setType(type);
        projectCost.setName(name);
        
        Page<ProjectCostVo> page = projectCostService.getUserPage(projectCost,pageType,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-costs/:id : get the "id" projectCost.
     *
     * @param id the id of the projectCost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectCost, or with status 404 (Not Found)
     */
    @GetMapping("/project-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<ProjectCostVo> getProjectCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ProjectCost : {}", id);
//        ProjectCost projectCost = projectCostService.findOne(id);
        ProjectCostVo projectCost = projectCostService.getProjectCost(id);
        return Optional.ofNullable(projectCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-costs/:id : delete the "id" projectCost.
     *
     * @param id the id of the projectCost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<Void> deleteProjectCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ProjectCost : {}", id);
        ProjectCostVo projectCost = projectCostService.getProjectCost(id);
        if(projectCost == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.noPerm", "")).body(null);
        }
        if(projectCost.getStatus() == CpmConstants.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.delete.status2Error", "")).body(null);
        }
        if(projectCost.getType() == ProjectCost.TYPE_HUMAN_COST){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.delete.type1Error", "")).body(null);
        }
        //查看项目是否删除或者结项
        ProjectInfo projectInfo = projectInfoService.findOne(projectCost.getProjectId());
        if(projectInfo.getStatus() != ProjectInfo.STATUS_ADD){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.projectError", "")).body(null);
        }
        projectCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectCost", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-costs?query=:query : search for the projectCost corresponding
     * to the query.
     *
     * @param query the query of the projectCost search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<List<ProjectCost>> searchProjectCosts(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search for a page of ProjectCosts for query {}", query);
        Page<ProjectCost> page = projectCostService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    @GetMapping("/project-costs/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_COST)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value="filePath",required = true) String filePath){
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for filePath : {}",filePath);
    	List<ProjectCost> projectCosts = null;
    	CpmResponse cpmResponse = new CpmResponse();
    	try {
    		//校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.projectCost.upload.requiredError"));
			}
			//从第一行读取，最多读取10个sheet，最多读取7列
        	int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,7);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.projectCost.upload.requiredError"));
			}
			//初始化
			//初始化项目信息
			List<ProjectInfo> projectInfoList = projectInfoRepository.findAll();
			Map<String,ProjectInfo> projectInfoMap = new HashMap<String,ProjectInfo>();
			for(ProjectInfo info : projectInfoList){
				projectInfoMap.put(info.getSerialNum(), info);
			}
			//其他信息
			projectCosts = new ArrayList<ProjectCost>();
			String updator = SecurityUtils.getCurrentUserLogin();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			for (ExcelValue excelValue : lists) {
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty()) {//每个sheet也可能没有数据，空sheet
					continue;
				}
				rowNum = 1;//都是从第一行读取的
				for(List<Object> ls : excelValue.getVals()){
					rowNum ++;
					if(ls == null){//每个sheet里面也可能有空行
						continue;
					}
					try {
						ProjectCost projectCost = new ProjectCost();
						projectCost.setStatus(CpmConstants.STATUS_VALID);
						projectCost.setCreator(updator);
						projectCost.setCreateTime(ZonedDateTime.now());
						projectCost.setUpdator(updator);
						projectCost.setUpdateTime(projectCost.getCreateTime());
						//校验第一列 项目编号
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.projectCost.upload.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						ProjectInfo info = projectInfoMap.get(val.toString());
						if(info == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.serialNumError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(info.getStatus() != ProjectInfo.STATUS_ADD){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.statusError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						projectCost.setProjectId(info.getId());
						
						//第二列  项目名称(不需要读取，可为空)
						columnNum++;
						
						//校验第三列 报销名称
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						projectCost.setName(val.toString());
						
						//校验第四列 报销类型
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						if(val.equals("差旅")){
							projectCost.setType(ProjectCost.TYPE_TRAVEL_COST);
						}else if(val.equals("采购")){
							projectCost.setType(ProjectCost.TYPE_PURCHASE_COST);
						}else if(val.equals("商务")){
							projectCost.setType(ProjectCost.TYPE_BUSINESS_COST);
						}else if(val.equals("工时")){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.typeError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else{
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第五列  报销日期
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){//date
							projectCost.setCostDay(StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, (Date)val)));
						}else if(val instanceof Double){//double
							projectCost.setCostDay(((Double)val).longValue());
						}else{//String
							projectCost.setCostDay(StringUtil.nullToLong(val));
						}
						if(projectCost.getCostDay() == 0){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第六列 报销金额
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							projectCost.setTotal((Double)val);
						}else{//String
							projectCost.setTotal(StringUtil.nullToCloneDouble(val));
							if(projectCost.getTotal() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.projectCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//校验第七行 描述
						columnNum++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if(val != null){
								projectCost.setCostDesc(val.toString());
							}
						}
						projectCosts.add(projectCost);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//校验完成后，入库处理
			projectCostService.saveOrUpdateUploadRecord(projectCosts);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.TRUE)
						.setMsgKey("cpmApp.projectCost.upload.handleSucc"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.projectCost.upload.handleError"));
		}
    }
}
