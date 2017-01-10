package com.wondertek.cpm.service;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.repository.ContractInfoDao;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.search.ContractInfoSearchRepository;
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
 * Service Implementation for managing ContractInfo.
 */
@Service
@Transactional
public class ContractInfoService {

    private final Logger log = LoggerFactory.getLogger(ContractInfoService.class);
    
    @Inject
    private ContractInfoRepository contractInfoRepository;

    @Inject
    private ContractInfoSearchRepository contractInfoSearchRepository;
    
    @Inject
    private ContractInfoDao contractInfoDao;
    /**
     * Save a contractInfo.
     *
     * @param contractInfo the entity to save
     * @return the persisted entity
     */
    public ContractInfo save(ContractInfo contractInfo) {
        log.debug("Request to save ContractInfo : {}", contractInfo);
        ContractInfo result = contractInfoRepository.save(contractInfo);
        contractInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ContractInfos");
        Page<ContractInfo> result = contractInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractInfo findOne(Long id) {
        log.debug("Request to get ContractInfo : {}", id);
        ContractInfo contractInfo = contractInfoRepository.findOne(id);
        return contractInfo;
    }

    /**
     *  Delete the  contractInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractInfo : {}", id);
        ContractInfo contractInfo = contractInfoRepository.findOne(id);
        if (contractInfo != null) {
        	contractInfo.setStatus(CpmConstants.STATUS_DELETED);
            contractInfoSearchRepository.save(contractInfo);
		}
    }

    /**
     * Search for the contractInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractInfo> search(String query, Pageable pageable) {
    	log.debug("Request to search for a page of ContractInfos for query {}", query);
        Page<ContractInfo> result = contractInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
	public Page<ContractInfo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable) {
		Page<ContractInfo> page = contractInfoDao.getContractInfoPage(contractInfo, pageable);
		return page;
	}

	

}
