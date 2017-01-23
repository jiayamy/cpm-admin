package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.repository.DeptTypeRepository;
import com.wondertek.cpm.repository.search.DeptTypeSearchRepository;

/**
 * Service Implementation for managing DeptType.
 */
@Service
@Transactional
public class DeptTypeService {

    private final Logger log = LoggerFactory.getLogger(DeptTypeService.class);
    
    @Inject
    private DeptTypeRepository deptTypeRepository;

    @Inject
    private DeptTypeSearchRepository deptTypeSearchRepository;

    /**
     * Save a deptType.
     *
     * @param deptType the entity to save
     * @return the persisted entity
     */
    public DeptType save(DeptType deptType) {
        log.debug("Request to save DeptType : {}", deptType);
        DeptType result = deptTypeRepository.save(deptType);
        deptTypeSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the deptTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<DeptType> findAll(Pageable pageable) {
        log.debug("Request to get all DeptTypes");
        Page<DeptType> result = deptTypeRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one deptType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DeptType findOne(Long id) {
        log.debug("Request to get DeptType : {}", id);
        DeptType deptType = deptTypeRepository.findOne(id);
        return deptType;
    }

    /**
     *  Delete the  deptType by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DeptType : {}", id);
        deptTypeRepository.delete(id);
        deptTypeSearchRepository.delete(id);
    }

    /**
     * Search for the deptType corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DeptType> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DeptTypes for query {}", query);
        Page<DeptType> result = deptTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
