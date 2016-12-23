package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.service.ProjectInfoService;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api")
public class ProjectInfoResource {
    private final Logger log = LoggerFactory.getLogger(ProjectInfoResource.class);
    @Autowired
    private ProjectInfoRepository projectInfoRepository;
    
    @Autowired
    private ProjectInfoService projectInfoService;
    
    @GetMapping("/project/info")
    @Timed
    public ResponseEntity<List<ProjectInfo>> getPageByPageable(
    		@RequestParam(value = "name") String name,
    		@ApiParam Pageable pageable
    		){
    	Page<ProjectInfo> page = projectInfoService.getPageByParam(name, pageable);
    	HttpHeaders headers = null;
		try {
			headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project/info");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    @GetMapping("/project/infos")
    @Timed
    public ResponseEntity<List<ProjectInfo>> getPageByPageable(
    		@RequestParam(value = "name") String name,
    		@RequestParam(value = "start") int start,
    		@RequestParam(value = "limit") int limit
    		){
    	Page<ProjectInfo> page = projectInfoService.getPageByParam(name, start,limit,null);
    	HttpHeaders headers = null;
		try {
			headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project/infos");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
