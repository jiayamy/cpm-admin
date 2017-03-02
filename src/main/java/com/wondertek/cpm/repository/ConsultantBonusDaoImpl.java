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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
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
	public Page<ConsultantBonusVo> getUserPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus,Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		querySql.append(" select m.id, m.contract_id, m.contract_amount, m.consultants_id, m.consultants_, m.bonus_basis, m.bonus_rate,");
		querySql.append(" m.consultants_share_rate , m.current_bonus ,m.creator_ ,m.stat_week ,m.create_time , i.serial_num , i.amount_, i.name_");
		
		countSql.append(" select count(m.id)");
		
		whereSql.append(" from w_consultants_bonus m");
		whereSql.append(" inner join (select max(wcb.id) as id from w_consultants_bonus wcb where wcb.stat_week <= ? group by wcb.contract_id) wcbc on wcbc.id = m.id");
		whereSql.append(" inner join w_contract_info i on i.id = m.contract_id");
		whereSql.append(" left join w_dept_info wdi on i.consultants_dept_id = wdi.id");
		List<Object> params = new ArrayList<Object>();
    	params.add(consultantsBonus.getStatWeek());
    	//权限
    	whereSql.append(" where (i.creator_ = ? or i.consultants_id = ?");
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
    	whereSql.append(")");
		if(consultantsBonus.getContractId() != null){
    		whereSql.append(" and m.contract_id = ?");
    		params.add(consultantsBonus.getContractId());
    	}
		if(consultantsBonus.getConsultantsId() != null){
			whereSql.append(" and m.consultants_id = ?");
			params.add(consultantsBonus.getConsultantsId());
		}
    	StringBuffer orderSql = new StringBuffer();
    	if(pageable.getSort() != null){//页面都会有个默认排序
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderSql.length() != 0){
    				orderSql.append(",");
    			}else{
    				orderSql.append(" order by ");
    			}
    			if(order.isAscending()){
    				orderSql.append(order.getProperty()).append(" asc");
    			}else{
    				orderSql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
    	Page<Object[]> page = this.querySqlPage(
    			querySql.toString() + whereSql.toString() + orderSql.toString(), 
    			countSql.toString() + whereSql.toString(), 
    			params.toArray(), 
    			pageable
    		);
    	List<ConsultantBonusVo> returnList = new ArrayList<>();
    	if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transConsultantBonusVo(o));
			}
		}
    	return new PageImpl(returnList, pageable, page.getTotalElements());
	}
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
		consultantBonusVo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[11]));
		consultantBonusVo.setSerialNum(StringUtil.null2Str(o[12]));
		consultantBonusVo.setAmount(StringUtil.nullToDouble(o[13]));
		consultantBonusVo.setName(StringUtil.null2Str(o[14]));
//		consultantBonusVo.setConsultantsSerialNum(StringUtil.null2Str(o[15]));
		return consultantBonusVo;
	}

	@Override
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus, Pageable pageable) {
		StringBuffer fromHql = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		
		queryHql.append(" select m.id, m.contractId, m.contractAmount, m.consultantsId, m.consultants, m.bonusBasis, m.bonusRate,");
		queryHql.append(" m.consultantsShareRate , m.currentBonus ,m.creator ,m.statWeek ,m.createTime , i.serialNum , i.amount,i.name");
		
		countHql.append(" select count(m.id)");
		
		fromHql.append(" from ConsultantsBonus m");
		fromHql.append(" left join ContractInfo i on m.contractId = i.id");
		fromHql.append(" left join DeptInfo di on i.consultantsDeptId = di.id");
		
		List<Object> params = new ArrayList<Object>();
    	//权限
    	whereHql.append(" where (i.creator = ? or i.consultantsId = ?");
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		whereHql.append(" or di.idPath like ? or di.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
    	whereHql.append(")");
		
		whereHql.append(" and m.statWeek <= ?");
		params.add(consultantsBonus.getStatWeek());
		if(consultantsBonus.getContractId() != null){
    		whereHql.append(" and m.contractId = ?");
    		params.add(consultantsBonus.getContractId());
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
		consultantBonusVo.setCreateTime((ZonedDateTime)(o[11]));
		consultantBonusVo.setSerialNum(StringUtil.null2Str(o[12]));
		consultantBonusVo.setAmount(StringUtil.nullToDouble(o[13]));
		consultantBonusVo.setName(StringUtil.null2Str(o[14]));
//		consultantBonusVo.setConsultantsSerialNum(StringUtil.null2Str(o[15]));
		return consultantBonusVo;
	}

	@Override
	public List<ConsultantBonusVo> getConsultantBonusData(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		querySql.append(" select m.id, m.contract_id, m.contract_amount, m.consultants_id, m.consultants_, m.bonus_basis, m.bonus_rate,"
				+ "m.consultants_share_rate , m.current_bonus ,m.creator_ ,m.stat_week ,m.create_time , i.serial_num , i.amount_, i.name_");
		whereSql.append(" from w_consultants_bonus m");
		whereSql.append(" inner join (select max(wcb.id) as id from w_consultants_bonus wcb where wcb.stat_week <= ? group by wcb.contract_id) wcbc on wcbc.id = m.id");
		whereSql.append(" inner join w_contract_info i on i.id = m.contract_id");
		whereSql.append(" left join w_dept_info wdi on i.consultants_dept_id = wdi.id");
		List<Object> params = new ArrayList<Object>();
		params.add(consultantsBonus.getStatWeek());
		
    	//权限
    	whereSql.append(" where (i.creator_ = ? or i.consultants_id = ?");
    	params.add(user.getLogin());
    	params.add(user.getId());
    	if(user.getIsManager()){
    		whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
    	whereSql.append(")");
    	
		if(consultantsBonus.getContractId() != null){
    		whereSql.append(" and m.contract_id = ?");
    		params.add(consultantsBonus.getContractId());
    	}
		if(consultantsBonus.getConsultantsId() != null){
			whereSql.append(" and m.consultants_id = ?");
			params.add(consultantsBonus.getConsultantsId());
		}
		StringBuffer orderSql = new StringBuffer();
    	orderSql.append(" order by m.id desc");
    	List<Object[]> resultList = this.queryAllSql(querySql.toString() + whereSql.toString() + orderSql.toString(), params.toArray());
    	List<ConsultantBonusVo> returnList = new ArrayList<>();
    	if(resultList != null && !resultList.isEmpty()){
			for(Object[] o : resultList){
				returnList.add(transConsultantBonusVo(o));
			}
		}
    	return returnList;
	}

	@Override
	public List<ConsultantBonusVo> getConsultantBonusDetailList(Long contractId,Long statWeek) {
		StringBuffer fromHql = new StringBuffer();
		StringBuffer queryHql = new StringBuffer();
		StringBuffer whereHql = new StringBuffer();
		
		queryHql.append(" select m.id, m.contractId, m.contractAmount, m.consultantsId, m.consultants, m.bonusBasis, m.bonusRate,"
				+ "m.consultantsShareRate , m.currentBonus ,m.creator ,m.statWeek ,m.createTime , i.serialNum , i.amount,i.name,u.serialNum");
		fromHql.append(" from ConsultantsBonus m");
		fromHql.append(" left join ContractInfo i on m.contractId = i.id");
		fromHql.append(" left join User u on m.consultantsId = u.id");
		
		List<Object> params = new ArrayList<Object>();
		whereHql.append(" where m.statWeek <= ?");
		params.add(statWeek);	//resource已设默认值,不为空
		if(contractId != null){
    		whereHql.append(" and m.contractId = ?");
    		params.add(contractId);
    	}
		
    	List<Object[]> resultList = this.queryAllHql(
    			queryHql.toString() + fromHql.toString() + whereHql.toString(), 
    			params.toArray());
    	List<ConsultantBonusVo> returnList = new ArrayList<>();
    	if(resultList != null){
			for(Object[] o : resultList){
				returnList.add(transHqlConsultantBonusVo(o));
			}
		}
    	return returnList;
	}
}
