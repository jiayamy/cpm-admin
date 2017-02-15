package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ProjectSupportCost;

public interface ProjectSupportCostRepository extends JpaRepository<ProjectSupportCost,Long>{
	
	@Query("select sum(productCost) from ProjectSupportCost where deptType = ?1 and statWeek = ?2")
	Double findSumProductCostByDeptTypeAndStatWeek(Long deptType, Long statWeek);
	
	@Query("select sum(grossProfit) from ProjectSupportCost where contractId = ?1 and statWeek = ?2")
	Double findSumGrossProfitByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProjectSupportCost where contractId = ?1 and statWeek = ?2")
	List<ProjectSupportCost> findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from ProjectSupportCost where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	List<ProjectSupportCost> findByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
	
	@Query("select sum(grossProfit) from ProjectSupportCost where contractId = ?1 and deptType = ?2 and statWeek = ?3")
	Double findSumGrossProfitByContractIdAndDeptTypeAndStatWeek(Long contractId, Long deptType, Long statWeek);
}
