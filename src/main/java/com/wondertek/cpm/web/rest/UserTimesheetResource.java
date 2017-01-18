package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.domain.vo.UserTimesheetVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.UserTimesheetService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing UserTimesheet.
 */
@RestController
@RequestMapping("/api")
public class UserTimesheetResource {

    private final Logger log = LoggerFactory.getLogger(UserTimesheetResource.class);
        
    @Inject
    private UserTimesheetService userTimesheetService;
    /**
     * GET  /user-timesheets : get all the userTimesheets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userTimesheets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<List<UserTimesheetVo>> getAllUserTimesheets(
    		@RequestParam(value = "workDay",required=false) Long workDay,
    		@RequestParam(value = "type",required=false) Integer type,
    		@RequestParam(value = "objName",required=false) String objName,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of UserTimesheets");
//        Page<UserTimesheet> page = userTimesheetService.findAll(pageable);
        UserTimesheet userTimesheet = new UserTimesheet();
        userTimesheet.setWorkDay(workDay);
        userTimesheet.setType(type);
        userTimesheet.setObjName(objName);
        Page<UserTimesheet> page = userTimesheetService.getUserPage(userTimesheet, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-timesheets");
        List<UserTimesheetVo> returnList = new ArrayList<UserTimesheetVo>();
        if(page.getContent() != null){
        	for(UserTimesheet tmp : page.getContent()){
        		returnList.add(new UserTimesheetVo(tmp,null));
        	}
        }
        return new ResponseEntity<>(returnList, headers, HttpStatus.OK);
    }

    /**
     * GET  /user-timesheets/:id : get the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userTimesheet, or with status 404 (Not Found)
     */
    @GetMapping("/user-timesheets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<UserTimesheet> getUserTimesheet(@PathVariable Long id) {
        log.debug("REST request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetService.findOne(id);
        return Optional.ofNullable(userTimesheet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-timesheets/:id : delete the "id" userTimesheet.
     *
     * @param id the id of the userTimesheet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-timesheets/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<Void> deleteUserTimesheet(@PathVariable Long id) {
        log.debug("REST request to delete UserTimesheet : {}", id);
        userTimesheetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userTimesheet", id.toString())).build();
    }

    @GetMapping("/_edit/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<List<UserTimesheetForUser>> queryEditByUser(@RequestParam(value = "workDay") String workDay, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to queryByUser for a page of UserTimesheets for query {}", workDay);
        Date workDayDate = null;
        if(!StringUtil.isNullStr(workDay)){
        	workDayDate = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, workDay);
        }
        if(workDayDate == null){
        	workDayDate = new Date();
        }
        
        List<UserTimesheetForUser> list = userTimesheetService.queryEditByUser(workDayDate);
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @PutMapping("/_edit/user-timesheets")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_TIMESHEET)
    public ResponseEntity<Map<String, Object>> updateEditByUser(@RequestBody List<UserTimesheetForUser> userTimesheetForUsers) throws URISyntaxException {
        log.debug("REST request to update UserTimesheet : {}", userTimesheetForUsers);
        Map<String,Object> resultMap = new HashMap<String,Object>(); 
        if (userTimesheetForUsers == null || userTimesheetForUsers.isEmpty() || userTimesheetForUsers.size() < 3) {
        	resultMap.put("message", "cpmApp.userTimesheet.save.paramError");
        	resultMap.put("success", false);
        	return new ResponseEntity<>(resultMap, null, HttpStatus.OK);
        }
        //日期，工作地点，日报
        String[] messages = userTimesheetService.updateEditByUser(userTimesheetForUsers).split("#");
        resultMap.put("message", messages[0]);
        resultMap.put("success", messages[0].equals("cpmApp.userTimesheet.save.success"));
        if(messages.length > 1){
        	resultMap.put("param", messages[1]);
        }
    	return new ResponseEntity<>(resultMap, null, HttpStatus.OK);
    }
    
}
