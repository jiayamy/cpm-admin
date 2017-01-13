package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractMonthlyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractMonthlyStat entity.
 */
@SuppressWarnings("unused")
public interface ContractMonthlyStatRepository extends JpaRepository<ContractMonthlyStat,Long> {

}
