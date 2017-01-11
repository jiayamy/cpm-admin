package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;

public interface ProjectMonthlyStatDao extends GenericDao<ProjectMonthlyStat, Long>{
	
	public Page<ProjectMonthlyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
}
