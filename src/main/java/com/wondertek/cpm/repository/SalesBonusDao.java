package com.wondertek.cpm.repository;
import java.util.List;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SalesBonusVo;

public interface SalesBonusDao extends GenericDao<SalesBonus, Long> {
	/**
	 * 一个销售的数据放在一起
	 * @return
	 */
	List<SalesBonusVo> getUserPage(User user, DeptInfo deptInfo, SalesBonus salesBonus);

}
