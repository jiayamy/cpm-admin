package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.PurchaseItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PurchaseItem entity.
 */
public interface PurchaseItemSearchRepository extends ElasticsearchRepository<PurchaseItem, Long> {
}
