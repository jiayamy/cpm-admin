package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProductPriceService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProductPrice.
 */
@RestController
@RequestMapping("/api")
public class ProductPriceResource {

    private final Logger log = LoggerFactory.getLogger(ProductPriceResource.class);
        
    @Inject
    private ProductPriceService productPriceService;

    /**
     * POST  /product-prices : Create a new productPrice.
     *
     * @param productPrice the productPrice to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productPrice, or with status 400 (Bad Request) if the productPrice has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/product-prices")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<ProductPrice> createProductPrice(@RequestBody ProductPrice productPrice) throws URISyntaxException {
        log.debug("REST request to save ProductPrice : {}", productPrice);
        if (productPrice.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productPrice", "idexists", "A new productPrice cannot already have an ID")).body(null);
        }
        ProductPrice result = productPriceService.save(productPrice);
        return ResponseEntity.created(new URI("/api/product-prices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("productPrice", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /product-prices : Updates an existing productPrice.
     *
     * @param productPrice the productPrice to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productPrice,
     * or with status 400 (Bad Request) if the productPrice is not valid,
     * or with status 500 (Internal Server Error) if the productPrice couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/product-prices")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<ProductPrice> updateProductPrice(@RequestBody ProductPrice productPrice) throws URISyntaxException {
        log.debug("REST request to update ProductPrice : {}", productPrice);
        if (productPrice.getId() == null) {
            return createProductPrice(productPrice);
        }
        ProductPrice result = productPriceService.save(productPrice);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("productPrice", productPrice.getId().toString()))
            .body(result);
    }

    /**
     * GET  /product-prices : get all the productPrices.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of productPrices in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
//    @GetMapping("/product-prices")
//    @Timed
//    public ResponseEntity<List<ProductPrice>> getAllProductPrices(@ApiParam Pageable pageable)
//        throws URISyntaxException {
//        log.debug("REST request to get a page of ProductPrices");
//        Page<ProductPrice> page = productPriceService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/product-prices");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

    /**
     * GET  /product-prices/:id : get the "id" productPrice.
     *
     * @param id the id of the productPrice to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productPrice, or with status 404 (Not Found)
     */
    @GetMapping("/product-prices/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<ProductPrice> getProductPrice(@PathVariable Long id) {
        log.debug("REST request to get ProductPrice : {}", id);
        ProductPrice productPrice = productPriceService.findOne(id);
        return Optional.ofNullable(productPrice)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /product-prices/:id : delete the "id" productPrice.
     *
     * @param id the id of the productPrice to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/product-prices/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<Void> deleteProductPrice(@PathVariable Long id) {
        log.debug("REST request to delete ProductPrice : {}", id);
        productPriceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productPrice", id.toString())).build();
    }

    /**
     * SEARCH  /_search/product-prices?query=:query : search for the productPrice corresponding
     * to the query.
     *
     * @param query the query of the productPrice search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/product-prices")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<List<ProductPrice>> searchProductPrices(
    		@RequestParam(value = "name",required=false) String name,
    		@RequestParam(value = "type",required=false) String type,
    		@RequestParam(value = "source",required=false) String source,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProductPrices for query {}", name);
        Page<ProductPrice> page = productPriceService.search(name,type,source, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(name,page, "/api/_search/product-prices");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * GET  /product-prices : get all the productPrice corresponding
     * to the query.
     *
     * @param query the query of the productPrice search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/product-prices")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_CONTRACT_PRODUCTPRICE)
    public ResponseEntity<List<ProductPrice>> getAllProductPrices(
    		@RequestParam(value = "source",required=false) String source,
    		@RequestParam(value = "type",required=false) String type,
    		@RequestParam(value = "name",required=false) String name,
    		@ApiParam Pageable pageable)
    	throws URISyntaxException{
    	log.debug("REST request to get a page of ProductPrice");
    	 Page<ProductPrice> page = productPriceService.search(name,type,source, pageable);
         HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(name,page, "/api/product-prices");
         return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
