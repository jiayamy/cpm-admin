package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractReceiveVo;

public interface ContractReceiveDao extends GenericDao<ContractReceive, Long>{
	/**
	 * 获取用户权限下的合同回款
	 */
	public ContractReceiveVo getContractReceive(User user, DeptInfo deptInfo, Long id);
	
	/**
	 * 查看合同回款
	 * @return
	 */
	public Page<ContractReceiveVo> getUserPage(ContractReceive contractReceive, User user, DeptInfo deptInfo,
			Pageable pageable);

}
