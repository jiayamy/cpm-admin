package com.wondertek.cpm.service;

import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;
import com.wondertek.cpm.repository.PurchaseItemDao;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.search.PurchaseItemSearchRepository;

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
 * Service Implementation for managing PurchaseItem.
 */
@Service
@Transactional
public class PurchaseItemService {

    private final Logger log = LoggerFactory.getLogger(PurchaseItemService.class);
    
    @Inject
    private PurchaseItemRepository purchaseItemRepository;

    @Inject
    private PurchaseItemSearchRepository purchaseItemSearchRepository;
    
    @Inject
    private PurchaseItemDao purchaseItemDao;

    /**
     * Save a purchaseItem.
     *
     * @param purchaseItem the entity to save
     * @return the persisted entity
     */
    public PurchaseItem save(PurchaseItem purchaseItem) {
        log.debug("Request to save PurchaseItem : {}", purchaseItem);
        PurchaseItem result = purchaseItemRepository.save(purchaseItem);
        purchaseItemSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the purchaseItems.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PurchaseItem> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseItems");
        Page<PurchaseItem> result = purchaseItemRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one purchaseItem by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PurchaseItem findOne(Long id) {
        log.debug("Request to get PurchaseItem : {}", id);
        PurchaseItem purchaseItem = purchaseItemRepository.findOne(id);
        return purchaseItem;
    }

    /**
     *  Delete the  purchaseItem by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseItem : {}", id);
        purchaseItemRepository.delete(id);
        purchaseItemSearchRepository.delete(id);
    }

    /**
     * Search for the purchaseItem corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PurchaseItem> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PurchaseItems for query {}", query);
        Page<PurchaseItem> result = purchaseItemSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public Page<PurchaseItemVo> getPurchasePage(PurchaseItem purchaseItem,
			Pageable pageable) {
		Page<PurchaseItemVo> page = purchaseItemDao.getPurchaserPage(purchaseItem,pageable);
		return page;
	}
}
