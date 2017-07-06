package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectSupportBonus;

public interface ProjectSupportBonusRepository extends JpaRepository<ProjectSupportBonus,Long>{
	
	@Query("select sum(currentBonus) from ProjectSupportBonus where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	Double findSumCurrentBonusByContractIdAndDeptTypeAndStatWeek(Long contractId,Long deptType, Long statWeek);
	
	@Query("select sum(currentBonus) from ProjectSupportBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProjectSupportBonus psb where psb.contractId = ?1 and psb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
