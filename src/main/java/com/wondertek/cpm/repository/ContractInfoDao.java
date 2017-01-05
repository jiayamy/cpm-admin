package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractInfo;

public interface ContractInfoDao extends GenericDao<ContractInfo, Long>{
	/**
	 * 获取用户筛选的列表
	 * @param contractInfo
	 * @param pageable
	 * @return Page<ContractInfo>
	 */
	public Page<ContractInfo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable);
	
}
