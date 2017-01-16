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
	
	@Query(" from ContractCost where id in (select max(id) from ContractCost where status = 1 and contractId = ?1 and costDay >= ?2 and costDay <= ?3 group by contractId)")
	ContractCost findMaxByContractIdAndCostDay(Long contractId, Long beginTime, Long endTime);
	
	@Query(" from ContractCost where contractId = ?1 and type = ?2 and status = 1")
	List<ContractCost> findByContractIdAndType(Long contractId, Integer type);
}
