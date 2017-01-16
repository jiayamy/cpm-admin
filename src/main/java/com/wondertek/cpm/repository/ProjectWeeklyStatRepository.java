package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectWeeklyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectWeeklyStat entity.
 */
public interface ProjectWeeklyStatRepository extends JpaRepository<ProjectWeeklyStat,Long> {
	
	@Query(" from ProjectWeeklyStat where statWeek = ?1 and projectId = ?2")
	List<ProjectWeeklyStat> findByStatWeekAndProjectId(Long statWeek, Long projectId);
}
