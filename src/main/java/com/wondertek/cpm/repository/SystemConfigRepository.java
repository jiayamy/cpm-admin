package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.SystemConfig;

/**
 * Spring Data JPA repository for the WorkArea entity.
 */
@SuppressWarnings("unused")
public interface SystemConfigRepository extends JpaRepository<SystemConfig,Long> {
	
	@Query(" from SystemConfig where key = ?1")
	public SystemConfig findByKey(String configKey);
}
