package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.SalesAnnualIndex;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.SalesAnnualIndexService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing SalesAnnualIndex.
 */
@RestController
@RequestMapping("/api")
public class SalesAnnualIndexResource {

    private final Logger log = LoggerFactory.getLogger(SalesAnnualIndexResource.class);
    @Inject
    private SalesAnnualIndexService salesAnnualIndexService;
    /**
     * 列表页
     */
    @GetMapping("/sales-annualIndex")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<SalesAnnualIndex>> getUserPage(
    		@RequestParam(value = "statYear",required=false) Long statYear, //年份
    		@RequestParam(value = "userId",required=false) Long userId, 	//用户
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of BonusRate  statYear:{},userId:{}",statYear,userId);
        SalesAnnualIndex salesAnnualIndex = new SalesAnnualIndex();
        salesAnnualIndex.setUserId(userId);
        salesAnnualIndex.setStatYear(statYear);
        
        Page<SalesAnnualIndex> page = salesAnnualIndexService.getUserPage(salesAnnualIndex,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sales-annualIndex");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PutMapping("/sales-annualIndex")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<SalesAnnualIndex> updatesalesAnnualIndex(@RequestBody SalesAnnualIndex salesAnnualIndex) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ProjectInfo : {}", salesAnnualIndex);
        Boolean isNew = salesAnnualIndex.getId() == null;
        //基本校验
        if(salesAnnualIndex.getStatYear() == null || salesAnnualIndex.getUserId() == null || salesAnnualIndex.getUserName() == null
        		|| salesAnnualIndex.getAnnualIndex() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.salesAnnualIndex.save.requriedError", "")).body(null);
        }
        //查看该用户是否有修改的权限
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(!isNew){
        	//是否有权限
        	SalesAnnualIndex oldsalesAnnualIndex = salesAnnualIndexService.findOne(salesAnnualIndex.getId());
        	if(oldsalesAnnualIndex == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.salesAnnualIndex.save.noPerm", "")).body(null);
        	}
        	//年份 销售都不能修改
        	if(oldsalesAnnualIndex.getStatYear().longValue() != salesAnnualIndex.getStatYear().longValue() 
        			|| oldsalesAnnualIndex.getUserId().longValue() != salesAnnualIndex.getUserId().longValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.salesAnnualIndex.save.changeError", "")).body(null);
        	}
        	//不变的
        	salesAnnualIndex.setCreateTime(oldsalesAnnualIndex.getCreateTime());
        	salesAnnualIndex.setCreator(oldsalesAnnualIndex.getCreator());
        }else{
        	//获取级别是否存在
        	SalesAnnualIndex oldsalesAnnualIndex = salesAnnualIndexService.findByStatYearAndUserId(salesAnnualIndex.getStatYear(),salesAnnualIndex.getUserId());
        	if(oldsalesAnnualIndex != null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.salesAnnualIndex.save.existError", "")).body(null);
        	}
        	salesAnnualIndex.setCreateTime(updateTime);
        	salesAnnualIndex.setCreator(updator);
        }
        salesAnnualIndex.setUpdateTime(updateTime);
        salesAnnualIndex.setUpdator(updator);
        
        SalesAnnualIndex result = salesAnnualIndexService.save(salesAnnualIndex);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("salesAnnualIndex", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("salesAnnualIndex", salesAnnualIndex.getId().toString()))
        			.body(result);
        }
    }
    
    @GetMapping("/sales-annualIndex/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<SalesAnnualIndex> getProjectInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ProjectInfo : {}", id);
        SalesAnnualIndex salesAnnualIndex = salesAnnualIndexService.findOne(id);
        
        return Optional.ofNullable(salesAnnualIndex)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/sales-annualIndex/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteProjectInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ProjectInfo : {}", id);
        SalesAnnualIndex salesAnnualIndex = salesAnnualIndexService.findOne(id);
        if(salesAnnualIndex == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.salesAnnualIndex.save.noPerm", "")).body(null);
        }
        salesAnnualIndexService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("salesAnnualIndex", id.toString())).build();
    }
}
