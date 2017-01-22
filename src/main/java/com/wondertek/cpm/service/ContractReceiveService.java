package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractReceiveVo;
import com.wondertek.cpm.repository.ContractReceiveDao;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ContractReceiveSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractReceive.
 */
@Service
@Transactional
public class ContractReceiveService {

    private final Logger log = LoggerFactory.getLogger(ContractReceiveService.class);
    @Inject
    private ContractReceiveRepository contractReceiveRepository;

    @Inject
    private ContractReceiveSearchRepository contractReceiveSearchRepository;
    @Inject
    private ContractReceiveDao contractReceiveDao;
    @Inject
    private UserRepository userRepository;

    /**
     * Save a contractReceive.
     *
     * @param contractReceive the entity to save
     * @return the persisted entity
     */
    public ContractReceive save(ContractReceive contractReceive) {
        log.debug("Request to save ContractReceive : {}", contractReceive);
        ContractReceive result = contractReceiveRepository.save(contractReceive);
       // contractReceiveSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the contractReceives.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractReceive> findAll(Pageable pageable) {
        log.debug("Request to get all ContractReceives");
        Page<ContractReceive> result = contractReceiveRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractReceive by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractReceive findOne(Long id) {
        log.debug("Request to get ContractReceive : {}", id);
        ContractReceive contractReceive = contractReceiveRepository.findOne(id);
        return contractReceive;
    }

    /**
     *  Delete the  contractReceive by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractReceive : {}", id);
        ContractReceive contractReceive = contractReceiveRepository.findOne(id);
        contractReceive.setStatus(CpmConstants.STATUS_DELETED);
        contractReceiveRepository.save(contractReceive);
    }

    /**
     * Search for the contractReceive corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractReceive> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractReceives for query {}", query);
        Page<ContractReceive> result = contractReceiveSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public ContractReceiveVo getContractReceive(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[0];
			return contractReceiveDao.getContractReceive(user,deptInfo,id);
		}
		
		return null;
	}

	public Page<ContractReceiveVo> getuserPage(ContractReceive contractReceive, Pageable pageable) {
		log.debug("Request to get all contractReceive");
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];
			return contractReceiveDao.getUserPage(contractReceive,user,deptInfo,pageable);
		}
		return new PageImpl<>(new ArrayList<ContractReceiveVo>(), pageable, 0);
	}
}
