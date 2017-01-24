package com.wondertek.cpm;
/**
 * 常见变量常量类
 * @author lvliuzhong
 *
 */
public class CpmConstants {

	public static final String DFAULT_USER_TIMESHEET_USER_INPUT = "0";
	
	public static final String ORDER_IGNORE_SCORE = "_score";
	
	public static final int STATUS_VALID = 1;		//可用
	public static final int STATUS_DELETED = 2;		//删除
	public static final Double DEFAULT_UPLOAD_EXCEL_USER_COST = 0D;
	public static final String DEFAULT_USER_COST_COSTMONTH_FROMAT = "yyyyMM";	//所属年月格式
	/**
	 * 用户默认组织，顶级部门的默认父部门ID
	 */
	public static final Long DEFAULT_DEPT_TOPID = 0l;

	public static final String DEFAULT_BLANK = "";
	
	public static final String DEFAULT_HOLIDAY_CREATOR = "system";
	public static final String DEFAULT_HOLIDAY_UPDATOR = "system";
	
	public static final Integer HOLIDAY_WORKDAY_TYPE = 1;	//正常工作日
	public static final Integer HOLIDAY_WEEKEND_TYPE = 2;	//正常假日
	public static final Integer HOLIDAY_ANNUAL_TYPE = 3;	//年假
	public static final Integer HOLIDAY_NATIONAL_TYPE= 4;	//国家假日
}
