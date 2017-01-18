package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.ProjectFinishInfo;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectMonthlyStat;
import com.wondertek.cpm.domain.ProjectWeeklyStat;
import com.wondertek.cpm.domain.StatIdentify;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectMonthlyStatRepository;
import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
import com.wondertek.cpm.repository.StatIdentifyRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class ProjectStateTask {
	
	private Logger log = LoggerFactory.getLogger(ProjectStateTask.class);
	
	private Map<Long, Integer> contractTypeMap = new HashMap<>();
	
	public static Integer TYPE_CONTRACT_INTERNAL = 1;
	
	public static Integer TYPE_CONTRACT_EXTERNAL = 2;
	
	public static Integer TYPE_PROJECT_COST_HUMAN_COST = 1;
	
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
	private UserTimesheetRepository userTimesheetRepository;
	
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@Inject
	private StatIdentifyRepository statIdentifyRepository;
	
	@PostConstruct
	private void init(){
		List<ContractInfo> contractInfos = contractInfoRepository.findAll();
		for(ContractInfo contractInfo : contractInfos){
			contractTypeMap.put(contractInfo.getId(), contractInfo.getType());
//			deptIdContractMap.put(contractInfo.getDeptId(), contractInfo);
		}
	}
	@Scheduled(cron = "0 0 22 ? * MON")
	protected void generateProjectWeeklyState(){
		log.info("=====begin generate project weekly state=====");
//		List<ProjectInfo> projectInfos = projectInfoRepository.findAll();
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday());
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday().getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd().getTime());
		List<ProjectInfo> projectInfos = projectInfoRepository.findByStatusOrUpdateTime(ProjectInfo.STATUS_ADD, beginTime, endTime);
		if(projectInfos != null && projectInfos.size() > 0){
			for (ProjectInfo projectInfo : projectInfos) {
				log.info("======begin generate project : "+projectInfo.getSerialNum()+"=====");
				Long id = projectInfo.getId();
				//初始化projectcost
				try {
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
					if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
						ProjectCost projectCost = projectCostRepository.findMaxByProjectIdAndCostDay(projectInfo.getId(), StringUtil.nullToLong(dates[0]), StringUtil.nullToLong(dates[6]));
						if(projectCost != null){
							Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
							initProjectHumanCost(projectInfo, initDate, DateUtil.lastSundayEnd());
						}else{
							initProjectHumanCost(projectInfo, DateUtil.lastMonday(), DateUtil.lastSundayEnd());
						}
					}else{
						ProjectCost projectCost = projectCostRepository.findMaxByProjectIdAndCostDay(projectInfo.getId(), StringUtil.nullToLong(dates[0]), StringUtil.nullToLong(dates[6]));
						if(projectCost != null){
							Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
							initProjectHumanCost(projectInfo, initDate, Date.from(projectInfo.getUpdateTime().toInstant()));
						}else{
							initProjectHumanCost(projectInfo, DateUtil.lastMonday(), Date.from(projectInfo.getUpdateTime().toInstant()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					overIdentify(projectInfo);
				}
				
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
				//人工成本
				Double humanCost = 0D;
				List<ProjectCost> projectCosts2 = projectCostRepository.findByProjectIdAndType(id, TYPE_PROJECT_COST_HUMAN_COST);
				if(projectCosts2 != null && projectCosts2.size() > 0){
					for(ProjectCost projectCost : projectCosts2){
						humanCost += projectCost.getTotal();
					}
				}else{
					log.error("no humanCost found belong to " + projectInfo.getSerialNum());
				}
				projectWeeklyStat.setHumanCost(humanCost);
				//报销成本
				List<ProjectCost> projectCosts = projectCostRepository.findAllByProjectIdAndNoType(id, TYPE_PROJECT_COST_HUMAN_COST);
				Double payment = 0D;
				if(projectCosts != null && projectCosts.size() > 0){
					for (ProjectCost projectCost2 : projectCosts) {
						payment += projectCost2.getTotal();
					}
				}else{
					log.error("no projectPayment found belong to " + projectInfo.getSerialNum());
				}
				projectWeeklyStat.setPayment(payment);
				projectWeeklyStat.setCreateTime(ZonedDateTime.now());
				projectWeeklyStat.setStatWeek(StringUtil.nullToLong(dates[6]));
				projectWeeklyStatRepository.save(projectWeeklyStat);
				log.info("project : "+projectInfo.getSerialNum()+" weekly state saved");
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project weekly state=====");
	}
	
	@Scheduled(cron = "0 0 22 1 * ?")
	protected void generateProjectMonthlyState(){
		log.info("=====begin generate project monthly state=====");
//		List<ProjectInfo> projectInfos = projectInfoRepository.findAll();
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonthBegin().getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastMonthend().getTime());
		String fDay = DateUtil.getFirstDayOfLastMonth("yyyyMMdd");
		String lDay = DateUtil.getLastDayOfLastMonth("yyyyMMdd");
		String lMonth = DateUtil.getLastDayOfLastMonth("yyyyMM");
		List<ProjectInfo> projectInfos = projectInfoRepository.findByStatusOrUpdateTime(ProjectInfo.STATUS_ADD, beginTime, endTime);
		if(projectInfos != null && projectInfos.size() > 0){
			for (ProjectInfo projectInfo : projectInfos) {
				log.info("=======begin generate project : "+projectInfo.getSerialNum()+"=======");
				
				Long id = projectInfo.getId();
				//初始化cost
				try {
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
					if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
						ProjectCost projectCost = projectCostRepository.findMaxByProjectIdAndCostDay(projectInfo.getId(), StringUtil.nullToLong(fDay), StringUtil.nullToLong(lDay));
						if(projectCost != null){
							Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
							initProjectHumanCost(projectInfo, initDate, DateUtil.lastMonthend());
						}else{
							initProjectHumanCost(projectInfo, DateUtil.lastMonthBegin(), DateUtil.lastMonthend());
						}
					}else{
						ProjectCost projectCost = projectCostRepository.findMaxByProjectIdAndCostDay(projectInfo.getId(), StringUtil.nullToLong(fDay), StringUtil.nullToLong(lDay));
						if(projectCost != null){
							Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", projectCost.getCostDay().toString()));
							initProjectHumanCost(projectInfo, initDate, Date.from(projectInfo.getUpdateTime().toInstant()));
						}else{
							initProjectHumanCost(projectInfo, DateUtil.lastMonthBegin(), Date.from(projectInfo.getUpdateTime().toInstant()));
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					overIdentify(projectInfo);
				}
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
				Double humanCost = 0D;
				List<ProjectCost> projectCosts2 = projectCostRepository.findByProjectIdAndType(id, TYPE_PROJECT_COST_HUMAN_COST);
				if(projectCosts2 != null && projectCosts2.size() > 0){
					for(ProjectCost projectCost : projectCosts2){
						humanCost += projectCost.getTotal();
					}
				}else{
					log.error("no humanCost found belong to " + projectInfo.getSerialNum());
				}
				projectMonthlyStat.setHumanCost(humanCost);
				//报销成本
				List<ProjectCost> projectCosts = projectCostRepository.findAllByProjectIdAndNoType(id, TYPE_PROJECT_COST_HUMAN_COST);
				Double payment = 0D;
				if(projectCosts != null && projectCosts.size() > 0){
					for (ProjectCost projectCost2 : projectCosts) {
						payment += projectCost2.getTotal();
					}
				}else{
					log.error("no projectPayment found belong to " + projectInfo.getSerialNum());
				}
				projectMonthlyStat.setPayment(payment);
				projectMonthlyStat.setCreateTime(ZonedDateTime.now());
				projectMonthlyStat.setStatWeek(StringUtil.nullToLong(lMonth));
				projectMonthlyStatRepository.save(projectMonthlyStat);
				log.info("project : "+projectInfo.getSerialNum()+" monthly state saved");
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project monthly state=====");
	}
	
	private void initProjectHumanCost(ProjectInfo projectInfo, Date beginTime, Date endTime){
		if(endTime.getTime() < beginTime.getTime()){
			return;
		}
		Long countDay = (endTime.getTime() - beginTime.getTime())/(24*60*60*1000);
		Integer contractType = contractTypeMap.get(projectInfo.getContractId());
		Date currentDay = beginTime;
		for(int i = 0; i <= countDay; i++){
			Long workDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", currentDay).toString());
			ProjectCost projectCost = new ProjectCost();
			projectCost.setProjectId(projectInfo.getId());
			projectCost.setName(projectInfo.getSerialNum() + "-humanCost-" + DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			projectCost.setType(TYPE_PROJECT_COST_HUMAN_COST);
			Double total = 0D; 
			List<UserTimesheet> userTimesheets = userTimesheetRepository.findByWorkDayAndObjIdAndType(workDay, projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
			for(UserTimesheet userTimesheet : userTimesheets){
				UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
				if(userCost != null){
					if(contractType == TYPE_CONTRACT_INTERNAL){
						total += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
					}else if(contractType == TYPE_CONTRACT_EXTERNAL){
						total += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
					}else{
						log.info(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
					}
				}else{
					log.error(" no UserCost founded belong to User : " +userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
				}
				
			}
			projectCost.setTotal(total);
			projectCost.setCostDesc(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			projectCost.setStatus(1);
			projectCost.setCreator("admin");
			projectCost.setCreateTime(ZonedDateTime.now());
			projectCost.setUpdator("admin");
			projectCost.setUpdateTime(ZonedDateTime.now());
			projectCost.setCostDay(workDay);
			projectCostRepository.save(projectCost);
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