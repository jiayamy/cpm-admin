package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
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
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ContractUserVo;
import com.wondertek.cpm.domain.vo.LongValue;
@Repository("contractUserDao")
public class ContractUserDaoImpl extends GenericDaoImpl<ContractUser, Long> implements ContractUserDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ContractUser> getDomainClass() {
		return ContractUser.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<LongValue> getByUserAndDay(Long userId,Long[] weekDays) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select distinct cu.contract_id,ci.serial_num,ci.name_ from w_contract_user cu left join w_contract_info ci on cu.contract_id = ci.id where ci.id is not null and cu.user_id = ?");
		params.add(userId);
		if(weekDays != null && weekDays.length > 0){
			sql.append(" and (");
			for(int i = 0 ; i < weekDays.length; i++){
				if(i != 0){
					sql.append(" or ");
				}
				sql.append("(cu.join_day <= ? and (cu.leave_day is null or cu.leave_day >= ?))");
				params.add(weekDays[i]);
				params.add(weekDays[i]);
			}
			sql.append(")");
		}
		
		List<Object[]> list = this.queryAllSql(sql.toString(),params.toArray());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),UserTimesheet.TYPE_CONTRACT,StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return returnList;
	}

	@Override
	public Page<ContractUserVo> getUserPage(ContractUser contractUser, User user, DeptInfo deptInfo,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wcu.id,wcu.contract_id,wcu.user_id,wcu.user_name,wcu.dept_id,wcu.dept_,wcu.join_day,wcu.leave_day,wcu.creator_,wcu.create_time,wcu.updator_,wcu.update_time");
		querySql.append(",wci.serial_num,wci.name_");
		
		countSql.append("select count(wcu.id)");
		
		whereSql.append(" from w_contract_user wcu");
		whereSql.append(" left join w_contract_info wci on wci.id = wcu.contract_id");
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		whereSql.append(" where (wci.sales_man_id = ? or wci.consultants_id = ? or wci.creator_ = ?");
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
		}
		whereSql.append(")");
		
		//查询条件
		if (contractUser.getContractId() != null) {
			whereSql.append(" and wcu.contract_id = ?");
			params.add(contractUser.getContractId());
		}
		if (contractUser.getUserId() != null) {
			whereSql.append(" and wcu.user_id = ?");
			params.add(contractUser.getUserId());
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
		List<ContractUserVo> returnList = new ArrayList<ContractUserVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returnList.add(transContractUserVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private ContractUserVo transContractUserVo(Object[] o) {
		ContractUserVo vo = new ContractUserVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setContractId(StringUtil.nullToLong(o[1]));
		vo.setUserId(StringUtil.nullToLong(o[2]));
		vo.setUserName(StringUtil.null2Str(o[3]));
		vo.setDeptId(StringUtil.nullToLong(o[4]));
		vo.setDept(StringUtil.null2Str(o[5]));
		vo.setJoinDay(StringUtil.nullToCloneLong(o[6]));
		vo.setLeaveDay(StringUtil.nullToCloneLong(o[7]));
		vo.setCreator(StringUtil.null2Str(o[8]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[9]));
		vo.setUpdator(StringUtil.null2Str(o[10]));
		vo.setUpdateTime(DateUtil.getZonedDateTime((Timestamp) o[11]));
		
		vo.setContractNum(StringUtil.null2Str(o[12]));
		vo.setContractName(StringUtil.null2Str(o[13]));
		return vo;
		
	}

	@Override
	public ContractUserVo getContractUser(User user, DeptInfo deptInfo, Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wcu,wci.serialNum,wci.name");
		queryHql.append(" from ContractUser wcu");
		queryHql.append(" left join ContractInfo wci on wci.id = wcu.contractId ");
		queryHql.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
		queryHql.append(" left join DeptInfo wdi2 on  wdi2.id = wci.consultantsDeptId");
		queryHql.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++) + " or wci.creator = ?" + (count++));
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(") and wcu.id = ?" + (count++));
		params.add(id);
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new ContractUserVo((ContractUser)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}

	@Override
	public int updateLeaveDayByContract(Long contractId, long leaveDay, String updator) {
		return this.excuteHql("update ContractUser set leaveDay = ?0 , updator = ?1, updateTime = ?2 where (leaveDay is null or leaveDay > ?3) and contractId = ?4", 
				new Object[]{leaveDay,updator,ZonedDateTime.now(),leaveDay,contractId});
	}
}
