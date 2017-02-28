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
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.vo.BonusRateVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.BonusRateService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

/**
 * REST controller for managing BonusRate.
 */
@RestController
@RequestMapping("/api")
public class BonusRateResource {

    private final Logger log = LoggerFactory.getLogger(BonusRateResource.class);
    @Inject
    private BonusRateService bonusRateService;
    /**
     * 列表页
     */
    @GetMapping("/bonus-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<List<BonusRateVo>> getUserPage(
    		@RequestParam(value = "contractType",required=false) Integer contractType, //合同类型
    		@RequestParam(value = "deptType",required=false) Long deptType, 	//部门类型
    		@ApiParam Pageable pageable
    		)
        throws URISyntaxException {
        log.debug("REST request to get a page of BonusRate  contractType:{},deptType:{}",contractType,deptType);
        BonusRate bonusRate = new BonusRate();
        bonusRate.setContractType(contractType);
        bonusRate.setDeptType(deptType);
        
        Page<BonusRateVo> page = bonusRateService.getUserPage(bonusRate,pageable);
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bonus-rate");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * @author sunshine
     * @Description : Updates an existing bonusRate.
     * 
     */
    @PutMapping("/bonus-rate")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Boolean> updateBonusRate(@RequestBody BonusRate bonusRate) throws URISyntaxException {
    	 log.debug("REST request to update BonusRate : {}", bonusRate);
    	 Boolean isNew = bonusRate.getId() == null;
    	 //校验参数
    	 if (bonusRate.getContractType() == null || bonusRate.getDeptType() == null || bonusRate.getRate() == null) {
    		 return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.bonusRate.save.requiedError", "")).body(null);
		}
    	String updator = SecurityUtils.getCurrentUserLogin();
    	ZonedDateTime updateTime = ZonedDateTime.now();
    	if (!isNew) {
			BonusRate oldBonusRate = bonusRateService.findOne(bonusRate.getId());
			if (oldBonusRate == null) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.bonusRate.save.idNone", "")).body(null);
			}else if (oldBonusRate.getDeptType() != bonusRate.getDeptType().longValue() || oldBonusRate.getContractType() != bonusRate.getContractType().longValue()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.bonusRate.save.fieldNoChange", "")).body(null);
			}
			bonusRate.setCreateTime(oldBonusRate.getCreateTime());
			bonusRate.setCreator(oldBonusRate.getCreator());
		}else {
			BonusRate hasBonusRate = bonusRateService.findByParams(bonusRate.getContractType(),bonusRate.getDeptType());
			if (hasBonusRate != null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.bonusRate.save.hasExist", "")).body(null);
			}
			bonusRate.setCreateTime(updateTime);
			bonusRate.setCreator(updator);
		}
    	bonusRate.setUpdateTime(updateTime);
    	bonusRate.setUpdator(updator);
    	BonusRate result = bonusRateService.save(bonusRate);
    	
    	if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("bonusRate", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("bonusRate", result.getId().toString()))
        			.body(isNew);
        }
    }
    /**
     * @author sunshine
     * @Description :  GET  /bonus-rate/:id : get the "id" bonusRate.
     * 
     */
    @GetMapping("/bonus-rate/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<BonusRate> getBonusRate(@PathVariable Long id){
        log.debug("REST request to get BonusRate : {}", id);
        BonusRate bonusRate = bonusRateService.getBonusRate(id);
        return Optional.ofNullable(bonusRate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    	
    }
    /**
     * @author sunshine
     * @Description : /bonus-rate/:id : delete the "id" bonusRate.
     * 
     */
    @DeleteMapping("/bonus-rate/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_BASIC)
    public ResponseEntity<Void> deleteBonusRate(@PathVariable Long id) {
        log.debug("REST request to delete BonusRate : {}", id);
        bonusRateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bonusRate", id.toString())).build();
    }
}
