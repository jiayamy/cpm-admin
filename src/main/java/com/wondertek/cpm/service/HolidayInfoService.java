package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.repository.HolidayInfoRepository;
import com.wondertek.cpm.repository.search.HolidayInfoSearchRepository;

/**
 * Service Implementation for managing HolidayInfo.
 */
@Service
@Transactional
public class HolidayInfoService {

    private final Logger log = LoggerFactory.getLogger(HolidayInfoService.class);
    
    @Inject
    private HolidayInfoRepository holidayInfoRepository;

    @Inject
    private HolidayInfoSearchRepository holidayInfoSearchRepository;

    /**
     * Save a holidayInfo.
     *
     * @param holidayInfo the entity to save
     * @return the persisted entity
     */
    public HolidayInfo save(HolidayInfo holidayInfo) {
        log.debug("Request to save HolidayInfo : {}", holidayInfo);
        HolidayInfo result = holidayInfoRepository.save(holidayInfo);
        holidayInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the holidayInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<HolidayInfo> findAll(Pageable pageable) {
        log.debug("Request to get all HolidayInfos");
        Page<HolidayInfo> result = holidayInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one holidayInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public HolidayInfo findOne(Long id) {
        log.debug("Request to get HolidayInfo : {}", id);
        HolidayInfo holidayInfo = holidayInfoRepository.findOne(id);
        return holidayInfo;
    }

    /**
     *  Delete the  holidayInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete HolidayInfo : {}", id);
        holidayInfoRepository.delete(id);
        holidayInfoSearchRepository.delete(id);
    }

    /**
     * Search for the holidayInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<HolidayInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of HolidayInfos for query {}", query);
        Page<HolidayInfo> result = holidayInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    /**
     * save multiple holidayInfo.
     * @param holidayInfos
     * @return the list of entities
     */
    public List<HolidayInfo> save(List<HolidayInfo> holidayInfos) {
        log.debug("Request to save HolidayInfo : {}", holidayInfos);
        List<HolidayInfo> result = holidayInfoRepository.save(holidayInfos);
        return result;
    }
    
    /**
     * Get multiple holidayInfo by currDay.
     * @param date
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public int findByCurrDay(Long date){
    	log.debug("Request to get HolidayInfo :{}", date);
    	int result = holidayInfoRepository.findByCurrDay(date);
    	return result;
    }
}
