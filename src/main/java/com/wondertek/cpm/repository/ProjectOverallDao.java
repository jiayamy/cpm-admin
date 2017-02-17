package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;

public interface ProjectOverallDao extends GenericDao<ProjectOverall, Long> {

	public Page<ProjectOverallVo> getPageByParams(String fromDate,String toDate,String contractId,String userId,
			Pageable pageable);

	public Page<ProjectOverallVo> getPageDetai(String contractId,
			Pageable pageable);
	

}
