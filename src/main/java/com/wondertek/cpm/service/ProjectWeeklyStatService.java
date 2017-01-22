package com.wondertek.cpm.service;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ProjectWeeklyStatVo;
import com.wondertek.cpm.repository.ProjectWeeklyStatDao;
import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.search.ProjectWeeklyStatSearchRepository;
import com.wondertek.cpm.security.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProjectWeeklyStat.
 */
@Service
@Transactional
public class ProjectWeeklyStatService {

    private final Logger log = LoggerFactory.getLogger(ProjectWeeklyStatService.class);
    
    @Inject
    private ProjectWeeklyStatRepository projectWeeklyStatRepository;

    @Inject
    private ProjectWeeklyStatSearchRepository projectWeeklyStatSearchRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ProjectWeeklyStatDao projectWeeklyStatDao;

    /**
     * Save a projectWeeklyStat.
     *
     * @param projectWeeklyStat the entity to save
     * @return the persisted entity
     */
    public ProjectWeeklyStat save(ProjectWeeklyStat projectWeeklyStat) {
        log.debug("Request to save ProjectWeeklyStat : {}", projectWeeklyStat);
        ProjectWeeklyStat result = projectWeeklyStatRepository.save(projectWeeklyStat);
        projectWeeklyStatSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectWeeklyStats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProjectWeeklyStat> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectWeeklyStats");
        Page<ProjectWeeklyStat> result = projectWeeklyStatRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one projectWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProjectWeeklyStatVo findOne(Long id) {
        log.debug("Request to get ProjectWeeklyStat : {}", id);
//        ProjectWeeklyStat projectWeeklyStat = projectWeeklyStatRepository.findOne(id);
        ProjectWeeklyStatVo projectWeeklyStatVo = projectWeeklyStatDao.getById(id);
        return projectWeeklyStatVo;
    }

    /**
     *  Delete the  projectWeeklyStat by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProjectWeeklyStat : {}", id);
        projectWeeklyStatRepository.delete(id);
        projectWeeklyStatSearchRepository.delete(id);
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
        Page<ProjectWeeklyStat> result = projectWeeklyStatSearchRepository.search(queryStringQuery(query), pageable);
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
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<ProjectWeeklyStatVo> page = projectWeeklyStatDao.getUserPage(projectId, pageable, user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ProjectWeeklyStatVo>(), pageable, 0);
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
    		
    		returnList = projectWeeklyStatDao.queryUserProject(user,deptInfo);
    	}
		return returnList;
	}
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartData(Date fromDate, Date toDate, Long projectId){
    	List<ChartReportDataVo> datas = new ArrayList<>();
    	ChartReportDataVo data1 = new ChartReportDataVo();
    	ChartReportDataVo data2 = new ChartReportDataVo();
    	data1.setName("人工成本");
    	data1.setType("line");
    	data2.setName("报销成本");
    	data2.setType("line");
    	List<Double> dataD1 = new ArrayList<>();
    	List<Double> dataD2 = new ArrayList<>();
    	Long temp = fromDate.getTime();
    	Long sevenDay = 7*24*60*60*1000L;
    	while(temp <= toDate.getTime()){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
    		List<ProjectWeeklyStat> projectWeeklyStats = projectWeeklyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double humanCost = 0D;
    		Double payment = 0D;
    		if(projectWeeklyStats != null && projectWeeklyStats.size() > 0){
    			int max = projectWeeklyStats.size() - 1;
    			humanCost = projectWeeklyStats.get(max).getHumanCost();
    			payment = projectWeeklyStats.get(max).getPayment();
    		}
    		dataD1.add(humanCost);
    		dataD2.add(payment);
    		temp += sevenDay;
    	}
    	data1.setData(dataD1);
    	data2.setData(dataD2);
    	datas.add(data1);
    	datas.add(data2);
    	return datas;
    }
    
    public List<ChartReportDataVo> getFinishRateData(Date fromDate, Date toDate, Long projectId){
    	List<ChartReportDataVo> datas = new ArrayList<>();
    	ChartReportDataVo data = new ChartReportDataVo();
    	data.setName("完成率");
    	data.setType("line");
    	List<Double> dataD = new ArrayList<>();
    	Long temp = fromDate.getTime();
    	Long sevenDay = 7*24*60*60*1000L;
    	while(temp <= toDate.getTime()){
    		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
    		List<ProjectWeeklyStat> projectWeeklyStats = projectWeeklyStatRepository.findByStatWeekAndProjectId(statWeek, projectId);
    		Double FinishRate = 0D;
    		if(projectWeeklyStats != null && projectWeeklyStats.size() > 0){
    			int max = projectWeeklyStats.size() - 1;
    			FinishRate = projectWeeklyStats.get(max).getFinishRate();
    		}
    		dataD.add(FinishRate);
    		temp += sevenDay;
    	}
    	data.setData(dataD);
    	datas.add(data);
    	return datas;
    }
    
}
