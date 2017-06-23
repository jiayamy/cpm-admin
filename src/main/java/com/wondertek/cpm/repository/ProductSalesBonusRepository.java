package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProductSalesBonus;

public interface ProductSalesBonusRepository extends JpaRepository<ProductSalesBonus,Long>{
	
	@Query("select sum(bonusBasis) from ProductSalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumBonusBasisByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query("select sum(currentBonus) from ProductSalesBonus where contractId = ?1 and statWeek = ?2")
	Double findSumCurrentBonusByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProductSalesBonus where contractId = ?1 and statWeek = ?2")
	List<ProductSalesBonus> findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProductSalesBonus where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	List<ProductSalesBonus> findByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
	
	@Query("select sum(bonusBasis) from ProductSalesBonus where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	Double findSumBonusBasisByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
	
	@Query("select sum(currentBonus) from ProductSalesBonus where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	Double findSumCurrentBonusByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
	
	@Query(" from ProductSalesBonus where statWeek = ?1")
	List<ProductSalesBonus> findByStatWeek(Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProductSalesBonus psb where psb.contractId = ?1 and psb.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
