package com.wondertek.cpm.repository;

import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.SystemConfig;
import com.wondertek.cpm.domain.UserTimesheet;

import org.springframework.data.jpa.repository.*;

import java.util.List;
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
	
	@Query("select wsc.value from SystemConfig wsc where key = ?1")
	public Double findValueByKey(String key);
}
