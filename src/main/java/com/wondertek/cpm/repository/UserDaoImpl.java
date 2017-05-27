package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
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
	
	@Inject
	public DeptInfoRepository deptInfoRepository;
	
	@Override
	public Page<User> getUserPage(User user, Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querySql.append("select distinct user from User user left join fetch user.authorities left join DeptInfo wdi on wdi.id = user.deptId");
		
		countSql.append("select count(user.id) from User user left join DeptInfo wdi on wdi.id = user.deptId");
		
		whereSql.append(" where 1=1");
		
		//查询条件
		if(!StringUtil.isNullStr(user.getLogin())){
			whereSql.append(" and user.login like ?" + (count++));
			params.add("%" + user.getLogin() + "%");
		}
		if(!StringUtil.isNullStr(user.getLastName())){
			whereSql.append(" and user.lastName like ?" + (count++));
			params.add("%" + user.getLastName() + "%");
		}
		if(!StringUtil.isNullStr(user.getSerialNum())){
			whereSql.append(" and user.serialNum like ?" + (count++));
			params.add("%" + user.getSerialNum() + "%");
		}
		if(!StringUtil.isNullStr(user.getWorkArea())){
			whereSql.append(" and user.workArea = ?" + (count++));
			params.add(user.getWorkArea());
		}
		if(user.getDeptId() != null){
			DeptInfo deptInfo = deptInfoRepository.getOne(user.getDeptId());
			if(deptInfo != null){
				whereSql.append(" and (user.deptId = ?" + (count++) + " or wdi.idPath like ?" + (count++) + ")");
				params.add(user.getDeptId());
				params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			}else{
				whereSql.append(" and user.deptId = ?" + (count++));
				params.add(user.getDeptId());
			}
		}
		if(user.getGrade() != null){
			whereSql.append(" and user.grade = ?" + (count++));
			params.add(user.getGrade());
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		
		//排序
		if(pageable != null && pageable.getSort() != null){//页面都会有个默认排序
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
