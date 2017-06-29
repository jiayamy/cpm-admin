package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ProjectUserInputVo;
import com.wondertek.cpm.domain.vo.UserProjectInputVo;
import com.wondertek.cpm.domain.vo.UserTimesheetForHardWorkingVo;
@Repository("userTimesheetDao")
public class UserTimesheetDaoImpl extends GenericDaoImpl<UserTimesheet, Long> implements UserTimesheetDao  {
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserTimesheetRepository userTimesheetRepository;
	
	@Autowired
	private SystemConfigRepository systemConfigRepository;

	@Override
	public Class<UserTimesheet> getDomainClass() {
		return UserTimesheet.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable, User user) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append("where 1=1");
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and objName like ?" + (count++));
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and objId = ?" + (count++));
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getType() != null){
    		sb.append(" and type = ?" + (count++));
    		params.add(userTimesheet.getType());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and userId = ?" + (count++));
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and workDay = ?" + (count++));
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and status = ?" + (count++));
    	params.add(CpmConstants.STATUS_VALID);
    	sb.append(" and userId = ?" + (count++));
    	params.add(user.getId());
    	
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
    	Page<UserTimesheet> page = this.queryHqlPage(
    			"from UserTimesheet " + sb.toString() + orderHql.toString(), 
    			"select count(id) from UserTimesheet " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}
	@Override
	public Page<UserTimesheet> getContractPage(UserTimesheet userTimesheet, User user, DeptInfo deptInfo,Pageable pageable) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append(" left join ContractInfo wci on wci.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
    	sb.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
    	
    	sb.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++));
    	params.add(user.getId());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ?" + (count++) + " or wdi.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    		
    		sb.append(" or wdi2.id = ?" + (count++) + " or wdi2.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and wut.objName like ?" + (count++));
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and wut.objId = ?" + (count++));
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and wut.userId = ?" + (count++));
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and wut.workDay = ?" + (count++));
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and wut.type = ?" + (count++));
    	params.add(UserTimesheet.TYPE_CONTRACT);
    	sb.append(" and wut.status = ?" + (count++));
    	params.add(CpmConstants.STATUS_VALID);
    	
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
    	Page<UserTimesheet> page = this.queryHqlPage(
    			"select wut from UserTimesheet wut" + sb.toString() + orderHql.toString(), 
    			"select count(wut.id) from UserTimesheet wut" + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}
	@Override
	public Page<UserTimesheet> getProjectPage(UserTimesheet userTimesheet, User user, DeptInfo deptInfo,Pageable pageable){
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append(" left join ProjectInfo wpi on wpi.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
    	
    	sb.append(" where (wpi.pmId = ?" + (count++));
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ?" + (count++) + " or wdi.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and wut.objName like ?" + (count++));
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and wut.objId = ?" + (count++));
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and wut.userId = ?" + (count++));
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and wut.workDay = ?" + (count++));
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and wut.type = ?" + (count++));
    	params.add(UserTimesheet.TYPE_PROJECT);
    	sb.append(" and wut.status = ?" + (count++));
    	params.add(CpmConstants.STATUS_VALID);
    	
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
    	Page<UserTimesheet> page = this.queryHqlPage(
    			"select wut from UserTimesheet wut" + sb.toString() + orderHql.toString(), 
    			"select count(wut.id) from UserTimesheet wut" + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}
	@Override
	public List<UserTimesheet> getByWorkDayAndUser(Long startDay, Long endDay, Long userId) {
		return this.queryAllHql("from UserTimesheet where workDay >= ?0 and workDay <= ?1 and status = ?2 and userId = ?3 order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,userId});
	}

	@Override
	public void saveByUser(List<UserTimesheet> saveList,List<UserTimesheet> updateList) {
		Double changeAmount = 0d;
		//Double updateWorkTime = 0d;
		List<Object> offerList = new ArrayList<Object>();
		Map<Long, Double> amountMap = new HashMap<Long, Double>();
		//查询出外包合同月对应的工作日
		Double monthWorkDay = StringUtil.nullToDouble(systemConfigRepository.findValueByKey("contract.external.month.day"));
		if(saveList != null && !saveList.isEmpty()){
			for(UserTimesheet userTimesheet : saveList){
				if (userTimesheet.getType().intValue() == UserTimesheet.TYPE_PROJECT) {
					offerList = getOffer(userTimesheet.getUserId(), userTimesheet.getObjId(), userTimesheet.getWorkDay());
					if (offerList != null && !offerList.isEmpty()) {
						changeAmount = (Double)offerList.get(1) / monthWorkDay * userTimesheet.getRealInput() / 8;
						if (!amountMap.containsKey((Long)offerList.get(0))) {
							amountMap.put((Long)offerList.get(0), changeAmount);
						}else {
							amountMap.put((Long)offerList.get(0), amountMap.get((Long)offerList.get(0)) + changeAmount);
						}
					}
				}
				this.save(userTimesheet);
			}
		}
		UserTimesheet oldUserTimesheet = null;
		Boolean isModified = Boolean.FALSE;
		if(updateList != null && !updateList.isEmpty()){
			for(UserTimesheet userTimesheet : updateList){
				oldUserTimesheet = userTimesheetRepository.findOne(userTimesheet.getId());
				if (userTimesheet.getType() == UserTimesheet.TYPE_PROJECT) {
					if(oldUserTimesheet != null){
						if (userTimesheet.getRealInput() != oldUserTimesheet.getRealInput()) {
							offerList = getOffer(userTimesheet.getUserId(), userTimesheet.getObjId(), userTimesheet.getWorkDay());
							if (offerList != null && !offerList.isEmpty()) {
								changeAmount = (Double)offerList.get(1) / monthWorkDay * (userTimesheet.getRealInput() - oldUserTimesheet.getRealInput()) / 8;
								if (!amountMap.containsKey((Long)offerList.get(0))) {
									amountMap.put((Long)offerList.get(0), changeAmount);
								}else {
									amountMap.put((Long)offerList.get(0), amountMap.get((Long)offerList.get(0)) + changeAmount);
								}
							}
						}
					}
				}
				if(userTimesheet.getRealInput() < oldUserTimesheet.getAcceptInput()){
					isModified = Boolean.TRUE;
					oldUserTimesheet.setAcceptInput(userTimesheet.getRealInput());
				}
				if(userTimesheet.getRealInput() != oldUserTimesheet.getRealInput()){
					isModified = Boolean.TRUE;
					oldUserTimesheet.setRealInput(userTimesheet.getRealInput());
				}
				if((oldUserTimesheet.getWorkArea() == null && userTimesheet.getWorkArea() != null)
						|| (oldUserTimesheet.getWorkArea() != null && userTimesheet.getWorkArea() == null)
						|| !oldUserTimesheet.getWorkArea().equals(userTimesheet.getWorkArea())){
					isModified = Boolean.TRUE;
					oldUserTimesheet.setWorkArea(userTimesheet.getWorkArea());
				}
				if(userTimesheet.getExtraInput() < oldUserTimesheet.getAcceptExtraInput()){
					isModified = Boolean.TRUE;
					oldUserTimesheet.setAcceptExtraInput(userTimesheet.getExtraInput());
				}
				if(userTimesheet.getExtraInput() != oldUserTimesheet.getExtraInput()){
					isModified = Boolean.TRUE;
					oldUserTimesheet.setExtraInput(userTimesheet.getExtraInput());
				}
				if(isModified){
					oldUserTimesheet.setStatus(userTimesheet.getStatus());
					oldUserTimesheet.setUpdator(userTimesheet.getUpdator());
					oldUserTimesheet.setUpdateTime(userTimesheet.getUpdateTime());
					userTimesheetRepository.save(oldUserTimesheet);
				}
			}
		}
		for (Map.Entry<Long, Double> entry : amountMap.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null) {
				this.excuteHql("update ContractInfo set amount = amount + ?0 where id = ?1",
						new Object[]{entry.getValue(),entry.getKey()});
				this.excuteHql("update ContractInfo set taxes = amount * taxRate / 100,shareCost = amount * shareRate / 100 where id = ?0",
						new Object[]{entry.getKey()});
			}
		}
	}
	@Override
	public UserTimesheet getUserTimesheetForContract(Long id, User user, DeptInfo deptInfo){
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append("select wut from UserTimesheet wut");
    	sb.append(" left join ContractInfo wci on wci.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
    	sb.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
    	
    	sb.append(" where (wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++));
    	params.add(user.getId());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ?" + (count++) + " or wdi.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    		
    		sb.append(" or wdi2.id = ?" + (count++) + " or wdi2.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	sb.append(" and wut.type = ?" + (count++) + " and wut.id = ?" + (count++));
    	params.add(UserTimesheet.TYPE_CONTRACT);
    	params.add(id);
    	
    	List<UserTimesheet> list = this.queryAllHql(sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
    		return list.get(0);
    	}
    	return null;
	}
	@Override
	public UserTimesheet getUserTimesheetForProject(Long id, User user, DeptInfo deptInfo){
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append("select wut from UserTimesheet wut");
    	sb.append(" left join ProjectInfo wpi on wpi.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
    	
    	sb.append(" where (wpi.pmId = ?" + (count++));
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ?" + (count++) + " or wdi.idPath like ?" + (count++));
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	sb.append(" and wut.type = ?" + (count++) + " and wut.id = ?" + (count++));
    	params.add(UserTimesheet.TYPE_PROJECT);
    	params.add(id);
    	
    	List<UserTimesheet> list = this.queryAllHql(sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
    		return list.get(0);
    	}
    	return null;
	}
	
	@Override
	public List<UserTimesheet> getByWorkDayAndObjType(Long startDay, Long endDay, Long objId, Integer type){
		return this.queryAllHql("from UserTimesheet where workDay >= ?0 and workDay <= ?1 and status = ?2 and objId = ?3 and type = ?4 order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,objId,type});
	}
	@Override
	public List<UserTimesheet> getByWorkDayAndObjType(Long startDay, Long endDay, Long objId, Integer type, Long userId){
		return this.queryAllHql("from UserTimesheet where workDay >= ?0 and workDay <= ?1 and status = ?2 and objId = ?3 and type = ?4 and userId = ?5 order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,objId,type,userId});
	}
	
	@Override
	public void updateAcceptInput(List<UserTimesheet> updateList) {
		if(updateList != null && !updateList.isEmpty()){
			for(UserTimesheet userTimesheet : updateList){
				this.excuteHql("update UserTimesheet set acceptInput = ?0,updator = ?1,updateTime = ?2,acceptExtraInput = ?3 where (acceptInput != ?4 or acceptExtraInput != ?5) and userId = ?6 and objId = ?7 and type = ?8 and id = ?9",
						new Object[]{userTimesheet.getAcceptInput(),
								userTimesheet.getUpdator(),userTimesheet.getUpdateTime(),userTimesheet.getAcceptExtraInput(),
								userTimesheet.getAcceptInput(),userTimesheet.getAcceptExtraInput(),
								userTimesheet.getUserId(),userTimesheet.getObjId(),userTimesheet.getType(),
								userTimesheet.getId()});
			}
		}
	}

	@Override
	public List<Object> getOffer(Long userId, Long objId, Long workDay) {
		StringBuffer hql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		hql.append("select wci.id, wou.offer from ProjectInfo wpi");
		hql.append(" inner join ContractInfo wci on wci.id = wpi.contractId");
		hql.append(" and wci.type = ?" + (count++));
		params.add(ContractInfo.TYPE_EXTERNAL);
		
		hql.append(" left join OutsourcingUser wou on wou.contractId = wci.id");
		hql.append(" left join ProjectUser wpu on wpu.projectId = wpi.id");
		hql.append(" and wpu.userId = ?" + (count++));
		params.add(userId);
		
		hql.append(" and wpu.rank = wou.rank");
		hql.append(" where wpi.id = ?" + (count++));
		params.add(objId);
		
		hql.append(" and wpu.id is not null");
		hql.append(" and wpu.joinDay <= ?" + (count++));
		params.add(workDay);
		
		hql.append(" order by wpu.joinDay desc,wpu.id desc");
		
		List<Object[]> list = this.queryAllHql(hql.toString(), params.toArray());
		if (list != null && !list.isEmpty()) {
			List<Object> returnList = new ArrayList<Object>();
			returnList.add(list.get(0)[0]);
			returnList.add(list.get(0)[1]);
			return returnList;
		}
		return null;
	}

	@Override
	public void saveByDelete(UserTimesheet userTimesheet, ContractInfo contractInfo) {
		if (userTimesheet != null) {
			this.save(userTimesheet);
		}
		if (contractInfo != null) {
			this.excuteHql("update ContractInfo set amount = amount + ?0 where id = ?1", 
					new Object[]{contractInfo.getAmount(),contractInfo.getId()});
			this.excuteHql("update ContractInfo set taxes = amount * taxRate / 100,shareCost = amount * shareRate / 100 where id = ?0",
					new Object[]{contractInfo.getId()});
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserTimesheetForHardWorkingVo> findByWorkDay(Long fromDay, Long endDay) {
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	sb.append(" select uts.userId,sum(uts.realInput),sum(uts.acceptInput),sum(uts.extraInput),sum(uts.acceptExtraInput) from UserTimesheet uts");
    	
    	sb.append(" where uts.workDay >= ?"+(count++)+" and uts.workDay <= ?"+(count++)+" and uts.status = 1 group by uts.userId");
    	params.add(fromDay);
    	params.add(endDay);
    	
    	List<Object[]> page = this.queryAllHql(sb.toString(), params.toArray());
    	
    	List<UserTimesheetForHardWorkingVo> list = new ArrayList<UserTimesheetForHardWorkingVo>();
    	if(page != null && !page.isEmpty()){
    		for(Object[] o : page){
    			list.add(transVo(o));
			}
    	}
    	return list;
	}

	private UserTimesheetForHardWorkingVo transVo(Object[] o) {
		UserTimesheetForHardWorkingVo roleHardWorkingVo = new UserTimesheetForHardWorkingVo();
		roleHardWorkingVo.setUserId(StringUtil.nullToLong(o[0]));
		roleHardWorkingVo.setSumRealInput(StringUtil.nullToDouble(o[1]));
		roleHardWorkingVo.setSumAcceptRealInput(StringUtil.nullToDouble(o[2]));
		roleHardWorkingVo.setSumExtraInput(StringUtil.nullToDouble(o[3]));
		roleHardWorkingVo.setSumAcceptExtraInput(StringUtil.nullToDouble(o[4]));
		return roleHardWorkingVo;
	}

	@Override
	public Long getWorkDayByParam(Long userId, Long objId, Integer type, Long fromDay, Long endDay, int iType) {
		StringBuffer queryHql = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	int count = 0;//jpa格式 问号后的数组，一定要从0开始
    	
    	if(fromDay.longValue() == endDay){
    		queryHql.append(" select max(uts.workDay) from UserTimesheet uts");
    		queryHql.append(" where uts.workDay = ?"+(count++));
    		params.add(fromDay);
    	}else if(iType == 1){
    		queryHql.append(" select min(uts.workDay) from UserTimesheet uts");
    		queryHql.append(" where uts.workDay >= ?"+(count++)+" and uts.workDay < ?"+(count++));
    		params.add(fromDay);
    		params.add(endDay);
    	}else if(iType == 2){
    		queryHql.append(" select max(uts.workDay) from UserTimesheet uts");
    		queryHql.append(" where uts.workDay > ?"+(count++)+" and uts.workDay <= ?"+(count++));
    		params.add(fromDay);
    		params.add(endDay);
    	}else{//3
    		queryHql.append(" select max(uts.workDay) from UserTimesheet uts");
    		queryHql.append(" where uts.workDay > ?"+(count++)+" and uts.workDay <= ?"+(count++));
    		params.add(fromDay);
    		params.add(endDay);
    	}
    	queryHql.append(" and uts.status = 1 and uts.userId = ?"+(count++)+" and uts.objId = ?"+(count++)+" and uts.type = ?"+(count++));
    	params.add(userId);
    	params.add(objId);
    	params.add(type);
    	
    	List<Long> workDays = this.queryAllHql(queryHql.toString(), params.toArray());
    	if(workDays != null && !workDays.isEmpty()){
    		return workDays.get(0);
    	}
    	return null;
	}

	@Override
	public List<ProjectUserInputVo> getProjectUserInputsByParam(Long startTime, Long endTime, List<Long> userIds,User user,DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		
		queryHql.append(" select wpi.serialNum,wpi.name,wci.serialNum,wci.name,ju1.serialNum,ju1.lastName,wdt.name,");
		queryHql.append(" ju.serialNum,ju.lastName,sum(wut.realInput),sum(wut.acceptInput),sum(wut.extraInput),sum(wut.acceptExtraInput) from UserTimesheet wut");
		queryHql.append(" left join ProjectInfo wpi on wut.objId = wpi.id");
		queryHql.append(" left join ContractInfo wci on wpi.contractId = wci.id");
		queryHql.append(" left join User ju on wut.userId = ju.id");
		queryHql.append(" left join User ju1 on wpi.pmId = ju1.id");
		queryHql.append(" left join DeptInfo wdi on wpi.deptId = wdi.id");
		queryHql.append(" left join DeptType wdt on wdi.type = wdt.id");
		
		whereHql.append(" where wut.type = ?"+(count++));
		params.add(UserTimesheet.TYPE_PROJECT);
		//权限
		whereHql.append(" and (wut.creator = ?"+(count++)+" or wpi.pmId = ?"+(count++));
		params.add(user.getLogin());
		params.add(user.getId());
		if(user.getIsManager()){
			whereHql.append(" or wdi.idPath like ?"+(count++)+" or wdi.id = ?"+(count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		
		if (startTime != null) {
			whereHql.append(" and wut.workDay >= ?" + (count++));
			params.add(startTime);
		}
		if(endTime != null){
			whereHql.append(" and wut.workDay <= ?" + (count++));
			params.add(endTime);
		}
		if(userIds != null && userIds.size() > 0){
			whereHql.append(" and wut.userId in ?" + (count++));
			params.add(userIds);
		}
		whereHql.append(" group by wut.objId,wut.userId");
		whereHql.append(" order by wut.objId,wut.userId desc");
		
		List<Object[]> lists = this.queryAllHql(queryHql.toString() + whereHql.toString(), params.toArray());
		List<ProjectUserInputVo> result = new ArrayList<ProjectUserInputVo>();
		if(lists != null && lists.size() > 0){
			for(Object[] o : lists){
				result.add(transferInputVo(o));
			}
		}
		return result;
	}
	
	private ProjectUserInputVo transferInputVo(Object[] o){
		ProjectUserInputVo vo = new ProjectUserInputVo();
		vo.setProjectSerialNum(StringUtil.null2Str(o[0]));
		vo.setProjectName(StringUtil.null2Str(o[1]));
		vo.setContractSerialNum(StringUtil.null2Str(o[2]));
		vo.setContractName(StringUtil.null2Str(o[3]));
		vo.setPmSerialNum(StringUtil.null2Str(o[4]));
		vo.setPmName(StringUtil.null2Str(o[5]));
		vo.setPmDeptType(StringUtil.null2Str(o[6]));
		vo.setUserSerialNum(StringUtil.null2Str(o[7]));
		vo.setUserName(StringUtil.null2Str(o[8]));
		vo.setRealInput(StringUtil.nullToDouble(o[9]));
		vo.setAcceptInput(StringUtil.nullToDouble(o[10]));
		vo.setExtraInput(StringUtil.nullToDouble(o[11]));
		vo.setAcceptExtraInput(StringUtil.nullToDouble(o[12]));
		return vo;
	}

	@Override
	public List<UserProjectInputVo> getUserProjectInputsByParam(Long startTime, Long endTime, List<Long> userIds,
			List<Long> projectIds, User user, DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		
		queryHql.append(" select wpi.serialNum,wpi.name,wci.serialNum,wci.name,ju1.serialNum,ju1.lastName,wdt.name,");
		queryHql.append(" ju.serialNum,ju.lastName,sum(wut.realInput),sum(wut.acceptInput),sum(wut.extraInput),sum(wut.acceptExtraInput) from UserTimesheet wut");
		queryHql.append(" left join ProjectInfo wpi on wut.objId = wpi.id");
		queryHql.append(" left join ContractInfo wci on wpi.contractId = wci.id");
		queryHql.append(" left join User ju on wut.userId = ju.id");
		queryHql.append(" left join User ju1 on wpi.pmId = ju1.id");
		queryHql.append(" left join DeptInfo wdi on wpi.deptId = wdi.id");
		queryHql.append(" left join DeptType wdt on wdi.type = wdt.id");
		
		whereHql.append(" where wut.type = ?"+(count++));
		params.add(UserTimesheet.TYPE_PROJECT);
		//权限
		whereHql.append(" and (wut.creator = ?"+(count++)+" or wpi.pmId = ?"+(count++));
		params.add(user.getLogin());
		params.add(user.getId());
		if(user.getIsManager()){
			whereHql.append(" or wdi.idPath like ?"+(count++)+" or wdi.id = ?"+(count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		
		if (startTime != null) {
			whereHql.append(" and wut.workDay >= ?" + (count++));
			params.add(startTime);
		}
		if(endTime != null){
			whereHql.append(" and wut.workDay <= ?" + (count++));
			params.add(endTime);
		}
		if(userIds != null && userIds.size() > 0){
			whereHql.append(" and wut.userId in ?" + (count++));
			params.add(userIds);
		}
		if(projectIds != null && projectIds.size() > 0){
			whereHql.append(" and wut.objId in ?" + (count++));
			params.add(projectIds);
		}
		whereHql.append(" group by wut.userId,wut.objId");
		whereHql.append(" order by wut.userId,wut.objId desc");
		
		List<Object[]> lists = this.queryAllHql(queryHql.toString() + whereHql.toString(), params.toArray());
		List<UserProjectInputVo> result = new ArrayList<UserProjectInputVo>();
		if(lists != null && lists.size() > 0){
			for(Object[] o : lists){
				result.add(transferUserProjectInputVo(o));
			}
		}
		return result;
	}
	private UserProjectInputVo transferUserProjectInputVo(Object[] o){
		UserProjectInputVo vo = new UserProjectInputVo();
		vo.setProjectSerialNum(StringUtil.null2Str(o[0]));
		vo.setProjectName(StringUtil.null2Str(o[1]));
		vo.setContractSerialNum(StringUtil.null2Str(o[2]));
		vo.setContractName(StringUtil.null2Str(o[3]));
		vo.setPmSerialNum(StringUtil.null2Str(o[4]));
		vo.setPmName(StringUtil.null2Str(o[5]));
		vo.setPmDeptType(StringUtil.null2Str(o[6]));
		vo.setUserSerialNum(StringUtil.null2Str(o[7]));
		vo.setUserName(StringUtil.null2Str(o[8]));
		vo.setRealInput(StringUtil.nullToDouble(o[9]));
		vo.setAcceptInput(StringUtil.nullToDouble(o[10]));
		vo.setExtraInput(StringUtil.nullToDouble(o[11]));
		vo.setAcceptExtraInput(StringUtil.nullToDouble(o[12]));
		return vo;
	}
}
