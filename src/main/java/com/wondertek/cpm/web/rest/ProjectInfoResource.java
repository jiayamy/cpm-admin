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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectInfo.
 */
@RestController
@RequestMapping("/api")
public class ProjectInfoResource {

    private final Logger log = LoggerFactory.getLogger(ProjectInfoResource.class);
        
    @Inject
    private ProjectInfoService projectInfoService;

    /**
     * POST  /project-infos : Create a new projectInfo.
     *
     * @param projectInfo the projectInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectInfo, or with status 400 (Bad Request) if the projectInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-infos")
    @Timed
    public ResponseEntity<ProjectInfo> createProjectInfo(@RequestBody ProjectInfo projectInfo) throws URISyntaxException {
        log.debug("REST request to save ProjectInfo : {}", projectInfo);
        if (projectInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectInfo", "idexists", "A new projectInfo cannot already have an ID")).body(null);
        }
        ProjectInfo result = projectInfoService.save(projectInfo);
        return ResponseEntity.created(new URI("/api/project-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-infos : Updates an existing projectInfo.
     *
     * @param projectInfo the projectInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectInfo,
     * or with status 400 (Bad Request) if the projectInfo is not valid,
     * or with status 500 (Internal Server Error) if the projectInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-infos")
    @Timed
    public ResponseEntity<ProjectInfo> updateProjectInfo(@RequestBody ProjectInfo projectInfo) throws URISyntaxException {
        log.debug("REST request to update ProjectInfo : {}", projectInfo);
        if (projectInfo.getId() == null) {
            return createProjectInfo(projectInfo);
        }
        ProjectInfo result = projectInfoService.save(projectInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectInfo", projectInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-infos : get all the projectInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-infos")
    @Timed
    public ResponseEntity<List<ProjectInfoVo>> getAllProjectInfos(
    		@RequestParam(value = "contractId") String contractId, 
    		@RequestParam(value = "serialNum") String serialNum, 
    		@RequestParam(value = "name") String name, 
    		@RequestParam(value = "status") String status, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectInfos");
//        Page<ProjectInfo> page = projectInfoService.findAll(pageable);
        
        ProjectInfo projectInfo = new ProjectInfo();
        if(!StringUtil.isNullStr(contractId)){
        	projectInfo.setContractId(StringUtil.nullToLong(contractId));
        }
        if(!StringUtil.isNullStr(serialNum)){
        	projectInfo.setSerialNum(serialNum);
        }
        if(!StringUtil.isNullStr(name)){
        	projectInfo.setName(name);
        }
        if(!StringUtil.isNullStr(status)){
        	projectInfo.setStatus(StringUtil.nullToInteger(status));
        }
        
        Page<ProjectInfoVo> page = projectInfoService.getUserPage(projectInfo, pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-infos/:id : get the "id" projectInfo.
     *
     * @param id the id of the projectInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectInfo, or with status 404 (Not Found)
     */
    @GetMapping("/project-infos/{id}")
    @Timed
    public ResponseEntity<ProjectInfo> getProjectInfo(@PathVariable Long id) {
        log.debug("REST request to get ProjectInfo : {}", id);
        ProjectInfo projectInfo = projectInfoService.findOne(id);
        return Optional.ofNullable(projectInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-infos/:id : delete the "id" projectInfo.
     *
     * @param id the id of the projectInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectInfo(@PathVariable Long id) {
        log.debug("REST request to delete ProjectInfo : {}", id);
        projectInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-infos?query=:query : search for the projectInfo corresponding
     * to the query.
     *
     * @param query the query of the projectInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-infos")
    @Timed
    public ResponseEntity<List<ProjectInfo>> searchProjectInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectInfos for query {}", query);
        Page<ProjectInfo> page = projectInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/project-infos/queryUserContract")
    @Timed
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException {
        log.debug("REST request to queryUserContract");
        List<LongValue> list = projectInfoService.queryUserContract();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    

}
