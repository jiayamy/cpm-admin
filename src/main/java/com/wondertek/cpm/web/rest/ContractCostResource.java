package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

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
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.vo.ContractCostVo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.service.ContractCostService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

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
        if (contractInfo.getStatus() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractCost.save.contractInfoError", "")).body(null);
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
}
