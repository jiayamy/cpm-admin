package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractWeeklyStatVo;
import com.wondertek.cpm.domain.vo.LongValue;

@Repository("contractWeeklyStatDao")
public class ContractWeeklyStatDaoImpl extends GenericDaoImpl<ContractWeeklyStat, Long> implements ContractWeeklyStatDao{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ContractWeeklyStat> getDomainClass() {
		return ContractWeeklyStat.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public Page<ContractWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable,
			User user) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	sb.append("where 1=1");
    	if(!StringUtil.isNullStr(beginDate)){
    		Date bgDate = DateUtil.parseDate("yyyyMMdd", beginDate);
    		ZonedDateTime bg = DateUtil.getZonedDateTime(bgDate.getTime());
    		sb.append(" and createTime >= ?");
    		params.add(bg);
    	}
    	if(!StringUtil.isNullStr(endDate)){
    		Date edDate = DateUtil.parseDate("yyyyMMdd", endDate);
    		ZonedDateTime ed = DateUtil.getZonedDateTime(edDate.getTime());
    		sb.append(" and createTime <= ?");
    		params.add(ed);
    	}
    	if(!StringUtil.isNullStr(statDate)){
    		String[] dates = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", statDate));
    		Long st = StringUtil.nullToLong(dates[6]);
    		sb.append(" and statWeek = ?");
    		params.add(st);
    	}
//    	sb.append(" and status = ?");
//    	params.add(CpmConstants.STATUS_VALID);
    	StringBuffer orderHql = new StringBuffer();
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
    			if(order.isAscending()){
    				orderHql.append(order.getProperty()).append(" asc");
    			}else{
    				orderHql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
    	Page<ContractWeeklyStat> page = this.queryHqlPage(
    			"from ContractWeeklyStat " + sb.toString() + orderHql.toString(), 
    			"select count(id) from ContractWeeklyStat " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}

	@Override
	public Page<ContractWeeklyStatVo> getUserPage(String contractId, Pageable pageable, User user) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.contractId, m.finishRate, m.receiveTotal, m.costTotal, m.grossProfit, m.salesHumanCost,"
				+ "m.salesPayment , m.consultHumanCost ,m.consultPayment ,m.hardwarePurchase ,m.externalSoftware ,m.internalSoftware ,m.projectHumanCost ,"
				+ "m.projectPayment ,m.statWeek ,m.createTime , i.serialNum , i.name");
		countsql.append(" select count(m.id)");
		sb.append(" from ContractWeeklyStat m");
		sb.append(" left join ContractInfo i on m.contractId = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from ContractWeeklyStat where 1=1 ");
    	if(!StringUtil.isNullStr(contractId)){
    		sb.append(" and contractId = ?");
    		params.add(StringUtil.nullToLong(contractId));
    	}
    	sb.append(" group by contractId");
    	sb.append(" )");
    	StringBuffer orderHql = new StringBuffer();
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
    			if(order.isAscending()){
    				orderHql.append(order.getProperty()).append(" asc");
    			}else{
    				orderHql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
    	Page<Object[]> page = this.queryHqlPage(
    			querysql.toString() + sb.toString() + orderHql.toString(), 
    			countsql.toString() + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<ContractWeeklyStatVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transContractWeeklyStatVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	@Override
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		//只有项目经理，预算指定经理，项目创建人，项目部门以上部门管理人员能看到对应的项目
		
		sql.append("select a.id,a.serial_num,a.name_ from w_contract_info a where a.id in(");
			sql.append("select distinct b.contract_id from (");
				sql.append("select c.contract_id as contract_id from w_project_info c left join w_dept_info d on d.id = c.dept_id");
				sql.append(" where c.pm_id = ? or c.creator_ = ?");
				params.add(user.getId());
				params.add(user.getLogin());
				
				if(user.getIsManager()){
					sql.append(" or d.id_path like ? or d.id = ?");
					params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
					params.add(deptInfo.getId());
				}
				sql.append(" union all ");
				sql.append("select e.contract_id as contract_id from w_contract_budget e left join w_dept_info f on f.id = e.dept_id");
				sql.append(" where (e.user_id = ?");
				params.add(user.getId());
				if(user.getIsManager()){
					sql.append(" or f.id_path like ? or f.id = ?");
					params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
					params.add(deptInfo.getId());
				}
				sql.append(") and e.type_ = ? and e.purchase_type = ?");
				params.add(ContractBudget.TYPE_PURCHASE);
				params.add(ContractBudget.PURCHASETYPE_SERVICE);
			sql.append(") b");
		sql.append(") order by a.id desc");
		
		List<Object[]> list = this.queryAllSql(sql.toString(), params.toArray());
		
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return returnList;
	}

	@Override
	public ContractWeeklyStatVo getById(Long id) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.contract_id, m.finish_rate, m.receive_total, m.cost_total, m.gross_profit, m.sales_human_cost,"
				+ "m.sales_payment , m.consult_human_cost ,m.consult_payment ,m.hardware_purchase ,m.external_software ,m.internal_software ,m.project_human_cost ,"
				+ "m.project_payment ,m.stat_week ,m.create_time , i.serial_num , i.name_");
		countsql.append(" select count(m.id)");
		sb.append(" from w_contract_weekly_stat m");
		sb.append(" left join w_contract_info i on m.contract_id = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from w_contract_weekly_stat where 1=1 ");
    	if(!StringUtil.isNullStr(id)){
    		sb.append(" and id = ?");
    		params.add(id);
    	}
    	sb.append(" group by contract_id");
    	sb.append(" )");
    	
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transContractWeeklyStatVo2(list.get(0));
		}
		return null;
	}
	
	private ContractWeeklyStatVo transContractWeeklyStatVo(Object[] o){
		ContractWeeklyStatVo contractWeeklyStatVo = new ContractWeeklyStatVo();
		contractWeeklyStatVo.setId(StringUtil.nullToLong(o[0]));
		contractWeeklyStatVo.setContractId(StringUtil.nullToLong(o[1]));
		contractWeeklyStatVo.setFinishRate(StringUtil.nullToDouble(o[2]));
		contractWeeklyStatVo.setReceiveTotal(StringUtil.nullToDouble(o[3]));
		contractWeeklyStatVo.setCostTotal(StringUtil.nullToDouble(o[4]));
		contractWeeklyStatVo.setGrossProfit(StringUtil.nullToDouble(o[5]));
		contractWeeklyStatVo.setSalesHumanCost(StringUtil.nullToDouble(o[6]));
		contractWeeklyStatVo.setSalesPayment(StringUtil.nullToDouble(o[7]));
		contractWeeklyStatVo.setConsultHumanCost(StringUtil.nullToDouble(o[8]));
		contractWeeklyStatVo.setConsultPayment(StringUtil.nullToDouble(o[9]));
		contractWeeklyStatVo.setHardwarePurchase(StringUtil.nullToDouble(o[10]));
		contractWeeklyStatVo.setExternalSoftware(StringUtil.nullToDouble(o[11]));
		contractWeeklyStatVo.setInternalSoftware(StringUtil.nullToDouble(o[12]));
		contractWeeklyStatVo.setProjectHumanCost(StringUtil.nullToDouble(o[13]));
		contractWeeklyStatVo.setProjectPayment(StringUtil.nullToDouble(o[14]));
		contractWeeklyStatVo.setStatWeek(StringUtil.nullToLong(o[15]));
		contractWeeklyStatVo.setCreateTime((ZonedDateTime) o[16]);
		contractWeeklyStatVo.setSerialNum(StringUtil.null2Str(o[17]));
		contractWeeklyStatVo.setName(StringUtil.null2Str(o[18]));
		return contractWeeklyStatVo;
	}
	
	private ContractWeeklyStatVo transContractWeeklyStatVo2(Object[] o){
		ContractWeeklyStatVo contractWeeklyStatVo = new ContractWeeklyStatVo();
		contractWeeklyStatVo.setId(StringUtil.nullToLong(o[0]));
		contractWeeklyStatVo.setContractId(StringUtil.nullToLong(o[1]));
		contractWeeklyStatVo.setFinishRate(StringUtil.nullToDouble(o[2]));
		contractWeeklyStatVo.setReceiveTotal(StringUtil.nullToDouble(o[3]));
		contractWeeklyStatVo.setCostTotal(StringUtil.nullToDouble(o[4]));
		contractWeeklyStatVo.setGrossProfit(StringUtil.nullToDouble(o[5]));
		contractWeeklyStatVo.setSalesHumanCost(StringUtil.nullToDouble(o[6]));
		contractWeeklyStatVo.setSalesPayment(StringUtil.nullToDouble(o[7]));
		contractWeeklyStatVo.setConsultHumanCost(StringUtil.nullToDouble(o[8]));
		contractWeeklyStatVo.setConsultPayment(StringUtil.nullToDouble(o[9]));
		contractWeeklyStatVo.setHardwarePurchase(StringUtil.nullToDouble(o[10]));
		contractWeeklyStatVo.setExternalSoftware(StringUtil.nullToDouble(o[11]));
		contractWeeklyStatVo.setInternalSoftware(StringUtil.nullToDouble(o[12]));
		contractWeeklyStatVo.setProjectHumanCost(StringUtil.nullToDouble(o[13]));
		contractWeeklyStatVo.setProjectPayment(StringUtil.nullToDouble(o[14]));
		contractWeeklyStatVo.setStatWeek(StringUtil.nullToLong(o[15]));
		contractWeeklyStatVo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[16]));
		contractWeeklyStatVo.setSerialNum(StringUtil.null2Str(o[17]));
		contractWeeklyStatVo.setName(StringUtil.null2Str(o[18]));
		return contractWeeklyStatVo;
	}
}
