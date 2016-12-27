package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.DeptInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the DeptInfo entity.
 */
public interface DeptInfoSearchRepository extends ElasticsearchRepository<DeptInfo, Long> {
}
