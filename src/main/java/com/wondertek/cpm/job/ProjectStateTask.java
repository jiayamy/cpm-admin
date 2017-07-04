package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.StatIdentify;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ExternalQuotationRepository;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectMonthlyStatRepository;
import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
import com.wondertek.cpm.repository.StatIdentifyRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class ProjectStateTask {
	
	private Logger log = LoggerFactory.getLogger(ProjectStateTask.class);
	
	private Map<Long, Boolean> contractIsEpibolicMap = new HashMap<>();
	
	private Map<Long, Integer> userIdGradeMap = new HashMap<>();
	
	private Map<Integer, Double> externalQuotationMap = new HashMap<>();
	
	@Inject
	private ProjectInfoRepository projectInfoRepository;
	
	@Inject
	private ProjectWeeklyStatRepository projectWeeklyStatRepository;
	
	@Inject
	private ProjectMonthlyStatRepository projectMonthlyStatRepository;
	
	@Inject
	private ProjectFinishInfoRepository projectFinishInfoRepository;
	
	@Inject
	private ProjectCostRepository projectCostRepository;
	
	@Inject
	private UserCostRepository userCostRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserTimesheetRepository userTimesheetRepository;
	
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@Inject
	private StatIdentifyRepository statIdentifyRepository;
	
	@Inject
	private ExternalQuotationRepository externalQuotationRepository;
	
	private void init(){
		List<ContractInfo> contractInfos = contractInfoRepository.findAll();
		contractIsEpibolicMap.clear();
		for(ContractInfo contractInfo : contractInfos){
			contractIsEpibolicMap.put(contractInfo.getId(), contractInfo.getIsEpibolic() != null ? contractInfo.getIsEpibolic() : Boolean.FALSE);
		}
		List<ExternalQuotation> externalQuotations = externalQuotationRepository.findAll();
		externalQuotationMap.clear();
		if(externalQuotations != null && externalQuotations.size() > 0){
			for(ExternalQuotation externalQuotation : externalQuotations){
				externalQuotationMap.put(externalQuotation.getGrade(), externalQuotation.getHourCost());
			}
		}
		List<User> users = userRepository.findAll();
		userIdGradeMap.clear();
		if(users != null && users.size() > 0){
			for(User user : users){
				userIdGradeMap.put(user.getId(), user.getGrade());
			}
		}
		List<StatIdentify> statIdentifies = statIdentifyRepository.findByStatus(StatIdentify.STATUS_UNAVALIABLE);
		if(statIdentifies != null && statIdentifies.size() > 0){
			statIdentifyRepository.delete(statIdentifies);
		}
	}
	/**
	 * TODO 每周的项目周统计，在合同统计之前执行
	 */
	@Scheduled(cron = "0 0 21 ? * MON")
	protected void generateProjectWeeklyState(){
		Date now = new Date();
		generateProjectWeeklyState(null, now);
	}
	protected void generateProjectWeeklyState(Long projectId, Date now){
		log.info("=====begin generate project weekly state=====");
		init();
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday(now));
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd(now).getTime());
		List<ProjectInfo> projectInfos = projectInfoRepository.findByStatusOrBeginTime(ProjectInfo.STATUS_ADD, beginTime, endTime);
		if(projectInfos != null && projectInfos.size() > 0){
			for (ProjectInfo projectInfo : projectInfos) {
				if(projectId != null && !projectId.equals(projectInfo.getId())){
					continue;
				}
				log.info("======begin generate project : "+projectInfo.getSerialNum()+"=====");
				Long id = projectInfo.getId();
				//初始化projectcost项目成本中人工成本
				initProjectCost(projectInfo,dates[0],dates[6],DateUtil.lastMonday(now),DateUtil.lastSundayEnd(now));
				
				//初始上周的stat
				List<ProjectWeeklyStat> projectWeeklyStats = projectWeeklyStatRepository.findByStatWeekAndProjectId(StringUtil.nullToLong(dates[6]), id);
				if(projectWeeklyStats != null && projectWeeklyStats.size()>0){
					for(ProjectWeeklyStat projectWeeklyStat : projectWeeklyStats){
						projectWeeklyStatRepository.delete(projectWeeklyStat);
					}
				}
				ProjectWeeklyStat projectWeeklyStat = new ProjectWeeklyStat();
				//项目主键
				projectWeeklyStat.setProjectId(id);
				//完成率
				List<ProjectFinishInfo> projectFinishInfos = projectFinishInfoRepository.findAllByProjectIdAndCreateTimeBefore(id, endTime);
				if(projectFinishInfos != null && projectFinishInfos.size() > 0){
					ProjectFinishInfo finishInfo = projectFinishInfos.get(projectFinishInfos.size() - 1);
					projectWeeklyStat.setFinishRate(finishInfo.getFinishRate());
				}else{
					log.error("no projectFinishInfo found belong to " + projectInfo.getSerialNum());
					projectWeeklyStat.setFinishRate(0D);
				}
				//人工成本,从项目成本中查询数据
				Double humanCost = projectCostRepository.findTotalByProjectIdAndTypeAndBeforeCostDay(id, ProjectCost.TYPE_HUMAN_COST, StringUtil.nullToLong(dates[6]));
				projectWeeklyStat.setHumanCost(StringUtil.nullToDouble(humanCost));
				//报销成本，从项目成本中查询数据
				Double payment = projectCostRepository.findTotalByProjectIdAndNoTypeAndBeforeCostDay(id, ProjectCost.TYPE_HUMAN_COST,StringUtil.nullToLong(dates[6]));
				projectWeeklyStat.setPayment(StringUtil.nullToDouble(payment));
				//项目总工时
				Double totalInput = 0D;
				List<Object[]> inputList =  userTimesheetRepository.findSumByDateAndObjIdAndType(StringUtil.nullToLong(dates[6]),id, UserTimesheet.TYPE_PROJECT);
				if(inputList != null && inputList.size() > 0){
					totalInput += StringUtil.nullToDouble(inputList.get(0)[0]);
				}else{
					log.error("no totalInput found belong to " + projectInfo.getSerialNum());
				}
				projectWeeklyStat.setTotalInput(totalInput);
				
				projectWeeklyStat.setCreateTime(ZonedDateTime.now());
				projectWeeklyStat.setStatWeek(StringUtil.nullToLong(dates[6]));
				projectWeeklyStatRepository.save(projectWeeklyStat);
				log.info("project : "+projectInfo.getSerialNum()+" weekly state saved");
				if(projectId != null && projectId.equals(projectInfo.getId())){
					break;
				}
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project weekly state=====");
	}
	/**
	 * 项目的月统计，最好在合同的月统计之前执行
	 */
	@Scheduled(cron = "0 0 22 10 * ?")
	protected void generateProjectMonthlyState(){
		Date now = new Date();
		generateProjectMonthlyState(null, now);
	}
	protected void generateProjectMonthlyState(Long projectId, Date now){
		log.info("=====begin generate project monthly state=====");
		init();
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonthBegin(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastMonthend(now).getTime());
		String fDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthBegin(now));
		String lDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthend(now));
		String lMonth = DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now));
		List<ProjectInfo> projectInfos = projectInfoRepository.findByStatusOrBeginTime(ProjectInfo.STATUS_ADD, beginTime, endTime);
		if(projectInfos != null && projectInfos.size() > 0){
			for (ProjectInfo projectInfo : projectInfos) {
				if(projectId != null && !projectId.equals(projectInfo.getId())){
					continue;
				}
				log.info("=======begin generate project : "+projectInfo.getSerialNum()+"=======");
				
				Long id = projectInfo.getId();
				//初始化cost
				initProjectCost(projectInfo,fDay,lDay,DateUtil.lastMonthBegin(now),DateUtil.lastMonthend(now));
				//初始上月的stat
				List<ProjectMonthlyStat> projectMonthlyStats = projectMonthlyStatRepository.findByStatWeekAndProjectId(StringUtil.nullToLong(lMonth), projectInfo.getId());
				if(projectMonthlyStats != null && projectMonthlyStats.size() > 0){
					for(ProjectMonthlyStat projectMonthlyStat : projectMonthlyStats){
						projectMonthlyStatRepository.delete(projectMonthlyStat);
					}
				}
				ProjectMonthlyStat projectMonthlyStat = new ProjectMonthlyStat();
				//项目id
				projectMonthlyStat.setProjectId(id);
				//完成率
				List<ProjectFinishInfo> projectFinishInfos = projectFinishInfoRepository.findAllByProjectIdAndCreateTimeBefore(id, endTime);
				if(projectFinishInfos != null && projectFinishInfos.size() > 0){
					ProjectFinishInfo finishInfo = projectFinishInfos.get(projectFinishInfos.size() - 1);
					projectMonthlyStat.setFinishRate(finishInfo.getFinishRate());
				}else{
					log.error("no projectFinishInfo found belong to " + projectInfo.getSerialNum());
					projectMonthlyStat.setFinishRate(0D);
				}
				//人工成本
				Double humanCost = projectCostRepository.findTotalByProjectIdAndTypeAndBeforeCostDay(id, ProjectCost.TYPE_HUMAN_COST, StringUtil.nullToLong(lDay));
				projectMonthlyStat.setHumanCost(StringUtil.nullToDouble(humanCost));
				//报销成本
				Double payment = projectCostRepository.findTotalByProjectIdAndNoTypeAndBeforeCostDay(id, ProjectCost.TYPE_HUMAN_COST, StringUtil.nullToLong(lDay));;
				projectMonthlyStat.setPayment(StringUtil.nullToDouble(payment));
				//项目总工时
				Double totalInput = 0D;
				List<Object[]> inputList = userTimesheetRepository.findSumByDateAndObjIdAndType(StringUtil.nullToLong(lDay), id, UserTimesheet.TYPE_PROJECT);
				if(inputList != null && inputList.size() > 0){
					totalInput += StringUtil.nullToDouble(inputList.get(0)[0]);
				}else{
					log.error("no totalInput found belong to " + projectInfo.getSerialNum());
				}
				projectMonthlyStat.setTotalInput(totalInput);
				
				projectMonthlyStat.setCreateTime(ZonedDateTime.now());
				projectMonthlyStat.setStatWeek(StringUtil.nullToLong(lMonth));
				projectMonthlyStatRepository.save(projectMonthlyStat);
				log.info("project : "+projectInfo.getSerialNum()+" monthly state saved");
				if(projectId != null && projectId.equals(projectInfo.getId())){
					break;
				}
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project monthly state=====");
	}
	/**
	 * 初始化项目工时成本
	 * @param dates 
	 * @param now 
	 */
	private void initProjectCost(ProjectInfo projectInfo, String startDayStr, String endDayStr,
			Date startDay, Date endDay) {
		try {
			//查看该项目是否在初始化工时信息
			while(true){
				StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(projectInfo.getId(), StatIdentify.TYPE_PROJECT);
				if(statIdentify != null){
					Integer status = statIdentify.getStatus();
					if(status == StatIdentify.STATUS_UNAVALIABLE){
						log.info("====waiting for statIdentfiy belong to project : " + projectInfo.getSerialNum());
						Thread.sleep(5*1000);
					}else{
						initIdentify(projectInfo);
						break;
					}
				}else{
					initIdentify(projectInfo);
					break;
				}
			}
			//该项目在起始日期内最后一条工时成本信息
			ProjectCost projectCost = projectCostRepository.findMaxByProjectIdAndCostDayAndType(projectInfo.getId(), StringUtil.nullToLong(startDayStr), StringUtil.nullToLong(endDayStr), ProjectCost.TYPE_HUMAN_COST);
			if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
				if(projectCost != null){//起始结束日期内有项目工时成本
					Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
					initProjectHumanCost(projectInfo, initDate, endDay);
				}else{
					initProjectHumanCost(projectInfo, startDay, endDay);
				}
			}else{
				if(projectCost != null){
					Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
					initProjectHumanCost(projectInfo, initDate, Date.from(projectInfo.getUpdateTime().toInstant()));
				}else{
					initProjectHumanCost(projectInfo, startDay, Date.from(projectInfo.getUpdateTime().toInstant()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			overIdentify(projectInfo);
		}
	}
	private void initProjectHumanCost(ProjectInfo projectInfo, Date beginTime, Date endTime){
		if(endTime.getTime() < beginTime.getTime()){
			return;
		}
		Long countDay = (endTime.getTime() - beginTime.getTime())/(24*60*60*1000);
		Boolean isEpibolic = contractIsEpibolicMap.get(projectInfo.getContractId());
		Date currentDay = beginTime;
		for(int i = 0; i <= countDay; i++){
			Long workDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", currentDay).toString());
			ProjectCost projectCost = new ProjectCost();
			projectCost.setProjectId(projectInfo.getId());
			projectCost.setName(projectInfo.getSerialNum() + "-" + DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			projectCost.setType(ProjectCost.TYPE_HUMAN_COST);
			Double total = 0D; 
			Double totalHour = 0D;
			List<Long> userTimesheetIds = new ArrayList<Long>();//被统计的员工日报id
			List<UserTimesheet> userTimesheets = userTimesheetRepository.findByWorkDayAndObjIdAndType(workDay, projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
			for(UserTimesheet userTimesheet : userTimesheets){
				userTimesheetIds.add(userTimesheet.getId());
				UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
				if(userCost != null){
					if(!isEpibolic){
						total += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
					}else{
						total += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
					}
				}else{
					log.error(" no UserCost founded belong to User : " +userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
				}
				if(userTimesheet.getRealInput() != null){
					totalHour += userTimesheet.getRealInput();
				}
			}
			projectCost.setInput(totalHour);
			projectCost.setTotal(total);
			projectCost.setCostDesc(StringUtil.getScaleDouble(totalHour, 1).toString());
			projectCost.setStatus(1);
			projectCost.setCreator("admin");
			projectCost.setCreateTime(ZonedDateTime.now());
			projectCost.setUpdator("admin");
			projectCost.setUpdateTime(ZonedDateTime.now());
			projectCost.setCostDay(workDay);
			projectCostRepository.save(projectCost);
			if(!userTimesheetIds.isEmpty()){
				if(userTimesheetIds.size() < 1000){
					userTimesheetRepository.updateCharacterById(userTimesheetIds);//更新已经被统计的对应日报记录
				}else{
					List<Long> updateIds = new ArrayList<Long>();
					for(Long id : userTimesheetIds){
						updateIds.add(id);
						if(updateIds.size() >= 1000){
							userTimesheetRepository.updateCharacterById(updateIds);//更新已经被统计的对应日报记录
							updateIds.clear();
						}
					}
					if(updateIds.size() > 0){
						userTimesheetRepository.updateCharacterById(updateIds);//更新已经被统计的对应日报记录
						updateIds.clear();
					}
				}
				userTimesheetIds.clear();
			}
			currentDay = new Date(currentDay.getTime() + (24*60*60*1000));
		}
		
	}
	
	private void initIdentify(ProjectInfo projectInfo){
		StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(projectInfo.getId(), StatIdentify.TYPE_PROJECT);
		if(statIdentify == null){
			StatIdentify st = new StatIdentify();
			st.setObjId(projectInfo.getId());
			st.setType(StatIdentify.TYPE_PROJECT);
			st.setStatus(StatIdentify.STATUS_UNAVALIABLE);
			statIdentifyRepository.saveAndFlush(st);
		}else{
			statIdentify.setStatus(StatIdentify.STATUS_UNAVALIABLE);
			statIdentifyRepository.saveAndFlush(statIdentify);
		}
	}
	
	private void overIdentify(ProjectInfo projectInfo){
		StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(projectInfo.getId(), StatIdentify.TYPE_PROJECT);
		if(statIdentify == null){
			StatIdentify st = new StatIdentify();
			st.setObjId(projectInfo.getId());
			st.setType(StatIdentify.TYPE_PROJECT);
			st.setStatus(StatIdentify.STATUS_AVALIABLE);
			statIdentifyRepository.saveAndFlush(st);
		}else{
			statIdentify.setStatus(StatIdentify.STATUS_AVALIABLE);
			statIdentifyRepository.saveAndFlush(statIdentify);
		}
	}
	
}
