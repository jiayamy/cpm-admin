package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.search.ProjectInfoSearchRepository;
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
 * Service Implementation for managing ProjectInfo.
 */
@Service
@Transactional
public class ProjectInfoService {

    private final Logger log = LoggerFactory.getLogger(ProjectInfoService.class);
    
    @Inject
    private ProjectInfoRepository projectInfoRepository;

    @Inject
    private ProjectInfoSearchRepository projectInfoSearchRepository;

    /**
     * Save a projectInfo.
     *
     * @param projectInfo the entity to save
     * @return the persisted entity
     */
    public ProjectInfo save(ProjectInfo projectInfo) {
        log.debug("Request to save ProjectInfo : {}", projectInfo);
        ProjectInfo result = projectInfoRepository.save(projectInfo);
        projectInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectInfos");
        Page<ProjectInfo> result = projectInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectInfo findOne(Long id) {
        log.debug("Request to get ProjectInfo : {}", id);
        ProjectInfo projectInfo = projectInfoRepository.findOne(id);
        return projectInfo;
    }

    /**
     *  Delete the  projectInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectInfo : {}", id);
        projectInfoRepository.delete(id);
        projectInfoSearchRepository.delete(id);
    }

    /**
     * Search for the projectInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectInfos for query {}", query);
        Page<ProjectInfo> result = projectInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
