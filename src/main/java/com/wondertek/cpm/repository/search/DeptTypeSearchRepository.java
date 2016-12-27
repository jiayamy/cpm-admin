package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.DeptType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the DeptType entity.
 */
public interface DeptTypeSearchRepository extends ElasticsearchRepository<DeptType, Long> {
}
