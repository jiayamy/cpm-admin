package com.wondertek.cpm.repository;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.wondertek.cpm.domain.ContractReceive;
@Repository("contractReceiveDao")
public class ContractReceiveDaoImpl extends GenericDaoImpl<ContractReceive, Long> implements ContractReceiveDao{
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Class<ContractReceive> getDomainClass() {
		return ContractReceive.class;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
