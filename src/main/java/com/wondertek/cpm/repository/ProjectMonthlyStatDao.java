package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;

public interface ProjectMonthlyStatDao extends GenericDao<ProjectMonthlyStat, Long>{
	
	public Page<ProjectMonthlyStatVo> getUserPage(String projectId, Pageable pageable, User user);
	
	public List<LongValue> queryUserProject(User user, DeptInfo deptInfo);
	
	public ProjectMonthlyStatVo getById(Long id);
	
}
