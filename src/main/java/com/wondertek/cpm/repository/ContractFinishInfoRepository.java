package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractFinshInfo;

/**
 * Spring Data JPA repository for the ContractFinshInfo entity.
 */
public interface ContractFinishInfoRepository extends JpaRepository<ContractFinshInfo,Long> {

	@Query("from ContractFinshInfo where contractId = ?1 and createTime < ?2 order by createTime asc")
	List<ContractFinshInfo> findAllByContractIdAndCreateTimeBefore(Long contractId, ZonedDateTime createTime);
}