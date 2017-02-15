package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.Bonus;

public interface BonusRepository extends JpaRepository<Bonus,Long>{
	
	@Query(" from Bonus where contractId = ?1 and statWeek = ?2")
	Bonus findByContractIdAndStatWeek(Long contractId, Long statWeek);
}
