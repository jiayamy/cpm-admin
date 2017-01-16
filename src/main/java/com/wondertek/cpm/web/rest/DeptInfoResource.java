package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.vo.DeptTree;
import com.wondertek.cpm.service.DeptInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing DeptInfo.
 */
@RestController
@RequestMapping("/api")
public class DeptInfoResource {

    private final Logger log = LoggerFactory.getLogger(DeptInfoResource.class);
        
    @Inject
    private DeptInfoService deptInfoService;

    /**
     * POST  /dept-infos : Create a new deptInfo.
     *
     * @param deptInfo the deptInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deptInfo, or with status 400 (Bad Request) if the deptInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dept-infos")
    @Timed
    public ResponseEntity<DeptInfo> createDeptInfo(@Valid @RequestBody DeptInfo deptInfo) throws URISyntaxException {
        log.debug("REST request to save DeptInfo : {}", deptInfo);
        if (deptInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("deptInfo", "idexists", "A new deptInfo cannot already have an ID")).body(null);
        }
        DeptInfo result = deptInfoService.save(deptInfo);
        return ResponseEntity.created(new URI("/api/dept-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("deptInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dept-infos : Updates an existing deptInfo.
     *
     * @param deptInfo the deptInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deptInfo,
     * or with status 400 (Bad Request) if the deptInfo is not valid,
     * or with status 500 (Internal Server Error) if the deptInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dept-infos")
    @Timed
    public ResponseEntity<DeptInfo> updateDeptInfo(@Valid @RequestBody DeptInfo deptInfo) throws URISyntaxException {
        log.debug("REST request to update DeptInfo : {}", deptInfo);
        if (deptInfo.getId() == null) {
            return createDeptInfo(deptInfo);
        }
        DeptInfo result = deptInfoService.save(deptInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("deptInfo", deptInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dept-infos : get all the deptInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of deptInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/dept-infos")
    @Timed
    public ResponseEntity<List<DeptInfo>> getAllDeptInfos(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of DeptInfos");
        Page<DeptInfo> page = deptInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dept-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dept-infos/:id : get the "id" deptInfo.
     *
     * @param id the id of the deptInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deptInfo, or with status 404 (Not Found)
     */
    @GetMapping("/dept-infos/{id}")
    @Timed
    public ResponseEntity<DeptInfo> getDeptInfo(@PathVariable Long id) {
        log.debug("REST request to get DeptInfo : {}", id);
        DeptInfo deptInfo = deptInfoService.findOne(id);
        return Optional.ofNullable(deptInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dept-infos/:id : delete the "id" deptInfo.
     *
     * @param id the id of the deptInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dept-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteDeptInfo(@PathVariable Long id) {
        log.debug("REST request to delete DeptInfo : {}", id);
        deptInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deptInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/dept-infos?query=:query : search for the deptInfo corresponding
     * to the query.
     *
     * @param query the query of the deptInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/dept-infos")
    @Timed
    public ResponseEntity<List<DeptInfo>> searchDeptInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of DeptInfos for query {}", query);
        Page<DeptInfo> page = deptInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/dept-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/dept-infos/getDeptAndUserTree")
    @Timed
    public ResponseEntity<List<DeptTree>> getDeptAndUserTree(
    			@RequestParam(value = "selectType") Integer selectType,
    			@RequestParam(value = "showChild") Boolean showChild,
    			@RequestParam(value = "showUser") Boolean showUser
    		) throws URISyntaxException {
        log.debug("REST request to get a page of getDeptAndUserTree");
        List<DeptTree> list = deptInfoService.getDeptAndUserTree(selectType,showChild,showUser);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
}
