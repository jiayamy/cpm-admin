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
}
