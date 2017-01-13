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
	
	@Query("select d.id from DeptInfo d where d.type = ?1 and status = 1")
	List<Long> findIdsByType(Long type);
	
	@Query("select d.type from DeptInfo d, User u where u.deptId=d.id and u.id=?1 and d.status=1")
	Integer findTypeByUid(Long uid);
	
	@Query("select d.type from DeptInfo d , ContractInfo c where c.deptId=d.id and c.id=?1 and d.status=1 ")
	Integer findTypeByContractId(Long cid);
	
}
