package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.UserTimesheet;

/**
 * Spring Data JPA repository for the UserTimesheet entity.
 */
public interface UserTimesheetRepository extends JpaRepository<UserTimesheet,Long> {

	@Query("select count(ut.id) from UserTimesheet ut where ut.status = 1 and ut.userId = ?1 and ut.workDay >= ?2 and ut.workDay <= ?3")
	public Long findByUserId(Long userId, Long startDay, Long endDay);
	
	@Query("select count(ut.id) from UserTimesheet ut where ut.status = 1 and ut.userId = ?1 and ut.workDay >= ?2 and ut.workDay <= ?3 and ut.id not in ?4")
	public Long findByUserIdAndId(Long userId, Long startDay, Long endDay, List<Long> ids);
}
