package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Query(" from UserTimesheet where workDay <= ?1 and objId = ?2 and type = ?3 and status = 1 ")
	public List<UserTimesheet> findByDateAndObjIdAndType(Long endDay, Long objId, Integer type);
	
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
	
	@Query(" from UserTimesheet where userId = ?1 and type = ?2 and objId = ?3 and workDay <= ?4 and status = 1")
	public List<UserTimesheet> findByUserIdAndTypeAndObjIdAndWorkDay(Long userId, Integer type, Long objId, Long workDay);
	
	@Query(" from UserTimesheet where userId = ?1 and type = ?2 and objId = ?3 and workDay >= ?4 and workDay <= ?5 and status = 1")
	public List<UserTimesheet> findByUserIdAndTypeAndObjIdAndTime(Long userId, Integer type, Long objId, Long startDay, Long endDay);
	
	@Query(" from UserTimesheet where type = ?1 and objId = ?2 and workDay <= ?3 and status = 1 group by userId")
	public List<UserTimesheet> findByTypeAndObjIdAndEndDay(Integer type, Long objId, Long endDay);
	
	@Query(" from UserTimesheet where objId = ?1 and type = ?2 and workDay >= ?3 and workDay <= ?4 and status = 1 ")
	public List<UserTimesheet> findByObjIdAndTypeAndWordDayBetween(Long objId, Integer type, Long startDay, Long endDay);
	
	@Query("select sum(acceptInput),sum(acceptExtraInput) from UserTimesheet where workDay <= ?1 and objId = ?2 and type = ?3 and status = 1 ")
	public List<Object[]> findSumByDateAndObjIdAndType(Long endDay, Long objId, Integer type);
	
	@Query(value = "select ju.last_name,ju.serial_num, sum(wut.accept_input)+sum(wut.accept_extra_input) as totalInput from w_user_timesheet wut ,jhi_user ju  where wut.user_id = ju.id and wut.obj_id = ?1 and wut.type_ = 3 group by wut.user_id",nativeQuery = true)
	public List<Object[]> findProjectInfoUserByObjIdAndType(Long objId);
	
	@Modifying
	@Transactional
	@Query(" update UserTimesheet set character = 1 where id in ?1")
	public void updateCharacterById(List<Long> ids);
	
	@Query("select count(id) from UserTimesheet where id = ?1 and (realInput != ?2 or acceptInput != ?3 or extraInput != ?4 or acceptExtraInput != ?5) and character = 1")
	public int getCountByIdAndInputs(Long id,Double realInput,Double acceptInput,Double extraInput,Double acceptExtraInput);
	
	@Query(" from UserTimesheet ut where id = ?1 and (realInput != ?2 or acceptInput != ?3 or extraInput != ?4 or acceptExtraInput != ?5) and character = 1")
	public UserTimesheet getUserTimesheetByIdAndInputs(Long id,Double realInput,Double acceptInput,Double extraInput,Double acceptExtraInput);

	@Query("select sum(acceptInput),sum(acceptExtraInput) from UserTimesheet where workDay >= ?1 and workDay <= ?2 and objId = ?3 and type = ?4 and status = 1 ")
	public List<Object[]> findSumByDateAndObjIdAndType(Long startDay, Long endDay, Long objId, Integer type);
}
