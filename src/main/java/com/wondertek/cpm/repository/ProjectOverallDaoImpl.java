package com.wondertek.cpm.repository;

import java.sql.Timestamp;
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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.User;
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
	public Page<ProjectOverallVo> getPageByParams(User user,DeptInfo deptInfo,ProjectOverall projectOverall,Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpo.id,wpo.contract_response,wpo.contract_id,wpo.contract_amount,wpo.tax_rate,wpo.identifiable_income,wpo.contract_finish_rate,wpo.acceptance_income,wpo.receive_total,wpo.receivable_account,wpo.share_cost,wpo.third_party_purchase,wpo.internal_purchase,wpo.bonus_,wpo.gross_profit,wpo.gross_profit_rate,wci.serial_num,wci.sales_man,wci.consultants_,c.ta1,c.ta2,wpo.stat_week,wpo.create_time");
		countSql.append("select count(wpo.id)");
		
		whereSql.append(" from w_project_overall wpo");
		whereSql.append(" inner join ");
		whereSql.append("(");
		whereSql.append("select max(wpo1.id) as id,wpo1.contract_id from w_project_overall wpo1 where wpo1.stat_week <= ? group by wpo1.contract_id");
		whereSql.append(")");
		whereSql.append(" b on wpo.id = b.id");
		whereSql.append(" inner join w_contract_info wci on wci.id = wpo.contract_id");
		whereSql.append(" left join (select a.contract_id,wcip1.total_amount as ta1,wcip2.total_amount as ta2 from");
		whereSql.append("(select wcip.contract_id,max(case when wcip.dept_type = " + DeptType.PROJECT_IMPLEMENTATION + " then wcip.id end) as dept_type_5,");
		whereSql.append(" max(case when wcip.dept_type = " +DeptType.PRODUCT_DEVELOPMENT + " then wcip.id end) as dept_type_4 from w_contract_internal_purchase wcip where wcip.stat_week <= ? group by wcip.contract_id) a");
		whereSql.append(" left join w_contract_internal_purchase wcip1 on wcip1.id = a.dept_type_5");
		whereSql.append(" left join w_contract_internal_purchase wcip2 on wcip2.id = a.dept_type_4) c");
		whereSql.append(" on wpo.contract_id = c.contract_id");
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		//
		params.add(projectOverall.getStatWeek());
		params.add(projectOverall.getStatWeek());
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//查询条件
		if (projectOverall.getContractId() != null) {
			whereSql.append(" and wpo.contract_id = ?");
			params.add(projectOverall.getContractId());
		}
		if (projectOverall.getContractResponse() != null) {
			whereSql.append(" and wpo.contract_response = ?");
			params.add(projectOverall.getContractResponse());
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
		vo.setImplementationCost(StringUtil.nullToDouble(o[19]));
		vo.setAcademicCost(StringUtil.nullToDouble(o[20]));
		vo.setStatWeek(StringUtil.nullToLong(o[21]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp)o[22]));
		
		return vo;
	}

	@Override
	public Page<ProjectOverallVo> getPageDetail(Long contractId,User user,DeptInfo deptInfo,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpo.id,wpo.contract_response,wpo.contract_id,wpo.contract_amount,wpo.tax_rate,wpo.identifiable_income,wpo.contract_finish_rate,wpo.acceptance_income,wpo.receive_total,wpo.receivable_account,wpo.share_cost,wpo.third_party_purchase,wpo.internal_purchase,wpo.bonus_,wpo.gross_profit,wpo.gross_profit_rate,wci.serial_num,wci.sales_man,wci.consultants_,c.total_amount_5,c.total_amount_4,wpo.stat_week,wpo.create_time");
		countSql.append("select count(wpo.id)");
		
		whereSql.append(" from w_project_overall wpo");
		whereSql.append(" inner join w_contract_info wci on wci.id = wpo.contract_id");
		whereSql.append(" left join (select wcip.project_overall_id,max(case when wcip.dept_type = " + DeptType.PROJECT_IMPLEMENTATION + " then wcip.total_amount end) as total_amount_5,");
		whereSql.append(" max(case when wcip.dept_type = " + DeptType.PRODUCT_DEVELOPMENT + " then wcip.total_amount end) as total_amount_4");
		whereSql.append(" from w_contract_internal_purchase wcip where wcip.contract_id = ? group by wcip.project_overall_id) c");
		whereSql.append(" on c.project_overall_id = wpo.id");
		params.add(contractId);
		
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		if (contractId != null) {
			whereSql.append(" and wpo.contract_id = ?");
			params.add(contractId);
		}
		querySql.append(whereSql.toString());
		querySql.append(" order by wpo.stat_week desc");
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

	@Override
	public ProjectOverallVo getUserProjectOverall(Long id, User user,
			DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querySql.append("select wpo");
		
		whereSql.append(" from ProjectOverall as wpo");
		whereSql.append(" left join ContractInfo wci on wci.id = wpo.contractId");
		whereSql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		whereSql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		//权限
		whereSql.append(" where (wci.creator = ?" + (count++) + " or wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++));
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//搜索条件
		if (id != null) {
			whereSql.append(" and wpo.id = ?" + (count++));
			params.add(id);
		}
		querySql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		List<ProjectOverall> list = this.queryAllHql(querySql.toString(), params.toArray());
		if (list != null && !list.isEmpty()) {
			return new ProjectOverallVo(list.get(0));
		}
		return null;
	}
}