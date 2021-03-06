package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectMonthlyStat;

/**
 * Spring Data JPA repository for the ProjectMonthlyStat entity.
 */
public interface ProjectMonthlyStatRepository extends JpaRepository<ProjectMonthlyStat,Long> {
	
	@Query(" from ProjectMonthlyStat where statWeek = ?1 and projectId = ?2")
	List<ProjectMonthlyStat> findByStatWeekAndProjectId(Long statWeek, Long projectId);
}
