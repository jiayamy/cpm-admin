package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ContractInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ContractInfo entity.
 */
@SuppressWarnings("unused")
public interface ContractInfoRepository extends JpaRepository<ContractInfo,Long> {

}
