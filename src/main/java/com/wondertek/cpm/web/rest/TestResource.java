package com.wondertek.cpm.web.rest;

import java.util.Date;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.job.AccountScheduledJob;
import com.wondertek.cpm.security.AuthoritiesConstants;

@RestController
@RequestMapping("/api")
public class TestResource extends AccountScheduledJob{
	
	@GetMapping("/test/")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String generateProjectWeeklyState(@RequestParam(value="date", required = false) Long date) {
		if(date == null){
			//每周一晚上22点开始跑定时任务
			accountScheduled(new Date());
		}else{
			accountScheduled(DateUtil.parseDate("yyyyMMdd", date.toString()));
		}
		return "success";
	}
}
