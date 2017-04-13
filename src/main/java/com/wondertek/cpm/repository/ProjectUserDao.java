package com.wondertek.cpm.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ParticipateInfo;
import com.wondertek.cpm.domain.vo.ProjectUserVo;

public interface ProjectUserDao extends GenericDao<ProjectUser, Long> {
	/**
	 * 获取用户在某个时段参与过的所有项目
	 * @return
	 */
	List<LongValue> getByUserAndDay(Long userId, Long[] weekDays);
	List<ParticipateInfo> getInfoByUserAndDay(Long userId, Long[] weekDays);
	/**
	 * 项目经理查看的项目用户列表
	 * @return
	 */
	Page<ProjectUserVo> getUserPage(ProjectUser projectUser, User user, DeptInfo deptInfo, Pageable pageable);
	/**
	 * 获取用户权限下的项目用户
	 */
	ProjectUserVo getProjectUser(User user, DeptInfo deptInfo, Long id);
	/**
	 * 更新项目的人员离开日
	 */
	int updateLeaveDayByProject(Long projectId, long leaveDay, String updator);
	
	
	/**
	 * 导Excel
	 */
	public List<ProjectUserVo> getProjectUserList(ProjectUser searchParams,User user, DeptInfo deptInfo);
}
