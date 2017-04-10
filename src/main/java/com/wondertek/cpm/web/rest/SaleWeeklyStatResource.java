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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.vo.SaleWeeklyStatVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.SaleWeeklyStatService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing SaleWeeklyStat.
 */
@RestController
@RequestMapping("/api")
public class SaleWeeklyStatResource {
	
	private final Logger log = LoggerFactory.getLogger(SaleWeeklyStatResource.class);
	
	@Inject
	private SaleWeeklyStatService saleWeeklyStatService;
	
	/**
     * GET  /sale-weekly-stats : get all the saleWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of saleWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@GetMapping("/sale-weekly-stats")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
	public ResponseEntity<List<SaleWeeklyStatVo>> getAllSaleWeeklyStats(
				@ApiParam(value = "deptId") @RequestParam(value = "deptId",required = false) String deptId,
				@ApiParam Pageable pageable) throws URISyntaxException{
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of SaleWeeklyStats by deptId : {}", deptId);
		Page<SaleWeeklyStatVo> page = saleWeeklyStatService.getStatPage(deptId, pageable);
        for (SaleWeeklyStatVo saleWeeklyStatVo : page.getContent()) {
			saleWeeklyStatVo.setReceiveTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getReceiveTotal(), 10000d, 2));
			saleWeeklyStatVo.setCostTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getCostTotal(), 10000d, 2));
			saleWeeklyStatVo.setHardwarePurchase(StringUtil.getScaleDouble(saleWeeklyStatVo.getHardwarePurchase(), 10000d, 2));
		}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sale-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

}
