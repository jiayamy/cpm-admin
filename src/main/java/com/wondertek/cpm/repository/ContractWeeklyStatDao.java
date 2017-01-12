package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.User;

public interface ContractWeeklyStatDao extends GenericDao<ContractWeeklyStat, Long>{
	
	public Page<ContractWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
}
