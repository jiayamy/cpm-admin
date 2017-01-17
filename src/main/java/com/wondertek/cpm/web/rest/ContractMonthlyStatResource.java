package com.wondertek.cpm.web.rest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.service.ContractMonthlyStatService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractMonthlyStat.
 */
@RestController
@RequestMapping("/api")
public class ContractMonthlyStatResource {
	
	private final Logger log = LoggerFactory.getLogger(ContractMonthlyStatResource.class);
	
	@Inject
	private ContractMonthlyStatService contractMonthlyStatService;
	
	/**
     * GET  /contract-monthly-stats : get all the ContractMonthlyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ContractMonthlyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-monthly-stats")
    @Timed
    public ResponseEntity<List<ContractMonthlyStatVo>> getAllContractMonthlyStats(
    		@ApiParam(value="contractId") @RequestParam(value="contractId") String contractId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractMonthlyStats");
        Page<ContractMonthlyStatVo> page = contractMonthlyStatService.getStatPage(contractId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /contract-monthly-stats/:id : get the "id" ContractMonthlyStat.
     *
     * @param id the id of the ContractMonthlyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ContractMonthlyStat, or with status 404 (Not Found)
     */
    @GetMapping("/contract-monthly-stats/{id}")
    @Timed
    public ResponseEntity<ContractMonthlyStatVo> getContractMonthlyStat(@PathVariable Long id) {
        log.debug("REST request to get ContractMonthlyStats : {}", id);
        ContractMonthlyStatVo contractMonthlyStat = contractMonthlyStatService.findOne(id);
        return Optional.ofNullable(contractMonthlyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * DELETE  /contract-monthly-stats/:id : delete the "id" contractMonthlyStats.
     *
     * @param id the id of the contractMonthlyStats to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-monthly-stats/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractMonthlyStat(@PathVariable Long id) {
        log.debug("REST request to delete ContractMonthlyStats : {}", id);
        contractMonthlyStatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractMonthlyStats", id.toString())).build();
    }
    
    /**
     * SEARCH  /_search/contract-monthly-stats?query=:query : search for the contractMonthlyStats corresponding
     * to the query.
     *
     * @param query the query of the contractMonthlyStats search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-monthly-stats")
    @Timed
    public ResponseEntity<List<ContractMonthlyStat>> searchContractMonthlyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractMonthlyStat for query {}", query);
        Page<ContractMonthlyStat> page = contractMonthlyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/contract-monthly-stats/queryUserContract")
    @Timed
	public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException {
	    log.debug("REST request to queryUserProject");
	    List<LongValue> list = contractMonthlyStatService.queryUserContract();
	    return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
}
