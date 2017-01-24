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
		queryHql.append("select wcb,");
		queryHql.append("wci.serialNum,wci.name as contractName,wci.creator as contractCreator,wci.salesmanId,wci.consultantsId,");
		queryHql.append("wdi.id as wdiId,wdi.idPath as wdiIdPath,");
		queryHql.append("wdi2.id as wdi2Id,wdi2.idPath as wdi2IdPath,");
		queryHql.append("wdi3.id as wdi3Id,wdi3.idPath as wdi3IdPath");
		
		countHql.append("select count(wci.id)");
		whereHql.append(" from ContractBudget wcb");
		whereHql.append(" left join ContractInfo wci on wci.id = wcb.contractId");
		whereHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		whereHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		whereHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		//权限
		whereHql.append(" where (wci.salesmanId = ? or wci.consultantsId = ? or wcb.userId = ? or wci.creator = ? or wcb.creator = ?");
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		if (user.getIsManager()) {
			whereHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi3.idPath like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		
		//页面搜索条件
		if (contractBudget.getContractId() != null) {
			whereHql.append(" and wcb.contractId = ?");
			params.add(contractBudget.getContractId());
		}
		if (contractBudget.getName() != null) {
			whereHql.append(" and wcb.name like ?");
			params.add("%" + contractBudget.getName() + "%");
		}
		if (contractBudget.getPurchaseType() != null) {
			whereHql.append(" and wcb.purchaseType = ?");
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
    	
		System.out.println(queryHql.toString());
		System.out.println(countHql.toString());
		System.out.println(params.size());
		
		Page<Object[]> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
		List<ContractBudgetVo> returnList = new ArrayList<ContractBudgetVo>();
		if (page.getContent() != null) {
			System.out.println(page.getContent().size());
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
		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wcb.contract_id,wpi.id from w_contract_budget wcb left join w_project_info wpi on wpi.budget_id = wcb.id");
		querySql.append(" and wcb.id = ?");
		params.add(contractBudget.getId());
		
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		if (list == null || list.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		
		queryHql.append(" select distinct wci.id,wci.serialNum,wci.name from ContractInfo as wci");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" left join ContractBudget wcb on wci.id = wcb.contractId");
		queryHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		queryHql.append(" where (wci.salesmanId = ? or wci.consultantsId = ? or wcb.userId = ? or wci.creator = ? or wcb.creator = ?");
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi3.idPath like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		
		queryHql.append(")");
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
		
		queryHql.append("select distinct wcb.id,wcb.name,wcb.contractId from ContractBudget as wcb");
		queryHql.append(" left join ContractInfo wci on wci.id = wcb.contractId");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		queryHql.append(" left join DeptInfo wdi3 on wcb.deptId = wdi3.id");
		queryHql.append(" where (wci.salesmanId = ? or wci.consultantsId = ? or wcb.userId = ? or wci.creator = ? or wcb.creator = ?");
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getIdPath() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi3.idPath like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		if (contractId != null ) {
			queryHql.append(" and wcb.contractId = ?");
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
}
	