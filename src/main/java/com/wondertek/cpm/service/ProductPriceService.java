package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.repository.ProductPriceRepository;
import com.wondertek.cpm.repository.search.ProductPriceSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProductPrice.
 */
@Service
@Transactional
public class ProductPriceService {

    private final Logger log = LoggerFactory.getLogger(ProductPriceService.class);
    
    @Inject
    private ProductPriceRepository productPriceRepository;

    @Inject
    private ProductPriceSearchRepository productPriceSearchRepository;

    /**
     * Save a productPrice.
     *
     * @param productPrice the entity to save
     * @return the persisted entity
     */
    public ProductPrice save(ProductPrice productPrice) {
        log.debug("Request to save ProductPrice : {}", productPrice);
        ProductPrice result = productPriceRepository.save(productPrice);
        productPriceSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the productPrices.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProductPrice> findAll(Pageable pageable) {
        log.debug("Request to get all ProductPrices");
        Page<ProductPrice> result = productPriceRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one productPrice by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProductPrice findOne(Long id) {
        log.debug("Request to get ProductPrice : {}", id);
        ProductPrice productPrice = productPriceRepository.findOne(id);
        return productPrice;
    }

    /**
     *  Delete the  productPrice by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductPrice : {}", id);
        productPriceRepository.delete(id);
        productPriceSearchRepository.delete(id);
    }

    /**
     * Search for the productPrice corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProductPrice> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductPrices for query {}", query);
        Page<ProductPrice> result = productPriceSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
