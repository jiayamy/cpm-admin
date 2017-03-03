package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantsBonusVo;

public interface ConsultantsBonusDao extends GenericDao<ConsultantsBonus, Long>{

	public Page<ConsultantsBonusVo> getUserPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus,Pageable pageable);
	
	public Page<ConsultantsBonusVo> getConsultantsBonusRecordPage(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus,Pageable pageable);

	public List<ConsultantsBonusVo> getConsultantsBonusData(User user,DeptInfo deptInfo,ConsultantsBonus consultantsBonus);
	
	public List<ConsultantsBonusVo> getConsultantsBonusDetailList(Long contractId,Long statWeek);
}
