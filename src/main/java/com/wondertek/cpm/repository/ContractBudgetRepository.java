package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.vo.ContractBudgetVo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractBudget entity.
 */
@SuppressWarnings("unused")
public interface ContractBudgetRepository extends JpaRepository<ContractBudget,Long> {

@Query(value = "select * from w_contract_budget cb where cb.id = ?1",nativeQuery = true)
public	ContractBudget findOneById(Long id);

}
