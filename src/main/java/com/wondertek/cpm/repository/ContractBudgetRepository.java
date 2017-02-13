package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractBudget;

/**
 * Spring Data JPA repository for the ContractBudget entity.
 */
public interface ContractBudgetRepository extends JpaRepository<ContractBudget, Long> {

	@Query(value = "select * from w_contract_budget cb where cb.id = ?1", nativeQuery = true)
	public ContractBudget findOneById(Long id);
	
	@Query(" from ContractBudget cb, ProductPrice pp, DeptInfo di where cb.productPriceId=pp.id and cb.deptId=di.id and cb.contractId = ?1 and pp.type=?2 and pp.source=?3 and di.type=?4 and cb.status=1")
	List<ContractBudget> findByContractIdAndTypeAndSourceAndDeptType(Long contractId,Integer type, Integer source, Long deptType);
	
}
