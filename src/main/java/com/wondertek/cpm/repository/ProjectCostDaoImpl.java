package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectCostVo;
@Repository("projectCostDao")
public class ProjectCostDaoImpl extends GenericDaoImpl<ProjectCost, Long> implements ProjectCostDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectCost> getDomainClass() {
		return ProjectCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ProjectCostVo> getUserPage(ProjectCost projectCost, User user, DeptInfo deptInfo, Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wpc,wpi.serialNum,wpi.name");
		countHql.append("select count(wpc.id)");
		
		whereHql.append(" from ProjectCost wpc");
		whereHql.append(" left join ProjectInfo wpi on wpi.id = wpc.projectId");
		whereHql.append(" left join DeptInfo wdi on wpi.deptId = wdi.id");
		
		whereHql.append(" where (wpi.pmId = ? or wpi.creator = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			whereHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		whereHql.append(" and wpc.type > ?");	
		params.add(ProjectCost.TYPE_HUMAN_COST);
		
		//查询条件
		if(projectCost.getName() != null){
			whereHql.append(" and wpc.name like ?");
			params.add("%" + projectCost.getName() + "%");
		}
		if(projectCost.getType() != null){
			whereHql.append(" and wpc.type = ?");
			params.add(projectCost.getType());
		}
		if(projectCost.getProjectId() != null){
			whereHql.append(" and wpc.projectId = ?");
			params.add(projectCost.getProjectId());
		}
		
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		
		//排序
		if(pageable.getSort() != null){//页面都会有个默认排序
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderHql.length() != 0){
    				orderHql.append(",");
    			}else{
    				orderHql.append(" order by ");
    			}
   				orderHql.append(order.getProperty());
    			if(order.isAscending()){
    				orderHql.append(" asc");
    			}else{
    				orderHql.append(" desc");
    			}
    		}
    	}
		queryHql.append(orderHql.toString());
		orderHql.setLength(0);
		orderHql = null;
		
		Page<Object[]> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
		
		List<ProjectCostVo> returnList = new ArrayList<ProjectCostVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(new ProjectCostVo((ProjectCost)o[0],StringUtil.null2Str(o[1]),StringUtil.null2Str(o[2])));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	@Override
	public ProjectCostVo getProjectCost(User user, DeptInfo deptInfo, Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wpc,wpi.serialNum,wpi.name");
		queryHql.append(" from ProjectCost wpc");
		queryHql.append(" left join ProjectInfo wpi on wpi.id = wpc.projectId ");
		queryHql.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
		queryHql.append(" where (wpi.pmId = ? or wpi.creator = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(") and wpc.id = ?");
		params.add(id);
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new ProjectCostVo((ProjectCost)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}
	
}
