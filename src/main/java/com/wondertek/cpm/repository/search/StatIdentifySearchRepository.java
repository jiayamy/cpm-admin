package com.wondertek.cpm.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.wondertek.cpm.domain.StatIdentify;

public interface StatIdentifySearchRepository extends ElasticsearchRepository<StatIdentify, Long>{

}
