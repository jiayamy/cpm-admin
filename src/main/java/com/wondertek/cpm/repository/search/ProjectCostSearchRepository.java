package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProjectCost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectCost entity.
 */
public interface ProjectCostSearchRepository extends ElasticsearchRepository<ProjectCost, Long> {
}
