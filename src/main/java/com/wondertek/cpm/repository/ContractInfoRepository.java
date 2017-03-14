package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractInfo;

import org.springframework.data.jpa.repository.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the ContractInfo entity.
 */
public interface ContractInfoRepository extends JpaRepository<ContractInfo,Long> {
	
	@Query(" from ContractInfo where status = ?1 or (updateTime >= ?2 and updateTime <= ?3)")
	List<ContractInfo> findByStatusOrUpdateTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ContractInfo where startDay <= ?3 and ( status = ?1 or (updateTime >= ?2 and updateTime <= ?3))")
	List<ContractInfo> findByStartDayAndStatusOrUpdateTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" from ContractInfo where status = ?1 or updateTime >= ?2")
	List<ContractInfo> findByStatusOrEndTime(Integer status, ZonedDateTime endTime);
}
