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
import com.wondertek.cpm.domain.WorkArea;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.WorkAreaService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing WorkArea.
 */
@RestController
@RequestMapping("/api")
public class WorkAreaResource {

    private final Logger log = LoggerFactory.getLogger(WorkAreaResource.class);
        
    @Inject
    private WorkAreaService workAreaService;

    /**
     * POST  /work-areas : Create a new workArea.
     *
     * @param workArea the workArea to create
     * @return the ResponseEntity with status 201 (Created) and with body the new workArea, or with status 400 (Bad Request) if the workArea has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/work-areas")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<WorkArea> createWorkArea(@RequestBody WorkArea workArea) throws URISyntaxException {
        log.debug("REST request to save WorkArea : {}", workArea);
        if (workArea.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("workArea", "idexists", "A new workArea cannot already have an ID")).body(null);
        }
        WorkArea result = workAreaService.save(workArea);
        return ResponseEntity.created(new URI("/api/work-areas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("workArea", result.getId().toString()))
            .body(result);
    }

//    /**
//     * PUT  /work-areas : Updates an existing workArea.
//     *
//     * @param workArea the workArea to update
//     * @return the ResponseEntity with status 200 (OK) and with body the updated workArea,
//     * or with status 400 (Bad Request) if the workArea is not valid,
//     * or with status 500 (Internal Server Error) if the workArea couldnt be updated
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PutMapping("/work-areas")
//    @Timed
//    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
//    public ResponseEntity<WorkArea> updateWorkArea(@RequestBody WorkArea workArea) throws URISyntaxException {
//        log.debug("REST request to update WorkArea : {}", workArea);
//        if (workArea.getId() == null) {
//            return createWorkArea(workArea);
//        }
//        WorkArea result = workAreaService.save(workArea);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert("workArea", workArea.getId().toString()))
//            .body(result);
//    }

    /**
     * GET  /work-areas : get all the workAreas.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of workAreas in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/work-areas")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<WorkArea>> getAllWorkAreas(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of WorkAreas");
        Page<WorkArea> page = workAreaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/work-areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /work-areas/:id : get the "id" workArea.
     *
     * @param id the id of the workArea to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the workArea, or with status 404 (Not Found)
     */
    @GetMapping("/work-areas/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<WorkArea> getWorkArea(@PathVariable Long id) {
        log.debug("REST request to get WorkArea : {}", id);
        WorkArea workArea = workAreaService.findOne(id);
        return Optional.ofNullable(workArea)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /work-areas/:id : delete the "id" workArea.
     *
     * @param id the id of the workArea to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/work-areas/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteWorkArea(@PathVariable Long id) {
        log.debug("REST request to delete WorkArea : {}", id);
        workAreaService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("workArea", id.toString())).build();
    }

    /**
     * SEARCH  /_search/work-areas?query=:query : search for the workArea corresponding
     * to the query.
     *
     * @param query the query of the workArea search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/work-areas")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<WorkArea>> searchWorkAreas(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of WorkAreas for query {}", query);
        Page<WorkArea> page = workAreaService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/work-areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/work-areas/queryAll")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<String>> queryAllWorkAreas() throws URISyntaxException {
    	log.debug("REST request to queryAllWorkAreas");
        List<String> page = workAreaService.queryAll();
        return new ResponseEntity<>(page, null, HttpStatus.OK);
    }
    
}
