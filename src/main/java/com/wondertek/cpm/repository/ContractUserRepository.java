package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractUser;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractUser entity.
 */
@SuppressWarnings("unused")
public interface ContractUserRepository extends JpaRepository<ContractUser,Long> {

}
