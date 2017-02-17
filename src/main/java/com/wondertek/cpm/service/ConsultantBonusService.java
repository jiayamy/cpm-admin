package com.wondertek.cpm.service;

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

import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;
import com.wondertek.cpm.repository.ConsultantBonusDao;
import com.wondertek.cpm.repository.ConsultantBonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ConsultantBonus.
 */
@Service
@Transactional
public class ConsultantBonusService {

	private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatService.class);
	
	@Inject
	private ConsultantBonusRepository consultantBonusRepository;
	
	@Inject
	private ConsultantBonusDao consultantBonusDao;
	
	@Inject
	private UserRepository userRepository;
	
	 /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
	public Page<ConsultantBonusVo> getConsultantBonusPage(String contractId,String fromDate,String toDate,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
//			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ConsultantBonusVo> page = consultantBonusDao.getUserPage(contractId,fromDate,toDate,pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ConsultantBonusVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public ConsultantsBonus findOne(Long id){
    	return consultantBonusRepository.findOne(id);
    }
    
    /**
     * 查询某合同的统计记录
     * @param contractId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
//			Object[] o = objs.get(0);
//			User user = (User)o[0];
//			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ConsultantBonusVo> page = consultantBonusDao.getConsultantBonusRecordPage(contractId,pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ConsultantBonusVo>(), pageable, 0);
    	}
    }
}
