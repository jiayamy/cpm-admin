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
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProductPriceVo;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;
@Repository("productPriceDao")
public class ProductPriceDaoImpl extends GenericDaoImpl<ProductPrice, Long> implements ProductPriceDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProductPrice> getDomainClass() {
		return ProductPrice.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ProductPriceVo> getPricePage(ProductPrice productPrice, Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		
		StringBuffer orderHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append(" select wpp");
		countHql.append("select count(wpp.id)");
		
		whereHql.append(" from ProductPrice as wpp");
		whereHql.append(" where 1=1");
		
		//页面查询条件
		if (!StringUtil.isNullStr(productPrice.getName())) {
			whereHql.append(" and wpp.name like ?");
			params.add("%"+productPrice.getName()+"%");
		}
		if (!StringUtil.isNullStr(productPrice.getType())) {
			whereHql.append(" and wpp.type = ?");
			params.add(Integer.valueOf(productPrice.getType()));
		}
		if (!StringUtil.isNullStr(productPrice.getSource())) {
			whereHql.append(" and wpp.source = ?");
			params.add(Integer.valueOf(productPrice.getSource()));
		}
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		
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
    	queryHql.append(orderHql.toString());
		orderHql.setLength(0);
		orderHql = null;
		
		Page<ProductPrice> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
		List<ProductPriceVo> returnList = new ArrayList<ProductPriceVo>();
		if (page.getContent() != null) {
			for (ProductPrice o : page.getContent()) {
					returnList.add(new ProductPriceVo(o,null));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
}