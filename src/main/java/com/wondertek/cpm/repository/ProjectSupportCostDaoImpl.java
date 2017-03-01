package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectSupportCostVo;

@Repository("projectSupportCostDao")
public class ProjectSupportCostDaoImpl extends GenericDaoImpl<ProjectSupportCost, Long> implements ProjectSupportCostDao {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ProjectSupportCost> getDomainClass() {
		return ProjectSupportCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<ProjectSupportCostVo> getAllSalePurchaseInternalPage(User user,DeptInfo deptInfo,ProjectSupportCost projectSupportCost) {
		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();

		querySql.append("select p.id, p.stat_week, p.contract_id, p.dept_type, p.user_id, p.serial_num, p.user_name, p.grade_, p.settlement_cost,");
		querySql.append(" p.project_hour_cost, p.internal_budget_cost, p.sal_, p.social_security_fund, p.other_expense, p.user_month_cost, p.user_hour_cost,");
		querySql.append(" p.product_cost, p.gross_profit, p.creator_, p.create_time, c.serial_num as contract_serial_num, wdt.name_");
		
		querySql.append(" from w_project_support_cost p");
		querySql.append(" inner join (select max(wps.stat_week) as max_stat_week,wps.contract_id as contract_id from w_project_support_cost wps where wps.stat_week <= ? group by wps.contract_id) wpsc on wpsc.contract_id = p.contract_id");
		querySql.append(" left join w_contract_info c on p.contract_id = c.id");
		querySql.append(" left join w_dept_type wdt on p.dept_type = wdt.id");
		querySql.append(" left join w_project_info wpi on p.project_id = wpi.id");
		querySql.append(" left join w_dept_info wdi on wdi.id = wpi.dept_id");
		
		params.add(projectSupportCost.getStatWeek());
		
		querySql.append(" where p.stat_week = wpsc.max_stat_week");
		//权限
		querySql.append(" and (wpi.pm_id = ? or wpi.creator_ = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		if(user.getIsManager()){
			querySql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		querySql.append(")");
		
		if(projectSupportCost.getContractId() != null){
			querySql.append(" and p.contract_id = ?");
			params.add(projectSupportCost.getContractId());
		}
		if(projectSupportCost.getUserId() != null){
			querySql.append(" and p.user_id = ?");
			params.add(projectSupportCost.getUserId());
		}
		if (projectSupportCost.getDeptType() != null) {
			querySql.append(" and p.dept_type = ?");
			params.add(projectSupportCost.getDeptType());
		}
		
		querySql.append(" order by p.dept_type asc,p.id desc");
		
    	List<Object[]> page = this.queryAllSql(querySql.toString(), params.toArray());
    	List<ProjectSupportCostVo> returnList = new ArrayList<>();
    	if(page != null){
			for(Object[] o : page){
				returnList.add(transProjectSupportCostVo(o));
			}
		}
    	return returnList;
	}
	private ProjectSupportCostVo transProjectSupportCostVo(Object[] o){
		ProjectSupportCostVo projectSupportCostVo = new ProjectSupportCostVo();
		projectSupportCostVo.setId(StringUtil.nullToLong(o[0]));
		projectSupportCostVo.setStatWeek(StringUtil.nullToLong(o[1]));
		projectSupportCostVo.setContractId(StringUtil.nullToLong(o[2]));
		projectSupportCostVo.setDeptType(StringUtil.nullToLong(o[3]));
		projectSupportCostVo.setUserId(StringUtil.nullToLong(o[4]));
		projectSupportCostVo.setSerialNum(StringUtil.null2Str(o[5]));
		projectSupportCostVo.setUserName(StringUtil.null2Str(o[6]));
		projectSupportCostVo.setGrade(StringUtil.nullToInteger(o[7]));
		projectSupportCostVo.setSettlementCost(StringUtil.nullToDouble(o[8]));
		projectSupportCostVo.setProjectHourCost(StringUtil.nullToDouble(o[9]));
		projectSupportCostVo.setInternalBudgetCost(StringUtil.nullToDouble(o[10]));
		projectSupportCostVo.setSal(StringUtil.nullToDouble(o[11]));
		projectSupportCostVo.setSocialSecurityFund(StringUtil.nullToDouble(o[12]));
		projectSupportCostVo.setOtherExpense(StringUtil.nullToDouble(o[13]));
		projectSupportCostVo.setUserMonthCost(StringUtil.nullToDouble(o[14]));
		projectSupportCostVo.setUserHourCost(StringUtil.nullToDouble(o[15]));
		projectSupportCostVo.setProductCost(StringUtil.nullToDouble(o[16]));
		projectSupportCostVo.setGrossProfit(StringUtil.nullToDouble(o[17]));
		projectSupportCostVo.setCreator(StringUtil.null2Str(o[18]));
		projectSupportCostVo.setCreateTime(DateUtil.getZonedDateTime((Timestamp)o[19]));
		projectSupportCostVo.setContractSerialNum(StringUtil.null2Str(o[20]));
		projectSupportCostVo.setDeptName(StringUtil.null2Str(o[21]));
		return projectSupportCostVo;
	}

	@Override
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalDetailPage(User user,DeptInfo deptInfo,ProjectSupportCost projectSupportCost, Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select p.id, p.stat_week, p.contract_id, p.dept_type, p.user_id, p.serial_num, p.user_name, p.grade_, p.settlement_cost,");
		querySql.append(" p.project_hour_cost, p.internal_budget_cost, p.sal_, p.social_security_fund, p.other_expense, p.user_month_cost, p.user_hour_cost,");
		querySql.append(" p.product_cost, p.gross_profit, p.creator_, p.create_time, c.serial_num as contract_serial_num, wdt.name_");
		
		countSql.append(" select count(distinct p.id)");
		
		whereSql.append(" from w_project_support_cost p");
		whereSql.append(" left join w_contract_info c on p.contract_id = c.id");
		whereSql.append(" left join w_dept_type wdt on p.dept_type = wdt.id");
		whereSql.append(" left join w_project_info wpi on p.project_id = wpi.id");
		whereSql.append(" left join w_dept_info wdi on wdi.id = wpi.dept_id");
		
		whereSql.append(" where (wpi.pm_id = ? or wpi.creator_ = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		if(projectSupportCost.getContractId() != null){
			whereSql.append(" and p.contract_id = ?");
			params.add(projectSupportCost.getContractId());
		}
		if(projectSupportCost.getDeptType() != null){
			whereSql.append(" and p.dept_type = ?");
			params.add(projectSupportCost.getDeptType());
		}
		if(projectSupportCost.getUserId() != null){
			whereSql.append(" and p.user_id = ?");
			params.add(projectSupportCost.getUserId());
		}
		StringBuffer orderSql = new StringBuffer();
		orderSql.append(" order by p.id desc");
		
    	Page<Object[]> page = this.querySqlPage(
    			querySql.toString() + whereSql.toString() + orderSql.toString(), 
    			countSql.toString() + whereSql.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<ProjectSupportCostVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectSupportCostVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
}
