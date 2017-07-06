package com.wondertek.cpm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.DeptInfo;

/**
 * Spring Data JPA repository for the DeptInfo entity.
 */
public interface DeptInfoRepository extends JpaRepository<DeptInfo,Long> {

	@Query("from DeptInfo di where di.status = ?1 order by id asc")
	List<DeptInfo> findAllByStatus(int status);
	
	@Query("select id from DeptInfo di where di.status = ?1 and name like ?2 order by id asc")
	List<Long> findAllByStatus(int status, String name);
	
	@Query("select d.id from DeptInfo d where d.type = ?1 and status = 1")
	List<Long> findIdsByType(Long type);
	
	@Query("from DeptInfo wdi where wdi.name = ?2 and wdi.status = 1 and wdi.parentId = ?1")
	Optional<DeptInfo> findOneByParentName(Long parentId, String name);
	
	@Query("from DeptInfo where status = 1 and parentId is null")
	List<DeptInfo> findCompanyByParentId();
	@Query("from DeptInfo where status = 1 and idPath like ?1 order by id asc")
	List<DeptInfo> findByIdPath(String idPath);
	
	@Query(" from DeptInfo where status = 1 and type = ?1 order by idPath asc")
	List<DeptInfo> findDeptInfosByType(Long type);
}
