package com.wondertek.cpm.web.rest;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.job.ContractStateTask;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;

@RestController
@RequestMapping("/api")
public class ContractStatJobTestResource extends ContractStateTask{
	
	private final Logger log = LoggerFactory.getLogger(ContractStatJobTestResource.class);
	
	@GetMapping("/ContractMonthlyStatTest/")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String ContractMonthlyStatTest(@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: date : {}" , date);
		if(date == null){
			//每周一晚上22点开始跑定时任务
			generateContractMonthlyStat(new Date());
		}else{
			generateContractMonthlyStat(DateUtil.parseDate("yyyyMMdd", date.toString()));
		}
		return "success";
	}
	
	@GetMapping("/ContractWeeklyStatTest/")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String ContractWeeklyStatTest(@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: date : {}" , date);
		if(date == null){
			//每周一晚上22点开始跑定时任务
			generateContractWeeklyStat(new Date());
		}else{
			generateContractWeeklyStat(DateUtil.parseDate("yyyyMMdd", date.toString()));
		}
		return "success";
	}
}
