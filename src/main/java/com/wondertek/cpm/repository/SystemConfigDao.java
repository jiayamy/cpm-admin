package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.SystemConfig;

public interface SystemConfigDao {

	Page<SystemConfig> getAllSystemConfig(String key, Pageable pageable);

}
