package com.wondertek.cpm.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.ContractCost;
@Repository("contractCostDao")
public class ContractCostDaoImpl extends GenericDaoImpl<ContractCost, Long> {
	@Autowired
	private EntityManager entityManager;

	@Override
	public Class<ContractCost> getDomainClass() {
		return ContractCost.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
}
