package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.vo.UserCostVo;

public interface UserCostDao extends GenericDao<UserCost, Long> {

	/**
	 * 获取员工成本列表
	 * @param userCost
	 * @param pageable
	 * @return
	 */
	public Page<UserCostVo> getUserCostPage(UserCost userCost,Pageable pageable);
}
