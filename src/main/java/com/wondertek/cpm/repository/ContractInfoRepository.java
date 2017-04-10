package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;

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
	
	Optional<ContractInfo> findOneBySerialNum(String serialNum);
	
	@Query(" from ContractInfo")
	List<ContractInfo> findContractInfo();
	
	@Query(" from ContractInfo where serialNum= ?1")
	ContractInfo getAllBySerialNum(String serialNum);
}
