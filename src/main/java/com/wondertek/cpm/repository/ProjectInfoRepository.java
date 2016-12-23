package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondertek.cpm.domain.ProjectInfo;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long> {
	
}	
