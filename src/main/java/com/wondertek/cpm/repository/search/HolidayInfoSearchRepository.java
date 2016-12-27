package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.HolidayInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the HolidayInfo entity.
 */
public interface HolidayInfoSearchRepository extends ElasticsearchRepository<HolidayInfo, Long> {
}
