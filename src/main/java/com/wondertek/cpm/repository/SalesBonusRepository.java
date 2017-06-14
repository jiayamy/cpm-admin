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
	
	@Query(" from SalesBonus where contractId = ?1 and statWeek = ?2")
	List<SalesBonus> findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query("select sum(currentBonus) from SalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from SalesBonus where statWeek = ?1")
	List<SalesBonus> findByStatWeek(Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from SalesBonus sb where sb.statWeek = ?1")
	void deleteByStatWeek(Long statWeek);
}
