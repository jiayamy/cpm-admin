package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProjectFinishInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectFinishInfo entity.
 */
public interface ProjectFinishInfoSearchRepository extends ElasticsearchRepository<ProjectFinishInfo, Long> {
}
