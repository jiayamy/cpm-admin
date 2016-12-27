package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.DeptType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DeptType entity.
 */
@SuppressWarnings("unused")
public interface DeptTypeRepository extends JpaRepository<DeptType,Long> {

}
