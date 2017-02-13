package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.BonusRate;

public interface BonusRateRepository extends JpaRepository<BonusRate,Long>{
	
	@Query(" from BonusRate where deptType = ?1")
	BonusRate findByDeptType(Long deptType);
	
	@Query(" from BonusRate wbr, DeptInfo wdi where wdi.type=wbr.deptType and wdi.id=?1")
	BonusRate findByDeptId(Long deptId);
}
