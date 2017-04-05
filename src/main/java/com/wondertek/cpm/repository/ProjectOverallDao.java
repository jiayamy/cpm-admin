package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;

public interface ProjectOverallDao extends GenericDao<ProjectOverall, Long> {

	public Page<ProjectOverallVo> getPageByParams(User user,DeptInfo deptInfo,ProjectOverall projectInfo,Pageable pageable);

	public Page<ProjectOverallVo> getPageDetail(Long contractId,User user,DeptInfo deptInfo,
			Pageable pageable);

	public ProjectOverallVo getUserProjectOverall(Long id, User user,
			DeptInfo deptInfo);

	public List<ProjectOverallVo> getListByParams(User user, DeptInfo deptInfo,
			ProjectOverall projectOverall);

}
