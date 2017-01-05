package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.UserTimesheet;
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
		sql.append("select distinct cu.contract_id,ci.serial_num from w_contract_user cu left join w_contract_info ci on cu.contract_id = ci.id where ci.id is not null and cu.user_id = ?");
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
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),UserTimesheet.TYPE_CONTRACT,StringUtil.null2Str(o[1])));
			}
		}
		return returnList;
	}
	
}
