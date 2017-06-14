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
	
	@Query("from ProjectUser where projectId = ?1")
	List<ProjectUser> findByProjectId(Long projectId);
	
	@Query("from ProjectUser pu where pu.projectId = ?1 and pu.userId = ?2")
	List<ProjectUser> getdates(Long projectId, Long userId);
	
	@Query(" select ju.serialNum,ju.lastName from ProjectUser pu ,User ju where pu.userId = ju.id and pu.projectId = ?1")
	List<Object[]> getUsersByProjectId(Long projectId);
	
}
