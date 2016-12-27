package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractReceive;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractReceive entity.
 */
public interface ContractReceiveSearchRepository extends ElasticsearchRepository<ContractReceive, Long> {
}
