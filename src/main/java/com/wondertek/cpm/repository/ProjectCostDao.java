package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectCostVo;

public interface ProjectCostDao extends GenericDao<ProjectCost, Long> {
	/**
	 * 项目经理查看的项目成本
	 * @return
	 */
	Page<ProjectCostVo> getUserPage(ProjectCost projectCost, User user, DeptInfo deptInfo, Pageable pageable);
	/**
	 * 获取用户权限下的项目成本
	 */
	ProjectCostVo getProjectCost(User user, DeptInfo deptInfo, Long id);
}
