package com.wondertek.cpm.job;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.TimerHolidayUtil;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.service.HolidayInfoService;

@Component
public class HolidayScheduledJob {
	
	private final Logger log = LoggerFactory.getLogger(HolidayScheduledJob.class);
	
	@Inject
    private HolidayInfoService holidayInfoService;

	@Scheduled(cron="0 0 5 1 * ?")
	public void holidayUpdate(){
		log.debug("holidayUpdate start");
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Long dateTime = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime()));
		log.debug("holidayUpdate handling ,dateTime:" + dateTime);
		
		HolidayInfo holiResult = holidayInfoService.findByCurrDay(dateTime);
		List<HolidayInfo> lists = null;
		try {
			if(holiResult==null){
				lists = TimerHolidayUtil.holidayUpdate();
				holidayInfoService.save(lists);
			}else{
				lists = TimerHolidayUtil.holidayMonthUpdate();
				holidayInfoService.save(lists);
			}
		} catch (Exception e) {
			log.error("holidayUpdate error:", e);
		}
		log.debug("holidayUpdate end");
	}
}
