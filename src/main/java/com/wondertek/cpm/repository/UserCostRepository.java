package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.UserCost;

/**
 * Spring Data JPA repository for the UserCost entity.
 */
public interface UserCostRepository extends JpaRepository<UserCost,Long> {
	
	@Query(" from UserCost where id in (select max(id) from UserCost where status = 1 and costMonth <= ?1 and userId = ?2 group by userId )")
	public UserCost findMaxByCostMonthAndUserId(Long costMonth, Long userId);
	
	@Query(" from UserCost where userId = ?1 and costMonth = ?2")
	public UserCost findByUserIdAndCostMonth(Long userId,Long costMonth);
}
