package com.wondertek.cpm.config;

public class MonthInfo{
	private int year;	//是请求日期的年月
	private int month;	//是请求日期的年月
	private int week;	//当年的第几周
	//全部的日期
	private Integer sunday;
	private Integer monday;
	private Integer tuesday;
	private Integer wednesday;
	private Integer thursday;
	private Integer friday;
	private Integer saturday;
	//简写
	private Integer sun;
	private Integer mon;
	private Integer tues;
	private Integer wed;
	private Integer thur;
	private Integer fri;
	private Integer sat;
	
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
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public Integer getSunday() {
		return sunday;
	}
	public void setSunday(Integer sunday) {
		this.sunday = sunday;
	}
	public Integer getMonday() {
		return monday;
	}
	public void setMonday(Integer monday) {
		this.monday = monday;
	}
	public Integer getTuesday() {
		return tuesday;
	}
	public void setTuesday(Integer tuesday) {
		this.tuesday = tuesday;
	}
	public Integer getWednesday() {
		return wednesday;
	}
	public void setWednesday(Integer wednesday) {
		this.wednesday = wednesday;
	}
	public Integer getThursday() {
		return thursday;
	}
	public void setThursday(Integer thursday) {
		this.thursday = thursday;
	}
	public Integer getFriday() {
		return friday;
	}
	public void setFriday(Integer friday) {
		this.friday = friday;
	}
	public Integer getSaturday() {
		return saturday;
	}
	public void setSaturday(Integer saturday) {
		this.saturday = saturday;
	}
	public Integer getSun() {
		return sun;
	}
	public void setSun(Integer sun) {
		this.sun = sun;
	}
	public Integer getMon() {
		return mon;
	}
	public void setMon(Integer mon) {
		this.mon = mon;
	}
	public Integer getTues() {
		return tues;
	}
	public void setTues(Integer tues) {
		this.tues = tues;
	}
	public Integer getWed() {
		return wed;
	}
	public void setWed(Integer wed) {
		this.wed = wed;
	}
	public Integer getThur() {
		return thur;
	}
	public void setThur(Integer thur) {
		this.thur = thur;
	}
	public Integer getFri() {
		return fri;
	}
	public void setFri(Integer fri) {
		this.fri = fri;
	}
	public Integer getSat() {
		return sat;
	}
	public void setSat(Integer sat) {
		this.sat = sat;
	}
	@Override
	public String toString() {
		return "MonthInfo [year=" + year + ", month=" + month + ", week=" + week + ", sunday=" + sunday + ", monday="
				+ monday + ", tuesday=" + tuesday + ", wednesday=" + wednesday + ", thursday=" + thursday + ", friday="
				+ friday + ", saturday=" + saturday + ", sun=" + sun + ", mon=" + mon + ", tues=" + tues + ", wed="
				+ wed + ", thur=" + thur + ", fri=" + fri + ", sat=" + sat + "]";
	}
}
