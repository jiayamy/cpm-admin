package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ContractUser;

/**
 * Spring Data JPA repository for the ContractUser entity.
 */
@SuppressWarnings("unused")
public interface ContractUserRepository extends JpaRepository<ContractUser,Long> {
	@Query("from ContractUser cu where cu.userId = ?1 and cu.contractId = ?2")
	List<ContractUser> findByUserId(Long userId, Long contractId);
	@Query("from ContractUser cu where cu.contractId = ?1 and cu.userId = ?2")
	List<ContractUser> getdatesByContractIdAndUserId(Long contractId, Long userId);

}
