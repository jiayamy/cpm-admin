package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractWeeklyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractWeeklyStatDao extends GenericDao<ContractWeeklyStat, Long>{
	
	public Page<ContractWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
	
	public Page<ContractWeeklyStatVo> getUserPage(String contractId, Pageable pageable,User user);
	
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo);
	
	public ContractWeeklyStatVo getById(Long id);
}
