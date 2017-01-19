package com.wondertek.cpm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectWeeklyStatService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectWeeklyStat.
 */
@RestController
@RequestMapping("/api")
public class ProjectWeeklyStatResource {

    private final Logger log = LoggerFactory.getLogger(ProjectWeeklyStatResource.class);
        
    @Inject
    private ProjectWeeklyStatService projectWeeklyStatService;

    /**
     * POST  /project-weekly-stats : Create a new projectWeeklyStat.
     *
     * @param projectWeeklyStat the projectWeeklyStat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectWeeklyStat, or with status 400 (Bad Request) if the projectWeeklyStat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<ProjectWeeklyStat> createProjectWeeklyStat(@RequestBody ProjectWeeklyStat projectWeeklyStat) throws URISyntaxException {
        log.debug("REST request to save ProjectWeeklyStat : {}", projectWeeklyStat);
        if (projectWeeklyStat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("projectWeeklyStat", "idexists", "A new projectWeeklyStat cannot already have an ID")).body(null);
        }
        ProjectWeeklyStat result = projectWeeklyStatService.save(projectWeeklyStat);
        return ResponseEntity.created(new URI("/api/project-weekly-stats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("projectWeeklyStat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-weekly-stats : Updates an existing projectWeeklyStat.
     *
     * @param projectWeeklyStat the projectWeeklyStat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectWeeklyStat,
     * or with status 400 (Bad Request) if the projectWeeklyStat is not valid,
     * or with status 500 (Internal Server Error) if the projectWeeklyStat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<ProjectWeeklyStat> updateProjectWeeklyStat(@RequestBody ProjectWeeklyStat projectWeeklyStat) throws URISyntaxException {
        log.debug("REST request to update ProjectWeeklyStat : {}", projectWeeklyStat);
        if (projectWeeklyStat.getId() == null) {
            return createProjectWeeklyStat(projectWeeklyStat);
        }
        ProjectWeeklyStat result = projectWeeklyStatService.save(projectWeeklyStat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("projectWeeklyStat", projectWeeklyStat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-weekly-stats : get all the projectWeeklyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectWeeklyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<List<ProjectWeeklyStatVo>> getAllProjectWeeklyStats(
    		@ApiParam(value="projectId") @RequestParam(value="projectId") String projectId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectWeeklyStats");
        Page<ProjectWeeklyStatVo> page = projectWeeklyStatService.getStatPage(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-weekly-stats/:id : get the "id" projectWeeklyStat.
     *
     * @param id the id of the projectWeeklyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectWeeklyStat, or with status 404 (Not Found)
     */
    @GetMapping("/project-weekly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<ProjectWeeklyStatVo> getProjectWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to get ProjectWeeklyStat : {}", id);
        ProjectWeeklyStatVo projectWeeklyStat = projectWeeklyStatService.findOne(id);
        return Optional.ofNullable(projectWeeklyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /project-weekly-stats/:id : delete the "id" projectWeeklyStat.
     *
     * @param id the id of the projectWeeklyStat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-weekly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<Void> deleteProjectWeeklyStat(@PathVariable Long id) {
        log.debug("REST request to delete ProjectWeeklyStat : {}", id);
        projectWeeklyStatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectWeeklyStat", id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-weekly-stats?query=:query : search for the projectWeeklyStat corresponding
     * to the query.
     *
     * @param query the query of the projectWeeklyStat search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-weekly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<List<ProjectWeeklyStat>> searchProjectWeeklyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectWeeklyStats for query {}", query);
        Page<ProjectWeeklyStat> page = projectWeeklyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-weekly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/project-weekly-stats/queryUserProject")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
	public ResponseEntity<List<LongValue>> queryUserProject() throws URISyntaxException {
	    log.debug("REST request to queryUserProject");
	    List<LongValue> list = projectWeeklyStatService.queryUserProject();
	    return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
    
    @GetMapping("/project-weekly-stats/queryChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ChartReportVo getChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id", required = true) Long statId){
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatService.findOne(statId);
    	Long projectId = projectWeeklyStatVo.getProjectId();
    	chartReportVo.setTitle(projectWeeklyStatVo.getSerialNum());
    	if(StringUtil.isNullStr(toDate)){
    		toDate = projectWeeklyStatVo.getStatWeek().toString();
    	}else{
    		toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", toDate))[6];
    	}
    	Date lDay = DateUtil.parseDate("yyyyMMdd", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMMdd", DateUtil.addDayNum(-6*7, lDay));
    	}else{
    		fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", fromDate))[6];
    	}
    	Date fDay = DateUtil.parseDate("yyyyMMdd", fromDate);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"人工成本","报销成本"}));
    	chartReportVo.setLegend(legend);
    	Long sevenDay = 7*24*60*60*1000L;
    	Long temp = fDay.getTime();
    	List<String> category = new ArrayList<String>();
    	while(temp <= lDay.getTime()){
    		category.add(DateUtil.formatDate("yyyy-MM-dd", new Date(temp)));
    		temp += sevenDay;
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = projectWeeklyStatService.getChartData(fDay, lDay, projectId);
    	chartReportVo.setSeries(datas);
    	return chartReportVo;
    }
    
    @GetMapping("/project-weekly-stats/queryFinishRateChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ChartReportVo getFinishRateChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id", required = true) Long statId){
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatService.findOne(statId);
    	Long projectId = projectWeeklyStatVo.getProjectId();
    	chartReportVo.setTitle(projectWeeklyStatVo.getSerialNum() + "-完成率");
    	if(StringUtil.isNullStr(toDate)){
    		toDate = projectWeeklyStatVo.getStatWeek().toString();
    	}else{
    		toDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", toDate))[6];
    	}
    	Date lDay = DateUtil.parseDate("yyyyMMdd", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMMdd", DateUtil.addDayNum(-6*7, lDay));
    	}else{
    		fromDate = DateUtil.getWholeWeekByDate(DateUtil.parseDate("yyyyMMdd", fromDate))[6];
    	}
    	Date fDay = DateUtil.parseDate("yyyyMMdd", fromDate);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"完成率"}));
    	chartReportVo.setLegend(legend);
    	Long sevenDay = 7*24*60*60*1000L;
    	Long temp = fDay.getTime();
    	List<String> category = new ArrayList<String>();
    	while(temp <= lDay.getTime()){
    		category.add(DateUtil.formatDate("yyyy-MM-dd", new Date(temp)));
    		temp += sevenDay;
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = projectWeeklyStatService.getFinishRateData(fDay, lDay, projectId);
    	chartReportVo.setSeries(datas);
    	return chartReportVo;
    }
}
