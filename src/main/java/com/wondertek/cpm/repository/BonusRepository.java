package com.wondertek.cpm.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.Bonus;

public interface BonusRepository extends JpaRepository<Bonus,Long>{
	
	@Query(" from Bonus where contractId = ?1 and statWeek = ?2")
	Bonus findByContractIdAndStatWeek(Long contractId, Long statWeek);
	
	@Query(" from Bonus where statWeek = ?1")
	List<Bonus> findByStatWeek(Long statWeek);
	
	@Modifying
	@Transactional
	@Query("delete from Bonus b where b.contractId = ?1 and b.statWeek = ?2")
	void deleteByContractIdAndStatWeek(Long contractId, Long statWeek);
}
