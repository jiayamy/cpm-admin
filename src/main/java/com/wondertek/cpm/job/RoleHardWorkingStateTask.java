package com.wondertek.cpm.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.RoleHardWorking;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.UserTimesheetVo;
import com.wondertek.cpm.repository.RoleHardWorkingRepository;
import com.wondertek.cpm.service.RoleHardWorkingService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.service.UserTimesheetService;

@Component
@EnableScheduling
public class RoleHardWorkingStateTask {
	private Logger log = LoggerFactory.getLogger(RoleHardWorkingStateTask.class);

	@Inject
	private RoleHardWorkingService roleHardWorkingService;
	
	@Inject
	private RoleHardWorkingRepository roleHardWorkingRepository;
	
	@Inject
	private UserTimesheetService userTimesheetService;
	
	@Inject
    private UserService userService; 
	
	

	@Scheduled(cron = "0 0 0 1 * ?")
	protected void generateProjectMonthlyState() {
		Date now = new Date();
		generateProjectMonthlyState(now);
	}

	protected void generateProjectMonthlyState(Date now) {
		log.info("=====begin generate role hardworking state=====");
		String fDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthBegin(now));
		String lDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthend(now));
		String lMonth = DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now));
		// 初始上月的stat
		List<RoleHardWorking> roleHardWorkings = roleHardWorkingRepository.findByOriginMonth(StringUtil.nullToLong("201703"));
		if (roleHardWorkings != null && roleHardWorkings.size() > 0) {
			for (RoleHardWorking roleHardWorking : roleHardWorkings) {
				roleHardWorkingRepository.delete(roleHardWorking);
			}
		}
		//从工时表中获取这个月的所有统计记录
		Map<Long,UserTimesheetVo> map = new HashMap<Long,UserTimesheetVo>();
		List<UserTimesheetVo> userTimesheets = userTimesheetService.findByWorkDay(fDay,lDay);
		List<User> user = userService.getAllUserByActivated();
		RoleHardWorking roleHardWorking = new RoleHardWorking();
		Double sumRealInput = null;
		Double sumAcceptInput = null;
		Double sumExtraInput = null;
		Double sumAcceptExtraInput = null;
		
		if(userTimesheets != null && userTimesheets.size() > 0){
			for(UserTimesheetVo utso : userTimesheets){
				map.put(utso.getUserId(), utso);
			}
		}
		for(User u : user){
			if(map.get(u.getId())==null){
				roleHardWorking.setUserId(u.getId());
				roleHardWorking.setSerialNum(u.getSerialNum());
				roleHardWorking.setRoleName(u.getLastName());
				roleHardWorking.setHardWorking(0D);
				roleHardWorking.setOriginMonth(StringUtil.stringToLong(lMonth));
			}else{
				sumRealInput = map.get(u.getId()).getSumRealInput();
				sumAcceptInput = map.get(u.getId()).getSumAcceptInput();
				sumExtraInput = map.get(u.getId()).getSumExtraInput();
				sumAcceptExtraInput = map.get(u.getId()).getSumAcceptExtraInput();
				roleHardWorking.setUserId(u.getId());
				roleHardWorking.setSerialNum(u.getSerialNum());
				roleHardWorking.setRoleName(u.getLastName()); 
				Double temp =  ((sumAcceptInput + sumAcceptExtraInput)/sumRealInput*100);
				roleHardWorking.setHardWorking(temp);
				roleHardWorking.setOriginMonth(StringUtil.stringToLong(lMonth));
			}
			map.remove(u.getId());
			roleHardWorkingService.saveAll(roleHardWorking);
			roleHardWorking.setId(null);
		}
		if(map.size()>0){
			for(Long userId : map.keySet()){
				roleHardWorking.setUserId(userId);
				User userById = userService.getRoleByUserId(userId);
				roleHardWorking.setSerialNum(userById.getSerialNum());
				roleHardWorking.setRoleName(userById.getLastName());
				sumRealInput = map.get(userId).getSumRealInput();
				sumAcceptInput = map.get(userId).getSumAcceptInput();
				sumExtraInput = map.get(userId).getSumExtraInput();
				Double temp =  ((sumAcceptInput + sumAcceptExtraInput)/sumRealInput*100);
				roleHardWorking.setHardWorking(temp);
				roleHardWorking.setOriginMonth(StringUtil.stringToLong(lMonth));
				roleHardWorkingService.saveAll(roleHardWorking);
				roleHardWorking.setId(null);
			}
		}
		
	}
}
