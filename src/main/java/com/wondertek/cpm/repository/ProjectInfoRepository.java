package com.wondertek.cpm.repository;

import com.wondertek.cpm.domain.ProjectInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectInfo entity.
 */
@SuppressWarnings("unused")
public interface ProjectInfoRepository extends JpaRepository<ProjectInfo,Long> {

}
