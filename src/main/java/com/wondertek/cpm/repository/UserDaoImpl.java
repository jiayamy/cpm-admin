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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.User;
@Repository("userDao")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<User> getDomainClass() {
		return User.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<User> getUserPage(User user, Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select distinct user from User user left join fetch user.authorities");
		
		countSql.append("select count(user.id) from User user");
		
		whereSql.append(" where 1=1");
		
		//查询条件
		if(!StringUtil.isNullStr(user.getLogin())){
			whereSql.append(" and user.login like ?");
			params.add("%" + user.getLogin() + "%");
		}
		if(!StringUtil.isNullStr(user.getLastName())){
			whereSql.append(" and user.lastName like ?");
			params.add("%" + user.getLastName() + "%");
		}
		if(!StringUtil.isNullStr(user.getSerialNum())){
			whereSql.append(" and user.serialNum like ?");
			params.add("%" + user.getSerialNum() + "%");
		}
		if(!StringUtil.isNullStr(user.getWorkArea())){
			whereSql.append(" and user.workArea = ?");
			params.add(user.getWorkArea());
		}
		if(user.getDeptId() != null){
			whereSql.append(" and user.deptId = ?");
			params.add(user.getDeptId());
		}
		
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		
		//排序
		if(pageable.getSort() != null){//页面都会有个默认排序
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderSql.length() != 0){
    				orderSql.append(",");
    			}else{
    				orderSql.append(" order by ");
    			}
   				orderSql.append(order.getProperty());
    			if(order.isAscending()){
    				orderSql.append(" asc");
    			}else{
    				orderSql.append(" desc");
    			}
    		}
    	}
		querySql.append(orderSql.toString());
		orderSql.setLength(0);
		orderSql = null;
		
		Page<User> page = this.queryHqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		return page;
	}
	
}
