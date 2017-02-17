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
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.vo.ProjectOverallVo;
@Repository("projectOverallDao")
public class ProjectOverallDaoImpl extends GenericDaoImpl<ProjectOverall, Long> implements ProjectOverallDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectOverall> getDomainClass() {
		return ProjectOverall.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ProjectOverallVo> getPageByParams(String fromDate,String toDate,String contractId,String userId,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpo.id,wpo.contract_response,wpo.contract_id,wpo.contract_amount,wpo.tax_rate,wpo.identifiable_income,wpo.contract_finish_rate,wpo.acceptance_income,wpo.receive_total,wpo.receivable_account,wpo.share_cost,wpo.third_party_purchase,wpo.internal_purchase,wpo.bonus_,wpo.gross_profit,wpo.gross_profit_rate,wci.serial_num,wci.sales_man,wci.consultants_");
		countSql.append("select count(wpo.id)");
		
		whereSql.append(" from w_project_overall wpo");
		whereSql.append(" inner join ");
		whereSql.append("(");
		whereSql.append("select max(wpo1.id) as id,wpo1.contract_id from w_project_overall wpo1 group by wpo1.contract_id");
		whereSql.append(")");
		whereSql.append(" b on wpo.id = b.id");
		whereSql.append(" left join w_contract_info wci on wci.id = wpo.contract_id");
		whereSql.append(" where 1=1");
		
		//查询条件
		if(!StringUtil.isNullStr(fromDate)){
			whereSql.append(" and wpo.stat_week >= ?");
			params.add(StringUtil.nullToLong(fromDate));
		}
		if (!StringUtil.isNullStr(toDate)) {
			whereSql.append(" and wpo.stat_week <= ?");
			params.add(StringUtil.nullToLong(toDate));
		}
		if (!StringUtil.isNullStr(contractId)) {
			whereSql.append(" and wpo.contract_id = ?");
			params.add(StringUtil.nullToLong(contractId));
		}
		if (!StringUtil.isNullStr(userId)) {
			whereSql.append(" and wpo.contract_response = ?");
			params.add(StringUtil.nullToLong(userId));
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		
		//排序
		if(pageable.getSort() != null){//页面都会有个默认排序
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderSql.length() != 0){
    				orderSql.append(",");
    			}else{
    				orderSql.append(" order by ");
    			}
   				orderSql.append(order.getProperty());
    			if(order.isAscending()){
    				orderSql.append(" asc");
    			}else{
    				orderSql.append(" desc");
    			}
    		}
    	}
		querySql.append(orderSql.toString());
		orderSql.setLength(0);
		orderSql = null;
		
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		System.out.println(page.getContent());
		List<ProjectOverallVo> returnList = new ArrayList<ProjectOverallVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectOverallVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	private ProjectOverallVo transProjectOverallVo(Object[] o) {
		ProjectOverallVo vo = new ProjectOverallVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setContractResponse(StringUtil.nullToLong(o[1]));
		vo.setContractId(StringUtil.nullToLong(o[2]));
		vo.setContractAmount(StringUtil.nullToDouble(o[3]));
		vo.setTaxRate(StringUtil.nullToDouble(o[4]));
		vo.setIdentifiableIncome(StringUtil.nullToDouble(o[5]));
		vo.setContractFinishRate(StringUtil.nullToDouble(o[6]));
		vo.setAcceptanceIncome(StringUtil.nullToDouble(o[7]));
		vo.setReceiveTotal(StringUtil.nullToDouble(o[8]));
		vo.setReceivableAccount(StringUtil.nullToDouble(o[9]));
		vo.setShareCost(StringUtil.nullToDouble(o[10]));
		vo.setThirdPartyPurchase(StringUtil.nullToDouble(o[11]));
		vo.setInternalPurchase(StringUtil.nullToDouble(o[12]));
		vo.setBonus(StringUtil.nullToDouble(o[13]));
		vo.setGrossProfit(StringUtil.nullToDouble(o[14]));
		vo.setGrossProfitRate(StringUtil.nullToDouble(o[15]));
		vo.setSerialNum(StringUtil.null2Str(o[16]));
		vo.setSalesman(StringUtil.null2Str(o[17]));
		vo.setConsultants(StringUtil.null2Str(o[18]));
		
		return vo;
	}

	@Override
	public Page<ProjectOverallVo> getPageDetai(String contractId,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpo.id,wpo.contract_response,wpo.contract_id,wpo.contract_amount,wpo.tax_rate,wpo.identifiable_income,wpo.contract_finish_rate,wpo.acceptance_income,wpo.receive_total,wpo.receivable_account,wpo.share_cost,wpo.third_party_purchase,wpo.internal_purchase,wpo.bonus_,wpo.gross_profit,wpo.gross_profit_rate,wci.serial_num,wci.sales_man,wci.consultants_");
		countSql.append("select count(wpo.id)");
		
		whereSql.append(" from w_project_overall wpo");
		whereSql.append(" left join w_contract_info wci on wci.id = wpo.contract_id");
		whereSql.append(" where 1=1");
		
		if (!StringUtil.isNullStr(contractId)) {
			whereSql.append(" and wpo.contract_id = ?");
			params.add(StringUtil.nullToLong(contractId));
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<ProjectOverallVo> returnList = new ArrayList<ProjectOverallVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectOverallVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
}