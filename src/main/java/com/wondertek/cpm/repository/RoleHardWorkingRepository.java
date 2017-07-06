package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.RoleHardWorking;

public interface RoleHardWorkingRepository extends JpaRepository<RoleHardWorking,Long>{

	@Modifying
	@Transactional
	@Query("delete from RoleHardWorking rhw where rhw.originMonth = ?1")
	void deleteAllByOriginMonth(Long originMonth);
	

}
