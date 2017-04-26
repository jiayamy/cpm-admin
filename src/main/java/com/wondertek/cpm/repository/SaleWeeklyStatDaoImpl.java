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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SaleWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SaleWeeklyStatVo;

@Repository("saleWeeklyStatDao")
public class SaleWeeklyStatDaoImpl extends GenericDaoImpl<SaleWeeklyStat, Long> implements SaleWeeklyStatDao  {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<SaleWeeklyStat> getDomainClass() {
		return SaleWeeklyStat.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<SaleWeeklyStatVo> getUserPage(String deptId, Pageable pageable, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		queryHql.append(" select s.id, s.originYear, s.deptId, s.annualIndex, s.finishTotal, s.receiveTotal, s.costTotal,"
				+ "s.salesHumanCost ,s.salesPayment, s.consultHumanCost ,s.consultPayment ,s.hardwarePurchase ,s.externalSoftware ,s.internalSoftware ,s.projectHumanCost ,"
				+ "s.projectPayment ,s.statWeek ,s.createTime ,wdi.name");
		countHql.append(" select count(s.id)");
		sb.append(" from SaleWeeklyStat s");
		sb.append(" left join DeptInfo wdi on s.deptId = wdi.id");
		//sb.append(" left join User u on wdi.id = u.deptId");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where s.id in (select max(id) from SaleWeeklyStat where 1=1 ");
    	if(!StringUtil.isNullStr(deptId)){
    		sb.append(" and deptId = ?" + (count++));
    		params.add(StringUtil.nullToLong(deptId));
    	}
    	sb.append(" group by deptId");
    	sb.append(" )");
    	sb.append(" and ( 1 = 2 ");
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
    			queryHql.toString() + sb.toString() + orderHql.toString(), 
    			countHql.toString() + sb.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<SaleWeeklyStatVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transSaleWeeklyStatVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	
	}
	
	private SaleWeeklyStatVo transSaleWeeklyStatVo(Object[] o){
		SaleWeeklyStatVo saleWeeklyStatVo = new SaleWeeklyStatVo();
		saleWeeklyStatVo.setId(StringUtil.nullToLong(o[0]));
		saleWeeklyStatVo.setOriginYear(StringUtil.nullToLong(o[1]));
		saleWeeklyStatVo.setDeptId(StringUtil.nullToLong(o[2]));
		saleWeeklyStatVo.setAnnualIndex(StringUtil.nullToDouble(o[3]));
		saleWeeklyStatVo.setFinishTotal(StringUtil.nullToDouble(o[4]));
		saleWeeklyStatVo.setReceiveTotal(StringUtil.nullToDouble(o[5]));
		saleWeeklyStatVo.setCostTotal(StringUtil.nullToDouble(o[6]));
		saleWeeklyStatVo.setSalesHumanCost(StringUtil.nullToDouble(o[7]));
		saleWeeklyStatVo.setSalesPayment(StringUtil.nullToDouble(o[8]));
		saleWeeklyStatVo.setConsultHumanCost(StringUtil.nullToDouble(o[9]));
		saleWeeklyStatVo.setConsultPayment(StringUtil.nullToDouble(o[10]));
		saleWeeklyStatVo.setHardwarePurchase(StringUtil.nullToDouble(o[11]));
		saleWeeklyStatVo.setExternalSoftware(StringUtil.nullToDouble(o[12]));
		saleWeeklyStatVo.setInternalSoftware(StringUtil.nullToDouble(o[13]));
		saleWeeklyStatVo.setProjectHumanCost(StringUtil.nullToDouble(o[14]));
		saleWeeklyStatVo.setProjectPayment(StringUtil.nullToDouble(o[15]));
		saleWeeklyStatVo.setStatWeek(StringUtil.nullToLong(o[16]));
		saleWeeklyStatVo.setCreateTime((ZonedDateTime) o[17]);
		saleWeeklyStatVo.setDept(StringUtil.nullToString(o[18]));
		return saleWeeklyStatVo;
	}

	@Override
	public SaleWeeklyStatVo getById(Long id, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querysql.append(" select s.id, s.originYear, s.deptId, s.annualIndex, s.finishTotal, s.receiveTotal, s.costTotal,"
				+ "s.salesHumanCost ,s.salesPayment, s.consultHumanCost ,s.consultPayment ,s.hardwarePurchase ,s.externalSoftware ,s.internalSoftware ,s.projectHumanCost ,"
				+ "s.projectPayment ,s.statWeek ,s.createTime ,wdi.name");
		sb.append(" from SaleWeeklyStat s");
		sb.append(" left join DeptInfo wdi on s.deptId = wdi.id");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where 1 = 1 ");
    	if(id != null){
    		sb.append(" and s.id = ?" + (count++));
    		params.add(id);
    	}
    	sb.append(" and ( 1 = 2 ");
		if(user.getIsManager()){
			sb.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
    	sb.append(" )");

    	StringBuffer orderHql = new StringBuffer();
    	List<Object[]> list = this.queryAllHql(
    			querysql.toString() + sb.toString() + orderHql.toString(), 
    			params.toArray()
    		);
    	if(list != null && !list.isEmpty()){
    		return transSaleWeeklyStatVo(list.get(0));
		}
    	return null;
	}

	@Override
	public SaleWeeklyStatVo getByStatWeekAndDeptId(Long statWeek, Long deptId, User user, DeptInfo deptInfo) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querysql.append(" select s.id, s.originYear, s.deptId, s.annualIndex, s.finishTotal, s.receiveTotal, s.costTotal,"
				+ "s.salesHumanCost ,s.salesPayment, s.consultHumanCost ,s.consultPayment ,s.hardwarePurchase ,s.externalSoftware ,s.internalSoftware ,s.projectHumanCost ,"
				+ "s.projectPayment ,s.statWeek ,s.createTime ,wdi.name");
		sb.append(" from SaleWeeklyStat s");
		sb.append(" left join DeptInfo wdi on s.deptId = wdi.id");
		sb.append(" left join User u on wdi.id = u.deptId");
		List<Object> params = new ArrayList<Object>();
    	sb.append(" where 1 = 1 ");
    	if(deptId != null){
    		sb.append(" and s.deptId = ?" + (count++));
    		params.add(deptId);
    	}
    	if(statWeek != null){
    		sb.append(" and s.statWeek = ?" + (count++));
    		params.add(statWeek);
    	}
    	sb.append(" and ( 1 = 2 ");
		if(user.getIsManager()){
			sb.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
    	sb.append(" )");
    	
    	StringBuffer orderHql = new StringBuffer();
    	List<Object[]> list = this.queryAllHql(
    			querysql.toString() + sb.toString() + orderHql.toString(), 
    			params.toArray()
    		);
    	if(list != null && !list.isEmpty()){
    		return transSaleWeeklyStatVo(list.get(0));
		}
		return null;
	}

}
