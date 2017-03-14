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
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;
import com.wondertek.cpm.domain.vo.LongValue;

@Repository("contractBudgetDao")
public class ContractBudgetDaoImpl extends GenericDaoImpl<ContractBudget, Long> implements ContractBudgetDao {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ContractBudget> getDomainClass() {
		return ContractBudget.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ContractBudgetVo> getPageByParams(ContractBudget contractBudget,User user,DeptInfo deptInfo,Pageable pageable) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wcb,");
		queryHql.append("wci.serialNum,wci.name as contractName,wci.creator as contractCreator,wci.salesmanId,wci.consultantsId,");
		queryHql.append("wdi.id as wdiId,wdi.idPath as wdiIdPath,");
		queryHql.append("wdi2.id as wdi2Id,wdi2.idPath as wdi2IdPath,");
		queryHql.append("wdi3.id as wdi3Id,wdi3.idPath as wdi3IdPath,");
		queryHql.append("wpi.budgetId");
		
		countHql.append("select count(wci.id)");
		whereHql.append(" from ContractBudget wcb");
		whereHql.append(" left join ContractInfo wci on wci.id = wcb.contractId");
		whereHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		whereHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		whereHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		whereHql.append(" left join ProjectInfo wpi on wpi.contractId = wcb.contractId and wpi.budgetId = wcb.id");
		//权限
		whereHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++)
				+ " or wcb.userId = ?" + (count++) + " or wci.creator = ?" + (count++) + " or wcb.creator = ?" + (count++));
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		if (user.getIsManager()) {
			whereHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi3.idPath like ?" + (count++) + " or wdi3.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		
		//页面搜索条件
		if (contractBudget.getContractId() != null) {
			whereHql.append(" and wcb.contractId = ?" + (count++));
			params.add(contractBudget.getContractId());
		}
		if (contractBudget.getName() != null) {
			whereHql.append(" and wcb.name like ?" + (count++));
			params.add("%" + contractBudget.getName() + "%");
		}
		if (contractBudget.getPurchaseType() != null) {
			whereHql.append(" and wcb.purchaseType = ?" + (count++));
			params.add(contractBudget.getPurchaseType());
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
		List<ContractBudgetVo> returnList = new ArrayList<ContractBudgetVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returnList.add(transContractBudgetVo(o,user.getId(),user.getLogin(),deptInfo.getId(),deptInfo.getIdPath() + deptInfo.getId() + "/"));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
}
	
	private ContractBudgetVo transContractBudgetVo(Object[] o, Long userId, String login, Long deptId, String idPath){
//		return new ContractBudgetVo((ContractBudget)o[0],StringUtil.null2Str(o[1]),StringUtil.null2Str(o[2]));
		return new ContractBudgetVo(o,userId,login,deptId,idPath);
		
	}

	@Override
	public Boolean checkBudgetExit(ContractBudget contractBudget) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append(" select wpi from ProjectInfo wpi left join ContractBudget wcb on wpi.budgetId = wcb.id");
		queryHql.append(" where wcb.id = ?0");
		params.add(contractBudget.getId());
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		if (list == null || list.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append(" select distinct wci.id,wci.serialNum,wci.name from ContractInfo as wci");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" left join ContractBudget wcb on wci.id = wcb.contractId");
		queryHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		queryHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++)
				+ " or wcb.userId = ?" + (count++) + " or wci.creator = ?" + (count++) + " or wcb.creator = ?" + (count++));
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi3.idPath like ?" + (count++) + " or wdi3.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		
		queryHql.append(")");
		queryHql.append(" order by wci.id desc");
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		List<LongValue> resultList = new ArrayList<LongValue>();
		if (list != null) {
			for (Object[] o : list) {
				resultList.add(new LongValue(StringUtil.nullToLong(o[0]), StringUtil.null2Str(o[1]) + ":" +StringUtil.null2Str(o[2])));
			}
		}
		return resultList;
	}

	@Override
	public List<LongValue> queryUserContractBudget(User user,
			DeptInfo deptInfo, Long contractId) {
		StringBuffer queryHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select distinct wcb.id,wcb.name,wcb.contractId from ContractBudget as wcb");
		queryHql.append(" left join ContractInfo wci on wci.id = wcb.contractId");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		queryHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++) 
				+ " or wcb.userId = ?" + (count++) + " or wci.creator = ?" + (count++) + " or wcb.creator = ?" + (count++));
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi3.idPath like ?" + (count++) + " or wdi3.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		if (contractId != null ) {
			queryHql.append(" and wcb.contractId = ?" + (count++));
			params.add(contractId);
		}
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		List<LongValue> resultList = new ArrayList<LongValue>();
		if (list != null) {
			for (Object[] o : list) {
				resultList.add(new LongValue(StringUtil.nullToLong(o[0]), StringUtil.null2Str(o[1]),StringUtil.nullToLong(o[2]),null));
			}
		}
		return resultList;
	}

	@Override
	public ContractBudgetVo getUserBudget(Long id, User user,
			DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wcb,wci.name,wci.serialNum from ContractBudget as wcb");
		queryHql.append(" left join ContractInfo wci on wci.id = wcb.contractId");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		queryHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++) 
				+ " or wcb.userId = ?" + (count++) + " or wci.creator = ?" + (count++) + " or wcb.creator = ?" + (count++));
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi3.idPath like ?" + (count++) + " or wdi3.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		//搜索条件
		if (id != null) {
			queryHql.append(" and wcb.id = ?" + (count++));
			params.add(id);
		}
		List<Object[]> list = this.queryAllHql(queryHql.toString(), params.toArray());
		
		if (list != null && !list.isEmpty()) {
			return new ContractBudgetVo((ContractBudget) list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}
}
	