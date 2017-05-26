package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.wondertek.cpm.ExcelWrite;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.domain.vo.UserBaseVo;
import com.wondertek.cpm.repository.ContractBudgetRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
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
    
    @Inject
    private ContractBudgetRepository contractBudgetRepository;
    
    @Inject
    private ContractInfoService contractInfoService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private DeptInfoService deptInfoService;
    
    @Inject
    private ContractBudgetService contractBudgetService;

    @PutMapping("/project-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<ProjectInfo> updateProjectInfo(@RequestBody ProjectInfo projectInfo) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ProjectInfo : {}", projectInfo);
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
        	ContractBudget contractBudget = contractBudgetRepository.findOneById(projectInfo.getBudgetId());
	        if (contractBudget == null) {
	        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.saveError", "")).body(null);
			}
	        if (contractBudget.getStatus() == ContractBudget.STATUS_DELETED) {
	        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.statue2CreateError", "")).body(null);
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
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.existSerialNum", "")).body(null);
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
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ProjectInfos by contractId : {}, serialNum : {}, "
        		+ "name : {}, status : {}", contractId, serialNum, name, status);
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
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ProjectInfo : {}", id);
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
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ProjectInfo : {}", id);
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
    /**
     * 获取项目经理能看到的合同列表
     * @return
     */
    @GetMapping("/project-infos/queryUserContract")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContract");
        List<LongValue> list = projectInfoService.queryUserContract();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    /**
     * 获取项目经理能看到的合同上的内部采购单列表
     */
    @GetMapping("/project-infos/queryUserContractBudget")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContractBudget(@RequestParam(value = "contractId") String contractId) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContract by contractId : {}", contractId);
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
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to endProjectInfo by id : {}", id);
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
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to endProjectInfo by id : {}, finishRate : {}", id, finishRate);
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
    /**
     * 获取项目经理能看到的项目列表
     */
    @GetMapping("/project-infos/queryUserProject")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserProject() throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContract");
        List<LongValue> list = projectInfoService.queryUserProject();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @GetMapping("/project-infos/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value="filePath",required=true) String filePath){
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for filePath : {}",filePath);
    	List<ProjectInfo> projectInfos = null;
    	CpmResponse cpmResponse = new CpmResponse();
    	try {
    		//检验文件是否存在
    		File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
    		if(!file.exists() || !file.isFile()){
    			return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.projectInfo.upload.requiredError"));
    		}
    		//从第一行读取，最多读取10个sheet，最多读取8列
    		int startNum = 1;
    		List<ExcelValue> lists = ExcelUtil.readExcel(file, startNum, 10, 8);
    		if(lists == null || lists.isEmpty()){
    			return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.projectInfo.upload.requiredError"));
    		}
    		//初始化
    		//初始化合同信息
    		Map<String,ContractInfo> contractInfosMap = contractInfoService.getContractInfoMapBySerialnum();
    		//初始化项目信息
    		Map<String,Long> projectInfosMap = projectInfoService.getProjectInfo();
    		//初始化人员信息
    		Map<String,UserBaseVo> userBaseVoMap = userService.getAllUser();
    		//初始化部门信息
    		Map<Long,DeptInfo> deptInfosMap = deptInfoService.getAllDeptInfosMap();
    		//其它信息
    		projectInfos = new ArrayList<ProjectInfo>();
    		String updator = SecurityUtils.getCurrentUserLogin();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			Map<String,Integer> existMap = new HashMap<String,Integer>();
			for(ExcelValue excelValue : lists){
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty()) {//每个sheet也可能没有数据，空sheet
					continue;
				}
				rowNum = 1;//都是从第一行读取的
				for(List<Object> ls : excelValue.getVals()){
					rowNum ++;
					if(ls == null){
						continue;
					}
					try {
						ProjectInfo projectInfo = new ProjectInfo();
						projectInfo.setStatus(ProjectInfo.STATUS_ADD);
						projectInfo.setFinishRate(0D);
						projectInfo.setCreator(updator);
						projectInfo.setCreateTime(ZonedDateTime.now());
						projectInfo.setUpdator(updator);
						projectInfo.setUpdateTime(projectInfo.getCreateTime());
						//检验第一列  项目编号
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							DecimalFormat format = new DecimalFormat("0");
							projectInfo.setSerialNum(format.format(val));
						}else{//String
							projectInfo.setSerialNum(StringUtil.nullToString(val.toString()));
						}
						if(projectInfosMap.containsKey(projectInfo.getSerialNum())){//检验是否已存在项目编号
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.serialNumExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						//检查导入表中是否有重复的记录
						if(existMap.containsKey(projectInfo.getSerialNum())){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.recordExistError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						existMap.put(projectInfo.getSerialNum(), 1);
						
						//检验第二列  项目名称
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							val = ((Double)val).longValue();
						}
						projectInfo.setName(val.toString());
						
						//检验第三列  合同编号
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							DecimalFormat format = new DecimalFormat("0");
							val = format.format(val);
						}
						if(!contractInfosMap.containsKey(StringUtil.nullToString(val.toString()))){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.serialNumError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						projectInfo.setContractId(contractInfosMap.get(StringUtil.nullToString(val.toString())).getId());
						
						//检验第四列  项目经理工号
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							DecimalFormat format = new DecimalFormat("0");
							val = format.format(val);
						}
						String userSerialNum = StringUtil.nullToString(val.toString());
						if(!userBaseVoMap.containsKey(userSerialNum)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.serialNumError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(userBaseVoMap.get(userSerialNum).getDeptId() == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.userDeptNoExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(!deptInfosMap.containsKey(userBaseVoMap.get(userSerialNum).getDeptId())){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.userDeptNoExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						//填充项目经理id
						projectInfo.setPmId(userBaseVoMap.get(userSerialNum).getId());
						//第五列 填充项目经理(不用检验，根据userBase走)
						columnNum ++;
						projectInfo.setPm(userBaseVoMap.get(userSerialNum).getLastName());
						//填充项目经理所属部门id
						projectInfo.setDeptId(userBaseVoMap.get(userSerialNum).getDeptId());
						//填充项目经理所属部门
						projectInfo.setDept(deptInfosMap.get(projectInfo.getDeptId()).getName());
						
						//检验第六列  开始日期
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){
							projectInfo.setStartDay(((Date)val).toInstant().atZone(ZoneId.systemDefault()));
						}else {
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//检验第七列  结束日期
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){
							projectInfo.setEndDay(((Date)val).toInstant().atZone(ZoneId.systemDefault()));
						}else {
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						if(Date.from(projectInfo.getEndDay().toInstant()).getTime() < Date.from(projectInfo.getStartDay().toInstant()).getTime()){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dateError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//检验第八列  预算金额
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							projectInfo.setBudgetTotal((Double)val);
						}else{//String
							projectInfo.setBudgetTotal(StringUtil.nullToDouble(val));
						}
						
						projectInfos.add(projectInfo);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.projectInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//校验完毕后，入库处理
			//先新增内部采购单
			ContractBudget contractBudget = null;
			ContractBudget result = null;
			for(ProjectInfo info : projectInfos){
				//填充合同预算主键前需先新增内部采购单
				contractBudget = new ContractBudget();
				contractBudget.setContractId(info.getContractId());
				contractBudget.setType(ContractBudget.TYPE_PURCHASE);
				contractBudget.setUserId(info.getPmId());
				contractBudget.setUserName(info.getPm());
				contractBudget.setDeptId(info.getDeptId());
				contractBudget.setDept(info.getDept());
				contractBudget.setPurchaseType(ContractBudget.PURCHASETYPE_SERVICE);
				contractBudget.setBudgetTotal(info.getBudgetTotal());
				contractBudget.setStatus(ContractBudget.STATUS_VALIDABLE);
				contractBudget.setCreator(updator);
				contractBudget.setCreateTime(info.getCreateTime());
				contractBudget.setUpdator(updator);
				contractBudget.setUpdateTime(contractBudget.getCreateTime());
				contractBudget.setName(info.getSerialNum() + "_内部采购单");
				result = contractBudgetService.save(contractBudget);
				if(result == null || result.getId() == null){
					return ResponseEntity.ok().body(cpmResponse
							.setSuccess(Boolean.FALSE)
							.setMsgKey("cpmApp.projectInfo.upload.dataBaseError"));
				}
				info.setBudgetId(result.getId());
				projectInfoService.save(info);
			}
			return ResponseEntity.ok().body(cpmResponse
					.setSuccess(Boolean.TRUE)
					.setMsgKey("cpmApp.projectInfo.upload.handleSucc"));
			
		} catch (Exception e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.projectInfo.upload.handleError"));
		}
    }
    
    @GetMapping("/project-infos/exportXls")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_PROJECT_INFO)
    public void exportXls(
    		HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(value = "contractId",required=false) String contractId, 
    		@RequestParam(value = "serialNum",required=false) String serialNum, 
    		@RequestParam(value = "name",required=false) String name, 
    		@RequestParam(value = "status",required=false) String status 
    		)throws URISyntaxException, IOException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to export ProjectInfos by contractId : {}, serialNum : {}, "
        		+ "name : {}, status : {}", contractId, serialNum, name, status);
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
        
        Page<ProjectInfoVo> page = projectInfoService.getUserPage(projectInfo, null);
      //拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"项目编号",
    			"项目名称",
    			"合同编号",
    			"负责人",
    			"开始日期",
    			"结束日期",
    			"合同完成率(%)",
    			"状态",
    			"更新时间"
    	};
    	String fileName = "项目信息.xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("项目信息", 1, heads);
    	//写入数据
    	if(page != null){
    		handleSheetData(page.getContent(),2,excelWrite,projectInfo);
    	}
    	excelWrite.close(outputStream);
    }
    
    /**
     * 处理sheet数据
     * @param salesBonus 
     */
	private void handleSheetData(List<ProjectInfoVo> page, int startRow, ExcelWrite excelWrite, ProjectInfo projectInfo) {
		//除表头外的其他数据单元格格式
    	Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_NUMERIC,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_NUMERIC
    	};
    	XSSFSheet sheet = excelWrite.getCurrentSheet();
    	XSSFWorkbook wb = excelWrite.getXSSFWorkbook();
		XSSFRow row = null;
		XSSFCell cell = null;
		int i = -1;
		int j = 0;
		//百分比格式
		XSSFCellStyle cellStyle = wb.createCellStyle();  
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%")); 
		//日期格式
		XSSFDataFormat format = wb.createDataFormat();
		XSSFCellStyle cellStyleDate = wb.createCellStyle();  
		cellStyleDate.setDataFormat(format.getFormat("yyyy/MM/dd")); 
		
		XSSFCellStyle cellStyleDateDateil = wb.createCellStyle();  
		cellStyleDateDateil.setDataFormat(format.getFormat("yyyy/MM/dd HH:mm:ss"));
		//数据
		for(ProjectInfoVo vo : page){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getSerialNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getSerialNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getPm() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getPm());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getStartDay() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(Date.from(vo.getStartDay().toInstant()));
				cell.setCellStyle(cellStyleDate);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getEndDay() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(Date.from(vo.getEndDay().toInstant()));
				cell.setCellStyle(cellStyleDate);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getFinishRate() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getFinishRate() / 100);
				cell.setCellStyle(cellStyle);
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getStatus() == null){
				cell.setCellValue("");
			}else{
				if(vo.getStatus() == ProjectInfo.STATUS_ADD){
					cell.setCellValue("开发中");
				}else if(vo.getStatus() == ProjectInfo.STATUS_CLOSED){
					cell.setCellValue("已结项");
				}else if(vo.getStatus() == ProjectInfo.STATUS_DELETED){
					cell.setCellValue("已终止");
				}
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUpdateTime() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(Date.from(vo.getUpdateTime().toInstant()));
				cell.setCellStyle(cellStyleDateDateil);
			}
			j++;
		}
	}
}
