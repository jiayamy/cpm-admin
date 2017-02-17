package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectOverallService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing ProductPrice.
 */
@RestController
@RequestMapping("/api")
public class ProjectOverallResource {

    private final Logger log = LoggerFactory.getLogger(ProjectOverallResource.class);
        
    @Inject
    private ProjectOverallService projectOverallService;
    
    @GetMapping("/project-overall-controller")
    @Timed
    public ResponseEntity<List<ProjectOverallVo>> getAllProjectOverallByParams(
    		@RequestParam(value = "fromDate",required=false) String fromDate,
    		@RequestParam(value = "toDate",required=false) String toDate,
    		@RequestParam(value = "contractId",required=false) String contractId,
    		@RequestParam(value = "userId",required=false) String userId,
    		@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug("REST request to get a page of ProjectOverallVo");
		if (!StringUtil.isNullStr(fromDate)) {
			fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", fromDate))[6];
		}
		if (!StringUtil.isNullStr(toDate)) {
			toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", toDate))[6];
		}
		Page<ProjectOverallVo> page = projectOverallService.searchPage(fromDate,toDate,contractId,userId,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(fromDate, page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);    	
    }

    @GetMapping("/project-overall-controller/queryDetail")
    @Timed
    public ResponseEntity<List<ProjectOverallVo>> getProjectOverallDetail(
    		@RequestParam(value = "contractId",required=false) String contractId,
    		@ApiParam Pageable pageable) 
    	throws URISyntaxException {
        log.debug("REST request to get ProductPrice : {}", contractId);
        Page<ProjectOverallVo> page = projectOverallService.searchPageDetail(contractId,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(contractId, page,"/api/project-projectOverall");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK); 
    }
    
    @GetMapping("/project-overall-controller/{id}")
    @Timed
    public ResponseEntity<ProjectOverall> getProjectOverall(@PathVariable Long id) {
        log.debug("REST request to get ProductPrice : {}", id);
        ProjectOverall projectOverall = projectOverallService.findOne(id);
        return Optional.ofNullable(projectOverall)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
