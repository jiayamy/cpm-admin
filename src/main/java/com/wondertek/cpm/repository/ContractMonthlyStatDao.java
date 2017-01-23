package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;

public interface ContractMonthlyStatDao extends GenericDao<ContractMonthlyStat, Long> {
	
	public Page<ContractMonthlyStatVo> getUserPage(String contractId, Pageable pageable,User user, DeptInfo deptInfo);
	
	public ContractMonthlyStatVo getById(Long id,  User user, DeptInfo deptInfo);
	
	public ContractMonthlyStatVo getByStatWeekAndContractId(Long statWeek, Long contractId, User user, DeptInfo deptInfo);
}
