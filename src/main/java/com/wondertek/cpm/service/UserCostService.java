package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.repository.UserCostDao;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.search.UserCostSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UserCost.
 */
@Service
@Transactional
public class UserCostService {

    private final Logger log = LoggerFactory.getLogger(UserCostService.class);
    
    @Inject
    private UserCostRepository userCostRepository;

    @Inject
    private UserCostSearchRepository userCostSearchRepository;
    
    @Autowired
    private UserCostDao userCostDao;

    /**
     * Save a userCost.
     *
     * @param userCost the entity to save
     * @return the persisted entity
     */
    public UserCost save(UserCost userCost) {
        log.debug("Request to save UserCost : {}", userCost);
        UserCost result = userCostRepository.save(userCost);
        userCostSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the userCosts.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<UserCost> findAll(Pageable pageable) {
        log.debug("Request to get all UserCosts");
        Page<UserCost> result = userCostRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one userCost by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public UserCost findOne(Long id) {
        log.debug("Request to get UserCost : {}", id);
        UserCost userCost = userCostRepository.findOne(id);
        return userCost;
    }

    /**
     *  Delete the  userCost by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserCost : {}", id);
        userCostRepository.delete(id);
        userCostSearchRepository.delete(id);
    }

    /**
     * Search for the userCost corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserCost> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserCosts for query {}", query);
        Page<UserCost> result = userCostSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    /**
     * 加载员工成本列表页面
     * @param userCost
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UserCost> getUserCostPage(UserCost userCost,Pageable pageable){
    	log.debug("Request to a page of UserCosts");
    	Page<UserCost> page = userCostDao.getUserCostPage(userCost, pageable);
    	return page;
    }
    
    public UserCost findByUserIdAndCostMonth(Long userId,Long costMonth){
    	log.debug("Request to get UserCost by userId and costMonth");
    	UserCost userCost = userCostRepository.findByUserIdAndCostMonth(userId,costMonth);
    	return userCost;
    }
}
