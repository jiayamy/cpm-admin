package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.vo.ContractCostVo;
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

    /**
     * POST  /contract-costs : Create a new contractCost.
     *
     * @param contractCost the contractCost to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractCost, or with status 400 (Bad Request) if the contractCost has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contract-costs")
    @Timed
    public ResponseEntity<ContractCost> createContractCost(@RequestBody ContractCost contractCost) throws URISyntaxException {
        log.debug("REST request to save ContractCost : {}", contractCost);
        if (contractCost.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractCost", "idexists", "A new contractCost cannot already have an ID")).body(null);
        }
        ContractCost result = contractCostService.save(contractCost);
        return ResponseEntity.created(new URI("/api/contract-costs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractCost", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contract-costs : Updates an existing contractCost.
     *
     * @param contractCost the contractCost to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractCost,
     * or with status 400 (Bad Request) if the contractCost is not valid,
     * or with status 500 (Internal Server Error) if the contractCost couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-costs")
    @Timed
    public ResponseEntity<ContractCost> updateContractCost(@RequestBody ContractCost contractCost) throws URISyntaxException {
        log.debug("REST request to update ContractCost : {}", contractCost);
        if (contractCost.getId() == null) {
            return createContractCost(contractCost);
        }
        ContractCost result = contractCostService.save(contractCost);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractCost", contractCost.getId().toString()))
            .body(result);
    }
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
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-costs");
        
        return new ResponseEntity<List<ContractCostVo>>(page.getContent(), headers,HttpStatus.OK);
    }

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
        contractCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractCost", id.toString())).build();
    }

    @GetMapping("/_search/contract-costs")
    @Timed
    public ResponseEntity<List<ContractCost>> searchContractCosts(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractCosts for query {}", query);
        Page<ContractCost> page = contractCostService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
