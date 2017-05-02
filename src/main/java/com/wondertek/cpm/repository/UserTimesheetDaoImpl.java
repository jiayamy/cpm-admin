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

//	@Override
//	public void saveByUser(List<UserTimesheet> saveList,List<UserTimesheet> updateList) {
//		Double changeAmount = 0d;
//		Double updateWorkTime = 0d;
//		List<Object> offerList = new ArrayList<Object>();
//		if(saveList != null && !saveList.isEmpty()){
//			for(UserTimesheet userTimesheet : saveList){
//				if (userTimesheet.getType().intValue() == UserTimesheet.TYPE_PROJECT) {
//					offerList = getOffer(userTimesheet.getUserId(), userTimesheet.getObjId(), userTimesheet.getWorkDay());
//					if (offerList != null && !offerList.isEmpty()) {
//						changeAmount = (Double)offerList.get(1) * userTimesheet.getRealInput() / 8;
//						this.excuteHql("update ContractInfo set amount = amount + ?0 where id = ?1",
//								new Object[]{changeAmount,(Long)offerList.get(0)});
//						this.excuteHql("update ContractInfo set taxes = amount * taxRate,shareCost = amount * shareRate where id = ?0",
//								new Object[]{(Long)offerList.get(0)});
//					}
//				}
//				this.save(userTimesheet);
//			}
//		}
//		if(updateList != null && !updateList.isEmpty()){
//			for(UserTimesheet userTimesheet : updateList){
//				if (userTimesheet.getType() == userTimesheet.TYPE_PROJECT) {
//					updateWorkTime = userTimesheetRepository.findRealInputById(userTimesheet.getId());
//					if (userTimesheet.getRealInput().doubleValue() != updateWorkTime) {
//						offerList = getOffer(userTimesheet.getUserId(), userTimesheet.getObjId(), userTimesheet.getWorkDay());
//						if (offerList != null && !offerList.isEmpty()) {
//							changeAmount = (Double)offerList.get(1) * (userTimesheet.getRealInput() - updateWorkTime) / 8;
//							this.excuteHql("update ContractInfo set amount = amount + ?0 where id = ?1",
//									new Object[]{changeAmount,(Long)offerList.get(0)});
//							this.excuteHql("update ContractInfo set taxes = amount * taxRate,shareCost = amount * shareRate where id = ?0",
//									new Object[]{(Long)offerList.get(0)});
//						}
//					}
//				}
//				this.excuteHql("update UserTimesheet set realInput = ?0,status = ?1,workArea = ?2,updator = ?3,updateTime = ?4 where (realInput != ?5 or workArea != ?6) and id = ?7",
//						new Object[]{userTimesheet.getRealInput(),userTimesheet.getStatus(),userTimesheet.getWorkArea(),
//								userTimesheet.getUpdator(),userTimesheet.getUpdateTime(),
//								userTimesheet.getRealInput(),userTimesheet.getWorkArea(),
//								userTimesheet.getId()});
//				//gengx
//			}
//		}
//	}
	
	@Override
	public void saveByUser(List<UserTimesheet> saveList,List<UserTimesheet> updateList) {
		Double changeAmount = 0d;
		Double updateWorkTime = 0d;
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
		if(updateList != null && !updateList.isEmpty()){
			for(UserTimesheet userTimesheet : updateList){
				if (userTimesheet.getType() == userTimesheet.TYPE_PROJECT) {
					updateWorkTime = userTimesheetRepository.findRealInputById(userTimesheet.getId());
					if (userTimesheet.getRealInput().doubleValue() != updateWorkTime) {
						offerList = getOffer(userTimesheet.getUserId(), userTimesheet.getObjId(), userTimesheet.getWorkDay());
						if (offerList != null && !offerList.isEmpty()) {
							changeAmount = (Double)offerList.get(1) / monthWorkDay * (userTimesheet.getRealInput() - updateWorkTime) / 8;
							if (!amountMap.containsKey((Long)offerList.get(0))) {
								amountMap.put((Long)offerList.get(0), changeAmount);
							}else {
								amountMap.put((Long)offerList.get(0), amountMap.get((Long)offerList.get(0)) + changeAmount);
							}
						}
					}
				}
				this.excuteHql("update UserTimesheet set realInput = ?0,status = ?1,workArea = ?2,updator = ?3,updateTime = ?4 where (realInput != ?5 or workArea != ?6) and id = ?7",
						new Object[]{userTimesheet.getRealInput(),userTimesheet.getStatus(),userTimesheet.getWorkArea(),
								userTimesheet.getUpdator(),userTimesheet.getUpdateTime(),
								userTimesheet.getRealInput(),userTimesheet.getWorkArea(),
								userTimesheet.getId()});
				//gengx
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
	public void updateAcceptInput(List<UserTimesheet> updateList) {
		if(updateList != null && !updateList.isEmpty()){
			for(UserTimesheet userTimesheet : updateList){
				this.excuteHql("update UserTimesheet set acceptInput = ?0,updator = ?1,updateTime = ?2 where acceptInput != ?3 and userId = ?4 and objId = ?5 and type = ?6 and id = ?7",
						new Object[]{userTimesheet.getAcceptInput(),
								userTimesheet.getUpdator(),userTimesheet.getUpdateTime(),
								userTimesheet.getAcceptInput(),
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
}
