package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.User;

public interface ContractMonthlyStatDao extends GenericDao<ContractMonthlyStat, Long> {
	
	public Page<ContractMonthlyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
}
