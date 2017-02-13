package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProductPriceVo;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;
import com.wondertek.cpm.repository.PurchaseItemDao;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing PurchaseItem.
 */
@Service
@Transactional
public class PurchaseItemService {

    private final Logger log = LoggerFactory.getLogger(PurchaseItemService.class);
    
    @Inject
    private PurchaseItemRepository purchaseItemRepository;

//    @Inject
//    private PurchaseItemSearchRepository purchaseItemSearchRepository;
    
    @Inject
    private PurchaseItemDao purchaseItemDao;
    
    @Inject
    private UserRepository userRepository;

    /**
     * Save a purchaseItem.
     *
     * @param purchaseItem the entity to save
     * @return the persisted entity
     */
    public PurchaseItem save(PurchaseItem purchaseItem) {
        log.debug("Request to save PurchaseItem : {}", purchaseItem);
        PurchaseItem result = purchaseItemRepository.save(purchaseItem);
//        purchaseItemSearchRepository.save(result);
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
    public PurchaseItemVo getPurchaseItem(Long id) {
        log.debug("Request to get PurchaseItem : {}", id);
        List<Object[]> objs = this.userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
        if (objs != null && !objs.isEmpty()) {
        	Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];
			
			return purchaseItemDao.findPurchaseItemById(id,user,deptInfo);
		}
        return null;
    }

    /**
     *  Delete the  purchaseItem by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseItem : {}", id);
        PurchaseItem purchaseItem = purchaseItemRepository.findOne(id);
        if(purchaseItem != null){
        	purchaseItem.setStatus(PurchaseItem.STATUS_DELETED);
        	purchaseItem.setUpdateTime(ZonedDateTime.now());
        	purchaseItem.setUpdator(SecurityUtils.getCurrentUserLogin());
        	purchaseItemRepository.save(purchaseItem);
        }
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
        Page<PurchaseItem> result = null;
//        Page<PurchaseItem> result = purchaseItemSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

	public Page<PurchaseItemVo> getPurchasePage(PurchaseItem purchaseItem,
			Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null && !objs.isEmpty()) {
			Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];
			Page<PurchaseItemVo> page = purchaseItemDao.getPurchaserPage(purchaseItem,user,deptInfo,pageable);
			return page;
		}else {
			return new PageImpl<PurchaseItemVo>(new ArrayList<PurchaseItemVo>(),pageable,0);
		}
	}

	public PurchaseItem findOneById(Long id) {
		PurchaseItem purchaseItem = purchaseItemRepository.findOne(id);
		return purchaseItem;
	}

	public List<PurchaseItem> findOneByParams(String name, Integer source,
			Integer type) {
		List<PurchaseItem> list = purchaseItemRepository.findByNameAndSourceAndPurchaseType(name,source,type);
		return list;
	}

	public List<LongValue> queryUserContract() {
		List<Object[]> objs = this.userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		List<LongValue> returnList = new ArrayList<LongValue>();
		if (objs != null && objs.size() != 0) {
			Object[] o = objs.get(0);
			User user = (User) o[0];
			DeptInfo deptInfo = (DeptInfo) o[1];			
			returnList = purchaseItemDao.queryUserContract(user,deptInfo);
		}
		return returnList;
	}

	public Page<ProductPriceVo> searchPricePage(String selectName,
			Pageable pageable) {
		Page<ProductPriceVo> page = purchaseItemDao.getPricePage(selectName,pageable);
		return page;
	}
}
