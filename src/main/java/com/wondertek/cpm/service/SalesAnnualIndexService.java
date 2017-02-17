package com.wondertek.cpm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.SalesAnnualIndex;
import com.wondertek.cpm.repository.SalesAnnualIndexRepository;

/**
 * Service Implementation for managing ProjectInfo.
 */
@Service
@Transactional
public class SalesAnnualIndexService {

    private final Logger log = LoggerFactory.getLogger(SalesAnnualIndexService.class);
    
    @Inject
    private SalesAnnualIndexRepository salesAnnualIndexRepository;
    
    public SalesAnnualIndex save(SalesAnnualIndex salesAnnualIndex) {
        log.debug("Request to save ProjectInfo : {}", salesAnnualIndex);
        SalesAnnualIndex result = salesAnnualIndexRepository.save(salesAnnualIndex);
        return result;
    }
    @Transactional(readOnly = true) 
    public SalesAnnualIndex findOne(Long id) {
        log.debug("Request to get SalesBonus : {}", id);
        SalesAnnualIndex salesAnnualIndex = salesAnnualIndexRepository.findOne(id);
        return salesAnnualIndex;
    }
    /**
     * 获取某年所有销售的年指标
     * @return
     */
    @Transactional(readOnly = true) 
    public Map<Long,Double> getAnnualIndexByStatYear(Long statYear){
    	List<SalesAnnualIndex> list = salesAnnualIndexRepository.findByStatYear(statYear);
    	
    	Map<Long,Double> returnMap = new HashMap<Long,Double>();
    	if(list != null){
    		for(SalesAnnualIndex salesAnnualIndex : list){
    			returnMap.put(salesAnnualIndex.getUserId(), salesAnnualIndex.getAnnualIndex());
    		}
    	}
    	return returnMap;
    }
}
