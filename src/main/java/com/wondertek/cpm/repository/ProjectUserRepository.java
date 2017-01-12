package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectUser;

/**
 * Spring Data JPA repository for the ProjectUser entity.
 */
public interface ProjectUserRepository extends JpaRepository<ProjectUser,Long> {
	@Query("from ProjectUser pu where pu.userId = ?1 and pu.projectId = ?2")
	List<ProjectUser> findByUserId(Long userId, Long projectId);

}
