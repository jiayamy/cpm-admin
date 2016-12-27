package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.UserCost;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the UserCost entity.
 */
@SuppressWarnings("unused")
public interface UserCostRepository extends JpaRepository<UserCost,Long> {

}
