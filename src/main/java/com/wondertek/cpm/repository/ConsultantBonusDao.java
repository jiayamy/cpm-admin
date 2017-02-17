package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;

public interface ConsultantBonusDao extends GenericDao<ConsultantsBonus, Long>{

	public Page<ConsultantBonusVo> getUserPage(String contractId,String fromDate,String toDate ,Pageable pageable);
	
	public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId,Pageable pageable);
}
