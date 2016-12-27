package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractReceive;
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

    /**
     * POST  /contract-receives : Create a new contractReceive.
     *
     * @param contractReceive the contractReceive to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractReceive, or with status 400 (Bad Request) if the contractReceive has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
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

    /**
     * PUT  /contract-receives : Updates an existing contractReceive.
     *
     * @param contractReceive the contractReceive to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractReceive,
     * or with status 400 (Bad Request) if the contractReceive is not valid,
     * or with status 500 (Internal Server Error) if the contractReceive couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-receives")
    @Timed
    public ResponseEntity<ContractReceive> updateContractReceive(@RequestBody ContractReceive contractReceive) throws URISyntaxException {
        log.debug("REST request to update ContractReceive : {}", contractReceive);
        if (contractReceive.getId() == null) {
            return createContractReceive(contractReceive);
        }
        ContractReceive result = contractReceiveService.save(contractReceive);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractReceive", contractReceive.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contract-receives : get all the contractReceives.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractReceives in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-receives")
    @Timed
    public ResponseEntity<List<ContractReceive>> getAllContractReceives(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractReceives");
        Page<ContractReceive> page = contractReceiveService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-receives");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-receives/:id : get the "id" contractReceive.
     *
     * @param id the id of the contractReceive to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractReceive, or with status 404 (Not Found)
     */
    @GetMapping("/contract-receives/{id}")
    @Timed
    public ResponseEntity<ContractReceive> getContractReceive(@PathVariable Long id) {
        log.debug("REST request to get ContractReceive : {}", id);
        ContractReceive contractReceive = contractReceiveService.findOne(id);
        return Optional.ofNullable(contractReceive)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-receives/:id : delete the "id" contractReceive.
     *
     * @param id the id of the contractReceive to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-receives/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractReceive(@PathVariable Long id) {
        log.debug("REST request to delete ContractReceive : {}", id);
        contractReceiveService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractReceive", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-receives?query=:query : search for the contractReceive corresponding
     * to the query.
     *
     * @param query the query of the contractReceive search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-receives")
    @Timed
    public ResponseEntity<List<ContractReceive>> searchContractReceives(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractReceives for query {}", query);
        Page<ContractReceive> page = contractReceiveService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-receives");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
