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
	
	@Query(" from UserTimesheet where userId = ?2 and id = ?1")
	public UserTimesheet findOneByIdAndUserId(Long id, Long userId);
	
	@Query(" from UserTimesheet where workDay >= ?1 and workDay <= ?2 and status = 1")
	public List<UserTimesheet> findByWorkday(Long beginDay, Long endDay);
	
	@Query(" from UserTimesheet where workDay <= ?1 and type = ?2 and status = 1 ")
	public List<UserTimesheet> findByDateAndType(Long endDay, Integer type);
	
	@Query(" from UserTimesheet where workDay <= ?1 and objId = ?2 and type = ?3 and status = 1 ")
	public List<UserTimesheet> findByDateAndObjIdAndType(Long endDay, Long objId, Integer type);
	
	@Query("select t from UserTimesheet t , User u , DeptInfo d where t.userId = u.id and u.deptId = d.id and d.type = ?2 and t.workDay <= ?1 and t.objId = ?3 and t.type = ?4 and t.status = 1")
	public List<UserTimesheet> findByDateAndDeptTypeAndObjIdType(Long endDay, Long deptType, Long objId, Integer type);
	
	@Query("select t from UserTimesheet t , User u , DeptInfo d where t.userId = u.id and u.deptId = d.id and d.type = ?2 and t.workDay = ?1 and t.objId = ?3 and t.type = ?4 and t.status = 1")
	public List<UserTimesheet> findByWorkDayAndDeptTypeAndObjIdAndType(Long workDay, Long deptType, Long objId, Integer type);
	
	@Query("select t from UserTimesheet t , User u , DeptInfo d where t.userId = u.id and u.deptId = d.id and d.type = ?2 and t.workDay = ?1 and t.type = ?3 and t.status = 1")
	public List<UserTimesheet> findByWorkDayAndDeptTypeAndType(Long workDay, Long deptType, Integer type);
	
	@Query("select t from UserTimesheet t , User u , DeptInfo d where t.userId = u.id and u.deptId = d.id and d.type != ?2 and t.workDay = ?1 and t.objId = ?3 and t.type = ?4 and t.status = 1")
	public List<UserTimesheet> findByWorkDayAndNotDeptTypeAndObjIdAndType(Long workDay, Long deptType, Long objId, Integer type);
	
	@Query("select t from UserTimesheet t , User u , DeptInfo d where t.userId = u.id and u.deptId = d.id and d.type != ?2 and t.workDay = ?1 and t.type = ?3 and t.status = 1")
	public List<UserTimesheet> findByWorkDayAndNotDeptTypeAndType(Long workDay, Long deptType, Integer type);
	
	@Query(" from UserTimesheet where workDay = ?1 and objId = ?2 and type = ?3 and status = 1")
	public List<UserTimesheet> findByWorkDayAndObjIdAndType(Long workDay, Long objId, Integer type);
	
	@Query(" from UserTimesheet where workDay = ?1 and type = ?2 and status = 1")
	public List<UserTimesheet> findByWorkDayAndType(Long workDay, Integer type);
	
	@Query(" from UserTimesheet where userId = ?1 and type = ?2 and objId = ?3 and workDay <= ?4 and status = 1")
	public List<UserTimesheet> findByUserIdAndTypeAndObjIdAndWorkDay(Long userId, Integer type, Long objId, Long workDay);
}
