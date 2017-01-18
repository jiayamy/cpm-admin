package com.wondertek.cpm.web.rest;

import java.net.URI;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.UserCostService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing UserCost.
 */
@RestController
@RequestMapping("/api")
public class UserCostResource {

    private final Logger log = LoggerFactory.getLogger(UserCostResource.class);
        
    @Inject
    private UserCostService userCostService;

    /**
     * POST  /user-costs : Create a new userCost.
     *
     * @param userCost the userCost to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userCost, or with status 400 (Bad Request) if the userCost has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCost> createUserCost(@RequestBody UserCost userCost) throws URISyntaxException {
        log.debug("REST request to save UserCost : {}", userCost);
        if (userCost.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userCost", "idexists", "A new userCost cannot already have an ID")).body(null);
        }
        UserCost result = userCostService.save(userCost);
        return ResponseEntity.created(new URI("/api/user-costs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("userCost", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-costs : Updates an existing userCost.
     *
     * @param userCost the userCost to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userCost,
     * or with status 400 (Bad Request) if the userCost is not valid,
     * or with status 500 (Internal Server Error) if the userCost couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCost> updateUserCost(@RequestBody UserCost userCost) throws URISyntaxException {
        log.debug("REST request to update UserCost : {}", userCost);
        Boolean isNew = null;
        if(userCost == null || userCost.getUserId() == null || userCost.getCostMonth() == null || 
        		userCost.getUserName() == null || userCost.getStatus() == null){
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.requriedError", "")).body(null);
    	}
        //获取当前用户
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        isNew = userCost.getId() == null;
        UserCost findUserCost = null;
        if(isNew){//新增
        	userCost.setId(null);
        	
        	findUserCost = userCostService.findByUserIdAndCostMonth(userCost.getUserId(),userCost.getCostMonth());
        	if(findUserCost != null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.existError", "")).body(null);
        	}
        	findUserCost = new UserCost();
        	findUserCost.setUserId(userCost.getUserId());
        	findUserCost.setUserName(userCost.getUserName());
        	findUserCost.setCostMonth(userCost.getCostMonth());
        	findUserCost.setCreateTime(updateTime);
        	findUserCost.setCreator(updator);
        }else{//编辑
        	findUserCost = userCostService.findByUserIdAndCostMonth(userCost.getUserId(),userCost.getCostMonth());
        	if(findUserCost == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.noExistError", "")).body(null);
        	}else if(userCost.getUserName() == null || userCost.getStatus() == null){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.requriedError", "")).body(null);
        	}else if(findUserCost.getStatus() == CpmConstants.STATUS_DELETED){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.statusError", "")).body(null);
        	}else if(!userCost.getUserName().equals(findUserCost.getUserName())){
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.save.userNameError", "")).body(null);
        	}
        }
        findUserCost.setStatus(userCost.getStatus());
        findUserCost.setExternalCost(userCost.getExternalCost());
    	findUserCost.setInternalCost(userCost.getInternalCost());
    	findUserCost.setUpdateTime(updateTime);
    	findUserCost.setUpdator(updator);
    	
        UserCost result = userCostService.save(findUserCost);
        if(isNew){
        	return ResponseEntity.created(new URI("/api/user-costs/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("userCost", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert("userCost", userCost.getId().toString()))
                    .body(result);
        }
    }

    /**
     * GET  /user-costs : get all the userCosts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userCosts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<List<UserCost>> getAllUserCosts(
    		@RequestParam(value = "userId",required=false) String userId,
    		@RequestParam(value = "userName",required=false) String userName,
    		@RequestParam(value = "costMonth",required=false) String costMonth,
    		@RequestParam(value = "status",required=false) String status,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of UserCosts");
        UserCost userCost = new UserCost();
        if(!StringUtil.isNullStr(userId)){
        	userCost.setUserId(StringUtil.nullToLong(userId));
        }
        if(!StringUtil.isNullStr(userName)){
        	userCost.setUserName(userName);
        }
        if(!StringUtil.isNullStr(costMonth)){
        	userCost.setCostMonth(StringUtil.nullToLong(costMonth));
        }
        if(!StringUtil.isNullStr(status)){
        	userCost.setStatus(StringUtil.nullToInteger(status));
        }
        Page<UserCost> page = userCostService.getUserCostPage(userCost,pageable);
//        Page<UserCost> page = userCostService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-costs/:id : get the "id" userCost.
     *
     * @param id the id of the userCost to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userCost, or with status 404 (Not Found)
     */
    @GetMapping("/user-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<UserCost> getUserCost(@PathVariable Long id) {
        log.debug("REST request to get UserCost : {}", id);
        UserCost userCost = userCostService.findOne(id);
        return Optional.ofNullable(userCost)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-costs/:id : delete the "id" userCost.
     *
     * @param id the id of the userCost to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-costs/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<Void> deleteUserCost(@PathVariable Long id) {
        log.debug("REST request to delete UserCost : {}", id);
        UserCost userCost = userCostService.findOne(id);
        if(userCost.getStatus() == CpmConstants.STATUS_DELETED){
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.userCost.delete.statusError", "")).body(null);
        }
        userCostService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userCost", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-costs?query=:query : search for the userCost corresponding
     * to the query.
     *
     * @param query the query of the userCost search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/user-costs")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_INFO_USERCOST)
    public ResponseEntity<List<UserCost>> searchUserCosts(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of UserCosts for query {}", query);
        Page<UserCost> page = userCostService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-costs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
