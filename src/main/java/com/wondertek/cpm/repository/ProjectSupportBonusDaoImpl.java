package com.wondertek.cpm.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProjectSupportBonusVo;

@Repository("projectSupportBonusDao")
public class ProjectSupportBonusDaoImpl extends GenericDaoImpl<ProjectSupportBonus, Long> implements ProjectSupportBonusDao {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ProjectSupportBonus> getDomainClass() {
		return ProjectSupportBonus.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ProjectSupportBonusVo> getPageByParams(User user,
			DeptInfo deptInfo, ProjectSupportBonus projectSupportBonus,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wci.serial_num,wpsb.id,wpsb.stat_week,wpsb.contract_id,wpsb.dept_type,wdt.name_,wpsb.pm_id,pm_name,wpsb.delivery_time,wpsb.acceptance_rate,wpsb.plan_days,wpsb.real_days,wpsb.bonus_adjust_rate,wpsb.bonus_rate,wpsb.bonus_acceptance_rate,wpsb.contract_amount,wpsb.tax_rate,wpsb.bonus_basis,wpsb.current_bonus,wpsb.creator_,wpsb.create_time");
		countSql.append("select count(wpsb.id)");
		
		whereSql.append(" from w_project_support_bonus wpsb");
		whereSql.append(" inner join");
		whereSql.append("(");
		whereSql.append("select max(wpsb1.id) as id,wpsb1.contract_id from w_project_support_bonus wpsb1 where wpsb1.stat_week <= ? group by wpsb1.contract_id)");
		whereSql.append(" b on wpsb.id = b.id");
		whereSql.append(" inner join w_contract_info wci on wci.id = wpsb.contract_id");
		whereSql.append(" inner join w_dept_type wdt on wdt.id = wpsb.dept_type");
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		//
		params.add(projectSupportBonus.getStatWeek());
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//查询条件
		if (projectSupportBonus.getContractId() != null) {
			whereSql.append(" and wpsb.contract_id = ?");
			params.add(projectSupportBonus.getContractId());
		}
		if (projectSupportBonus.getDeptType() != null) {
			whereSql.append(" and wpsb.dept_type = ?");
			params.add(projectSupportBonus.getDeptType());
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		querySql.append(" order by wpsb.stat_week desc");

		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<ProjectSupportBonusVo> returnList = new ArrayList<ProjectSupportBonusVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectSupportBonus(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	private ProjectSupportBonusVo transProjectSupportBonus(Object[] o) {
		ProjectSupportBonusVo vo = new ProjectSupportBonusVo();
		vo.setSerialNum(StringUtil.null2Str(o[0]));
		vo.setId(StringUtil.nullToLong(o[1]));
		vo.setStatWeek(StringUtil.nullToLong(o[2]));
		vo.setContractId(StringUtil.nullToLong(o[3]));
		vo.setDeptType(StringUtil.nullToLong(o[4]));
		vo.setDeptTypeName(StringUtil.null2Str(o[5]));
		vo.setPmId(StringUtil.nullToLong(o[6]));
		vo.setPmName(StringUtil.null2Str(o[7]));
		vo.setDeliveryTime(StringUtil.nullToInteger(o[8]));
		vo.setAcceptanceRate(StringUtil.nullToDouble(o[9]));
		vo.setPlanDays(StringUtil.nullToDouble(o[10]));
		vo.setRealDays(StringUtil.nullToInteger(o[11]));
		vo.setBonusAdjustRate(StringUtil.nullToDouble(o[12]));
		vo.setBonusRate(StringUtil.nullToDouble(o[13]));
		vo.setBonusAcceptanceRate(StringUtil.getScaleDouble(StringUtil.nullToDouble(o[14]), 2));
		vo.setContractAmount(StringUtil.nullToDouble(o[15]));
		vo.setTaxRate(StringUtil.nullToDouble(o[16]));
		vo.setBonusBasis(StringUtil.nullToDouble(o[17]));
		vo.setCurrentBonus(StringUtil.getScaleDouble(StringUtil.nullToDouble(o[18]), 2));
		vo.setCreator(StringUtil.null2Str(o[19]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[20]));
		
		return vo;
	}

	@Override
	public Page<ProjectSupportBonusVo> getPageDetail(Long contractId,
			Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wci.serial_num,wpsb.id,wpsb.stat_week,wpsb.contract_id,wpsb.dept_type,wdt.name_,wpsb.pm_id,pm_name,wpsb.delivery_time,wpsb.acceptance_rate,wpsb.plan_days,wpsb.real_days,wpsb.bonus_adjust_rate,wpsb.bonus_rate,wpsb.bonus_acceptance_rate,wpsb.contract_amount,wpsb.tax_rate,wpsb.bonus_basis,wpsb.current_bonus,wpsb.creator_,wpsb.create_time");
		countSql.append("select count(wpsb.id)");
		
		whereSql.append(" from w_project_support_bonus wpsb");
		whereSql.append(" inner join w_contract_info wci on wci.id = wpsb.contract_id");
		whereSql.append(" inner join w_dept_type wdt on wdt.id = wpsb.dept_type");
		
		if (contractId != null) {
			whereSql.append(" where wpsb.contract_id = ?");
			params.add(contractId);
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		querySql.append(" order by wpsb.stat_week desc");
		
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<ProjectSupportBonusVo> returList = new ArrayList<ProjectSupportBonusVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returList.add(transProjectSupportBonus(o));
			}
		}
		return new PageImpl<ProjectSupportBonusVo>(returList, pageable, page.getTotalElements());
	}

}
