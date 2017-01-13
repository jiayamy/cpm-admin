package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;

public interface ContractBudgetDao extends GenericDao<ContractBudget, Long> {

	Page<ContractBudgetVo> getPageByParams(String name, String serialNum,String budgetName,Pageable pageable);

	Boolean checkBudgetExit(ContractBudgetVo contractBudgetVo);

}
