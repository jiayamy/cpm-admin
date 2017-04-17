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
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.vo.ContractCostVo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.service.ContractCostService;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractCost.
 */
@RestController
@RequestMapping("/api")
public class ContractCostResource {

    private final Logger log = LoggerFactory.getLogger(ContractCostResource.class);
        
    @Inject
    private ContractCostService contractCostService;
    @Inject
    private ContractBudgetService contractBudgetService;
    @Inject
    private ContractInfoRepository contractInfoRepository;
    @Inject
    private DeptInfoRepository deptInfoRepository;
    @Inject
    private ContractInfoService contractInfoService;
    @Inject
    private DeptInfoService deptInfoService;

    @PutMapping("/contract-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_COST)
    public ResponseEntity<Boolean> updateContractCost(@RequestBody ContractCost contractCost) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractCost : {}", contractCost);
        Boolean isNew = contractCost.getId() == null;
        if (contractCost.getContractId() == null
        		|| contractCost.getType() == null || contractCost.getCostDay() == null
        		|| contractCost.getTotal() == null || StringUtil.isNullStr(contractCost.getName())
        		|| contractCost.getTotal() < 0 || contractCost.getDeptId() ==  null
        		|| StringUtil.isNullStr(contractCost.getDept())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.paramNone", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractCost.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus().intValue() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.contractInfoError", "")).body(null);
		}
        if (contractCost.getType() == ContractCost.TYPE_HUMAN_COST) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.type1Error", "")).body(null);
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
//        ContractCost newContractCost = contractCostService.getNewContratCost(contractCost);
        if (isNew) {
        	contractCost.setStatus(CpmConstants.STATUS_VALID);
        	contractCost.setCreateTime(updateTime);
        	contractCost.setCreator(updator);
		}else {
			ContractCostVo contractCostVo = contractCostService.getContractCost(contractCost.getId());
			if (contractCostVo == null) {
				return  ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.noPerm", "")).body(null);
			}
			ContractCost old = contractCostService.findOne(contractCost.getId());
			if (old == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.idNone", "")).body(null);
			}else if (old.getContractId() != contractCost.getContractId().longValue()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.contractIdError", "")).body(null);
			}else if (old.getStatus() == CpmConstants.STATUS_DELETED) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.statue2Error", "")).body(null);
			}else if (old.getType().intValue() != contractCost.getType()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.type1Error", "")).body(null);
			}
			contractCost.setStatus(old.getStatus());
			contractCost.setCreateTime(old.getCreateTime());
			contractCost.setCreator(old.getCreator());
		}
        contractCost.setUpdateTime(updateTime);
        contractCost.setUpdator(updator);
        ContractCost result =  contractCostService.save(contractCost);
        if (isNew) {
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("contractCost", result.getId().toString()))
                    .body(isNew);
			
		}else {
			return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractCost", result.getId().toString()))
        			.body(isNew);
		}
    }
    @GetMapping("/contract-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_COST)
    public ResponseEntity<List<ContractCostVo>> getAllContractCosts(
    		@RequestParam(value = "contractId",required=false) Long contractId, 
    		@RequestParam(value = "type",required=false) Integer type, 
    		@RequestParam(value = "name",required=false) String name, 
    		@RequestParam(value = "pageType",required=true) Integer pageType, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractCosts by contractId : {}, type : {}, name : {}, pageType : {}", contractId, type, name, pageType);
        ContractCost contractCost = new ContractCost();
        contractCost.setContractId(contractId);
        contractCost.setType(type);
        contractCost.setName(name);
        Page<ContractCostVo> page = contractCostService.getUserPage(contractCost,pageType,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-costs");
        
        return new ResponseEntity<List<ContractCostVo>>(page.getContent(), headers,HttpStatus.OK);
    }
    @GetMapping("/contract-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_COST)
    public ResponseEntity<ContractCostVo> getContractCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() +  " REST request to get ContractCost : {}", id);
        ContractCostVo contractCost = contractCostService.getContractCost(id);
        return Optional.ofNullable(contractCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_COST)
    public ResponseEntity<Void> deleteContractCost(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() +  " REST request to delete ContractCost : {}", id);
        ContractCostVo contractCost = contractCostService.getContractCost(id);
        if (contractCost == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.noPerm", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractCost.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.contractInfoError", "")).body(null);
		}
        if (contractCost.getStatus() == CpmConstants.STATUS_DELETED) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.delete.status2Error", "")).body(null);
		}
        if (contractCost.getType() == ContractCost.TYPE_HUMAN_COST) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.delete.type1Error", "")).body(null);
		}
        contractCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractCost", id.toString())).build();
    }
    
    @GetMapping("/contract-costs/uploadExcel")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_COST)
    public ResponseEntity<CpmResponse> uploadExcel(@RequestParam(value="filePath",required = true) String filePath){
    	log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to uploadExcel for filePath : {}",filePath);
    	List<ContractCost> contractCosts = null;
    	CpmResponse cpmResponse = new CpmResponse();
    	try {
    		//校验文件是否存在
			File file = new File(FilePathHelper.joinPath(CpmConstants.FILE_UPLOAD_SERVLET_BASE_PATH,filePath));
			if(!file.exists() || !file.isFile()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractCost.upload.requiredError"));
			}
			//从第一行读取，最多读取10个sheet，最多读取9列
        	int startNum = 1;
			List<ExcelValue> lists = ExcelUtil.readExcel(file,startNum,10,9);
			if(lists == null || lists.isEmpty()){
				return ResponseEntity.ok()
						.body(cpmResponse
								.setSuccess(Boolean.FALSE)
								.setMsgKey("cpmApp.contractCost.upload.requiredError"));
			}
			//初始化
			//初始化部门
			Map<String,Long> companys = deptInfoService.getUsedCompanyInfos();
			//获取公司下的部门
			Map<String,List<DeptInfo>> deptInfos = deptInfoService.getUsedDetpInfos(companys.values());
			//初始化部门信息
			List<DeptInfo> deptInfoList = deptInfoRepository.findAll();
			Map<String,DeptInfo> deptInfoMap = new HashMap<String,DeptInfo>();
			for(DeptInfo info : deptInfoList){
				deptInfoMap.put(info.getName(), info);
			}
			//初始化合同信息
			Map<String,ContractInfo> contractInfoMap = contractInfoService.getContractInfoMapBySerialnum();
			//其他信息
			contractCosts = new ArrayList<ContractCost>();
			String updator = SecurityUtils.getCurrentUserLogin();
			int columnNum = 0;
			int rowNum = 0;
			Object val = null;
			Long companyId = null;
			List<DeptInfo> primaryDeptInfos = null;	//一级部门
			List<DeptInfo> secondaryDeptInfos = null;	//二级部门
			//Map<String,Integer> existMap = new HashMap<String,Integer>();
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
						ContractCost contractCost = new ContractCost();
						contractCost.setStatus(CpmConstants.STATUS_VALID);
						contractCost.setCreator(updator);
						contractCost.setCreateTime(ZonedDateTime.now());
						contractCost.setUpdator(updator);
						contractCost.setUpdateTime(contractCost.getCreateTime());
						//校验第一列 合同编号
						columnNum = 0;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractCost.upload.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						ContractInfo info = contractInfoMap.get(val.toString());
						if(info == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.serialNumError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(info.getStatus() != ContractInfo.STATUS_VALIDABLE){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.statusError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						contractCost.setContractId(info.getId());
						
						//校验第二列 名称
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
											.setSuccess(Boolean.FALSE)
											.setMsgKey("cpmApp.contractCost.upload.dataError")
											.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						contractCost.setName(StringUtil.nullToString(val));
						
						//校验第三列 公司名 系统设置-部门信息中的顶级部门名称。方便获取下面的一级部门和二级部门
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						companyId = companys.get(StringUtil.null2Str(val));
						if(companyId == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第四列 一级部门 不可为空，且在一个公司下名称必须唯一
						columnNum++;
						val = ls.get(columnNum);
						if(val == null || StringUtil.isNullStr(val)){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						primaryDeptInfos = deptInfos.get(companyId + "_" + StringUtil.null2Str(val));
						if(primaryDeptInfos == null || primaryDeptInfos.isEmpty()){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(primaryDeptInfos.size() > 1){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第五列 二级部门 必须在一级部门下，可为空
						columnNum++;
						val = ls.get(columnNum);
						if(!StringUtil.isNullStr(val)){//校验二级部门是否存在
							secondaryDeptInfos = deptInfos.get(companyId + "_" + StringUtil.null2Str(val));
							if(secondaryDeptInfos == null || secondaryDeptInfos.isEmpty()){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractCost.upload.dataNotExist")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}									
						}
						
						Long deptId = getUserDeptFromDeptInfos(deptInfos,companyId,primaryDeptInfos.get(0),StringUtil.null2Str(val));
						if(deptId < 0){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.deptNotExist")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						contractCost.setDeptId(deptId);
						if (StringUtil.isNullStr(val)) {
							contractCost.setDept(primaryDeptInfos.get(0).getName());
						}else{
							contractCost.setDept(val.toString());
						}
						//校验第六列 成本类型
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						if(val.equals("差旅")){
							contractCost.setType(ContractCost.TYPE_TRAVEL_COST);
						}else if(val.equals("采购")){
							contractCost.setType(ContractCost.TYPE_PURCHASE_COST);
						}else if(val.equals("商务")){
							contractCost.setType(ContractCost.TYPE_BUSINESS_COST);
						}else if(val.equals("工时")){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.typeError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else{
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第七列 成本日期
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Date){//date
							contractCost.setCostDay(StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, (Date)val)));
						}else if(val instanceof Double){//double
							contractCost.setCostDay(((Double)val).longValue());
						}else{//String
							contractCost.setCostDay(StringUtil.nullToLong(val));
						}
						if(contractCost.getCostDay() == 0){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}
						
						//校验第八列 成本金额
						columnNum++;
						val = ls.get(columnNum);
						if(val == null){
							return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
						}else if(val instanceof Double){//double
							contractCost.setTotal((Double)val);
						}else{//String
							contractCost.setTotal(StringUtil.nullToCloneDouble(val));
							if(contractCost.getTotal() == null){
								return ResponseEntity.ok().body(cpmResponse
										.setSuccess(Boolean.FALSE)
										.setMsgKey("cpmApp.contractCost.upload.dataError")
										.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
							}
						}
						
						//校验第九列 成本描述
						columnNum++;
						if (ls.size() > columnNum) {
							val = ls.get(columnNum);
							if(val != null){
								contractCost.setCostDesc(val.toString());
							}
						}
						contractCosts.add(contractCost);
					} catch (Exception e) {
						log.error("校验excel数据出错，msg:"+e.getMessage(),e);
						return ResponseEntity.ok().body(cpmResponse
									.setSuccess(Boolean.FALSE)
									.setMsgKey("cpmApp.contractCost.upload.dataError")
									.setMsgParam(excelValue.getSheet() + "," + rowNum +","+(columnNum+1)));
					}
				}
			}
			//校验完成后，入库处理
			contractCostService.saveOrUpdateUploadRecord(contractCosts);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.TRUE)
						.setMsgKey("cpmApp.contractCost.upload.handleSucc"));
		} catch (IOException e) {
			log.error("msg:" + e.getMessage(),e);
			return ResponseEntity.ok().body(cpmResponse
						.setSuccess(Boolean.FALSE)
						.setMsgKey("cpmApp.contractCost.upload.handleError"));
		}
    }
    
    /**
     * 根据一级部门，二级部门来判定用户的所属部门
     * @return
     */
	private Long getUserDeptFromDeptInfos(Map<String, List<DeptInfo>> deptInfos, Long companyId, DeptInfo primaryDeptInfo, String secondaryDept) {
		if(secondaryDept == null || StringUtil.isNullStr(secondaryDept)){
			return primaryDeptInfo.getId();
		}
		List<DeptInfo> secondaryDepts = deptInfos.get(companyId + "_" + secondaryDept);
		String likePath = "/"+primaryDeptInfo.getId()+"/";
		for(DeptInfo deptInfo : secondaryDepts){
			if(deptInfo.getIdPath().indexOf(likePath) != -1){//属于一级部门的子部门
				return deptInfo.getId();
			}
		}
		return -1L;
	}
}
