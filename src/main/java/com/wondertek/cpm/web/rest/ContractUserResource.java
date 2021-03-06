package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ContractUserVo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.ContractUserService;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.service.UserTimesheetService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractUser.
 */
@RestController
@RequestMapping("/api")
public class ContractUserResource {

    private final Logger log = LoggerFactory.getLogger(ContractUserResource.class);
        
    @Inject
    private ContractUserService contractUserService;
    
    @Inject
    private ContractInfoRepository contractInfoRepository;
    
    @Inject
    private ContractInfoService contractInfoService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private UserTimesheetService userTimesheetService;
    
    @Inject
    private DeptInfoService deptInfoService;
    
    @PutMapping("/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<Boolean> updateContractUser(@RequestBody ContractUser contractUser) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractUser : {}", contractUser);
        Boolean isNew = contractUser.getId() == null;
        if (contractUser.getContractId() == null || contractUser.getUserId() == null 
        		 || StringUtil.isNullStr(contractUser.getUserName()) || contractUser.getJoinDay() == null
        		 || contractUser.getUserId() == null || StringUtil.isNullStr(contractUser.getUserName())
        		) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.paramNone", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractUser.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus().intValue() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.contractInfoError", "")).body(null);
		}
        if(contractUser.getLeaveDay() != null && contractUser.getLeaveDay().longValue() < contractUser.getJoinDay()){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.dayError", "")).body(null);
        }
        //查看用户是否被添加
        boolean isExist = contractUserService.checkUserExist(contractUser);
        if (isNew) {
			 if(isExist){
		     	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.userIdError", "")).body(null);
		     }
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (isNew) {
			contractUser.setCreateTime(updateTime);
			contractUser.setCreator(updator);
		}else {
			ContractUserVo contractUserVo = contractUserService.getContractUser(contractUser.getId());
			if (contractUserVo == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.noPerm", "")).body(null);
			}
			ContractUser old = contractUserService.findOne(contractUser.getId());
			if (old == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.idNone", "")).body(null);
			}else if (old.getContractId() != contractUser.getContractId().longValue()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.contractIdError", "")).body(null);
			} else if (old.getUserId().longValue() != contractUser.getUserId()){
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createError("cpmApp.contractUser.save.userIdChangeError", "")).body(null);
			}
			long nowDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
			//校验加盟日修改
			if ((contractUser.getJoinDay() <= nowDay || old.getJoinDay() <= nowDay)
				&& contractUser.getJoinDay().longValue() != old.getJoinDay()){//有一个加盟日小于今天的都需要校验日报
				Long workDay = null;
				if(contractUser.getJoinDay().longValue() > old.getJoinDay()){//修改的加盟日大于原有的加盟日
					workDay = userTimesheetService.getWorkDayByParam(contractUser.getUserId(), contractUser.getContractId(), UserTimesheet.TYPE_CONTRACT, old.getJoinDay(), contractUser.getJoinDay(), 1);
				}
				if(workDay != null){
					return ResponseEntity.badRequest()
							.headers(HeaderUtil.createError("cpmApp.contractUser.save.joinDayNotModify", workDay.toString())).body(null);
				}
			}
			//校验离开日修改
			if ((contractUser.getLeaveDay() != null && contractUser.getLeaveDay() <= nowDay)
				&& (old.getLeaveDay() == null || contractUser.getLeaveDay().longValue() != old.getLeaveDay())){//离开日修改为当前日期之前才需要校验
				Long workDay = null;
				if(old.getLeaveDay() == null){//原来为空
					workDay = userTimesheetService.getWorkDayByParam(contractUser.getUserId(), contractUser.getContractId(), UserTimesheet.TYPE_CONTRACT, contractUser.getLeaveDay(), nowDay, 2);
				}else if(old.getLeaveDay().longValue() > contractUser.getLeaveDay()){//原来的日期大于现在的日期，需要校验
					workDay = userTimesheetService.getWorkDayByParam(contractUser.getUserId(), contractUser.getContractId(), UserTimesheet.TYPE_CONTRACT, contractUser.getLeaveDay(), old.getLeaveDay(), 2);
				}
				if(workDay != null){
					return ResponseEntity.badRequest()
							.headers(HeaderUtil.createError("cpmApp.contractUser.save.leaveDayNotModify", workDay.toString())).body(null);
				}
			}
			
			contractUser.setCreateTime(old.getCreateTime());
			contractUser.setCreator(old.getCreator());
		}
        contractUser.setUpdateTime(updateTime);
        contractUser.setUpdator(updator);
        
        ContractUser result = contractUserService.save(contractUser);
        
        if (isNew) {
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("contractUser", result.getId().toString()))
                    .body(isNew);
		}else {
			return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractUser", result.getId().toString()))
        			.body(isNew);
		}
        
    }
    @GetMapping("/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<List<ContractUserVo>> getAllContractUsers(
    		@RequestParam(value = "contractId",required=false) Long contractId, 
    		@RequestParam(value = "userId" ,required=false) Long userId, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractUsers by contractId : {}, userId : {}", contractId, userId);
        ContractUser contractUser = new ContractUser();
        contractUser.setContractId(contractId);
        contractUser.setUserId(userId);
        
        Page<ContractUserVo> page = contractUserService.getUserPage(contractUser,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/contract-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<ContractUserVo> getContractUser(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractUser : {}", id);
        ContractUserVo contractUserVo = contractUserService.getContractUser(id);
        return Optional.ofNullable(contractUserVo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<Void> deleteContractUser(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ContractUser : {}", id);
        ContractUserVo contractVo = contractUserService.getContractUser(id);
        if (contractVo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.noPerm", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractVo.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.contractInfoError", "")).body(null);
		}
//        long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
//        if(contractVo.getLeaveDay() != null && contractVo.getLeaveDay() <= leaveDay){
//        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.leaveDayError", "")).body(null);
//        }
        contractUserService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractUser", id.toString())).build();
    }

    @GetMapping("/_search/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<List<ContractUser>> searchContractUsers(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search for a page of ContractUsers for query {}", query);
        Page<ContractUser> page = contractUserService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/contract-users/exportXls")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public void exportXls(
	    		HttpServletRequest request, HttpServletResponse response,
	    		@RequestParam(value="contractId",required = false) Long contractId,
	    		@RequestParam(value="userId",required = false) Long userId
    		) throws IOException{
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to exportXls : contractId:{},userId:{}",contractId,userId);
    	ContractUser searchParams = new ContractUser();
        searchParams.setContractId(contractId);
        searchParams.setUserId(userId);
        List<ContractUserVo> list = contractUserService.getContractUserList(searchParams);
    	//拼接sheet数据
    	//标题
    	String[] heads = new String[]{
    			"合同编号",
    			"合同名称",
    			"员工",
    			"部门",
    			"加盟日",
    			"离开日"
    	};
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String now = sdf.format(date);
    	String fileName = "合同人员信息_"+now+".xlsx";
    	//写入sheet
    	ServletOutputStream outputStream = response.getOutputStream();
    	response.setHeader("Content-Disposition","attachment;filename=" + ExcelUtil.getExportName(request, fileName));
    	response.setContentType("application/x-msdownload");
    	response.setCharacterEncoding("UTF-8");
    	ExcelWrite excelWrite = new ExcelWrite();
    	//写入标题
    	excelWrite.createSheetTitle("咨询", 1, heads);
    	//写入数据
    	if(list != null){
    		handleSheetData(list,2,excelWrite);
    	}
    	excelWrite.close(outputStream);
    }

	private void handleSheetData(List<ContractUserVo> list, int startRow, ExcelWrite excelWrite) {
		Integer[] cellType = new Integer[]{
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING,
    			Cell.CELL_TYPE_STRING
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
		//数据
		for(ContractUserVo vo : list){
			i++;
			row = sheet.createRow(i + startRow-1);
			
			j = 0;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractNum() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractNum());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getContractName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getContractName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getUserName() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getUserName());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getDept() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getDept());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getJoinDay() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getJoinDay());
			}
			j++;
			cell = row.createCell(j,cellType[j]);
			if(vo.getLeaveDay() == null){
				cell.setCellValue("");
			}else{
				cell.setCellValue(vo.getLeaveDay());
			}
			j++;
		}
		
	}
	
	@GetMapping("/contract-users/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value = "filePath",required=true) String filePath)
            throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for file : {}",filePath);
        List<ContractUser> users = null;
        CpmResponse cpmResponse = new CpmResponse();
		try {
			//校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractUser.save.requiredError"));
			}
			//从第一行读取，最多读取10个sheet，最多读取6列
			int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,6);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractUser.save.requiredError"));
			}
			//初始信息
			//合同信息
			Map<String,Long> contractInfos = contractInfoService.getContractInfo();
			//员工信息
			Map<String, User> allUser = userService.getAllUsers();
			
			//其他信息
			users = new ArrayList<ContractUser>();
			
			String updator = SecurityUtils.getCurrentUserLogin();
			ZonedDateTime updateTime = ZonedDateTime.now();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			Long contractId = null;
			User user = null;
			DeptInfo deptInfo = null;
			Map<String,Map<Long,Long>> userProjects = new HashMap<String,Map<Long,Long>>();
			for (ExcelValue excelValue : lists) {
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty()) {//每个sheet也可能没有数据，空sheet
					continue;
				}
				rowNum = 1;//都是从第一行读取的。
				for(List<Object> ls : excelValue.getVals()){
					rowNum ++;
					if(ls == null){//每个sheet里面也可能有空行。
						continue;
					}
					try {
						ContractUser contractUser = new ContractUser();
						contractUser.setUpdator(updator);
						contractUser.setUpdateTime(updateTime);
						contractUser.setCreateTime(updateTime);
						contractUser.setCreator(updator);
						
				        //校验第一列，合同编号， 查看导入的合同编号是否在数据库中存在。
						columnNum = 0;
						val = ls.get(columnNum);
						String contractSerialNum = null;
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							contractSerialNum = ((Double)val).longValue() +"";
						}else{//String
							contractSerialNum = StringUtil.null2Str(val);
						}
						//根据合同编号得到合同id
						contractId = contractInfos.get(contractSerialNum);
						//校验合同编号是否存在。
						if(contractId==null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractUser.save.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						contractUser.setContractId(contractId);
						
						//第二列，合同姓名，可以不填写，不用校验
						columnNum++;
						// 校验第三列，员工编号，查看导入的员工编号是否存在。
						columnNum++;
						val = ls.get(columnNum);
						String userSerialNum = StringUtil.null2Str(val);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}else if(val instanceof Double){//double
							userSerialNum = ((Double)val).longValue() +"";
						}else{//String
							userSerialNum = StringUtil.null2Str(val);
						}
						user = allUser.get(userSerialNum);
						if (user == null) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractUser.save.dataNotExist")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						
						contractUser.setUserId(user.getId());
						contractUser.setUserName(user.getLastName());
						//根据员工编号得到对应的员工所在的部门信息
						deptInfo = deptInfoService.findDeptInfo(user.getSerialNum());
						contractUser.setDeptId(deptInfo.getId());
						contractUser.setDept(deptInfo.getName());
						//第四列，员工姓名,可以不填写，不用校验。
						columnNum++;
						
						// 校验第五列，加盟日 
						columnNum++;
						long joinDay = 0;
						val = ls.get(columnNum);
						if (val == null || StringUtil.isNullStr(val)) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractUser.save.dataIsError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}else if(val instanceof Date){//date
							contractUser.setJoinDay(StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, (Date)val)));
						}else{
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.joinDayStyleError")	
											.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						joinDay = contractUser.getJoinDay().longValue();
						// 校验第六列，离开日。
						columnNum++;
						long leaveDay = 0;
						val = ls.get(columnNum);
						if(!StringUtil.isNullStr(val)){
							if(val instanceof Date){//date
								contractUser.setLeaveDay(StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, (Date)val)));
							}else{
								return ResponseEntity.ok()
										.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.leaveDayStyleError")	
												.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
							}
							if (contractUser.getLeaveDay().longValue() < contractUser.getJoinDay()) {
								return ResponseEntity.ok()
										.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.improtTimeExitsError")
												.setMsgParam(excelValue.getSheet() + "," + rowNum));
							}
							leaveDay = contractUser.getLeaveDay().longValue();
						}
						String key = contractId + "_" + user.getId();
						if(!userProjects.containsKey(key)){
							Map<Long, Long> date = contractUserService.getdates(contractId, user.getId());
							userProjects.put(key, date);
						}
						Set<Entry<Long, Long>> entryLong = userProjects.get(key).entrySet();
						for (Entry<Long, Long> entry : entryLong) {
							if (entry.getValue() == null) {
								if (leaveDay >= entry.getKey() || leaveDay == 0) {
									return ResponseEntity.ok()
											.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.timeQuantumError")
													.setMsgParam(excelValue.getSheet() + "," + rowNum));
								}
							} else {
								if (leaveDay == 0) {
									if (joinDay <= entry.getValue()) {
										return ResponseEntity.ok()
												.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.timeQuantumError")
														.setMsgParam(excelValue.getSheet() + "," + rowNum));
									}
								} else {
									if(!(leaveDay < entry.getKey() || entry.getValue() < joinDay)){
										return ResponseEntity.ok()
												.body(cpmResponse.setSuccess(Boolean.FALSE).setMsgKey("cpmApp.contractUser.save.timeQuantumError")
														.setMsgParam(excelValue.getSheet() + "," + rowNum));
									}
								}
							}

						}
						userProjects.get(key).put(joinDay, leaveDay == 0 ? null : leaveDay);
						
						users.add(contractUser);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractUser.save.dataErrors")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//入库
			if(users != null ){
				for(ContractUser contractUser : users){
					contractUserService.saveAll(contractUser);
				}
			}
			return ResponseEntity.ok().body(cpmResponse
					.setSuccess(Boolean.TRUE)
					.setMsgKey("cpmApp.contractUser.save.importSuccess"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.contractUser.save.importError"));
		}
		
    }
}
