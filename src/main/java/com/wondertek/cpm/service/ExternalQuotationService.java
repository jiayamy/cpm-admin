package com.wondertek.cpm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.repository.ExternalQuotationRepository;

/**
 * 外部报价，员工级别对应的成本信息
 */
@Service
@Transactional
public class ExternalQuotationService {

    private final Logger log = LoggerFactory.getLogger(ExternalQuotationService.class);
    
    @Inject
    private ExternalQuotationRepository externalQuotationRepository;
    
    /**
     * Save a ExternalQuotation.
     *
     * @param ExternalQuotation the entity to save
     * @return the persisted entity
     */
    public ExternalQuotation save(ExternalQuotation externalQuotation) {
        log.debug("Request to save ExternalQuotation : {}", externalQuotation);
        ExternalQuotation result = externalQuotationRepository.save(externalQuotation);
//        ExternalQuotationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get one ExternalQuotation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ExternalQuotation findOne(Long id) {
        log.debug("Request to get ExternalQuotation : {}", id);
        ExternalQuotation externalQuotation = externalQuotationRepository.findOne(id);
        return externalQuotation;
    }

    /**
     *  Delete the  ExternalQuotation by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ExternalQuotation : {}", id);
//        ExternalQuotation externalQuotation = externalQuotationRepository.findOne(id);
//        if(externalQuotation != null){
//        	externalQuotation.setUpdateTime(ZonedDateTime.now());
//        	externalQuotation.setUpdator(SecurityUtils.getCurrentUserLogin());
//        	externalQuotationRepository.save(externalQuotation);
//        }
    }

	public Map<Integer, ExternalQuotation> getAllInfo() {
		Map<Integer, ExternalQuotation> returnMap = new HashMap<Integer, ExternalQuotation>();
		List<ExternalQuotation> alls = externalQuotationRepository.findAll();
		if(alls != null){
			for(ExternalQuotation externalQuotation : alls){
				returnMap.put(externalQuotation.getGrade(), externalQuotation);
			}
		}
		return returnMap;
	}
    
    
}