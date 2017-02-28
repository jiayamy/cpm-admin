package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.vo.ProjectSupportCostVo;

public interface ProjectSupportCostDao extends GenericDao<ProjectSupportCost, Long> {

	public List<ProjectSupportCostVo> getAllSalePurchaseInternalPage(Long contractId,Long userId,Long statWeek,Long deptType);
	
	public List<ProjectSupportCostVo> getAllSalePurchaseInternalList(Long contractId,Long userId,Long statWeek,Long deptType);
	
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalDetailPage(Long userId,Long statWeek,Pageable pageable);
}
