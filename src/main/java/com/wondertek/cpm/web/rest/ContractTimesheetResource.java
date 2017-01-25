package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.UserTimesheetForOther;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.domain.vo.UserTimesheetVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.UserTimesheetService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractTimesheet.
 */
@RestController
@RequestMapping("/api")
public class ContractTimesheetResource {

    private final Logger log = LoggerFactory.getLogger(ContractTimesheetResource.class);
        
    @Inject
    private UserTimesheetService userTimesheetService;

    @GetMapping("/contract-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_TIMESHEET)
    public ResponseEntity<List<UserTimesheetVo>> getAllUserTimesheets(
    		@RequestParam(value = "workDay",required=false) Long workDay,
    		@RequestParam(value = "contractId",required=false) Long contractId,
    		@RequestParam(value = "userId",required=false) Long userId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of UserTimesheets");
        UserTimesheet userTimesheet = new UserTimesheet();
        userTimesheet.setWorkDay(workDay);
        userTimesheet.setType(UserTimesheet.TYPE_CONTRACT);
        userTimesheet.setObjId(contractId);
        userTimesheet.setUserId(userId);
        
        Page<UserTimesheet> page = userTimesheetService.getContractPage(userTimesheet, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-timesheets");
        List<UserTimesheetVo> returnList = new ArrayList<UserTimesheetVo>();
        if(page.getContent() != null){
        	for(UserTimesheet tmp : page.getContent()){
        		returnList.add(new UserTimesheetVo(tmp));
        	}
        }
        return new ResponseEntity<>(returnList, headers, HttpStatus.OK);
    }
    
    @GetMapping("/contract-timesheets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_TIMESHEET)
    public ResponseEntity<UserTimesheetVo> getUserTimesheet(@PathVariable Long id) {
        log.debug("REST request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetService.getUserTimesheetForContract(id);
        UserTimesheetVo vo = null;
        if(userTimesheet != null){
        	vo = new UserTimesheetVo(userTimesheet);
        }
        return Optional.ofNullable(vo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/contract-timesheets/queryEdit")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_TIMESHEET)
    public ResponseEntity<List<UserTimesheetForOther>> getEditUserTimesheets(
    		@RequestParam(value = "workDay") String workDay,
    		@RequestParam(value = "id") Long id
    		) {
        log.debug("REST request to get UserTimesheet : {}", id);
        Date workDayDate = null;
        if(!StringUtil.isNullStr(workDay)){
        	workDayDate = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, workDay);
        }
        List<UserTimesheetForOther> list = userTimesheetService.queryEditByOther(id,UserTimesheet.TYPE_CONTRACT,workDayDate);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
}
