package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.HolidayInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the HolidayInfo entity.
 */
@SuppressWarnings("unused")
public interface HolidayInfoRepository extends JpaRepository<HolidayInfo,Long> {

	@Query("select count(h.id) from HolidayInfo h where h.currDay=?1")
	public int findByCurrDay(Long date);
}
