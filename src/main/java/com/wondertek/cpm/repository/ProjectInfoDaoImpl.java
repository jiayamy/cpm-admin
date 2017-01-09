package com.wondertek.cpm.repository;

import java.sql.Timestamp;
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
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;

@Repository("projectInfoDao")
public class ProjectInfoDaoImpl extends GenericDaoImpl<ProjectInfo, Long> implements ProjectInfoDao{

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ProjectInfo> getDomainClass() {
		return ProjectInfo.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		//只有项目经理，预算指定经理，项目创建人，项目部门以上部门管理人员能看到对应的项目
		
		sql.append("select a.id,a.serial_num from w_contract_info a where a.id in(");
			sql.append("select distinct b.contract_id from (");
				sql.append("select c.contract_id as contract_id from w_project_info c left join w_dept_info d on d.id = c.dept_id");
				sql.append(" where c.pm_id = ? or c.creator_ = ?");
				params.add(user.getId());
				params.add(user.getLogin());
				
				if(user.getIsManager()){
					sql.append(" or d.id_path like ? or d.id = ?");
					params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
					params.add(deptInfo.getId());
				}
				sql.append(" union all ");
				sql.append("select e.contract_id as contract_id from w_contract_budget e left join w_dept_info f on f.id = e.dept_id");
				sql.append(" where (e.user_id = ?");
				params.add(user.getId());
				if(user.getIsManager()){
					sql.append(" or f.id_path like ? or f.id = ?");
					params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
					params.add(deptInfo.getId());
				}
				sql.append(") and e.type_ = ? and e.purchase_type = ?");
				params.add(ContractBudget.TYPE_PURCHASE);
				params.add(ContractBudget.PURCHASETYPE_SERVICE);
			sql.append(") b");
		sql.append(")");
		
		List<Object[]> list = this.queryAllSql(sql.toString(), params.toArray());
		
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1])));
			}
		}
		return returnList;
	}
	
	@Override
	public Page<ProjectInfoVo> getUserPage(ProjectInfo projectInfo, Pageable pageable, User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		StringBuffer whereSql = new StringBuffer();
		StringBuffer orderSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		querySql.append("select wpi.id,wpi.serial_num,wpi.contract_id,wpi.budget_id,wpi.name_,wpi.pm_,wpi.dept_,wpi.start_day,wpi.end_day,wpi.budget_total,wpi.status_,wpi.finish_rate,wpi.creator_,wpi.create_time,wpi.updator_,wpi.update_time,");
		querySql.append("wci.serial_num as contract_num,");
		querySql.append("wcb.name_ as budget_name,wcb.budget_total as budget_original");
		
		countSql.append("select count(wpi.id)");
		
		whereSql.append(" from w_project_info wpi");
		whereSql.append(" left join w_contract_info wci on wci.id = wpi.contract_id");
		whereSql.append(" left join w_dept_info wdi on wdi.id = wpi.dept_id");
		whereSql.append(" left join w_contract_budget wcb on wcb.id = wpi.budget_id");
		whereSql.append(" left join w_dept_info wdi2 on wdi2.id = wcb.dept_id");
		
		whereSql.append(" where (wpi.pm_id = ? or wpi.creator_ = ? or wcb.user_id = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getId());

		if(user.getIsManager()){
			whereSql.append(" or wdi.id_path like ? or wdi.id = ? or wdi2.id_path like ? or wdi2.id = ?");
			
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereSql.append(")");
		
		//页面搜索条件
		if(projectInfo.getStatus() != null && projectInfo.getStatus() > 0){
			whereSql.append(" and wpi.status_ = ?");
			
			params.add(projectInfo.getStatus());
		}
		if(!StringUtil.isNullStr(projectInfo.getName())){
			whereSql.append(" and wpi.name_ like ?");
			
			params.add("%"+projectInfo.getName()+"%");
		}
		if(!StringUtil.isNullStr(projectInfo.getSerialNum())){
			whereSql.append(" and wpi.serial_num like ?");
			
			params.add("%"+projectInfo.getSerialNum()+"%");
		}
		if(projectInfo.getContractId() != null){
			whereSql.append(" and wpi.contract_id = ?");
			
			params.add(projectInfo.getContractId());
		}
		
		querySql.append(whereSql.toString());
		countSql.append(whereSql.toString());
		
		whereSql.setLength(0);
		whereSql = null;
		//排序
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
   				orderSql.append(order.getProperty());
    			if(order.isAscending()){
    				orderSql.append(" asc");
    			}else{
    				orderSql.append(" desc");
    			}
    		}
    	}
		querySql.append(orderSql.toString());
		orderSql.setLength(0);
		orderSql = null;
		
		Page<Object[]> page = this.querySqlPage(querySql.toString(), countSql.toString(), params.toArray(), pageable);
		
		List<ProjectInfoVo> returnList = new ArrayList<ProjectInfoVo>();
		if(page.getContent() != null){
			for(Object[] o : page.getContent()){
				returnList.add(transProjectInfoVo(o));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	private ProjectInfoVo transProjectInfoVo(Object[] o) {
//		querySql.append("select wpi.id,wpi.serial_num,wpi.contract_id,wpi.budget_id,wpi.name_,wpi.pm_,wpi.dept_,
//		wpi.start_day,wpi.end_day,wpi.budget_total,wpi.status_,wpi.finish_rate,wpi.creator_,wpi.create_time,
//		wpi.updator_,wpi.update_time,");
//		querySql.append("wci.serial_num,");
//		querySql.append("wcb.name_,wcb.budget_total");
		ProjectInfoVo vo = new ProjectInfoVo();
		vo.setId(StringUtil.nullToLong(o[0]));
		vo.setSerialNum(StringUtil.null2Str(o[1]));;
		vo.setContractId(StringUtil.nullToLong(o[2]));
		vo.setBudgetId(StringUtil.nullToLong(o[3]));
		vo.setName(StringUtil.null2Str(o[4]));
		vo.setPm(StringUtil.null2Str(o[5]));
		vo.setDept(StringUtil.null2Str(o[6]));
		
		vo.setStartDay(DateUtil.getZonedDateTime((Timestamp) o[7]));
		vo.setEndDay(DateUtil.getZonedDateTime((Timestamp) o[8]));
		vo.setBudgetTotal(StringUtil.nullToDouble(o[9]));
		vo.setStatus(StringUtil.nullToInteger(o[10]));
		vo.setFinishRate(StringUtil.nullToDouble(o[11]));
		vo.setCreator(StringUtil.null2Str(o[12]));
		vo.setCreateTime(DateUtil.getZonedDateTime((Timestamp) o[13]));
		
		vo.setUpdator(StringUtil.null2Str(o[14]));
		vo.setUpdateTime(DateUtil.getZonedDateTime((Timestamp) o[15]));
		
		vo.setContractNum(StringUtil.null2Str(o[16]));
		
		vo.setBudgetName(StringUtil.null2Str(o[17]));
		vo.setBudgetOriginal(StringUtil.nullToDouble(o[18]));
		
		return vo;
	}

	@Override
	public ProjectInfoVo getUserProjectInfo(Long id, User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		querySql.append("select wpi.id,wpi.serial_num,wpi.contract_id,wpi.budget_id,wpi.name_,wpi.pm_,wpi.dept_,wpi.start_day,wpi.end_day,wpi.budget_total,wpi.status_,wpi.finish_rate,wpi.creator_,wpi.create_time,wpi.updator_,wpi.update_time,");
		querySql.append("wci.serial_num as contract_num,");
		querySql.append("wcb.name_ as budget_name,wcb.budget_total as budget_original");
		querySql.append(" from w_project_info wpi");
		querySql.append(" left join w_contract_info wci on wci.id = wpi.contract_id");
		querySql.append(" left join w_dept_info wdi on wdi.id = wpi.dept_id");
		querySql.append(" left join w_contract_budget wcb on wcb.id = wpi.budget_id");
		querySql.append(" left join w_dept_info wdi2 on wdi2.id = wcb.dept_id");
		
		querySql.append(" where (wpi.pm_id = ? or wpi.creator_ = ? or wcb.user_id = ?");
		params.add(user.getId());
		params.add(user.getLogin());
		params.add(user.getId());

		if(user.getIsManager()){
			querySql.append(" or wdi.id_path like ? or wdi.id = ? or wdi2.id_path like ? or wdi2.id = ?");
			
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		querySql.append(")");
		
		querySql.append(" and wpi.id = ?");
		params.add(id);
		
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		if(list != null && !list.isEmpty()){
			return transProjectInfoVo(list.get(0));
		}
		return null;
	}
}	
