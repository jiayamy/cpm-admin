package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.StatIdentify;

public interface StatIdentifyRepository extends JpaRepository<StatIdentify,Long>{
	
	@Query(" from StatIdentify where objId = ?1 and type = ?2")
	StatIdentify findByObjIdAndType(Long objId, Integer type);
	
	@Query(" from StatIdentify where status = ?1")
	List<StatIdentify> findByStatus(Integer status);
}
