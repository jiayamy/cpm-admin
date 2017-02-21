package com.wondertek.cpm.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;

public interface ConsultantBonusDao extends GenericDao<ConsultantsBonus, Long>{

	public Page<ConsultantBonusVo> getUserPage(String contractId,String consultantManId,String fromDate,String toDate ,Pageable pageable);
	
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId,Pageable pageable);

	public List<ConsultantBonusVo> getConsultantBonusData(Long contractId, Long consultantManId, Long fromDate,Long toDate);
	
	public List<ConsultantBonusVo> getConsultantBonusDetailList(Long contractId);
}
