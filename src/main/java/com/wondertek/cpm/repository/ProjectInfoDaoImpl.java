package com.wondertek.cpm.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.ProjectInfo;

@Repository("projectInfoDao")
public class ProjectInfoDaoImpl extends GenericDaoImpl<ProjectInfo, Long> implements ProjectInfoDao{

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ProjectInfo> getDomainClass() {
		return ProjectInfo.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ProjectInfo> getPageByParam(String name, int start, int limit, Order... orders) {
		Pageable pageable = this.buildPageable(2, 2, orders);
//		Page<Object[]> page = this.querySqlPage("select id,name from w_project_info where name_ like ?", 
//				"select count(1) from w_project_info where name_ like ?", 
//				new Object[]{"%" + name + "%"}, pageable);
//		List<Object[]> list = page.getContent();
//		List<ProjectInfo> returnList = new ArrayList<ProjectInfo>();
//		list.forEach(o -> {
//			returnList.add(new ProjectInfo(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1])));
//		});
//		return new PageImpl<ProjectInfo>(returnList, pageable, page.getTotalElements());
		
		Page<ProjectInfo> page = this.queryHqlPage("from ProjectInfo where name like ?", 
				"select count(1) from ProjectInfo where name like ?", 
				new Object[]{"%" + name + "%"}, pageable);
		return page;
	}
	
	@Override
	public Page<ProjectInfo> getPageByParam(String name, Pageable pageable) {
//		Page<Object[]> page = this.querySqlPage("select id,name from w_project_info where name_ like ?", 
//				"select count(1) from w_project_info where name_ like ?", 
//				new Object[]{"%" + name + "%"}, pageable);
//		List<Object[]> list = page.getContent();
//		List<ProjectInfo> returnList = new ArrayList<ProjectInfo>();
//		list.forEach(o -> {
//			returnList.add(new ProjectInfo(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1])));
//		});
//		return new PageImpl<ProjectInfo>(returnList, pageable, page.getTotalElements());
		
		Page<ProjectInfo> page = this.queryHqlPage("from ProjectInfo where name like ?", 
				"select count(1) from ProjectInfo where name like ?", 
				new Object[]{"%" + name + "%"}, pageable);
		return page;
	}
}	
