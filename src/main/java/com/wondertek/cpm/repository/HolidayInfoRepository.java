package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.HolidayInfo;

/**
 * Spring Data JPA repository for the HolidayInfo entity.
 */
@SuppressWarnings("unused")
public interface HolidayInfoRepository extends JpaRepository<HolidayInfo,Long> {

	@Query("from HolidayInfo where currDay in ?1 and type > 1")
	public List<HolidayInfo> findHolidayByCurrDay(List<Long> currDays);

}
