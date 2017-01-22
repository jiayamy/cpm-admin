package com.wondertek.cpm.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.wondertek.cpm.domain.ContractFinishInfo;

/**
 * Spring Data ElasticSearch repository for the ContractWeeklyStat entity.
 */
public interface ContractFinishInfoSearchRepository extends ElasticsearchRepository<ContractFinishInfo, Long> {
}
