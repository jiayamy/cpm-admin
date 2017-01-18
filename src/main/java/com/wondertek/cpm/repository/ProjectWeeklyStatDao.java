package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;

public interface ProjectWeeklyStatDao extends GenericDao<ProjectWeeklyStat, Long>{
	
	public Page<ProjectWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable, User user);
	
	public Page<ProjectWeeklyStatVo> getUserPage(String projectId, Pageable pageable, User user);
	
	public List<LongValue> queryUserProject(User user, DeptInfo deptInfo);
	
	public ProjectWeeklyStatVo getById(Long id);
}
