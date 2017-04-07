package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.SaleWeeklyStat;

/**
 * Spring Data JPA repository for the SaleWeeklyStat entity.
 */
public interface SaleWeeklyStatRepository extends JpaRepository<SaleWeeklyStat,Long> {

	@Query(" from SaleWeeklyStat where statWeek = ?1")
	List<SaleWeeklyStat> findByStatWeek(Long statWeek);
}
