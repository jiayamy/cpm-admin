package com.wondertek.cpm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractInfo;

/**
 * Spring Data JPA repository for the ContractInfo entity.
 */
public interface ContractInfoRepository extends JpaRepository<ContractInfo,Long> {
	
	@Query(" from ContractInfo where startDay <= ?3 and (status = ?1 or updateTime >= ?2)")
	List<ContractInfo> findByStatusOrEndTime(Integer status, ZonedDateTime beginTime, ZonedDateTime endTime);
	
	@Query(" select c from ContractInfo c ,DeptInfo d where c.deptId = d.id and d.type = ?1 and (c.status = ?2 or c.updateTime >= ?3)")
	List<ContractInfo> findByDeptTypeAndStatusOrTime(Long type,Integer status,ZonedDateTime beginTime);
	
}
