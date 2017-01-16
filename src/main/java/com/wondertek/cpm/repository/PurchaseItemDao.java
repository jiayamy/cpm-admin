package com.wondertek.cpm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;

public interface PurchaseItemDao extends GenericDao<PurchaseItem, Long> {

	Page<PurchaseItemVo> getPurchaserPage(PurchaseItem purchaseItem,Pageable pageable);

}
