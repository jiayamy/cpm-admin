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
import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectFinishInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectFinishInfo.
 */
@RestController
@RequestMapping("/api")
public class ProjectFinishInfoResource {

    private final Logger log = LoggerFactory.getLogger(ProjectFinishInfoResource.class);
        
    @Inject
    private ProjectFinishInfoService projectFinishInfoService;

    /**
     * POST  /project-finish-infos : Create a new projectFinishInfo.
     *
     * @param projectFinishInfo the projectFinishInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectFinishInfo, or with status 400 (Bad Request) if the projectFinishInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-finish-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<ProjectFinishInfo> createProjectFinishInfo(@RequestBody ProjectFinishInfo projectFinishInfo) throws URISyntaxException {
        log.debug("REST request to save ProjectFinishInfo : {}", projectFinishInfo);
        if (projectFinishInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectFinishInfo", "idexists", "A new projectFinishInfo cannot already have an ID")).body(null);
        }
        ProjectFinishInfo result = projectFinishInfoService.save(projectFinishInfo);
        return ResponseEntity.created(new URI("/api/project-finish-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectFinishInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-finish-infos : Updates an existing projectFinishInfo.
     *
     * @param projectFinishInfo the projectFinishInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectFinishInfo,
     * or with status 400 (Bad Request) if the projectFinishInfo is not valid,
     * or with status 500 (Internal Server Error) if the projectFinishInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-finish-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<ProjectFinishInfo> updateProjectFinishInfo(@RequestBody ProjectFinishInfo projectFinishInfo) throws URISyntaxException {
        log.debug("REST request to update ProjectFinishInfo : {}", projectFinishInfo);
        if (projectFinishInfo.getId() == null) {
            return createProjectFinishInfo(projectFinishInfo);
        }
        ProjectFinishInfo result = projectFinishInfoService.save(projectFinishInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectFinishInfo", projectFinishInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-finish-infos : get all the projectFinishInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectFinishInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-finish-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<List<ProjectFinishInfo>> getAllProjectFinishInfos(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectFinishInfos");
        Page<ProjectFinishInfo> page = projectFinishInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-finish-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-finish-infos/:id : get the "id" projectFinishInfo.
     *
     * @param id the id of the projectFinishInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectFinishInfo, or with status 404 (Not Found)
     */
    @GetMapping("/project-finish-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<ProjectFinishInfo> getProjectFinishInfo(@PathVariable Long id) {
        log.debug("REST request to get ProjectFinishInfo : {}", id);
        ProjectFinishInfo projectFinishInfo = projectFinishInfoService.findOne(id);
        return Optional.ofNullable(projectFinishInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-finish-infos/:id : delete the "id" projectFinishInfo.
     *
     * @param id the id of the projectFinishInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-finish-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<Void> deleteProjectFinishInfo(@PathVariable Long id) {
        log.debug("REST request to delete ProjectFinishInfo : {}", id);
        projectFinishInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectFinishInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-finish-infos?query=:query : search for the projectFinishInfo corresponding
     * to the query.
     *
     * @param query the query of the projectFinishInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-finish-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_FINISH)
    public ResponseEntity<List<ProjectFinishInfo>> searchProjectFinishInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectFinishInfos for query {}", query);
        Page<ProjectFinishInfo> page = projectFinishInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-finish-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
