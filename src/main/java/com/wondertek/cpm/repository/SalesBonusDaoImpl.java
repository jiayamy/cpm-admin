package com.wondertek.cpm.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.SalesBonus;

@Repository("salesBonusDao")
public class SalesBonusDaoImpl extends GenericDaoImpl<SalesBonus, Long> implements SalesBonusDao  {
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<SalesBonus> getDomainClass() {
		return SalesBonus.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
}
