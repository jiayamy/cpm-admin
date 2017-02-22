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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.service.ExternalQuotationService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ExternalQuotation.
 */
@RestController
@RequestMapping("/api")
public class ExternalQuotationResource {

    private final Logger log = LoggerFactory.getLogger(ExternalQuotationResource.class);
    @Inject
    private ExternalQuotationService externalQuotationService;
    /**
     * 列表页
     */
    @GetMapping("/external-quotation")
    @Timed
    public ResponseEntity<List<ExternalQuotation>> getUserPage(
    		@RequestParam(value = "grade",required=false) Integer grade, //级别
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  grade:{}",grade);
        ExternalQuotation externalQuotation = new ExternalQuotation();
        externalQuotation.setGrade(grade);
        
        Page<ExternalQuotation> page = externalQuotationService.getUserPage(externalQuotation,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/external-quotation");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
