package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProductPriceVo;
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
    public ResponseEntity<Boolean> updatePurchaseItem(@RequestBody PurchaseItem purchaseItem) throws URISyntaxException {
        log.debug("REST request to update PurchaseItem : {}", purchaseItem);
        Boolean isNew = purchaseItem.getId() == null;
        if (purchaseItem.getBudgetId() == null || purchaseItem.getContractId() == null
        		|| StringUtil.isNullStr(purchaseItem.getName()) || purchaseItem.getSource() == null
        		|| purchaseItem.getType() == null || purchaseItem.getPrice() == null) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.requiedError", "")).body(null);
		}
        String updator = SecurityUtils.getCurrentUserLogin();
        ZonedDateTime updateTime = ZonedDateTime.now();
        if (!isNew) {
        	PurchaseItem oldPurchaseItem = this.purchaseItemService.findOneById(purchaseItem.getId());
			if (oldPurchaseItem == null) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.idNone", "")).body(null);
			}else if (oldPurchaseItem.getStatus() == PurchaseItem.STATUS_DELETED) {
        		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.statue2Error", "")).body(null);
			}else if (oldPurchaseItem.getProductPriceId() != null) {
				if (oldPurchaseItem.getContractId() != purchaseItem.getContractId() || !oldPurchaseItem.getName().equals(purchaseItem.getName())
						|| oldPurchaseItem.getBudgetId() != purchaseItem.getBudgetId() || oldPurchaseItem.getType() != purchaseItem.getType()
						|| oldPurchaseItem.getSource() != purchaseItem.getSource() || !oldPurchaseItem.getUnits().equals(purchaseItem.getUnits())) {
					return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.changeNameError", "")).body(null);
				}
			}
			purchaseItem.setCreateTime(oldPurchaseItem.getCreateTime());
			purchaseItem.setCreator(oldPurchaseItem.getCreator());
			purchaseItem.setStatus(oldPurchaseItem.getStatus());
		}else {
//			List<PurchaseItem> list = this.purchaseItemService.findOneByParams(purchaseItem.getName(),purchaseItem.getSource(),purchaseItem.getType(),purchaseItem.getPurchaser());
//			if (list != null && !list.isEmpty()) {
//				return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.purchaseItemHaveExit", "")).body(null);
//			}			
			purchaseItem.setCreateTime(updateTime);
			purchaseItem.setCreator(updator);
			purchaseItem.setStatus(PurchaseItem.STATUS_VALIBLE);
		}
        	purchaseItem.setUpdateTime(updateTime);
        	purchaseItem.setUpdator(updator);
        	PurchaseItem result =  purchaseItemService.save(purchaseItem);
        
        	if(isNew){
            	return ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityCreationAlert("purchaseItem", result.getId().toString()))
                        .body(isNew);
            }else{
            	return ResponseEntity.ok()
            			.headers(HeaderUtil.createEntityUpdateAlert("purchaseItem", result.getId().toString()))
            			.body(isNew);
            }
    }

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
        PurchaseItemVo purchaseItem = purchaseItemService.getPurchaseItem(id);
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
        PurchaseItemVo purchaseItem = purchaseItemService.getPurchaseItem(id);
        if (purchaseItem == null) {
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.save.noPerm", "")).body(null);
		}
        if (purchaseItem.getStatus() != PurchaseItem.STATUS_VALIBLE) {
    		return ResponseEntity.badRequest().headers(HeaderUtil.createError("cpmApp.purchaseItem.delete.hasDeleted", "")).body(null);
		}
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
    public ResponseEntity<List<PurchaseItemVo>> searchPurchaseItems(@RequestParam(value = "name",required=false) String name, 
    		@RequestParam(value = "contractId",required=false) String contractId,
    		@RequestParam(value = "source",required=false) String source,
    		@RequestParam(value = "ppType",required=false) String ppType,
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
        if (!StringUtil.isNullStr(ppType)) {
			purchaseItem.setType(StringUtil.nullToInteger(ppType));
		}
        Page<PurchaseItemVo> page = purchaseItemService.getPurchasePage(purchaseItem, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/purchase-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * 获取采购子项中已经有的合同列表
     */
    @GetMapping("/purchase-item/queryUserContract")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<LongValue>> queryUserContract() throws URISyntaxException{
    	 log.debug("REST request to queryUserContract");
    	 List<LongValue> list = purchaseItemService.queryUserContract();
    	 return new ResponseEntity<>(list, null, HttpStatus.OK);
    }
    
    @GetMapping("/purchase-item/queryProductPrice")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PURCHASE)
    public ResponseEntity<List<ProductPriceVo>> getAllProductPrices(
    		@RequestParam(value = "selectName",required=false) String selectName,
    		@RequestParam(value = "type",required=false) Integer type,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException{
    	log.debug("REST request to get a page of ProductPrice");
    	 Page<ProductPriceVo> page = purchaseItemService.searchPricePage(selectName,type,pageable);
         HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(selectName,page, "/api/product-prices");
         return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
