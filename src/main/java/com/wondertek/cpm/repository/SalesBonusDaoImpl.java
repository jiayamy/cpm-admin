package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SalesBonusVo;

@Repository("salesBonusDao")
public class SalesBonusDaoImpl extends GenericDaoImpl<SalesBonus, Long> implements SalesBonusDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<SalesBonus> getDomainClass() {
		return SalesBonus.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<SalesBonusVo> getUserPage(User user, DeptInfo deptInfo, SalesBonus salesBonus) {
		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		querySql.append("select wsb.id,wsb.stat_week,wsb.sales_man_id,wsb.sales_man,wsb.contract_id,wsb.origin_year,wsb.contract_amount,");
		querySql.append("wsb.tax_rate,wsb.receive_total,wsb.taxes_,wsb.share_cost,wsb.third_party_purchase,wsb.bonus_basis,wsb.bonus_rate,wsb.current_bonus,wsb.creator_,wsb.create_time,");
		querySql.append("wci.serial_num");
		querySql.append(" from w_sales_bonus wsb");
		querySql.append(" inner join (select max(wsb2.id) as id from w_sales_bonus wsb2 where wsb2.stat_week <= ? and wsb2.origin_year = ? group by wsb2.contract_id) wsbc on wsbc.id = wsb.id");
		querySql.append(" inner join w_contract_info wci on wci.id = wsb.contract_id");
		querySql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		//
		params.add(salesBonus.getStatWeek());
		params.add(salesBonus.getOriginYear());
		//权限
		querySql.append(" where (wci.creator_ = ? or wci.sales_man_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		if(user.getIsManager()){
			querySql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		querySql.append(")");
		
		if(salesBonus.getSalesManId() != null){
			querySql.append(" and wsb.sales_man_id = ?");
			params.add(salesBonus.getSalesManId());
		}
		if(salesBonus.getContractId() != null){
			querySql.append(" and wsb.contract_id = ?");
			params.add(salesBonus.getContractId());
		}
		//TODO 排序不能动，保证同一个销售的记录在一起，后面引用有处理
		querySql.append(" order by wsb.sales_man_id asc,wci.start_day asc,wsb.id asc");
		
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		List<SalesBonusVo> returnList = new ArrayList<SalesBonusVo>();
		if(list != null){
			//
			for(Object[] o : list){
//				returnList.add(new SalesBonusVo((SalesBonus)o[0],StringUtil.null2Str(o[1])));
				returnList.add(transSalesBonusVo(o));
			}
		}
		
		return returnList;
	}

	private SalesBonusVo transSalesBonusVo(Object[] o) {
//		queryHql.append("select wsb.id,wsb.stat_week,wsb.sales_mam_id,wsb.sales_mam,wsb.contract_id,wsb.origin_year,wsb.contract_amount,");
//		queryHql.append("wsb.tax_rate,wsb.receive_total,wsb.taxes_,wsb.share_cost,wsb.third_party_purchase,wsb.bonus_basis,wsb.bonus_rate,wsb.current_bonus,wsb.creator_,wsb.create_time");
//		queryHql.append("wci.serialNum");
		
		SalesBonusVo vo = new SalesBonusVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setStatWeek(StringUtil.nullToLong(o[1]));
		vo.setSalesManId(StringUtil.nullToLong(o[2]));
		vo.setSalesMan(StringUtil.null2Str(o[3]));
		vo.setContractId(StringUtil.nullToLong(o[4]));
		vo.setOriginYear(StringUtil.nullToLong(o[5]));
		vo.setContractAmount(StringUtil.nullToDouble(o[6]));
		
		vo.setTaxRate(StringUtil.nullToDouble(o[7]));
		vo.setReceiveTotal(StringUtil.nullToDouble(o[8]));
		vo.setTaxes(StringUtil.nullToDouble(o[9]));
		vo.setShareCost(StringUtil.nullToDouble(o[10]));
		vo.setThirdPartyPurchase(StringUtil.nullToDouble(o[11]));
		vo.setBonusBasis(StringUtil.nullToDouble(o[12]));
		vo.setBonusRate(StringUtil.nullToDouble(o[13]));
		vo.setCurrentBonus(StringUtil.nullToDouble(o[14]));
		vo.setCreator(StringUtil.null2Str(o[15]));;
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp)o[16]));
		
		vo.setContractNum(StringUtil.null2Str(o[17]));
		return vo;
	}
}