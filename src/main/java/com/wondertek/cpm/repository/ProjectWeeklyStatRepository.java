package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectWeeklyStat;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectWeeklyStat entity.
 */
@SuppressWarnings("unused")
public interface ProjectWeeklyStatRepository extends JpaRepository<ProjectWeeklyStat,Long> {

}
