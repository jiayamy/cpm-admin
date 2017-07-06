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
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.job.ContractStateTask;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;

@RestController
@RequestMapping("/api")
public class ContractStatJobTestResource extends ContractStateTask{
	
	private final Logger log = LoggerFactory.getLogger(ContractStatJobTestResource.class);
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@GetMapping("/contractStatJob/contractMonthlyStatTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String contractMonthlyStatTest(
			@RequestParam(value = "contractId", required = false) Long contractId,
			@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: date : {}" , date);
		if(date != null){
			generateContractMonthlyStat(contractId, DateUtil.parseDate("yyyyMMdd", date.toString()));
		}else{
			//每周一晚上22点开始跑定时任务
			generateContractMonthlyStat(contractId, new Date());
		}
		return "success";
	}
	/**
	 * 按合同来重跑合同的所有月统计
	 */
	@GetMapping("/contractStatJob/contractMonthlyAllStatTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String contractMonthlyAllStatTest(
			@RequestParam(value = "contractId", required = false) String contractId) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource:contractId:{}" ,contractId);
		
		List<Long> ids = StringUtil.stringToLongArray(contractId);
		List<ContractInfo> contractInfos = contractInfoRepository.findAll(ids);
		if(contractInfos != null){
			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			for(ContractInfo ci : contractInfos){
				cal.setTime(DateUtil.getFirstDayOfMonth(DateUtil.convertZonedDateTime(ci.getCreateTime())));
				
				while(true){
					cal.add(Calendar.MONTH, 1);//周一加7天，就是下一周的周一
					if(cal.getTime().before(now)){
						generateContractMonthlyStat(ci.getId(), cal.getTime());
					}else{
						break;
					}
				}
			}
		}
		return "success";
	}
	
	@GetMapping("/contractStatJob/contractWeeklyStatTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String contractWeeklyStatTest(
			@RequestParam(value = "contractId", required = false) Long contractId,
			@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: date : {}" , date);
		if(date != null){
			generateContractWeeklyStat(contractId, DateUtil.parseDate("yyyyMMdd", date.toString()));
		}else{
			//每周一晚上22点开始跑定时任务
			generateContractWeeklyStat(contractId, new Date());
		}
		return "success";
	}
	@GetMapping("/contractStatJob/contractWeeklyStatAllTest")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String contractWeeklyStatAllTest(
			@RequestParam(value = "contractId", required = false) String contractId) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource:contractId:{}" ,contractId);
		
		List<Long> ids = StringUtil.stringToLongArray(contractId);
		List<ContractInfo> contractInfos = contractInfoRepository.findAll(ids);
		if(contractInfos != null){
			Date now = new Date();
			Calendar cal = Calendar.getInstance();
			for(ContractInfo ci : contractInfos){
				cal.setTime(DateUtil.getMonday(DateUtil.convertZonedDateTime(ci.getCreateTime())));
				
				while(true){
					cal.add(Calendar.DAY_OF_YEAR, 7);//周一加7天，就是下一周的周一
					if(cal.getTime().before(now)){
						generateContractWeeklyStat(ci.getId(), cal.getTime());
					}else{
						break;
					}
				}
			}
		}
		return "success";
	}
	@GetMapping("/contractStatJob/saleContractWeeklyStat")
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public @ResponseBody String saleContractWeeklyStat(
			@RequestParam(value = "contractId", required = false) Long contractId,
			@RequestParam(value="date", required = false) Long date) {
		log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to Test Resource: date : {}" , date);
		if(date != null){
			generateSaleContractWeeklyStat(contractId, DateUtil.parseDate("yyyyMMdd", date.toString()));
		}else{
			//每周一晚上22点开始跑定时任务
			generateSaleContractWeeklyStat(contractId, new Date());
		}
		return "success";
	}
}
