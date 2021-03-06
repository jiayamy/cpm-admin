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
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.vo.UserCostVo;

@Repository("userCostDao")
public class UserCostDaoImpl extends GenericDaoImpl<UserCost, Long> implements UserCostDao{

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<UserCost> getDomainClass() {
		return UserCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<UserCostVo> getUserCostPage(UserCostVo userCostVo, Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		
		querySql.append("select wuc.id,wuc.user_id,wuc.user_name,wuc.cost_month,wuc.internal_cost,wuc.external_cost,wuc.status_");
		querySql.append(",wuc.updator_,wuc.update_time,wuc.sal_,wuc.social_security,wuc.fund_,wuc.social_security_fund,wuc.other_expense,ju.serial_num");
		
		countSql.append("select count(wuc.id)");
		
		whereSql.append(" from w_user_cost wuc");
		whereSql.append(" left join jhi_user ju on wuc.user_id = ju.id");
		
		List<Object> params = new ArrayList<Object>();
		
		whereSql.append(" where 1 = 1");
		if(userCostVo.getSerialNum() != null){
			whereSql.append(" and ju.serial_num like ?");
			params.add("%" + userCostVo.getSerialNum() + "%");
		}
		if(userCostVo.getUserName() != null){
			whereSql.append(" and wuc.user_name like ?");
			params.add("%" + userCostVo.getUserName() + "%");
		}
		if(userCostVo.getCostMonth() != null){
			whereSql.append(" and wuc.cost_month = ?");
			params.add(userCostVo.getCostMonth());
		}
		if(userCostVo.getStatus() != null){
			whereSql.append(" and wuc.status_ = ?");
			params.add(userCostVo.getStatus());
		}
		
		querySql.append(whereSql);
		countSql.append(whereSql);
		whereSql.setLength(0);
		whereSql = null;
		
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
    			if(order.isAscending()){
    				orderSql.append(order.getProperty()).append(" asc");
    			}else{
    				orderSql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
		querySql.append(orderSql.toString());
		orderSql.setLength(0);
		orderSql = null;
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<UserCostVo> returnList = new ArrayList<UserCostVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transUserCostVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private UserCostVo transUserCostVo(Object[] o) {
		UserCostVo vo = new UserCostVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setUserId(StringUtil.nullToLong(o[1]));
		vo.setUserName(StringUtil.null2Str(o[2]));
		vo.setCostMonth(StringUtil.nullToLong(o[3]));
		vo.setInternalCost(StringUtil.nullToDouble(o[4]));
		vo.setExternalCost(StringUtil.nullToDouble(o[5]));
		vo.setStatus(StringUtil.nullToInteger(o[6]));
		vo.setUpdator(StringUtil.null2Str(o[7]));
		vo.setUpdateTime(DateUtil.getZonedDateTime((Timestamp) o[8]));
		vo.setSal(StringUtil.nullToDouble(o[9]));
		vo.setSocialSecurity(StringUtil.nullToDouble(o[10]));
		vo.setFund(StringUtil.nullToDouble(o[11]));
		vo.setSocialSecurityFund(StringUtil.nullToDouble(o[12]));
		vo.setOtherExpense(StringUtil.nullToDouble(o[13]));
		vo.setSerialNum(StringUtil.null2Str(o[14]));
		return vo;
	}

	public List<Object[]> findAllMaxByCostMonth(Long costMonth){
		StringBuffer querySql = new StringBuffer();
		querySql.append("select wuc.user_id,wuc.sal_");
		
		querySql.append(" from w_user_cost wuc");
		querySql.append(" inner join (select max(cost_month) as cost_month,user_id from w_user_cost where cost_month <= ? and status_ = 1 group by user_id) wucm");
		querySql.append(" on wuc.user_id = wucm.user_id and wuc.cost_month = wucm.cost_month");
		
		querySql.append(" order by wuc.id asc");
		
		return this.queryAllSql(querySql.toString(), new Object[]{costMonth});
	}
}
