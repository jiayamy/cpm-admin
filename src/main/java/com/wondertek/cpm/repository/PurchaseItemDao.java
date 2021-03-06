package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProductPriceVo;
import com.wondertek.cpm.domain.vo.PurchaseItemVo;

public interface PurchaseItemDao extends GenericDao<PurchaseItem, Long> {

	Page<PurchaseItemVo> getPurchaserPage(PurchaseItem purchaseItem,User user,DeptInfo deptInfo,Pageable pageable);

	PurchaseItemVo findPurchaseItemById(Long id,User user,DeptInfo deptInfo);

	List<LongValue> queryUserContract(User user,DeptInfo deptInfo);

	Page<ProductPriceVo> getPricePage(String selectName,Integer type,Pageable pageable);

}
