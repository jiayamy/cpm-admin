package com.wondertek.cpm.config;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtil {

	private static Log log = LogFactory.getLog(DateUtil.class);
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final String DATE_TIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_YYYYMMDD_PATTERN = "yyyyMMdd";
	public static final String DATE_YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
	public static final String TIME_HHMM_PATTERN = "HH:mm";
	public static final String TIME_HHMM_PATTERN2 = "HHmm";
	public static final String DATE_TIME_NO_HORI_PATTERN = "yyyyMMdd HH:mm:ss";
	public static final String DATE_TIME_NO_SPACE_PATTERN = "yyyyMMddHHmmss";
	public static final String DATE_TIME_NO_SPACE_MS_PATTERN = "yyyyMMddHHmmssS";
	public static final String DATE_TIME_PLAYBILL_PATTERN = "yyyyMMdd HH:mm";
	public static final String DATE_TIME_INDEX_PLAYBILL_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_ENGLISH_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";


	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			DATE_TIME_MS_PATTERN);
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			DATE_TIME_PATTERN);
	public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
			DATE_YYYYMMDD_PATTERN);
	public static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat(
			DATE_YYYY_MM_DD_PATTERN);
	public static final SimpleDateFormat HHmm = new SimpleDateFormat(
			TIME_HHMM_PATTERN);
	public static final SimpleDateFormat HHmm2 = new SimpleDateFormat(
			TIME_HHMM_PATTERN2);
	public static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
			DATE_TIME_NO_HORI_PATTERN);
	public static final SimpleDateFormat yyyyMMddHHmmssFile = new SimpleDateFormat(
			DATE_TIME_NO_SPACE_PATTERN);
	public static final SimpleDateFormat PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
			DATE_TIME_PLAYBILL_PATTERN);
	public static final SimpleDateFormat INDEX_PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
			DATE_TIME_INDEX_PLAYBILL_PATTERN);
	public static final SimpleDateFormat ENGLISH_SDF = new SimpleDateFormat(
			DATE_ENGLISH_FORMAT, Locale.ENGLISH);

	

	private static Map<String, SimpleDateFormat> patternFormatMap;

	public static  synchronized Map<String, SimpleDateFormat> getInstance() {

		if (patternFormatMap == null) {

			SimpleDateFormat timeFormat = new SimpleDateFormat(
					DATE_TIME_MS_PATTERN);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					DATE_TIME_PATTERN);
			SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
					DATE_YYYYMMDD_PATTERN);
			SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat(
					DATE_YYYY_MM_DD_PATTERN);
			SimpleDateFormat HHmm = new SimpleDateFormat(TIME_HHMM_PATTERN);
			SimpleDateFormat HHmm2 = new SimpleDateFormat(TIME_HHMM_PATTERN2);
			SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
					DATE_TIME_NO_HORI_PATTERN);
			SimpleDateFormat yyyyMMddHHmmssFile = new SimpleDateFormat(
					DATE_TIME_NO_SPACE_PATTERN);
			SimpleDateFormat yyyyMMddHHmmssSFile = new SimpleDateFormat(
					DATE_TIME_NO_SPACE_MS_PATTERN);
			SimpleDateFormat PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
					DATE_TIME_PLAYBILL_PATTERN);
			SimpleDateFormat INDEX_PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
					DATE_TIME_INDEX_PLAYBILL_PATTERN);
			SimpleDateFormat ENGLISH_SDF = new SimpleDateFormat(
					DATE_ENGLISH_FORMAT, Locale.ENGLISH);

			patternFormatMap = new HashMap<String, SimpleDateFormat>();
			patternFormatMap.put(DATE_TIME_MS_PATTERN, timeFormat);
			patternFormatMap.put(DATE_TIME_PATTERN, dateFormat);
			patternFormatMap.put(DATE_YYYYMMDD_PATTERN, yyyyMMdd);
			patternFormatMap.put(TIME_HHMM_PATTERN, HHmm);
			patternFormatMap.put(TIME_HHMM_PATTERN2, HHmm2);
			patternFormatMap.put(DATE_TIME_NO_HORI_PATTERN, yyyyMMddHHmmss);
			patternFormatMap
					.put(DATE_TIME_NO_SPACE_PATTERN, yyyyMMddHHmmssFile);
			patternFormatMap.put(DATE_TIME_PLAYBILL_PATTERN,
					PLAYBILL_TIME_PATTERN);
			patternFormatMap.put(DATE_ENGLISH_FORMAT, ENGLISH_SDF);
			patternFormatMap.put(DATE_YYYY_MM_DD_PATTERN, yyyy_MM_dd);
			patternFormatMap.put(DATE_TIME_INDEX_PLAYBILL_PATTERN, INDEX_PLAYBILL_TIME_PATTERN);
			patternFormatMap.put(DATE_TIME_NO_SPACE_MS_PATTERN, yyyyMMddHHmmssSFile);

		}
		return patternFormatMap;

	}

	public static String formatDate(String pattern, Date adate) {
		if (adate == null) {
			return "";
		}
		
//		SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);
//
//		if (sdf == null) {
//
//			sdf = new SimpleDateFormat(pattern);
//			DateUtil.getInstance().put(pattern, sdf);
//		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(adate);
	}

	public static Date parseDate(String pattern, String dateStr) {

		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}

//		SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);
//
//		if (sdf == null) {
//
//			sdf = new SimpleDateFormat(pattern);
//			DateUtil.getInstance().put(pattern, sdf);
//		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			log.error("errorMsg:"+e.getMessage());
		}

		return null;
	}

	
	public static Date parseDate(String pattern, String dateStr,Locale locale){ 
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_ENGLISH_FORMAT,locale);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 计算时间的起始时间
	 * */
	private final static String BASIC_DATE = "2000-01-01 00:00:00";

	/**
	 * Checkstyle rule: utility classes should not have public constructor
	 */
	private DateUtil() {
	}

	/**
	 * 把日期字符串yyyy-MM-dd HH:mm:ss转换成HH:mm形式
	 */
	public static String strToString(String date) {
		if (date == null || "".equals(date)) {
			return date;
		}
		String temp = "";
		try {
			Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN, date);
			temp = DateUtil.formatDate(TIME_HHMM_PATTERN, dateStr);
		} catch (Exception ex) {
			log.debug(ex.getStackTrace());
		}
		return temp;
	}

	/**
	 * 把日期字符串yyyy-MM-dd HH:mm:ss转换成yyyy-MM-dd HH:mm形式
	 */
	public static String strToStr(String date) {
		if (date == null || "".equals(date)) {
			return date;
		}
		String temp = "";
		try {
			Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN,date);
			temp = DateUtil.formatDate(DATE_TIME_INDEX_PLAYBILL_PATTERN,dateStr);
		} catch (Exception ex) {
			log.debug(ex.getStackTrace());
		}
		return temp;
	}

	public static String dateToString(Date date) {
		SimpleDateFormat df;
		String returnValue = "";
		if (date != null) {
			df = new SimpleDateFormat(DATE_FORMAT);
			returnValue = df.format(date);
		}

		return (returnValue);
	}

	/**
	 * This method generates a string representation of a date/time in the
	 * format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param strDate
	 *            a string representation of a date
	 * @return a converted Date object
	 * @see java.text.SimpleDateFormat
	 * @throws ParseException
	 *             when String doesn't match the expected format
	 */
	public static Date convertStringToDate(String aMask, String strDate)
			throws ParseException {
		SimpleDateFormat df;
		Date date;
		df = new SimpleDateFormat(aMask);

		if (log.isDebugEnabled()) {
			log.debug("converting '" + strDate + "' to date with mask '"
					+ aMask + "'");
		}

		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			// log.error("ParseException: " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}

	/**
	 * This method returns the current date time in the format: MM/dd/yyyy HH:MM
	 * a
	 * 
	 * @param theTime
	 *            the current time
	 * @return the current date/time
	 */
	public static String getTimeNow(Date theTime) {
		return getDateTime(TIME_PATTERN, theTime);
	}

	/**
	 * This method generates a string representation of a date's date/time in
	 * the format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param aDate
	 *            a date object
	 * @return a formatted string representation of the date
	 * 
	 * @see java.text.SimpleDateFormat
	 */
	public static String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			log.error("aDate is null!");
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	public static java.sql.Date convertDateToSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Timestamp convertDateToTimestamp(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}

	public static String getNowTime(Date date) {
		if (date == null) {
			return "";
		}
		return DateUtil.formatDate(DATE_TIME_MS_PATTERN, date);
	}
	
	public static String getNowTime(Object date) {
		if (date == null) {
			return "";
		}
		Date date2 = (Date) date;
		return DateUtil.formatDate(DATE_TIME_MS_PATTERN, date2);
	}

	public static String getDateTime(String sdate) {
		try {
			java.sql.Timestamp date = stringToTimestamp(sdate);
			return DateUtil.formatDate(DATE_TIME_PATTERN, date);
		} catch (Exception e) {
			return sdate;
		}
	}

	public static java.sql.Timestamp stringToTimestamp(String timestampStr) {
		if (timestampStr == null || timestampStr.length() < 1)
			return null;
		return java.sql.Timestamp.valueOf(timestampStr);
	}

	/**
	 * 根据日期计算出所在周的日期，并返回大小为7的数组
	 * 
	 * @param date
	 * @return
	 */
	public static String[] getWholeWeekByDate(Date date) {
		String[] ss = new String[7];
		Calendar calendar = Calendar.getInstance();
		for (int i = 0, j = 2; i < 6 && j < 8; i++, j++) {
			calendar.setTime(date);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, j);
			ss[i] = getFormatDate(calendar.getTime());
		}
		calendar.setTime(date);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
		ss[6] = getFormatDate(calendar.getTime());
		return ss;
	}

	/**
	 * 返回格式 yyyyMMdd的日期格式
	 * 
	 * @param d
	 * @return
	 */
	public static String getFormatDate(Date d) {
		return DateUtil.formatDate(DATE_YYYYMMDD_PATTERN, d);
	}

	public static String getHHmm2(Date d) {
		return DateUtil.formatDate(TIME_HHMM_PATTERN2, d);
	}

	public static Date getDateByString(String pattern) throws ParseException {
		return DateUtil.parseDate(DATE_YYYYMMDD_PATTERN, pattern);
	}

	public static Date getPlayBillTimeByPattern(String date)
			throws ParseException {
		return DateUtil.parseDate(DATE_TIME_PLAYBILL_PATTERN, date);
	}

	public static String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowTime = df.format(date);
		return nowTime;
	}

	/**
	 * @return 当前标准日期yyyyMMddHHmmss
	 */
	public static String getNowTimeNumber() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String nowTime = df.format(date);
		return nowTime;
	}

	/**
	 * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
	 * 
	 * @param 日期
	 * @return long
	 */
	public static Long getSeconds(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Date basicDate = formatter.parse(BASIC_DATE, new ParsePosition(0));
		long secLong = (date.getTime() - basicDate.getTime()) / 1000L;
		return secLong;
	}

	/**
	 * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
	 * 
	 * @param 日期
	 * @param 日期格式
	 *            例如：yyyy-MM-dd HH:mm:ss
	 * @return long
	 */
	public static Long getSeconds(String dateStr, String df) {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		if (df == null || "".equals(df)) {
			df = DATE_FORMAT;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(df);
		Date date = formatter.parse(dateStr, new ParsePosition(0));
		return getSeconds(date);
	}

	/**
	 * 返回格式 yyyyMMdd的日期格式
	 * 
	 * @param d
	 * @return
	 */

	public static Date getDateByStringyyyyMMddHHmmss(String pattern)
			throws ParseException {
		return DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN, pattern);
	}

	public static Date getFormatDateByEnglishSDF(String s) {
		return DateUtil.parseDate(DATE_ENGLISH_FORMAT, s);
	}

	public static String getFormatDateByyyyyMMddHHmmssFile(Date d) {
		return DateUtil.formatDate(DATE_TIME_NO_SPACE_PATTERN, d);
	}

	public static String formateStrDate(String d) {
		Date formateDate = DateUtil.parseDate(DATE_TIME_PATTERN, d);
		String dateStr = getFormatDateByyyyyMMddHHmmssFile(formateDate);
		return dateStr;
	}

	/**
	 * 将格式为yyyyMMddhhmmss的字符串 格式为yyyy-MM-dd
	 * 
	 * @param yyyyMMddhhmmss
	 * @return
	 */
	public static String formatDate(String d) {
		Date formateDate = DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN, d);
		String dateStr = DateUtil.formatDate(DATE_YYYY_MM_DD_PATTERN, formateDate);
		return dateStr;
	}

	/**
	 * 格式化 开始时间和结束时间
	 * 
	 * @param startDt
	 *            开始时间
	 * @param endDt
	 *            结束时间
	 * @param maxDay
	 *            最大天数
	 * @return
	 */
	public static Long[] formatDate(String startDt, String endDt, int maxDay) {
		Long[] res = new Long[2];
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			if (startDt == null)
				startDt = "";
			if (endDt == null)
				endDt = "";
			startDt = startDt.replace("-", "").replace(" ", "").replace(":", "");
			endDt = endDt.replace("-", "").replace(" ", "").replace(":", "");
			try {
				Long.valueOf(startDt);
			} catch (Exception e1) {
				startDt = "";
			}
			try {
				Long.valueOf(endDt);
			} catch (Exception e1) {
				endDt = "";
			}
			if (startDt == "" && endDt == "") {
				endDt = df.format(new Date());
			}

			if (startDt == "") {
				Date ed = df.parse(endDt);
				long time = ed.getTime();
				for (int i = 0; i < maxDay; i++) {
					time = time - 1000 * 3600 * 24;
				}
				Date sd = new Date(time);
				startDt = df.format(sd);
			} else if (endDt == "") {
				Date sd = df.parse(startDt);
				long time = sd.getTime();
				for (int i = 0; i < maxDay; i++) {
					time = time + 1000 * 3600 * 24;
				}
				Date ed = new Date(time);
				endDt = df.format(ed);
			} else {
				Date ed = df.parse(endDt);
				long time = ed.getTime();
				for (int i = 0; i < maxDay; i++) {
					time = time - 1000 * 3600 * 24;
				}
				Date sd = new Date(time);
				Long maxStartDt = Long.valueOf(df.format(sd));
				Long currStartDt = Long.valueOf(startDt);
				if (currStartDt < maxStartDt)
					startDt = String.valueOf(maxStartDt);
			}

			res[0] = Long.valueOf(startDt);
			res[1] = Long.valueOf(endDt);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatLongToTimeStr(Long msl) {
		String str = "";
		long day = 0;
		long hour = 0;
		long minute = 0;
		long second = 0;
		long ms = 0;

		second = msl.intValue() / 1000;
		ms = msl.intValue() % 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		if (hour > 24) {
			day = hour / 24;
			hour = hour % 24;
		}

		if (day > 0)
			str = day + "天";
		if (hour > 0)
			str += hour + "小时";
		if (minute > 0)
			str += minute + "分钟";
		if (second > 0)
			str += second + "秒";
		if (ms > 0)
			str += ms + "毫秒";

		return str;
	}

	public static String formatLongToTxt(Long msl) {
		String str = "";
		long day = 0;
		long hour = 0;
		long minute = 0;
		long second = 0;
		long ms = 0;
		second = msl.longValue() / 1000;
		ms = msl.longValue() % 1000;
		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		if (hour > 24) {
			day = hour / 24;
			hour = hour % 24;
		}

		if (day > 0)
			str = day + "天";
		if (hour > 0)
			str += hour + "小时";
		if (minute > 0 && (second > 0 || ms > 0)){
			minute = minute + 1;
			str += minute + "分钟";
		}else if(minute > 0){
			str += minute + "分钟";
		}else if(second > 0 || ms > 0){
			str += "1分钟";
		}
		
		return str;
	}

	public static long getBetweenDays(Date t1, Date t2) throws ParseException {
		return ((t2.getTime()) - t1.getTime()) / 1000 / 60 / 60 / 24;

	}

	public static int getIntervalDaysOfExitDate2(Date exitDateFrom,
			Date exitDateTo) {
		Calendar aCalendar = Calendar.getInstance();
		Calendar bCalendar = Calendar.getInstance();
		aCalendar.setTime(exitDateFrom);
		bCalendar.setTime(exitDateTo);
		int days = 0;
		while (aCalendar.before(bCalendar)) {
			days++;
			aCalendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	/**
	 * 日期String转换为Date.并且严格处理日期字符串的格式.
	 * 
	 * @param pattern
	 * @param dateStr
	 * @return
	 */
	public static Date parseDateNotLenient(String pattern, String dateStr) {

		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);

		if (sdf == null) {

			sdf = new SimpleDateFormat(pattern);
			DateUtil.getInstance().put(pattern, sdf);
		}

		try {
			sdf.setLenient(false);
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/****
	 * 
	 * 
	 * @param currentTimes
	 *            当前时间的毫秒数
	 * @param times
	 *            输入判断的毫秒数
	 * @return yesteday today tomorrow
	 */
	public static String getMarkByTime(long currentTimes, long times) {
		String result = "";

		long rate = 24 * 3600 * 1000;

		long today = currentTimes / rate;
		long yesterday = today - 1;
		long beforeYesterday = yesterday - 1;
		long tomorrow = today + 1;

		long input = times / rate;
		if (input == yesterday) {
			result = "yesterday";
		} else if (input == today) {
			result = "today";
		} else if (input == beforeYesterday) {
			result = "beforeYesterday";
		} else if (input == tomorrow) {
			result = "tomorrow";
		}
		return result;
	}

	/**
	 * 获取下月的第一天
	 * 
	 * @param curMonth
	 *            yyyy-mm : 2011-09
	 * @return
	 */
	public static String getNextMonth(String curMonth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String startDate = ""; // 当前月的第一天
		String endDate = ""; // 下个月的第一天
		String[] ta = curMonth.split("-");
		c.setTime(java.sql.Date.valueOf(curMonth + "-01"));
		c.add(Calendar.MONTH, 0);// 当前月
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置月份的第一天
		startDate = sdf.format(c.getTime());
		c.set(Calendar.MONTH, Integer.parseInt(ta[1]));// 回到当前月
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置当前月第一天
		c.add(Calendar.DAY_OF_YEAR, 0); //
		endDate = sdf.format(c.getTime());
		return endDate;
	}

	/**
	 * 获取日期范围内的月份明细
	 * 
	 * @param startMonth
	 *            2012-07
	 * @param endMonth
	 *            2012-09
	 * @return {2012-07,2012-08,2012-09}
	 * @throws Exception
	 */
	public static List<String> getMonthList(String startMonth, String endMonth)
			throws Exception {
		List<String> monthList = new ArrayList<String>();
		monthList.add(startMonth);
		while (!startMonth.equals(endMonth)) {
			startMonth = DateUtil.getNextMonth(startMonth);
			if (startMonth != null && !startMonth.equals("")) {
				startMonth = startMonth.substring(0, 7);
				monthList.add(startMonth);
			}
		}

		return monthList;
	}

	/**
	 * 得到某年某月的第一天
	 * 
	 * @param yearMonth
	 *            2012-09
	 * @return
	 */
	public static String getFirstDayOfMonth(String yearMonth) {
		String[] tmp = yearMonth.split("-");
		int year = Integer.parseInt(tmp[0]);
		int month = Integer.parseInt(tmp[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}

	/**
	 * 得到某年某月的最后一天
	 * 
	 * @param yearMonth
	 *            2012-09
	 * @return
	 */
	public static String getLastDayOfMonth(String yearMonth) {
		String[] tmp = yearMonth.split("-");
		int year = Integer.parseInt(tmp[0]);
		int month = Integer.parseInt(tmp[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}

	/**
	 * 
	 * @param pattern
	 * @param day
	 * @return
	 */
	public static String getYesterdayOfDay(String pattern, Date day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
	/**
	 * 校验日期格式
	 * @param sDate
	 * @return
	 */
	public static boolean isValidDate(String sDate){
		String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
		String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
				+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
				+ "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
				+ "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		if ((sDate != null)) {
			Pattern pattern = Pattern.compile(datePattern1);
			Matcher match = pattern.matcher(sDate);
			if (match.matches()) {
				pattern = Pattern.compile(datePattern2);
				match = pattern.matcher(sDate);
				return match.matches(); 
			} else {
				return false;
			}
		}
		return false;
	}
	/**
	 * 数据库中的时间 转成ZondedDateTime
	 * @param timestamp
	 * @return
	 */
	public static ZonedDateTime getZonedDateTime(Timestamp timestamp){
		if(timestamp == null){
			return null;
		}
		return ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.systemDefault());
	}
	/**
	 * 毫秒值转成ZonedDateTime
	 * @param mins
	 * @return
	 */
	public static ZonedDateTime getZonedDateTime(Long mins){
		if(mins == null){
			return null;
		}
		return Instant.ofEpochMilli(mins).atZone(ZoneId.systemDefault());
	}
	
	/**
	 * 获取上周周六0点
	 * @return
	 */
	public static Date lastSaturday() {
		Calendar cal = Calendar.getInstance();
		int n = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (n == 0) {
			n = 7;
		}
		cal.add(Calendar.DATE, -(7 + (n - 1)));// 上周一的日期
		cal.add(Calendar.DATE, 5);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date Saturday = cal.getTime();
		return Saturday;
	}
	
	/**
	 * 上周周日晚上23时
	 * @return
	 */
	public static Date lastSundayEnd(){
		Calendar cal = Calendar.getInstance();
		int n = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (n == 0) {
			n = 7;
		}
		cal.add(Calendar.DATE, -(7 + (n - 1)));// 上周一的日期
		cal.add(Calendar.DATE, 6);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date Saturday = cal.getTime();
		return Saturday;
	}
	
	/**
	 * 获取上月最后一天
	 * @return
	 */
	public static Date lastMonthend() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date strDateTo = cal.getTime();
		return strDateTo;
	}
	
	
	public static String getFirstDayOfLastMonth(String pattern) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}

	
	public static String getLastDayOfLastMonth(String pattern) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
	
	/**
	 * 得到某年某月的最后一天
	 * 
	 * @param yearMonth
	 *            2012-09
	 * @return
	 */
	public static String getLastDayOfMonth(String pattern, String yearMonth) {
		String[] tmp = yearMonth.split("-");
		int year = Integer.parseInt(tmp[0]);
		int month = Integer.parseInt(tmp[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}
}
