package com.wondertek.cpm.repository;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.vo.DeptInfoVo;
@Repository("deptInfoDao")
public class DeptInfoDaoImpl extends GenericDaoImpl<DeptInfo, Long> implements DeptInfoDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<DeptInfo> getDomainClass() {
		return DeptInfo.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public List<String> getExistUserDeptNameByDeptParent(Long deptId, String idPath) {
		StringBuffer querySql = new StringBuffer();
		querySql.append("select distinct dept.deptName from jhi_user jh,");
		querySql.append("(select wdi.id as deptid,wdi.name_ as deptName from w_dept_info wdi where (wdi.id = ? or wdi.id_path like ?) and wdi.status_ = ?) dept");
		querySql.append(" where jh.dept_id = dept.deptid");
		
		List<Object> list = this.queryAllSql(querySql.toString(), new Object[]{deptId,idPath + deptId + "/%",CpmConstants.STATUS_VALID});
		
		if(list != null && !list.isEmpty()){
			List<String> returnList = new ArrayList<String>();
			for(Object o : list){
				returnList.add(StringUtil.null2Str(o));
			}
			return returnList;
		}		
		return null;
	}

	@Override
	public DeptInfoVo getDeptInfo(Long id) {
		StringBuffer querySql = new StringBuffer();
		querySql.append("select wdi,wdi2.name as parentName,wdt.name as typeName from DeptInfo wdi");
		querySql.append(" left join DeptType wdt on wdt.id = wdi.type");
		querySql.append(" left join DeptInfo wdi2 on wdi2.id = wdi.parentId");
		querySql.append(" where wdi.id = ?0");
		
		List<Object[]> list = this.queryAllHql(querySql.toString(), new Object[]{id});
		if(list != null && !list.isEmpty()){
			Object[] o = list.get(0);
			return new DeptInfoVo((DeptInfo)o[0],StringUtil.null2Str(o[1]),StringUtil.null2Str(o[2]));
		}
		return null;
	}
	/**
	 * 根据合同人员的编号得到人员相应的部门名称。
	 */
	@Override
	public DeptInfo findDeptInfo(String user_serial_num) {
		DeptInfo deptInfo = new DeptInfo();
		StringBuffer querySql = new StringBuffer();
		querySql.append("select wdi.name,wdi.id from DeptInfo wdi");
		querySql.append(" inner join User u on u.deptId = wdi.id");
		querySql.append(" where u.serialNum = ?0");
		List<Object[]> list =this.queryAllHql(querySql.toString(), new Object[]{user_serial_num});
		if(list !=null && !list.isEmpty()){
			String deptName = null;
			Long deptId = null;
			for(Object[] o : list){
				deptName = StringUtil.null2Str(o[0]);
				deptId = StringUtil.nullToLong(o[1]);
			}
			deptInfo.setName(deptName);
			deptInfo.setId(deptId);
			return deptInfo;
		}
		return null;
	}

}
