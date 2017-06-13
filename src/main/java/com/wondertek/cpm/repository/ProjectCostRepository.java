package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectCost;

/**
 * Spring Data JPA repository for the ProjectCost entity.
 */
public interface ProjectCostRepository extends JpaRepository<ProjectCost,Long> {
	
	@Query("from ProjectCost where projectId = ?1 and type=?2 and costDay <= ?3 and status=1")
	List<ProjectCost> findByProjectIdAndTypeAndBeforeCostDay(Long projectId, Integer type, Long costDay);
	
	@Query("select sum(total) from ProjectCost where projectId = ?1 and type=?2 and costDay <= ?3 and status=1")
	Double findTotalByProjectIdAndTypeAndBeforeCostDay(Long projectId, Integer type, Long costDay);
	
	@Query("from ProjectCost where projectId = ?1 and type!=?2 and costDay <= ?3 and status=1")
	List<ProjectCost> findAllByProjectIdAndNoTypeAndBeforeCostDay(Long projectId, Integer type, Long costDay);
	
	@Query("select sum(total) from ProjectCost where projectId = ?1 and type!=?2 and costDay <= ?3 and status=1")
	Double findTotalByProjectIdAndNoTypeAndBeforeCostDay(Long projectId, Integer type, Long costDay);
	
	@Query(" from ProjectCost where id in (select max(id) from ProjectCost where status = 1 and projectId = ?1 and costDay >= ?2 and costDay <= ?3 and type = ?4 group by projectId)")
	ProjectCost findMaxByProjectIdAndCostDayAndType(Long projectId, Long beginTime, Long endTime, Integer type);
	
	@Query(" from ProjectCost where projectId = ?1 and costDay = ?2 and type = ?3 and status=1")
	ProjectCost findOneByProjectIdAndCostDayAndType(Long projectId, Long costDay, Integer type);
	
	@Query("from ProjectCost where projectId = ?1 and type!=?2 and costDay >= ?3 and costDay <= ?4 and status=1")
	List<ProjectCost> findAllByProjectIdAndNoTypeAndCostDayBetween(Long projectId, Integer type, Long startDay, Long endDay);
}
