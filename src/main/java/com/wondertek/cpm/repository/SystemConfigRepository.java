package com.wondertek.cpm.repository;

import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.SystemConfig;
import com.wondertek.cpm.domain.UserTimesheet;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the WorkArea entity.
 */
@SuppressWarnings("unused")
public interface SystemConfigRepository extends JpaRepository<SystemConfig,Long> {
	
}
