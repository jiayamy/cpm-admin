package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.OutsourcingUserVo;
import com.wondertek.cpm.repository.OutsourcingUserDao;
import com.wondertek.cpm.repository.OutsourcingUserRepository;
import com.wondertek.cpm.repository.UserRepository;

/**
 * Service Implementation for managing ContractInfo.
 */
@Service
@Transactional
public class OutsourcingUserService {

    private final Logger log = LoggerFactory.getLogger(OutsourcingUserService.class);
    
    @Inject
    private UserRepository userRepository;
    @Inject
    private OutsourcingUserRepository outsourcingUserRepository;
    @Inject
    private OutsourcingUserDao outsourcingUserDao;

	public List<OutsourcingUser> searchUserList(Long contractId) {
		return outsourcingUserRepository.findByContractId(contractId);
	}

	public OutsourcingUser save(OutsourcingUser outsourcingUser) {
		OutsourcingUser result = outsourcingUserRepository.save(outsourcingUser);
		return result;
	}

	public OutsourcingUserVo findById(Long id) {
			return outsourcingUserDao.findById(id);
	}

	public List<OutsourcingUser> getUserList(String mark,Long contractId) {
		return outsourcingUserRepository.findByMarkOrContractId(mark,contractId);
	}

	public List<LongValue> queryUserRank(Long contractId) {
		List<LongValue> returnList = new ArrayList<LongValue>();
    	returnList = outsourcingUserDao.queryUserRank(contractId);
    	return returnList;
    }
}
