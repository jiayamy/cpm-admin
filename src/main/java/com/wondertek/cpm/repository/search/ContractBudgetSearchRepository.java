package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractBudget;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractBudget entity.
 */
public interface ContractBudgetSearchRepository extends ElasticsearchRepository<ContractBudget, Long> {
}
