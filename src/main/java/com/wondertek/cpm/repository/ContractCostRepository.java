package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractCost;

/**
 * Spring Data JPA repository for the ContractCost entity.
 */
public interface ContractCostRepository extends JpaRepository<ContractCost,Long> {
	
	@Query(" from ContractCost where deptId in (?1) and type = ?2 and contractId = ?3 and status = 1")
	List<ContractCost> findByDeptIdsAndTypeAndContractId(List<Long> deptIds, Integer type, Long contractId);
	
	@Query(" from ContractCost where deptId in (?1) and type != ?2 and contractId = ?3 and status = 1")
	List<ContractCost> findByDeptIdsAndNoTypeAndContractId(List<Long> deptIds, Integer type, Long contractId);
	
	@Query(" from ContractCost where id in (select max(id) from ContractCost where status = 1 and contractId = ?1 and costDay >= ?2 and costDay <= ?3 and type=?4 group by contractId)")
	ContractCost findMaxByContractIdAndCostDayAndType(Long contractId, Long beginTime, Long endTime, Integer type);
	
	@Query(" from ContractCost where contractId = ?1 and type = ?2 and status = 1")
	List<ContractCost> findByContractIdAndType(Long contractId, Integer type);
	
	@Query(" from ContractCost where deptId = ?1 and type = ?2 and contractId = ?3 and costDay = ?4 and status = 1")
	ContractCost findByDeptIdAndTypeAndContractIdAndCostDay(Long deptId, Integer type, Long contractId, Long costDay);
	
	@Query(" from ContractCost where deptId = ?1 and type = ?2 and contractId = ?3 and costDay >= ?4 and costDay <= ?5 and status = 1")
	List<ContractCost> findByDeptIdAndTypeAndContractIdAndCostDayBetween(Long deptId,Integer type, Long contractId, Long beginTime, Long endTime);
	
	@Query(" from ContractCost where deptId = ?1 and type != ?2 and contractId = ?3 and costDay >= ?4 and costDay <= ?5 and status = 1")
	List<ContractCost> findByDeptIdAndNoTypeAndContractIdAndCostDayBetween(Long deptId,Integer type, Long contractId, Long beginTime, Long endTime);
}
