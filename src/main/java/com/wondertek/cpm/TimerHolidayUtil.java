package com.wondertek.cpm;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.wondertek.cpm.domain.HolidayInfo;
/**
 * Update Holiday Info.
 *
 */
public final class TimerHolidayUtil {

	private TimerHolidayUtil(){
		
	}
	/**
	 * Update Workday、 Saturday 、 Sunday of the next year.
	 * @return
	 */
	public static List<HolidayInfo> holidayUpdate(){
		Calendar cal = Calendar.getInstance();
//		cal.set(cal.get(Calendar.YEAR)+1, 0, 1);
//		cal.set(cal.get(Calendar.YEAR), 0, 1);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		Date start = cal.getTime();
		cal.add(Calendar.MONTH, 11);
//		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date end = cal.getTime();
		Long oneDay = 24*60*60*1000L;
		Long days = (end.getTime()-start.getTime())/oneDay;		//间隔天数
		List<Date> dateLists = new ArrayList<Date>();
		dateLists.add(start);
		for(int i=0;i<days;i++){
			dateLists.add(new Date(dateLists.get(i).getTime()+oneDay));
		}
		
		List<HolidayInfo> holidayInfos = new ArrayList<HolidayInfo>();
		ZoneId zone = ZoneId.of("GMT+08:00");
		Date date1 = new Date();
		Instant ins = date1.toInstant();
		ZonedDateTime zdt = ins.atZone(zone);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for(Date date:dateLists){
			HolidayInfo holidayInfo = new HolidayInfo();
			cal.setTime(date);
			holidayInfo.setCreateTime(zdt);
			holidayInfo.setCreator(CpmConstants.DEFAULT_HOLIDAY_CREATOR);
			holidayInfo.setCurrDay(Long.valueOf(sdf.format(date)));
			holidayInfo.setUpdateTime(zdt);
			holidayInfo.setUpdator(CpmConstants.DEFAULT_HOLIDAY_UPDATOR);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY ){
				holidayInfo.setType(CpmConstants.HOLIDAY_WEEKEND_TYPE);
				holidayInfos.add(holidayInfo);
			}else{
				holidayInfo.setType(CpmConstants.HOLIDAY_WORKDAY_TYPE);
				holidayInfos.add(holidayInfo);
			}
		}
		return holidayInfos;
	}
	
	/**
	 * Update Workday、 Saturday 、 Sunday of one month.
	 * @return
	 */
	public static List<HolidayInfo> holidayMonthUpdate(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date start = cal.getTime();
		Long oneDay = 24*60*60*1000L;
		List<Date> dateLists = new ArrayList<Date>();
		dateLists.add(start);
		for(int i=0;i<cal.getActualMaximum(Calendar.DAY_OF_MONTH)-1;i++){
			dateLists.add(new Date(dateLists.get(i).getTime()+oneDay));
		}
		
		List<HolidayInfo> holidayInfos = new ArrayList<HolidayInfo>();
		ZonedDateTime zdt = ZonedDateTime.now();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for(Date date:dateLists){
			HolidayInfo holidayInfo = new HolidayInfo();
			cal.setTime(date);
			holidayInfo.setCreateTime(zdt);
			holidayInfo.setCreator(CpmConstants.DEFAULT_HOLIDAY_CREATOR);
			holidayInfo.setCurrDay(Long.valueOf(sdf.format(date)));
			holidayInfo.setUpdateTime(zdt);
			holidayInfo.setUpdator(CpmConstants.DEFAULT_HOLIDAY_UPDATOR);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY ){
				holidayInfo.setType(CpmConstants.HOLIDAY_WEEKEND_TYPE);
				holidayInfos.add(holidayInfo);
			}else{
				holidayInfo.setType(CpmConstants.HOLIDAY_WORKDAY_TYPE);
				holidayInfos.add(holidayInfo);
			}
		}
		return holidayInfos;
	}
}
