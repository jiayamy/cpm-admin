package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectOverall;

public interface ProjectOverallRepository extends JpaRepository<ProjectOverall,Long>{
	
	@Query(" from ProjectOverall where contractId = ?1 and statWeek = ?2")
	ProjectOverall findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProjectOverall where statWeek = ?1")
	List<ProjectOverall> findByStatWeek(Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProjectOverall po where po.statWeek = ?1")
	void deleteByStatWeek(Long statWeek);
}
