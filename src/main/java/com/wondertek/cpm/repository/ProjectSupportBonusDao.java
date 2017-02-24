package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;
import com.wondertek.cpm.domain.vo.ProjectSupportBonusVo;

public interface ProjectSupportBonusDao extends GenericDao<ProjectSupportBonus,Long> {

	Page<ProjectSupportBonusVo> getPageByParams(User user, DeptInfo deptInfo,
			ProjectSupportBonus projectSupportBonus, Pageable pageable);

	Page<ProjectSupportBonusVo> getPageDetail(Long contractId, Pageable pageable);

}
