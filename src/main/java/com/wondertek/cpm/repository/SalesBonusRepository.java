package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondertek.cpm.domain.SalesBonus;

/**
 * Spring Data JPA repository for the SalesBonus entity.
 */
public interface SalesBonusRepository extends JpaRepository<SalesBonus,Long> {
	
}
