package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProductSalesBonus;

public interface ProductSalesBonusRepository extends JpaRepository<ProductSalesBonus,Long>{
	
	@Query(" from ProductSalesBonus where contractId = ?1 and statWeek = ?2")
	List<ProductSalesBonus> findByContractIdAndStatWeek(Long contractId, long statWeek);
}
