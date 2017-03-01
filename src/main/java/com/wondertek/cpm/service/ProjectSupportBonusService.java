package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
import com.wondertek.cpm.domain.vo.ProjectSupportBonusVo;
import com.wondertek.cpm.repository.ProjectSupportBonusDao;
import com.wondertek.cpm.repository.ProjectSupportBonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class ProjectSupportBonusService {
	private final Logger log = LoggerFactory.getLogger(ProjectSupportBonusService.class);
	
	@Inject
	private ProjectSupportBonusDao projectSupportBonusDao;
	
	@Inject ProjectSupportBonusRepository projectSupportBonusRepository;
	
	@Inject UserRepository userRepository;
	
	public Page<ProjectSupportBonusVo> searchPage(
			ProjectSupportBonus projectSupportBonus, Pageable pageable) {
		//获取列表页
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
			Page<ProjectSupportBonusVo> page = projectSupportBonusDao.getPageByParams(user,deptInfo,projectSupportBonus,pageable);
			return page;
		}else {
			return new PageImpl<ProjectSupportBonusVo>(new ArrayList<ProjectSupportBonusVo>(),pageable,0);
		}
	}

	public Page<ProjectSupportBonusVo> searchPageDetail(ProjectSupportBonus projectSupportBonus, Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		Page<ProjectSupportBonusVo> page = projectSupportBonusDao.getPageDetail(projectSupportBonus,user,deptInfo,pageable);
    		return page;
		}else {
			return new PageImpl<ProjectSupportBonusVo>(new ArrayList<ProjectSupportBonusVo>(),pageable,0);
		}
	}

	public ProjectSupportBonusVo getUserSupportBonus(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
			Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return projectSupportBonusDao.getUserSupportBonus(id,user,deptInfo);
		}
		return null;
	}

}
