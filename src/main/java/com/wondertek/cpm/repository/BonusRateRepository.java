package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.BonusRate;

public interface BonusRateRepository extends JpaRepository<BonusRate,Long>{
	
	@Query(" from BonusRate where deptType = ?1 and contractType = ?2")
	BonusRate findByDeptTypeAndContractType(Long deptType, Integer contractType);
	
	@Query(" from BonusRate wbr, DeptInfo wdi where wdi.type=wbr.deptType and wdi.id=?1 and wbr.contractType = ?2")
	BonusRate findByDeptIdAndContractType(Long deptId, Integer contractType);
}
