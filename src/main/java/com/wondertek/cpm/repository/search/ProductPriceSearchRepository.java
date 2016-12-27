package com.wondertek.cpm.repository.search;

import com.wondertek.cpm.domain.ProductPrice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProductPrice entity.
 */
public interface ProductPriceSearchRepository extends ElasticsearchRepository<ProductPrice, Long> {
}
