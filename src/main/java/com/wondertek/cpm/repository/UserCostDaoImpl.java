package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.domain.UserCost;

@Repository("userCostDao")
public class UserCostDaoImpl extends GenericDaoImpl<UserCost, Long> implements UserCostDao{

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<UserCost> getDomainClass() {
		return UserCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<UserCost> getUserCostPage(UserCost userCost, Pageable pageable) {
		StringBuffer whereHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		whereHql.append("where 1 = 1");
		if(userCost.getUserId() != null){
			whereHql.append(" and userId = ?");
			params.add(userCost.getUserId());
		}
		if(userCost.getUserName() != null){
			whereHql.append(" and userName = ?");
			params.add(userCost.getUserName());
		}
		if(userCost.getCostMonth() != null){
			whereHql.append(" and costMonth = ?");
			params.add(userCost.getCostMonth());
		}
		if(userCost.getStatus() != null){
			whereHql.append(" and status = ?");
			params.add(userCost.getStatus());
		}
		
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
		Page<UserCost> page = this.queryHqlPage(
    			"from UserCost " + whereHql.toString() + orderHql.toString(), 
    			"select count(id) from UserCost " + whereHql.toString(), 
    			params.toArray(), 
    			pageable
    		);
		return page;
	}

}
