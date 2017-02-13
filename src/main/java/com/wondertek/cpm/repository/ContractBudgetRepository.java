package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractBudget;

/**
 * Spring Data JPA repository for the ContractBudget entity.
 */
public interface ContractBudgetRepository extends JpaRepository<ContractBudget, Long> {

	@Query(value = "select * from w_contract_budget cb where cb.id = ?1", nativeQuery = true)
	public ContractBudget findOneById(Long id);
	
}
