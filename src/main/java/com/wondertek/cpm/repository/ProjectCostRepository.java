package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectCost;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectCost entity.
 */
@SuppressWarnings("unused")
public interface ProjectCostRepository extends JpaRepository<ProjectCost,Long> {

}
