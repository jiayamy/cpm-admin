package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.UserCost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the UserCost entity.
 */
public interface UserCostSearchRepository extends ElasticsearchRepository<UserCost, Long> {
}
