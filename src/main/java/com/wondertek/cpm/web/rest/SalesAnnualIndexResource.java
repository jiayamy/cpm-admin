package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.SalesAnnualIndex;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.SalesAnnualIndexService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing SalesAnnualIndex.
 */
@RestController
@RequestMapping("/api")
public class SalesAnnualIndexResource {

    private final Logger log = LoggerFactory.getLogger(SalesAnnualIndexResource.class);
    @Inject
    private SalesAnnualIndexService salesAnnualIndexService;
    /**
     * 列表页
     */
    @GetMapping("/sales-annualIndex")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<SalesAnnualIndex>> getUserPage(
    		@RequestParam(value = "statYear",required=false) Long statYear, //年份
    		@RequestParam(value = "userId",required=false) Long userId, 	//用户
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  statYear:{},userId:{}",statYear,userId);
        SalesAnnualIndex salesAnnualIndex = new SalesAnnualIndex();
        salesAnnualIndex.setUserId(userId);
        salesAnnualIndex.setStatYear(statYear);
        
        Page<SalesAnnualIndex> page = salesAnnualIndexService.getUserPage(salesAnnualIndex,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales-annualIndex");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
