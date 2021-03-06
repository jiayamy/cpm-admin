package com.wondertek.cpm.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ProjectUserInputVo;
import com.wondertek.cpm.domain.vo.UserProjectInputVo;
import com.wondertek.cpm.domain.vo.UserTimesheetForHardWorkingVo;

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
	/**
	 * 获取某个类型的对象在某个时间段内的所有日报
	 */
	public List<UserTimesheet> getByWorkDayAndObjType(Long startDay, Long endDay, Long objId, Integer type);
	/**
	 * 获取某个类型的对象在某个时间段内的所有日报
	 */
	public List<UserTimesheet> getByWorkDayAndObjType(Long startDay, Long endDay, Long objId, Integer type, Long userId);
	/**
	 * 更新认可工时
	 */
	public void updateAcceptInput(List<UserTimesheet> updateList);
	/**
	 * 查找该员工在这个项目中的报价
	 */
	public List<Object> getOffer(Long userId,Long objId,Long workDay);
	/**
	 * 删除时的保存合同金额
	 */
	public void saveByDelete(UserTimesheet userTimesheet, ContractInfo contractInfo);
	
	
	public List<UserTimesheetForHardWorkingVo> findByWorkDay(Long fromDay, Long endDay);
	/**
	 * 某个项目的某个区间内是否有日报
	 */
	public Long getWorkDayByParam(Long userId, Long objId, Integer type, Long fromDay, Long endDay, int iType);
	/**
	 * 查找项目人员工时
	 */
	public List<ProjectUserInputVo> getProjectUserInputsByParam(Long startTime,Long endTime,List<Long> userIds,List<Long> projectIds,User user,DeptInfo deptInfo);
	/**
	 * 查找人员项目工时
	 */
	public List<UserProjectInputVo> getUserProjectInputsByParam(Long startTime,Long endTime,List<Long> userIds,List<Long> projectId,User user,DeptInfo deptInfo);
}
