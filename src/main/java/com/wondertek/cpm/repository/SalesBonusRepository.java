package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.SalesBonus;

/**
 * Spring Data JPA repository for the SalesBonus entity.
 */
public interface SalesBonusRepository extends JpaRepository<SalesBonus,Long> {
	
	@Query("select sum(currentBonus) from SalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from SalesBonus sb where sb.contractId = ?1 and sb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
