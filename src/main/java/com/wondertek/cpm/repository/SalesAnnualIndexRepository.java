package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.SalesAnnualIndex;

/**
 * Spring Data JPA repository for the SalesAnnualIndex entity.
 */
public interface SalesAnnualIndexRepository extends JpaRepository<SalesAnnualIndex,Long> {

	@Query(" from SalesAnnualIndex where statYear = ?1")
	List<SalesAnnualIndex> findByStatYear(Long statYear);
	@Query(" from SalesAnnualIndex where statYear = ?1 and userId = ?2")
	SalesAnnualIndex findByStatYearAndUserId(Long statYear, Long userId);
	@Query(" from SalesAnnualIndex s,DeptInfo d where s.userId = d.id and s.statYear = ?1 and d.id = ?2")
	List<SalesAnnualIndex> findByStatYearAndDeptId(Long statYear, Long deptId);
	
}
