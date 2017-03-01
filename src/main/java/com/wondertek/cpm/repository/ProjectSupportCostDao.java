package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectSupportCostVo;

public interface ProjectSupportCostDao extends GenericDao<ProjectSupportCost, Long> {

	public List<ProjectSupportCostVo> getAllSalePurchaseInternalPage(User user,DeptInfo deptInfo,ProjectSupportCost projectSupportCost);
	
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalDetailPage(User user,DeptInfo deptInfo,ProjectSupportCost projectSupportCost,Pageable pageable);
}
