package com.wondertek.cpm.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.OutsourcingUserVo;
@Repository("outsourcingUserDao")
public class OutsourcingUserDaoImpl extends GenericDaoImpl<OutsourcingUser, Long> implements OutsourcingUserDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<OutsourcingUser> getDomainClass() {
		return OutsourcingUser.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public OutsourcingUserVo findById(Long id) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wosu,wci.name,wci.serialNum from OutsourcingUser wosu");
		queryHql.append(" left join ContractInfo wci on wosu.contractId = wci.id");
		
		queryHql.append(" where wosu.id = ?" + (count++));
		params.add(id);
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new OutsourcingUserVo((OutsourcingUser) list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}

	@Override
	public List<LongValue> queryUserRank(Long contractId) {

		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append(" select wosu.id,wosu.rank_ from w_outsourcing_user wosu");
		
		querySql.append(" where wosu.contract_id = ?");
		
		querySql.append(" order by wosu.id desc");
		params.add(contractId);
		
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1])));
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long,List<String>> getType(List<Long> projectIds) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> paramNameList = new HashMap<String, Object>();
		hql.append("select distinct wpi.id,wou.rank");
		hql.append(" from OutsourcingUser wou,ContractInfo wci,ProjectInfo wpi");
		hql.append(" where wou.contractId = wci.id and wci.id = wpi.contractId and wci.type = ?0 and wpi.id in (:projectIds)");
		
		paramNameList.put("projectIds", projectIds);
		
		List<Object[]> list = this.queryAllHql(hql.toString(), new Object[]{ContractInfo.TYPE_EXTERNAL}, paramNameList);
		Map<Long,List<String>> returnMap = new HashMap<Long,List<String>>();
		if(list != null){
			Long projectId = null;
			String rank = null;
			for(Object[] o : list){
				projectId = StringUtil.nullToLong(o[0]);
				rank = StringUtil.null2Str(o[1]);
				if(!returnMap.containsKey(projectId)){
					returnMap.put(projectId, new ArrayList<String>());
				}
				returnMap.get(projectId).add(rank);
			}
		}
		return returnMap;
	}
}