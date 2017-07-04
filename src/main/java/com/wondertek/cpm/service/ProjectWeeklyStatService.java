package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;
import com.wondertek.cpm.repository.ProjectWeeklyStatDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ProjectWeeklyStat.
 */
@Service
@Transactional
public class ProjectWeeklyStatService {

    private final Logger log = LoggerFactory.getLogger(ProjectWeeklyStatService.class);
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ProjectWeeklyStatDao projectWeeklyStatDao;

    /**
     *  Get one projectWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectWeeklyStatVo findOne(Long id) {
        log.debug("Request to get ProjectWeeklyStat : {}", id);
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
	        ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatDao.getById(id, user, deptInfo);
	        return projectWeeklyStatVo;
		}
		return null;
    }

    /**
     * Search for the projectWeeklyStat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProjectWeeklyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectWeeklyStats for query {}", query);
        Page<ProjectWeeklyStat> result = null;
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
    public Page<ProjectWeeklyStatVo> getStatPage(String projectId, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ProjectWeeklyStatVo> page = projectWeeklyStatDao.getUserPage(projectId, pageable, user,deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ProjectWeeklyStatVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartData(Date fromDate, Date toDate, Long projectId){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ChartReportDataVo> datas = new ArrayList<>();
        	ChartReportDataVo data1 = new ChartReportDataVo();
        	ChartReportDataVo data2 = new ChartReportDataVo();
        	ChartReportDataVo data3 = new ChartReportDataVo();
        	ChartReportDataVo data4 = new ChartReportDataVo();
        	data1.setName("人工成本");
        	data1.setType("line");
        	data2.setName("报销成本");
        	data2.setType("line");
        	data3.setName("项目总工时");
        	data3.setType("line");
        	data4.setName("当周工时");
        	data4.setType("line");
        	List<Double> dataD1 = new ArrayList<>();
        	List<Double> dataD2 = new ArrayList<>();
        	List<Double> dataD3 = new ArrayList<>();
        	List<Double> dataD4 = new ArrayList<>();
        	
        	Long temp = fromDate.getTime();
        	Long sevenDay = 7*24*60*60*1000L;
        	while(temp <= toDate.getTime()){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
        		ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatDao.getByStatWeekAndProjectId(statWeek, projectId, user, deptInfo);
        		Double humanCost = 0D;
        		Double payment = 0D;
        		Double totalInput = 0D;
        		Double thisInput = 0D;
        		if(projectWeeklyStatVo != null){
        			humanCost = projectWeeklyStatVo.getHumanCost();
        			payment = projectWeeklyStatVo.getPayment();
        			totalInput = projectWeeklyStatVo.getTotalInput();
        			thisInput = projectWeeklyStatVo.getThisInput();
        		}
        		dataD1.add(humanCost);
        		dataD2.add(payment);
        		dataD3.add(totalInput);
        		dataD4.add(thisInput);
        		
        		temp += sevenDay;
        	}
        	data1.setData(dataD1);
        	data2.setData(dataD2);
        	data3.setData(dataD3);
        	data4.setData(dataD4);
        	datas.add(data1);
        	datas.add(data2);
        	datas.add(data3);
        	datas.add(data4);
        	return datas;
    	}
    	return null;
    }
    
    public List<ChartReportDataVo> getFinishRateData(Date fromDate, Date toDate, Long projectId){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ChartReportDataVo> datas = new ArrayList<>();
        	ChartReportDataVo data = new ChartReportDataVo();
        	data.setName("完成率");
        	data.setType("line");
        	List<Double> dataD = new ArrayList<>();
        	Long temp = fromDate.getTime();
        	Long sevenDay = 7*24*60*60*1000L;
        	while(temp <= toDate.getTime()){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
        		ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatDao.getByStatWeekAndProjectId(statWeek, projectId, user, deptInfo);
        		Double FinishRate = 0D;
        		if(projectWeeklyStatVo != null){
        			FinishRate = projectWeeklyStatVo.getFinishRate();
        		}
        		dataD.add(FinishRate);
        		temp += sevenDay;
        	}
        	data.setData(dataD);
        	datas.add(data);
        	return datas;
    	}
    	return null;
    }
}
