package com.wondertek.cpm.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.repository.ProjectOverallDao;
import com.wondertek.cpm.repository.ProjectOverallRepository;

@Service
@Transactional
public class ProjectOverallService {
	private final Logger log = LoggerFactory.getLogger(ProjectOverallService.class);
	
	@Inject
	private ProjectOverallDao projectOverallDao;
	
	@Inject
	private ProjectOverallRepository projectOverallRepository;
	
	public Page<ProjectOverallVo> searchPage(String fromDate,String toDate,String contractId,String userId,Pageable pageable) {
		Page<ProjectOverallVo> page = projectOverallDao.getPageByParams(fromDate,toDate,contractId,userId,pageable);
		return page;
	}

	public Page<ProjectOverallVo> searchPageDetail(String contractId,
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
