package com.wondertek.cpm.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.repository.SalesBonusRepository;

/**
 * Service Implementation for managing ProjectInfo.
 */
@Service
@Transactional
public class SalesBonusService {

    private final Logger log = LoggerFactory.getLogger(SalesBonusService.class);
    
    @Inject
    private SalesBonusRepository salesBonusRepository;

    public SalesBonus save(SalesBonus salesBonus) {
        log.debug("Request to save ProjectInfo : {}", salesBonus);
        SalesBonus result = salesBonusRepository.save(salesBonus);
        return result;
    }
    @Transactional(readOnly = true) 
    public SalesBonus findOne(Long id) {
        log.debug("Request to get SalesBonus : {}", id);
        SalesBonus salesBonus = salesBonusRepository.findOne(id);
        return salesBonus;
    }
}
