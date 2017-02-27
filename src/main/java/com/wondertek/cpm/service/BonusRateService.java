package com.wondertek.cpm.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.vo.BonusRateVo;
import com.wondertek.cpm.repository.BonusRateDao;
import com.wondertek.cpm.repository.BonusRateRepository;

/**
 * Service Implementation for managing BonusRate.
 */
@Service
@Transactional
public class BonusRateService {

    private final Logger log = LoggerFactory.getLogger(BonusRateService.class);
    
    @Inject
    private BonusRateRepository bonusRateRepository;
    @Inject
    private BonusRateDao bonusRateDao;
    
    public BonusRate save(BonusRate bonusRate) {
        log.debug("Request to save BonusRate : {}", bonusRate);
        BonusRate result = bonusRateRepository.save(bonusRate);
        return result;
    }
    @Transactional(readOnly = true) 
    public BonusRate findOne(Long id) {
        log.debug("Request to get BonusRate : {}", id);
        BonusRate salesBonus = bonusRateRepository.findOne(id);
        return salesBonus;
    }
    /**
     * 列表页
     * @return
     */
    @Transactional(readOnly = true)
	public Page<BonusRateVo> getUserPage(BonusRate bonusRate,Pageable pageable) {
    	return bonusRateDao.getUserPage(bonusRate, pageable);
	}
	public BonusRate getBonusRate(Long id) {
		return bonusRateRepository.findOne(id);
	}
	public void delete(Long id) {
		log.debug("Request to delete BonusRate : {}", id);
        bonusRateRepository.delete(id);
	}
}
