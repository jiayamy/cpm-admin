package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.PurchaseItem;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PurchaseItem entity.
 */
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem,Long> {
	
	@Query(" from PurchaseItem where contractId = ?1 and type = ?2 and status = 1 ")
	List<PurchaseItem> findByContractIdAndType(Long contractId, Integer type);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and type = ?3 and status = 1 ")
	List<PurchaseItem> findByContractIdAndSourceAndType(Long contractId, Integer source, Integer type);
	
	@Query(" from PurchaseItem where name = ?1 and source = ?2 and type = ?3 ")
	List<PurchaseItem> findByNameAndSourceAndPurchaseType(String name,
			Integer source, Integer type);
}
