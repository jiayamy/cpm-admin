package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;

public interface ProjectWeeklyStatDao extends GenericDao<ProjectWeeklyStat, Long>{
	
	public Page<ProjectWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
}
