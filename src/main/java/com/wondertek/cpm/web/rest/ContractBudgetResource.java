package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractBudgetService;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

import org.hibernate.validator.internal.util.Contracts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ContractBudget.
 */
/**
 * @author sunshine
 * @Description : 
 * 
 */
@RestController
@RequestMapping("/api")
public class ContractBudgetResource {

    private final Logger log = LoggerFactory.getLogger(ContractBudgetResource.class);
        
    @Inject
    private ContractBudgetService contractBudgetService;
    @Inject
    private ContractInfoService contractInfoService;
    @Inject
    private ProjectInfoService projectInfoService;

    /**
     * POST  /contract-budgets : Create a new contractBudget.
     *
     * @param contractBudget the contractBudget to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contractBudget, or with status 400 (Bad Request) if the contractBudget has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
//    @PostMapping("/contract-budgets")
//    @Timed
//    public ResponseEntity<ContractBudget> createContractBudget(@RequestBody ContractBudget contractBudget) throws URISyntaxException {
//        log.debug("REST request to save ContractBudget : {}", contractBudget);
//        if (contractBudget.getId() != null) {
//            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contractBudget", "idexists", "A new contractBudget cannot already have an ID")).body(null);
//        }
//        ContractBudget result = contractBudgetService.save(contractBudget);
//        return ResponseEntity.created(new URI("/api/contract-budgets/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert("contractBudget", result.getId().toString()))
//            .body(result);
//    }

    /**
     * PUT  /contract-budgets : Updates an existing contractBudget.
     *
     * @param contractBudget the contractBudget to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contractBudget,
     * or with status 400 (Bad Request) if the contractBudget is not valid,
     * or with status 500 (Internal Server Error) if the contractBudget couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contract-budgets")
    @Timed
    public ResponseEntity<ContractBudgetVo> updateContractBudget(@RequestBody ContractBudgetVo contractBudgetVo) throws URISyntaxException {
        log.debug("REST request to update ContractBudget : {}", contractBudgetVo);
        ContractBudget contractBudget = new ContractBudget();
        //是否构建项目
        Boolean flag = contractBudgetService.checkByBudget(contractBudgetVo);
        ContractBudget oldContractBudget = contractBudgetService.findOneById(contractBudgetVo.getId());
        if (flag) {//构建项目
        	contractBudget.setId(contractBudgetVo.getId());
			contractBudget.setBudgetTotal(contractBudgetVo.getBudgetTotal());
			contractBudget.setName(contractBudgetVo.getBudgetName());
			contractBudget.setUpdateTime(ZonedDateTime.now());
			contractBudget.setUpdator(SecurityUtils.getCurrentUserLogin());
//			contractBudget.setContractId(oldContractBudget.getContractId());
//			contractBudget.setDeptId(oldContractBudget.getDeptId());
//			contractBudget.setUserName(contractBudget.getUserName());
//			contractBudget.setUserId(oldContractBudget.getUserId());
//			contractBudget.setStatus(1);
//			contractBudget.setType(3);
		}else {//未构建
			contractBudget.setBudgetTotal(contractBudgetVo.getBudgetTotal());
			contractBudget.setPurchaseType(contractBudgetVo.getPurchaseType());
			contractBudget.setName(contractBudgetVo.getBudgetName());
			contractBudget.setDept(contractBudgetVo.getDept());
			contractBudget.setDeptId(contractBudgetVo.getDeptId());
			contractBudget.setUserId(contractBudgetVo.getUserId());
			contractBudget.setUserName(contractBudgetVo.getUserName());
			contractBudget.setStatus(1);
			contractBudget.setType(3);
			//新建采购单
			if (oldContractBudget == null) {
				contractBudget.setContractId(contractBudgetVo.getContractId());
				contractBudget.setUpdateTime(ZonedDateTime.now());
				contractBudget.setCreateTime(ZonedDateTime.now());
				contractBudget.setUpdator(SecurityUtils.getCurrentUserLogin());
				contractBudget.setCreator(SecurityUtils.getCurrentUserLogin());
			}else {//完成更新
				contractBudget.setId(oldContractBudget.getId());
				contractBudget.setContractId(oldContractBudget.getContractId());
				contractBudget.setUpdateTime(ZonedDateTime.now());
				contractBudget.setUpdator(SecurityUtils.getCurrentUserLogin());
				contractBudget.setCreateTime(oldContractBudget.getCreateTime());
				contractBudget.setCreator(oldContractBudget.getCreator());
			}
		}
        contractBudgetService.save(contractBudget);
        return new ResponseEntity<>(contractBudgetVo,HttpStatus.OK);
    }

    /**
     * GET  /contract-budgets : get all the contractBudgets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contractBudgets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
//    @GetMapping("/contract-budgets")
//    @Timed
//    public ResponseEntity<List<ContractBudget>> getAllContractBudgets(@ApiParam Pageable pageable)
//        throws URISyntaxException {
//        log.debug("REST request to get a page of ContractBudgets");
//        Page<ContractBudget> page = contractBudgetService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-budgets");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

    /**
     * GET  /contract-budgets/:id : get the "id" contractBudget.
     *
     * @param id the id of the contractBudget to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contractBudget, or with status 404 (Not Found)
     */
    @GetMapping("/contract-budgets/{id}")
    @Timed
    public ResponseEntity<ContractBudgetVo> getContractBudgetVo(@PathVariable Long id) {
        log.debug("REST request to get ContractBudget : {}", id);
        ContractBudget contractBudget = contractBudgetService.findOneById(id);
        ContractInfo contractInfo = contractInfoService.findOne(contractBudget.getContractId());
        ContractBudgetVo contractBudgetVo = new ContractBudgetVo();
        if (contractBudget != null) {
        	contractBudgetVo.setBudgetTotal(contractBudget.getBudgetTotal());
        	contractBudgetVo.setBudgetName(contractBudget.getName());
        	contractBudgetVo.setId(contractBudget.getId());
        	contractBudgetVo.setDept(contractBudget.getDept());
        	contractBudgetVo.setDeptId(contractBudget.getDeptId());
        	contractBudgetVo.setStatus(contractBudget.getStatus());
        	contractBudgetVo.setUserName(contractBudget.getUserName());
        	contractBudgetVo.setPurchaseType(contractBudget.getPurchaseType());
        	contractBudgetVo.setUserId(contractBudget.getUserId());
		}
        if (contractInfo != null) {
			contractBudgetVo.setSerialNum(contractInfo.getSerialNum());
			contractBudgetVo.setName(contractInfo.getName());
		}
        return new ResponseEntity<>(contractBudgetVo,HttpStatus.OK);
    }

    /**
     * DELETE  /contract-budgets/:id : delete the "id" contractBudget.
     *
     * @param id the id of the contractBudget to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contract-budgets/{id}")
    @Timed
    public ResponseEntity<Void> deleteContractBudget(@PathVariable Long id) {
        log.debug("REST request to delete ContractBudget : {}", id);
        ContractBudget contractBudget = contractBudgetService.findOne(id);
        if (contractBudget.getStatus() != 1) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.projectInfo.save.haveDeleted", "")).body(null);
		}
        contractBudgetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractBudget", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contract-budgets?query=:query : search for the contractBudget corresponding
     * to the query.
     *
     * @param query the query of the contractBudget search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/contract-budgets")
    @Timed
    public ResponseEntity<List<ContractBudget>> searchContractBudgets(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ContractBudgets for query {}", query);
        Page<ContractBudget> page = contractBudgetService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contract-budgets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * GET  /product-prices : get all the contractBudget corresponding
     * to the query.
     *
     * @param query the query of the contractBudget search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
//    @GetMapping("/contract-budgets")
//    @Timed
//    public ResponseEntity<List<ContractBudget>> getAllContractBudgetsByParams(
//    		@RequestParam(value = "type") Integer type,
//    		@RequestParam(value = "purchaseType") Integer purchaseType,
//    		@RequestParam(value = "dept") String dept,
//    		@ApiParam Pageable pageable)
//    	throws URISyntaxException{
//    	log.debug("REST request to get a page of ContractBudget");	
//    	Page<ContractBudget> page = contractBudgetService.search(type,purchaseType,dept,pageable);
//    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(dept,page, "/api/contract-budgets");
//    	return new ResponseEntity<>(page.getContent(),headers,HttpStatus.OK);
//    	
//    }
    /**
     * GET  /product-prices : get all the contractBudget corresponding
     * to the query.
     *
     * @param query the query of the contractBudget search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/contract-budgets")
    @Timed
    public ResponseEntity<List<ContractBudgetVo>> getAllContractBudgetsByParams(
    		@RequestParam(value = "name") String name,
    		@RequestParam(value = "serialNum") String serialNum,
    		@RequestParam(value = "budgetName") String budgetName,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException{
    	log.debug("REST request to get a page of ContractBudget");
    	Page<ContractBudgetVo> page = contractBudgetService.searchPage(name,serialNum,budgetName,pageable);
    	HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(name, page,"/api/contract-budgets");
    	return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/contract-budgets/queryUserContract")
    @Timed
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException{
		log.debug("REST request to queryUserContrac");
		List<LongValue> list = contractBudgetService.queryUserContract();
        return new ResponseEntity<>(list, null, HttpStatus.OK);
    	
    }
}
