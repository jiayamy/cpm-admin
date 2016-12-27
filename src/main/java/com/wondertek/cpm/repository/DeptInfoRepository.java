package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.DeptInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DeptInfo entity.
 */
@SuppressWarnings("unused")
public interface DeptInfoRepository extends JpaRepository<DeptInfo,Long> {

}
