package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.DeptType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DeptType entity.
 */
public interface DeptTypeRepository extends JpaRepository<DeptType,Long> {
	
	@Query(" from DeptType wdt, DeptInfo wdi, ContractBudget wcb, PurchaseItem wpi where wcb.deptId = wdi.id and wdi.type = wdt.id and wcb.id=wpi.budgetId and wcb.contractId = ?1 and wpi.type=?2 and wpi.source=?3 group by wdt.id ")
	List<DeptType> findByContractIdAndContractBudgetAndPurchaseItem(Long contractId,Integer purchaseItemType, Integer purchaseItemSource);
}
