package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.security.SecurityUtils;
import com.wondertek.cpm.service.PurchaseItemService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing PurchaseItem.
 */
@RestController
@RequestMapping("/api")
public class PurchaseItemResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseItemResource.class);
        
    @Inject
    private PurchaseItemService purchaseItemService;

    /**
     * POST  /purchase-items : Create a new purchaseItem.
     *
     * @param purchaseItem the purchaseItem to create
     * @return the ResponseEntity with status 201 (Created) and with body the new purchaseItem, or with status 400 (Bad Request) if the purchaseItem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/purchase-items")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<PurchaseItem> createPurchaseItem(@RequestBody PurchaseItem purchaseItem) throws URISyntaxException {
        log.debug("REST request to save PurchaseItem : {}", purchaseItem);
        if (purchaseItem.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("purchaseItem", "idexists", "A new purchaseItem cannot already have an ID")).body(null);
        }
        PurchaseItem result = purchaseItemService.save(purchaseItem);
        return ResponseEntity.created(new URI("/api/purchase-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("purchaseItem", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /purchase-items : Updates an existing purchaseItem.
     *
     * @param purchaseItem the purchaseItem to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated purchaseItem,
     * or with status 400 (Bad Request) if the purchaseItem is not valid,
     * or with status 500 (Internal Server Error) if the purchaseItem couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/purchase-items")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<PurchaseItem> updatePurchaseItem(@RequestBody PurchaseItem purchaseItem) throws URISyntaxException {
        log.debug("REST request to update PurchaseItem : {}", purchaseItem);
        Boolean isNew = purchaseItem.getId() == null;
        if (purchaseItem.getBudgetId() == null || purchaseItem.getContractId() == null
        		|| StringUtil.isNullStr(purchaseItem.getName()) || purchaseItem.getSource() == null
        		|| purchaseItem.getType() == null || purchaseItem.getPrice() == null) {
			
		}
        PurchaseItem oldPurchaseItem = this.purchaseItemService.findOneById(purchaseItem.getId());
        
        if (purchaseItem.getId() == null) {
        	purchaseItem.setCreateTime(ZonedDateTime.now());
        	purchaseItem.setCreator(SecurityUtils.getCurrentUserLogin());
        	purchaseItem.setUpdator(SecurityUtils.getCurrentUserLogin());
        	purchaseItem.setUpdateTime(ZonedDateTime.now());
        	purchaseItem.setStatus(1);
            PurchaseItem result = purchaseItemService.save(purchaseItem);
		}else {
			oldPurchaseItem.setUpdator(SecurityUtils.getCurrentUserLogin());
            oldPurchaseItem.setUpdateTime(ZonedDateTime.now());
            oldPurchaseItem.setName(purchaseItem.getName());
            oldPurchaseItem.setPrice(purchaseItem.getPrice());
            oldPurchaseItem.setPurchaser(purchaseItem.getPurchaser());
            oldPurchaseItem.setQuantity(purchaseItem.getQuantity());
            oldPurchaseItem.setSource(purchaseItem.getSource());
            oldPurchaseItem.setStatus(1);
            oldPurchaseItem.setType(purchaseItem.getType());
            oldPurchaseItem.setTotalAmount(purchaseItem.getTotalAmount());
            oldPurchaseItem.setUnits(purchaseItem.getUnits());
            PurchaseItem result = purchaseItemService.save(oldPurchaseItem);
		}
        
        
        return new ResponseEntity<>(purchaseItem,HttpStatus.OK);
    }

    /**
     * GET  /purchase-items : get all the purchaseItems.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of purchaseItems in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
//    @GetMapping("/purchase-items")
//    @Timed
//    public ResponseEntity<List<PurchaseItem>> getAllPurchaseItems(@ApiParam Pageable pageable)
//        throws URISyntaxException {
//        log.debug("REST request to get a page of PurchaseItems");
//        Page<PurchaseItem> page = purchaseItemService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/purchase-items");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

    /**
     * GET  /purchase-items/:id : get the "id" purchaseItem.
     *
     * @param id the id of the purchaseItem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the purchaseItem, or with status 404 (Not Found)
     */
    @GetMapping("/purchase-items/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<PurchaseItemVo> getPurchaseItem(@PathVariable Long id) {
        log.debug("REST request to get PurchaseItem : {}", id);
        PurchaseItemVo purchaseItem = purchaseItemService.findOne(id);
        return Optional.ofNullable(purchaseItem)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /purchase-items/:id : delete the "id" purchaseItem.
     *
     * @param id the id of the purchaseItem to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/purchase-items/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<Void> deletePurchaseItem(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseItem : {}", id);
        purchaseItemService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("purchaseItem", id.toString())).build();
    }

    /**
     * SEARCH  /_search/purchase-items?query=:query : search for the purchaseItem corresponding
     * to the query.
     *
     * @param query the query of the purchaseItem search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/purchase-items")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<List<PurchaseItemVo>> searchPurchaseItems(@RequestParam String name, 
    		@RequestParam String contractId,
    		@RequestParam String source,
    		@RequestParam String type,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PurchaseItems");
        PurchaseItem purchaseItem = new PurchaseItem();
        if (!StringUtil.isNullStr(name)) {
			purchaseItem.setName(name);
		}
        if (!StringUtil.isNullStr(contractId)) {
			purchaseItem.setContractId(StringUtil.nullToLong(contractId));
		}
        if (!StringUtil.isNullStr(source)) {
			purchaseItem.setSource(StringUtil.nullToInteger(source));
		}
        if (!StringUtil.isNullStr(type)) {
			purchaseItem.setType(StringUtil.nullToInteger(type));
		}
        Page<PurchaseItemVo> page = purchaseItemService.getPurchasePage(purchaseItem, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/purchase-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
