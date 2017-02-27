package com.wondertek.cpm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.SalesAnnualIndex;
import com.wondertek.cpm.repository.SalesAnnualIndexDao;
import com.wondertek.cpm.repository.SalesAnnualIndexRepository;

/**
 * Service Implementation for managing SalesAnnualIndex.
 */
@Service
@Transactional
public class SalesAnnualIndexService {

    private final Logger log = LoggerFactory.getLogger(SalesAnnualIndexService.class);
    
    @Inject
    private SalesAnnualIndexRepository salesAnnualIndexRepository;
    @Inject
    private SalesAnnualIndexDao salesAnnualIndexDao;
    
    public SalesAnnualIndex save(SalesAnnualIndex salesAnnualIndex) {
        log.debug("Request to save SalesAnnualIndex : {}", salesAnnualIndex);
        SalesAnnualIndex result = salesAnnualIndexRepository.save(salesAnnualIndex);
        return result;
    }
    @Transactional(readOnly = true) 
    public SalesAnnualIndex findOne(Long id) {
        log.debug("Request to get SalesAnnualIndex : {}", id);
        SalesAnnualIndex salesAnnualIndex = salesAnnualIndexRepository.findOne(id);
        return salesAnnualIndex;
    }
    
    public void delete(Long id) {
        log.debug("Request to delete SalesAnnualIndex : {}", id);
        salesAnnualIndexRepository.delete(id);
    }
    /**
     * 列表页
     * @return
     */
    @Transactional(readOnly = true)
	public Page<SalesAnnualIndex> getUserPage(SalesAnnualIndex salesAnnualIndex, Pageable pageable) {
    	return salesAnnualIndexDao.getUserPage(salesAnnualIndex, pageable);
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
    /**
     * 获取用户对应年份记录
     * @return
     */
	public SalesAnnualIndex findByStatYearAndUserId(Long statYear, Long userId) {
		return salesAnnualIndexRepository.findByStatYearAndUserId(statYear,userId);
	}
}
