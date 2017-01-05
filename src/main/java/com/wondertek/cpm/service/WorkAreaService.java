package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.WorkArea;
import com.wondertek.cpm.repository.WorkAreaRepository;
import com.wondertek.cpm.repository.search.WorkAreaSearchRepository;

/**
 * Service Implementation for managing WorkArea.
 */
@Service
@Transactional
public class WorkAreaService {

    private final Logger log = LoggerFactory.getLogger(WorkAreaService.class);
    
    @Inject
    private WorkAreaRepository workAreaRepository;

    @Inject
    private WorkAreaSearchRepository workAreaSearchRepository;

    /**
     * Save a workArea.
     *
     * @param workArea the entity to save
     * @return the persisted entity
     */
    public WorkArea save(WorkArea workArea) {
        log.debug("Request to save WorkArea : {}", workArea);
        WorkArea result = workAreaRepository.save(workArea);
        workAreaSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the workAreas.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<WorkArea> findAll(Pageable pageable) {
        log.debug("Request to get all WorkAreas");
        Page<WorkArea> result = workAreaRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one workArea by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public WorkArea findOne(Long id) {
        log.debug("Request to get WorkArea : {}", id);
        WorkArea workArea = workAreaRepository.findOne(id);
        return workArea;
    }

    /**
     *  Delete the  workArea by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkArea : {}", id);
        workAreaRepository.delete(id);
        workAreaSearchRepository.delete(id);
    }

    /**
     * Search for the workArea corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<WorkArea> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of WorkAreas for query {}", query);
        Page<WorkArea> result = workAreaSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public List<String> queryAll() {
		List<WorkArea> list = workAreaRepository.findAll();
		List<String> areas = new ArrayList<String>();
		if(list != null){
			for(WorkArea workArea : list){
				areas.add(workArea.getName());
			}
		}
		return areas;
	}
}
