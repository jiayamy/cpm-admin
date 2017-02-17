package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.vo.SalesBonusVo;
import com.wondertek.cpm.service.SalesBonusService;

/**
 * REST controller for managing SalesBonus.
 */
@RestController
@RequestMapping("/api")
public class SalesBonusResource {

    private final Logger log = LoggerFactory.getLogger(SalesBonusResource.class);
        
    @Inject
    private SalesBonusService salesBonusService;
    /**
     * 列表页
     */
    @GetMapping("/sales-bonus")
    @Timed
    public ResponseEntity<List<SalesBonusVo>> getSalesBonusPage(
    		@RequestParam(value = "originYear",required=false) Long originYear, //年份，默认当前年份
    		@RequestParam(value = "statWeek",required=false) Long statWeek, 	//统计日期，默认当前
    		@RequestParam(value = "contractId",required=false) Long contractId, //合同主键
    		@RequestParam(value = "salesManId",required=false) Long salesManId //销售
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectUsers");
        SalesBonus salesBonus = new SalesBonus();
        salesBonus.setOriginYear(originYear);
        salesBonus.setStatWeek(statWeek);
        salesBonus.setContractId(contractId);
        salesBonus.setSalesManId(salesManId);
        Date now = new Date();
        if(salesBonus.getOriginYear() == null){//默认当前年份
        	salesBonus.setOriginYear(StringUtil.nullToLong(DateUtil.formatDate("yyyy", now)));
        }
        if(salesBonus.getStatWeek() == null){//默认当前天
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(now))));
        }else{//更改为对应日期的周日
        	salesBonus.setStatWeek(StringUtil.nullToLong(
        			DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, 
        					DateUtil.getSundayOfDay(DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN,""+salesBonus.getStatWeek())))));
        }
        
        List<SalesBonusVo> page = salesBonusService.getUserPage(salesBonus);
        
        return new ResponseEntity<>(page, new HttpHeaders(), HttpStatus.OK);
    }
}
