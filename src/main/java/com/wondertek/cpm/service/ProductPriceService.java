package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.repository.ProductPriceDao;
import com.wondertek.cpm.repository.ProductPriceRepository;

/**
 * Service Imppementation for managing ProductPrice.
 */
@Service
@Transactional
public class ProductPriceService {

    private final Logger log = LoggerFactory.getLogger(ProductPriceService.class);
    
    @Inject
    private ProductPriceRepository productPriceRepository;

//    @Inject
//    private ProductPriceSearchRepository productPriceSearchRepository;
    
    @Inject
    private ProductPriceDao productPriceDao;
    /**
     * Save a productPrice.
     *
     * @param productPrice the entity to save
     * @return the persisted entity
     */
    public ProductPrice save(ProductPrice productPrice) {
        log.debug("Request to save ProductPrice : {}", productPrice);
        ProductPrice result = productPriceRepository.save(productPrice);
//        productPriceSearchRepository.save(result);
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
//        productPriceSearchRepository.delete(id);
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
        Page<ProductPrice> result = null;
//        Page<ProductPrice> result = productPriceSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public Page<ProductPrice> search(String name, String type, String source,
			Pageable pageable) {
		StringBuffer hql = new StringBuffer();
		hql.append("where 1=1");
		List<Object> params = new ArrayList<Object>();
		if (!StringUtil.isNullStr(name)) {
			hql.append(" and pp.name like ?");
			params.add("%"+name+"%");
		}
		if (!StringUtil.isNullStr(type)) {
			hql.append(" and pp.type = ?");
			params.add(Integer.valueOf(type));
		}
		if (!StringUtil.isNullStr(source)) {
			hql.append(" and pp.source = ?");
			params.add(Integer.valueOf(source));
		}
		
		StringBuffer orderHql = new StringBuffer();
    	if(pageable.getSort() != null){
    		for (Order order : pageable.getSort()) {
    			if(CpmConstants.ORDER_IGNORE_SCORE.equalsIgnoreCase(order.getProperty())){
    				continue;
    			}
    			if(orderHql.length() != 0){
    				orderHql.append(",");
    			}else{
    				orderHql.append(" order by ");
    			}
    			if(order.isAscending()){
    				orderHql.append(order.getProperty()).append(" asc");
    			}else{
    				orderHql.append(order.getProperty()).append(" desc");
    			}
    		}
    	}
		String queryHql = "from ProductPrice pp " + hql.toString() + orderHql.toString();
		String countHql = "select count(pp.id) from ProductPrice pp " + hql.toString();
		return productPriceDao.queryHqlPage(queryHql,countHql,params.toArray(),pageable);
	}
}
