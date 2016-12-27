package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProjectWeeklyStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProjectWeeklyStat entity.
 */
public interface ProjectWeeklyStatSearchRepository extends ElasticsearchRepository<ProjectWeeklyStat, Long> {
}
