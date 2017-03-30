package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectCost;

/**
 * Spring Data JPA repository for the ProjectCost entity.
 */
public interface ProjectCostRepository extends JpaRepository<ProjectCost,Long> {
	
	@Query("from ProjectCost where projectId = ?1 and type=?2 and status=1")
	List<ProjectCost> findByProjectIdAndType(Long projectId, Integer type);
	
	@Query("from ProjectCost where projectId = ?1 and type!=?2 and status=1")
	List<ProjectCost> findAllByProjectIdAndNoType(Long projectId, Integer type);
	
	@Query(" from ProjectCost where id in (select max(id) from ProjectCost where status = 1 and projectId = ?1 and costDay >= ?2 and costDay <= ?3 and type = ?4 group by projectId)")
	ProjectCost findMaxByProjectIdAndCostDayAndType(Long projectId, Long beginTime, Long endTime, Integer type);
	
	@Query(" from ProjectCost where projectId = ?1 and costDay = ?2 and type = ?3 and status=1")
	ProjectCost findOneByProjectIdAndCostDayAndType(Long projectId, Long costDay, Integer type);
}
