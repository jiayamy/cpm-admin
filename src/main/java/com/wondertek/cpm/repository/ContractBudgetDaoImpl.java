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
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;

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
	public Page<ContractBudgetVo> getPageByParams(String name, String serialNum,Pageable pageable) {
		StringBuffer sql = new StringBuffer();
		sql.append(" from w_contract_info ci inner join w_contract_budget cb on cb.contract_id = ci.id where 1=1");
		List<Object> params = new ArrayList<Object>();
		if (!StringUtil.isNullStr(serialNum)) {
			sql.append(" and ci.serial_num = ?");
			params.add(serialNum);
		}
		if (!StringUtil.isNullStr(name)) {
			sql.append(" and ci.name_ like ?");
			params.add("%" + name + "%");
		}
		StringBuffer orderSql = new StringBuffer();
    	if(pageable.getSort() != null){
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderSql.length() != 0){
    				orderSql.append(",");
    			}else{
    				orderSql.append(" order by ");
    			}
//    			if ("id".equals(order.getProperty())) {
//					orderSql.append("cb.id");
//				}else if ("serial_num".equals(order.getProperty())) {
//					orderSql.append("ci.serial_num");
//				}else if ("name_".equals(order.getProperty())) {
//					orderSql.append("ci.name_");
//				}else if ("user_name".equals(order.getProperty())) {
//					orderSql.append("cb.user_name");
//				}else if ("dept_".equals(order.getProperty())) {
//					orderSql.append("cb.dept_");
//				}else if ("budget_total".equals(order.getProperty())) {
//					orderSql.append("cb.budget_total");
//				}else if ("status_".equals(order.getProperty())) {
//					orderSql.append("cb.status_");
//				}else if ("create_time".equals(order.getProperty())) {
//					orderSql.append("cb.create_time");
//				}else if ("update_time".equals(order.getProperty())) {
//					orderSql.append("cb.update_time");
//				}else{
//					orderSql.append(order.getProperty());
//				}
    			orderSql.append(order.getProperty());
    			if(order.isAscending()){
    				orderSql.append(" asc");
    			}else{
    				orderSql.append(" desc");
    			}
    	}
    		
   }
    	String querySql = "select cb.id,ci.name_,ci.serial_num,cb.budget_total,cb.user_name,cb.dept_,cb.status_,cb.create_time,cb.update_time,cb.purchase_type" + sql.toString() + orderSql.toString();
		String countSql = "select count(ci.id)" + sql.toString();
		Page<Object[]> page = this.querySqlPage(querySql, countSql, params.toArray(), pageable);
		List<ContractBudgetVo> returnList = new ArrayList<ContractBudgetVo>();
		if (page.getContent() != null) {
			DateTimeFormatter format = DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_MS_PATTERN);
			for (Object[] o : page.getContent()) {
					returnList.add(transContractBudgetVo(o,format));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
}
	
	private ContractBudgetVo transContractBudgetVo(Object[] o,DateTimeFormatter format){
		ContractBudgetVo vo = new ContractBudgetVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setName(StringUtil.null2Str(o[1]));
		vo.setSerialNum(StringUtil.null2Str(o[2]));
		vo.setBudgetTotal(StringUtil.nullToDouble(o[3]));
		vo.setUserName(StringUtil.null2Str(o[4]));
		vo.setDept(StringUtil.null2Str(o[5]));
		vo.setStatus(StringUtil.nullToInteger(o[6]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[7]));
		vo.setUpdateTime(DateUtil.getZonedDateTime((Timestamp) o[8]));
		vo.setPurchaseType(StringUtil.nullToInteger(o[9]));

		return vo;
		
	}
}
	