package com.wondertek.cpm.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.HolidayInfo;

public interface HolidayInfoDao extends GenericDao<HolidayInfo, Long> {

	/**
	 * 获取满足搜索条件的节日列表
	 * @return
	 */
	public Page<HolidayInfo> getHolidayInfoPage(Map<String,Long> condition,Pageable pageable);
}
