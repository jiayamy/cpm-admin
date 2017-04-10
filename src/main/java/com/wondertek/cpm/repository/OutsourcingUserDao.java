package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.OutsourcingUserVo;

public interface OutsourcingUserDao extends GenericDao<OutsourcingUser, Long> {

	OutsourcingUserVo findById(Long id, User user, DeptInfo deptInfo);

	OutsourcingUserVo choseUser(Long id, User user, DeptInfo deptInfo);

}
