package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ConsultantBonusService;
import com.wondertek.cpm.web.rest.errors.CpmResponse;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ConsultantBonus.
 */
@RestController
@RequestMapping("/api")
public class ConsultantBonusResource {

	private final Logger log = LoggerFactory.getLogger(ConsultantBonusResource.class);
	
	@Inject
	private ConsultantBonusService consultantBonusService;
	
	/**
     * GET  /contract-weekly-stats : get all the contractWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/consultant-bonus")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ConsultantBonusVo>> getAllConsultantsBonus(
    		@RequestParam(value="contractId",required = false) String contractId,
    		@RequestParam(value="fromDate",required = false) String fromDate,
    		@RequestParam(value="toDate",required = false) String toDate,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ConsultantsBonus:contractId--"+contractId+",fromDate--"+fromDate+",toDate--"+toDate);
//        if(!StringUtil.isNullStr(fromDate) && !StringUtil.isNullStr(toDate) && StringUtil.nullToLong(fromDate)>StringUtil.nullToLong(toDate)){
//        	return new ResponseEntity<>(HeaderUtil.createError("cpmApp.consultantBonus.search.dateError", ""),HttpStatus.BAD_REQUEST);
//        }
//        if(!StringUtil.isNullStr(fromDate)){//转化日期到周末
//        	Date date = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, fromDate.trim());
//        	Calendar cal = Calendar.getInstance();
//        	cal.setTime(date);
//        	cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//        	cal.add(Calendar.DAY_OF_WEEK, 1);
//        	fromDate = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime());
//        }
//        if(!StringUtil.isNullStr(toDate)){//转化日期到周末
//        	Date date = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, toDate.trim());
//        	Calendar cal = Calendar.getInstance();
//        	cal.setTime(date);
//        	cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//        	cal.add(Calendar.DAY_OF_WEEK, 1);
//        	toDate = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, cal.getTime());
//        }
        log.debug("------------:fromDate--"+fromDate+",toDate---"+toDate);
        if(StringUtil.isNullStr(fromDate) && StringUtil.isNullStr(toDate)){//搜索条件为空时，默认截止日期为当前日期的周末
        	Calendar cal = Calendar.getInstance();
        	cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        	cal.add(Calendar.DAY_OF_WEEK, 1);
        	toDate = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date());
        }
        Page<ConsultantBonusVo> page = consultantBonusService.getConsultantBonusPage(contractId,fromDate,toDate, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-weekly-stats");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        return Optional.ofNullable(page.getContent()).map(result -> new ResponseEntity<>(result,headers,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * GET  /consultant-bonus/:id : get the "id" userCost.
     *
     * @param id the id of the consultantBonus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the consultantBonus, or with status 404 (Not Found)
     */
    @GetMapping("/consultant-bonus/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ConsultantsBonus> getConsultantBonus(@PathVariable Long id) {
        log.debug("REST request to get ConsultantBonus : {}", id);
        ConsultantsBonus consultantsBonus = consultantBonusService.findOne(id);
        return Optional.ofNullable(consultantsBonus)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/consultant-bonus/queryConsultantRecord")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ConsultantBonusVo>> queryConsultantRecord(
//    			@PathVariable(name="contId") String contractId,
    			@RequestParam(value="contId",required = false) String contractId,
    			@ApiParam Pageable pageable) throws URISyntaxException{
    	log.debug("queryChart-----contractId:"+contractId);
    	if(contractId == null){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	Page<ConsultantBonusVo> page = consultantBonusService.getConsultantBonusRecordPage(contractId, pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/consultant-bonus/queryConsultantRecord");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
