package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
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
import com.wondertek.cpm.domain.vo.UserTimesheetForHardWorkingVo;
import com.wondertek.cpm.repository.RoleHardWorkingRepository;
import com.wondertek.cpm.service.RoleHardWorkingService;
import com.wondertek.cpm.service.UserService;
import com.wondertek.cpm.service.UserTimesheetService;
/**
 * 员工勤奋度
 * @author lvliuzhong
 *
 */
@Component
@EnableScheduling
public class RoleHardWorkingStatTask {
	private Logger log = LoggerFactory.getLogger(RoleHardWorkingStatTask.class);

	@Inject
	private RoleHardWorkingService roleHardWorkingService;
	
	@Inject
	private RoleHardWorkingRepository roleHardWorkingRepository;
	
	@Inject
	private UserTimesheetService userTimesheetService;
	
	@Inject
    private UserService userService; 
	
	/**
	 * 每个月的第二天的1点开始执行，员工的勤奋度
	 */
	@Scheduled(cron = "0 0 3 15 * ?")
	protected void generateMonthlyState() {
		Date now = new Date();
		generateMonthlyState(now);
	}

	protected void generateMonthlyState(Date now) {
		log.info("=====begin generate role hardworking state=====");
		ZonedDateTime date = ZonedDateTime.now();
		Long fromDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthBegin(now)));
		Long endDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthend(now)));
		Long originMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now)));
		// 初始上月的stat
		roleHardWorkingRepository.deleteAllByOriginMonth(originMonth);
		
		//从工时表中获取这个月的所有统计记录
		Map<Long,UserTimesheetForHardWorkingVo> map = new HashMap<Long,UserTimesheetForHardWorkingVo>();
		List<UserTimesheetForHardWorkingVo> roleHardWorkingVos = userTimesheetService.findByWorkDay(fromDay,endDay);
		List<User> user = userService.getAllUserByActivated();
		
		RoleHardWorking roleHardWorking = new RoleHardWorking();
		Double sumRealInput = null;
		Double sumAcceptInput = null;
		Double sumExtraInput = null;
		Double sumAcceptExtraInput = null;
		
		if(roleHardWorkingVos != null && roleHardWorkingVos.size() > 0){
			for(UserTimesheetForHardWorkingVo rhwo : roleHardWorkingVos){
				map.put(rhwo.getUserId(), rhwo);
			}
		}
		
		for(User u : user){
			UserTimesheetForHardWorkingVo roleHardWorkingVo = map.get(u.getId());
			if(roleHardWorkingVo==null){
				roleHardWorking.setUserId(u.getId());
				roleHardWorking.setSerialNum(u.getSerialNum());
				roleHardWorking.setRoleName(u.getLastName());
				roleHardWorking.setHardWorking(0D);
				roleHardWorking.setOriginMonth(originMonth);
				roleHardWorking.setCreateTime(date);
			}else{
				sumRealInput = roleHardWorkingVo.getSumRealInput();
				sumAcceptInput = roleHardWorkingVo.getSumAcceptRealInput();
				sumExtraInput = roleHardWorkingVo.getSumExtraInput();
				sumAcceptExtraInput = roleHardWorkingVo.getSumAcceptExtraInput();
				Double temp =  ((sumAcceptInput + sumAcceptExtraInput)/sumRealInput*100);
				
				roleHardWorking.setUserId(u.getId());
				roleHardWorking.setSerialNum(u.getSerialNum());
				roleHardWorking.setRoleName(u.getLastName()); 
				roleHardWorking.setHardWorking(temp);
				roleHardWorking.setOriginMonth(originMonth);
				roleHardWorking.setCreateTime(date);
			}
			map.remove(u.getId());
			roleHardWorkingService.saveRoleHardWorking(roleHardWorking);
			roleHardWorking.setId(null);
		}
		if(map.size()>0){
			for(Long userId : map.keySet()){
				UserTimesheetForHardWorkingVo hwVo = map.get(userId);
				roleHardWorking.setUserId(userId);
				User userById = userService.getRoleByUserId(userId);
				roleHardWorking.setSerialNum(userById.getSerialNum());
				roleHardWorking.setRoleName(userById.getLastName());
				
				sumRealInput = hwVo.getSumRealInput();
				sumAcceptInput = hwVo.getSumAcceptRealInput();
				sumExtraInput = hwVo.getSumExtraInput();
				Double temp =  ((sumAcceptInput + sumAcceptExtraInput)/sumRealInput*100);
				roleHardWorking.setHardWorking(temp);
				roleHardWorking.setOriginMonth(originMonth);
				roleHardWorking.setCreateTime(date);
				roleHardWorkingService.saveRoleHardWorking(roleHardWorking);
				roleHardWorking.setId(null);
			}
		}
		
	}
}
