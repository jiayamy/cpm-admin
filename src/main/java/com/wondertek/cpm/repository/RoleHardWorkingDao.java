package com.wondertek.cpm.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.RoleHardWorking;

public interface RoleHardWorkingDao extends GenericDao<RoleHardWorking, Long>{

	Page<RoleHardWorking> getPageByParams(RoleHardWorking roleHardWorking,Pageable pageable);

	RoleHardWorking getByOriginMonthAndUserId(Long originMonth, Long userId);
	
}
