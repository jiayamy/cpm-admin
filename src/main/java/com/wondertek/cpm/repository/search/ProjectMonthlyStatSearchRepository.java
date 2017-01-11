package com.wondertek.cpm.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.wondertek.cpm.domain.ProjectMonthlyStat;

/**
 * Spring Data ElasticSearch repository for the ProjectWeeklyStat entity.
 */
public interface ProjectMonthlyStatSearchRepository extends ElasticsearchRepository<ProjectMonthlyStat, Long> {
	
	
}
