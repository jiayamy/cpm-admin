package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.NoRepositoryBean;

import com.wondertek.cpm.domain.ProjectInfo;

@NoRepositoryBean
public interface ProjectInfoDao extends GenericDao<ProjectInfo,Long> {
	public Page<ProjectInfo> getPageByParam(String name,int start,int limit,Order... orders);
	public Page<ProjectInfo> getPageByParam(String name,Pageable pageable);
}
