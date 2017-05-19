package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.RoleHardWorking;

public interface RoleHardWorkingRepository extends JpaRepository<RoleHardWorking,Long>{
	
	@Query(" from RoleHardWorking where originMonth = ?1")
	List<RoleHardWorking> findByOriginMonth(Long nullToLong);

	@Query(" from RoleHardWorking where Id = ?1")
	RoleHardWorking findById(Long statId);

}
