package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.search.DeptInfoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Service Implementation for managing DeptInfo.
 */
@Service
@Transactional
public class DeptInfoService {

    private final Logger log = LoggerFactory.getLogger(DeptInfoService.class);
    
    @Inject
    private DeptInfoRepository deptInfoRepository;

    @Inject
    private DeptInfoSearchRepository deptInfoSearchRepository;

    /**
     * Save a deptInfo.
     *
     * @param deptInfo the entity to save
     * @return the persisted entity
     */
    public DeptInfo save(DeptInfo deptInfo) {
        log.debug("Request to save DeptInfo : {}", deptInfo);
        DeptInfo result = deptInfoRepository.save(deptInfo);
        deptInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the deptInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<DeptInfo> findAll(Pageable pageable) {
        log.debug("Request to get all DeptInfos");
        Page<DeptInfo> result = deptInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one deptInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DeptInfo findOne(Long id) {
        log.debug("Request to get DeptInfo : {}", id);
        DeptInfo deptInfo = deptInfoRepository.findOne(id);
        return deptInfo;
    }

    /**
     *  Delete the  deptInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DeptInfo : {}", id);
        deptInfoRepository.delete(id);
        deptInfoSearchRepository.delete(id);
    }

    /**
     * Search for the deptInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DeptInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DeptInfos for query {}", query);
        Page<DeptInfo> result = deptInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
