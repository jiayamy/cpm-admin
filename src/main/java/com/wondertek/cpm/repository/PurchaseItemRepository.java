package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.PurchaseItem;

/**
 * Spring Data JPA repository for the PurchaseItem entity.
 */
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem,Long> {
	
	@Query(" from PurchaseItem where contractId = ?1 and type = ?2 and status = 1 ")
	List<PurchaseItem> findByContractIdAndType(Long contractId, Integer type);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and status = 1")
	List<PurchaseItem> findByContractIdAndSource(Long contractId, Integer source);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and type = ?3 and status = 1 ")
	List<PurchaseItem> findByContractIdAndSourceAndType(Long contractId, Integer source, Integer type);
	
	@Query("select wpi from PurchaseItem wpi, ContractBudget wcb, DeptInfo wdi where wpi.budgetId = wcb.id and wcb.deptId = wdi.id and wpi.contractId = ?1 and wdi.type = ?2 and wpi.source = ?3 and wpi.type = ?4 and wpi.status = 1")
	List<PurchaseItem> findByContractIdAndDeptTypeAndSourceAndType(Long contractId, Long deptType, Integer source, Integer type);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and updateTime <= ?3 and status = 1")
	List<PurchaseItem> findByContractIdAndSourceAndUpdateBefore(Long contractId, Integer source, ZonedDateTime updateTime);
	
	@Query(" from PurchaseItem where contractId = ?1 and type = ?2 and updateTime >= ?3 and updateTime <= ?4 and status = 1 ")
	List<PurchaseItem> findByContractIdAndTypeAndUpdateBetween(Long contractId, Integer type, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and type = ?3 and updateTime >= ?4 and updateTime <= ?5 and status = 1 ")
	List<PurchaseItem> findByContractIdAndSourceAndTypeAndUpdateBetween(Long contractId, Integer source, Integer type, ZonedDateTime beginTime, ZonedDateTime endTime);
}
