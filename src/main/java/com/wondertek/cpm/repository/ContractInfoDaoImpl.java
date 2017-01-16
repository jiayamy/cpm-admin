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

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
@Repository("contractInfoDao")
public class ContractInfoDaoImpl extends GenericDaoImpl<ContractInfo, Long> implements ContractInfoDao {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ContractInfo> getDomainClass() {
		return ContractInfo.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Page<ContractInfoVo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable, User user, DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		
		StringBuffer whereHql = new StringBuffer();
		StringBuffer orderHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		queryHql.append("select wci");
		countHql.append("select count(wci.id)");
		
		whereHql.append(" from ContractInfo wci");
		whereHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		whereHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		
		//权限
		whereHql.append(" where (wci.creator = ? or wci.salesmanId = ? or wci.consultantsId = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			whereHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			whereHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		whereHql.append(")");
		if (!StringUtil.isNullStr(contractInfo.getSerialNum())) {
			whereHql.append(" and wci.serialNum like ?");
			params.add("%"+contractInfo.getSerialNum()+"%");
		}
		
		if (!StringUtil.isNullStr(contractInfo.getName())) {
			whereHql.append(" and wci.name like ?");
			params.add("%"+contractInfo.getName()+"%");
		}
		if (contractInfo.getType() != null) {
			whereHql.append(" and wci.type = ?");
			params.add(contractInfo.getType());
		}
		if (contractInfo.getIsEpibolic() != null) {
			whereHql.append(" and wci.isEpibolic = ?");
			params.add(contractInfo.getIsEpibolic());
		}
		if (contractInfo.getIsPrepared() != null) {
			whereHql.append(" and wci.isPrepared = ?");
			params.add(contractInfo.getIsPrepared());
		}
		if (contractInfo.getSalesmanId() != null) {
			whereHql.append(" and wci.salesmanId = ?");
			params.add(contractInfo.getSalesmanId());
		}
		if(contractInfo.getConsultantsId() != null){
			whereHql.append(" and wci.consultantsId = ?");
			params.add(contractInfo.getConsultantsId());
		}
		
//		sb.append(" and wci.status = ?");
//    	params.add(CpmConstants.STATUS_VALID);
		
		queryHql.append(whereHql.toString());
		countHql.append(whereHql.toString());
		whereHql.setLength(0);
		whereHql = null;
		
    	if (pageable != null) {
			for (Order order : pageable.getSort()) {
				if (orderHql.length() != 0) {
					orderHql.append(",");
				}else {
					orderHql.append(" order by ");
				}
				if (order.isAscending()) {
					orderHql.append(order.getProperty()).append(" asc");
				}else {
					orderHql.append(order.getProperty()).append(" desc");
				}
			}
		}
    	queryHql.append(orderHql.toString());
		orderHql.setLength(0);
		orderHql = null;
		
		Page<ContractInfo> page = this.queryHqlPage(queryHql.toString(), countHql.toString(), params.toArray(), pageable);
    	List<ContractInfoVo> returnList = new ArrayList<ContractInfoVo>();
		if(page.getContent() != null){
			for(ContractInfo o : page.getContent()){
				returnList.add(new ContractInfoVo(o,null));
			}
		}
		return new PageImpl(returnList, pageable, page.getTotalElements());
	}

	@Override
	public boolean checkByContract(String serialNum, Long id) {
		StringBuffer countHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<>();
		countHql.append("select count(id) from ContractInfo where serialNum = ? ");
		params.add(serialNum);
		if (id != null) {
			countHql.append("and id <> ?");
			params.add(id);
		}
		return this.countHql(countHql.toString(), params.toArray())>0;
	}

	@Override
	public ContractInfoVo getUserContractInfo(Long id, User user, DeptInfo deptInfo) {
		StringBuffer queryHql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		
		queryHql.append("select wci from ContractInfo wci");
		queryHql.append(" left join DeptInfo wdi on wci.deptId = wdi.id");
		queryHql.append(" left join DeptInfo wdi2 on wci.consultantsDeptId = wdi2.id");
		
		queryHql.append(" where (wci.creator = ? or wci.salesmanId = ? or wci.consultantsId = ?");
		params.add(user.getLogin());
		params.add(user.getId());
		params.add(user.getId());
		if(user.getIsManager()){
			queryHql.append(" or wdi.idPath like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
			
			queryHql.append(" or wdi2.idPath like ? or wdi2.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		queryHql.append(")");
		queryHql.append(" and wci.id = ?");
		params.add(id);
		
		List<ContractInfo> list = this.queryAllHql(queryHql.toString(),params.toArray());
		if(list != null && !list.isEmpty()){
			return new ContractInfoVo(list.get(0));
		}
		return null;
	}

	@Override
<<<<<<< HEAD
	public List<LongValue> queryUserContract(User user, DeptInfo deptInfo) {
		StringBuffer querySql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<Object>();
		
		querySql.append(" select wci.id,wci.serial_num,wci.name_ from w_contract_info wci");
		querySql.append(" left join w_dept_info wdi on wci.dept_id = wdi.id");
		querySql.append(" where (wci.sales_man_id = ? or wci.consultants_id = ? or wci.creator_ = ?");
		
		params.add(user.getId());
		params.add(user.getId());
		params.add(user.getLogin());
		
		if (user.getIsManager()) {
			querySql.append(" or wdi.id_path like ? or wdi.id = ?");
			params.add(deptInfo.getIdPath() + deptInfo.getId() + "/%");
			params.add(deptInfo.getId());
		}
		
		querySql.append(")");
		List<Object[]> list = this.queryAllSql(querySql.toString(), params.toArray());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if(list != null){
			for(Object[] o : list){
				returnList.add(new LongValue(StringUtil.nullToLong(o[0]),StringUtil.null2Str(o[1]) + ":" + StringUtil.null2Str(o[2])));
			}
		}
		
		return returnList;
	}

=======
	public int finishContractInfo(Long id, Double finishRate, String updator) {
		return this.excuteHql("update ContractInfo set finishRate = ? , updator = ?, updateTime = ? where id = ?", new Object[]{finishRate,updator,ZonedDateTime.now(),id});
	}
>>>>>>> c97d2fc8c09afbae2401d6cbfd61ac156131f0fe
}
