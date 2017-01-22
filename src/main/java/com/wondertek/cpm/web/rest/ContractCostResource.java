package com.wondertek.cpm.web.rest;

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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.vo.ContractCostVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.service.ContractCostService;
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
    /**
     * 新增和修改
     * @param contractCost
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/contract-costs")
    @Timed
    public ResponseEntity<Boolean> updateContractCost(@RequestBody ContractCost contractCost) throws URISyntaxException {
        log.debug("REST request to update ContractCost : {}", contractCost);
        Boolean isNew = contractCost.getId() == null;
        if (contractCost.getContractId() == null || contractCost.getBudgetId() == null
        		|| contractCost.getType() == null || contractCost.getCostDay() == null
        		|| contractCost.getTotal() == null || StringUtil.isNullStr(contractCost.getName())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.paramNone", "")).body(null);
		}
        if (contractCost.getType() == contractCost.TYPE_HUMAN_COST) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.type1Error", "")).body(null);
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        ContractCost newContractCost = contractCostService.getNewContratCost(contractCost);
        if (isNew) {
        	newContractCost.setStatus(CpmConstants.STATUS_VALID);
        	newContractCost.setCreateTime(updateTime);
        	newContractCost.setCreator(updator);
		}else {
			ContractCostVo contractCostVo = contractCostService.getContractCost(newContractCost.getId());
			if (contractCostVo == null) {
				return  ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.noPerm", "")).body(null);
			}
			ContractCost old = contractCostService.findOne(newContractCost.getId());
			if (old == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.idNone", "")).body(null);
			}else if (old.getContractId() != newContractCost.getContractId().longValue() || old.getBudgetId() != newContractCost.getBudgetId().longValue() ) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.contractIdError", "")).body(null);
			}else if (old.getStatus() == CpmConstants.STATUS_DELETED) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.statue2Error", "")).body(null);
			}else if (old.getType().intValue() != newContractCost.getType()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.type1Error", "")).body(null);
			}
			newContractCost.setStatus(old.getStatus());
			newContractCost.setCreateTime(old.getCreateTime());
			newContractCost.setCreator(old.getCreator());
		}
        newContractCost.setUpdateTime(updateTime);
        newContractCost.setUpdator(updator);
        ContractCost result =  contractCostService.save(newContractCost);
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
    /**
     * 页面搜索查询
     * @param contractId
     * @param type
     * @param name
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/contract-costs")
    @Timed
    public ResponseEntity<List<ContractCostVo>> getAllContractCosts(
    		@RequestParam(value = "contractId",required=false) Long contractId, 
    		@RequestParam(value = "type",required=false) Integer type, 
    		@RequestParam(value = "name",required=false) String name, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractCosts");
        ContractCost contractCost = new ContractCost();
        contractCost.setContractId(contractId);
        contractCost.setType(type);
        contractCost.setName(name);
        Page<ContractCostVo> page = contractCostService.getUserPage(contractCost,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-costs");
        
        return new ResponseEntity<List<ContractCostVo>>(page.getContent(), headers,HttpStatus.OK);
    }
    /**
     * 根据id查询ContractCostVo 返给页面，回显
     * @param id
     * @return
     */
    @GetMapping("/contract-costs/{id}")
    @Timed
    public ResponseEntity<ContractCostVo> getContractCost(@PathVariable Long id) {
        log.debug("REST request to get ContractCost : {}", id);
        ContractCostVo contractCost = contractCostService.getContractCost(id);
        return Optional.ofNullable(contractCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-costs/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractCost(@PathVariable Long id) {
        log.debug("REST request to delete ContractCost : {}", id);
        ContractCostVo contractCost = contractCostService.getContractCost(id);
        if (contractCost != null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.noPerm", "")).body(null);
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
    
    @GetMapping("/contract-costs/queryBudges")
    public ResponseEntity<List<LongValue>> queryBudges(@RequestParam(value = "contractId",required=false) Long contractId){
    	log.debug("REST request to queryBudges");
    	List<LongValue> list = contractBudgetService.queryBudges(contractId);
    	return new ResponseEntity<List<LongValue>>(list, HttpStatus.OK);
    }
    @GetMapping("/contract-costs/queryAllBudges")
    public ResponseEntity<List<LongValue>> queryAllBudges(){
    	log.debug("REST request to queryBudges");
    	List<LongValue> list = contractBudgetService.queryBudges();
    	return new ResponseEntity<List<LongValue>>(list, HttpStatus.OK);
    }
}
