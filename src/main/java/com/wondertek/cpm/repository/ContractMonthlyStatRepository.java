package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractMonthlyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractMonthlyStat entity.
 */
public interface ContractMonthlyStatRepository extends JpaRepository<ContractMonthlyStat,Long> {
	
	@Query(" from ContractMonthlyStat where statWeek = ?1 and contractId = ?2 ")
	List<ContractMonthlyStat> findByStatWeekAndContractId(Long statWeek, Long contractId);
	
	@Query(" from ContractMonthlyStat where statWeek = ?1 and contractId = ?2) ")
	ContractMonthlyStat findOneByStatWeekAndContractId(Long statWeek, Long contractId);
}
