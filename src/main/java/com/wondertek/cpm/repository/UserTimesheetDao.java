package com.wondertek.cpm.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;

public interface UserTimesheetDao extends GenericDao<UserTimesheet, Long> {
	/**
	 * 获取用户自己看到的日报列表
	 */
	public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable, User user);
	/**
	 * 获取合同看到的日报列表
	 */
	public Page<UserTimesheet> getContractPage(UserTimesheet userTimesheet, User user, DeptInfo deptInfo,Pageable pageable);
	/**
	 * 获取合同看到的日报列表
	 */
	public Page<UserTimesheet> getProjectPage(UserTimesheet userTimesheet, User user, DeptInfo deptInfo,Pageable pageable);
	/**
	 * 获取用户某个时间段中间的所有可用日报
	 */
	public List<UserTimesheet> getByWorkDayAndUser(Long startDay, Long endDay, Long userId);
	/**
	 * 用户自己新增修改的
	 */
	public void saveByUser(List<UserTimesheet> saveList, List<UserTimesheet> updateList);
	/**
	 * 合同工时中查看日报信息
	 */
	public UserTimesheet getUserTimesheetForContract(Long id, User user, DeptInfo deptInfo);
	/**
	 * 项目工时中查看日报信息
	 */
	public UserTimesheet getUserTimesheetForProject(Long id, User user, DeptInfo deptInfo);
}
