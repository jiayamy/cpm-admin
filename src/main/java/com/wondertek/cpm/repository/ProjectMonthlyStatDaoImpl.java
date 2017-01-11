package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;

@Repository("projectMonthlyStatDao")
public class ProjectMonthlyStatDaoImpl extends GenericDaoImpl<ProjectMonthlyStat, Long> implements ProjectMonthlyStatDao{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectMonthlyStat> getDomainClass() {
		return ProjectMonthlyStat.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public Page<ProjectMonthlyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable,User user) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	sb.append("where 1=1");
    	if(!StringUtil.isNullStr(beginDate)){
    		Date bgDate = DateUtil.parseDate("yyyyMMdd", beginDate);
    		ZonedDateTime bg = DateUtil.getZonedDateTime(bgDate.getTime());
    		sb.append(" and createTime >= ?");
    		params.add(bg);
    	}
    	if(!StringUtil.isNullStr(endDate)){
    		Date edDate = DateUtil.parseDate("yyyyMMdd", endDate);
    		ZonedDateTime ed = DateUtil.getZonedDateTime(edDate.getTime());
    		sb.append(" and createTime <= ?");
    		params.add(ed);
    	}
    	if(!StringUtil.isNullStr(statDate)){
    		Long st = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.parseDate("yyyyMMdd", statDate)).toString());
    		sb.append(" and statWeek = ?");
    		params.add(st);
    	}
//    	sb.append(" and status = ?");
//    	params.add(CpmConstants.STATUS_VALID);
    	StringBuffer orderHql = new StringBuffer();
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
    			if(order.isAscending()){
    				orderHql.append(order.getProperty()).append(" asc");
    			}else{
    				orderHql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
    	Page<ProjectMonthlyStat> page = this.queryHqlPage(
    			"from ProjectMonthlyStat " + sb.toString() + orderHql.toString(), 
    			"select count(id) from ProjectMonthlyStat " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}
	
}
