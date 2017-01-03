package com.wondertek.cpm.repository;
import java.util.List;

import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractUserDao extends GenericDao<ContractUser, Long> {
	/**
	 * 获取用户参与过的所有合同
	 * @return
	 */
	List<LongValue> getAllByUser(Long userId);

}
