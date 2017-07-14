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
import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;
@Repository("bonusDao")
public class BonusDaoImpl extends GenericDaoImpl<Bonus, Long> implements BonusDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<Bonus> getDomainClass() {
		return Bonus.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<BonusVo> getPageByParams(User user,DeptInfo deptInfo,Bonus bonus,Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select distinct wci.serial_num,wbs.id,wbs.stat_week,wbs.contract_id,wbs.sales_bonus,wbs.project_bonus,wm.implemtationBonus,wm.academicBonus,wbs.consultants_bonus,wbs.bonus_total,wbs.creator_,wbs.create_time,wbs.contract_amount");
		countSql.append("select count(distinct wbs.id)");
		
		whereSql.append(" from w_bonus wbs");
		whereSql.append(" inner join ");
		whereSql.append("(");
		whereSql.append("select max(wbs1.id) as id,wbs1.contract_id from w_bonus wbs1 where wbs1.stat_week <= ? group by wbs1.contract_id");
		whereSql.append(")");
		whereSql.append(" b on wbs.id = b.id");
		whereSql.append(" inner join w_contract_info wci on wci.id = wbs.contract_id");
		whereSql.append(" left join (select a.contract_id,wcpb1.bonus_ as implemtationBonus,wcpb2.bonus_ as academicBonus from");
		whereSql.append("(select wcpb.contract_id,max(case when wcpb.dept_type = ? then wcpb.id end) as dept_type_5,");
		whereSql.append(" max(case when wcpb.dept_type = ? then wcpb.id end) as dept_type_4 from w_contract_project_bonus wcpb where wcpb.stat_week <= ? group by wcpb.contract_id) a");
		whereSql.append(" left join w_contract_project_bonus wcpb1 on wcpb1.id = a.dept_type_5");
		whereSql.append(" left join w_contract_project_bonus wcpb2 on wcpb2.id = a.dept_type_4) wm");
		whereSql.append(" on wbs.contract_id = wm.contract_id");
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		whereSql.append(" left join w_project_info wpi on wci.id = wpi.contract_id");
		whereSql.append(" left join w_dept_info wdi3 on wpi.dept_id = wdi3.id");
		//
		params.add(bonus.getStatWeek());
		params.add(DeptType.PROJECT_IMPLEMENTATION);
		params.add(DeptType.PRODUCT_DEVELOPMENT);
		params.add(bonus.getStatWeek());
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		//添加项目经理权限
    	whereSql.append(" or wpi.pm_id = ?");
    	params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi3.id_path like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//查询条件
		if (bonus.getContractId() != null) {
			whereSql.append(" and wbs.contract_id = ?");
			params.add(bonus.getContractId());
		}
		countSql.append(whereSql.toString());
		querySql.append(whereSql.toString());
		querySql.append(" order by wbs.stat_week desc");
		whereSql.setLength(0);
		whereSql = null;
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<BonusVo> returnList = new ArrayList<BonusVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transBonusVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}
	
	private BonusVo transBonusVo(Object[] o) {
		BonusVo vo = new BonusVo();
		vo.setSerialNum(StringUtil.null2Str(o[0]));
		vo.setId(StringUtil.nullToLong(o[1]));
		vo.setStatWeek(StringUtil.nullToLong(o[2]));
		vo.setContractId(StringUtil.nullToLong(o[3]));
		vo.setSalesBonus(StringUtil.nullToDouble(o[4]));
		vo.setProjectBonus(StringUtil.nullToDouble(o[5]));
		vo.setImplemtationBonus(StringUtil.nullToDouble(o[6]));
		vo.setAcademicBonus(StringUtil.nullToDouble(o[7]));
		vo.setConsultantsBonus(StringUtil.nullToDouble(o[8]));
		vo.setBonusTotal(StringUtil.nullToDouble(o[9]));
		vo.setCreator(StringUtil.null2Str(o[10]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[11]));
		vo.setContractAmount(StringUtil.nullToDouble(o[12]));
		return vo;
	}

	@Override
	public Page<BonusVo> getPageDetail(Long contractId,User user,DeptInfo deptInfo,Pageable pageable) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select distinct wci.serial_num,wbs.id,wbs.stat_week,wbs.contract_id,wbs.sales_bonus,wbs.project_bonus,wm.implemtationBonus,wm.academicBonus,wbs.consultants_bonus,wbs.bonus_total,wbs.creator_,wbs.create_time,wbs.contract_amount");
		countSql.append("select count(distinct wbs.id)");
		
		whereSql.append(" from w_bonus wbs");
		whereSql.append(" inner join w_contract_info wci on wbs.contract_id = wci.id");
		whereSql.append(" left join (select wcpb.bonus_id,");
		whereSql.append(" max(case when wcpb.dept_type = ? then wcpb.bonus_ end) as implemtationBonus,");
		whereSql.append(" max(case when wcpb.dept_type = ? then wcpb.bonus_ end) as academicBonus");
		whereSql.append(" from w_contract_project_bonus wcpb where wcpb.contract_id = ? group by wcpb.bonus_id) wm on wm.bonus_id = wbs.id");
		params.add(DeptType.PROJECT_IMPLEMENTATION);
		params.add(DeptType.PRODUCT_DEVELOPMENT);
		params.add(contractId);
		
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		whereSql.append(" left join w_project_info wpi on wci.id = wpi.contract_id");
		whereSql.append(" left join w_dept_info wdi3 on wpi.dept_id = wdi3.id");
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		//添加项目经理权限
    	whereSql.append(" or wpi.pm_id = ?");
    	params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi3.id_path like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		if (contractId != null) {
			whereSql.append(" and wbs.contract_id = ?");
			params.add(contractId);
		}
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		querySql.append(" order by wbs.stat_week desc");
		
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		List<BonusVo> returList = new ArrayList<BonusVo>();
		if (page.getContent() != null) {
			for (Object[] o : page.getContent()) {
				returList.add(transBonusVo(o));
			}
		}
		return new PageImpl<BonusVo>(returList, pageable, page.getTotalElements());
	}

	@Override
	public BonusVo getUserBonus(Long id, User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		int count = 0;//jpa格式 问号后的数组，一定要从0开始
		
		querySql.append("select wbs");
		whereSql.append(" from Bonus wbs");
		whereSql.append(" inner join ContractInfo wci on wci.id = wbs.contractId");
		whereSql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		whereSql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		whereSql.append(" left join ProjectInfo wpi on wci.id = wpi.contractId");
		whereSql.append(" left join DeptInfo wdi3 on wpi.deptId = wdi3.id");
		//权限
		whereSql.append(" where (wci.creator = ?" + (count++) + " or wci.salesmanId = ?" + (count++) + " or wci.consultantsId = ?" + (count++));
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		//添加项目经理权限
		whereSql.append(" or wpi.pmId = ?" + (count++));
    	params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.idPath like ?" + (count++) + " or wdi.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.idPath like ?" + (count++) + " or wdi2.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi3.idPath like ?" + (count++) + " or wdi3.id = ?" + (count++));
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//搜索条件
		if (id != null) {
			whereSql.append(" and wbs.id = ?" + (count++));
			params.add(id);
		}
		querySql.append(whereSql.toString());
		whereSql.setLength(0);
		whereSql = null;
		List<Bonus> list = this.queryAllHql(querySql.toString(), params.toArray());
		if (list != null && !list.isEmpty()) {
			return new BonusVo(list.get(0));
		}
		return null;
	}

	@Override
	public List<BonusVo> getBonusListl(Bonus bonus, User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select distinct wci.serial_num,wbs.id,wbs.stat_week,wbs.contract_id,wbs.sales_bonus,wbs.project_bonus,wm.implemtationBonus,wm.academicBonus,wbs.consultants_bonus,wbs.bonus_total,wbs.creator_,wbs.create_time,wbs.contract_amount");
		
		whereSql.append(" from w_bonus wbs");
		whereSql.append(" inner join ");
		whereSql.append("(");
		whereSql.append("select max(wbs1.id) as id,wbs1.contract_id from w_bonus wbs1 where wbs1.stat_week <= ? group by wbs1.contract_id");
		whereSql.append(")");
		whereSql.append(" b on wbs.id = b.id");
		whereSql.append(" inner join w_contract_info wci on wci.id = wbs.contract_id");
		whereSql.append(" left join (select a.contract_id,wcpb1.bonus_ as implemtationBonus,wcpb2.bonus_ as academicBonus from");
		whereSql.append("(select wcpb.contract_id,max(case when wcpb.dept_type = ? then wcpb.id end) as dept_type_5,");
		whereSql.append(" max(case when wcpb.dept_type = ? then wcpb.id end) as dept_type_4 from w_contract_project_bonus wcpb where wcpb.stat_week <= ? group by wcpb.contract_id) a");
		whereSql.append(" left join w_contract_project_bonus wcpb1 on wcpb1.id = a.dept_type_5");
		whereSql.append(" left join w_contract_project_bonus wcpb2 on wcpb2.id = a.dept_type_4) wm");
		whereSql.append(" on wbs.contract_id = wm.contract_id");
		whereSql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		whereSql.append(" left join w_dept_info wdi2 on wci.consultants_dept_id = wdi2.id");
		whereSql.append(" left join w_project_info wpi on wci.id = wpi.contract_id");
		whereSql.append(" left join w_dept_info wdi3 on wpi.dept_id = wdi3.id");
		//
		params.add(bonus.getStatWeek());
		params.add(DeptType.PROJECT_IMPLEMENTATION);
		params.add(DeptType.PRODUCT_DEVELOPMENT);
		params.add(bonus.getStatWeek());
		//权限
		whereSql.append(" where (wci.creator_ = ? or wci.sales_man_id = ? or wci.consultants_id = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		//添加项目经理权限
    	whereSql.append(" or wpi.pm_id = ?");
    	params.add(user.getId());
		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi2.id_path like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereSql.append(" or wdi3.id_path like ? or wdi3.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//查询条件
		if (bonus.getContractId() != null) {
			whereSql.append(" and wbs.contract_id = ?");
			params.add(bonus.getContractId());
		}
		querySql.append(whereSql.toString());
		querySql.append(" order by wbs.stat_week desc");
		whereSql.setLength(0);
		whereSql = null;
		List<Object[]> page = this.queryAllSql(querySql.toString(),params.toArray());
		List<BonusVo> returnList = new ArrayList<BonusVo>();
		if(page != null){
			for(Object[] o : page){
				returnList.add(transBonusVo(o));
			}
		}
		return returnList;
	}

}