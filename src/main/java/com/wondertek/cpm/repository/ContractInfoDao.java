package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;

public interface ContractInfoDao extends GenericDao<ContractInfo, Long>{
	/**
	 * 获取用户筛选的列表
	 */
	public Page<ContractInfoVo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable, User user, DeptInfo deptInfo);
	/**
	 * 查看新建时是否合同名重复
	 * @return
	 */
	public boolean checkByContract(String serialNum, Long id);
	/**
	 * 有权限的查看合同信息
	 * @return
	 */
	public ContractInfoVo getUserContractInfo(Long id, User user, DeptInfo deptInfo);
	/**
	 * 查询用户能看到的合同信息
	 */
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo);
	/**
	 * 更新完成率
	 * @return
	 */
	public int finishContractInfo(Long id, Double finishRate, String updator);
	/**
	 * 更新合同的回款总额
	 */
	public int updateReceiveTotal(Long contractId, Double receiveTotal, Double oldTotal);
	
	
}
