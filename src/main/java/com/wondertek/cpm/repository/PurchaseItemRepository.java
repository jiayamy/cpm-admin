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
	
	@Query(" from PurchaseItem where contractId = ?1 and type = ?2 and updateTime <= ?3 and status = 1 ")
	List<PurchaseItem> findByContractIdAndTypeAndBeforeUpdateTime(Long contractId, Integer type, ZonedDateTime updateTime);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and type = ?3 and updateTime <= ?4 and status = 1 ")
	List<PurchaseItem> findByContractIdAndSourceAndTypeAndBeforeUpdateTime(Long contractId, Integer source, Integer type, ZonedDateTime updateTime);
	
	@Query(" from PurchaseItem where contractId = ?1 and source = ?2 and updateTime <= ?3 and status = 1")
	List<PurchaseItem> findByContractIdAndSourceAndUpdateBefore(Long contractId, Integer source, ZonedDateTime updateTime);
	
	@Query("select sum(totalAmount) from PurchaseItem where contractId = ?1 and type = ?2 and createTime >= ?3 and createTime <= ?4 and status = 1")
	Double findByContractIdAndTypeAndUpdateBetween(Long contractId, Integer type, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query("select sum(totalAmount) from PurchaseItem where contractId = ?1 and source = ?2 and type = ?3 and createTime >= ?4 and createTime <= ?5 and status = 1")
	Double findByContractIdAndSourceAndTypeAndUpdateBetween(Long contractId, Integer source, Integer type, ZonedDateTime beginTime, ZonedDateTime endTime);
}
