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
	
	@Query(" from ProjectMonthlyStat where statWeek >= ?1 and statWeek <= ?2 and projectId = ?3")
	List<ProjectMonthlyStat> findByStatWeekAndProjectId(Long beginWeek, Long endWeek, Long projectId);
	
	@Query(" from ProjectMonthlyStat where id in (select max(id) from ProjectMonthlyStat where projectId = ?1 group by projectId )")
	ProjectMonthlyStat findMaxByProjectId(Long projectId);
}
