package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractReceive;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractReceive entity.
 */
@SuppressWarnings("unused")
public interface ContractReceiveRepository extends JpaRepository<ContractReceive,Long> {

}
