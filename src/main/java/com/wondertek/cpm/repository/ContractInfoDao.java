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
	 * @param contractInfo
	 * @param pageable
	 * @return Page<ContractInfo>
	 */
	public Page<ContractInfo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable);
	/**
	 * 查看新建时是否合同名重复
	 * @param serialNum
	 * @param id
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
	
	
}
