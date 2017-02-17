package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractInternalPurchase;

public interface ContractInternalPurchaseRepository extends JpaRepository<ContractInternalPurchase,Long>{
	
	@Query(" from ContractInternalPurchase where contractId = ?1")
	List<ContractInternalPurchase> findByContractId(Long contractId);
	
	@Query(" from ContractInternalPurchase where statWeek  = ?1")
	List<ContractInternalPurchase> findByStatWeek(Long statWeek);
}
