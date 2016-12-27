package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.search.ProjectCostSearchRepository;
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
 * Service Implementation for managing ProjectCost.
 */
@Service
@Transactional
public class ProjectCostService {

    private final Logger log = LoggerFactory.getLogger(ProjectCostService.class);
    
    @Inject
    private ProjectCostRepository projectCostRepository;

    @Inject
    private ProjectCostSearchRepository projectCostSearchRepository;

    /**
     * Save a projectCost.
     *
     * @param projectCost the entity to save
     * @return the persisted entity
     */
    public ProjectCost save(ProjectCost projectCost) {
        log.debug("Request to save ProjectCost : {}", projectCost);
        ProjectCost result = projectCostRepository.save(projectCost);
        projectCostSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectCosts.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectCost> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectCosts");
        Page<ProjectCost> result = projectCostRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectCost by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectCost findOne(Long id) {
        log.debug("Request to get ProjectCost : {}", id);
        ProjectCost projectCost = projectCostRepository.findOne(id);
        return projectCost;
    }

    /**
     *  Delete the  projectCost by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectCost : {}", id);
        projectCostRepository.delete(id);
        projectCostSearchRepository.delete(id);
    }

    /**
     * Search for the projectCost corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectCost> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectCosts for query {}", query);
        Page<ProjectCost> result = projectCostSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
