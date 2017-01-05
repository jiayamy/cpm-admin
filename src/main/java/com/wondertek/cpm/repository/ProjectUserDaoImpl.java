package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.LongValue;
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
		sql.append("select distinct pu.project_id,pi.serial_num from w_project_user pu left join w_project_info pi on pu.project_id = pi.id where pi.id is not null and pu.user_id = ?");
		params.add(userId);
		
		if(weekDays != null && weekDays.length > 0){
			sql.append(" and (");
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
		
		List<Object[]> list = this.queryAllSql(sql.toString(),params.toArray());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),UserTimesheet.TYPE_PROJECT,StringUtil.null2Str(o[1])));
			}
		}
		return returnList;
	}
	
}