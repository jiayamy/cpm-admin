package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondertek.cpm.domain.DeptType;

/**
 * Spring Data JPA repository for the DeptType entity.
 */
public interface DeptTypeRepository extends JpaRepository<DeptType,Long> {
	
}
