package com.wondertek.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondertek.cpm.domain.ShareInfo;

public interface ShareInfoRepository extends JpaRepository<ShareInfo,Long>{
	
}
