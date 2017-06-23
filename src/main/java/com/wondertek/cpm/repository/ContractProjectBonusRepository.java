package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractProjectBonus;

public interface ContractProjectBonusRepository extends JpaRepository<ContractProjectBonus,Long>{
	
	@Query(" from ContractProjectBonus where statWeek = ?1")
	List<ContractProjectBonus> findByStatWeek(Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ContractProjectBonus cpb where cpb.contractId = ?1 and cpb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
