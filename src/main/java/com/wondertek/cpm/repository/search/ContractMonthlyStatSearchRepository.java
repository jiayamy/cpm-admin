package com.wondertek.cpm.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.wondertek.cpm.domain.ContractMonthlyStat;

/**
 * Spring Data ElasticSearch repository for the ProjectWeeklyStat entity.
 */
public interface ContractMonthlyStatSearchRepository extends ElasticsearchRepository<ContractMonthlyStat, Long> {
	
	
}
