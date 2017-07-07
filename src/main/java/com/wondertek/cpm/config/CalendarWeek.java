package com.wondertek.cpm.config;

import java.util.ArrayList;
import java.util.List;

public class CalendarWeek {
	private int week;	//第几周
	private List<CalendarDay> days;
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public List<CalendarDay> getDays() {
		return days;
	}
	public void setDays(List<CalendarDay> days) {
		this.days = days;
	}
	public void addDay(CalendarDay day) {
		if(this.days == null) {
			this.days = new ArrayList<CalendarDay>();
		}
		this.days.add(day);
	}
	@Override
	public String toString() {
		return "CalendarWeek [week=" + week + ", days=" + days + "]";
	}
}
