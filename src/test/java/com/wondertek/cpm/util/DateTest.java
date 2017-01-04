package com.wondertek.cpm.util;

import com.wondertek.cpm.config.DateUtil;

public class DateTest {
	public static void main(String[] args) {
		String[] ds = DateUtil.getWholeWeekByDate(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, "20161225"));
		for(String d : ds){
			System.out.println(d);
		}
	}
}
