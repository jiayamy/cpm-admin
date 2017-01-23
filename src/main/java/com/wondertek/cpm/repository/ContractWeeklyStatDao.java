package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractWeeklyStatVo;

public interface ContractWeeklyStatDao extends GenericDao<ContractWeeklyStat, Long>{
	
	public Page<ContractWeeklyStatVo> getUserPage(String contractId, Pageable pageable,User user, DeptInfo deptInfo);
	
	public ContractWeeklyStatVo getById(Long id, User user, DeptInfo deptInfo);
	
	public ContractWeeklyStatVo getByStatWeekAndContractId(Long statWeek, Long contractId, User user, DeptInfo deptInfo);
}
