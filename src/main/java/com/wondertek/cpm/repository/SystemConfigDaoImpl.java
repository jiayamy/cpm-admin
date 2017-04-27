package com.wondertek.cpm.repository;

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
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.SystemConfig;

@Repository("systemConfigDao")
public class SystemConfigDaoImpl  extends GenericDaoImpl<SystemConfig, Long> implements SystemConfigDao{
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<SystemConfig> getDomainClass() {
		return SystemConfig.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<SystemConfig> getAllSystemConfig(String key, Pageable pageable) {

		StringBuffer querywhere = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		queryHql.append(" select sc.id, sc.key, sc.value, sc.description, sc.creator, sc.createTime, sc.updator, sc.updateTime");
		countHql.append(" select count(sc.id)");
		
		querywhere.append(" from SystemConfig sc");
		List<Object> params = new ArrayList<Object>();
		
		if(key!=null){
			querywhere.append(" where sc.key like ?0 ");
			params.add("%"+key+"%");
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
    	
    	
    	
    	List<SystemConfig> returnList = new ArrayList<>();
    	
    	
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transSystemConfig(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	
	}
	private SystemConfig transSystemConfig(Object[] o) {
		SystemConfig vo = new SystemConfig();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setKey(StringUtil.null2Str(o[1]));
		vo.setValue(StringUtil.null2Str(o[2]));
		vo.setDescription(StringUtil.null2Str(o[3]));
		vo.setCreator(StringUtil.null2Str(o[4]));
		vo.setCreateTime((ZonedDateTime)o[5]);
		vo.setUpdator(StringUtil.null2Str(o[6]));
		vo.setUpdateTime((ZonedDateTime)o[7]);
		return vo;
	}

}
