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

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SaleWeeklyStatVo;
import com.wondertek.cpm.repository.SaleWeeklyStatDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing SaleWeeklyStat.
 */
@Service
@Transactional
public class SaleWeeklyStatService {
	
	private final Logger log = LoggerFactory.getLogger(SaleWeeklyStatService.class);
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SaleWeeklyStatDao saleWeeklyStatDao;
	
	/**
     *  Get one saleWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public SaleWeeklyStatVo findOne(Long id) {
        log.debug("Request to get SaleWeeklyStat : {}", id);
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
	        SaleWeeklyStatVo saleWeeklyStat = saleWeeklyStatDao.getById(id , user, deptInfo);
	        return saleWeeklyStat;
		}
		return null;
    }
    
    /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SaleWeeklyStatVo> getStatPage(String deptId, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<SaleWeeklyStatVo> page = saleWeeklyStatDao.getUserPage(deptId, pageable, user, deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<SaleWeeklyStatVo>(), pageable, 0);
    	}
    }

}
