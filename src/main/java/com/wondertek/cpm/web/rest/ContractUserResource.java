package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.vo.ContractUserVo;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractUserService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractUser.
 */
@RestController
@RequestMapping("/api")
public class ContractUserResource {

    private final Logger log = LoggerFactory.getLogger(ContractUserResource.class);
        
    @Inject
    private ContractUserService contractUserService;
    
    @Inject
    private ContractInfoRepository contractInfoRepository;


    @PutMapping("/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<Boolean> updateContractUser(@RequestBody ContractUser contractUser) throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to update ContractUser : {}", contractUser);
        Boolean isNew = contractUser.getId() == null;
        if (contractUser.getContractId() == null || contractUser.getUserId() == null 
        		 || StringUtil.isNullStr(contractUser.getUserName()) || contractUser.getJoinDay() == null
        		 || contractUser.getUserId() == null || StringUtil.isNullStr(contractUser.getUserName())
        		) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.paramNone", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractUser.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.contractInfoError", "")).body(null);
		}
        if(contractUser.getLeaveDay() != null && contractUser.getLeaveDay().longValue() < contractUser.getJoinDay()){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.dayError", "")).body(null);
        }
        //查看用户是否被添加
        boolean isExist = contractUserService.checkUserExist(contractUser);
        if (isNew) {
			 if(isExist){
		     	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.userIdError", "")).body(null);
		     }
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (isNew) {
			contractUser.setCreateTime(updateTime);
			contractUser.setCreator(updator);
		}else {
			ContractUserVo contractUserVo = contractUserService.getContractUser(contractUser.getId());
			if (contractUserVo == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.noPerm", "")).body(null);
			}
			ContractUser old = contractUserService.findOne(contractUser.getId());
			if (old == null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.idNone", "")).body(null);
			}else if (old.getContractId() != contractUser.getContractId().longValue()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.contractIdError", "")).body(null);
			}
			long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
	        if(old.getLeaveDay() != null && old.getLeaveDay() <= leaveDay){
	        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.leaveDayError", "")).body(null);
	        }
			contractUser.setCreateTime(old.getCreateTime());
			contractUser.setCreator(old.getCreator());
		}
        contractUser.setUpdateTime(updateTime);
        contractUser.setUpdator(updator);
        
        ContractUser result = contractUserService.save(contractUser);
        
        if (isNew) {
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityCreationAlert("contractUser", result.getId().toString()))
                    .body(isNew);
		}else {
			return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractUser", result.getId().toString()))
        			.body(isNew);
		}
        
    }
    @GetMapping("/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<List<ContractUserVo>> getAllContractUsers(
    		@RequestParam(value = "contractId",required=false) Long contractId, 
    		@RequestParam(value = "userId" ,required=false) Long userId, 
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get a page of ContractUsers by contractId : {}, userId : {}", contractId, userId);
        ContractUser contractUser = new ContractUser();
        contractUser.setContractId(contractId);
        contractUser.setUserId(userId);
        
        Page<ContractUserVo> page = contractUserService.getUserPage(contractUser,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/contract-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<ContractUserVo> getContractUser(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to get ContractUser : {}", id);
        ContractUserVo contractUserVo = contractUserService.getContractUser(id);
        return Optional.ofNullable(contractUserVo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contract-users/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<Void> deleteContractUser(@PathVariable Long id) {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to delete ContractUser : {}", id);
        ContractUserVo contractVo = contractUserService.getContractUser(id);
        if (contractVo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.noPerm", "")).body(null);
		}
        //校验合同状态是否可用
        ContractInfo contractInfo = contractInfoRepository.findOne(contractVo.getContractId());
        if (contractInfo == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.dataError", "")).body(null);
		}
        if (contractInfo.getStatus() != ContractInfo.STATUS_VALIDABLE) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractReceive.save.contractInfoError", "")).body(null);
		}
        long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
        if(contractVo.getLeaveDay() != null && contractVo.getLeaveDay() <= leaveDay){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractUser.save.leaveDayError", "")).body(null);
        }
        contractUserService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractUser", id.toString())).build();
    }

    @GetMapping("/_search/contract-users")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_USER)
    public ResponseEntity<List<ContractUser>> searchContractUsers(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug(SecurityUtils.getCurrentUserLogin() + " REST request to search for a page of ContractUsers for query {}", query);
        Page<ContractUser> page = contractUserService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
