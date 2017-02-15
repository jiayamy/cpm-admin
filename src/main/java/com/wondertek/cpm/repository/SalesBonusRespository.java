package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.SalesBonus;

public interface SalesBonusRespository extends JpaRepository<SalesBonus,Long>{
	
	@Query(" from SalesBonus where contractId = ?1 and statWeek = ?2")
	List<SalesBonus> findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query("select sum(currentBonus) from SalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
}
