package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractCost;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractCost entity.
 */
public interface ContractCostRepository extends JpaRepository<ContractCost,Long> {
	
	@Query(" from ContractCost where deptId in (?1) and type = ?2 and contractId = ?3 and status = 1")
	List<ContractCost> findByDeptIdsAndTypeAndContractId(List<Long> deptIds, Integer type, Long contractId);
	
	@Query(" from ContractCost where deptId in (?1) and type != ?2 and contractId = ?3 and status = 1")
	List<ContractCost> findByDeptIdsAndNoTypeAndContractId(List<Long> deptIds, Integer type, Long contractId);
	
}
