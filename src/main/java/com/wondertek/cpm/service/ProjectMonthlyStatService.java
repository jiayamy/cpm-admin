package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;
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
    public ProjectMonthlyStatVo findOne(Long id) {
        log.debug("Request to get ProjectMonthlyStat : {}", id);
//        ProjectMonthlyStat projectMonthlyStat = projectMonthlyStatRepository.findOne(id);
        ProjectMonthlyStatVo projectMonthlyStat = projectMonthlyStatDao.getById(id);
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
    public Page<ProjectMonthlyStatVo> getStatPage(String projectId, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<ProjectMonthlyStatVo> page = projectMonthlyStatDao.getUserPage(projectId, pageable, user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ProjectMonthlyStatVo>(), pageable, 0);
    	}
    }
    
    /**
     * 查询用户的所有项目，管理人员能看到部门下面所有人员的项目信息
     */
    @Transactional(readOnly = true)
	public List<LongValue> queryUserProject() {
    	List<LongValue> returnList = new ArrayList<LongValue>();
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = projectMonthlyStatDao.queryUserProject(user,deptInfo);
    	}
		return returnList;
	}
    
    @Transactional(readOnly = true)
    public List<Double> getHumanCost(Date fromMonth, Date toMonth, Long projectId){
    	List<Double> data = new ArrayList<>();
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fromMonth);
    	cal2.setTime(toMonth);
    	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
    	int count = 0;
    	count += 12*yearCount;
    	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	for(int i = 0 ; i <= count; i++){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
    		List<ProjectMonthlyStat> projectMonthlyStats = projectMonthlyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double humanCost = 0D;
    		if(projectMonthlyStats != null && projectMonthlyStats.size() > 0){
    			humanCost = projectMonthlyStats.get(0).getHumanCost();
    		}
    		data.add(humanCost);
    		cal1.add(Calendar.MONTH, i+1);
    	}
    	return data;
    }
    
    @Transactional(readOnly = true)
    public List<Double> getPayment(Date fromMonth, Date toMonth, Long projectId){
    	List<Double> data = new ArrayList<>();
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fromMonth);
    	cal2.setTime(toMonth);
    	int count = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	for(int i = 0 ; i <= count; i++){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
    		List<ProjectMonthlyStat> projectMonthlyStats = projectMonthlyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double humanCost = 0D;
    		if(projectMonthlyStats != null && projectMonthlyStats.size() > 0){
    			humanCost = projectMonthlyStats.get(0).getPayment();
    		}
    		data.add(humanCost);
    		cal1.add(Calendar.MONTH, i+1);
    	}
    	return data;
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartData(Date fromMonth, Date toMonth, Long projectId){
    	List<ChartReportDataVo> datas = new ArrayList<>();
    	ChartReportDataVo data1 = new ChartReportDataVo();
    	ChartReportDataVo data2 = new ChartReportDataVo();
    	data1.setName("人工成本");
    	data1.setType("line");
    	data2.setName("报销成本");
    	data2.setType("line");
    	List<Double> dataD1 = new ArrayList<>();
    	List<Double> dataD2 = new ArrayList<>();
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fromMonth);
    	cal2.setTime(toMonth);
    	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
    	int count = 0;
    	count += 12*yearCount;
    	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	for(int i = 0 ; i <= count; i++){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
    		List<ProjectMonthlyStat> projectMonthlyStats = projectMonthlyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double humanCost = 0D;
    		Double payment = 0D;
    		if(projectMonthlyStats != null && projectMonthlyStats.size() > 0){
    			int max = projectMonthlyStats.size() - 1;
    			humanCost = projectMonthlyStats.get(max).getHumanCost();
    			payment = projectMonthlyStats.get(max).getPayment();
    		}
    		dataD1.add(humanCost);
    		dataD2.add(payment);
    		cal1.add(Calendar.MONTH, 1);
    	}
    	data1.setData(dataD1);
    	data2.setData(dataD2);
    	datas.add(data1);
    	datas.add(data2);
    	return datas;
    }
    
    public List<ChartReportDataVo> getFinishRateData(Date fromMonth, Date toMonth, Long projectId){
    	List<ChartReportDataVo> datas = new ArrayList<>();
    	ChartReportDataVo data = new ChartReportDataVo();
    	data.setName("完成率");
    	data.setType("line");
    	List<Double> dataD = new ArrayList<>();
    	Calendar cal1 = Calendar.getInstance();
    	Calendar cal2 = Calendar.getInstance();
    	cal1.setTime(fromMonth);
    	cal2.setTime(toMonth);
    	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
    	int count = 0;
    	count += 12*yearCount;
    	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
    	for(int i = 0 ; i <= count; i++){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
    		List<ProjectMonthlyStat> projectMonthlyStats = projectMonthlyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double finishRate = 0D;
    		if(projectMonthlyStats != null && projectMonthlyStats.size() > 0){
    			int max = projectMonthlyStats.size() - 1;
    			finishRate = projectMonthlyStats.get(max).getFinishRate();
    		}
    		dataD.add(finishRate);
    		cal1.add(Calendar.MONTH, 1);
    	}
    	data.setData(dataD);
    	datas.add(data);
    	return datas;
    }
}
