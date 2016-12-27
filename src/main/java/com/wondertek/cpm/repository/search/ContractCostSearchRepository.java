package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractCost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractCost entity.
 */
public interface ContractCostSearchRepository extends ElasticsearchRepository<ContractCost, Long> {
}
