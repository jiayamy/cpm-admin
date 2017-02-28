package com.wondertek.cpm.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ShareCostRate;
import com.wondertek.cpm.repository.ShareCostRateDao;
import com.wondertek.cpm.repository.ShareCostRateRepository;

/**
 * Service Implementation for managing ShareCostRate.
 */
@Service
@Transactional
public class ShareCostRateService {

    private final Logger log = LoggerFactory.getLogger(ShareCostRateService.class);
    
    @Inject
    private ShareCostRateRepository shareCostRateRepository;
    @Inject
    private ShareCostRateDao shareCostRateDao;
    
    public ShareCostRate save(ShareCostRate shareCostRate) {
        log.debug("Request to save ShareCostRate : {}", shareCostRate);
        ShareCostRate result = shareCostRateRepository.save(shareCostRate);
        return result;
    }
    @Transactional(readOnly = true) 
    public ShareCostRate findOne(Long id) {
        log.debug("Request to get ShareCostRate : {}", id);
        ShareCostRate salesBonus = shareCostRateRepository.findOne(id);
        return salesBonus;
    }
    /**
     * 列表页
     * @return
     */
    @Transactional(readOnly = true)
	public Page<ShareCostRate> getUserPage(ShareCostRate shareCostRate, Pageable pageable) {
    	return shareCostRateDao.getUserPage(shareCostRate, pageable);
	}
    @Transactional(readOnly = true)
	public void delete(Long id) {
		log.debug("Request to delete ShareCostRate : {}", id);
		shareCostRateRepository.delete(id);
		
	}
}
