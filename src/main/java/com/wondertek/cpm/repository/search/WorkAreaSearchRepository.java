package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.WorkArea;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the WorkArea entity.
 */
public interface WorkAreaSearchRepository extends ElasticsearchRepository<WorkArea, Long> {
}
