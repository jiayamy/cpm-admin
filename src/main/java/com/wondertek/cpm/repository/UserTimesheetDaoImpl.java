package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
@Repository("userTimesheetDao")
public class UserTimesheetDaoImpl extends GenericDaoImpl<UserTimesheet, Long> implements UserTimesheetDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<UserTimesheet> getDomainClass() {
		return UserTimesheet.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable, Optional<User> user) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	sb.append("where 1=1");
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and objName like ?");
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getType() != null){
    		sb.append(" and type = ?");
    		params.add(userTimesheet.getType());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and workDay = ?");
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and status = ?");
    	params.add(CpmConstants.STATUS_VALID);
    	sb.append(" and userId = ?");
    	params.add(user.get().getId());
    	
    	StringBuffer orderHql = new StringBuffer();
    	if(pageable.getSort() != null){
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
    	Page<UserTimesheet> page = this.queryHqlPage(
    			"from UserTimesheet " + sb.toString() + orderHql.toString(), 
    			"select count(id) from UserTimesheet " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}

	@Override
	public List<UserTimesheet> getByWorkDayAndUser(Long startDay, Long endDay, Long userId) {
		return this.queryAllHql("from UserTimesheet where workDay >= ? and workDay <= ? and status = ? and userId = ? order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,userId});
	}
	
}
