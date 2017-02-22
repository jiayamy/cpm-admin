package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ShareCostRate;

public interface ShareCostRateDao extends GenericDao<ShareCostRate, Long> {
	/**
	 * 获取列表页
	 * @return
	 */
	Page<ShareCostRate> getUserPage(ShareCostRate shareCostRate, Pageable pageable);

}
