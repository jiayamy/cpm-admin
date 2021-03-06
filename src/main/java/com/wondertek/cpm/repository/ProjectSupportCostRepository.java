package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectSupportCost;

public interface ProjectSupportCostRepository extends JpaRepository<ProjectSupportCost,Long>{
	
	@Query("select sum(grossProfit) from ProjectSupportCost where contractId = ?1 and statWeek = ?2")
	Double findSumGrossProfitByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProjectSupportCost where contractId = ?1 and statWeek = ?2")
	List<ProjectSupportCost> findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query("select sum(grossProfit) from ProjectSupportCost where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	Double findSumGrossProfitByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from ProjectSupportCost psc where psc.contractId = ?1 and psc.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
