package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractInfo entity.
 */
public interface ContractInfoSearchRepository extends ElasticsearchRepository<ContractInfo, Long> {
}
