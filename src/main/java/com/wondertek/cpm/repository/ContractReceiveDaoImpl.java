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
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractReceiveVo;
@Repository("contractReceiveDao")
public class ContractReceiveDaoImpl extends GenericDaoImpl<ContractReceive, Long> implements ContractReceiveDao{
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ContractReceive> getDomainClass() {
		return ContractReceive.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public ContractReceiveVo getContractReceive(User user, DeptInfo deptInfo, Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wcr,wci.serialNum,wci.name");
		queryHql.append(" from ContractReceive wcr");
		queryHql.append(" left join ContractInfo wci on wci.id = wcr.contractId");
		queryHql.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
		queryHql.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
		queryHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsDeptId = ?" + (count++) + " or wci.creator = ?" + (count++));
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId()+"/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(") and wcr.id = ?" + (count++));
		params.add(id);
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if (list != null && !list.isEmpty()) {
			return new ContractReceiveVo((ContractReceive)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}

	@Override
	public Page<ContractReceiveVo> getUserPage(ContractReceive contractReceive, User user, DeptInfo deptInfo,
			Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wcr,wci.serialNum,wci.name");
		countHql.append("select count(wcr.id)");
		
		whereHql.append(" from ContractReceive wcr");
		whereHql.append(" left join ContractInfo wci on wci.id = wcr.contractId");
		whereHql.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
		whereHql.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
		whereHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsDeptId = ?" + (count++) + " or wci.creator = ?" + (count++));
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			whereHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath()+deptInfo.getId()+"/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		//查询条件
		if (contractReceive.getContractId() != null) {
			whereHql.append(" and wcr.contractId = ?" + (count++));
			params.add(contractReceive.getContractId());
		}
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		//排序
		if (pageable.getSort() != null) {
			for (Order order : pageable.getSort()) {
				if (CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())) {
					continue;
				}
				if (orderHql.length() != 0) {
					orderHql.append(",");
				}else {
					orderHql.append(" order by ");
				}
				orderHql.append(order.getProperty());
				if (order.isAscending()) {
					orderHql.append(" asc");
				}else {
					orderHql.append(" desc");
				}
				
			}
		}
		queryHql.append(orderHql.toString());
		orderHql.setLength(0);
		orderHql = null;
		
		Page<Object[]> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
		List<ContractReceiveVo> returnList = new ArrayList<>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returnList.add(new ContractReceiveVo((ContractReceive)o[0], StringUtil.null2Str(o[1]),StringUtil.null2Str(o[2])));
			}
		}
		
		return new PageImpl<>(returnList, pageable, page.getTotalElements());
	}
}
