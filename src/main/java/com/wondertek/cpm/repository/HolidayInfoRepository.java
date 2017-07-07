package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.HolidayInfo;

/**
 * Spring Data JPA repository for the HolidayInfo entity.
 */
public interface HolidayInfoRepository extends JpaRepository<HolidayInfo,Long> {

	@Query("from HolidayInfo h where h.currDay=?1")
	public HolidayInfo findByCurrDay(Long date);
	
	@Query("select currDay from HolidayInfo where currDay>=?1")
	public List<Long> findCurrdaysByCurrDay(Long currDay);
	
	@Query("from HolidayInfo where currDay in ?1 and type > 1")
	public List<HolidayInfo> findHolidayByCurrDay(List<Long> currDays);

	@Query("select count(1) from HolidayInfo where currDay >= ?1 and currDay <= ?2 and type = 1")
	public Long findWorkDayByParam(Long startDay,Long endDay);
	
	@Query("from HolidayInfo where currDay >= ?1 and currDay <= ?2")
	public List<HolidayInfo> findDayByParam(Long startDay,Long endDay);
}
