package com.wondertek.cpm.repository;
import java.util.List;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.vo.DeptInfoVo;

public interface DeptInfoDao extends GenericDao<DeptInfo, Long> {
	
	List<String> getExistUserDeptNameByDeptParent(Long deptId,String idPath);

	DeptInfoVo getDeptInfo(Long id);
	
}
