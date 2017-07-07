package com.wondertek.cpm.config;

import java.util.ArrayList;
import java.util.List;

public class CalendarMonth {
	private int year;	//是请求日期的年月
	private int month;	//是请求日期的年月
	
	private List<CalendarWeek> weeks;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public List<CalendarWeek> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<CalendarWeek> weeks) {
		this.weeks = weeks;
	}

	public void addWeek(CalendarWeek week) {
		if(this.weeks == null) {
			this.weeks = new ArrayList<CalendarWeek>();
		}
		this.weeks.add(week);
	}
	@Override
	public String toString() {
		return "CalendarMonth [year=" + year + ", month=" + month + ", weeks=" + weeks + "]";
	}
}
