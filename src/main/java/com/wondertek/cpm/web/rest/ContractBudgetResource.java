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
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractBudget.
 */
/**
 * @author sunshine
 * @Description : 
 * 
 */
@RestController
@RequestMapping("/api")
public class ContractBudgetResource {

    private final Logger log = LoggerFactory.getLogger(ContractBudgetResource.class);
        
    @Inject
    private ContractBudgetService contractBudgetService;
    
    /**
     * PUT  /contract-budgets : Updates an existing contractBudget.
     *
     * @param contractBudget the contractBudget to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractBudget,
     * or with status 400 (Bad Request) if the contractBudget is not valid,
     * or with status 500 (Internal Server Error) if the contractBudget couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-budgets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_BUDGET)
    public ResponseEntity<Boolean> updateContractBudget(@RequestBody ContractBudget contractBudget) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractBudget : {}", contractBudget);
        Boolean isNew = contractBudget.getId() == null;
        //检验参数
        if (contractBudget.getContractId() == null || contractBudget.getUserId() == null
        		|| StringUtil.isNullStr(contractBudget.getUserName()) || contractBudget.getDeptId() == null || StringUtil.isNullStr(contractBudget.getDept())
        		|| contractBudget.getPurchaseType() == null || contractBudget.getBudgetTotal() == null || contractBudget.getBudgetTotal() < 0 || StringUtil.isNullStr(contractBudget.getName())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.requiedError", "")).body(null);
		}
        Boolean flag = Boolean.FALSE;
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
        	ContractBudget oldContractBudget = contractBudgetService.findOneById(contractBudget.getId());
        	if(oldContractBudget == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.idNone", "")).body(null);
        	}else if(oldContractBudget.getContractId() != contractBudget.getContractId().longValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.contractIdError", "")).body(null);
        	}else if(oldContractBudget.getStatus() == ContractBudget.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.statue2Error", "")).body(null);
        	}
        	if(oldContractBudget.getPurchaseType() == ContractBudget.PURCHASETYPE_SERVICE && oldContractBudget.getPurchaseType() != contractBudget.getPurchaseType()){
        		//是否构建项目
        		flag = contractBudgetService.checkByBudget(contractBudget);
        		//构建项目
        		if (flag) {
        			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.projectError1", "")).body(null);
        		}
        	}else if (oldContractBudget.getPurchaseType() != ContractBudget.PURCHASETYPE_SERVICE) {
        		//是否构建项目
        		flag = contractBudgetService.checkByBudget(contractBudget);
        		//构建项目
        		if (flag) {
        			return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.projectError2", "")).body(null);
        		}
			}else if (oldContractBudget.getPurchaseType() == ContractBudget.PURCHASETYPE_SERVICE && oldContractBudget.getPurchaseType() == contractBudget.getPurchaseType()) {
				//是否构建项目
        		flag = contractBudgetService.checkByBudget(contractBudget);
        		//构建项目
        		if (flag) {
        			if (!contractBudget.getUserName().equals(oldContractBudget.getUserName()) || !contractBudget.getDept().equals(oldContractBudget.getDept()) 
        					|| contractBudget.getUserId() != oldContractBudget.getUserId() || contractBudget.getDeptId() != oldContractBudget.getDeptId()
        					|| contractBudget.getContractId() != oldContractBudget.getContractId()) {
        				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.projectError3", "")).body(null);
        			}
        			contractBudget.setUserId(oldContractBudget.getUserId());
        			contractBudget.setUserName(oldContractBudget.getUserName());
        			contractBudget.setDeptId(oldContractBudget.getDeptId());
        			contractBudget.setDept(oldContractBudget.getDept());
        			contractBudget.setContractId(oldContractBudget.getContractId());
        		}
			}
        	contractBudget.setCreateTime(oldContractBudget.getCreateTime());
        	contractBudget.setCreator(oldContractBudget.getCreator());
        	contractBudget.setStatus(oldContractBudget.getStatus());
        	contractBudget.setType(oldContractBudget.getType());
		}else {
			contractBudget.setCreateTime(updateTime);
    		contractBudget.setCreator(updator);
    		contractBudget.setType(ContractBudget.TYPE_PURCHASE);
    		contractBudget.setStatus(ContractBudget.STATUS_VALIDABLE);
		}
        contractBudget.setUpdateTime(updateTime);
		contractBudget.setUpdator(updator);
		ContractBudget result = contractBudgetService.save(contractBudget);
		
		if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("contractBudget", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractBudget", result.getId().toString()))
        			.body(isNew);
        }
        
    }

    /**
     * GET  /contract-budgets/:id : get the "id" contractBudget.
     *
     * @param id the id of the contractBudget to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractBudget, or with status 404 (Not Found)
     */
    @GetMapping("/contract-budgets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_BUDGET)
    public ResponseEntity<ContractBudgetVo> getContractBudgetVo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractBudget : {}", id);
        ContractBudgetVo contractBudget = contractBudgetService.getUserBudget(id);
        return Optional.ofNullable(contractBudget)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-budgets/:id : delete the "id" contractBudget.
     *
     * @param id the id of the contractBudget to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-budgets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_BUDGET)
    public ResponseEntity<Void> deleteContractBudget(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ContractBudget : {}", id);
        ContractBudgetVo contractBudget = contractBudgetService.getUserBudget(id);
        if (contractBudget == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.noPerm", "")).body(null);
		}
        if (contractBudget.getStatus() == ContractBudget.STATUS_DELETED) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractBudget.save.haveDeleted", "")).body(null);
		}
        contractBudgetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractBudget", id.toString())).build();
    }

    /**
     * GET  /product-prices : get all the contractBudget corresponding
     * to the query.
     *
     * @param query the query of the contractBudget search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-budgets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_BUDGET)
    public ResponseEntity<List<ContractBudgetVo>> getAllContractBudgetsByParams(
    		@RequestParam(value = "name",required=false) String name,
    		@RequestParam(value = "contractId",required=false) String contractId,
    		@RequestParam(value = "purchaseType",required=false) String purchaseType,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException{
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractBudget  name:{},contractId:{},purchaseType:{}",name,contractId,purchaseType);
    	ContractBudget contractBudget = new ContractBudget();
    	if (!StringUtil.isNullStr(name)) {
			contractBudget.setName(name);
		}
    	if (!StringUtil.isNullStr(contractId)) {
			contractBudget.setContractId(StringUtil.nullToLong(contractId));
		}
    	if (!StringUtil.isNullStr(purchaseType)) {
			contractBudget.setPurchaseType(StringUtil.nullToInteger(purchaseType));
		}
    	Page<ContractBudgetVo> page = contractBudgetService.searchPage(contractBudget,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(name, page,"/api/contract-budgets");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * 获取用户能查看的合同列表
     */
    @GetMapping("/contract-budgets/queryUserContract")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException{
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContract");
		List<LongValue> list = contractBudgetService.queryUserContract();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    	
    }
    /**
     * 获取用户能查看的合同上的内部采购单
     */
    @GetMapping("/contract-budgets/queryUserContractBudget")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContractBudget(@RequestParam(value = "contractId") String contractId) throws URISyntaxException{
    	 log.debug( SecurityUtils.getCurrentUserLogin() + " REST request to queryUserContractBudget : {}",contractId);
    	 Long contractIdLong = StringUtil.nullToCloneLong(contractId);
    	 if (contractIdLong == null) {
    		 return new ResponseEntity<>(new ArrayList<LongValue>(), null, HttpStatus.OK);
		}
    	 List<LongValue> list = contractBudgetService.queryUserContractBudget(contractIdLong);
    	 return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
}
