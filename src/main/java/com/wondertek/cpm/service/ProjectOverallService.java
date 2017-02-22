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
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.domain.vo.SalesBonusVo;
import com.wondertek.cpm.repository.ProjectOverallDao;
import com.wondertek.cpm.repository.ProjectOverallRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class ProjectOverallService {
	private final Logger log = LoggerFactory.getLogger(ProjectOverallService.class);
	
	@Inject
	private ProjectOverallDao projectOverallDao;
	
	@Inject
	private ProjectOverallRepository projectOverallRepository;
	
	@Inject
	private UserRepository userRepository;
	
	public Page<ProjectOverallVo> searchPage(ProjectOverall projectOverall,Pageable pageable) {
		//获取列表页
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
			Page<ProjectOverallVo> page = projectOverallDao.getPageByParams(user,deptInfo,projectOverall,pageable);
			return page;
		}else {
			return new PageImpl<ProjectOverallVo>(new ArrayList<ProjectOverallVo>(),pageable,0);
		}
			
	}

	public Page<ProjectOverallVo> searchPageDetail(Long contractId,
			Pageable pageable) {
		Page<ProjectOverallVo> page = projectOverallDao.getPageDetai(contractId,pageable);
		return page;
	}

	public ProjectOverall findOne(Long id) {
		log.debug("Request to get ProductPrice : {}", id);
		ProjectOverall projectOverall = projectOverallRepository.findOne(id);
        return projectOverall;
	}

}
