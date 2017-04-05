package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.OutsourcingUser;

public interface OutsourcingUserRepository extends JpaRepository<OutsourcingUser,Long>{

	@Query(" from OutsourcingUser where contractId = ?1")
	List<OutsourcingUser> findByContractId(Long contractId);

	@Query(" from OutsourcingUser where contractId = ?1 and rank = ?2")
	OutsourcingUser findByParams(Long contractId, String rank);
	
}
