package com.wondertek.cpm.web.rest;

import java.net.URI;
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
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractInfoService;
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

	@PutMapping("/contract-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<Boolean> updateContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
        log.debug("REST request to update ContractInfo : {}", contractInfo);
        Boolean isNew = contractInfo.getId() == null;
        if(contractInfo.getIsPrepared() == null){
        	contractInfo.setIsPrepared(Boolean.FALSE);
        }
        if(contractInfo.getIsEpibolic() == null){
        	contractInfo.setIsEpibolic(Boolean.FALSE);
        }
        //基本校验
        if(contractInfo.getSalesmanId() == null && contractInfo.getConsultantsId() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.manallnone", "")).body(null);
        }
        if(contractInfo.getSalesmanId() != null){//销售不为空
        	if(StringUtil.isNullStr(contractInfo.getSalesman())
            		|| contractInfo.getDeptId() == null || StringUtil.isNullStr(contractInfo.getDept())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
        	}
        }
        if(contractInfo.getConsultantsId() != null){//咨询不为空
        	if(StringUtil.isNullStr(contractInfo.getConsultants())
            		|| contractInfo.getConsultantsDeptId() == null || StringUtil.isNullStr(contractInfo.getConsultantsDept())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
        	}
        	if(contractInfo.getConsultantsShareRate() == null){//咨询分润比例
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.consultantsRateError", "")).body(null);
        	}else if(contractInfo.getConsultantsShareRate() < 0d || contractInfo.getConsultantsShareRate() > 100d){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.consultantsRateError", "")).body(null);
        	}
        }else{//咨询分润比例，没咨询不需要咨询分润比例
        	contractInfo.setConsultantsShareRate(null);
        }
        if (StringUtil.isNullStr(contractInfo.getSerialNum()) || StringUtil.isNullStr(contractInfo.getName())
        		|| contractInfo.getAmount() == null || contractInfo.getType() == null
        		|| contractInfo.getStartDay() == null || contractInfo.getEndDay() == null
        		|| contractInfo.getTaxRate() == null || contractInfo.getTaxes() == null 
        		|| contractInfo.getShareRate() == null || contractInfo.getShareCost() == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
		}
        //结束时间校验
        if (contractInfo.getEndDay() != null && contractInfo.getEndDay().isBefore(contractInfo.getStartDay())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.endDayError", "")).body(null);
		}
        //查看合同是否是唯一
        boolean isExist = contractInfoService.checkByContract(contractInfo.getSerialNum(),contractInfo.getId());
        if (isExist) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.existSerialNum" ,"")).body(null);
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
        	//查看该用户是否有修改的权限
        	ContractInfoVo contractInfoVo = contractInfoService.getUserContractInfo(contractInfo.getId());
        	if(contractInfoVo == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.noPerm" ,"")).body(null);
        	}else if(contractInfoVo.getStatus() == ContractInfo.STATU_FINISH || contractInfoVo.getStatus() == ContractInfo.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.statusError" ,"")).body(null);
        	}else if(contractInfoVo.getIsPrepared() == false && contractInfo.getIsPrepared() == true){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.isPreparedError" ,"")).body(null);
        	}
        	contractInfo.setCreateTime(contractInfoVo.getCreateTime());
        	contractInfo.setCreator(contractInfoVo.getCreator());
        	contractInfo.setStatus(contractInfoVo.getStatus());
        	contractInfo.setFinishTotal(contractInfoVo.getFinishTotal());
        	contractInfo.setReceiveTotal(contractInfoVo.getReceiveTotal());
        	contractInfo.setFinishRate(contractInfoVo.getFinishRate());
		}else {
			contractInfo.setCreateTime(updateTime);
			contractInfo.setCreator(updator);
			contractInfo.setStatus(ContractInfo.STATUS_VALIDABLE);
			contractInfo.setFinishTotal(0d);
        	contractInfo.setReceiveTotal(0d);
			contractInfo.setFinishRate(0d);
		}
        contractInfo.setUpdateTime(updateTime);
        contractInfo.setUpdator(updator);
        ContractInfo result = contractInfoService.save(contractInfo);
        if(isNew){
        	return ResponseEntity.created(new URI("/api/project-infos/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("contractInfo", result.getId().toString()))
                    .body(true);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractInfo", contractInfo.getId().toString()))
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
		log.debug("REST request to get a page of ContractInfos");
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
		log.debug("REST request to get ContractInfo : {}", id);
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
		log.debug("REST request to delete ContractInfo : {}", id);
		contractInfoService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractInfo", id.toString())).build();
	}

	// 留作参考
	@GetMapping("/_search/contract-infos")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
	public ResponseEntity<List<ContractInfo>> searchContractInfos(@RequestParam String query,
			@ApiParam Pageable pageable) throws URISyntaxException {
		log.debug("REST request to search for a page of ContractInfos for query {}", query);
		Page<ContractInfo> page = contractInfoService.search(query, pageable);
		HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page,
				"/api/_search/contract-infos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@GetMapping("/contract-infos/queryUserContract")
	@Secured(AuthoritiesConstants.USER)
	public ResponseEntity<List<LongValue>> queryUserContract() {
		log.debug("REST request to queryUserContract");
		 List<LongValue> list = contractInfoService.queryUserContract();
		 return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
	

	@PutMapping("/contract-infos/finish")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_INFO)
    public ResponseEntity<ProjectInfo> finishContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
    	log.debug("REST request to finishContractInfo");
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
}
