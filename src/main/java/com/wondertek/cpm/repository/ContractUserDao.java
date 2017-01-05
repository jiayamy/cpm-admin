package com.wondertek.cpm.repository;
import java.util.List;

import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractUserDao extends GenericDao<ContractUser, Long> {
	/**
	 * 获取用户在某个时段参加过的合同
	 * @return
	 */
	List<LongValue> getByUserAndDay(Long userId,Long[] weekDays);

}
