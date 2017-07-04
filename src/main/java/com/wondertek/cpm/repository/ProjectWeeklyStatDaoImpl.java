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
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
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
	public ProjectWeeklyStatVo getById(Long id, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.project_id, m.finish_rate, m.human_cost, m.payment_, m.stat_week, m.create_time, i.serial_num, i.name_, m.total_input, m.this_input ");
		countsql.append(" select count(m.id)");
		sb.append(" from w_project_weekly_stat m");
		sb.append(" left join w_project_info i on m.project_id = i.id");
		sb.append(" left join w_dept_info wdi on wdi.id = i.dept_id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from w_project_weekly_stat where 1=1 ");
    	if(!StringUtil.isNullStr(id)){
    		sb.append(" and id = ?");
    		params.add(id);
    	}
    	sb.append(" group by project_id");
    	sb.append(" )");
    	sb.append(" and ( i.creator_ = ? or i.pm_id = ?");
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id_path like ? or wdi.id = ?");
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
    	}
    	sb.append(" )");
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transProjectWeeklyStatVo2(list.get(0));
		}
		return null;
	}

	@Override
	public Page<ProjectWeeklyStatVo> getUserPage(String projectId, Pageable pageable, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querysql.append(" select m.id, m.projectId, m.finishRate, m.humanCost, m.payment, m.statWeek, m.createTime, i.serialNum, i.name, m.totalInput, m.thisInput ");
		countsql.append(" select count(m.id)");
		sb.append(" from ProjectWeeklyStat m");
		sb.append(" left join ProjectInfo i on m.projectId = i.id");
		sb.append(" left join DeptInfo wdi on wdi.id = i.deptId");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from ProjectWeeklyStat where 1=1 ");
    	if(!StringUtil.isNullStr(projectId)){
    		sb.append(" and projectId = ?" + (count++));
    		params.add(StringUtil.nullToLong(projectId));
    	}
    	sb.append(" group by projectId");
    	sb.append(" )");
    	sb.append(" and ( i.creator = ?" + (count++) + " or i.pmId = ?" + (count++));
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
    	}
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
		vo.setName(StringUtil.null2Str(o[8]));
		vo.setTotalInput(StringUtil.nullToDouble(o[9]));
		vo.setThisInput(StringUtil.nullToDouble(o[10]));
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
		vo.setName(StringUtil.null2Str(o[8]));
		vo.setTotalInput(StringUtil.nullToDouble(o[9]));
		vo.setThisInput(StringUtil.nullToDouble(o[10]));
		return vo;
	}

	@Override
	public ProjectWeeklyStatVo getByStatWeekAndProjectId(Long statWeek, Long projectId, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.project_id, m.finish_rate, m.human_cost, m.payment_, m.stat_week, m.create_time, i.serial_num, i.name_, m.total_input, m.this_input ");
		countsql.append(" select count(m.id)");
		sb.append(" from w_project_weekly_stat m");
		sb.append(" left join w_project_info i on m.project_id = i.id");
		sb.append(" left join w_dept_info wdi on wdi.id = i.dept_id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from w_project_weekly_stat where 1=1 ");
    	if(!StringUtil.isNullStr(statWeek)){
    		sb.append(" and stat_week = ?");
    		params.add(statWeek);
    	}
    	if(!StringUtil.isNullStr(projectId)){
    		sb.append(" and project_id = ?");
    		params.add(projectId);
    	}
    	sb.append(" group by project_id");
    	sb.append(" )");
    	sb.append(" and ( i.creator_ = ? or i.pm_id = ?");
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		sb.append(" or wdi.id_path like ? or wdi.id = ?");
    		params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
    	}
    	sb.append(" )");
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transProjectWeeklyStatVo2(list.get(0));
		}
		return null;
	}
}
