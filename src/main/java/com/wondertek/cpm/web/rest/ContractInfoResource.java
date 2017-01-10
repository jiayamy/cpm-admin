package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.wondertek.cpm.domain.ContractInfo;
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

    /**
     * POST  /contract-infos : Create a new contractInfo.
     *
     * @param contractInfo the contractInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractInfo, or with status 400 (Bad Request) if the contractInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contract-infos")
    @Timed
    public ResponseEntity<ContractInfo> createContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
        log.debug("REST request to save ContractInfo : {}", contractInfo);
        if (contractInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractInfo", "idexists", "A new contractInfo cannot already have an ID")).body(null);
        }
        ContractInfo result = contractInfoService.save(contractInfo);
        return ResponseEntity.created(new URI("/api/contract-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contract-infos : Updates an existing contractInfo.
     *
     * @param contractInfo the contractInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractInfo,
     * or with status 400 (Bad Request) if the contractInfo is not valid,
     * or with status 500 (Internal Server Error) if the contractInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-infos")
    @Timed
    public ResponseEntity<ContractInfo> updateContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
        log.debug("REST request to update ContractInfo : {}", contractInfo);
        if (contractInfo.getId() == null) {
            return createContractInfo(contractInfo);
        }
        ContractInfo result = contractInfoService.save(contractInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractInfo", contractInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contract-infos : get all the contractInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-infos")
    @Timed
    public ResponseEntity<List<ContractInfo>> getAllContractInfos(
    		@RequestParam(value = "name") String name,
    		@RequestParam(value = "type") Integer type,
    		@RequestParam(value = "isPrepared") Boolean isPrepared,
    		@RequestParam(value = "isEpibolic") Boolean isEpibolic,
    		@RequestParam(value = "salesman") Long salesman,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractInfos");
        ContractInfo contractInfo = new ContractInfo();
       
        contractInfo.setSalesmanId(salesman);
        contractInfo.setName(name);
        contractInfo.setType(type);
        contractInfo.setIsPrepared(isPrepared);
        contractInfo.setIsEpibolic(isEpibolic);
        
        Page<ContractInfo> page = contractInfoService.getContractInfoPage(contractInfo,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-infos/:id : get the "id" contractInfo.
     *
     * @param id the id of the contractInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractInfo, or with status 404 (Not Found)
     */
    @GetMapping("/contract-infos/{id}")
    @Timed
    public ResponseEntity<ContractInfo> getContractInfo(@PathVariable Long id) {
        log.debug("REST request to get ContractInfo : {}", id);
        ContractInfo contractInfo = contractInfoService.findOne(id);
        return Optional.ofNullable(contractInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-infos/:id : delete the "id" contractInfo.
     *
     * @param id the id of the contractInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractInfo(@PathVariable Long id) {
        log.debug("REST request to delete ContractInfo : {}", id);
        contractInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-infos?query=:query : search for the contractInfo corresponding
     * to the query.
     *
     * @param query the query of the contractInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-infos")
    @Timed
    public ResponseEntity<List<ContractInfo>> searchContractInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractInfos for query {}", query);
        Page<ContractInfo> page = contractInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
