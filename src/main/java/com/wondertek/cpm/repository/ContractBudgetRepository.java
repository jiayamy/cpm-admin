package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractBudget;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractBudget entity.
 */
@SuppressWarnings("unused")
public interface ContractBudgetRepository extends JpaRepository<ContractBudget,Long> {

}
