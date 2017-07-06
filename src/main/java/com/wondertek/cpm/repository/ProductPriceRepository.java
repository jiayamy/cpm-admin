package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProductPrice;

/**
 * Spring Data JPA repository for the ProductPrice entity.
 */
public interface ProductPriceRepository extends JpaRepository<ProductPrice,Long> {
	
	@Query(" from ProductPrice where name = ?1 and source = ?2 and type = ?3")
	List<ProductPrice> findListByParams(String name, Integer source,Integer type);

}
