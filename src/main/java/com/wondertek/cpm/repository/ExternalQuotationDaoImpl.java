package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.ExternalQuotation;

@Repository("externalQuotationDao")
public class ExternalQuotationDaoImpl extends GenericDaoImpl<ExternalQuotation, Long> implements ExternalQuotationDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ExternalQuotation> getDomainClass() {
		return ExternalQuotation.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ExternalQuotation> getUserPage(ExternalQuotation externalQuotation, Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		countHql.append("select count(weq.id)");
		
		whereHql.append(" from ExternalQuotation weq");
		
		whereHql.append(" where 1=1");
		
		//查询条件
		if(externalQuotation.getGrade() != null){
			whereHql.append(" and weq.grade = ?" + (count++));
			params.add(externalQuotation.getGrade());
		}
		
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		
		whereHql.setLength(0);
		whereHql = null;
		
		//排序
		if(pageable.getSort() != null){//页面都会有个默认排序
    		for (Order order : pageable.getSort()) {
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
		
		return this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
	}
}