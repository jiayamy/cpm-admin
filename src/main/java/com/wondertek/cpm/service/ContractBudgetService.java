package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.repository.ContractBudgetDao;
import com.wondertek.cpm.repository.ContractBudgetRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ContractBudgetSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ContractBudget.
 */
@Service
@Transactional
public class ContractBudgetService {

    private final Logger log = LoggerFactory.getLogger(ContractBudgetService.class);
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ContractBudgetRepository contractBudgetRepository;
    
    @Inject
    private ContractBudgetSearchRepository contractBudgetSearchRepository;
    
    @Inject
    private ContractBudgetDao contractBudgetDao;

    /**
     * Save a contractBudget.
     *
     * @param contractBudget the entity to save
     * @return the persisted entity
     */
    public ContractBudget save(ContractBudget contractBudget) {
        log.debug("Request to save ContractBudget : {}", contractBudget);
        ContractBudget result = contractBudgetRepository.save(contractBudget);
        contractBudgetSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractBudgets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractBudget> findAll(Pageable pageable) {
        log.debug("Request to get all ContractBudgets");
        Page<ContractBudget> result = contractBudgetRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractBudget by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractBudget findOne(Long id) {
        log.debug("Request to get ContractBudget : {}", id);
        ContractBudget contractBudget = contractBudgetRepository.findOne(id);
        return contractBudget;
    }

    /**
     *  Delete the  contractBudget by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractBudget : {}", id);
        ContractBudget contractBudget = contractBudgetRepository.findOne(id);
        if(contractBudget != null){
        	contractBudget.setStatus(2);
        	contractBudget.setUpdateTime(ZonedDateTime.now());
        	contractBudget.setUpdator(SecurityUtils.getCurrentUserLogin());
        	contractBudgetRepository.save(contractBudget);
        }
    }

    /**
     * Search for the contractBudget corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractBudget> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractBudgets for query {}", query);
        Page<ContractBudget> result = contractBudgetSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public Page<ContractBudgetVo> searchPage(ContractBudget contractBudget,Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user  = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];
			
			Page<ContractBudgetVo> page = contractBudgetDao.getPageByParams(contractBudget,user,deptInfo,pageable);
			return page;
		}else {
			return new PageImpl<ContractBudgetVo>(new ArrayList<ContractBudgetVo>(),pageable,0);
		}
	}

	public ContractBudget findOneById(Long id) {
		return contractBudgetRepository.findOneById(id);
	}

	public List<LongValue> queryUserContract() {
		List<Object[]> objs = this.userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());;
		List<LongValue> returnList = new ArrayList<LongValue>();
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];
			
			returnList = contractBudgetDao.queryUserContract(user,deptInfo);
		}
		return returnList;
	}

	public Boolean checkByBudget(ContractBudget contractBudget) {
		return contractBudgetDao.checkBudgetExit(contractBudget);
	}

	public List<LongValue> queryUserContractBudget(Long contractId) {
		List<Object[]> objs = this.userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());;
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = contractBudgetDao.queryUserContractBudget(user,deptInfo,contractId);
    	}
		return returnList;
	}

}
