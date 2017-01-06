package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.DeptInfo;

/**
 * Spring Data JPA repository for the DeptInfo entity.
 */
@SuppressWarnings("unused")
public interface DeptInfoRepository extends JpaRepository<DeptInfo,Long> {

	@Query("from DeptInfo di where di.status = ?1 order by id asc")
	List<DeptInfo> findAllByStatus(int status);

}
