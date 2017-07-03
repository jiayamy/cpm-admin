package com.wondertek.cpm.web.rest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.job.ProjectStateTask;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;

@RestController
@RequestMapping("/api")
public class ProjectStatJobTestResource extends ProjectStateTask{
	
	private final Logger log = LoggerFactory.getLogger(ProjectStatJobTestResource.class);
	@Inject
	private ProjectInfoRepository projectInfoRepository;
	
	@GetMapping("/projectStateJob/projectMonthlyStatTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String projectMonthlyState(
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: projectId : {}, date : {}" , projectId, date);
		if(date != null){
			generateProjectMonthlyState(projectId, DateUtil.parseDate("yyyyMMdd", date.toString()));
		}else{
			//每周一晚上22点开始跑定时任务
			generateProjectMonthlyState(projectId, new Date());
		}
		return "success";
	}
	@GetMapping("/projectStateJob/projectMonthlyStateAll")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String projectMonthlyStateAll(
			@RequestParam(value = "projectId", required = false) String projectId) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: projectId : {}" , projectId);
		
		List<Long> ids = StringUtil.stringToLongArray(projectId);
		List<ProjectInfo> projectInfos = projectInfoRepository.findAll(ids);
		if(projectInfos != null){
			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			for(ProjectInfo pi : projectInfos){
				pi.getCreateTime();
				cal.setTime(DateUtil.getFirstDayOfMonth(DateUtil.convertZonedDateTime(pi.getCreateTime())));
				
				while(true){
					cal.add(Calendar.MONTH, 1);//周一加7天，就是下一周的周一
					if(cal.getTime().before(now)){
						generateProjectMonthlyState(pi.getId(), cal.getTime());
					}else{
						break;
					}
				}
			}
		}
		return "success";
	}
	
	@GetMapping("/projectStateJob/projectWeeklyStatTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String projectWeeklyStatTest(
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: projectId : {}, date : {}" , projectId, date);
		if(date != null){
			generateProjectWeeklyState(projectId, DateUtil.parseDate("yyyyMMdd", date.toString()));
		}else{
			//每周一晚上22点开始跑定时任务
			generateProjectWeeklyState(projectId, new Date());
		}
		return "success";
	}
	
	@GetMapping("/projectStateJob/projectWeeklyStatAll")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String projectWeeklyStatAll(
			@RequestParam(value = "projectId", required = false) String projectId) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: projectId : {}" , projectId);
		
		List<Long> ids = StringUtil.stringToLongArray(projectId);
		List<ProjectInfo> projectInfos = projectInfoRepository.findAll(ids);
		if(projectInfos != null){
			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			for(ProjectInfo pi : projectInfos){
				pi.getCreateTime();
				cal.setTime(DateUtil.getMonday(DateUtil.convertZonedDateTime(pi.getCreateTime())));
				
				while(true){
					cal.add(Calendar.DAY_OF_YEAR, 7);//周一加7天，就是下一周的周一
					if(cal.getTime().before(now)){
						generateProjectWeeklyState(pi.getId(), cal.getTime());
					}else{
						break;
					}
				}
			}
		}
		return "success";
	}
}
