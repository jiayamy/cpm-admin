package com.wondertek.cpm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProductSalesBonus;

public interface ProductSalesBonusRepository extends JpaRepository<ProductSalesBonus,Long>{
	
	@Query("select sum(currentBonus) from ProductSalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProductSalesBonus psb where psb.contractId = ?1 and psb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
