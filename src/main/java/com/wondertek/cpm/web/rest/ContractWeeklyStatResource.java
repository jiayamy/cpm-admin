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
import org.springframework.security.access.annotation.Secured;
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
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ContractWeeklyStatService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractWeeklyStat.
 */
@RestController
@RequestMapping("/api")
public class ContractWeeklyStatResource {

    private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatResource.class);
        
    @Inject
    private ContractWeeklyStatService contractWeeklyStatService;

    /**
     * POST  /contract-weekly-stats : Create a new contractWeeklyStat.
     *
     * @param contractWeeklyStat the contractWeeklyStat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractWeeklyStat, or with status 400 (Bad Request) if the contractWeeklyStat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ContractWeeklyStat> createContractWeeklyStat(@RequestBody ContractWeeklyStat contractWeeklyStat) throws URISyntaxException {
        log.debug("REST request to save ContractWeeklyStat : {}", contractWeeklyStat);
        if (contractWeeklyStat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractWeeklyStat", "idexists", "A new contractWeeklyStat cannot already have an ID")).body(null);
        }
        ContractWeeklyStat result = contractWeeklyStatService.save(contractWeeklyStat);
        return ResponseEntity.created(new URI("/api/contract-weekly-stats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractWeeklyStat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contract-weekly-stats : Updates an existing contractWeeklyStat.
     *
     * @param contractWeeklyStat the contractWeeklyStat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractWeeklyStat,
     * or with status 400 (Bad Request) if the contractWeeklyStat is not valid,
     * or with status 500 (Internal Server Error) if the contractWeeklyStat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ContractWeeklyStat> updateContractWeeklyStat(@RequestBody ContractWeeklyStat contractWeeklyStat) throws URISyntaxException {
        log.debug("REST request to update ContractWeeklyStat : {}", contractWeeklyStat);
        if (contractWeeklyStat.getId() == null) {
            return createContractWeeklyStat(contractWeeklyStat);
        }
        ContractWeeklyStat result = contractWeeklyStatService.save(contractWeeklyStat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractWeeklyStat", contractWeeklyStat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contract-weekly-stats : get all the contractWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractWeeklyStat>> getAllContractWeeklyStats(
    		@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="statDate") @RequestParam(value="statDate") String statDate,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractWeeklyStats");
        Page<ContractWeeklyStat> page = contractWeeklyStatService.getStatPage(fromDate, toDate, statDate, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-weekly-stats/:id : get the "id" contractWeeklyStat.
     *
     * @param id the id of the contractWeeklyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractWeeklyStat, or with status 404 (Not Found)
     */
    @GetMapping("/contract-weekly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ContractWeeklyStat> getContractWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to get ContractWeeklyStat : {}", id);
        ContractWeeklyStat contractWeeklyStat = contractWeeklyStatService.findOne(id);
        return Optional.ofNullable(contractWeeklyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-weekly-stats/:id : delete the "id" contractWeeklyStat.
     *
     * @param id the id of the contractWeeklyStat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-weekly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<Void> deleteContractWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to delete ContractWeeklyStat : {}", id);
        contractWeeklyStatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractWeeklyStat", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-weekly-stats?query=:query : search for the contractWeeklyStat corresponding
     * to the query.
     *
     * @param query the query of the contractWeeklyStat search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractWeeklyStat>> searchContractWeeklyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractWeeklyStats for query {}", query);
        Page<ContractWeeklyStat> page = contractWeeklyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
