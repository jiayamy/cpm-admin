package com.wondertek.cpm.repository;

import java.util.List;

import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.OutsourcingUserVo;

public interface OutsourcingUserDao extends GenericDao<OutsourcingUser, Long> {

	OutsourcingUserVo findById(Long id);

	List<LongValue> queryUserRank(Long contractId);

}
