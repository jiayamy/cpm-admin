package com.wondertek.cpm.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.UserBaseVo;
import com.wondertek.cpm.repository.OutsourcingUserRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractInfo.
 */
@RestController
@RequestMapping("/api")
public class ContractInfoResource {

	private final Logger log = LoggerFactory.getLogger(ContractInfoResource.class);

	@Inject
	private ContractInfoService contractInfoService;
	
	@Inject
	private OutsourcingUserRepository outsourcingUserRepository;
	
	@Inject
    private UserService userService;
	@Inject
	private DeptInfoService deptInfoService;

	@PutMapping("/contract-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<Boolean> updateContractInfo(@RequestBody ContractInfoVo contractInfoVo1) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractInfo : {}", contractInfoVo1);
        Boolean isNew = contractInfoVo1.getId() == null;
        if(contractInfoVo1.getIsPrepared() == null){
        	contractInfoVo1.setIsPrepared(Boolean.FALSE);
        }
        if(contractInfoVo1.getIsEpibolic() == null){
        	contractInfoVo1.setIsEpibolic(Boolean.FALSE);
        }
        //基本校验
        if(contractInfoVo1.getSalesmanId() == null && contractInfoVo1.getConsultantsId() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.manallnone", "")).body(null);
        }
        if(contractInfoVo1.getSalesmanId() != null){//销售不为空
        	if(StringUtil.isNullStr(contractInfoVo1.getSalesman())
            		|| contractInfoVo1.getDeptId() == null || StringUtil.isNullStr(contractInfoVo1.getDept())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
        	}
        }
        if(contractInfoVo1.getConsultantsId() != null){//咨询不为空
        	if(StringUtil.isNullStr(contractInfoVo1.getConsultants())
            		|| contractInfoVo1.getConsultantsDeptId() == null || StringUtil.isNullStr(contractInfoVo1.getConsultantsDept())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
        	}
        	if(contractInfoVo1.getConsultantsShareRate() == null){//咨询分润比例
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.consultantsRateError", "")).body(null);
        	}else if(contractInfoVo1.getConsultantsShareRate() < 0d || contractInfoVo1.getConsultantsShareRate() > 100d){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.consultantsRateError", "")).body(null);
        	}
        }else{//咨询分润比例，没咨询不需要咨询分润比例
        	contractInfoVo1.setConsultantsShareRate(null);
        }
        if (StringUtil.isNullStr(contractInfoVo1.getSerialNum()) || StringUtil.isNullStr(contractInfoVo1.getName())
        		|| contractInfoVo1.getAmount() == null || contractInfoVo1.getType() == null
        		|| contractInfoVo1.getStartDay() == null || contractInfoVo1.getEndDay() == null
        		|| contractInfoVo1.getTaxRate() == null || contractInfoVo1.getTaxes() == null 
        		|| contractInfoVo1.getShareRate() == null || contractInfoVo1.getShareCost() == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
		}
        //结束时间校验
        if (contractInfoVo1.getEndDay() != null && contractInfoVo1.getEndDay().isBefore(contractInfoVo1.getStartDay())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.endDayError", "")).body(null);
		}
        //查看合同是否是唯一
        boolean isExist = contractInfoService.checkByContract(contractInfoVo1.getSerialNum(),contractInfoVo1.getId());
        if (isExist) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.existSerialNum" ,"")).body(null);
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
        	//查看该用户是否有修改的权限
        	ContractInfoVo contractInfoVo = contractInfoService.getUserContractInfo(contractInfoVo1.getId());
        	if(contractInfoVo == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.noPerm" ,"")).body(null);
        	}else if(contractInfoVo.getStatus() == ContractInfo.STATU_FINISH || contractInfoVo.getStatus() == ContractInfo.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.statusError" ,"")).body(null);
        	}else if(contractInfoVo.getIsPrepared() == false && contractInfoVo1.getIsPrepared() == true){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.isPreparedError" ,"")).body(null);
        	}
        	contractInfoVo1.setCreateTime(contractInfoVo.getCreateTime());
        	contractInfoVo1.setCreator(contractInfoVo.getCreator());
        	contractInfoVo1.setStatus(contractInfoVo.getStatus());
        	contractInfoVo1.setFinishTotal(contractInfoVo.getFinishTotal());
        	contractInfoVo1.setReceiveTotal(contractInfoVo.getReceiveTotal());
        	contractInfoVo1.setFinishRate(contractInfoVo.getFinishRate());
		}else {
			if (contractInfoVo1.getType().intValue() == ContractInfo.TYPE_EXTERNAL) {
				if (StringUtil.isNullStr(contractInfoVo1.getMark())) {
		        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
				}else {
					String str[] = contractInfoVo1.getMark().split("_");
					String num = str[0];
					String createTimeD = str[1];
					if (StringUtil.isNullStr(num) || StringUtil.isNullStr(createTimeD) || StringUtil.nullToCloneLong(num) == null
							|| StringUtil.nullToCloneLong(createTimeD) == null || StringUtil.nullToInteger(num) < 0
							|| StringUtil.nullToInteger(num) > 100) {
			        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
					}
					//校验optionTime 
		           Date optionTime = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, createTimeD.substring(0,8)); 
		           if(optionTime == null || !createTimeD.substring(0,8).equals(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, optionTime))){ 
		        	   return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
		           }
				}
			}
			contractInfoVo1.setCreateTime(updateTime);
			contractInfoVo1.setCreator(updator);
			contractInfoVo1.setStatus(ContractInfo.STATUS_VALIDABLE);
			contractInfoVo1.setFinishTotal(0d);
			contractInfoVo1.setReceiveTotal(0d);
			contractInfoVo1.setFinishRate(0d);
		}
        contractInfoVo1.setUpdateTime(updateTime);
        contractInfoVo1.setUpdator(updator);
        ContractInfo result = new ContractInfo();
        if (isNew && contractInfoVo1.getType().intValue() == ContractInfo.TYPE_EXTERNAL) {
    		List<OutsourcingUser> list = outsourcingUserRepository.findByMark(contractInfoVo1.getMark());
    		if (list != null && !list.isEmpty()){
    			 result = contractInfoService.save(new ContractInfo(contractInfoVo1));
    			 for (OutsourcingUser outsourcingUser : list) {
    				outsourcingUser.setContractId(result.getId());
 					outsourcingUser.setMark(null);
 					outsourcingUserRepository.save(outsourcingUser);
    			 }
    		}else {
	        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.userCannotEmpty", "")).body(null);
			}
        }else {
   			result = contractInfoService.save(new ContractInfo(contractInfoVo1));
    	}
		
        if(isNew){
        	return ResponseEntity.created(new URI("/api/project-infos/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("contractInfo", result.getId().toString()))
                    .body(true);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractInfo", contractInfoVo1.getId().toString()))
        			.body(false);
        }
        
    }

	@GetMapping("/contract-infos")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
	public ResponseEntity<List<ContractInfoVo>> getAllContractInfos(@RequestParam(value = "name",required=false) String name,
			@RequestParam(value = "type",required=false) Integer type, @RequestParam(value = "isPrepared",required=false) Boolean isPrepared,
			@RequestParam(value = "isEpibolic",required=false) Boolean isEpibolic, @RequestParam(value = "serialNum",required=false) String serialNum,
			@RequestParam(value = "salesmanId",required=false) Long salesmanId,@RequestParam(value = "consultantsId",required=false) Long consultantsId,
			@ApiParam Pageable pageable) throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractInfos by name : {}, type : {}, isPrepared : {}, "
				+ "isEpibolic : {}, serialNum : {}, salesmanId : {}, consultantsId : {}", name, type, isPrepared, isEpibolic, serialNum,salesmanId,consultantsId);
		ContractInfo contractInfo = new ContractInfo();

		contractInfo.setName(name);
		contractInfo.setType(type);
		contractInfo.setIsPrepared(isPrepared);
		contractInfo.setIsEpibolic(isEpibolic);
		contractInfo.setSerialNum(serialNum);
		contractInfo.setSalesmanId(salesmanId);
		contractInfo.setConsultantsId(consultantsId);
		
		Page<ContractInfoVo> page = contractInfoService.getContractInfoPage(contractInfo, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-infos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/contract-infos/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
	public ResponseEntity<ContractInfoVo> getContractInfo(@PathVariable Long id) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractInfo : {}", id);
		ContractInfoVo contractInfo = contractInfoService.getUserContractInfo(id);
		return Optional.ofNullable(contractInfo).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /contract-infos/:id : delete the "id" contractInfo.
	 *
	 * @param id
	 *            the id of the contractInfo to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/contract-infos/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
	public ResponseEntity<Void> deleteContractInfo(@PathVariable Long id) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ContractInfo : {}", id);
		
		ContractInfoVo contractInfo = contractInfoService.getUserContractInfo(id);
        if(contractInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.noPerm", "")).body(null);
        }
        if(contractInfo.getStatus() == ContractInfo.STATU_FINISH){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.delete.status2Error", "")).body(null);
        }else if(contractInfo.getStatus() == ContractInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.delete.status3Error", "")).body(null);
        }
	        
		contractInfoService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractInfo", id.toString())).build();
	}

	// 留作参考
	@GetMapping("/_search/contract-infos")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
	public ResponseEntity<List<ContractInfo>> searchContractInfos(@RequestParam String query,
			@ApiParam Pageable pageable) throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search for a page of ContractInfos for query {}", query);
		Page<ContractInfo> page = contractInfoService.search(query, pageable);
		HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page,
				"/api/_search/contract-infos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	/**
	 * 获取销售或咨询能看到的合同列表
	 */
	@GetMapping("/contract-infos/queryUserContract")
	@Secured(AuthoritiesConstants.USER)
	public ResponseEntity<List<LongValue>> queryUserContract() {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContract");
		 List<LongValue> list = contractInfoService.queryUserContract();
		 return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
	

	@PutMapping("/contract-infos/finish")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<ContractInfo> finishContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to finishContractInfo");
    	if(contractInfo.getId() == null || contractInfo.getFinishRate() == null){
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
    	}
    	//有没权限
    	ContractInfoVo contractInfoInfo = contractInfoService.getUserContractInfo(contractInfo.getId());
        if(contractInfoInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.noPerm", "")).body(null);
        }
        if(contractInfo.getFinishRate() < 0){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.finish.minError", "")).body(null);
        }
        if(contractInfo.getFinishRate() > 100){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.finish.maxError", "")).body(null);
        }
        if(contractInfoInfo.getStatus() == ContractInfo.STATU_FINISH || contractInfoInfo.getStatus() == ContractInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.finish.statusError", "")).body(null);
        }
        contractInfoService.finishContractInfo(contractInfo.getId(),contractInfo.getFinishRate());
    	return ResponseEntity.ok()
    			.headers(HeaderUtil.createAlert("cpmApp.contractInfo.finish.success", contractInfoInfo.getId().toString()))
    			.body(null);
    }
	
	@GetMapping("/contract-infos/end")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<ContractInfo> endContractInfo(@RequestParam(value = "id") Long id) throws URISyntaxException {
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to endContractInfo by id : {}", id);
    	if(id == null){
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
    	}
    	//有没权限
    	ContractInfoVo contractInfoInfo = contractInfoService.getUserContractInfo(id);
        if(contractInfoInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.noPerm", "")).body(null);
        }
        if(contractInfoInfo.getStatus() == ContractInfo.STATU_FINISH){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.end.status2Error", "")).body(null);
        }else if(contractInfoInfo.getStatus() == ContractInfo.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.end.status3Error", "")).body(null);
        }
        contractInfoService.endContractInfo(id);
        
    	return ResponseEntity.ok()
    			.headers(HeaderUtil.createAlert("cpmApp.contractInfo.end.success", contractInfoInfo.getId().toString()))
    			.body(null);
    }
	
	@GetMapping("/contract-infos/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value="filePath",required=true) String filePath)
            throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for filePath : {}",filePath);
        List<ContractInfo> contractInfos = null;
        CpmResponse cpmResponse = new CpmResponse();
        try {
        	//校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractInfo.upload.requiredError"));
			}
			//从第一行读取，最多读取10个sheet，最多读取23列
        	int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,23);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractInfo.upload.requiredError"));
			}
			//初始化员工信息
			Map<String,UserBaseVo> userMap = userService.getAllUser();
			//初始化部门信息
			Map<Long,DeptInfo> deptInfoMap = deptInfoService.getAllDeptInfosMap();
			//初始化合同信息
			Map<String,ContractInfo> contractInfoMap = contractInfoService.getContractInfoMapBySerialnum();
			//其他信息
			contractInfos = new ArrayList<ContractInfo>();
			String updator = SecurityUtils.getCurrentUserLogin();
			ZonedDateTime updateTime = ZonedDateTime.now();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			Map<String,Integer> existMap = new HashMap<String,Integer>();
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
						ContractInfo contractInfo = new ContractInfo();
						contractInfo.setStatus(ContractInfo.STATUS_VALIDABLE);
						contractInfo.setCreator(updator);
						contractInfo.setCreateTime(updateTime);
						contractInfo.setUpdator(updator);
						contractInfo.setUpdateTime(updateTime);
						//校验第一列 合同编号
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractInfo.upload.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							DecimalFormat format = new DecimalFormat("0");
							val = format.format((Double)val);
						}
						contractInfo.setSerialNum(val.toString());
						//检验是否新增合同
						Boolean isExistSerialnum = contractInfoMap.containsKey(contractInfo.getSerialNum());
						//上传合同信息时，只添加不更新
						if(isExistSerialnum){	//如果合同已存在，前端提示存在错误
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.existError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum));
						}
						
						//校验第二列 合同名称
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum + "," + (columnNum+1)));
						}
						contractInfo.setName(val.toString());
						
						//检验第三列 合同类型
						columnNum ++;
						val = ls.get(columnNum);
						if (val == null) {
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractInfo.upload.dataError").setMsgParam(
													excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						if (val.equals("产品")) {
							contractInfo.setType(ContractInfo.TYPE_INTERNAL);
						} else if (val.equals("外包")) {
							contractInfo.setType(ContractInfo.TYPE_EXTERNAL);
						} else if (val.equals("硬件")) {
							contractInfo.setType(ContractInfo.TYPE_HARDWARE);
						} else if (val.equals("公共")) {
							contractInfo.setType(ContractInfo.TYPE_PUBLIC);
						} else if (val.equals("项目")) {
							contractInfo.setType(ContractInfo.TYPE_PROJECT);
						} else if (val.equals("推广")) {
							contractInfo.setType(ContractInfo.TYPE_EXTEND);
						} else {
							contractInfo.setType(ContractInfo.TYPE_OTHER);
						}
						//填充是否 预立合同(根据合同编号判断是否预立合同)
						Boolean isMatched = contractInfo.getSerialNum().substring(0, 2).equalsIgnoreCase("WY");
						if(isMatched){
							contractInfo.setIsPrepared(Boolean.TRUE);
						}else{
							contractInfo.setIsPrepared(Boolean.FALSE);
						}
						
						//检验第四列 外部合同
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							//contractInfo.setIsEpibolic(Boolean.FALSE);
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractInfo.upload.dataError").setMsgParam(
													excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}else if(val.equals("外包合同")){
							contractInfo.setIsEpibolic(Boolean.TRUE);
						}else if(val.equals("内部合同")){
							contractInfo.setIsEpibolic(Boolean.FALSE);
						}else{
							return ResponseEntity.ok()
									.body(cpmResponse.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractInfo.upload.dataError").setMsgParam(
													excelValue.getSheet() + "," + rowNum + "," + (columnNum + 1)));
						}
						
						//检验第五列 销售人员工号
						columnNum ++;
						val = ls.get(columnNum);
						UserBaseVo salemanVo = null;
						if(!StringUtil.isNullStr(val)){
							salemanVo = userMap.get(val.toString());
							if(salemanVo == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.serialNumError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
							contractInfo.setSalesmanId(salemanVo.getId());
						}else{
							contractInfo.setSalesmanId(null);
						}
						
						//检验第六列 销售人员姓名(人员工号为空时，此为空)
						columnNum ++;
						if(contractInfo.getSalesmanId() == null){
							contractInfo.setSalesman(null);
						}else{
							contractInfo.setSalesman(salemanVo.getLastName());
						}
						
						//填充 销售部门(人员工号为空时，此为空)
						if(contractInfo.getSalesmanId() == null){
							contractInfo.setDeptId(null);
							contractInfo.setDept(null);
						}else{
							contractInfo.setDeptId(salemanVo.getDeptId());
							contractInfo.setDept(deptInfoMap.get(salemanVo.getDeptId()).getName());
						}
						
						//检验第七列 咨询人员工号
						columnNum ++;
						val = ls.get(columnNum);
						UserBaseVo consultantVo = null;
						if(!StringUtil.isNullStr(val)){
							consultantVo = userMap.get(val.toString());
							if(consultantVo == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.serialNumError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
							contractInfo.setConsultantsId(consultantVo.getId());
						}else{
							contractInfo.setConsultantsId(null);
						}
						//销售人员、咨询人员 至少有一个不为空
						if(contractInfo.getSalesmanId() == null && contractInfo.getConsultantsId() == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.salesmanAndconsultants")
									.setMsgParam(excelValue.getSheet() + "," + rowNum));
						}
						//检验第八列 咨询人员姓名(人员工号为空时，此为空)
						columnNum ++;
						if (contractInfo.getConsultantsId() == null) {
							contractInfo.setConsultants(null);
						}else{
							contractInfo.setConsultants(consultantVo.getLastName());
						}
						//填充 咨询部门(人员工号为空时，此为空)
						if(contractInfo.getConsultantsId() == null){
							contractInfo.setConsultantsDeptId(null);
							contractInfo.setConsultantsDept(null);
						}else{
							contractInfo.setConsultantsDeptId(consultantVo.getDeptId());
							contractInfo.setConsultantsDept(deptInfoMap.get(consultantVo.getDeptId()).getName());
						}
						
						//检验第九列 咨询分润比率(人员工号为空时，此为空)
						columnNum ++;
						val = ls.get(columnNum);
						if(contractInfo.getConsultantsId() == null){
							contractInfo.setConsultantsShareRate(null);
						}else if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){
							contractInfo.setConsultantsShareRate((Double)val);
						}else{//String
							contractInfo.setConsultantsShareRate(StringUtil.nullToCloneDouble(val));
							if(contractInfo.getConsultantsShareRate() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//检验第十列 开始日期 
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){
							contractInfo.setStartDay(((Date)val).toInstant().atZone(ZoneId.systemDefault()));
						}else{
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//检验第十一列 结束日期 
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){
							contractInfo.setEndDay(((Date)val).toInstant().atZone(ZoneId.systemDefault()));
						}else{
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						if(Date.from(contractInfo.getEndDay().toInstant()).getTime() < Date.from(contractInfo.getStartDay().toInstant()).getTime()){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dateError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//检验第十二列 合同金额 
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							contractInfo.setAmount((Double)val);
						}else {//string
							contractInfo.setAmount(StringUtil.nullToCloneDouble(val));
							if(contractInfo.getAmount() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//检验第十三列 付款方式
						columnNum ++;
						val = ls.get(columnNum);
						contractInfo.setPaymentWay(StringUtil.nullToString(val));
						
						//检验第十四列  税率
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							contractInfo.setTaxRate((Double)val);
						}else {//string
							contractInfo.setTaxRate(StringUtil.nullToCloneDouble(val));
							if(contractInfo.getTaxRate() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//检验第十五列 税费
						columnNum ++;
						contractInfo.setTaxes(contractInfo.getAmount() * contractInfo.getTaxRate() / 100);
						
						//检验第十六列 公摊比例
						columnNum ++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							contractInfo.setShareRate((Double)val);
						}else {//string
							contractInfo.setShareRate(StringUtil.nullToCloneDouble(val));
							if(contractInfo.getShareRate() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractInfo.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//第十七列 公摊成本
						columnNum ++;
						contractInfo.setShareCost(contractInfo.getAmount() * contractInfo.getShareRate() / 100);
						
						//检验第十八列 合同方公司
						columnNum ++;
						if(ls.size() > columnNum){
							val = ls.get(columnNum);
							if(val == null){
							}else{
								contractInfo.setContractor(StringUtil.nullToString(val));
							}
						}
						
						//检验第十九列 合同方联系人
						columnNum ++;
						if(ls.size() > columnNum){
							val = ls.get(columnNum);
							if(val == null){
							}else{
								contractInfo.setLinkman(StringUtil.nullToString(val));
							}
						}
						
						//检验第二十列 合同方联系部门
						columnNum ++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if (val == null) {
							} else {
								contractInfo.setContactDept(StringUtil.nullToString(val));
							} 
						}
						//检验第二十一列 合同方电话
						columnNum ++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if (val == null) {
							} else {
								contractInfo.setTelephone(StringUtil.nullToString(val));
							} 
						}
						//检验第二十二列 合同方通信地址
						columnNum ++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if (val == null) {
							} else {
								contractInfo.setAddress(StringUtil.nullToString(val));
							} 
						}
						//检验第二十三列 合同方邮编
						columnNum ++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if (val == null) {
							} else {
								contractInfo.setPostcode(StringUtil.nullToString(val));
							} 
						}
						
						//收款金额
						contractInfo.setReceiveTotal(0d);
						//合同累计完成金额
						contractInfo.setFinishTotal(0d);
						//完成率
						contractInfo.setFinishRate(0d);
						//校验记录是否存在
						String key = contractInfo.getSerialNum();
						if(existMap.containsKey(key)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.recordExistError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						existMap.put(key, 1);
						
						contractInfos.add(contractInfo);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractInfo.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//校验完成后，入库处理
			contractInfoService.saveOrUpdateUploadRecord(contractInfos);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.TRUE)
						.setMsgKey("cpmApp.contractInfo.upload.handleSucc"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.contractInfo.upload.handleError"));
		}
    }
}
