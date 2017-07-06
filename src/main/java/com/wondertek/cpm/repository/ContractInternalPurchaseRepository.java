package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractInternalPurchase;

public interface ContractInternalPurchaseRepository extends JpaRepository<ContractInternalPurchase,Long>{
	
	@Modifying
	@Transactional
	@Query("delete from ContractInternalPurchase cip where cip.contractId = ?1 and cip.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
