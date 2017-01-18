package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.repository.ContractMonthlyStatDao;
import com.wondertek.cpm.repository.ContractMonthlyStatRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ContractMonthlyStatSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractMonthlyStat.
 */
@Service
@Transactional
public class ContractMonthlyStatService {
	
	private final Logger log = LoggerFactory.getLogger(ContractMonthlyStatService.class);
	
	@Inject
	private ContractMonthlyStatRepository contractMonthlyStatRepository;
	
	@Inject
	private ContractMonthlyStatSearchRepository contractMonthlyStatSearchRepository;
	
	@Inject
    private UserRepository userRepository;
	
	@Inject
	private ContractMonthlyStatDao contractMonthlyStatDao;
	
	 /**
     *  Get all the contractMonthlyStats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractMonthlyStat> findAll(Pageable pageable) {
        log.debug("Request to get all ContractMonthlyStat");
        Page<ContractMonthlyStat> result = contractMonthlyStatRepository.findAll(pageable);
        return result;
    }
    
    /**
     *  Get one contractMonthlyStats by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractMonthlyStatVo findOne(Long id) {
        log.debug("Request to get ContractMonthlyStat : {}", id);
        ContractMonthlyStatVo contractMonthlyStat = contractMonthlyStatDao.getById(id);
        return contractMonthlyStat;
    }
    
    /**
     *  Delete the  contractMonthlyStats by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractMonthlyStat : {}", id);
        contractMonthlyStatRepository.delete(id);
        contractMonthlyStatSearchRepository.delete(id);
    }
    
    /**
     * Search for the contractMonthlyStats corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractMonthlyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractMonthlyStat for query {}", query);
        Page<ContractMonthlyStat> result = contractMonthlyStatSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ContractMonthlyStatVo> getStatPage(String contractId, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<ContractMonthlyStatVo> page = contractMonthlyStatDao.getUserPage(contractId, pageable, user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ContractMonthlyStatVo>(), pageable, 0);
    	}
    }
    
    /**
     * 查询用户的所有项目，管理人员能看到部门下面所有人员的项目信息
     */
    @Transactional(readOnly = true)
	public List<LongValue> queryUserContract() {
    	List<LongValue> returnList = new ArrayList<LongValue>();
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = contractMonthlyStatDao.queryUserContract(user,deptInfo);
    	}
		return returnList;
	}
}
