package com.wondertek.cpm.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProductPrice;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ProductPriceVo;

public interface ProductPriceDao extends GenericDao<ProductPrice, Long> {

	Page<ProductPriceVo> getPricePage(ProductPrice productPrice,Pageable pageable);

}
