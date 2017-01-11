package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.repository.ProjectMonthlyStatDao;
import com.wondertek.cpm.repository.ProjectMonthlyStatRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ProjectMonthlyStatSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class ProjectMonthlyStatService {
	
	private final Logger log = LoggerFactory.getLogger(ProjectMonthlyStatService.class);
	
	@Inject
	private ProjectMonthlyStatRepository projectMonthlyStatRepository;
	
	@Inject
	private ProjectMonthlyStatDao projectMonthlyStatDao;
	
	@Inject
	private ProjectMonthlyStatSearchRepository projectMonthlyStatSearchRepository;
	
	@Inject
    private UserRepository userRepository;
	
	/**
     *  Get all the ProjectMonthlyStats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectMonthlyStat> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMonthlyStat");
        Page<ProjectMonthlyStat> result = projectMonthlyStatRepository.findAll(pageable);
        return result;
    }
    
    /**
     *  Get one ProjectMonthlyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectMonthlyStat findOne(Long id) {
        log.debug("Request to get ProjectMonthlyStat : {}", id);
        ProjectMonthlyStat projectMonthlyStat = projectMonthlyStatRepository.findOne(id);
        return projectMonthlyStat;
    }
    
    /**
     *  Delete the  ProjectMonthlyStat by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectMonthlyStat : {}", id);
        projectMonthlyStatRepository.delete(id);
        projectMonthlyStatSearchRepository.delete(id);
    }
    
    /**
     * Search for the ProjectMonthlyStats corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectMonthlyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMonthlyStat for query {}", query);
        Page<ProjectMonthlyStat> result = projectMonthlyStatSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ProjectMonthlyStat> getStatPage(String fromDate, String endDate, String statDate, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<ProjectMonthlyStat> page = projectMonthlyStatDao.getUserPage(fromDate, endDate, statDate, pageable, user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ProjectMonthlyStat>(), pageable, 0);
    	}
    }
}
