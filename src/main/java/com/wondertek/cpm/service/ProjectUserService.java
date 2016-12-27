package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.repository.ProjectUserRepository;
import com.wondertek.cpm.repository.search.ProjectUserSearchRepository;
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
 * Service Implementation for managing ProjectUser.
 */
@Service
@Transactional
public class ProjectUserService {

    private final Logger log = LoggerFactory.getLogger(ProjectUserService.class);
    
    @Inject
    private ProjectUserRepository projectUserRepository;

    @Inject
    private ProjectUserSearchRepository projectUserSearchRepository;

    /**
     * Save a projectUser.
     *
     * @param projectUser the entity to save
     * @return the persisted entity
     */
    public ProjectUser save(ProjectUser projectUser) {
        log.debug("Request to save ProjectUser : {}", projectUser);
        ProjectUser result = projectUserRepository.save(projectUser);
        projectUserSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectUsers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectUser> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectUsers");
        Page<ProjectUser> result = projectUserRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectUser by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectUser findOne(Long id) {
        log.debug("Request to get ProjectUser : {}", id);
        ProjectUser projectUser = projectUserRepository.findOne(id);
        return projectUser;
    }

    /**
     *  Delete the  projectUser by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectUser : {}", id);
        projectUserRepository.delete(id);
        projectUserSearchRepository.delete(id);
    }

    /**
     * Search for the projectUser corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectUser> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectUsers for query {}", query);
        Page<ProjectUser> result = projectUserSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
