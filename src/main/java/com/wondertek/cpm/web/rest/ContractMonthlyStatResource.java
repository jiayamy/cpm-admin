package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ContractMonthlyStatService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractMonthlyStat.
 */
@RestController
@RequestMapping("/api")
public class ContractMonthlyStatResource {
	
	private final Logger log = LoggerFactory.getLogger(ContractMonthlyStatResource.class);
	
	@Inject
	private ContractMonthlyStatService contractMonthlyStatService;
	
	/**
     * GET  /contract-monthly-stats : get all the ContractMonthlyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ContractMonthlyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-monthly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractMonthlyStatVo>> getAllContractMonthlyStats(
    		@ApiParam(value="contractId") @RequestParam(value="contractId") String contractId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ContractMonthlyStats");
        Page<ContractMonthlyStatVo> page = contractMonthlyStatService.getStatPage(contractId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /contract-monthly-stats/:id : get the "id" ContractMonthlyStat.
     *
     * @param id the id of the ContractMonthlyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ContractMonthlyStat, or with status 404 (Not Found)
     */
    @GetMapping("/contract-monthly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ContractMonthlyStatVo> getContractMonthlyStat(@PathVariable Long id) {
        log.debug("REST request to get ContractMonthlyStats : {}", id);
        ContractMonthlyStatVo contractMonthlyStat = contractMonthlyStatService.findOne(id);
        return Optional.ofNullable(contractMonthlyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    
    /**
     * SEARCH  /_search/contract-monthly-stats?query=:query : search for the contractMonthlyStats corresponding
     * to the query.
     *
     * @param query the query of the contractMonthlyStats search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-monthly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractMonthlyStat>> searchContractMonthlyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractMonthlyStat for query {}", query);
        Page<ContractMonthlyStat> page = contractMonthlyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/contract-monthly-stats/queryUserContract")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
	public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException {
	    log.debug("REST request to queryUserProject");
	    List<LongValue> list = contractMonthlyStatService.queryUserContract();
	    return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
    
    @GetMapping("/contract-monthly-stats/queryChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ChartReportVo getChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id") Long statId){
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ContractMonthlyStatVo contractMonthlyStatvo = contractMonthlyStatService.findOne(statId);
    	Long contractId = contractMonthlyStatvo.getContractId();
    	chartReportVo.setTitle(contractMonthlyStatvo.getSerialNum());
    	if(StringUtil.isNullStr(toDate)){
    		toDate = contractMonthlyStatvo.getStatWeek().toString();
    	}
    	Date lMonth = DateUtil.parseyyyyMM("yyyy-MM", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMM", DateUtil.addMonthNum(-6, lMonth));
    	}
    	Date fMonth = DateUtil.parseyyyyMM("yyyy-MM", fromDate);
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fMonth);
    	cal2.setTime(lMonth);
    	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
    	int count = 0;
    	count += 12*yearCount;
    	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	List<String> category = new ArrayList<String>();
    	for(int i = 0; i <= count; i++){
    		category.add(DateUtil.formatDate("yyyy-MM", cal1.getTime()));
    		cal1.add(Calendar.MONTH, 1);
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = contractMonthlyStatService.getChartData(fMonth, lMonth, contractId);
    	chartReportVo.setSeries(datas);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"合同回款总额","所有成本","合同毛利","销售人工成本","销售报销成本","咨询人工成本","咨询报销成本","硬件采购成本","外部软件采购成本","内容软件采购成本","项目人工成本","项目报销成本"}));
    	chartReportVo.setLegend(legend);
    	return chartReportVo;
    }
}
