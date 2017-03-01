package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectInfo;

import org.springframework.data.jpa.repository.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the ProjectInfo entity.
 */
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo,Long> {
	
	@Query(" from ProjectInfo where contractId = ?1 and status = 1 ")
	List<ProjectInfo> findByContractId(Long contractId);
	
	@Query(" from ProjectInfo where status = ?1")
	List<ProjectInfo> findByStatus(Integer status);
	
	@Query(" from ProjectInfo where status = ?1 and (updateTime >= ?2 and updateTime <= ?3)")
	List<ProjectInfo> findByStatusAndUpdateTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ProjectInfo where status = ?1 or (updateTime >= ?2 and updateTime <= ?3)")
	List<ProjectInfo> findByStatusOrUpdateTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ProjectInfo where contractId = ?1 and (status = ?2 or (updateTime >= ?3 and updateTime <= ?4))")
	List<ProjectInfo> findByContractIdAndStatusOrUpdateTime(Long contractId, Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ProjectInfo where contractId = ?1 order by id desc")
	List<ProjectInfo> findAllByContractId(Long contractId);
}
