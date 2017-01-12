package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectInfo entity.
 */
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo,Long> {
	
	@Query(" from ProjectInfo where contractId = ?1 and status = 1 ")
	List<ProjectInfo> findByContractId(Long contractId);
}
