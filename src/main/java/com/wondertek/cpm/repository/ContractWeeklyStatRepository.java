package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractWeeklyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractWeeklyStat entity.
 */
public interface ContractWeeklyStatRepository extends JpaRepository<ContractWeeklyStat,Long> {
	
	@Query(" from ContractWeeklyStat where statWeek = ?1 and contractId = ?2")
	List<ContractWeeklyStat> findByStatWeekAndContractId(Long statWeek, Long contractId);
	
	@Query(" from ContractWeeklyStat where id in (select max(id) from ContractWeeklyStat where statWeek < ?1 and contractId = ?2) ")
	ContractWeeklyStat findMaxByStatWeekBeforeAndContractId(Long statWeek, Long contractId);
}
