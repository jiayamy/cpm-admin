package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.TimerHolidayUtil;
import com.wondertek.cpm.config.CalendarDay;
import com.wondertek.cpm.config.CalendarMonth;
import com.wondertek.cpm.config.CalendarWeek;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.HolidayInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing HolidayInfo.
 */
@RestController
@RequestMapping("/api")
public class HolidayInfoResource {

    private final Logger log = LoggerFactory.getLogger(HolidayInfoResource.class);
        
    @Inject
    private HolidayInfoService holidayInfoService;

    /**
     * PUT  /holiday-infos : Updates an existing holidayInfo.
     *
     * @param holidayInfo the holidayInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated holidayInfo,
     * or with status 400 (Bad Request) if the holidayInfo is not valid,
     * or with status 500 (Internal Server Error) if the holidayInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/holiday-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<HolidayInfo> updateHolidayInfo(@RequestBody HolidayInfo holidayInfo) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to update HolidayInfo : {}", holidayInfo);
        Boolean isNew = null;
        if(holidayInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.requriedError", "")).body(null);
        }
        isNew = holidayInfo.getId() == null;
        //判断新增或编辑参数
        if(holidayInfo.getCurrDay() == null || holidayInfo.getType() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.requriedError", "")).body(null);
        }
        //获取当前用户
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        HolidayInfo findHoliday  = null;
        if(isNew){
        	findHoliday = holidayInfoService.findByCurrDay(holidayInfo.getCurrDay());
        	if(findHoliday != null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.existError", "")).body(null);
        	}
        	findHoliday = new HolidayInfo();
        	findHoliday.setCreateTime(updateTime);
        	findHoliday.setCreator(updator);
        	findHoliday.setCurrDay(holidayInfo.getCurrDay());
        }else{
        	findHoliday = holidayInfoService.findByCurrDay(holidayInfo.getCurrDay());
        	if(findHoliday == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.lostError", "")).body(null);
        	}
        }
        findHoliday.setType(holidayInfo.getType());
    	findHoliday.setUpdateTime(updateTime);
    	findHoliday.setUpdator(updator);
    	
        HolidayInfo result = holidayInfoService.save(findHoliday);
        if(isNew){
        	return ResponseEntity.created(new URI("/api/holiday-infos/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("holidayInfo", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("holidayInfo", findHoliday.getId().toString()))
        			.body(result);
        }
    }

    /**
     * GET  /holiday-infos : get all the holidayInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of holidayInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/holiday-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<HolidayInfo>> getAllHolidayInfos(
    		@RequestParam(value = "fromCurrDay",required=false) Long fromCurrDay,
    		@RequestParam(value = "toCurrDay",required=false) Long toCurrDay,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to get a page of HolidayInfos : fromCurrDay:{},toCurrDay:{}",fromCurrDay,toCurrDay);
        //初始化，以防定时任务不起作用
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Long dateTime = Long.valueOf(sdf.format(cal.getTime()));
        HolidayInfo holiResult = holidayInfoService.findByCurrDay(dateTime);
        List<HolidayInfo> lists = null;
        try {
			if(holiResult==null){
				lists = TimerHolidayUtil.holidayUpdate();
				holidayInfoService.save(lists);
			}else{
				lists = TimerHolidayUtil.holidayMonthUpdate();
				holidayInfoService.save(lists);
			}
		} catch (Exception e) {
			log.error("HolidayInfo update error:",e);
		}
        
        if(fromCurrDay != null && toCurrDay != null && toCurrDay < fromCurrDay){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.search.deadLineError", "")).body(null);
        }
        Map<String,Long> searchCondition = new HashMap<String,Long>();
        searchCondition.put("fromCurrDay", fromCurrDay);
        searchCondition.put("toCurrDay", toCurrDay);
        Page<HolidayInfo> page = holidayInfoService.getHolidayInfoPage(searchCondition,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/holiday-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /holiday-infos/:id : get the "id" holidayInfo.
     *
     * @param id the id of the holidayInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the holidayInfo, or with status 404 (Not Found)
     */
    @GetMapping("/holiday-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<HolidayInfo> getHolidayInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to getHolidayInfo : {}",id);
        HolidayInfo holidayInfo = holidayInfoService.findOne(id);
        return Optional.ofNullable(holidayInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /holiday-infos/:id : delete the "id" holidayInfo.
     *
     * @param id the id of the holidayInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/holiday-infos/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteHolidayInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to delete HolidayInfo : {}",id);
//        holidayInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("holidayInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/holiday-infos?fromCurrDay=:fromCurrDay&toCurrDay=:toCurrDay : search for the holidayInfo corresponding
     * to the query.
     *
     * @param query the fromCurrDay,toCurrDay of the holidayInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/holiday-infos")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<HolidayInfo>> searchHolidayInfos(
        		@RequestParam(value = "fromCurrDay",required=false) Long fromCurrDay,
        		@RequestParam(value = "toCurrDay",required=false) Long toCurrDay,
    			@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to search for a page of HolidayInfos : fromCurrDay:{},toCurrDay:{}",fromCurrDay,toCurrDay);
        Map<String,Long> searchCondition = new HashMap<String,Long>();
        searchCondition.put("fromCurrDay", fromCurrDay);
        searchCondition.put("toCurrDay", toCurrDay);
		Page<HolidayInfo> page = holidayInfoService.getHolidayInfoPage(searchCondition,pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/_search/holiday-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/holiday-infos/queryCalendar")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<CalendarMonth> queryCalendar(
    		@RequestParam(value = "currDay",required=false) Long currDay){
    	Date date = null;
    	if(currDay == null || currDay.toString().length() != 8){
    		date = new Date();
    	}else{
    		date = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, currDay.toString());//当天的日期
    	}
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    	Long fromCurrDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime()));
    	
    	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    	Long toCurrDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime()));
    	
    	Map<Long, HolidayInfo> types = holidayInfoService.getInfoByCurrDay(fromCurrDay,toCurrDay);
    	
    	CalendarMonth calendarMonth = getCalendarMonth(date, types, Boolean.TRUE);
    	
    	return ResponseEntity.ok().body(calendarMonth);
    }
    
    private CalendarMonth getCalendarMonth(Date day,Map<Long, HolidayInfo> types,boolean onlyThisMonth){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.setFirstDayOfWeek(Calendar.SUNDAY);	//周日开始
		int year = cal.get(Calendar.YEAR);		//所在年
		int month = cal.get(Calendar.MONTH);	//所在月
		
		CalendarMonth calendarMonth = new CalendarMonth();
		calendarMonth.setYear(year);
		calendarMonth.setMonth(month + 1);
		//这一个月的第一周的第一天
		cal.set(Calendar.WEEK_OF_MONTH, 1);//这个月的第一周
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//这个月的第一周的第一天
		//获取这一个月的所有日期
		boolean isThisMonth = false;
		boolean isLastThisMonth = false;
		while(true){
			if(cal.get(Calendar.MONTH) == month){
				isThisMonth = true;
			}else{
				isThisMonth = false;
			}
			if(isLastThisMonth && !isThisMonth){//跨月了，到下个月了
				break;
			}
			//一次加一周的记录
			CalendarWeek calendarWeek = new CalendarWeek();
			calendarWeek.setWeek(cal.get(Calendar.WEEK_OF_YEAR));
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			CalendarDay calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.SUNDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WEEKEND);
			}
			calendarWeek.addDay(calendarDay);
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.MONDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WORKDAY);
			}
			calendarWeek.addDay(calendarDay);

			cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.TUESDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WORKDAY);
			}
			calendarWeek.addDay(calendarDay);

			cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.WEDNESDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WORKDAY);
			}
			calendarWeek.addDay(calendarDay);

			cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.THURSDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WORKDAY);
			}
			calendarWeek.addDay(calendarDay);
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.FRIDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WORKDAY);
			}
			calendarWeek.addDay(calendarDay);

			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			calendarDay = new CalendarDay();
			if(!onlyThisMonth || (cal.get(Calendar.MONTH) == month)){
				calendarDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
				calendarDay.setDayOfWeek(Calendar.SATURDAY);
				setCalendarDay(calendarDay,types,StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime())),CalendarDay.TYPE_WEEKEND);
			}
			calendarWeek.addDay(calendarDay);
			
			isLastThisMonth = isThisMonth;
			calendarMonth.addWeek(calendarWeek);
			//下一周的数据
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		return calendarMonth;
	}
	private void setCalendarDay(CalendarDay calendarDay, Map<Long, HolidayInfo> types, Long day,
			Integer defaultType) {
		if(types != null && types.containsKey(day)){
			calendarDay.setType(types.get(day).getType());
			calendarDay.setId(types.get(day).getId());
		}else{
			calendarDay.setType(defaultType);
		}
	}
	@PutMapping("/holiday-infos/updateByOne")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<HolidayInfo> updateByOne(@RequestBody HolidayInfo holidayInfo) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin()+" REST request to update HolidayInfo : {}", holidayInfo);
        if(holidayInfo == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.requriedError", "")).body(null);
        }
        //判断新增或编辑参数
        if(holidayInfo.getCurrDay() == null || holidayInfo.getType() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.holidayInfo.save.requriedError", "")).body(null);
        }
        //获取当前用户
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        Boolean isNew = Boolean.FALSE;
        HolidayInfo findHoliday  = holidayInfoService.findByCurrDay(holidayInfo.getCurrDay());
    	if(findHoliday == null){
    		isNew = Boolean.TRUE;
    		findHoliday = new HolidayInfo();
    		findHoliday.setCreateTime(updateTime);
    		findHoliday.setCreator(updator);
    		findHoliday.setCurrDay(holidayInfo.getCurrDay());
    	}
        findHoliday.setType(holidayInfo.getType());
    	findHoliday.setUpdateTime(updateTime);
    	findHoliday.setUpdator(updator);
    	
        HolidayInfo result = holidayInfoService.save(findHoliday);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("holidayInfo", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("holidayInfo", findHoliday.getId().toString()))
        			.body(result);
        }
    }
}
