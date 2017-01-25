package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
@Repository("userTimesheetDao")
public class UserTimesheetDaoImpl extends GenericDaoImpl<UserTimesheet, Long> implements UserTimesheetDao  {
	
	@Autowired
	private EntityManager entityManager;

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
    	sb.append("where 1=1");
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and objName like ?");
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and objId = ?");
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getType() != null){
    		sb.append(" and type = ?");
    		params.add(userTimesheet.getType());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and userId = ?");
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and workDay = ?");
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and status = ?");
    	params.add(CpmConstants.STATUS_VALID);
    	sb.append(" and userId = ?");
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
    	sb.append(" left join ContractInfo wci on wci.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
    	sb.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
    	
    	sb.append(" where (wci.salesmanId = ? or wci.consultantsId = ?");
    	params.add(user.getId());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ? or wdi.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    		
    		sb.append(" or wdi2.id = ? or wdi2.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and wut.objName like ?");
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and wut.objId = ?");
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and wut.userId = ?");
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and wut.workDay = ?");
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and wut.type = ?");
    	params.add(UserTimesheet.TYPE_CONTRACT);
    	sb.append(" and wut.status = ?");
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
    	sb.append(" left join ProjectInfo wpi on wpi.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
    	
    	sb.append(" where (wpi.pmId = ?");
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ? or wdi.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	
    	if(!StringUtil.isNullStr(userTimesheet.getObjName())){
    		sb.append(" and wut.objName like ?");
    		params.add("%"+userTimesheet.getObjName() +"%");
    	}
    	if(userTimesheet.getObjId() != null){
    		sb.append(" and wut.objId = ?");
    		params.add(userTimesheet.getObjId());
    	}
    	if(userTimesheet.getUserId() != null){
    		sb.append(" and wut.userId = ?");
    		params.add(userTimesheet.getUserId());
    	}
    	if(userTimesheet.getWorkDay() != null){
    		sb.append(" and wut.workDay = ?");
    		params.add(userTimesheet.getWorkDay());
    	}
    	sb.append(" and wut.type = ?");
    	params.add(UserTimesheet.TYPE_PROJECT);
    	sb.append(" and wut.status = ?");
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
		return this.queryAllHql("from UserTimesheet where workDay >= ? and workDay <= ? and status = ? and userId = ? order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,userId});
	}

	@Override
	public void saveByUser(List<UserTimesheet> saveList,List<UserTimesheet> updateList) {
		if(saveList != null && !saveList.isEmpty()){
			for(UserTimesheet userTimesheet : saveList){
				this.save(userTimesheet);
			}
		}
		if(updateList != null && !updateList.isEmpty()){
			for(UserTimesheet userTimesheet : updateList){
				this.excuteHql("update UserTimesheet set realInput = ?,status = ?,workArea = ?,updator = ?,updateTime = ? where (realInput != ? or workArea != ?) id = ?",
						new Object[]{userTimesheet.getRealInput(),userTimesheet.getStatus(),userTimesheet.getWorkArea(),
								userTimesheet.getUpdator(),userTimesheet.getUpdateTime(),
								userTimesheet.getRealInput(),userTimesheet.getWorkArea(),
								userTimesheet.getId()});
			}
		}
	}
	
	@Override
	public UserTimesheet getUserTimesheetForContract(Long id, User user, DeptInfo deptInfo){
		StringBuffer sb = new StringBuffer();
    	List<Object> params = new ArrayList<Object>();
    	sb.append("select wut from UserTimesheet wut");
    	sb.append(" left join ContractInfo wci on wci.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wci.deptId");
    	sb.append(" left join DeptInfo wdi2 on wdi2.id = wci.consultantsDeptId");
    	
    	sb.append(" where (wci.salesmanId = ? or wci.consultantsId = ?");
    	params.add(user.getId());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ? or wdi.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    		
    		sb.append(" or wdi2.id = ? or wdi2.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	sb.append(" and wut.type = ? and wut.id = ?");
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
    	sb.append("select wut from UserTimesheet wut");
    	sb.append(" left join ProjectInfo wpi on wpi.id = wut.objId");
    	sb.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
    	
    	sb.append(" where (wpi.pmId = ?");
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id = ? or wdi.idPath like ?");
    		params.add(deptInfo.getId());
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
    	}
    	sb.append(")");
    	sb.append(" and wut.type = ? and wut.id = ?");
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
		return this.queryAllHql("from UserTimesheet where workDay >= ? and workDay <= ? and status = ? and objId = ? and type = ? order by workDay asc,id asc",
				new Object[]{startDay,endDay,CpmConstants.STATUS_VALID,objId,type});
	}
}
