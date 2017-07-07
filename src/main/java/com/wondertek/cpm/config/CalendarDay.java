package com.wondertek.cpm.config;

public class CalendarDay {
	public static final Integer TYPE_WORKDAY = 1;	//正常工作日
	public static final Integer TYPE_WEEKEND = 2;	//正常假日
	public static final Integer TYPE_ANNUAL = 3;	//年假
	public static final Integer TYPE_NATIONAL= 4;	//国家假日
	
	private Long id;
	private Integer day;
	private Integer dayOfWeek;//星期几  1-7  周日-周六
	
	private Integer type;//类型（1正常工作日/2正常假日/3年假/4国家假日）
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public String toString() {
		return "CalendarDay [id=" + id + ", day=" + day + ", dayOfWeek=" + dayOfWeek + ", type=" + type + "]";
	}

}
