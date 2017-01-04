package com.wondertek.cpm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.service.HolidayInfoService;
import com.wondertek.cpm.web.rest.util.HeaderUtil;
import com.wondertek.cpm.web.rest.util.PaginationUtil;
import com.wondertek.cpm.web.rest.util.TimerHolidayUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing HolidayInfo.
 */
@RestController
@RequestMapping("/api")
public class HolidayInfoResource {

    private final Logger log = LoggerFactory.getLogger(HolidayInfoResource.class);
        
    @Inject
    private HolidayInfoService holidayInfoService;

    /**
     * POST  /holiday-infos : Create a new holidayInfo.
     *
     * @param holidayInfo the holidayInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new holidayInfo, or with status 400 (Bad Request) if the holidayInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/holiday-infos")
    @Timed
    public ResponseEntity<HolidayInfo> createHolidayInfo(@RequestBody HolidayInfo holidayInfo) throws URISyntaxException {
        log.debug("REST request to save HolidayInfo : {}", holidayInfo);
        if (holidayInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("holidayInfo", "idexists", "A new holidayInfo cannot already have an ID")).body(null);
        }
        HolidayInfo result = holidayInfoService.save(holidayInfo);
        return ResponseEntity.created(new URI("/api/holiday-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("holidayInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /holiday-infos : Updates an existing holidayInfo.
     *
     * @param holidayInfo the holidayInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated holidayInfo,
     * or with status 400 (Bad Request) if the holidayInfo is not valid,
     * or with status 500 (Internal Server Error) if the holidayInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/holiday-infos")
    @Timed
    public ResponseEntity<HolidayInfo> updateHolidayInfo(@RequestBody HolidayInfo holidayInfo) throws URISyntaxException {
        log.debug("REST request to update HolidayInfo : {}", holidayInfo);
        if (holidayInfo.getId() == null) {
            return createHolidayInfo(holidayInfo);
        }
        HolidayInfo result = holidayInfoService.save(holidayInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("holidayInfo", holidayInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /holiday-infos : get all the holidayInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of holidayInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/holiday-infos")
    @Timed
    public ResponseEntity<List<HolidayInfo>> getAllHolidayInfos(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of HolidayInfos");
        //更新一年内的工作日、休息日
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Long dateTime = Long.valueOf(sdf.format(cal.getTime()));
        int count = holidayInfoService.findByCurrDay(dateTime);
        if(count<=0){
        	List<HolidayInfo> lists = TimerHolidayUtil.holidayUpdate();
        	if(lists != null && !lists.isEmpty()){
        		lists = holidayInfoService.save(lists);
        	}
        }
        
        Page<HolidayInfo> page = holidayInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/holiday-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /holiday-infos/:id : get the "id" holidayInfo.
     *
     * @param id the id of the holidayInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the holidayInfo, or with status 404 (Not Found)
     */
    @GetMapping("/holiday-infos/{id}")
    @Timed
    public ResponseEntity<HolidayInfo> getHolidayInfo(@PathVariable Long id) {
        log.debug("REST request to get HolidayInfo : {}", id);
        HolidayInfo holidayInfo = holidayInfoService.findOne(id);
        return Optional.ofNullable(holidayInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /holiday-infos/:id : delete the "id" holidayInfo.
     *
     * @param id the id of the holidayInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/holiday-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteHolidayInfo(@PathVariable Long id) {
        log.debug("REST request to delete HolidayInfo : {}", id);
        holidayInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("holidayInfo", id.toString())).build();
    }

    /**
     * SEARCH  /_search/holiday-infos?query=:query : search for the holidayInfo corresponding
     * to the query.
     *
     * @param query the query of the holidayInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/holiday-infos")
    @Timed
    public ResponseEntity<List<HolidayInfo>> searchHolidayInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of HolidayInfos for query {}", query);
        Page<HolidayInfo> page = holidayInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/holiday-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
