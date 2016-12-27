package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.service.ContractCostService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

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

    /**
     * GET  /contract-costs : get all the contractCosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractCosts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-costs")
    @Timed
    public ResponseEntity<List<ContractCost>> getAllContractCosts(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractCosts");
        Page<ContractCost> page = contractCostService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-costs/:id : get the "id" contractCost.
     *
     * @param id the id of the contractCost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractCost, or with status 404 (Not Found)
     */
    @GetMapping("/contract-costs/{id}")
    @Timed
    public ResponseEntity<ContractCost> getContractCost(@PathVariable Long id) {
        log.debug("REST request to get ContractCost : {}", id);
        ContractCost contractCost = contractCostService.findOne(id);
        return Optional.ofNullable(contractCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-costs/:id : delete the "id" contractCost.
     *
     * @param id the id of the contractCost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-costs/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractCost(@PathVariable Long id) {
        log.debug("REST request to delete ContractCost : {}", id);
        contractCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractCost", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-costs?query=:query : search for the contractCost corresponding
     * to the query.
     *
     * @param query the query of the contractCost search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
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
