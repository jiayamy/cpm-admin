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
	@Query(" select sum(s.annualIndex) from SalesAnnualIndex s,DeptInfo d,User u where s.userId = u.id and u.deptId = d.id and s.statYear = ?1 and d.id in (?2)")
	Double findByStatYearAndDeptId(Long statYear, List<Long> deptIds);
}
