package com.wondertek.cpm.repository;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.OutsourcingUser;
import com.wondertek.cpm.domain.User;
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
	public OutsourcingUserVo findByContactId(Long id, User user,
			DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append("select wosu,wci.name,wci.serialNum from OutsourcingUser wosu");
		queryHql.append(" left join ContractInfo wci on wosu.contractId = wci.id");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		
		queryHql.append(" where (wci.creator = ?" + (count++) + " or wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++));
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			queryHql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		queryHql.append(" and wosu.id = ?" + (count++));
		params.add(id);
		
		List<Object[]> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new OutsourcingUserVo((OutsourcingUser) list.get(0)[0],StringUtil.null2Str(list.get(0)[1]),StringUtil.null2Str(list.get(0)[2]));
		}
		return null;
	}


}