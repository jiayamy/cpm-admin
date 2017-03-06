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
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ExternalQuotationService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ExternalQuotation.
 */
@RestController
@RequestMapping("/api")
public class ExternalQuotationResource {

    private final Logger log = LoggerFactory.getLogger(ExternalQuotationResource.class);
    @Inject
    private ExternalQuotationService externalQuotationService;
    /**
     * 列表页
     */
    @GetMapping("/external-quotation")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ExternalQuotation>> getUserPage(
    		@RequestParam(value = "grade",required=false) Integer grade, //级别
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of BonusRate  grade:{}",grade);
        ExternalQuotation externalQuotation = new ExternalQuotation();
        externalQuotation.setGrade(grade);
        
        Page<ExternalQuotation> page = externalQuotationService.getUserPage(externalQuotation,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/external-quotation");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @PutMapping("/external-quotation")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ExternalQuotation> updateExternalQuotation(@RequestBody ExternalQuotation externalQuotation) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ProjectInfo : {}", externalQuotation);
        Boolean isNew = externalQuotation.getId() == null;
        //基本校验
        if(externalQuotation.getGrade() == null || externalQuotation.getCostBasis() == null || externalQuotation.getExternalQuotation() == null
        		|| externalQuotation.getHourCost() == null || externalQuotation.getOtherExpense() == null || externalQuotation.getSocialSecurityFund() == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.externalQuotation.save.requriedError", "")).body(null);
        }
        //查看该用户是否有修改的权限
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if(!isNew){
        	//是否有权限
        	ExternalQuotation oldExternalQuotation = externalQuotationService.findOne(externalQuotation.getId());
        	if(oldExternalQuotation == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.externalQuotation.save.noPerm", "")).body(null);
        	}
        	//查看级别是否修改了。级别不能修改
        	if(oldExternalQuotation.getGrade().intValue() != externalQuotation.getGrade().intValue()){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.externalQuotation.save.changeError", "")).body(null);
        	}
        	//不变的
        	externalQuotation.setCreateTime(oldExternalQuotation.getCreateTime());
        	externalQuotation.setCreator(oldExternalQuotation.getCreator());
        }else{
        	//获取级别是否存在
        	ExternalQuotation oldExternalQuotation = externalQuotationService.findOneByGrade(externalQuotation.getGrade());
        	if(oldExternalQuotation != null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.externalQuotation.save.existError", "")).body(null);
        	}
        	externalQuotation.setCreateTime(updateTime);
        	externalQuotation.setCreator(updator);
        }
        externalQuotation.setUpdateTime(updateTime);
        externalQuotation.setUpdator(updator);
        
        ExternalQuotation result = externalQuotationService.save(externalQuotation);
        if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("externalQuotation", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("externalQuotation", externalQuotation.getId().toString()))
        			.body(result);
        }
    }
    
    @GetMapping("/external-quotation/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ExternalQuotation> getProjectInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ProjectInfo : {}", id);
        ExternalQuotation externalQuotation = externalQuotationService.findOne(id);
        
        return Optional.ofNullable(externalQuotation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/external-quotation/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteProjectInfo(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ProjectInfo : {}", id);
        ExternalQuotation externalQuotation = externalQuotationService.findOne(id);
        if(externalQuotation == null){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.externalQuotation.save.noPerm", "")).body(null);
        }
        externalQuotationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("externalQuotation", id.toString())).build();
    }
    
}
