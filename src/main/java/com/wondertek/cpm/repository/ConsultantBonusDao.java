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

	public Page<ConsultantBonusVo> getUserPage(User user,DeptInfo deptInfo,String contractId,String consultantManId,String statWeek ,Pageable pageable);
	
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId,Long statWeek,Pageable pageable);

	public List<ConsultantBonusVo> getConsultantBonusData(User user,DeptInfo deptInfo,Long contractId, Long consultantManId,Long statWeek);
	
	public List<ConsultantBonusVo> getConsultantBonusDetailList(Long contractId,Long statWeek);
}
