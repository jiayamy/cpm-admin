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
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractCostVo;
import com.wondertek.cpm.domain.vo.ProjectCostVo;
@Repository("contractCostDao")
public class ContractCostDaoImpl extends GenericDaoImpl<ContractCost, Long> implements ContractCostDao {
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ContractCost> getDomainClass() {
		return ContractCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ContractCostVo> getUserPage(ContractCost contractCost, User user, DeptInfo deptInfo,
			Pageable pageable) {
		StringBuffer countHql = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wcc,wci.serialNum,wci.name,wcb.budgetTotal");
		countHql.append("select count(wcc.id)");
		
		whereHql.append(" from ContractCost wcc");
		whereHql.append(" left join ContractInfo wci on wci.id = wcc.contractId");
		whereHql.append(" left join ContractBudget wcb on wcb.id = wcc.budgetId");
		whereHql.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
		whereHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		whereHql.append(" where (wci.salesmanId = ? or wci.consultantsId = ? or wci.creator = ?");
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			whereHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath()+deptInfo.getId()+"/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		whereHql.append(" and wcc.type>?");
		params.add(contractCost.TYPE_HUMAN_COST);
		
		//查询条件
		if (contractCost.getName() != null) {
			whereHql.append(" and wcc.name like ?");
			params.add("%"+contractCost.getName()+"%");
		}
		if (contractCost.getType() != null) {
			whereHql.append(" and wcc.type = ?");
			params.add(contractCost.getType());
		}
		if (contractCost.getContractId()!= null) {
			whereHql.append(" and wcc.contractId = ?");
			params.add(contractCost.getContractId());
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
		
		ArrayList<ContractCostVo> returnList = new ArrayList<ContractCostVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returnList.add(new ContractCostVo((ContractCost)o[0], StringUtil.null2Str(o[1]), StringUtil.null2Str((o)[2]),StringUtil.nullToDouble(o[3])));
			}
		}
		return new PageImpl<>(returnList, pageable, page.getTotalElements());
	
	}

	@Override
	public ContractCostVo getContractCost(User user, DeptInfo deptInfo, Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wcc,wci.serialNum,wci.name,wcb.budgetTotal");
		queryHql.append(" from ContractCost wcc");
		queryHql.append(" left join ContractInfo wci on wci.id = wcc.contractId");
		queryHql.append(" left join ContractBudget wcb on wcb.id = wcc.budgetId");
		queryHql.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" where (wci.salesmanId = ? or wci.consultantsId = ? or wci.creator = ?");
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath()+deptInfo.getId()+"/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(") and wcc.id = ?");
		params.add(id);
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		if (list != null && !list.isEmpty()) {
			
			return new ContractCostVo((ContractCost)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]),StringUtil.nullToDouble(list.get(0)[3]));
		}
		return null;
	}
	
}
