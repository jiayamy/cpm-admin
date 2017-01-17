package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectFinishInfo;

/**
 * Spring Data JPA repository for the ProjectFinishInfo entity.
 */
public interface ProjectFinishInfoRepository extends JpaRepository<ProjectFinishInfo,Long> {
	
	@Query("from ProjectFinishInfo where projectId = ?1 and createTime < ?2 order by createTime asc")
	List<ProjectFinishInfo> findAllByProjectIdAndCreateTimeBefore(Long projectId, ZonedDateTime createTime);
}
