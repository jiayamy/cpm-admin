package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.service.ProjectCostService;
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
 * REST controller for managing ProjectCost.
 */
@RestController
@RequestMapping("/api")
public class ProjectCostResource {

    private final Logger log = LoggerFactory.getLogger(ProjectCostResource.class);
        
    @Inject
    private ProjectCostService projectCostService;

    /**
     * POST  /project-costs : Create a new projectCost.
     *
     * @param projectCost the projectCost to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectCost, or with status 400 (Bad Request) if the projectCost has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-costs")
    @Timed
    public ResponseEntity<ProjectCost> createProjectCost(@RequestBody ProjectCost projectCost) throws URISyntaxException {
        log.debug("REST request to save ProjectCost : {}", projectCost);
        if (projectCost.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectCost", "idexists", "A new projectCost cannot already have an ID")).body(null);
        }
        ProjectCost result = projectCostService.save(projectCost);
        return ResponseEntity.created(new URI("/api/project-costs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectCost", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-costs : Updates an existing projectCost.
     *
     * @param projectCost the projectCost to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectCost,
     * or with status 400 (Bad Request) if the projectCost is not valid,
     * or with status 500 (Internal Server Error) if the projectCost couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-costs")
    @Timed
    public ResponseEntity<ProjectCost> updateProjectCost(@RequestBody ProjectCost projectCost) throws URISyntaxException {
        log.debug("REST request to update ProjectCost : {}", projectCost);
        if (projectCost.getId() == null) {
            return createProjectCost(projectCost);
        }
        ProjectCost result = projectCostService.save(projectCost);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectCost", projectCost.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-costs : get all the projectCosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectCosts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-costs")
    @Timed
    public ResponseEntity<List<ProjectCost>> getAllProjectCosts(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectCosts");
        Page<ProjectCost> page = projectCostService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-costs/:id : get the "id" projectCost.
     *
     * @param id the id of the projectCost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectCost, or with status 404 (Not Found)
     */
    @GetMapping("/project-costs/{id}")
    @Timed
    public ResponseEntity<ProjectCost> getProjectCost(@PathVariable Long id) {
        log.debug("REST request to get ProjectCost : {}", id);
        ProjectCost projectCost = projectCostService.findOne(id);
        return Optional.ofNullable(projectCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-costs/:id : delete the "id" projectCost.
     *
     * @param id the id of the projectCost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-costs/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectCost(@PathVariable Long id) {
        log.debug("REST request to delete ProjectCost : {}", id);
        projectCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectCost", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-costs?query=:query : search for the projectCost corresponding
     * to the query.
     *
     * @param query the query of the projectCost search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-costs")
    @Timed
    public ResponseEntity<List<ProjectCost>> searchProjectCosts(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectCosts for query {}", query);
        Page<ProjectCost> page = projectCostService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
