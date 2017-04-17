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
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
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
        	saleWeeklyStatVo.setAnnualIndex(StringUtil.getScaleDouble(saleWeeklyStatVo.getAnnualIndex(), 10000d,2));
			saleWeeklyStatVo.setReceiveTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getReceiveTotal(), 10000d, 2));
			saleWeeklyStatVo.setCostTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getCostTotal(), 10000d, 2));
			saleWeeklyStatVo.setHardwarePurchase(StringUtil.getScaleDouble(saleWeeklyStatVo.getHardwarePurchase(), 10000d, 2));
		}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sale-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@GetMapping("/sale-weekly-stats/{id}")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
	public ResponseEntity<SaleWeeklyStatVo> getSaleWeeklyStat(@PathVariable Long id){
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a detail of SaleWeeklyStats by id : {}", id);
		SaleWeeklyStatVo saleWeeklyStatVo = saleWeeklyStatService.findOne(id);
		if(saleWeeklyStatVo != null){
			saleWeeklyStatVo.setAnnualIndex(StringUtil.getScaleDouble(saleWeeklyStatVo.getAnnualIndex(), 10000d, 2));
			saleWeeklyStatVo.setFinishTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getFinishTotal(), 10000d, 2));
			saleWeeklyStatVo.setReceiveTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getReceiveTotal(), 10000d, 2));
			saleWeeklyStatVo.setCostTotal(StringUtil.getScaleDouble(saleWeeklyStatVo.getCostTotal(), 10000d, 2));
			saleWeeklyStatVo.setSalesHumanCost(StringUtil.getScaleDouble(saleWeeklyStatVo.getSalesHumanCost(), 10000d, 2));
			saleWeeklyStatVo.setSalesPayment(StringUtil.getScaleDouble(saleWeeklyStatVo.getSalesPayment(), 10000d, 2));
			saleWeeklyStatVo.setConsultHumanCost(StringUtil.getScaleDouble(saleWeeklyStatVo.getConsultHumanCost(), 10000d, 2));
			saleWeeklyStatVo.setConsultPayment(StringUtil.getScaleDouble(saleWeeklyStatVo.getConsultPayment(), 10000d, 2));
			saleWeeklyStatVo.setHardwarePurchase(StringUtil.getScaleDouble(saleWeeklyStatVo.getHardwarePurchase(), 10000d, 2));
			saleWeeklyStatVo.setExternalSoftware(StringUtil.getScaleDouble(saleWeeklyStatVo.getExternalSoftware(), 10000d, 2));
			saleWeeklyStatVo.setInternalSoftware(StringUtil.getScaleDouble(saleWeeklyStatVo.getInternalSoftware(), 10000d, 2));
			saleWeeklyStatVo.setProjectHumanCost(StringUtil.getScaleDouble(saleWeeklyStatVo.getProjectHumanCost(), 10000d, 2));
			saleWeeklyStatVo.setProjectPayment(StringUtil.getScaleDouble(saleWeeklyStatVo.getProjectPayment(), 10000d, 2));
		}
		return Optional.ofNullable(saleWeeklyStatVo)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/sale-weekly-stats/queryChart")
	@Timed
	@Secured(AuthoritiesConstants.ROLE_STAT_CONTRACT)
	public ResponseEntity<ChartReportVo> queryChart(
			@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id") Long id){
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a chart of SaleWeeklyStats by fromDate: {}, "
    			+ "toDate : {}, id : {}", fromDate, toDate, id);
		ChartReportVo chartReportVo = new ChartReportVo();
		SaleWeeklyStatVo saleWeeklyStatVo = saleWeeklyStatService.findOne(id);
		if(saleWeeklyStatVo == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Long deptId = saleWeeklyStatVo.getDeptId();
		//设置title
		chartReportVo.setTitle(saleWeeklyStatVo.getDept());
		//设置legend
		List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"合同年指标","合同累计完成金额","当年收款金额","当年新增所有成本",
				"当年销售人工成本","当年销售报销成本","当年咨询报销成本","当年咨询人工成本","当年硬件成本","当年外部软件成本","当年内部软件成本","当年项目人工成本","当年项目报销成本"}));
		chartReportVo.setLegend(legend);
		//设置category-横坐标(日期)
		if(StringUtil.isNullStr(toDate)){
			toDate = saleWeeklyStatVo.getStatWeek().toString();
		}else{
			toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, toDate))[6];
		}
		Date lDay = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, toDate);
		if(StringUtil.isNullStr(fromDate)){
			fromDate = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.addDayNum(-6*7, lDay));
		}else{
			fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, fromDate))[6];
		}
		Date fDay = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, fromDate);
		Long sevenDay = 7*24*60*60*1000L;
    	Long temp = fDay.getTime();
    	List<String> category = new ArrayList<String>();
    	while(temp <= lDay.getTime()){
    		category.add(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, new Date(temp)));
    		temp += sevenDay;
    	}
    	chartReportVo.setCategory(category);
    	//设置series-纵坐标
    	List<ChartReportDataVo> datas = saleWeeklyStatService.getChartDate(fDay,lDay,deptId);
    	if(datas == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	chartReportVo.setSeries(datas);
		return Optional.ofNullable(chartReportVo)
				.map(result -> new ResponseEntity<>(result,HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
