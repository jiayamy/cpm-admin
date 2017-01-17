package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.StatIdentify;

public interface StatIdentifyRepository extends JpaRepository<StatIdentify,Long>{
	
	@Query(" from StatIdentify where objId = ?1 and type = ?2")
	StatIdentify findByObjIdAndType(Long objId, Integer type);
	
}
