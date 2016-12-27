package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ContractUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ContractUser entity.
 */
public interface ContractUserSearchRepository extends ElasticsearchRepository<ContractUser, Long> {
}
