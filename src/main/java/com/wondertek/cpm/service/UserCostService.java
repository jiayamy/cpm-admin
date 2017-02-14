package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.vo.UserCostVo;
import com.wondertek.cpm.repository.UserCostDao;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing UserCost.
 */
@Service
@Transactional
public class UserCostService {

    private final Logger log = LoggerFactory.getLogger(UserCostService.class);
    
    @Inject
    private UserCostRepository userCostRepository;

//    @Inject
//    private UserCostSearchRepository userCostSearchRepository;
    
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
//        userCostSearchRepository.save(result);
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
        UserCost userCost = userCostRepository.findOne(id);
        if(userCost != null){
        	userCost.setStatus(CpmConstants.STATUS_DELETED);
        	userCost.setUpdateTime(ZonedDateTime.now());
        	userCost.setUpdator(SecurityUtils.getCurrentUserLogin());
        	userCostRepository.save(userCost);
        }
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
        Page<UserCost> result = null;
//        Page<UserCost> result = userCostSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    /**
     * 加载员工成本列表页面
     * @param userCost
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UserCostVo> getUserCostPage(UserCost userCost,Pageable pageable){
    	log.debug("Request to a page of UserCosts {}",userCost);
    	Page<UserCostVo> page = userCostDao.getUserCostPage(userCost, pageable);
    	return page;
    }
    
    /**
     * 根据用户Id和所属年月查找员工成本信息
     * @return
     */
    public UserCost findByUserIdAndCostMonth(Long userId,Long costMonth){
    	log.debug("Request to get UserCost by userId and costMonth {}",userId+"-"+costMonth);
    	UserCost userCost = userCostRepository.findByUserIdAndCostMonth(userId,costMonth);
    	return userCost;
    }

    public void saveOrUpdateUploadRecord(List<UserCost> userCosts) {
		if(userCosts != null){
			//记录中只有createTime和updateTime没填充
			for(UserCost uc: userCosts){
				UserCost old =  userCostRepository.findByUserIdAndCostMonth(uc.getUserId(), uc.getCostMonth());
				if(old != null){
					uc.setId(old.getId());
					uc.setCreateTime(old.getCreateTime());
					uc.setCreator(old.getCreator());
				}else{
					uc.setCreateTime(ZonedDateTime.now());
				}
				uc.setUpdateTime(ZonedDateTime.now());
			}
			userCostRepository.save(userCosts);
		}
	}
}
