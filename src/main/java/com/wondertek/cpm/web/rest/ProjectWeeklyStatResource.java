package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.service.ProjectWeeklyStatService;
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
 * REST controller for managing ProjectWeeklyStat.
 */
@RestController
@RequestMapping("/api")
public class ProjectWeeklyStatResource {

    private final Logger log = LoggerFactory.getLogger(ProjectWeeklyStatResource.class);
        
    @Inject
    private ProjectWeeklyStatService projectWeeklyStatService;

    /**
     * POST  /project-weekly-stats : Create a new projectWeeklyStat.
     *
     * @param projectWeeklyStat the projectWeeklyStat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectWeeklyStat, or with status 400 (Bad Request) if the projectWeeklyStat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-weekly-stats")
    @Timed
    public ResponseEntity<ProjectWeeklyStat> createProjectWeeklyStat(@RequestBody ProjectWeeklyStat projectWeeklyStat) throws URISyntaxException {
        log.debug("REST request to save ProjectWeeklyStat : {}", projectWeeklyStat);
        if (projectWeeklyStat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectWeeklyStat", "idexists", "A new projectWeeklyStat cannot already have an ID")).body(null);
        }
        ProjectWeeklyStat result = projectWeeklyStatService.save(projectWeeklyStat);
        return ResponseEntity.created(new URI("/api/project-weekly-stats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectWeeklyStat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-weekly-stats : Updates an existing projectWeeklyStat.
     *
     * @param projectWeeklyStat the projectWeeklyStat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectWeeklyStat,
     * or with status 400 (Bad Request) if the projectWeeklyStat is not valid,
     * or with status 500 (Internal Server Error) if the projectWeeklyStat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-weekly-stats")
    @Timed
    public ResponseEntity<ProjectWeeklyStat> updateProjectWeeklyStat(@RequestBody ProjectWeeklyStat projectWeeklyStat) throws URISyntaxException {
        log.debug("REST request to update ProjectWeeklyStat : {}", projectWeeklyStat);
        if (projectWeeklyStat.getId() == null) {
            return createProjectWeeklyStat(projectWeeklyStat);
        }
        ProjectWeeklyStat result = projectWeeklyStatService.save(projectWeeklyStat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectWeeklyStat", projectWeeklyStat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-weekly-stats : get all the projectWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-weekly-stats")
    @Timed
    public ResponseEntity<List<ProjectWeeklyStat>> getAllProjectWeeklyStats(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectWeeklyStats");
        Page<ProjectWeeklyStat> page = projectWeeklyStatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-weekly-stats/:id : get the "id" projectWeeklyStat.
     *
     * @param id the id of the projectWeeklyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectWeeklyStat, or with status 404 (Not Found)
     */
    @GetMapping("/project-weekly-stats/{id}")
    @Timed
    public ResponseEntity<ProjectWeeklyStat> getProjectWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to get ProjectWeeklyStat : {}", id);
        ProjectWeeklyStat projectWeeklyStat = projectWeeklyStatService.findOne(id);
        return Optional.ofNullable(projectWeeklyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-weekly-stats/:id : delete the "id" projectWeeklyStat.
     *
     * @param id the id of the projectWeeklyStat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-weekly-stats/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to delete ProjectWeeklyStat : {}", id);
        projectWeeklyStatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectWeeklyStat", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-weekly-stats?query=:query : search for the projectWeeklyStat corresponding
     * to the query.
     *
     * @param query the query of the projectWeeklyStat search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-weekly-stats")
    @Timed
    public ResponseEntity<List<ProjectWeeklyStat>> searchProjectWeeklyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectWeeklyStats for query {}", query);
        Page<ProjectWeeklyStat> page = projectWeeklyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
