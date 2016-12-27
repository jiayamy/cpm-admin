package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProductPrice;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProductPrice entity.
 */
@SuppressWarnings("unused")
public interface ProductPriceRepository extends JpaRepository<ProductPrice,Long> {

}
