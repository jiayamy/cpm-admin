package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.UserTimesheet;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the UserTimesheet entity.
 */
@SuppressWarnings("unused")
public interface UserTimesheetRepository extends JpaRepository<UserTimesheet,Long> {

}
