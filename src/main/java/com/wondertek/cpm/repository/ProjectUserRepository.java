package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectUser;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectUser entity.
 */
@SuppressWarnings("unused")
public interface ProjectUserRepository extends JpaRepository<ProjectUser,Long> {

}
