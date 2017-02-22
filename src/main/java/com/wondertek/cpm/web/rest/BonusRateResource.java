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
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.vo.BonusRateVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.BonusRateService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing BonusRate.
 */
@RestController
@RequestMapping("/api")
public class BonusRateResource {

    private final Logger log = LoggerFactory.getLogger(BonusRateResource.class);
    @Inject
    private BonusRateService bonusRateService;
    /**
     * 列表页
     */
    @GetMapping("/bonus-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<BonusRateVo>> getUserPage(
    		@RequestParam(value = "contractType",required=false) Integer contractType, //合同类型
    		@RequestParam(value = "deptType",required=false) Long deptType, 	//部门类型
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  contractType:{},deptType:{}",contractType,deptType);
        BonusRate bonusRate = new BonusRate();
        bonusRate.setContractType(contractType);
        bonusRate.setDeptType(deptType);
        
        Page<BonusRateVo> page = bonusRateService.getUserPage(bonusRate,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bonus-rate");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
