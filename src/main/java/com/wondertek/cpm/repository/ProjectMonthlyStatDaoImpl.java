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
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;

@Repository("projectMonthlyStatDao")
public class ProjectMonthlyStatDaoImpl extends GenericDaoImpl<ProjectMonthlyStat, Long> implements ProjectMonthlyStatDao{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProjectMonthlyStat> getDomainClass() {
		return ProjectMonthlyStat.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public Page<ProjectMonthlyStatVo> getUserPage(String projectId, Pageable pageable,User user) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.projectId, m.finishRate, m.humanCost, m.payment, m.statWeek, m.createTime, i.serialNum ");
		countsql.append(" select count(m.id)");
		sb.append(" from ProjectMonthlyStat m");
		sb.append(" left join ProjectInfo i on m.projectId = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from ProjectMonthlyStat where 1=1 ");
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
    	List<ProjectMonthlyStatVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectMonthlyStatVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private ProjectMonthlyStatVo transProjectMonthlyStatVo(Object[] o){
		ProjectMonthlyStatVo vo = new ProjectMonthlyStatVo();
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
	
	private ProjectMonthlyStatVo transProjectMonthlyStatVo2(Object[] o){
		ProjectMonthlyStatVo vo = new ProjectMonthlyStatVo();
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
	@Override
	public List<LongValue> queryUserProject(User user, DeptInfo deptInfo) {
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		StringBuffer wheresql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		querysql.append("select p.id, p.serial_num, p.name_ ");
		countsql.append("select count(p.id) ");
		wheresql.append("from w_project_info p ");
		List<Object[]> list = this.queryAllSql(querysql.toString()+wheresql.toString(), params.toArray());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		return returnList;
	}
	
	public ProjectMonthlyStatVo getById(Long id){
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		querysql.append(" select m.id, m.project_id, m.finish_rate, m.human_cost, m.payment_, m.stat_week, m.create_time, i.serial_num ");
		countsql.append(" select count(m.id)");
		sb.append(" from w_project_monthly_stat m");
		sb.append(" left join w_project_info i on m.project_id = i.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where m.id in (select max(id) from w_project_monthly_stat where 1=1 ");
    	if(!StringUtil.isNullStr(id)){
    		sb.append(" and id = ?");
    		params.add(id);
    	}
    	sb.append(" group by project_id");
    	sb.append(" )");
    	
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transProjectMonthlyStatVo2(list.get(0));
		}
		return null;
	}
	
}