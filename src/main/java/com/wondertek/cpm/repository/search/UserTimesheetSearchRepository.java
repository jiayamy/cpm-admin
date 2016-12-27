package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.UserTimesheet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the UserTimesheet entity.
 */
public interface UserTimesheetSearchRepository extends ElasticsearchRepository<UserTimesheet, Long> {
}
