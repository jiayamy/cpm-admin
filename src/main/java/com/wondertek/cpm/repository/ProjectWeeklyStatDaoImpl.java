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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;

@Repository("projectWeeklyStatDao")
public class ProjectWeeklyStatDaoImpl extends GenericDaoImpl<ProjectWeeklyStat, Long> implements ProjectWeeklyStatDao{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectWeeklyStat> getDomainClass() {
		return ProjectWeeklyStat.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public Page<ProjectWeeklyStat> getUserPage(String beginDate, String endDate, String statDate, Pageable pageable,
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
    	Page<ProjectWeeklyStat> page = this.queryHqlPage(
    			"from ProjectWeeklyStat " + sb.toString() + orderHql.toString(), 
    			"select count(id) from ProjectWeeklyStat " + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	return page;
	}

	@Override
	public List<LongValue> queryUserProject(User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append(" select wpi.id,wpi.serial_num,wpi.name_ from w_project_info wpi");
		querySql.append(" left join w_dept_info wdi on wpi.dept_id = wdi.id");
		
		querySql.append(" where (wpi.pm_id = ? or wpi.creator_ = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		
		if(user.getIsManager()){
			querySql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		querySql.append(")");
		
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return returnList;
	}

	@Override
	public ProjectWeeklyStatVo getById(Long id) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.project_id, m.finish_rate, m.human_cost, m.payment_, m.stat_week, m.create_time, i.serial_num ");
		countsql.append(" select count(m.id)");
		sb.append(" from w_project_weekly_stat m");
		sb.append(" left join w_project_info i on m.project_id = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from w_project_weekly_stat where 1=1 ");
    	if(!StringUtil.isNullStr(id)){
    		sb.append(" and id = ?");
    		params.add(id);
    	}
    	sb.append(" group by project_id");
    	sb.append(" )");
    	
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transProjectWeeklyStatVo2(list.get(0));
		}
		return null;
	}

	@Override
	public Page<ProjectWeeklyStatVo> getUserPage(String projectId, Pageable pageable, User user) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.projectId, m.finishRate, m.humanCost, m.payment, m.statWeek, m.createTime, i.serialNum ");
		countsql.append(" select count(m.id)");
		sb.append(" from ProjectWeeklyStat m");
		sb.append(" left join ProjectInfo i on m.projectId = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from ProjectWeeklyStat where 1=1 ");
    	if(!StringUtil.isNullStr(projectId)){
    		sb.append(" and projectId = ?");
    		params.add(StringUtil.nullToLong(projectId));
    	}
    	sb.append(" group by projectId");
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
    	List<ProjectWeeklyStatVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectWeeklyStatVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private ProjectWeeklyStatVo transProjectWeeklyStatVo(Object[] o){
		ProjectWeeklyStatVo vo = new ProjectWeeklyStatVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setProjectId(StringUtil.nullToLong(o[1]));
		vo.setFinishRate(StringUtil.nullToDouble(o[2]));
		vo.setHumanCost(StringUtil.nullToDouble(o[3]));
		vo.setPayment(StringUtil.nullToDouble(o[4]));
		vo.setStatWeek(StringUtil.nullToLong(o[5]));
		vo.setCreateTime((ZonedDateTime) o[6]);
		vo.setSerialNum(StringUtil.null2Str(o[7]));
		return vo;
	}
	
	private ProjectWeeklyStatVo transProjectWeeklyStatVo2(Object[] o){
		ProjectWeeklyStatVo vo = new ProjectWeeklyStatVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setProjectId(StringUtil.nullToLong(o[1]));
		vo.setFinishRate(StringUtil.nullToDouble(o[2]));
		vo.setHumanCost(StringUtil.nullToDouble(o[3]));
		vo.setPayment(StringUtil.nullToDouble(o[4]));
		vo.setStatWeek(StringUtil.nullToLong(o[5]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[6]));
		vo.setSerialNum(StringUtil.null2Str(o[7]));
		return vo;
	}
}
