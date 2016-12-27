package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
import com.wondertek.cpm.repository.search.ProjectWeeklyStatSearchRepository;
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
 * Service Implementation for managing ProjectWeeklyStat.
 */
@Service
@Transactional
public class ProjectWeeklyStatService {

    private final Logger log = LoggerFactory.getLogger(ProjectWeeklyStatService.class);
    
    @Inject
    private ProjectWeeklyStatRepository projectWeeklyStatRepository;

    @Inject
    private ProjectWeeklyStatSearchRepository projectWeeklyStatSearchRepository;

    /**
     * Save a projectWeeklyStat.
     *
     * @param projectWeeklyStat the entity to save
     * @return the persisted entity
     */
    public ProjectWeeklyStat save(ProjectWeeklyStat projectWeeklyStat) {
        log.debug("Request to save ProjectWeeklyStat : {}", projectWeeklyStat);
        ProjectWeeklyStat result = projectWeeklyStatRepository.save(projectWeeklyStat);
        projectWeeklyStatSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectWeeklyStats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectWeeklyStat> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectWeeklyStats");
        Page<ProjectWeeklyStat> result = projectWeeklyStatRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectWeeklyStat findOne(Long id) {
        log.debug("Request to get ProjectWeeklyStat : {}", id);
        ProjectWeeklyStat projectWeeklyStat = projectWeeklyStatRepository.findOne(id);
        return projectWeeklyStat;
    }

    /**
     *  Delete the  projectWeeklyStat by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectWeeklyStat : {}", id);
        projectWeeklyStatRepository.delete(id);
        projectWeeklyStatSearchRepository.delete(id);
    }

    /**
     * Search for the projectWeeklyStat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectWeeklyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectWeeklyStats for query {}", query);
        Page<ProjectWeeklyStat> result = projectWeeklyStatSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
