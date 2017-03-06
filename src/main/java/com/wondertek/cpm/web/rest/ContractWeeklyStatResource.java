package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.domain.vo.ContractWeeklyStatVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractWeeklyStatService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractWeeklyStat.
 */
@RestController
@RequestMapping("/api")
public class ContractWeeklyStatResource {

    private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatResource.class);
        
    @Inject
    private ContractWeeklyStatService contractWeeklyStatService;

    /**
     * GET  /contract-weekly-stats : get all the contractWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractWeeklyStatVo>> getAllContractWeeklyStats(
    		@ApiParam(value="contractId") @RequestParam(value="contractId") String contractId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractWeeklyStats by contractId : {}", contractId);
        Page<ContractWeeklyStatVo> page = contractWeeklyStatService.getStatPage(contractId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contract-weekly-stats/:id : get the "id" contractWeeklyStat.
     *
     * @param id the id of the contractWeeklyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractWeeklyStat, or with status 404 (Not Found)
     */
    @GetMapping("/contract-weekly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ContractWeeklyStatVo> getContractWeeklyStat(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractWeeklyStat : {}", id);
        ContractWeeklyStatVo contractWeeklyStat = contractWeeklyStatService.findOne(id);
        return Optional.ofNullable(contractWeeklyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

   /**
     * SEARCH  /_search/contract-weekly-stats?query=:query : search for the contractWeeklyStat corresponding
     * to the query.
     *
     * @param query the query of the contractWeeklyStat search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<List<ContractWeeklyStat>> searchContractWeeklyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search for a page of ContractWeeklyStats for query {}", query);
        Page<ContractWeeklyStat> page = contractWeeklyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/contract-weekly-stats/queryChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ChartReportVo> getChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id") Long statId){
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get Chart Report of ContractWeeklyStats by fromDate: {}, "
    			+ "toDate : {}, statId : {}", fromDate, toDate, statId);
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ContractWeeklyStatVo contractWeeklyStatvo = contractWeeklyStatService.findOne(statId);
    	if(contractWeeklyStatvo == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	Long contractId = contractWeeklyStatvo.getContractId();
    	chartReportVo.setTitle(contractWeeklyStatvo.getSerialNum());
    	if(StringUtil.isNullStr(toDate)){
    		toDate = contractWeeklyStatvo.getStatWeek().toString();
    	}else{
    		toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", toDate))[6];
    	}
    	Date lDay = DateUtil.parseDate("yyyyMMdd", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMMdd", DateUtil.addDayNum(-6*7, lDay));
    	}else{
    		fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", fromDate))[6];
    	}
    	Date fDay = DateUtil.parseDate("yyyyMMdd", fromDate);
    	Long sevenDay = 7*24*60*60*1000L;
    	Long temp = fDay.getTime();
    	List<String> category = new ArrayList<String>();
    	while(temp <= lDay.getTime()){
    		category.add(DateUtil.formatDate("yyyy-MM-dd", new Date(temp)));
    		temp += sevenDay;
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = contractWeeklyStatService.getChartData(fDay, lDay, contractId);
    	if(datas == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	chartReportVo.setSeries(datas);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"合同回款总额","所有成本","合同毛利","销售人工成本","销售报销成本","咨询人工成本","咨询报销成本","硬件采购成本","外部软件采购成本","内部软件采购成本","项目人工成本","项目报销成本"}));
    	chartReportVo.setLegend(legend);
    	return Optional.ofNullable(chartReportVo).map(result -> new ResponseEntity<>(result,HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/contract-weekly-stats/queryFinishRateChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
    public ResponseEntity<ChartReportVo>getFinishRateChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id") Long statId){
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get FinishRate Chart Report of ContractWeeklyStats by fromDate: {}, "
    			+ "toDate : {}, statId : {}", fromDate, toDate, statId);
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ContractWeeklyStatVo contractWeeklyStatvo = contractWeeklyStatService.findOne(statId);
    	if(contractWeeklyStatvo == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	Long contractId = contractWeeklyStatvo.getContractId();
    	chartReportVo.setTitle(contractWeeklyStatvo.getSerialNum()+"-完成率");
    	if(StringUtil.isNullStr(toDate)){
    		toDate = contractWeeklyStatvo.getStatWeek().toString();
    	}else{
    		toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", toDate))[6];
    	}
    	Date lDay = DateUtil.parseDate("yyyyMMdd", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMMdd", DateUtil.addDayNum(-6*7, lDay));
    	}else{
    		fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", fromDate))[6];
    	}
    	Date fDay = DateUtil.parseDate("yyyyMMdd", fromDate);
    	Long sevenDay = 7*24*60*60*1000L;
    	Long temp = fDay.getTime();
    	List<String> category = new ArrayList<String>();
    	while(temp <= lDay.getTime()){
    		category.add(DateUtil.formatDate("yyyy-MM-dd", new Date(temp)));
    		temp += sevenDay;
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = contractWeeklyStatService.getFinishRateData(fDay, lDay, contractId);
    	if(datas == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	chartReportVo.setSeries(datas);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"完成率"}));
    	chartReportVo.setLegend(legend);
    	return Optional.ofNullable(chartReportVo).map(result -> new ResponseEntity<>(result,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
