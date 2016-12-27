package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.PurchaseItem;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PurchaseItem entity.
 */
@SuppressWarnings("unused")
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem,Long> {

}
