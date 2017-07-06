package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectOverall;

public interface ProjectOverallRepository extends JpaRepository<ProjectOverall,Long>{
	
	@Query(" from ProjectOverall where contractId = ?1 and statWeek = ?2")
	ProjectOverall findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProjectOverall po where po.contractId = ?1 and po.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
