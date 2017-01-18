package com.wondertek.cpm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ChartReportVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;
import com.wondertek.cpm.security.AuthoritiesConstants;
import com.wondertek.cpm.service.ProjectMonthlyStatService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing ProjectMonthlyStat.
 */
@RestController
@RequestMapping("/api")
public class ProjectMonthlyStatResource {
	
	private final Logger log = LoggerFactory.getLogger(ProjectMonthlyStatResource.class);
	
	@Inject
	private ProjectMonthlyStatService projectMonthlyStatService;
	
	/**
     * GET  /projcet-monthly-stats : get all the ProjectMonthlyStats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ProjectMonthlyStats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/project-monthly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<List<ProjectMonthlyStatVo>> getAllProjectMonthlyStats(
    		@ApiParam(value="projectId") @RequestParam(value="projectId") String projectId,
    		@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ProjectMonthlyStats");
        Page<ProjectMonthlyStatVo> page = projectMonthlyStatService.getStatPage(projectId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /project-monthly-stats/:id : get the "id" ProjectMonthlyStat.
     *
     * @param id the id of the ProjectMonthlyStat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ProjectMonthlyStat, or with status 404 (Not Found)
     */
    @GetMapping("/project-monthly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<ProjectMonthlyStatVo> getProjectMonthlyStat(@PathVariable Long id) {
        log.debug("REST request to get ProjectMonthlyStats : {}", id);
        ProjectMonthlyStatVo projectMonthlyStat = projectMonthlyStatService.findOne(id);
        return Optional.ofNullable(projectMonthlyStat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * DELETE  /project-monthly-stats/:id : delete the "id" ProjectMonthlyStats.
     *
     * @param id the id of the ProjectMonthlyStats to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-monthly-stats/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<Void> deleteProjectMonthlyStat(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMonthlyStats : {}", id);
        projectMonthlyStatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("projectMonthlyStats", id.toString())).build();
    }
    
    /**
     * SEARCH  /_search/project-monthly-stats?query=:query : search for the ProjectMonthlyStats corresponding
     * to the query.
     *
     * @param query the query of the ProjectMonthlyStats search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/project-monthly-stats")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ResponseEntity<List<ProjectMonthlyStat>> searchProjectMonthlyStats(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ProjectMonthlyStat for query {}", query);
        Page<ProjectMonthlyStat> page = projectMonthlyStatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-monthly-stats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/project-monthly-stats/queryUserProject")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
	public ResponseEntity<List<LongValue>> queryUserProject() throws URISyntaxException {
	    log.debug("REST request to queryUserProject");
	    List<LongValue> list = projectMonthlyStatService.queryUserProject();
	    return new ResponseEntity<>(list, null, HttpStatus.OK);
	}
    
    @GetMapping("/project-monthly-stats/queryChart")
    @Timed
    @Secured(AuthoritiesConstants.ROLE_STAT_PROJECT)
    public ChartReportVo getChartReport(@ApiParam(value="fromDate") @RequestParam(value="fromDate") String fromDate,
    		@ApiParam(value="toDate") @RequestParam(value="toDate") String toDate,
    		@ApiParam(value="id") @RequestParam(value="id") Long statId){
    	ChartReportVo chartReportVo = new ChartReportVo();
    	ProjectMonthlyStatVo projectMonthlyStatvo = projectMonthlyStatService.findOne(statId);
    	Long projectId = projectMonthlyStatvo.getProjectId();
    	chartReportVo.setTitle(projectMonthlyStatvo.getSerialNum());
    	if(StringUtil.isNullStr(toDate)){
    		toDate = projectMonthlyStatvo.getStatWeek().toString();
    	}
    	Date lMonth = DateUtil.parseyyyyMM("yyyy-MM", toDate);
    	if(StringUtil.isNullStr(fromDate)){
    		fromDate = DateUtil.formatDate("yyyyMM", DateUtil.addMonthNum(-6, lMonth));
    	}
    	Date fMonth = DateUtil.parseyyyyMM("yyyy-MM", fromDate);
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fMonth);
    	cal2.setTime(lMonth);
    	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
    	int count = 0;
    	count += 12*yearCount;
    	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	List<String> category = new ArrayList<String>();
    	for(int i = 0; i <= count; i++){
    		category.add(DateUtil.formatDate("yyyy-MM", cal1.getTime()));
    		cal1.add(Calendar.MONTH, 1);
    	}
    	chartReportVo.setCategory(category);
    	List<ChartReportDataVo> datas = projectMonthlyStatService.getChartData(fMonth, lMonth, projectId);
    	chartReportVo.setSeries(datas);
    	List<String> legend = new ArrayList<String>(Arrays.asList(new String[]{"人工成本","报销成本"}));
    	chartReportVo.setLegend(legend);
    	return chartReportVo;
    }
}
