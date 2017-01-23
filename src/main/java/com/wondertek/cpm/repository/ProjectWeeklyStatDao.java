package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;

public interface ProjectWeeklyStatDao extends GenericDao<ProjectWeeklyStat, Long>{
	
	public Page<ProjectWeeklyStatVo> getUserPage(String projectId, Pageable pageable, User user, DeptInfo deptInfo);
	
	public ProjectWeeklyStatVo getById(Long id, User user, DeptInfo deptInfo);
	
	public ProjectWeeklyStatVo getByStatWeekAndProjectId(Long statWeek, Long projectId, User user, DeptInfo deptInfo);
}
