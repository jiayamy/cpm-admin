package com.wondertek.cpm.web.rest.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.service.HolidayInfoService;
import com.wondertek.cpm.web.rest.util.TimerHolidayUtil;

@Component
public class HolidayScheduledJob {
	
	private final Logger log = LoggerFactory.getLogger(HolidayScheduledJob.class);
	
	@Inject
    private HolidayInfoService holidayInfoService;

	@Scheduled(cron="0 0 5 1 * ?")
//	@Scheduled(fixedDelay = 20000)
	public void holidayUpdate(){
		//judge whether update holidayInfos.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Long dateTime = Long.valueOf(sdf.format(cal.getTime()));
		int count = holidayInfoService.findByCurrDay(dateTime);
		List<HolidayInfo> lists = null;
		try {
			if(count<=0){
				lists = TimerHolidayUtil.holidayUpdate();
				holidayInfoService.save(lists);
			}else{
				lists = TimerHolidayUtil.holidayMonthUpdate();
				holidayInfoService.save(lists);
			}
		} catch (Exception e) {
			log.error("HolidayInfo update error:", e);
		}
		log.debug("REST to update HolidayInfo : {}", count);
	}
}
