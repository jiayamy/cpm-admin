package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractMonthlyStatDao extends GenericDao<ContractMonthlyStat, Long> {
	
	public Page<ContractMonthlyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
	
	public Page<ContractMonthlyStatVo> getUserPage(String contractId, Pageable pageable,User user);
	
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo);
	
	public ContractMonthlyStatVo getById(Long id);
}
