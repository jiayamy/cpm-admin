package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;

public interface ContractBudgetDao extends GenericDao<ContractBudget, Long> {

	Page<ContractBudgetVo> getPageByParams(ContractBudget contractBudget,Pageable pageable);

	Boolean checkBudgetExit(ContractBudget contractBudget);

}
