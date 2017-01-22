package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractCostVo;

public interface ContractCostDao extends GenericDao<ContractCost, Long>{
	/**
	 * 查看合同成本
	 * @return
	 */
	Page<ContractCostVo> getUserPage(ContractCost contractCost, User user, DeptInfo deptInfo, Pageable pageable);
	/**
	 * 获取用户权限下的合同成本
	 */
	ContractCostVo getContractCost(User user, DeptInfo deptInfo, Long id);

}
