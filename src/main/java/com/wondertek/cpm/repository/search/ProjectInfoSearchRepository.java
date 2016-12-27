package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProjectInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectInfo entity.
 */
public interface ProjectInfoSearchRepository extends ElasticsearchRepository<ProjectInfo, Long> {
}
