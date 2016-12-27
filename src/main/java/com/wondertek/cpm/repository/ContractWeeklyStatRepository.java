package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractWeeklyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractWeeklyStat entity.
 */
@SuppressWarnings("unused")
public interface ContractWeeklyStatRepository extends JpaRepository<ContractWeeklyStat,Long> {

}
