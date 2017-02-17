package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractProjectBonus;

public interface ContractProjectBonusRepository extends JpaRepository<ContractProjectBonus,Long>{
	
	@Query(" from ContractProjectBonus where statWeek = ?1")
	List<ContractProjectBonus> findByStatWeek(Long statWeek);
}
