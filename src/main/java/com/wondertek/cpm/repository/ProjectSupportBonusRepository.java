package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectSupportBonus;

public interface ProjectSupportBonusRepository extends JpaRepository<ProjectSupportBonus,Long>{
	
	@Query(" from ProjectSupportBonus where contractId = ?1 and statWeek = ?2")
	List<ProjectSupportBonus> findByContractIdAndStatWeek(Long contractId, Long statWeek);
}
