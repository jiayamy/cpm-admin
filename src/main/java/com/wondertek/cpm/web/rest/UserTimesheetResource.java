package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.service.UserTimesheetService;
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
 * REST controller for managing UserTimesheet.
 */
@RestController
@RequestMapping("/api")
public class UserTimesheetResource {

    private final Logger log = LoggerFactory.getLogger(UserTimesheetResource.class);
        
    @Inject
    private UserTimesheetService userTimesheetService;

    /**
     * POST  /user-timesheets : Create a new userTimesheet.
     *
     * @param userTimesheet the userTimesheet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userTimesheet, or with status 400 (Bad Request) if the userTimesheet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-timesheets")
    @Timed
    public ResponseEntity<UserTimesheet> createUserTimesheet(@RequestBody UserTimesheet userTimesheet) throws URISyntaxException {
        log.debug("REST request to save UserTimesheet : {}", userTimesheet);
        if (userTimesheet.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTimesheet", "idexists", "A new userTimesheet cannot already have an ID")).body(null);
        }
        UserTimesheet result = userTimesheetService.save(userTimesheet);
        return ResponseEntity.created(new URI("/api/user-timesheets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("userTimesheet", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-timesheets : Updates an existing userTimesheet.
     *
     * @param userTimesheet the userTimesheet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userTimesheet,
     * or with status 400 (Bad Request) if the userTimesheet is not valid,
     * or with status 500 (Internal Server Error) if the userTimesheet couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-timesheets")
    @Timed
    public ResponseEntity<UserTimesheet> updateUserTimesheet(@RequestBody UserTimesheet userTimesheet) throws URISyntaxException {
        log.debug("REST request to update UserTimesheet : {}", userTimesheet);
        if (userTimesheet.getId() == null) {
            return createUserTimesheet(userTimesheet);
        }
        UserTimesheet result = userTimesheetService.save(userTimesheet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("userTimesheet", userTimesheet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-timesheets : get all the userTimesheets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userTimesheets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-timesheets")
    @Timed
    public ResponseEntity<List<UserTimesheet>> getAllUserTimesheets(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of UserTimesheets");
        Page<UserTimesheet> page = userTimesheetService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-timesheets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-timesheets/:id : get the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userTimesheet, or with status 404 (Not Found)
     */
    @GetMapping("/user-timesheets/{id}")
    @Timed
    public ResponseEntity<UserTimesheet> getUserTimesheet(@PathVariable Long id) {
        log.debug("REST request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetService.findOne(id);
        return Optional.ofNullable(userTimesheet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-timesheets/:id : delete the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-timesheets/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserTimesheet(@PathVariable Long id) {
        log.debug("REST request to delete UserTimesheet : {}", id);
        userTimesheetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userTimesheet", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-timesheets?query=:query : search for the userTimesheet corresponding
     * to the query.
     *
     * @param query the query of the userTimesheet search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/user-timesheets")
    @Timed
    public ResponseEntity<List<UserTimesheet>> searchUserTimesheets(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of UserTimesheets for query {}", query);
        Page<UserTimesheet> page = userTimesheetService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-timesheets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
