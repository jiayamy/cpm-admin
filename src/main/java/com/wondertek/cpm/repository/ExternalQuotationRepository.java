package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ExternalQuotation;

public interface ExternalQuotationRepository extends JpaRepository<ExternalQuotation,Long>{
	
	@Query(" from ExternalQuotation where grade = ?1")
	ExternalQuotation findByGrade(Integer grade);
	@Query("select grade from ExternalQuotation order by grade asc")
	List<Integer> findGradeOrderByGrade();
}
