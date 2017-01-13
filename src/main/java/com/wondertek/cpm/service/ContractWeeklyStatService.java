package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.repository.ContractWeeklyStatDao;
import com.wondertek.cpm.repository.ContractWeeklyStatRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ContractWeeklyStatSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractWeeklyStat.
 */
@Service
@Transactional
public class ContractWeeklyStatService {

    private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatService.class);
    
    @Inject
    private ContractWeeklyStatRepository contractWeeklyStatRepository;

    @Inject
    private ContractWeeklyStatSearchRepository contractWeeklyStatSearchRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ContractWeeklyStatDao contractWeeklyStatDao;

    /**
     * Save a contractWeeklyStat.
     *
     * @param contractWeeklyStat the entity to save
     * @return the persisted entity
     */
    public ContractWeeklyStat save(ContractWeeklyStat contractWeeklyStat) {
        log.debug("Request to save ContractWeeklyStat : {}", contractWeeklyStat);
        ContractWeeklyStat result = contractWeeklyStatRepository.save(contractWeeklyStat);
        contractWeeklyStatSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractWeeklyStats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractWeeklyStat> findAll(Pageable pageable) {
        log.debug("Request to get all ContractWeeklyStats");
        Page<ContractWeeklyStat> result = contractWeeklyStatRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractWeeklyStat findOne(Long id) {
        log.debug("Request to get ContractWeeklyStat : {}", id);
        ContractWeeklyStat contractWeeklyStat = contractWeeklyStatRepository.findOne(id);
        return contractWeeklyStat;
    }

    /**
     *  Delete the  contractWeeklyStat by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractWeeklyStat : {}", id);
        contractWeeklyStatRepository.delete(id);
        contractWeeklyStatSearchRepository.delete(id);
    }

    /**
     * Search for the contractWeeklyStat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractWeeklyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractWeeklyStats for query {}", query);
        Page<ContractWeeklyStat> result = contractWeeklyStatSearchRepository.search(queryStringQuery(query), pageable);
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
    public Page<ContractWeeklyStat> getStatPage(String fromDate, String endDate, String statDate, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<ContractWeeklyStat> page = contractWeeklyStatDao.getUserPage(fromDate, endDate, statDate, pageable, user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ContractWeeklyStat>(), pageable, 0);
    	}
    }
}
