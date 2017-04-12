package com.wondertek.cpm.web.rest;

import io.swagger.annotations.ApiParam;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.OutsourcingUserVo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.OutsourcingUserRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.OutsourcingUserService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;

/**
 * REST controller for managing ContractInfo.
 */
@RestController
@RequestMapping("/api")
public class OutsourcingUserResource {

	private final Logger log = LoggerFactory.getLogger(OutsourcingUserResource.class);

	@Inject
	private OutsourcingUserService outsourcingUserService;
	
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@Inject
	private OutsourcingUserRepository outsourcingUserRepository;

	@PutMapping("/contract-infos/updateOutsourcingUser")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<Boolean> updateOutsourcingUser(@RequestBody OutsourcingUser outsourcingUser) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update OutsourcingUser : {}", outsourcingUser);
        Boolean isNew = outsourcingUser.getId() == null;
        //校验参数
        if (outsourcingUser.getOffer() == null
        		|| outsourcingUser.getRank() == null || outsourcingUser.getTargetAmount() == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.requiedError", "")).body(null);
		}
        //判断合同状态是否可用（合同可能存在可能不存在）
    	if (outsourcingUser.getContractId() != null) {
    		ContractInfo contractInfo = contractInfoRepository.findOne(outsourcingUser.getContractId());
        	if (contractInfo == null) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
        	}
            if (contractInfo.getStatus().intValue() != ContractInfo.STATUS_VALIDABLE) {
            	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.statusUnvalidable", "")).body(null);
    		}
          //判断合同是否是外包类型
            if (contractInfo.getType().intValue() != ContractInfo.TYPE_EXTERNAL) {
            	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.typeError", "")).body(null);
    		}
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
			OutsourcingUser oldOutsourcingUser = this.outsourcingUserRepository.findOne(outsourcingUser.getId());
			if (oldOutsourcingUser == null ) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.idNone", "")).body(null);
			}else if(oldOutsourcingUser.getContractId() != null){
				if (!oldOutsourcingUser.getRank().equals(outsourcingUser.getRank())) {
					return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.ranknoChange", "")).body(null);
				}
			}
			outsourcingUser.setCreateTime(oldOutsourcingUser.getCreateTime());
			outsourcingUser.setCreator(oldOutsourcingUser.getCreator());
		}else {
			if (outsourcingUser.getContractId() == null) {
				//校验唯一标识
				if (StringUtil.isNullStr(outsourcingUser.getMark())) {
		        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
				}else {
					String str[] = outsourcingUser.getMark().split("_");
					String num = str[0];
					String createTimeD = str[1];
					if (StringUtil.isNullStr(num) || StringUtil.isNullStr(createTimeD) || StringUtil.nullToCloneLong(num) == null
							|| StringUtil.nullToCloneLong(createTimeD) == null || StringUtil.nullToInteger(num) < 0
							|| StringUtil.nullToInteger(num) > 100) {
			        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
					}
					//校验optionTime 
		           Date optionTime = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, createTimeD.substring(0,8)); 
		           if(optionTime == null || !createTimeD.substring(0,8).equals(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, optionTime))){ 
		        	   return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.dataError", "")).body(null);
		           }
		           //判断同一个标识下的人员级别是否有相同的情况
		           OutsourcingUser judgeUser = outsourcingUserRepository.findByRankAndMark(outsourcingUser.getRank(),outsourcingUser.getMark());
		           if (judgeUser != null) {
		        	   return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.rankHasExit", "")).body(null);
		           }
				}
			}else {
				//判断同个合同是否已经存在该级别
		        OutsourcingUser outUser = outsourcingUserRepository.findByParams(outsourcingUser.getContractId(),outsourcingUser.getRank());
		        if (outUser != null) {
		        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.rankHasExit", "")).body(null);
				}
			}
			outsourcingUser.setCreateTime(updateTime);
			outsourcingUser.setCreator(updator);
		}
        outsourcingUser.setUpdateTime(updateTime);
        outsourcingUser.setUpdator(updator);
		OutsourcingUser result = outsourcingUserService.save(outsourcingUser);
		
		if(isNew){
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("outsourcingUser", result.getId().toString()))
                    .body(isNew);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("outsourcingUser", result.getId().toString()))
        			.body(isNew);
        }
    }
	@GetMapping("/outsourcing-user/{infoId}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_PROJECT_USER)
    public ResponseEntity<OutsourcingUserVo> getOutsourcingUser(@PathVariable Long infoId) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get OutsourcingUser : {}", infoId);
        OutsourcingUserVo outsourcingUser = outsourcingUserService.findById(infoId);
        return Optional.ofNullable(outsourcingUser)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	@GetMapping("/outsourcing-user/getUserList")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<List<OutsourcingUser>> getUserList(
    		@RequestParam(value = "mark",required=false) String mark,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException{
    	log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a list of outsourcingUser mark:{},contractId:{}",mark,contractId);
    	//校验参数
    	if (StringUtil.isNullStr(mark) && StringUtil.isNullStr(contractId)) {
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.requiedError", "")).body(null);
		}
    	List<OutsourcingUser> page = outsourcingUserRepository.findByMarkOrContractId(mark, contractId);
		for (OutsourcingUser outsourcingUser : page) {
			outsourcingUser.setOffer(StringUtil.getScaleDouble(outsourcingUser.getOffer(), 2));
		}
         return new ResponseEntity<>(page,new HttpHeaders(),HttpStatus.OK);
    }
	/**
     * 加载项目人员的等级下拉框
     */
    @GetMapping("/outsourcing-user/queryUserRank")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserRank(@RequestParam(value = "contractId") Long contractId) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to queryUserRank");
        List<LongValue> list = outsourcingUserService.queryUserRank(contractId);
        if (list.size() < 1) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.outsourcingUser.save.haveNoUser", "")).body(null);
		}
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
}
