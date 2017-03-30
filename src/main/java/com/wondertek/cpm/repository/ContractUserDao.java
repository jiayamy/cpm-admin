package com.wondertek.cpm.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractUserVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ParticipateInfo;
import com.wondertek.cpm.domain.vo.ProjectUserVo;

public interface ContractUserDao extends GenericDao<ContractUser, Long> {
	/**
	 * 获取用户在某个时段参加过的合同
	 * @return
	 */
	List<LongValue> getByUserAndDay(Long userId,Long[] weekDays);
	List<ParticipateInfo> getInfoByUserAndDay(Long userId,Long[] weekDays);
	/**
	 * 查看的项目用户列表
	 * @return
	 */
	Page<ContractUserVo> getUserPage(ContractUser contractUser, User user, DeptInfo deptInfo, Pageable pageable);
	/**
	 * 获取用户权限下的合同用户
	 */
	ContractUserVo getContractUser(User user, DeptInfo deptInfo, Long id);
	/**
	 * 更新合同的人员离开日
	 * @return
	 */
	int updateLeaveDayByContract(Long contractId, long leaveDay, String updator);
	/**
	 * 导出合同人员信息的Excel格式
	 * @return
	 */
	List<ContractUserVo> getUserPage(ContractUser contractUser, User user, DeptInfo deptInfo);

}
