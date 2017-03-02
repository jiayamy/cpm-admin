package com.wondertek.cpm.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;

public interface ConsultantBonusDao extends GenericDao<ConsultantsBonus, Long>{

	public Page<ConsultantBonusVo> getUserPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus,Pageable pageable);
	
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus,Pageable pageable);

	public List<ConsultantBonusVo> getConsultantBonusData(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus);
	
	public List<ConsultantBonusVo> getConsultantBonusDetailList(Long contractId,Long statWeek);
}
