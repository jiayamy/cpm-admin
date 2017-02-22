package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.SalesAnnualIndex;

public interface SalesAnnualIndexDao extends GenericDao<SalesAnnualIndex, Long> {
	/**
	 * 获取列表页
	 * @return
	 */
	Page<SalesAnnualIndex> getUserPage(SalesAnnualIndex salesAnnualIndex, Pageable pageable);

}
