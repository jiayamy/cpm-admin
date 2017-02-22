package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.vo.BonusRateVo;

public interface BonusRateDao extends GenericDao<BonusRate, Long> {
	/**
	 * 获取列表页
	 * @return
	 */
	Page<BonusRateVo> getUserPage(BonusRate bonusRate, Pageable pageable);

}
