package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.repository.ContractUserRepository;
import com.wondertek.cpm.repository.search.ContractUserSearchRepository;
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
 * Service Implementation for managing ContractUser.
 */
@Service
@Transactional
public class ContractUserService {

    private final Logger log = LoggerFactory.getLogger(ContractUserService.class);
    
    @Inject
    private ContractUserRepository contractUserRepository;

    @Inject
    private ContractUserSearchRepository contractUserSearchRepository;

    /**
     * Save a contractUser.
     *
     * @param contractUser the entity to save
     * @return the persisted entity
     */
    public ContractUser save(ContractUser contractUser) {
        log.debug("Request to save ContractUser : {}", contractUser);
        ContractUser result = contractUserRepository.save(contractUser);
        contractUserSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractUsers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractUser> findAll(Pageable pageable) {
        log.debug("Request to get all ContractUsers");
        Page<ContractUser> result = contractUserRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractUser by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractUser findOne(Long id) {
        log.debug("Request to get ContractUser : {}", id);
        ContractUser contractUser = contractUserRepository.findOne(id);
        return contractUser;
    }

    /**
     *  Delete the  contractUser by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractUser : {}", id);
        contractUserRepository.delete(id);
        contractUserSearchRepository.delete(id);
    }

    /**
     * Search for the contractUser corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractUser> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractUsers for query {}", query);
        Page<ContractUser> result = contractUserSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
