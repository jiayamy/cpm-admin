package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;

@Repository("consultantBonusDao")
public class ConsultantBonusDaoImpl extends GenericDaoImpl<ConsultantsBonus, Long> implements ConsultantBonusDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ConsultantsBonus> getDomainClass() {
		return ConsultantsBonus.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ConsultantBonusVo> getUserPage(String contractId,String fromDate,String toDate,Pageable pageable) {
		StringBuffer sb = new StringBuffer();
		StringBuffer querysql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		StringBuffer wheresql = new StringBuffer();
//		querysql.append(" select m.id, m.contractId, m.contractAmount, m.consultantsId, m.consultants, m.bonusBasis, m.bonusRate,"
//				+ "m.consultantsShareRate , m.currentBonus ,m.creator ,m.statWeek ,m.createTime , i.serialNum , i.amount,i.name");
//		countsql.append(" select count(m.id)");
////		sb.append(" from ConsultantsBonus m");
//		sb.append(" from (select cb.id, cb.contractId, cb.contractAmount, cb.consultantsId, cb.consultants, cb.bonusBasis, cb.bonusRate,"
//				+ " cb.consultantsShareRate , cb.currentBonus ,cb.creator ,cb.statWeek ,cb.createTime from ConsultantsBonus cb where "
//				+ " cb.statWeek = (select max(c.statWeek) from ConsultantsBonus c where c.contractId = cb.contractId)) m");
//		sb.append(" left join ContractInfo i on m.contractId = i.id");
		querysql.append(" select m.id, m.contract_id, m.contract_amount, m.consultants_id, m.consultants_, m.bonus_basis, m.bonus_rate,"
				+ "m.consultants_share_rate , m.current_bonus ,m.creator_ ,m.stat_week ,m.create_time , i.serial_num , i.amount_, i.name_, j.serial_num as user_serial_num");
		countsql.append(" select count(m.id)");
//		sb.append(" from ConsultantsBonus m");
		sb.append(" from (select cb.id, cb.contract_id, cb.contract_amount, cb.consultants_id, cb.consultants_, cb.bonus_basis, cb.bonus_rate,"
				+ " cb.consultants_share_rate , cb.current_bonus ,cb.creator_ ,cb.stat_week ,cb.create_time from w_consultants_bonus cb"
				+ " where cb.stat_week = (select max(c.stat_week) from w_consultants_bonus c where c.contract_id = cb.contract_id)) m");
		sb.append(" left join w_contract_info i on m.contract_id = i.id");
		sb.append(" left join jhi_user j on m.consultants_id = j.id");
		List<Object> params = new ArrayList<Object>();
		wheresql.append(" where 1 = 1");
		if(!StringUtil.isNullStr(contractId)){
    		wheresql.append(" and m.contract_id = ?");
    		params.add(StringUtil.nullToLong(contractId));
    	}
    	if(!StringUtil.isNullStr(fromDate)){
    		wheresql.append(" and m.stat_week >= ?");
        	params.add(StringUtil.nullToLong(fromDate));
    	}
    	if(!StringUtil.isNullStr(toDate)){
    		wheresql.append(" and m.stat_week <= ?");
    		params.add(StringUtil.nullToLong(toDate));
    	}
//    	if(!StringUtil.isNullStr(contractId)){
//    		wheresql.append(" and m.contractId = ?");
//    		params.add(StringUtil.nullToLong(contractId));
//    	}
//    	if(!StringUtil.isNullStr(fromDate) && !StringUtil.isNullStr(toDate)){
//    		wheresql.append(" and m.statWeek >= ? and m.statWeek <= ?");
//        	params.add(StringUtil.nullToLong(fromDate));
//        	params.add(StringUtil.nullToLong(toDate));
//    	}
    	System.out.println("--------querysql:"+querysql.toString()+sb.toString()+wheresql.toString());
    	System.out.println("--------countsql:"+countsql.toString()+sb.toString()+wheresql.toString());
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
//    	Page<Object[]> page = this.queryHqlPage(
//    			querysql.toString() + sb.toString() + wheresql.toString() + orderHql.toString(), 
//    			countsql.toString() + sb.toString() + wheresql.toString(), 
//    			params.toArray(), 
//    			pageable
//    		);
    	Page<Object[]> page = this.querySqlPage(
    			querysql.toString() + sb.toString() + wheresql.toString() + orderHql.toString(), 
    			countsql.toString() + sb.toString() + wheresql.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<ConsultantBonusVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
//    		Map<Long,Long> tmp = new HashMap<Long,Long>();
			for(Object[] o : page.getContent()){
//				Long key1 = StringUtil.nullToLong(o[1]);
//				Long val10 = StringUtil.nullToLong(o[10]);
//				if(tmp.get(key1) == null){
//					tmp.put(key1, val10);
//				}else if(tmp.get(key1) < val10){
//					tmp.put(key1, val10);
//				}
				returnList.add(transConsultantBonusVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
//	querysql.append(" select m.id, m.contractId, m.contractAmount, m.consultantsId, m.consultantsName, m.bonusBasis, m.bonusRate,"
//			+ "m.consultantsShareRate , m.currentBonus ,m.creator ,m.statWeek ,m.createTime , i.serialNum , i.name");
	private ConsultantBonusVo transConsultantBonusVo(Object[] o){
		ConsultantBonusVo consultantBonusVo = new ConsultantBonusVo();
		consultantBonusVo.setId(StringUtil.nullToLong(o[0]));
		consultantBonusVo.setContractId(StringUtil.nullToLong(o[1]));
		consultantBonusVo.setContractAmount(StringUtil.nullToDouble(o[2]));
		consultantBonusVo.setConsultantsId(StringUtil.nullToLong(o[3]));
		consultantBonusVo.setConsultantsName(StringUtil.null2Str(o[4]));
		consultantBonusVo.setBonusBasis(StringUtil.nullToDouble(o[5]));
		consultantBonusVo.setBonusRate(StringUtil.nullToDouble(o[6]));
		consultantBonusVo.setConsultantsShareRate(StringUtil.nullToDouble(o[7]));
		consultantBonusVo.setCurrentBonus(StringUtil.nullToDouble(o[8]));
		consultantBonusVo.setCreator(StringUtil.null2Str(o[9]));
		consultantBonusVo.setStatWeek(StringUtil.nullToLong(o[10]));
//		consultantBonusVo.setCreateTime((ZonedDateTime) (o[11]));
		consultantBonusVo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[11]));
		consultantBonusVo.setSerialNum(StringUtil.null2Str(o[12]));
		consultantBonusVo.setAmount(StringUtil.nullToDouble(o[13]));
		consultantBonusVo.setName(StringUtil.null2Str(o[14]));
		consultantBonusVo.setConsultantsSerialNum(StringUtil.null2Str(o[15]));
		return consultantBonusVo;
	}

	@Override
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId, Pageable pageable) {
		StringBuffer fromHql = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		
		queryHql.append(" select m.id, m.contractId, m.contractAmount, m.consultantsId, m.consultants, m.bonusBasis, m.bonusRate,"
				+ "m.consultantsShareRate , m.currentBonus ,m.creator ,m.statWeek ,m.createTime , i.serialNum , i.amount,i.name,u.serialNum");
		countHql.append(" select count(m.id)");
		fromHql.append(" from ConsultantsBonus m");
		fromHql.append(" left join ContractInfo i on m.contractId = i.id");
		fromHql.append(" left join User u on m.consultantsId = u.id");
		
		List<Object> params = new ArrayList<Object>();
		whereHql.append(" where 1 = 1");
		if(!StringUtil.isNullStr(contractId)){
    		whereHql.append(" and m.contractId = ?");
    		params.add(StringUtil.nullToLong(contractId));
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
    			queryHql.toString() + fromHql.toString() + whereHql.toString() + orderHql.toString(), 
    			countHql.toString() + fromHql.toString() + whereHql.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<ConsultantBonusVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transHqlConsultantBonusVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private ConsultantBonusVo transHqlConsultantBonusVo(Object[] o){
		ConsultantBonusVo consultantBonusVo = new ConsultantBonusVo();
		consultantBonusVo.setId(StringUtil.nullToLong(o[0]));
		consultantBonusVo.setContractId(StringUtil.nullToLong(o[1]));
		consultantBonusVo.setContractAmount(StringUtil.nullToDouble(o[2]));
		consultantBonusVo.setConsultantsId(StringUtil.nullToLong(o[3]));
		consultantBonusVo.setConsultantsName(StringUtil.null2Str(o[4]));
		consultantBonusVo.setBonusBasis(StringUtil.nullToDouble(o[5]));
		consultantBonusVo.setBonusRate(StringUtil.nullToDouble(o[6]));
		consultantBonusVo.setConsultantsShareRate(StringUtil.nullToDouble(o[7]));
		consultantBonusVo.setCurrentBonus(StringUtil.nullToDouble(o[8]));
		consultantBonusVo.setCreator(StringUtil.null2Str(o[9]));
		consultantBonusVo.setStatWeek(StringUtil.nullToLong(o[10]));
//		consultantBonusVo.setCreateTime((ZonedDateTime) (o[11]));
		consultantBonusVo.setCreateTime((ZonedDateTime)(o[11]));
		consultantBonusVo.setSerialNum(StringUtil.null2Str(o[12]));
		consultantBonusVo.setAmount(StringUtil.nullToDouble(o[13]));
		consultantBonusVo.setName(StringUtil.null2Str(o[14]));
		consultantBonusVo.setConsultantsSerialNum(StringUtil.null2Str(o[15]));
		return consultantBonusVo;
	}
}
