package com.wondertek.cpm.job;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.ProjectFinishInfoRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectMonthlyStatRepository;
import com.wondertek.cpm.repository.ProjectWeeklyStatRepository;
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
		List<ProjectInfo> projectInfos = projectInfoRepository.findAll();
		if(projectInfos != null && projectInfos.size() > 0){
			ZoneId zone = ZoneId.systemDefault();
			ZonedDateTime lastSaturday = Instant.ofEpochMilli(DateUtil.lastSundayEnd().getTime()).atZone(zone);
			String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday());
			for (ProjectInfo projectInfo : projectInfos) {
				log.info("======begin generate project : "+projectInfo.getName()+"=====");
				ProjectWeeklyStat projectWeeklyStat = new ProjectWeeklyStat();
				Long id = projectInfo.getId();
				//项目主键
				projectWeeklyStat.setProjectId(id);
				//完成率
				List<ProjectFinishInfo> projectFinishInfos = projectFinishInfoRepository
						.findAllByProjectIdAndCreateTimeBefore(id, lastSaturday);
				if(projectFinishInfos != null && projectFinishInfos.size() > 0){
					ProjectFinishInfo finishInfo = projectFinishInfos.get(projectFinishInfos.size() - 1);
					projectWeeklyStat.setFinishRate(finishInfo.getFinishRate());
				}else{
					log.error("no projectFinishInfo found belong to " + projectInfo.getName());
					projectWeeklyStat.setFinishRate(0D);
				}
				//人工成本
				Double humanCost = 0D;
				List<UserTimesheet> userTimesheets = userTimesheetRepository.
						findByDateAndObjIdAndType(StringUtil.nullToLong(dates[6]), id, UserTimesheet.TYPE_PROJECT);
				if(userTimesheets != null && userTimesheets.size() > 0){
					Integer contractType = contractTypeMap.get(projectInfo.getContractId());
					for(UserTimesheet userTimesheet : userTimesheets){
						UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(dates[6]), userTimesheet.getUserId());
						if(contractType == TYPE_CONTRACT_INTERNAL){
							humanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
						}else if(contractType == TYPE_CONTRACT_EXTERNAL){
							humanCost += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
						}else{
							log.error(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
						}
					}
				}else{
					log.error("no user Timesheets founded belong to " + projectInfo.getName());
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
					log.error("no projectPayment found belong to " + projectInfo.getName());
				}
				projectWeeklyStat.setPayment(payment);
				projectWeeklyStat.setCreateTime(ZonedDateTime.now());
				projectWeeklyStat.setStatWeek(StringUtil.nullToLong(dates[6]));
				projectWeeklyStatRepository.save(projectWeeklyStat);
				log.info("project : "+projectInfo.getName()+" weekly state saved");
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project weekly state=====");
	}
	
	@Scheduled(cron = "0 0 22 1 * ?")
	protected void generateProjectMonthlyState(){
		log.info("=====begin generate project monthly state=====");
		List<ProjectInfo> projectInfos = projectInfoRepository.findAll();
		if(projectInfos != null && projectInfos.size() > 0){
			ZoneId zone = ZoneId.systemDefault();
			ZonedDateTime nowTime = Instant.ofEpochMilli(DateUtil.lastMonthend().getTime()).atZone(zone);
			String lDay = DateUtil.getLastDayOfLastMonth("yyyyMMdd");
			String lMonth = DateUtil.getLastDayOfLastMonth("yyyyMM");
			for (ProjectInfo projectInfo : projectInfos) {
				log.info("=======begin generate project : "+projectInfo.getName()+"=======");
				ProjectMonthlyStat projectMonthlyStat = new ProjectMonthlyStat();
				Long id = projectInfo.getId();
				//项目id
				projectMonthlyStat.setProjectId(id);
				//完成率
				List<ProjectFinishInfo> projectFinishInfos = projectFinishInfoRepository
						.findAllByProjectIdAndCreateTimeBefore(id, nowTime);
				if(projectFinishInfos != null && projectFinishInfos.size() > 0){
					ProjectFinishInfo finishInfo = projectFinishInfos.get(projectFinishInfos.size() - 1);
					projectMonthlyStat.setFinishRate(finishInfo.getFinishRate());
				}else{
					log.error("no projectFinishInfo found belong to " + projectInfo.getName());
					projectMonthlyStat.setFinishRate(0D);
				}
				//人工成本
				Double humanCost = 0D;
				List<UserTimesheet> userTimesheets = userTimesheetRepository.
						findByDateAndObjIdAndType(StringUtil.nullToLong(lDay), id, UserTimesheet.TYPE_PROJECT);
				if(userTimesheets != null && userTimesheets.size() > 0){
					Integer contractType = contractTypeMap.get(projectInfo.getContractId());
					for(UserTimesheet userTimesheet : userTimesheets){
						UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(lDay), userTimesheet.getUserId());
						if(contractType == TYPE_CONTRACT_INTERNAL){
							humanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
						}else if(contractType == TYPE_CONTRACT_EXTERNAL){
							humanCost += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
						}else{
							log.error(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
						}
					}
				}else{
					log.error("no user Timesheets founded belong to " + projectInfo.getName());
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
					log.error("no projectPayment found belong to " + projectInfo.getName());
				}
				projectMonthlyStat.setPayment(payment);
				projectMonthlyStat.setCreateTime(ZonedDateTime.now());
				projectMonthlyStat.setStatWeek(StringUtil.nullToLong(lMonth));
				projectMonthlyStatRepository.save(projectMonthlyStat);
				log.info("project : "+projectInfo.getName()+" monthly state saved");
			}
		}else{
			log.error("no projectinfo found");
		}
		log.info("=====end generate project monthly state=====");
	}
	
}
