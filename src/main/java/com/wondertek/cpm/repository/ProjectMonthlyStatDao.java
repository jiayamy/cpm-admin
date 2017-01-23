package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;

public interface ProjectMonthlyStatDao extends GenericDao<ProjectMonthlyStat, Long>{
	
	public Page<ProjectMonthlyStatVo> getUserPage(String projectId, Pageable pageable, User user, DeptInfo deptInfo);
	
	public ProjectMonthlyStatVo getById(Long id, User user, DeptInfo deptInfo);
	
	public ProjectMonthlyStatVo getByStatWeekAndProjectId(Long statWeek, Long projectId, User user, DeptInfo deptInfo);
}
