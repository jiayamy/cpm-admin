package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectFinishInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectFinishInfo entity.
 */
@SuppressWarnings("unused")
public interface ProjectFinishInfoRepository extends JpaRepository<ProjectFinishInfo,Long> {

}
