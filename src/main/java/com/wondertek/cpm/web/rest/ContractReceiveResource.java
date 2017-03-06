package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Date;
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
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.vo.ContractReceiveVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractReceiveService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractReceive.
 */
@RestController
@RequestMapping("/api")
public class ContractReceiveResource {

    private final Logger log = LoggerFactory.getLogger(ContractReceiveResource.class);
        
    @Inject
    private ContractReceiveService contractReceiveService;

    @PutMapping("/contract-receives")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_RECEIVE)
    public ResponseEntity<Void> updateContractReceive(@RequestBody ContractReceive contractReceive) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractReceive : {}", contractReceive);
        Boolean isNew = contractReceive.getId() == null;
        
        if (contractReceive.getContractId() == null
        		|| StringUtil.isNullStr(contractReceive.getReceiver()) || contractReceive.getReceiveDay() == null
        		|| contractReceive.getReceiveTotal() == null) { 
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.paramNone", "")).body(null);
		}
        if (contractReceive.getReceiveTotal() <= 0) { 
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.receiveTotalError", "")).body(null);
		}
        Long today = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
        if(contractReceive.getReceiveDay() > today){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.receiveDayError", "")).body(null);
        }
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        Double oldTotal = 0d;
        if (isNew) {
			contractReceive.setStatus(CpmConstants.STATUS_VALID);
			contractReceive.setCreateTime(updateTime);
			contractReceive.setCreator(updator);
		}else {
			ContractReceiveVo contractReceiveVo = contractReceiveService.getContractReceive(contractReceive.getId());
			if (contractReceiveVo == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.noPerm", "")).body(null);
			}
			if(contractReceiveVo.getContractId() != contractReceive.getContractId().longValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.contractChanged", "")).body(null);
        	}else if(contractReceiveVo.getStatus() == CpmConstants.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.status2Error", "")).body(null);
        	}
			oldTotal = contractReceiveVo.getReceiveTotal();
			contractReceive.setStatus(contractReceiveVo.getStatus());
			contractReceive.setCreateTime(contractReceiveVo.getCreateTime());
			contractReceive.setCreator(contractReceiveVo.getCreator());
		}
        contractReceive.setUpdateTime(updateTime);
        contractReceive.setUpdator(updator);
        ContractReceive result = contractReceiveService.save(contractReceive,oldTotal);
        
        if(isNew){
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityCreationAlert("contractReceive", result.getId().toString()))
        			.body(null);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractReceive", result.getId().toString()))
        			.body(null);
        }
    }

    @GetMapping("/contract-receives")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_RECEIVE)
    public ResponseEntity<List<ContractReceiveVo>> getAllContractReceives(
    		@RequestParam(value="contractId",required = false) Long contractId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractReceives by contractId : {}", contractId);
        ContractReceive contractReceive = new ContractReceive();
        contractReceive.setContractId(contractId);
        
        Page<ContractReceiveVo> page = contractReceiveService.getUserPage(contractReceive,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-receives");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/contract-receives/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_RECEIVE)
    public ResponseEntity<ContractReceiveVo> getContractReceive(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractReceive : {}", id);
        ContractReceiveVo contractReceive = contractReceiveService.getContractReceive(id);
        return Optional.ofNullable(contractReceive)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-receives/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_RECEIVE)
    public ResponseEntity<Void> deleteContractReceive(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ContractReceive : {}", id);
        ContractReceiveVo contractReceiveVo = contractReceiveService.getContractReceive(id);
        if(contractReceiveVo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.noPerm", "")).body(null);
        }
        if(contractReceiveVo.getStatus() == CpmConstants.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.delete.status2Error", "")).body(null);
        }
        contractReceiveService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractReceive", id.toString())).build();
    }
}
