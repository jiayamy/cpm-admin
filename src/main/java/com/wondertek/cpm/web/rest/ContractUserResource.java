package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.service.ContractUserService;
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
 * REST controller for managing ContractUser.
 */
@RestController
@RequestMapping("/api")
public class ContractUserResource {

    private final Logger log = LoggerFactory.getLogger(ContractUserResource.class);
        
    @Inject
    private ContractUserService contractUserService;

    /**
     * POST  /contract-users : Create a new contractUser.
     *
     * @param contractUser the contractUser to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractUser, or with status 400 (Bad Request) if the contractUser has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contract-users")
    @Timed
    public ResponseEntity<ContractUser> createContractUser(@RequestBody ContractUser contractUser) throws URISyntaxException {
        log.debug("REST request to save ContractUser : {}", contractUser);
        if (contractUser.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractUser", "idexists", "A new contractUser cannot already have an ID")).body(null);
        }
        ContractUser result = contractUserService.save(contractUser);
        return ResponseEntity.created(new URI("/api/contract-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contractUser", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contract-users : Updates an existing contractUser.
     *
     * @param contractUser the contractUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractUser,
     * or with status 400 (Bad Request) if the contractUser is not valid,
     * or with status 500 (Internal Server Error) if the contractUser couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-users")
    @Timed
    public ResponseEntity<ContractUser> updateContractUser(@RequestBody ContractUser contractUser) throws URISyntaxException {
        log.debug("REST request to update ContractUser : {}", contractUser);
        if (contractUser.getId() == null) {
            return createContractUser(contractUser);
        }
        ContractUser result = contractUserService.save(contractUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contractUser", contractUser.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contract-users : get all the contractUsers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractUsers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-users")
    @Timed
    public ResponseEntity<List<ContractUser>> getAllContractUsers(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractUsers");
        Page<ContractUser> page = contractUserService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-users/:id : get the "id" contractUser.
     *
     * @param id the id of the contractUser to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractUser, or with status 404 (Not Found)
     */
    @GetMapping("/contract-users/{id}")
    @Timed
    public ResponseEntity<ContractUser> getContractUser(@PathVariable Long id) {
        log.debug("REST request to get ContractUser : {}", id);
        ContractUser contractUser = contractUserService.findOne(id);
        return Optional.ofNullable(contractUser)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contract-users/:id : delete the "id" contractUser.
     *
     * @param id the id of the contractUser to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-users/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractUser(@PathVariable Long id) {
        log.debug("REST request to delete ContractUser : {}", id);
        contractUserService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractUser", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-users?query=:query : search for the contractUser corresponding
     * to the query.
     *
     * @param query the query of the contractUser search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-users")
    @Timed
    public ResponseEntity<List<ContractUser>> searchContractUsers(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractUsers for query {}", query);
        Page<ContractUser> page = contractUserService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
