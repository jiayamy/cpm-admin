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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ParticipateInfo;
import com.wondertek.cpm.domain.vo.ProjectUserVo;
@Repository("projectUserDao")
public class ProjectUserDaoImpl extends GenericDaoImpl<ProjectUser, Long> implements ProjectUserDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectUser> getDomainClass() {
		return ProjectUser.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<LongValue> getByUserAndDay(Long userId, Long[] weekDays) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select distinct pu.project_id,pi.serial_num,pi.name_ from w_project_user pu left join w_project_info pi on pu.project_id = pi.id where");
		int count = 0;
		if(weekDays != null && weekDays.length > 0){
			count++;
			sql.append(" (");
			for(int i = 0 ; i < weekDays.length; i++){
				if(i != 0){
					sql.append(" or ");
				}
				sql.append("(pu.join_day <= ? and (pu.leave_day is null or pu.leave_day >= ?))");
				params.add(weekDays[i]);
				params.add(weekDays[i]);
			}
			sql.append(")");
		}
		if(count > 0){
			sql.append(" and");
		}
		sql.append(" pi.id is not null and pu.user_id = ?");
		params.add(userId);
		List<Object[]> list = this.queryAllSql(sql.toString(),params.toArray());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),UserTimesheet.TYPE_PROJECT,StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return returnList;
	}
	@Override
	public List<ParticipateInfo> getInfoByUserAndDay(Long userId, Long[] weekDays){
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select distinct pu.project_id,pu.join_day,pu.leave_day from w_project_user pu where");
		int count = 0;
		if(weekDays != null && weekDays.length > 0){
			count++;
			sql.append(" (");
			for(int i = 0 ; i < weekDays.length; i++){
				if(i != 0){
					sql.append(" or ");
				}
				sql.append("(pu.join_day <= ? and (pu.leave_day is null or pu.leave_day >= ?))");
				params.add(weekDays[i]);
				params.add(weekDays[i]);
			}
			sql.append(")");
		}
		if(count > 0){
			sql.append(" and");
		}
		sql.append(" pu.user_id = ?");
		params.add(userId);
		List<Object[]> list = this.queryAllSql(sql.toString(),params.toArray());
		List<ParticipateInfo> returnList = new ArrayList<ParticipateInfo>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new ParticipateInfo(StringUtil.nullToLong(o[0]),
						UserTimesheet.TYPE_PROJECT,
						StringUtil.nullToCloneLong(o[1]),
						StringUtil.nullToCloneLong(o[2])));
			}
		}
		return returnList;
	}

	@Override
	public Page<ProjectUserVo> getUserPage(ProjectUser projectUser, User user, DeptInfo deptInfo, Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpu.id,wpu.project_id,wpu.user_id,wpu.user_name,wpu.user_role,wpu.join_day,wpu.leave_day,wpu.creator_,wpu.create_time,wpu.updator_,wpu.update_time");
		querySql.append(",wpi.serial_num,wpi.name_");
		
		countSql.append("select count(wpu.id)");
		
		whereSql.append(" from w_project_user wpu");
		whereSql.append(" left join w_project_info wpi on wpi.id = wpu.project_id");
		whereSql.append(" left join w_dept_info wdi on wpi.dept_id = wdi.id");
		
		whereSql.append(" where (wpi.pm_id = ? or wpi.creator_ = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//查询条件
		if(projectUser.getProjectId() != null){
			whereSql.append(" and wpu.project_id = ?");
			params.add(projectUser.getProjectId());
		}
		if(projectUser.getUserId() != null){
			whereSql.append(" and wpu.user_id = ?");
			params.add(projectUser.getUserId());
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
		
		List<ProjectUserVo> returnList = new ArrayList<ProjectUserVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectUserVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	private ProjectUserVo transProjectUserVo(Object[] o) {
		ProjectUserVo vo = new ProjectUserVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setProjectId(StringUtil.nullToLong(o[1]));
		vo.setUserId(StringUtil.nullToLong(o[2]));
		vo.setUserName(StringUtil.null2Str(o[3]));
		vo.setUserRole(StringUtil.null2Str(o[4]));
		vo.setJoinDay(StringUtil.nullToCloneLong(o[5]));
		vo.setLeaveDay(StringUtil.nullToCloneLong(o[6]));
		vo.setCreator(StringUtil.null2Str(o[7]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[8]));
		vo.setUpdator(StringUtil.null2Str(o[9]));
		vo.setUpdateTime(DateUtil.getZonedDateTime((Timestamp) o[10]));
		
		vo.setProjectNum(StringUtil.null2Str(o[11]));
		vo.setProjectName(StringUtil.null2Str(o[12]));
		return vo;
	}

	@Override
	public ProjectUserVo getProjectUser(User user, DeptInfo deptInfo, Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wpu,wpi.serialNum,wpi.name,wci.type,wci.id,wosu.id");
		queryHql.append(" from ProjectUser wpu");
		queryHql.append(" left join ProjectInfo wpi on wpi.id = wpu.projectId ");
		queryHql.append(" left join ContractInfo wci on wci.id = wpi.contractId");
		queryHql.append(" left join OutsourcingUser wosu on wci.id = wosu.contractId");
		queryHql.append(" left join DeptInfo wdi on wdi.id = wpi.deptId");
		queryHql.append(" where (wpi.pmId = ?" + (count++) + " or wpi.creator = ?" + (count++));
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(") and wpu.id = ?" + (count++));
		params.add(id);
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new ProjectUserVo((ProjectUser)list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]),StringUtil.nullToInteger(list.get(0)[3]),StringUtil.nullToLong(list.get(0)[4]),StringUtil.nullToLong(list.get(0)[5]));
		}
		return null;
	}

	@Override
	public int updateLeaveDayByProject(Long projectId, long leaveDay, String updator) {
		return this.excuteHql("update ProjectUser set leaveDay = ?0 , updator = ?1, updateTime = ?2 where (leaveDay is null or leaveDay > ?3) and projectId = ?4", 
				new Object[]{leaveDay,updator,ZonedDateTime.now(),leaveDay,projectId});
	}
	
}
