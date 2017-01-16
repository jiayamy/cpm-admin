package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
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
	public Page<ContractInfo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable) {
		
		
		StringBuffer sb = new StringBuffer();
		List<Object>  params = new ArrayList<Object>();
		sb.append("where 1=1");
		if (!StringUtil.isNullStr(contractInfo.getName())) {
			sb.append(" and name like ?");
			params.add("%"+contractInfo.getName()+"%");
		}
		if (contractInfo.getType() != null) {
			sb.append(" and type= ?");
			params.add(contractInfo.getType());
		}
		if (contractInfo.getIsEpibolic() != null) {
			sb.append(" and isEpibolic= ?");
			params.add(contractInfo.getIsEpibolic());
		}
		if (contractInfo.getIsPrepared() != null) {
			sb.append(" and isPrepared= ?");
			params.add(contractInfo.getIsPrepared());
		}
		if (contractInfo.getSalesmanId() != null) {
			sb.append(" and salesmanId= ?");
			params.add(contractInfo.getSalesmanId());
		}
		sb.append(" and status = ?");
    	params.add(CpmConstants.STATUS_VALID);
    	
    	StringBuffer orderHql = new StringBuffer();
    	if (pageable != null) {
			for (Order order : pageable.getSort()) {
				if (CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())) {
					continue;
				}
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
    	Page<ContractInfo> page = this.queryHqlPage(
    			"from ContractInfo "+sb.toString()+orderHql.toString(),
    			"select count(id) from ContractInfo "+sb.toString(), 
    			params.toArray(), 
    			pageable);
    	
		return page;
	}

	@Override
	public boolean checkByContract(String serialNum, Long id) {
		StringBuffer countHql = new StringBuffer();
		ArrayList<Object> params = new ArrayList<>();
		countHql.append("select count(id) form ContractInfo where serialNum = ? ");
		params.add(serialNum);
		if (id != null) {
			countHql.append("and id <> ?");
			params.add(id);
		}
		return this.countHql(countHql.toString(), params.toArray())>0;
	}

	@Override
	public ContractInfoVo getUserContractInfo(Long id, User user, DeptInfo deptInfo) {
		
		
		
		
		return null;
	}

	@Override
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

}
