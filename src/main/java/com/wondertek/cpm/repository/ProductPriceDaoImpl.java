package com.wondertek.cpm.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.ProductPrice;
@Repository("productPriceDao")
public class ProductPriceDaoImpl extends GenericDaoImpl<ProductPrice, Long> implements ProductPriceDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ProductPrice> getDomainClass() {
		return ProductPrice.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
}
