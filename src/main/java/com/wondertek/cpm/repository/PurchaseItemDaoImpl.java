package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
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
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;

@Repository("purchaseItemDao")
public class PurchaseItemDaoImpl extends GenericDaoImpl<PurchaseItem, Long> implements PurchaseItemDao {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<PurchaseItem> getDomainClass() {
		return PurchaseItem.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<PurchaseItemVo> getPurchaserPage(PurchaseItem purchaseItem,
			Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wpi,wci.serialNum,wci.name as contractName,wcb.name as budgetName");
		countHql.append("select count(wpi.id)");
		
		whereHql.append(" from PurchaseItem wpi");
		whereHql.append(" left join ContractInfo wci on wci.id = wpi.contractId");
		whereHql.append(" left join ContractBudget wcb on wcb.id = wpi.budgetId");
		
		whereHql.append(" where 1=1");
		
		//查询条件
		if (purchaseItem.getContractId() != null) {
			whereHql.append(" and wpi.contractId = ?");
			params.add(purchaseItem.getContractId());
		}
		if (purchaseItem.getName() != null) {
			whereHql.append(" and wpi.name like ?");
			params.add(purchaseItem.getName());
		}
		if (purchaseItem.getType() != null) {
			whereHql.append(" and wpi.type = ?");
			params.add(purchaseItem.getType());
		}
		if (purchaseItem.getSource() != null) {
			whereHql.append(" and wpi.source = ?");
			params.add(purchaseItem.getSource());
		}
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		
		//排序
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
		List<PurchaseItemVo> returnList = new ArrayList<PurchaseItemVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
					returnList.add(transPurchaseItemVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	private PurchaseItemVo transPurchaseItemVo(Object[] o) {
		return new PurchaseItemVo((PurchaseItem)o[0],StringUtil.null2Str(o[1]),StringUtil.null2Str(o[2]),StringUtil.null2Str(o[3]));
	}


}
	