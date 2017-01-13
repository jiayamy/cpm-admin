package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.ContractInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ContractInfo.
 */
@RestController
@RequestMapping("/api")
public class ContractInfoResource {

	private final Logger log = LoggerFactory.getLogger(ContractInfoResource.class);

	@Inject
	private ContractInfoService contractInfoService;

	@PutMapping("/contract-infos")
    @Timed
    public ResponseEntity<ContractInfo> updateContractInfo(@RequestBody ContractInfo contractInfo) throws URISyntaxException {
        log.debug("REST request to update ContractInfo : {}", contractInfo);
        Boolean isNew = contractInfo.getId() == null;
        //基本校验
        if (contractInfo.getIsPrepared() == null && contractInfo.getIsEpibolic() == null
        	&& contractInfo.getSalesmanId() == null && StringUtil.isNullStr(contractInfo.getSalesman())
        	&& contractInfo.getConsultantsId() == null && StringUtil.isNullStr(contractInfo.getConsultants())
        	&& contractInfo.getConsultantsDeptId() == null && StringUtil.isNullStr(contractInfo.getConsultantsDept())) {
        	
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
		}
        if (StringUtil.isNullStr(contractInfo.getSerialNum()) || StringUtil.isNullStr(contractInfo.getName())
        		|| contractInfo.getAmount() == null || contractInfo.getType() == null
        		|| contractInfo.getDeptId() == null || StringUtil.isNullStr(contractInfo.getDept()) 
        		|| contractInfo.getStartDay() == null || contractInfo.getTaxes() == null
        		|| contractInfo.getTaxes() == null || contractInfo.getShareRate() == null
        		|| contractInfo.getShareCost() == null || StringUtil.isNullStr(contractInfo.getPaymentWay())
        		|| StringUtil.isNullStr(contractInfo.getContractor()) || StringUtil.isNullStr(contractInfo.getAddress())
        		|| StringUtil.isNullStr(contractInfo.getPostcode()) || StringUtil.isNullStr(contractInfo.getLinkman())
        		|| StringUtil.isNullStr(contractInfo.getTelephone())) {
        		
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.requiedError", "")).body(null);
		}
        //结束时间校验
        if (contractInfo.getEndDay() != null && contractInfo.getEndDay().isBefore(contractInfo.getStartDay())) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contractInfo.save.endDayError", "")).body(null);
		}
        //查看合同是否是唯一
        boolean isExist = contractInfoService.checkByContract(contractInfo.getSerialNum(),contractInfo.getId());
        if (isExist) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.contactInfo.save.existSerialNum" ,"")).body(null);
		}
     
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
        	//查看该用户是否有修改的权限
			//是否有权限，此处该写权限，，，还没写
        	
        	ContractInfo oldContractInfo = contractInfoService.findOne(contractInfo.getId());
        	if (contractInfo.getSalesmanId() == null) {
				contractInfo.setSalesmanId(oldContractInfo.getSalesmanId());
				contractInfo.setSalesman(oldContractInfo.getSalesman());
			}
        	if (contractInfo.getDeptId() == null) {
				contractInfo.setDeptId(oldContractInfo.getDeptId());
				contractInfo.setDept(oldContractInfo.getDept());
			}
        	if (contractInfo.getConsultantsId() == null) {
				contractInfo.setConsultantsId(oldContractInfo.getConsultantsId());
				contractInfo.setConsultants(oldContractInfo.getConsultants());
			}
        	if (contractInfo.getConsultantsDeptId() == null) {
				contractInfo.setConsultantsDeptId(oldContractInfo.getConsultantsDeptId());
				contractInfo.setConsultantsDept(oldContractInfo.getConsultantsDept());
			}
        	if (contractInfo.getStatus() == null) {
        		contractInfo.setStatus(oldContractInfo.getStatus());
			}
        	if (contractInfo.getFinishRate() == null) {
				contractInfo.setFinishRate(oldContractInfo.getFinishRate());
			}
		}else {
			contractInfo.setCreateTime(updateTime);
			contractInfo.setCreator(updator);
			contractInfo.setStatus(ContractInfo.STATUS_VALIDABLE);
			contractInfo.setFinishRate(0d);
		}
        contractInfo.setUpdateTime(updateTime);
        contractInfo.setUpdator(updator);
        ContractInfo result = contractInfoService.save(contractInfo);
        if(isNew){
        	return ResponseEntity.created(new URI("/api/project-infos/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("contractInfo", result.getId().toString()))
                    .body(result);
        }else{
        	return ResponseEntity.ok()
        			.headers(HeaderUtil.createEntityUpdateAlert("contractInfo", contractInfo.getId().toString()))
        			.body(result);
        }
        
    }

	@GetMapping("/contract-infos")
	@Timed
	public ResponseEntity<List<ContractInfo>> getAllContractInfos(@RequestParam(value = "name") String name,
			@RequestParam(value = "type") Integer type, @RequestParam(value = "isPrepared") Boolean isPrepared,
			@RequestParam(value = "isEpibolic") Boolean isEpibolic, @RequestParam(value = "salesman") Long salesman,
			@ApiParam Pageable pageable) throws URISyntaxException {
		log.debug("REST request to get a page of ContractInfos");
		ContractInfo contractInfo = new ContractInfo();

		contractInfo.setSalesmanId(salesman);
		contractInfo.setName(name);
		contractInfo.setType(type);
		contractInfo.setIsPrepared(isPrepared);
		contractInfo.setIsEpibolic(isEpibolic);

		Page<ContractInfo> page = contractInfoService.getContractInfoPage(contractInfo, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contract-infos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/contract-infos/{id}")
	@Timed
	public ResponseEntity<ContractInfo> getContractInfo(@PathVariable Long id) {
		log.debug("REST request to get ContractInfo : {}", id);
		ContractInfo contractInfo = contractInfoService.findOne(id);
		return Optional.ofNullable(contractInfo).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /contract-infos/:id : delete the "id" contractInfo.
	 *
	 * @param id
	 *            the id of the contractInfo to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/contract-infos/{id}")
	@Timed
	public ResponseEntity<Void> deleteContractInfo(@PathVariable Long id) {
		log.debug("REST request to delete ContractInfo : {}", id);
		contractInfoService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contractInfo", id.toString())).build();
	}

	// 留作参考
	@GetMapping("/_search/contract-infos")
	@Timed
	public ResponseEntity<List<ContractInfo>> searchContractInfos(@RequestParam String query,
			@ApiParam Pageable pageable) throws URISyntaxException {
		log.debug("REST request to search for a page of ContractInfos for query {}", query);
		Page<ContractInfo> page = contractInfoService.search(query, pageable);
		HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page,
				"/api/_search/contract-infos");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

}
