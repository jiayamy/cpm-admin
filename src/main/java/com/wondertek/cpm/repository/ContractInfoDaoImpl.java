package com.wondertek.cpm.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
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

}
