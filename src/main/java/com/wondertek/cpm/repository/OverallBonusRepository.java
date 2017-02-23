package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondertek.cpm.domain.Bonus;

public interface OverallBonusRepository extends JpaRepository<Bonus,Long>{
	
}
