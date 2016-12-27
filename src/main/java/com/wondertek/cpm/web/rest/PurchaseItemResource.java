package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.service.PurchaseItemService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

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
    public ResponseEntity<PurchaseItem> updatePurchaseItem(@RequestBody PurchaseItem purchaseItem) throws URISyntaxException {
        log.debug("REST request to update PurchaseItem : {}", purchaseItem);
        if (purchaseItem.getId() == null) {
            return createPurchaseItem(purchaseItem);
        }
        PurchaseItem result = purchaseItemService.save(purchaseItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("purchaseItem", purchaseItem.getId().toString()))
            .body(result);
    }

    /**
     * GET  /purchase-items : get all the purchaseItems.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of purchaseItems in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/purchase-items")
    @Timed
    public ResponseEntity<List<PurchaseItem>> getAllPurchaseItems(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PurchaseItems");
        Page<PurchaseItem> page = purchaseItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/purchase-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /purchase-items/:id : get the "id" purchaseItem.
     *
     * @param id the id of the purchaseItem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the purchaseItem, or with status 404 (Not Found)
     */
    @GetMapping("/purchase-items/{id}")
    @Timed
    public ResponseEntity<PurchaseItem> getPurchaseItem(@PathVariable Long id) {
        log.debug("REST request to get PurchaseItem : {}", id);
        PurchaseItem purchaseItem = purchaseItemService.findOne(id);
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
    @GetMapping("/_search/purchase-items")
    @Timed
    public ResponseEntity<List<PurchaseItem>> searchPurchaseItems(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PurchaseItems for query {}", query);
        Page<PurchaseItem> page = purchaseItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/purchase-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
