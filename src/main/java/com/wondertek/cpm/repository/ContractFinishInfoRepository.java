package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractFinishInfo;

/**
 * Spring Data JPA repository for the ContractFinishInfo entity.
 */
public interface ContractFinishInfoRepository extends JpaRepository<ContractFinishInfo,Long> {

	@Query("from ContractFinishInfo where contractId = ?1 and createTime < ?2 order by createTime asc")
	List<ContractFinishInfo> findAllByContractIdAndCreateTimeBefore(Long contractId, ZonedDateTime createTime);
	
	@Query(" from ContractFinishInfo where id in (select max(id) from ContractFinishInfo where contractId = ?1 and createTime <= ?2)")
	ContractFinishInfo findMaxByContractIdAndCreateTimeBefore(Long contractId, ZonedDateTime createTime);
}
