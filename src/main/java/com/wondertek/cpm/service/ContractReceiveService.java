package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
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
import com.wondertek.cpm.repository.ContractInfoDao;
import com.wondertek.cpm.repository.ContractReceiveDao;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.UserRepository;
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

//    @Inject
//    private ContractReceiveSearchRepository contractReceiveSearchRepository;
    @Inject
    private ContractReceiveDao contractReceiveDao;
    @Inject
    private ContractInfoDao contractInfoDao;
    
    @Inject
    private UserRepository userRepository;

    @Transactional(readOnly = true) 
    public ContractReceive findOne(Long id) {
        log.debug("Request to get ContractReceive : {}", id);
        ContractReceive contractReceive = contractReceiveRepository.findOne(id);
        return contractReceive;
    }

    public void delete(Long id) {
        log.debug("Request to delete ContractReceive : {}", id);
        ContractReceive contractReceive = contractReceiveRepository.findOne(id);
        if(contractReceive != null){
        	contractReceive.setStatus(CpmConstants.STATUS_DELETED);
        	contractReceive.setUpdateTime(ZonedDateTime.now());
        	contractReceive.setUpdator(SecurityUtils.getCurrentUserLogin());
        	contractReceiveRepository.save(contractReceive);
        	
        	contractInfoDao.updateReceiveTotal(contractReceive.getContractId(),0d,contractReceive.getReceiveTotal());
        }
    }
    @Transactional(readOnly = true) 
	public ContractReceiveVo getContractReceive(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			return contractReceiveDao.getContractReceive(user,deptInfo,id);
		}
		
		return null;
	}
    @Transactional(readOnly = true) 
	public Page<ContractReceiveVo> getUserPage(ContractReceive contractReceive, Pageable pageable) {
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

	public ContractReceive save(ContractReceive contractReceive, Double oldTotal) {
		contractReceive = contractReceiveRepository.save(contractReceive);
		contractInfoDao.updateReceiveTotal(contractReceive.getContractId(),contractReceive.getReceiveTotal(),oldTotal);
		return contractReceive;
	}
}
