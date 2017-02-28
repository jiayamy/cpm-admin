package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ShareCostRate;

public interface ShareCostRateRepository extends JpaRepository<ShareCostRate,Long>{
	
	@Query(" from ShareCostRate where contractType = ?1 and deptType = ?2")
	ShareCostRate findByContactTypeAndDeptType(Integer contractType,
			Long deptType);

}
