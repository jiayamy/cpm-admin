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

import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.repository.OverallBonusDao;
import com.wondertek.cpm.repository.OverallBonusRepository;
import com.wondertek.cpm.repository.ProjectOverallDao;
import com.wondertek.cpm.repository.ProjectOverallRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class OverallBonusService {
	private final Logger log = LoggerFactory.getLogger(OverallBonusService.class);
	
	@Inject
	private OverallBonusDao overallBonusDao;
	
	@Inject
	private OverallBonusRepository overallBonusRepository;
	
	@Inject
	private UserRepository userRepository;
	
	public Page<BonusVo> searchPage(Bonus bonus,Pageable pageable) {
		//获取列表页
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
			Page<BonusVo> page = overallBonusDao.getPageByParams(user,deptInfo,bonus,pageable);
			return page;
		}else {
			return new PageImpl<BonusVo>(new ArrayList<BonusVo>(),pageable,0);
		}
			
	}

	public Bonus findOne(Long id) {
		log.debug("Request to get Bonus : {}", id);
		Bonus bonus = overallBonusRepository.findOne(id);
        return bonus;
	}

	public Page<BonusVo> searchPageDetail(Long contractId, Pageable pageable) {
		Page<BonusVo> page = overallBonusDao.getPageDetail(contractId,pageable);
		return page;
	}

}
