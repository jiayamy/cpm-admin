package com.wondertek.cpm.repository;
import java.util.List;

import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ProjectUserDao extends GenericDao<ProjectUser, Long> {
	/**
	 * 获取用户在某个时段参与过的所有项目
	 * @return
	 */
	List<LongValue> getByUserAndDay(Long userId, Long[] weekDays);

}
