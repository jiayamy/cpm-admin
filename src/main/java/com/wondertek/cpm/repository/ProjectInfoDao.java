package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;

@NoRepositoryBean
public interface ProjectInfoDao extends GenericDao<ProjectInfo,Long> {
	/**
	 * 查询用户项目中的所有合同信息
	 * @param user
	 * @param deptInfo
	 * @return
	 */
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo);
	/**
	 * 获取用户能够查看的项目列表
	 * @return
	 */
	public Page<ProjectInfoVo> getUserPage(ProjectInfo projectInfo, Pageable pageable, User user, DeptInfo deptInfo);
	/**
	 * 有权限的查看项目信息
	 * @return
	 */
	public ProjectInfoVo getUserProjectInfo(Long id, User user, DeptInfo deptInfo);
	/**
	 * 获取用户权限下的合同预算
	 */
	public List<LongValue> queryUserContractBudget(User user, DeptInfo deptInfo, Long contractId);
	/**
	 * 检查项目中的预算信息
	 * @return
	 */
	public int checkByBudget(ProjectInfo projectInfo);
	/**
	 * 项目项目信息，一般也就编号
	 */
	public boolean checkByProject(String serialNum, Long id);
	/**
	 * 项目完成率
	 */
	public int finishProjectInfo(Long id, Double finishRate, String updator);
	/**
	 * 项目结项
	 */
	public int endProjectInfo(Long id, String updator);
	/**
	 * 查询用户能看到的项目信息
	 */
	public List<LongValue> queryUserProject(User user, DeptInfo deptInfo);
}
