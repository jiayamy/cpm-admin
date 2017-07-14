package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractReceive;

/**
 * Spring Data JPA repository for the ContractReceive entity.
 */
public interface ContractReceiveRepository extends JpaRepository<ContractReceive,Long> {
	
	@Query(" from ContractReceive where contractId = ?1 and receiveDay <= ?2 and status = 1")
	List<ContractReceive> findAllByContractIdAndReceiveDayBefore(Long contractId, Long receiveDay);
	
	@Query(" from ContractReceive where contractId = ?1 and receiveDay >= ?2 and receiveDay <= ?3 and status = 1")
	List<ContractReceive> findAllByContractIdAndReceiveDayBetween(Long contractId, Long beginTime,Long endTime);
}
