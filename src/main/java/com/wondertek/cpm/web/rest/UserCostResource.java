package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.ExcelUtil;
import com.wondertek.cpm.ExcelValue;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.FilePathHelper;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.vo.UserBaseVo;
import com.wondertek.cpm.domain.vo.UserCostVo;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ExternalQuotationService;
import com.wondertek.cpm.service.UserCostService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing UserCost.
 */
@RestController
@RequestMapping("/api")
public class UserCostResource {

    private final Logger log = LoggerFactory.getLogger(UserCostResource.class);
        
    @Inject
    private UserCostService userCostService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserCostRepository userCostRepository;
    @Inject
    private UserService userService;
    @Inject
    private ExternalQuotationService externalQuotationService;

    @PutMapping("/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCost> updateUserCost(@RequestBody UserCost userCost) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to updateUserCost : {}", userCost);
        Boolean isNew = null;
        if(userCost == null || userCost.getUserId() == null || userCost.getCostMonth() == null || 
        		userCost.getUserName() == null || userCost.getSal() == null /*|| userCost.getSocialSecurityFund() == null*/
        		|| userCost.getSocialSecurity() == null || userCost.getFund() == null
        		|| userCost.getOtherExpense() == null){
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.requriedError", "")).body(null);
    	}
        userCost.setSocialSecurityFund(userCost.getSocialSecurity() + userCost.getFund());
        //获取当前用户
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        isNew = userCost.getId() == null;
        UserCost findUserCost = null;
        if(isNew){//新增
        	userCost.setId(null);
        	
        	findUserCost = userCostService.findByUserIdAndCostMonth(userCost.getUserId(),userCost.getCostMonth());
        	if(findUserCost != null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.existError", "")).body(null);
        	}
        	findUserCost = new UserCost();
        	findUserCost.setUserId(userCost.getUserId());
        	findUserCost.setUserName(userCost.getUserName());
        	findUserCost.setCostMonth(userCost.getCostMonth());
        	findUserCost.setCreateTime(updateTime);
        	findUserCost.setCreator(updator);
        	findUserCost.setStatus(CpmConstants.STATUS_VALID);	//设置默认状态 1
        }else{//编辑
        	findUserCost = userCostService.findByUserIdAndCostMonth(userCost.getUserId(),userCost.getCostMonth());
        	if(findUserCost == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.noExistError", "")).body(null);
        	}else if(userCost.getUserName() == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.requriedError", "")).body(null);
        	}else if(findUserCost.getStatus() == CpmConstants.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.statusError", "")).body(null);
        	}else if(!userCost.getUserName().equals(findUserCost.getUserName())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.userNameError", "")).body(null);
        	}
        }
        findUserCost.setSal(userCost.getSal());
        findUserCost.setSocialSecurity(userCost.getSocialSecurity());
        findUserCost.setFund(userCost.getFund());
        findUserCost.setSocialSecurityFund(userCost.getSocialSecurityFund());
        findUserCost.setOtherExpense(userCost.getOtherExpense());
    	findUserCost.setInternalCost(userCost.getSal()+userCost.getSocialSecurityFund()+userCost.getOtherExpense());
    	findUserCost.setExternalCost(findUserCost.getInternalCost());	//外部成本
    	findUserCost.setUpdateTime(updateTime);
    	findUserCost.setUpdator(updator);
    	
        UserCost result = userCostService.save(findUserCost);
        //更新用户的等级
        UserCost maxUserCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(DateUtil.formatDate(CpmConstants.DEFAULT_USER_COST_COSTMONTH_FROMAT, new Date())), findUserCost.getUserId());
        if(maxUserCost != null){
        	List<ExternalQuotation> externalQuotations = externalQuotationService.getAllInfoOrderByGradeAsc();
        	
        	userService.updateUser(maxUserCost.getUserId(), getUserGrade(externalQuotations,maxUserCost.getSal()));
        }
        if(isNew){
        	return ResponseEntity.created(new URI("/api/user-costs/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("userCost", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert("userCost", userCost.getId().toString()))
                    .body(result);
        }
    }
    /**
     * 获取用户的级别
     * @return
     */
    private Integer getUserGrade(List<ExternalQuotation> externalQuotations, Double sal) {
    	int grade = 1;
    	if(externalQuotations != null && sal != null){
    		for(ExternalQuotation externalQuotation : externalQuotations){
    			grade = externalQuotation.getGrade();
    			if(externalQuotation.getExternalQuotation().doubleValue() >= sal){
    				break;
    			}
    		}
    	}
		return grade;
	}

	/**
     * GET  /user-costs : get all the userCosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userCosts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<List<UserCostVo>> getAllUserCosts(
    		@RequestParam(value = "serialNum",required=false) String serialNum,
    		@RequestParam(value = "userName",required=false) String userName,
    		@RequestParam(value = "costMonth",required=false) String costMonth,
    		@RequestParam(value = "status",required=false) String status,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of getAllUserCosts : "
        		+ "serialNum:{},userName:{},costMonth:{},status:{}",serialNum,userName,costMonth,status);
        UserCostVo userCostVo = new UserCostVo();
        if(!StringUtil.isNullStr(serialNum)){
        	userCostVo.setSerialNum(serialNum);
        }
        if(!StringUtil.isNullStr(userName)){
        	userCostVo.setUserName(userName);
        }
        if(!StringUtil.isNullStr(costMonth)){
        	userCostVo.setCostMonth(StringUtil.nullToLong(costMonth));
        }
        if(!StringUtil.isNullStr(status)){
        	userCostVo.setStatus(StringUtil.nullToInteger(status));
        }
        Page<UserCostVo> page = userCostService.getUserCostPage(userCostVo,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-costs/:id : get the "id" userCost.
     *
     * @param id the id of the userCost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userCost, or with status 404 (Not Found)
     */
    @GetMapping("/user-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCost> getUserCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to getUserCost : {}", id);
        UserCost userCost = userCostService.findOne(id);
        return Optional.ofNullable(userCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-costs/:id : delete the "id" userCost.
     *
     * @param id the id of the userCost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<Void> deleteUserCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to deleteUserCost : {}", id);
        UserCost userCost = userCostService.findOne(id);
        if(userCost.getStatus() == CpmConstants.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.delete.statusError", "")).body(null);
        }
        userCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userCost", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-costs?query=:query : search for the userCost corresponding
     * to the query.
     *
     * @param query the query of the userCost search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<List<UserCost>> searchUserCosts(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to search for a page of searchUserCosts for query : {}", query);
        Page<UserCost> page = userCostService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/user-costs/getSerialNumByuserId")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCostVo> getSerialNumByuserId(@RequestParam(value="id",required=false) Long id) throws URISyntaxException{
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of getSerialNumByuserId : {}",id);
    	if(id == null){
    		return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    	}
    	User user = userRepository.findOne(id);
    	if(user == null){
    		return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    	}
    	UserCostVo userCostVo = new UserCostVo();
    	userCostVo.setSerialNum(user.getSerialNum());
    	return new ResponseEntity<>(userCostVo,HttpStatus.OK);
    }
    
    @GetMapping("/user-costs/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value = "filePath",required=true) String filePath)
            throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for filePath : {}",filePath);
        List<UserCost> userCosts = null;
        CpmResponse cpmResponse = new CpmResponse();
        try {
        	//校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.userCost.upload.requiredError"));
			}
			//从第一行读取，最多读取10个sheet，最多读取6列
        	int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,7);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.userCost.upload.requiredError"));
			}
			//初始化员工信息
			Map<String,UserBaseVo> userMap = userService.getAllUser();
			//初始化
			List<ExternalQuotation> externalQuotations = externalQuotationService.getAllInfoOrderByGradeAsc();
			Map<Integer, ExternalQuotation> externalQuotationMap = new HashMap<Integer, ExternalQuotation>();
			if(externalQuotations != null){
				for(ExternalQuotation externalQuotation : externalQuotations){
					externalQuotationMap.put(externalQuotation.getGrade(), externalQuotation);
				}
			}
			//其他信息
			userCosts = new ArrayList<UserCost>();
			String updator = SecurityUtils.getCurrentUserLogin();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			ExternalQuotation externalQuotation = null;
			Map<String,Integer> existMap = new HashMap<String,Integer>();
			Map<Long,Long> userIdMap = new HashMap<Long,Long>();
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
						UserCost userCost = new UserCost();
						userCost.setStatus(CpmConstants.STATUS_VALID);
						userCost.setCreator(updator);
						userCost.setUpdator(updator);
						//校验第一列 员工工号
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.userCost.upload.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						UserBaseVo vo = userMap.get(val.toString());
						if(vo == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.serialNumError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						userCost.setUserId(vo.getId());
						
						//校验第二列 员工姓名，可以不需要
						columnNum++;
						userCost.setUserName(vo.getLastName());
						
						//校验第三列 所属年月
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){//date
							userCost.setCostMonth(StringUtil.nullToLong(DateUtil.formatDate(CpmConstants.DEFAULT_USER_COST_COSTMONTH_FROMAT, (Date)val)));
						}else if(val instanceof Double){//double
							userCost.setCostMonth(((Double)val).longValue());
						}else{//String
							userCost.setCostMonth(StringUtil.nullToLong(val));
						}
						if(userCost.getCostMonth() == 0){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						//校验记录是否存在
						String key = userCost.getUserId() + "_" + userCost.getCostMonth();
						if(existMap.containsKey(key)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.recordExistError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						existMap.put(key, 1);
						
						//校验第四列 员工工资
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							userCost.setSal((Double)val);
						}else{//String
							userCost.setSal(StringUtil.nullToCloneDouble(val));
							if(userCost.getSal() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.userCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						//校验第五列 员工社保
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							userCost.setSocialSecurity((Double)val);
						}else{//String
							userCost.setSocialSecurity(StringUtil.nullToCloneDouble(val));
							if(userCost.getSocialSecurity() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.userCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//校验第六列 员工公积金
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							userCost.setFund((Double)val);
						}else{//String
							userCost.setFund(StringUtil.nullToCloneDouble(val));
							if(userCost.getFund() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.userCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//校验第七轮 其他费用
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							userCost.setOtherExpense((Double)val);
						}else{//String
							userCost.setOtherExpense(StringUtil.nullToCloneDouble(val));
							if(userCost.getOtherExpense() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.userCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						//社保公积金合计
						userCost.setSocialSecurityFund(userCost.getSocialSecurity() + userCost.getFund());
						//内部成本
						userCost.setInternalCost(userCost.getSal()+userCost.getSocialSecurityFund()+userCost.getOtherExpense());//内部成本
						//外部成本
						externalQuotation = externalQuotationMap.get(vo.getGrade());
						userCost.setExternalCost(externalQuotation == null ? userCost.getInternalCost() : externalQuotation.getCostBasis());
						
						userIdMap.put(userCost.getUserId(), userCost.getUserId());
						userCosts.add(userCost);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.userCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//校验完成后，入库处理
			userCostService.saveOrUpdateUploadRecord(userCosts);
			//更新用户的等级
			if(!userIdMap.isEmpty()){
				Long nowDate = StringUtil.nullToLong(DateUtil.formatDate(CpmConstants.DEFAULT_USER_COST_COSTMONTH_FROMAT, new Date()));
				for(Long userId : userIdMap.keySet()){
					UserCost maxUserCost = userCostRepository.findMaxByCostMonthAndUserId(nowDate, userId);
					if(maxUserCost != null){
						userService.updateUser(maxUserCost.getUserId(), getUserGrade(externalQuotations,maxUserCost.getSal()));
					}
				}
			}
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.TRUE)
						.setMsgKey("cpmApp.userCost.upload.handleSucc"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.userCost.upload.handleError"));
		}
    }
}
