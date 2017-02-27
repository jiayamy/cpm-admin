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
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;
import com.wondertek.cpm.repository.BonusDao;
import com.wondertek.cpm.repository.BonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class BonusService {
	private final Logger log = LoggerFactory.getLogger(BonusService.class);
	
	@Inject
	private BonusDao bonusDao;
	
	@Inject
	private BonusRepository bonusRepository;
	
	@Inject
	private UserRepository userRepository;
	
	public Page<BonusVo> searchPage(Bonus bonus,Pageable pageable) {
		//获取列表页
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
			Page<BonusVo> page = bonusDao.getPageByParams(user,deptInfo,bonus,pageable);
			return page;
		}else {
			return new PageImpl<BonusVo>(new ArrayList<BonusVo>(),pageable,0);
		}
			
	}

	public Bonus findOne(Long id) {
		log.debug("Request to get Bonus : {}", id);
		Bonus bonus = bonusRepository.findOne(id);
        return bonus;
	}

	public Page<BonusVo> searchPageDetail(Long contractId, Pageable pageable) {
		Page<BonusVo> page = bonusDao.getPageDetail(contractId,pageable);
		return page;
	}

}
