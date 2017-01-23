package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProductPrice;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProductPrice entity.
 */
@SuppressWarnings("unused")
public interface ProductPriceRepository extends JpaRepository<ProductPrice,Long> {
	
	@Query(" from ProductPrice where name = ?1 and source = ?2 and type = ?3")
	List<ProductPrice> findListByParams(String name, Integer source,
			Integer type);

}
