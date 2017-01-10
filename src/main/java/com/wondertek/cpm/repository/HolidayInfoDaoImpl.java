package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.domain.HolidayInfo;

@Repository("holidayInfoDao")
public class HolidayInfoDaoImpl extends GenericDaoImpl<HolidayInfo, Long> implements HolidayInfoDao {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<HolidayInfo> getDomainClass() {
		return HolidayInfo.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<HolidayInfo> getHolidayInfoPage(Map<String, Long> condition, Pageable pageable) {
		StringBuffer sb = new StringBuffer();
		List<Long> params = new ArrayList<Long>();
		sb.append("where 1=1");
		if(condition.get("fromCurrDay") != null && condition.get("toCurrDay") != null){
    		sb.append("and currDay >= ? and currDay <= ?");
    		params.add(condition.get("fromCurrDay"));
    		params.add(condition.get("toCurrDay"));
    	}else if(condition.get("fromCurrDay") != null && condition.get("toCurrDay") == null){
    		sb.append(" and currDay = ?");
    		params.add(condition.get("fromCurrDay"));
    	}else if(condition.get("fromCurrDay") == null && condition.get("toCurrDay") != null){
    		sb.append(" and currDay = ?");
    		params.add(condition.get("toCurrDay"));
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
		Page<HolidayInfo> page = this.queryHqlPage(
    			"from HolidayInfo " + sb.toString() + orderHql.toString(), 
    			"select count(id) from HolidayInfo " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
		return page;
	}

}
