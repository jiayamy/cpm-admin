package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.repository.ContractBudgetRepository;
import com.wondertek.cpm.repository.search.ContractBudgetSearchRepository;
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
 * Service Implementation for managing ContractBudget.
 */
@Service
@Transactional
public class ContractBudgetService {

    private final Logger log = LoggerFactory.getLogger(ContractBudgetService.class);
    
    @Inject
    private ContractBudgetRepository contractBudgetRepository;

    @Inject
    private ContractBudgetSearchRepository contractBudgetSearchRepository;

    /**
     * Save a contractBudget.
     *
     * @param contractBudget the entity to save
     * @return the persisted entity
     */
    public ContractBudget save(ContractBudget contractBudget) {
        log.debug("Request to save ContractBudget : {}", contractBudget);
        ContractBudget result = contractBudgetRepository.save(contractBudget);
        contractBudgetSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractBudgets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractBudget> findAll(Pageable pageable) {
        log.debug("Request to get all ContractBudgets");
        Page<ContractBudget> result = contractBudgetRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractBudget by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractBudget findOne(Long id) {
        log.debug("Request to get ContractBudget : {}", id);
        ContractBudget contractBudget = contractBudgetRepository.findOne(id);
        return contractBudget;
    }

    /**
     *  Delete the  contractBudget by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractBudget : {}", id);
        contractBudgetRepository.delete(id);
        contractBudgetSearchRepository.delete(id);
    }

    /**
     * Search for the contractBudget corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractBudget> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractBudgets for query {}", query);
        Page<ContractBudget> result = contractBudgetSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
