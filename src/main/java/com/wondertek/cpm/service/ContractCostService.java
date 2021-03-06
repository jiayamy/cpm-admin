package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractCostVo;
import com.wondertek.cpm.repository.ContractCostDao;
import com.wondertek.cpm.repository.ContractCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractCost.
 */
@Service
@Transactional
public class ContractCostService {

    private final Logger log = LoggerFactory.getLogger(ContractCostService.class);
    
    @Inject
    private ContractCostRepository contractCostRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ContractCostDao contractCostDao;

    /**
     * Save a contractCost.
     *
     * @param contractCost the entity to save
     * @return the persisted entity
     */
    public ContractCost save(ContractCost contractCost) {
        log.debug("Request to save ContractCost : {}", contractCost);
        ContractCost result = contractCostRepository.save(contractCost);
        return result;
    }

    /**
     *  Get all the contractCosts.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractCost> findAll(Pageable pageable) {
        log.debug("Request to get all ContractCosts");
        Page<ContractCost> result = contractCostRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractCost by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractCost findOne(Long id) {
        log.debug("Request to get ContractCost : {}", id);
        ContractCost contractCost = contractCostRepository.findOne(id);
        return contractCost;
    }

    /**
     *  Delete the  contractCost by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractCost : {}", id);
        ContractCost contractCost = contractCostRepository.findOne(id);
        if (contractCost != null) {
			contractCost.setStatus(CpmConstants.STATUS_DELETED);
			contractCost.setUpdateTime(ZonedDateTime.now());
			contractCost.setUpdator(SecurityUtils.getCurrentUserLogin());
			contractCostRepository.save(contractCost);
		}
    }

    /**
     * Search for the contractCost corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractCost> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractCosts for query {}", query);
        Page<ContractCost> result = null;
        return result;
    }
    /**
     * 查询列表
     * @param pageType 
     * @return
     */
	public Page<ContractCostVo> getUserPage(ContractCost contractCost, Integer pageType, Pageable pageable) {
		log.debug("Request to get all contractCosts");
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			return contractCostDao.getUserPage(contractCost,pageType,user,deptInfo,pageable);
		}
		
		return new PageImpl<>(new ArrayList<ContractCostVo>(), pageable, 0);
	}

	public ContractCostVo getContractCost(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return contractCostDao.getContractCost(user,deptInfo,id);
    	}
    	return null;
	}

	public ContractCost getNewContratCost(ContractCost contractCost) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		contractCost.setDept(deptInfo.getName());
    		contractCost.setDeptId(deptInfo.getId());
    		return contractCost;
    	}
		
		return null;
	}
	
	public void saveOrUpdateUploadRecord(List<ContractCost> contractCosts){
		if(contractCosts != null){
			contractCostRepository.save(contractCosts);
		}
	}
}
