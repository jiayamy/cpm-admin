package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.vo.UserCostVo;

public interface UserCostDao extends GenericDao<UserCost, Long> {

	/**
	 * 获取员工成本列表
	 * @return
	 */
	public Page<UserCostVo> getUserCostPage(UserCost userCost,Pageable pageable);
	/**
	 * 获取costMonth以及之前的每个用户最后一条记录
	 * @return
	 */
	public List<Object[]> findAllMaxByCostMonth(Long costMonth);
}
