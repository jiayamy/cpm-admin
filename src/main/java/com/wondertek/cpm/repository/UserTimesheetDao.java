package com.wondertek.cpm.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;

public interface UserTimesheetDao extends GenericDao<UserTimesheet, Long> {
	/**
	 * 获取用户自己看到的日报列表
	 * @return
	 */
	public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable, Optional<User> user);
	/**
	 * 获取用户某个时间段中间的所有可用日报
	 * @return
	 */
	public List<UserTimesheet> getByWorkDayAndUser(Long startDay, Long endDay, Long userId);

}
