package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectMonthlyStat;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectMonthlyStat entity.
 */
@SuppressWarnings("unused")
public interface ProjectMonthlyStatRepository extends JpaRepository<ProjectMonthlyStat,Long> {

}
