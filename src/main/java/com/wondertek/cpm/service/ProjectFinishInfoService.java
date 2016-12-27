package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.repository.search.ProjectFinishInfoSearchRepository;
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
 * Service Implementation for managing ProjectFinishInfo.
 */
@Service
@Transactional
public class ProjectFinishInfoService {

    private final Logger log = LoggerFactory.getLogger(ProjectFinishInfoService.class);
    
    @Inject
    private ProjectFinishInfoRepository projectFinishInfoRepository;

    @Inject
    private ProjectFinishInfoSearchRepository projectFinishInfoSearchRepository;

    /**
     * Save a projectFinishInfo.
     *
     * @param projectFinishInfo the entity to save
     * @return the persisted entity
     */
    public ProjectFinishInfo save(ProjectFinishInfo projectFinishInfo) {
        log.debug("Request to save ProjectFinishInfo : {}", projectFinishInfo);
        ProjectFinishInfo result = projectFinishInfoRepository.save(projectFinishInfo);
        projectFinishInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectFinishInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectFinishInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectFinishInfos");
        Page<ProjectFinishInfo> result = projectFinishInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectFinishInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectFinishInfo findOne(Long id) {
        log.debug("Request to get ProjectFinishInfo : {}", id);
        ProjectFinishInfo projectFinishInfo = projectFinishInfoRepository.findOne(id);
        return projectFinishInfo;
    }

    /**
     *  Delete the  projectFinishInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectFinishInfo : {}", id);
        projectFinishInfoRepository.delete(id);
        projectFinishInfoSearchRepository.delete(id);
    }

    /**
     * Search for the projectFinishInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectFinishInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectFinishInfos for query {}", query);
        Page<ProjectFinishInfo> result = projectFinishInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
