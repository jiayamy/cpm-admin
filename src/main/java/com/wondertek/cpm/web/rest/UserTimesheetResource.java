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
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.domain.vo.UserTimesheetVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.service.UserTimesheetService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing UserTimesheet.
 */
@RestController
@RequestMapping("/api")
public class UserTimesheetResource {

    private final Logger log = LoggerFactory.getLogger(UserTimesheetResource.class);
        
    @Inject
    private UserTimesheetService userTimesheetService;
    @Inject
    private ProjectInfoService projectInfoService;
    @Inject
    private UserService userService;
    /**
     * GET  /user-timesheets : get all the userTimesheets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userTimesheets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<List<UserTimesheetVo>> getAllUserTimesheets(
    		@RequestParam(value = "workDay",required=false) Long workDay,
    		@RequestParam(value = "type",required=false) Integer type,
    		@RequestParam(value = "objName",required=false) String objName,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of UserTimesheets by workDay : {}, type : {}, "
        		+ "objName : {}", workDay, type, objName);
//        Page<UserTimesheet> page = userTimesheetService.findAll(pageable);
        UserTimesheet userTimesheet = new UserTimesheet();
        userTimesheet.setWorkDay(workDay);
        userTimesheet.setType(type);
        userTimesheet.setObjName(objName);
        Page<UserTimesheet> page = userTimesheetService.getUserPage(userTimesheet, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-timesheets");
        List<UserTimesheetVo> returnList = new ArrayList<UserTimesheetVo>();
        if(page.getContent() != null){
        	for(UserTimesheet tmp : page.getContent()){
        		returnList.add(new UserTimesheetVo(tmp,null));
        	}
        }
        return new ResponseEntity<>(returnList, headers, HttpStatus.OK);
    }

    /**
     * GET  /user-timesheets/:id : get the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userTimesheet, or with status 404 (Not Found)
     */
    @GetMapping("/user-timesheets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<UserTimesheetVo> getUserTimesheet(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetService.getUserTimesheetForUser(id);
        UserTimesheetVo vo = null;
        if(userTimesheet != null){
        	vo = new UserTimesheetVo(userTimesheet,null);
        }
        return Optional.ofNullable(vo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-timesheets/:id : delete the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-timesheets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<Void> deleteUserTimesheet(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetService.getUserTimesheetForUser(id);
        if(userTimesheet == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userTimesheet.save.noPermit", "")).body(null);
        }else if(userTimesheet.getStatus() == CpmConstants.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userTimesheet.delete.statusError", "")).body(null);
        }
        userTimesheetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userTimesheet", id.toString())).build();
    }

    @GetMapping("/_edit/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<List<UserTimesheetForUser>> queryEditByUser(@RequestParam(value = "workDay") String workDay, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryByUser for a page of UserTimesheets for query {}", workDay);
        Date workDayDate = null;
        if(!StringUtil.isNullStr(workDay)){
        	workDayDate = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, workDay);
        }
        if(workDayDate == null){
        	workDayDate = new Date();
        }
        List<UserTimesheetForUser> list = userTimesheetService.queryEditByUser(workDayDate);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @PutMapping("/_edit/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<Map<String, Object>> updateEditByUser(@RequestBody List<UserTimesheetForUser> userTimesheetForUsers) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update UserTimesheet : {}", userTimesheetForUsers);
        Map<String,Object> resultMap = new HashMap<String,Object>(); 
        if (userTimesheetForUsers == null || userTimesheetForUsers.isEmpty() || userTimesheetForUsers.size() < 3) {
        	resultMap.put("message", "cpmApp.userTimesheet.save.paramError");
        	resultMap.put("success", false);
        	return new ResponseEntity<>(resultMap, null, HttpStatus.OK);
        }
        //日期，工作地点，日报
        String[] messages = userTimesheetService.updateEditByUser(userTimesheetForUsers).split("#");
        resultMap.put("message", messages[0]);
        resultMap.put("success", messages[0].equals("cpmApp.userTimesheet.save.success"));
        if(messages.length > 1){
        	resultMap.put("param", messages[1]);
        }
    	return new ResponseEntity<>(resultMap, null, HttpStatus.OK);
    }
    
    @GetMapping("/user-timesheets/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<CpmResponse> uploadProjectExcel(@RequestParam(value = "filePath",required=true) String filePath)
            throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for file : {}",filePath);
        CpmResponse cpmResponse = new CpmResponse();
		try {
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("userTimesheet.import.requiredError"));
			}
			List<ExcelValue> lists = ExcelUtil.readExcel(file);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("userTimesheet.import.requiredError"));
			}
			//初始化数据
			//所有项目信息
			Map<String,ProjectInfo> allProjectInfos = projectInfoService.getUsableProject();
			//所有用户信息
			Map<String,User> allUsers = userService.getAllUsers();
			
			//第一行是工作日、日期、姓名
			//第二行是空、空、工号
			//第三行开始是数据
			Map<Integer,User> users = new HashMap<Integer,User>();
			Map<Integer,String> userNames = new HashMap<Integer,String>();
			ZonedDateTime createTime = ZonedDateTime.now();
			long totalCount = 0;
			long totalSuccCount = 0;
			long totalErrorCount = 0;
			for (ExcelValue excelValue : lists) {
				users.clear();
				userNames.clear();
				if (excelValue.getVals() == null || excelValue.getVals().isEmpty() || StringUtil.isNullStr(excelValue.getSheetName())) {//每个sheet也可能没有数据，空sheet
					continue;
				}
				//获取项目信息
				ProjectInfo projectInfo = allProjectInfos.get(excelValue.getSheetName().trim());
				if(projectInfo == null){
					return ResponseEntity.ok().body(cpmResponse
							.setSuccess(Boolean.FALSE)
							.setMsgKey("userManagement.import.error")
							.setMsgParam(file.getName() + " 第" + excelValue.getSheet() + "个sheet["+excelValue.getSheetName()+"]所属项目不存在"));
				}
				String objName = projectInfo.getSerialNum() + ":" + projectInfo.getName();
				//初始化行头里面的用户信息，从第三列开始
				int rowNum = 0;
				Object val = null;
				Long workDay = null;
				Double realInput = null;
				User curUser = null;
				List<UserTimesheet> saveList = new ArrayList<UserTimesheet>();
				for(List<Object> ls : excelValue.getVals()){
					rowNum ++;
					if(ls == null){
						continue;
					}
					if(rowNum == 1){
						//中文名
						log.debug(ls.toString());
						for(int i = 2; i < ls.size(); i++){
							val = ls.get(i);
							if(val == null || StringUtil.isNullStr(val)){
								userNames.put(i,"");
							}else if (val instanceof Double) {
								userNames.put(i,((Double)val).longValue() +"");
							}else {
								userNames.put(i,StringUtil.null2Str(val).trim());
							}
						}
					}else if(rowNum == 2){//查看当前sheet里面的用户信息
						User user = null;
						for(int i = 2; i < ls.size(); i++){
							val = ls.get(i);
							if(val == null || StringUtil.isNullStr(val)){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("userManagement.import.error")
										.setMsgParam(file.getName() + " 第" + excelValue.getSheet() + "个sheet["+excelValue.getSheetName()+"]的第" + rowNum + "行第" + (i+1) + "列用户为空"));
							}else if (val instanceof Double) {
								user = allUsers.get(((Double)val).longValue() +"");
							}else {
								user = allUsers.get(StringUtil.null2Str(val).trim());
							}
							if(user != null){
								users.put(i, user);
								//检查名称是否一致
								if(userNames.get(i) == null || !userNames.get(i).equals(user.getLastName())){
									log.debug(file.getName() + " 第" + excelValue.getSheet() + "个sheet["+excelValue.getSheetName()+"]的第" + rowNum + "行第" + (i+1) + "列用户不一致,查出用户名:"+user.getLastName());
								}
							}else{
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("userManagement.import.error")
										.setMsgParam(file.getName() + " 第" + excelValue.getSheet() + "个sheet["+excelValue.getSheetName()+"]的第" + rowNum + "行第" + (i+1) + "列用户不存在"));
							}
						}
					}else{
						for(int i = 1; i < ls.size(); i++){//从日期开始读取
							if(i == 1){//当前日期
								val = ls.get(i);
								if(val == null || StringUtil.isNullStr(val)){
									continue;
								}else if (val instanceof Date) {
									workDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, (Date)val));
								}else {
									return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("userManagement.import.error")
											.setMsgParam(file.getName() + " 第" + excelValue.getSheet() + "个sheet["+excelValue.getSheetName()+"]的第" + rowNum + "行第" + (i+1) + "列日期不存在"));
								}
							}else{
								val = ls.get(i);
								if(val == null || StringUtil.isNullStr(val)){
									continue;
								}else if (val instanceof Double) {
									realInput = (Double) val;
								}else{
									realInput = StringUtil.nullToDouble(val, 1);
								}
								//一条记录
								curUser = users.get(i);//前面校验了不可能为空的
								if(curUser != null && realInput != null && realInput.doubleValue() != 0){
									UserTimesheet userTimesheet = new UserTimesheet();
									userTimesheet.setRealInput(realInput);
									userTimesheet.setExtraInput(0d);

									userTimesheet.setAcceptExtraInput(userTimesheet.getExtraInput());
									userTimesheet.setAcceptInput(userTimesheet.getRealInput());
									userTimesheet.setCharacter(UserTimesheet.CHARACTER_ABLE);
									userTimesheet.setCreateTime(createTime);
									userTimesheet.setCreator(curUser.getSerialNum());
									userTimesheet.setId(null);
									userTimesheet.setObjId(projectInfo.getId());
									userTimesheet.setObjName(objName);
									userTimesheet.setStatus(CpmConstants.STATUS_VALID);
									userTimesheet.setType(UserTimesheet.TYPE_PROJECT);
									userTimesheet.setUpdateTime(userTimesheet.getCreateTime());
									userTimesheet.setUpdator(curUser.getSerialNum());
									userTimesheet.setUserId(curUser.getId());
									userTimesheet.setUserName(curUser.getLastName());
									userTimesheet.setWorkArea(curUser.getWorkArea());
									userTimesheet.setWorkDay(workDay);
									saveList.add(userTimesheet);
								}
							}
						}
					}
				}
				
				//一个sheet保存一次
				Double countInput = 0d;
				long count = 0;
				long succCount = 0;
				long errorCount = 0;
				for(UserTimesheet userTimesheet : saveList){
					countInput += userTimesheet.getRealInput();
					count ++;
					try {
						userTimesheetService.save(userTimesheet);
						succCount ++;
					} catch (Exception e) {
						log.error(e.getMessage() + userTimesheet);
						errorCount ++;
					}
				}
				log.debug(file.getName() + " 第"+excelValue.getSheet()+"个sheet["+excelValue.getSheetName()+"]，总"+users.size()+"个用户,"
						+ ",工时总数:" + countInput +","
						+ ",工时记录总数:" + count + ","
						+ ",导入成功总数:" + succCount + ","
						+ ",导入失败总数:" + errorCount);
				totalCount += count;
				totalSuccCount += succCount;
				totalErrorCount += errorCount;
			}
			log.debug(file.getName() + " 工时记录总数:" + totalCount
					+ ",导入成功总数:" + totalSuccCount + ","
					+ ",导入失败总数:" + totalErrorCount);
			return ResponseEntity.ok().body(cpmResponse
					.setSuccess(Boolean.TRUE)
					.setMsgKey("userTimesheet.import.handleSucc"));
		}catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("userTimesheet.import.handleError"));
		}
    }
}
