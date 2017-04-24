package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.SystemConfig;
import com.wondertek.cpm.repository.SystemConfigDao;
import com.wondertek.cpm.repository.SystemConfigRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class SystemConfigService {

	private final Logger log = LoggerFactory.getLogger(SystemConfigService.class);
	 
	@Inject
	private SystemConfigDao systemConfigDao;
	
	@Inject
	private SystemConfigRepository systemConfigRepository;
	
	@Inject
	private UserRepository userRepository;

	public Page<SystemConfig> getAllSystemConfig(String key, Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
    		Page<SystemConfig> page = systemConfigDao.getAllSystemConfig(key, pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<SystemConfig>(), pageable, 0);
    	}
	}

	public SystemConfig findOne(Long id) {
		log.debug("Request to get SystemConfig : {}", id);
        SystemConfig systemConfig = systemConfigRepository.findOne(id);
        return systemConfig;
	}
}
