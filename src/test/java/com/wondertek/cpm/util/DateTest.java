package com.wondertek.cpm.util;

import java.util.List;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.MonthInfo;

public class DateTest {
	public static void main(String[] args) {
//		String[] ds = DateUtil.getWholeWeekByDate(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, "20161225"));
//		for(String d : ds){
//			System.out.println(d);
//		}
//		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_MS_PATTERN);
//		
//		Timestamp time = new Timestamp(new Date().getTime());
//		System.out.println(time.toString());
//		
//		ZonedDateTime time2 = ZonedDateTime.of(time.toLocalDateTime(), ZoneId.systemDefault());
//		System.out.println(time2.toString());
		
//		String pageDay = "2017\0*215*";
//		pageDay = pageDay.replaceAll("[*]", "");
//		System.out.println(pageDay);
		
		Long day = 20170101L;
		while(day < 20173000){
			List<MonthInfo> infos = DateUtil.getMonthData(day+"");
			for(MonthInfo MonthInfo : infos){
				System.out.println(MonthInfo);
			}
			System.out.println("------" + infos.size());
			day = day + 100;
		}
	}
}
