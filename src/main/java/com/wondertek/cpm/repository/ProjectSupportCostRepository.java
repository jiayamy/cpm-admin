package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectSupportCost;

public interface ProjectSupportCostRepository extends JpaRepository<ProjectSupportCost,Long>{
	
	@Query(" from ProjectSupportCost where deptType = ?1")
	List<ProjectSupportCost> findByDeptType(Long deptType);
}
