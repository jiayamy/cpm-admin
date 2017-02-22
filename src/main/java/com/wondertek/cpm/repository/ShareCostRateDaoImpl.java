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

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ShareCostRate;

@Repository("externalQuotationDao")
public class ShareCostRateDaoImpl extends GenericDaoImpl<ShareCostRate, Long> implements ShareCostRateDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ShareCostRate> getDomainClass() {
		return ShareCostRate.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ShareCostRate> getUserPage(ShareCostRate shareCostRate, Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wscr");
		queryHql.append(",wpt.name");
		
		countHql.append("select count(wscr.id)");
		
		whereHql.append(" from ShareCostRate wscr");
		whereHql.append(" left join DeptType wpt on wpt.id = wscr.deptType");
		
		whereHql.append(" where 1=1");
		//查询条件
		if(shareCostRate.getContractType() != null){
			whereHql.append(" and wscr.contractType = ?");
			params.add(shareCostRate.getContractType());
		}
		if(shareCostRate.getDeptType() != null){
			whereHql.append(" and wscr.deptType = ?");
			params.add(shareCostRate.getDeptType());
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
		
		Page<Object[]> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
		
		List<ShareCostRate> returnList = new ArrayList<ShareCostRate>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(new ShareCostRate((ShareCostRate)o[0],StringUtil.null2Str(o[1])));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
}