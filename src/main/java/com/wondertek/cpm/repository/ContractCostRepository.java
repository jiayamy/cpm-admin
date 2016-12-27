package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractCost;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractCost entity.
 */
@SuppressWarnings("unused")
public interface ContractCostRepository extends JpaRepository<ContractCost,Long> {

}
