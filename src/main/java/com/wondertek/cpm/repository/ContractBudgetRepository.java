package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondertek.cpm.domain.ContractBudget;

/**
 * Spring Data JPA repository for the ContractBudget entity.
 */
public interface ContractBudgetRepository extends JpaRepository<ContractBudget, Long> {

}
