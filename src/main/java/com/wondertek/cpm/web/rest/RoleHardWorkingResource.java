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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.RoleHardWorking;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.RoleHardWorkingService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api")
public class RoleHardWorkingResource {
	private final Logger log = LoggerFactory.getLogger(RoleHardWorkingResource.class);
	
	@Inject
    private RoleHardWorkingService roleHardWorkingService;
	
	/**
	 * 列表页
	 * @author zj
	 * @Description :
	 */
    @GetMapping("/role-hardworking")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_WORKHARDING)
    public ResponseEntity<List<RoleHardWorking>> getAllRoleHardWorkingByParams(
    		@RequestParam(value = "originMonth",required=false) Long originMonth,
    		@RequestParam(value = "userId",required=false) Long userId,
    		@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of RoleHardWorking  originMonth:{},userId:{}",originMonth,userId);
		RoleHardWorking roleHardWorking = new RoleHardWorking();
		if(originMonth != null){
			roleHardWorking.setOriginMonth(originMonth);
		}else{
			Date now = new Date();
			String lMonth = DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now));
			roleHardWorking.setOriginMonth(StringUtil.stringToLong(lMonth));
		}
		if(userId != null){
			roleHardWorking.setUserId(userId);
		}
		Page<RoleHardWorking> page = roleHardWorkingService.searchPage(roleHardWorking,pageable);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/role-hardworking");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK); 
    }
    
    @GetMapping("/role-hardworking/queryChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_WORKHARDING)
    public ResponseEntity<ChartReportVo> getChartReport(@ApiParam(value="beginningMonth") @RequestParam(value="beginningMonth") String beginningMonth,
    		@ApiParam(value="id") @RequestParam(value="id") Long statId){
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get Chart Report of RoleHardWorking by beginningMonth : {}, statId : {}", beginningMonth,statId);
    	ChartReportVo chartReportVo = new ChartReportVo();
    	RoleHardWorking roleHardWorking = roleHardWorkingService.findOne(statId);
    	if(roleHardWorking == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	Long userId = roleHardWorking.getUserId();
    	chartReportVo.setTitle(roleHardWorking.getSerialNum()+" "+roleHardWorking.getRoleName());
    	
    	if(StringUtil.isNullStr(beginningMonth)){
    		beginningMonth = roleHardWorking.getOriginMonth().toString();
    	}
    	Date lMonth = DateUtil.parseyyyyMM("yyyy-MM", beginningMonth);
    	
    	
    	String	fromDate = DateUtil.formatDate("yyyyMM", DateUtil.addMonthNum(-5, lMonth));
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
    	List<ChartReportDataVo> datas = roleHardWorkingService.getChartData(fMonth, lMonth, userId);
    	if(datas == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	chartReportVo.setSeries(datas);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"勤奋度"}));
    	chartReportVo.setLegend(legend);
    	return Optional.ofNullable(chartReportVo).map(result -> new ResponseEntity<>(result,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
