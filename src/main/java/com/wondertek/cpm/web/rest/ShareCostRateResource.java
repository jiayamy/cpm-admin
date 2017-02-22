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
import com.wondertek.cpm.domain.ShareCostRate;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ShareCostRateService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ShareCostRate.
 */
@RestController
@RequestMapping("/api")
public class ShareCostRateResource {

    private final Logger log = LoggerFactory.getLogger(ShareCostRateResource.class);
    @Inject
    private ShareCostRateService shareCostRateService;
    /**
     * 列表页
     */
    @GetMapping("/share-cost-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ShareCostRate>> getUserPage(
    		@RequestParam(value = "contractType",required=false) Integer contractType, //合同类型
    		@RequestParam(value = "deptType",required=false) Long deptType, 	//部门类型
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  contractType:{},deptType:{}",contractType,deptType);
        ShareCostRate shareCostRate = new ShareCostRate();
        shareCostRate.setContractType(contractType);
        shareCostRate.setDeptType(deptType);
        
        Page<ShareCostRate> page = shareCostRateService.getUserPage(shareCostRate,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/share-cost-rate");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
