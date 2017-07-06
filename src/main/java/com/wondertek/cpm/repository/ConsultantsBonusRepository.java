package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ConsultantsBonus;

public interface ConsultantsBonusRepository extends JpaRepository<ConsultantsBonus,Long>{
	
	@Query("select sum(currentBonus) from ConsultantsBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ConsultantsBonus cb where cb.contractId = ?1 and cb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
