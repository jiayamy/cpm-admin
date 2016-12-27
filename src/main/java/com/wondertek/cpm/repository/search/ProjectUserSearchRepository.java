package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProjectUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectUser entity.
 */
public interface ProjectUserSearchRepository extends ElasticsearchRepository<ProjectUser, Long> {
}
