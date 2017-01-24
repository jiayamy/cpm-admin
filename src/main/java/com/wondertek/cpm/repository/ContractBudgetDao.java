package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractBudgetDao extends GenericDao<ContractBudget, Long> {

	Page<ContractBudgetVo> getPageByParams(ContractBudget contractBudget,User user,DeptInfo deptInfo,Pageable pageable);

	Boolean checkBudgetExit(ContractBudget contractBudget);

	List<LongValue> queryUserContract(User user, DeptInfo deptInfo);

	List<LongValue> queryUserContractBudget(User user, DeptInfo deptInfo,
			Long contractId);

	ContractBudgetVo getUserBudget(Long id, User user, DeptInfo deptInfo);

}
