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
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.DeptTypeService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing DeptType.
 */
@RestController
@RequestMapping("/api")
public class DeptTypeResource {

    private final Logger log = LoggerFactory.getLogger(DeptTypeResource.class);
        
    @Inject
    private DeptTypeService deptTypeService;

    /**
     * POST  /dept-types : Create a new deptType.
     *
     * @param deptType the deptType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deptType, or with status 400 (Bad Request) if the deptType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dept-types")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<DeptType> createDeptType(@Valid @RequestBody DeptType deptType) throws URISyntaxException {
        log.debug("REST request to save DeptType : {}", deptType);
        if (deptType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("deptType", "idexists", "A new deptType cannot already have an ID")).body(null);
        }
        DeptType result = deptTypeService.save(deptType);
        return ResponseEntity.created(new URI("/api/dept-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("deptType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dept-types : Updates an existing deptType.
     *
     * @param deptType the deptType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deptType,
     * or with status 400 (Bad Request) if the deptType is not valid,
     * or with status 500 (Internal Server Error) if the deptType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dept-types")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<DeptType> updateDeptType(@Valid @RequestBody DeptType deptType) throws URISyntaxException {
        log.debug("REST request to update DeptType : {}", deptType);
        if (deptType.getId() == null) {
            return createDeptType(deptType);
        }
        DeptType result = deptTypeService.save(deptType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("deptType", deptType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dept-types : get all the deptTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of deptTypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/dept-types")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<DeptType>> getAllDeptTypes(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of DeptTypes");
        Page<DeptType> page = deptTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dept-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dept-types/:id : get the "id" deptType.
     *
     * @param id the id of the deptType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deptType, or with status 404 (Not Found)
     */
    @GetMapping("/dept-types/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<DeptType> getDeptType(@PathVariable Long id) {
        log.debug("REST request to get DeptType : {}", id);
        DeptType deptType = deptTypeService.findOne(id);
        return Optional.ofNullable(deptType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dept-types/:id : delete the "id" deptType.
     *
     * @param id the id of the deptType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dept-types/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteDeptType(@PathVariable Long id) {
        log.debug("REST request to delete DeptType : {}", id);
        deptTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deptType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/dept-types?query=:query : search for the deptType corresponding
     * to the query.
     *
     * @param query the query of the deptType search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/dept-types")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<DeptType>> searchDeptTypes(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of DeptTypes for query {}", query);
        Page<DeptType> page = deptTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/dept-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * 获取所有的部门类型列表
     */
    @GetMapping("/dept-types/forCombox")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> getAllForCombox(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of getAllForCombox");
        List<LongValue> list = deptTypeService.getAllForCombox();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }

}
