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
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.vo.BonusRateVo;

@Repository("bonusRateDao")
public class BonusRateDaoImpl extends GenericDaoImpl<BonusRate, Long> implements BonusRateDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<BonusRate> getDomainClass() {
		return BonusRate.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<BonusRateVo> getUserPage(BonusRate bonusRate, Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wbr");
		queryHql.append(",wpt.name");
		
		countHql.append("select count(wbr.id)");
		
		whereHql.append(" from BonusRate wbr");
		whereHql.append(" left join DeptType wpt on wpt.id = wbr.deptType");
		
		whereHql.append(" where 1=1");
		//查询条件
		if(bonusRate.getContractType() != null){
			whereHql.append(" and wbr.contractType = ?" + (count++));
			params.add(bonusRate.getContractType());
		}
		if(bonusRate.getDeptType() != null){
			whereHql.append(" and wbr.deptType = ?" + (count++));
			params.add(bonusRate.getDeptType());
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
		
		List<BonusRateVo> returnList = new ArrayList<BonusRateVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(new BonusRateVo((BonusRate)o[0],StringUtil.null2Str(o[1])));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
}