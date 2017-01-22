package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.vo.ContractReceiveVo;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractReceiveService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ContractReceive.
 */
@RestController
@RequestMapping("/api")
public class ContractReceiveResource {

    private final Logger log = LoggerFactory.getLogger(ContractReceiveResource.class);
        
    @Inject
    private ContractReceiveService contractReceiveService;

    @PostMapping("/contract-receives")
    @Timed
    public ResponseEntity<ContractReceive> createContractReceive(@RequestBody ContractReceive contractReceive) throws URISyntaxException {
        log.debug("REST request to save ContractReceive : {}", contractReceive);
        if (contractReceive.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractReceive", "idexists", "A new contractReceive cannot already have an ID")).body(null);
        }
        ContractReceive result = contractReceiveService.save(contractReceive);
        return ResponseEntity.created(new URI("/api/contract-receives/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractReceive", result.getId().toString()))
            .body(result);
    }

    @PutMapping("/contract-receives")
    @Timed
    public ResponseEntity<Boolean> updateContractReceive(@RequestBody ContractReceive contractReceive) throws URISyntaxException {
        log.debug("REST request to update ContractReceive : {}", contractReceive);
        Boolean isNew = contractReceive.getId() == null;
        if (contractReceive.getContractId() == null || contractReceive.getCreateTime() == null
        		|| StringUtil.isNullStr(contractReceive.getReceiver()) || contractReceive.getReceiveDay() == null
        		|| contractReceive.getReceiveTotal() == null) { 
        		
        		return null;
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (isNew) {
			contractReceive.setStatus(CpmConstants.STATUS_VALID);
			contractReceive.setCreateTime(updateTime);
		}else {
			ContractReceiveVo contractReceiveVo = contractReceiveService.getContractReceive(contractReceive.getId());
			if (contractReceiveVo == null) {
				return null;
			}
			ContractReceive old = contractReceiveService.findOne(contractReceive.getId());
			if(old == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectCost.save.idNone", "")).body(null);
        	}else if(old.getContractId() != contractReceive.getContractId().longValue()){
        		return null;
        	}else if(old.getStatus() == CpmConstants.STATUS_DELETED){
        		return null;
        	}
			contractReceive.setStatus(old.getStatus());
			contractReceive.setCreateTime(old.getCreateTime());
			contractReceive.setCreator(old.getCreator());
		}
        contractReceive.setUpdateTime(updateTime);
        contractReceive.setUpdator(updator);
        ContractReceive result = contractReceiveService.save(contractReceive);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractReceive", result.getId().toString()))
            .body(isNew);
    }

    @GetMapping("/contract-receives")
    @Timed
    public ResponseEntity<List<ContractReceiveVo>> getAllContractReceives(@RequestParam(value="contractId",required = false) Long contractId,@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractReceives");
        ContractReceive contractReceive = new ContractReceive();
        contractReceive.setContractId(contractId);
        
        
        Page<ContractReceiveVo> page = contractReceiveService.getuserPage(contractReceive,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-receives");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/contract-receives/{id}")
    @Timed
    public ResponseEntity<ContractReceiveVo> getContractReceive(@PathVariable Long id) {
        log.debug("REST request to get ContractReceive : {}", id);
        ContractReceiveVo contractReceive = contractReceiveService.getContractReceive(id);
        return Optional.ofNullable(contractReceive)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-receives/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractReceive(@PathVariable Long id) {
        log.debug("REST request to delete ContractReceive : {}", id);
        contractReceiveService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractReceive", id.toString())).build();
    }
}
