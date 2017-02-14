package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectOverall;

public interface ProjectOverallRepository extends JpaRepository<ProjectOverall,Long>{
	
	@Query(" from ProjectOverall where contractId = ?1 and statWeek = ?2")
	ProjectOverall findByContractIdAndStatWeek(Long contractId, Long statWeek);
}
