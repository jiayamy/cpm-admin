package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SaleWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SaleWeeklyStatVo;

public interface SaleWeeklyStatDao extends GenericDao<SaleWeeklyStat, Long> {

	public Page<SaleWeeklyStatVo> getUserPage(String deptId,Pageable pageable,User user, DeptInfo deptInfo);
	
	public SaleWeeklyStatVo getById(Long deptId, User user, DeptInfo deptInfo);
}
