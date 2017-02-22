package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.ExternalQuotation;

public interface ExternalQuotationDao extends GenericDao<ExternalQuotation, Long> {
	/**
	 * 获取列表页
	 * @return
	 */
	Page<ExternalQuotation> getUserPage(ExternalQuotation externalQuotation, Pageable pageable);

}
