package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractWeeklyStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractWeeklyStat entity.
 */
public interface ContractWeeklyStatSearchRepository extends ElasticsearchRepository<ContractWeeklyStat, Long> {
}
