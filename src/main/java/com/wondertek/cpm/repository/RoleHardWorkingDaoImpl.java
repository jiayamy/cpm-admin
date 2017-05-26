package com.wondertek.cpm.repository;

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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.RoleHardWorking;


@Repository("RoleHardWorkingDao")
public class RoleHardWorkingDaoImpl extends GenericDaoImpl<RoleHardWorking, Long>  implements RoleHardWorkingDao  {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<RoleHardWorking> getDomainClass() {
		return RoleHardWorking.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Page<RoleHardWorking> getPageByParams(RoleHardWorking roleHardWorking,Pageable pageable) {
		int count = 0;
		StringBuffer querywhere = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		queryHql.append(" select rhw.id,rhw.serialNum,rhw.roleName,rhw.hardWorking,rhw.originMonth");
		countHql.append(" select count(rhw.id)");
		
		querywhere.append(" from RoleHardWorking rhw where 1 = 1");
		
		List<Object> params = new ArrayList<Object>();
		if(roleHardWorking.getOriginMonth()!=null){
			querywhere.append(" and rhw.originMonth = ?"+(count++)+"");
			params.add(roleHardWorking.getOriginMonth());
		}
		if(roleHardWorking.getUserId()!=null){
			querywhere.append(" and rhw.userId = ?"+(count++)+"");
			params.add(roleHardWorking.getUserId());
		}
    	
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
    			queryHql.toString() + querywhere.toString() + orderHql.toString(), 
    			countHql.toString() + querywhere.toString(),
    			params.toArray(), 
    			pageable
    		);
    	
    	List<RoleHardWorking> returnList = new ArrayList<RoleHardWorking>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transRoleHardWorking(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	private RoleHardWorking transRoleHardWorking(Object[] o) {
		RoleHardWorking vo = new RoleHardWorking();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setSerialNum(StringUtil.null2Str(o[1]));
		vo.setRoleName(StringUtil.null2Str(o[2]));
		vo.setHardWorking(StringUtil.nullToDouble(o[3]));
		vo.setOriginMonth(StringUtil.nullToLong(o[4]));
		return vo;
	
	}

	@Override
	public RoleHardWorking getByOriginMonthAndUserId(Long originMonth, Long userId) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		
		querysql.append(" select wrh.id, wrh.serial_num, wrh.last_name, wrh.hardworking, wrh.origin_month");
		countsql.append(" select count(wrh.id)");
		sb.append(" from w_role_hardworking wrh");
		
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where 1=1");
    	if(!StringUtil.isNullStr(originMonth)){
    		sb.append(" and wrh.origin_month = ?");
    		params.add(originMonth);
    	}
    	if(!StringUtil.isNullStr(userId)){
    		sb.append(" and wrh.user_id = ?");
    		params.add(userId);
    	}
    	List<Object[]> list = this.queryAllSql(querysql.toString()+sb.toString(), params.toArray());
    	if(list != null && !list.isEmpty()){
			return transRoleHardWorking(list.get(0));
		}
		return null;
	}
}