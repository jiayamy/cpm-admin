package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.WorkArea;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the WorkArea entity.
 */
@SuppressWarnings("unused")
public interface WorkAreaRepository extends JpaRepository<WorkArea,Long> {

}
