package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.DeptType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DeptType entity.
 */
public interface DeptTypeRepository extends JpaRepository<DeptType,Long> {
	
	@Query(" from DeptType wdt, DeptInfo wdi, ContractBudget wcb, ProductPrice wpp where wcb.deptId = wdi.id and wdi.type = wdt.id and wcb.productPriceId=wpp.id and wpp.type=?1 and wpp.source=?2 group by wdt.id ")
	List<DeptType> findByContractBudgetAndProductPrice(Integer productPriceType, Integer productPriceSource);
}
