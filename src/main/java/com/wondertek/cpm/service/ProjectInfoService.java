package com.wondertek.cpm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.repository.ProjectInfoDao;
import com.wondertek.cpm.repository.ProjectInfoRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ProjectInfoService {

    private final Logger log = LoggerFactory.getLogger(ProjectInfoService.class);

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private ProjectInfoDao projectInfoDao;
    
    public Page<ProjectInfo> getPageByParam(String name,int start,int limit,Order... orders) {
    	return projectInfoDao.getPageByParam(name, start, limit, orders);
    }
    public Page<ProjectInfo> getPageByParam(String name,Pageable pageable) {
    	return projectInfoDao.getPageByParam(name, pageable);
    }
}
