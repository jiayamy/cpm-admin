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
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProductPriceVo;
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
	public Page<PurchaseItemVo> getPurchaserPage(PurchaseItem purchaseItem,User user,DeptInfo deptInfo,
			Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wpi,wci.serialNum,wci.name as contractName,wcb.name as budgetName");
		countHql.append("select count(wpi.id)");
		
		whereHql.append(" from PurchaseItem wpi");
		whereHql.append(" left join User wju on wpi.creator = wju.login");
		whereHql.append(" left join DeptInfo wdi on wju.deptId = wdi.id");
		whereHql.append(" left join ContractInfo wci on wci.id = wpi.contractId");
		whereHql.append(" left join ContractBudget wcb on wcb.id = wpi.budgetId");
		//权限
		whereHql.append(" where (wpi.creator = ?");
		params.add(user.getLogin());
		if (user.getIsManager()) {
			whereHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		
		//查询条件
		if (purchaseItem.getContractId() != null) {
			whereHql.append(" and wpi.contractId = ?");
			params.add(purchaseItem.getContractId());
		}
		if (purchaseItem.getName() != null) {
			whereHql.append(" and wpi.name like ?");
			params.add("%" + purchaseItem.getName() + "%");
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

	@Override
	public PurchaseItemVo findPurchaseItemById(Long id,User user, DeptInfo deptInfo) {
		StringBuffer 	queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wpi,wci.serialNum,wci.name as contractName,wcb.name as budgetName");
		queryHql.append(" from PurchaseItem wpi");
		queryHql.append(" left join User wju on wpi.creator = wju.login");
		queryHql.append(" left join DeptInfo wdi on wju.deptId = wdi.id");
		queryHql.append(" left join ContractInfo wci on wci.id = wpi.contractId");
		queryHql.append(" left join ContractBudget wcb on wcb.id = wpi.budgetId");
		
		//权限
		queryHql.append(" where (wpi.creator = ?");
		params.add(user.getLogin());
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		
		if (id != null) {
			queryHql.append(" and wpi.id = ?");
			params.add(id);
		}
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new PurchaseItemVo((PurchaseItem)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]),StringUtil.null2Str(list.get(0)[3]));
		}
		return null;
	}

	@Override
	public List<LongValue> queryUserContract(User user,DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		
		queryHql.append(" select distinct wci.id,wci.serialNum,wci.name from ContractInfo as wci");
		queryHql.append(" left join PurchaseItem wpi on wci.id = wpi.contractId");
		queryHql.append(" left join User wju on wpi.creator = wju.login");
		queryHql.append(" left join DeptInfo wdi on wju.deptId = wdi.id");
		queryHql.append(" where (wpi.creator = ?");
		
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
		}
		
		queryHql.append(")");
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		List<LongValue> resultList = new ArrayList<LongValue>();
		if (list != null && !list.isEmpty()) {
			for (Object[] o : list) {
				resultList.add(new LongValue(StringUtil.nullToLong(o[0]), StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return resultList;
	}

	@Override
	public Page<ProductPriceVo> getPricePage(String selectName,
			Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		
		StringBuffer countHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append(" select wpp");
		countHql.append("select count(wpp.id)");
		
		whereHql.append(" from ProductPrice as wpp");
		whereHql.append(" where 1=1");
		
		//页面查询条件
		if (!StringUtil.isNullStr(selectName)) {
			whereHql.append(" and wpp.name like ?");
			params.add("%"+ selectName +"%");
		}
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		
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
	