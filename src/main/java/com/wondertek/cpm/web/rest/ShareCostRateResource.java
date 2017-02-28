package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

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
import com.wondertek.cpm.domain.ShareCostRate;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ShareCostRateService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing ShareCostRate.
 */
@RestController
@RequestMapping("/api")
public class ShareCostRateResource {

    private final Logger log = LoggerFactory.getLogger(ShareCostRateResource.class);
    @Inject
    private ShareCostRateService shareCostRateService;
    /**
     * 列表页
     */
    @GetMapping("/share-cost-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<ShareCostRate>> getUserPage(
    		@RequestParam(value = "contractType",required=false) Integer contractType, //合同类型
    		@RequestParam(value = "deptType",required=false) Long deptType, 	//部门类型
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  contractType:{},deptType:{}",contractType,deptType);
        ShareCostRate shareCostRate = new ShareCostRate();
        shareCostRate.setContractType(contractType);
        shareCostRate.setDeptType(deptType);
        
        Page<ShareCostRate> page = shareCostRateService.getUserPage(shareCostRate,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/share-cost-rate");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * @author sunshine
     * @Description : Updates an existing shareCostRate.
     * 
     */
    @PutMapping("/share-cost-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Boolean> updateShareCostRate(@RequestBody ShareCostRate shareCostRate) throws URISyntaxException {
    	log.debug("REST request to update ShareCostRate : {}", shareCostRate);
    	Boolean isNew = shareCostRate.getId() == null;
    	//校验参数
    	if (shareCostRate.getContractType() == null || shareCostRate.getDeptType() == null
    			|| shareCostRate.getShareRate() == null) {
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.shareCostRate.save.requiedError", "")).body(null);
		}
    	String updator = SecurityUtils.getCurrentUserLogin();
    	ZonedDateTime updateTime = ZonedDateTime.now();
    	if (!isNew) {
			ShareCostRate oldShareCostRate = shareCostRateService.findOne(shareCostRate.getId());
			if (oldShareCostRate == null) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.shareCostRate.save.idNone", "")).body(null);
			}else if (oldShareCostRate.getContractType() != shareCostRate.getContractType().longValue() || oldShareCostRate.getDeptType() != shareCostRate.getDeptType().longValue()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.shareCostRate.update.fieldNoChange", "")).body(null);
			}
			shareCostRate.setCreateTime(oldShareCostRate.getCreateTime());
			shareCostRate.setCreator(oldShareCostRate.getCreator());
		}else {
			shareCostRate.setCreateTime(updateTime);
			shareCostRate.setCreator(updator);
		}
    	shareCostRate.setUpdateTime(updateTime);
    	shareCostRate.setUpdator(updator);
    	ShareCostRate result = shareCostRateService.save(shareCostRate);

    	if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("shareCostRate", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("shareCostRate", result.getId().toString()))
        			.body(isNew);
        }
    }
    /**
     * @author sunshine
     * @Description :  GET  /share-cost-rate/:id : get the "id" shareCostRate.
     * 
     */
    @GetMapping("/share-cost-rate/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<ShareCostRate> getShareCostRate(@PathVariable Long id){
        log.debug("REST request to get ShareCostRate : {}", id);
        ShareCostRate shareCostRate = shareCostRateService.findOne(id);
        return Optional.ofNullable(shareCostRate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    	
    }
    /**
     * @author sunshine
     * @Description : /bonus-rate/:id : delete the "id" shareCostRate.
     * 
     */
    @DeleteMapping("/share-cost-rate/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteShareCostRate(@PathVariable Long id) {
        log.debug("REST request to delete ShareCostRate : {}", id);
        shareCostRateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("shareCostRate", id.toString())).build();
    }
}
