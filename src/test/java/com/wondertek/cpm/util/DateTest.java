package com.wondertek.cpm.util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.wondertek.cpm.config.DateUtil;

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
		
		String pageDay = "2017\0*215*";
		pageDay = pageDay.replaceAll("[*]", "");
		System.out.println(pageDay);
	}
}
