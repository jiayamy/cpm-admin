package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectInfo;

/**
 * Spring Data JPA repository for the ProjectInfo entity.
 */
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo,Long> {
	
	@Query(" from ProjectInfo where contractId = ?1 and status = 1 ")
	List<ProjectInfo> findByContractId(Long contractId);
	
	@Query(" from ProjectInfo where createTime <= ?3 and (status = ?1 or updateTime >= ?2)")
	List<ProjectInfo> findByStatusOrBeginTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ProjectInfo where contractId = ?1 order by id desc")
	List<ProjectInfo> findAllByContractId(Long contractId);

	@Query(" select ju.serialNum,ju.lastName from ProjectInfo pi,User ju where pi.pmId = ju.id and pi.id = ?1")
	List<Object[]> findPmByProjectId(Long projectId);
}
