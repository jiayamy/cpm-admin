package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.UserTimesheetRepository;
import com.wondertek.cpm.repository.search.UserTimesheetSearchRepository;
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
 * Service Implementation for managing UserTimesheet.
 */
@Service
@Transactional
public class UserTimesheetService {

    private final Logger log = LoggerFactory.getLogger(UserTimesheetService.class);
    
    @Inject
    private UserTimesheetRepository userTimesheetRepository;

    @Inject
    private UserTimesheetSearchRepository userTimesheetSearchRepository;

    /**
     * Save a userTimesheet.
     *
     * @param userTimesheet the entity to save
     * @return the persisted entity
     */
    public UserTimesheet save(UserTimesheet userTimesheet) {
        log.debug("Request to save UserTimesheet : {}", userTimesheet);
        UserTimesheet result = userTimesheetRepository.save(userTimesheet);
        userTimesheetSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the userTimesheets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<UserTimesheet> findAll(Pageable pageable) {
        log.debug("Request to get all UserTimesheets");
        Page<UserTimesheet> result = userTimesheetRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one userTimesheet by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public UserTimesheet findOne(Long id) {
        log.debug("Request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetRepository.findOne(id);
        return userTimesheet;
    }

    /**
     *  Delete the  userTimesheet by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserTimesheet : {}", id);
        userTimesheetRepository.delete(id);
        userTimesheetSearchRepository.delete(id);
    }

    /**
     * Search for the userTimesheet corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserTimesheet> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserTimesheets for query {}", query);
        Page<UserTimesheet> result = userTimesheetSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
