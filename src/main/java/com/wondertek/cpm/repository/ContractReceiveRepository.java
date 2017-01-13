package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractReceive;

/**
 * Spring Data JPA repository for the ContractReceive entity.
 */
public interface ContractReceiveRepository extends JpaRepository<ContractReceive,Long> {
	
	@Query("from ContractReceive where contractId = ?1 and createTime < ?2 and status = 1 order by createTime ")
	List<ContractReceive> findAllByContractIdAndCreateTimeBefore(Long contractId, ZonedDateTime createTime);
}
