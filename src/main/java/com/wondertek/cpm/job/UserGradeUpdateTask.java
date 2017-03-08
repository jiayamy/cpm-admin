package com.wondertek.cpm.job;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.service.ExternalQuotationService;
import com.wondertek.cpm.service.UserCostService;
import com.wondertek.cpm.service.UserService;

@Component
@EnableScheduling
public class UserGradeUpdateTask {
	
	private Logger log = LoggerFactory.getLogger(UserGradeUpdateTask.class);
	@Inject
    private UserCostService userCostService;
	@Inject
    private UserService userService;
    @Inject
    private ExternalQuotationService externalQuotationService;
	/**
	 * 每月更新一下用户的等级
	 */
	@Scheduled(cron = "0 0 1 1 * ?")
	protected void updateUserGrade(){
		log.info("=====begin updateUserGrade=====");
		//获取所有记录
		List<ExternalQuotation> externalQuotations = externalQuotationService.getAllInfoOrderByGradeAsc();
		
		List<Object[]> userCosts = userCostService.findAllMaxByCostMonth(StringUtil.nullToLong(DateUtil.formatDate(CpmConstants.DEFAULT_USER_COST_COSTMONTH_FROMAT, new Date())));
		if(userCosts != null){
			Long userId = null;
			Double sal = null;
			for(Object[] o : userCosts){
				userId = StringUtil.nullToLong(o[0]);
				sal = StringUtil.nullToDouble(o[1]);

				userService.updateUser(userId, getUserGrade(externalQuotations,sal));
			}
		}
		log.info("=====end updateUserGrade=====");
	}

	private Integer getUserGrade(List<ExternalQuotation> externalQuotations, Double sal) {
		int grade = 1;
    	if(externalQuotations != null && sal != null){
    		for(ExternalQuotation externalQuotation : externalQuotations){
    			grade = externalQuotation.getGrade();
    			if(externalQuotation.getExternalQuotation().doubleValue() >= sal){
    				break;
    			}
    		}
    	}
		return grade;
	}
	
}
