package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.ContractFinishInfo;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.StatIdentify;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.ContractCostRepository;
import com.wondertek.cpm.repository.ContractFinishInfoRepository;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ContractMonthlyStatRepository;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.ContractWeeklyStatRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.StatIdentifyRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class ContractStateTask {

	private Logger log = LoggerFactory.getLogger(ContractStateTask.class);
	
	public static Long TYPE_DEPT_SALES = 2L;
	
	public static Long TYPE_DEPT_CONSULT = 3L;
	
	public static Integer TYPE_PROJECT_COST_HUMAN_COST = 1;
	
	public static Integer TYPE_CONTRACT_INTERNAL = 1;
	
	public static Integer TYPE_CONTRACT_EXTERNAL = 2;
	
	public static Integer TYPE_CONTRACT_PUBLIC = 4;
	
	public static Integer TYPE_CONTRACT_COST_HUMAN_COST = 1;
	
	public static Integer TYPE_PURCHASE_ITEM_HARDWARE = 1;
	
	public static Integer TYPE_PURCHASE_ITEM_SOFTWARE = 2;
	
	public static Integer SOURCE_PURCHASE_ITEM_INTERNAL = 1;
	
	public static Integer SOURCE_PURCHASE_ITEM_EXTERNAL = 2;
	
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@Inject
	private ContractWeeklyStatRepository contractWeeklyStatRepository;
	
	@Inject
	private ContractMonthlyStatRepository contractMonthlyStatRepository;
	
	@Inject
	private ContractFinishInfoRepository contractFinishInfoRepository;
	
	@Inject
	private ContractReceiveRepository contractReceiveRepository;

	@Inject
	private ContractCostRepository contractCostRepository;
	
	@Inject
	private DeptInfoRepository deptInfoRepository;
	
	@Inject
	private PurchaseItemRepository purchaseItemRepository;
	
	@Inject
	private ProjectInfoRepository projectInfoRepository;
	
	@Inject
	private ProjectCostRepository projectCostRepository;
	
	@Inject
	private UserTimesheetRepository userTimesheetRepository;
	
	@Inject
	private UserCostRepository userCostRepository;

	@Inject
	private StatIdentifyRepository statIdentifyRepository;
	
	
	@Scheduled(cron = "0 0 23 ? * MON")
	protected void generateContractWeeklyStat(){
		log.info("=====begin generate Contract Weekly Stat=====");
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday());
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday().getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd().getTime());
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrUpdateTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
//			//公共成本日报列表
//			List<UserTimesheet> userTimesheets4 = userTimesheetRepository.findByDateAndType( StringUtil.nullToLong(dates[6]), UserTimesheet.TYPE_PUBLIC);
			for(ContractInfo contractInfo : contractInfos){
				log.info("=========begin generate Contract : "+contractInfo.getName()+"=========");
				//初始化contractcost
				try {
					while(true){
						StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(contractInfo.getId(), StatIdentify.TYPE_CONTRACT);
						if(statIdentify != null){
							Integer status = statIdentify.getStatus();
							if(status == StatIdentify.STATUS_UNAVALIABLE){
								log.info("====waiting for statIdentfiy belong to project : " + contractInfo.getSerialNum());
								Thread.sleep(5*1000);
							}else{
								initIdentify(contractInfo);
								break;
							}
						}else{
							initIdentify(contractInfo);
							break;
						}
					}
					ContractCost contractCost2 = contractCostRepository.findMaxByContractIdAndCostDay(contractInfo.getId(), StringUtil.nullToLong(dates[0]), StringUtil.nullToLong(dates[6]));
					if(contractCost2 != null){
						Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", contractCost2.getCostDay().toString()));
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, initDate, DateUtil.lastSundayEnd());
						}else{
							initContractHumanCost(contractInfo, initDate, Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}else{
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, DateUtil.lastMonday(), DateUtil.lastSundayEnd());
						}else{
							initContractHumanCost(contractInfo, DateUtil.lastMonday(), Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					overIdentify(contractInfo);
				}
				//初始上周stat
				List<ContractWeeklyStat> contractWeeklyStats = contractWeeklyStatRepository.findByStatWeekAndContractId(StringUtil.nullToLong(dates[6]), contractInfo.getId());
				if(contractWeeklyStats != null){
					for(ContractWeeklyStat contractWeeklyStat : contractWeeklyStats){
						contractWeeklyStatRepository.delete(contractWeeklyStat);
					}
				}
				ContractWeeklyStat contractWeeklyStat = new ContractWeeklyStat();
				Long id = contractInfo.getId();
				//合同id
				contractWeeklyStat.setContractId(id);
				//合同完成率
				List<ContractFinishInfo> contractFinishInfos = contractFinishInfoRepository.findAllByContractIdAndCreateTimeBefore(id, endTime);
				if(contractFinishInfos != null && contractFinishInfos.size() > 0){
					ContractFinishInfo contractFinishInfo = contractFinishInfos.get(contractFinishInfos.size() - 1);
					contractWeeklyStat.setFinishRate(contractFinishInfo.getFinishRate());
				}else{
					log.error("no finish rate found belong to " + contractInfo.getName());
					contractWeeklyStat.setFinishRate(0D);
				}
				//合同回款总额
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndCreateTimeBefore(id, endTime);
				Double receiveTotal = 0D;
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						receiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.error("no contract receives found belong to " + contractInfo.getName());
				}
				contractWeeklyStat.setReceiveTotal(receiveTotal);
				
				if(contractInfo.getType() != TYPE_CONTRACT_PUBLIC){//正常合同
					//销售人工成本
					Double salesHumanCost = 0D;
					//销售报销成本
					Double salesPayment = 0D;
					List<Long> deptIds = deptInfoRepository.findIdsByType(TYPE_DEPT_SALES);
					if(deptIds != null && deptIds.size() > 0){
						List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts != null && contractCosts.size() > 0){
							for(ContractCost contractCost : contractCosts){
								salesHumanCost += contractCost.getTotal();
							}
						}
						List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts2 != null && contractCosts2.size() >0){
							for(ContractCost contractCost : contractCosts2){
								salesPayment += contractCost.getTotal();
							}
						}
					}else{
						log.error("no sales deptInfo found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setSalesHumanCost(salesHumanCost);
					contractWeeklyStat.setSalesPayment(salesPayment);
					//咨询人工成本
					Double consultHumanCost = 0D;
					//咨询报销成本
					Double consultPayment = 0D;
					List<Long> deptIds2 = deptInfoRepository.findIdsByType(TYPE_DEPT_CONSULT);
					if(deptIds2 != null && deptIds2.size() > 0){
						List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds2, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts != null && contractCosts.size() > 0){
							for(ContractCost contractCost : contractCosts){
								consultHumanCost += contractCost.getTotal();
							}
						}
						List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds2, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts2 != null && contractCosts2.size() >0){
							for(ContractCost contractCost : contractCosts2){
								consultPayment += contractCost.getTotal();
							}
						}
					}else{
						log.error("no consult deptInfo found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setConsultHumanCost(consultHumanCost);
					contractWeeklyStat.setConsultPayment(consultPayment);
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndType(id, TYPE_PURCHASE_ITEM_HARDWARE);
					Double hardwarePurchase = 0D;
					if(purchaseItems != null && purchaseItems.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems){
							hardwarePurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no hardware purchase item found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndType(id, SOURCE_PURCHASE_ITEM_EXTERNAL, TYPE_PURCHASE_ITEM_SOFTWARE);
					Double externalSoftware = 0D;
					if(purchaseItems2 != null && purchaseItems2.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems2){
							externalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no external software purchase item found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndType(id, SOURCE_PURCHASE_ITEM_INTERNAL, TYPE_PURCHASE_ITEM_SOFTWARE);
					Double internalSoftware = 0D;
					if(purchaseItems3 != null && purchaseItems3.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems3){
							internalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no internal software purchase item found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setInternalSoftware(internalSoftware);
					//项目人工成本
					Double projectHumanCost = 0D;
//					//公共成本
//					if(userTimesheets4 != null && userTimesheets4.size() > 0){
//						Integer ctype = deptInfoRepository.findTypeByContractId(contractInfo.getId());
//						for(UserTimesheet userTimesheet : userTimesheets4){
//							Long uid = userTimesheet.getUserId();
//							Integer utype = deptInfoRepository.findTypeByUid(uid);
//							if(utype==ctype){
//								UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(dates[6]), userTimesheet.getUserId());
//								if(contractInfo.getType() == TYPE_CONTRACT_INTERNAL){
//									projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
//								}else if(contractInfo.getType() == TYPE_CONTRACT_EXTERNAL){
//									projectHumanCost += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
//								}else{
//									log.error(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
//								}
//							}
//						}
//					}else{
//						log.error(" no user Timesheets founded for Public userTimesheet cost belong to " + contractInfo.getName());
//					}
					//项目报销成本
					Double projectPayment = 0D;
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(id);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							List<UserTimesheet> userTimesheets3 = userTimesheetRepository.
									findByDateAndObjIdAndType(StringUtil.nullToLong(dates[6]), projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
							if(userTimesheets3 != null && userTimesheets3.size() > 0){
								for(UserTimesheet userTimesheet : userTimesheets3){
									UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(dates[6]), userTimesheet.getUserId());
									if(userCost != null){
										if(contractInfo.getType() == TYPE_CONTRACT_INTERNAL){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
										}else if(contractInfo.getType() == TYPE_CONTRACT_EXTERNAL){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
										}else{
											log.error(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
										}
									}else{
										log.error("no UserCost founded belong to User : " + userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
									}
									
								}
							}else{
								log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getName());
							}
							//报销成本
							List<ProjectCost> projectCosts2 = projectCostRepository.findAllByProjectIdAndNoType(projectInfo.getId(), TYPE_PROJECT_COST_HUMAN_COST);
							if(projectCosts2 != null && projectCosts2.size() > 0){
								for(ProjectCost projectCost : projectCosts2){
									projectPayment += projectCost.getTotal();
								}
							}
						}
					}else{
						log.error("no project found belong to " + contractInfo.getName());
					}
					contractWeeklyStat.setProjectHumanCost(projectHumanCost);
					contractWeeklyStat.setProjectPayment(projectPayment);
					//所有成本
					Double costTotal = salesHumanCost + salesPayment + consultHumanCost + consultPayment + hardwarePurchase + externalSoftware
							+ internalSoftware + projectHumanCost + projectPayment + StringUtil.nullToDouble(contractInfo.getShareCost()) + StringUtil.nullToDouble(contractInfo.getTaxes());
					contractWeeklyStat.setCostTotal(costTotal);
					//合同毛利
					Double grossProfit = receiveTotal - costTotal;
					contractWeeklyStat.setGrossProfit(grossProfit);
				}else{ //公共合同
					//项目人工成本
					Double projectHumanCost = 0D;
					List<ContractCost> contractCosts = contractCostRepository.findByContractIdAndType(id, TYPE_CONTRACT_COST_HUMAN_COST);
					for(ContractCost contractCost : contractCosts){
						projectHumanCost += contractCost.getTotal();
					}
					contractWeeklyStat.setProjectHumanCost(projectHumanCost);
					Double costTotal = projectHumanCost;
					contractWeeklyStat.setCostTotal(costTotal);
					//合同毛利
					Double grossProfit = receiveTotal - costTotal;
					contractWeeklyStat.setGrossProfit(grossProfit);
				}
				//统计周
				contractWeeklyStat.setStatWeek(Long.parseLong(dates[6]));
				//统计日期
				contractWeeklyStat.setCreateTime(ZonedDateTime.now());
				contractWeeklyStatRepository.save(contractWeeklyStat);
				log.info(" =======contract : "+contractInfo.getName()+" weekly stat saved======= ");
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Weekly Stat=====");
	}
	
	@Scheduled(cron = "0 0 22 1 * ?")
	protected void generateContractMonthlyStat(){
		log.info("=====begin generate Contract Monthly Stat=====");
//		List<ContractInfo> contractInfos = contractInfoRepository.findAll();
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonthBegin().getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastMonthend().getTime());
		String fDay = DateUtil.getFirstDayOfLastMonth("yyyyMMdd");
		String lDay = DateUtil.getLastDayOfLastMonth("yyyyMMdd");
		String lMonth = DateUtil.getLastDayOfLastMonth("yyyyMM");
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrUpdateTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
//			//公共成本日报列表
//			List<UserTimesheet> userTimesheets4 = userTimesheetRepository.findByDateAndType(StringUtil.nullToLong(lDay), UserTimesheet.TYPE_PUBLIC);
			for(ContractInfo contractInfo : contractInfos){
				log.info("=====begin generate Contract : "+contractInfo.getName()+"=======");
				//初始contractCost
				try {
					while(true){
						StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(contractInfo.getId(), StatIdentify.TYPE_CONTRACT);
						if(statIdentify != null){
							Integer status = statIdentify.getStatus();
							if(status == StatIdentify.STATUS_UNAVALIABLE){
								log.info("====waiting for statIdentfiy belong to project : " + contractInfo.getSerialNum());
								Thread.sleep(5*1000);
							}else{
								initIdentify(contractInfo);
								break;
							}
						}else{
							initIdentify(contractInfo);
							break;
						}
					}
					ContractCost contractCost2 = contractCostRepository.findMaxByContractIdAndCostDay(contractInfo.getId(), StringUtil.nullToLong(fDay), StringUtil.nullToLong(lDay));
					if(contractCost2 != null){
						Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", contractCost2.getCostDay().toString()));
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, initDate, DateUtil.lastMonthend());
						}else{
							initContractHumanCost(contractInfo, initDate, Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}else{
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, DateUtil.lastMonthBegin(), DateUtil.lastMonthend());
						}else{
							initContractHumanCost(contractInfo, DateUtil.lastMonthBegin(), Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					overIdentify(contractInfo);
				}
				//初始monthlystat
				List<ContractMonthlyStat> contractMonthlyStats = contractMonthlyStatRepository.findByStatWeekAndContractId(StringUtil.nullToLong(lMonth), contractInfo.getId());
				if(contractMonthlyStats != null && contractMonthlyStats.size() > 0){
					for(ContractMonthlyStat contractMonthlyStat : contractMonthlyStats){
						contractMonthlyStatRepository.delete(contractMonthlyStat);
					}
				}
				
				ContractMonthlyStat contractMonthlyStat = new ContractMonthlyStat();
				Long id = contractInfo.getId();
				//合同id
				contractMonthlyStat.setContractId(id);
				//合同完成率
				List<ContractFinishInfo> contractFinishInfos = contractFinishInfoRepository.findAllByContractIdAndCreateTimeBefore(id, endTime);
				if(contractFinishInfos != null && contractFinishInfos.size() > 0){
					ContractFinishInfo contractFinishInfo = contractFinishInfos.get(contractFinishInfos.size() - 1);
					contractMonthlyStat.setFinishRate(contractFinishInfo.getFinishRate());
				}else{
					log.error("no finish rate found belong to " + contractInfo.getName());
					contractMonthlyStat.setFinishRate(0D);
				}
				//合同回款总额
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndCreateTimeBefore(id, endTime);
				Double receiveTotal = 0D;
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						receiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.error("no contract receives found belong to " + contractInfo.getName());
				}
				contractMonthlyStat.setReceiveTotal(receiveTotal);
				
				if(contractInfo.getType() != TYPE_CONTRACT_PUBLIC){//正常合同
					//销售人工成本
					Double salesHumanCost = 0D;
					//销售报销成本
					Double salesPayment = 0D;
					List<Long> deptIds = deptInfoRepository.findIdsByType(TYPE_DEPT_SALES);
					if(deptIds != null && deptIds.size() > 0){
						List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts != null && contractCosts.size() >0){
							for(ContractCost contractCost : contractCosts){
								salesHumanCost += contractCost.getTotal();
							}
						}
						List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts2 != null && contractCosts2.size() >0){
							for(ContractCost contractCost : contractCosts2){
								salesPayment += contractCost.getTotal();
							}
						}
					}else{
						log.error("no sales deptInfo found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setSalesHumanCost(salesHumanCost);
					contractMonthlyStat.setSalesPayment(salesPayment);
					
					//咨询人工成本
					Double consultHumanCost = 0D;
					//咨询报销成本
					Double consultPayment = 0D;
					List<Long> deptIds2 = deptInfoRepository.findIdsByType(TYPE_DEPT_CONSULT);
					if(deptIds2 != null && deptIds2.size() > 0){
						List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds2, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts != null && contractCosts.size() >0){
							for(ContractCost contractCost : contractCosts){
								consultHumanCost += contractCost.getTotal();
							}
						}
						List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds2, TYPE_CONTRACT_COST_HUMAN_COST, id);
						if(contractCosts2 != null && contractCosts2.size() >0){
							for(ContractCost contractCost : contractCosts2){
								consultPayment += contractCost.getTotal();
							}
						}
					}else{
						log.error("no consult deptInfo found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setConsultHumanCost(consultHumanCost);
					contractMonthlyStat.setConsultPayment(consultPayment);
					
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndType(id, TYPE_PURCHASE_ITEM_HARDWARE);
					Double hardwarePurchase = 0D;
					if(purchaseItems != null && purchaseItems.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems){
							hardwarePurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no hardware purchase item found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndType(id, SOURCE_PURCHASE_ITEM_EXTERNAL, TYPE_PURCHASE_ITEM_SOFTWARE);
					Double externalSoftware = 0D;
					if(purchaseItems2 != null && purchaseItems2.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems2){
							externalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no external software purchase item found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndType(id, SOURCE_PURCHASE_ITEM_INTERNAL, TYPE_PURCHASE_ITEM_HARDWARE);
					Double internalSoftware = 0D;
					if(purchaseItems3 != null && purchaseItems3.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems3){
							internalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no internal software purchase item found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setInternalSoftware(internalSoftware);
					
					//项目人工成本
					Double projectHumanCost = 0D;
					//项目报销成本
					Double projectPayment = 0D;
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(id);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							List<UserTimesheet> userTimesheets3 = userTimesheetRepository.
									findByDateAndObjIdAndType( StringUtil.nullToLong(lDay), projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
							if(userTimesheets3 != null && userTimesheets3.size() > 0){
								for(UserTimesheet userTimesheet : userTimesheets3){
									UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(lDay), userTimesheet.getUserId());
									if(contractInfo.getType() == TYPE_CONTRACT_INTERNAL){
										projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/22.5/8);
									}else if(contractInfo.getType() == TYPE_CONTRACT_EXTERNAL){
										projectHumanCost += userTimesheet.getRealInput() * (userCost.getExternalCost()/22.5/8);
									}else{
										log.error(" no contractType found belong to UserTimesheet : " + userTimesheet.getId());
									}
								}
							}else{
								log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getName());
							}
							//报销成本
							List<ProjectCost> projectCosts2 = projectCostRepository.findAllByProjectIdAndNoType(projectInfo.getId(), TYPE_PROJECT_COST_HUMAN_COST);
							if(projectCosts2 != null && projectCosts2.size() > 0){
								for(ProjectCost projectCost : projectCosts2){
									projectPayment += projectCost.getTotal();
								}
							}
						}
					}else{
						log.error("no project found belong to " + contractInfo.getName());
					}
					contractMonthlyStat.setProjectHumanCost(projectHumanCost);
					contractMonthlyStat.setProjectPayment(projectPayment);
					//所有成本
					Double costTotal = salesHumanCost + salesPayment + consultHumanCost + consultPayment + hardwarePurchase + externalSoftware
							+ internalSoftware + projectHumanCost + projectPayment + StringUtil.nullToDouble(contractInfo.getShareCost()) + StringUtil.nullToDouble(contractInfo.getTaxes());
					contractMonthlyStat.setCostTotal(costTotal);
					//合同毛利
					Double grossProfit = receiveTotal - costTotal;
					contractMonthlyStat.setGrossProfit(grossProfit);
				}else{//公共合同
					//项目人工成本
					Double projectHumanCost = 0D;
					List<ContractCost> contractCosts = contractCostRepository.findByContractIdAndType(id, TYPE_CONTRACT_COST_HUMAN_COST);
					for(ContractCost contractCost : contractCosts){
						projectHumanCost += contractCost.getTotal();
					}
					contractMonthlyStat.setProjectHumanCost(projectHumanCost);
					Double costTotal = projectHumanCost;
					contractMonthlyStat.setCostTotal(costTotal);
					//合同毛利
					Double grossProfit = receiveTotal - costTotal;
					contractMonthlyStat.setGrossProfit(grossProfit);
					
				}
				//统计周
				contractMonthlyStat.setStatWeek(StringUtil.nullToLong(lMonth));
				//统计日期
				contractMonthlyStat.setCreateTime(ZonedDateTime.now());
				contractMonthlyStatRepository.save(contractMonthlyStat);
				log.info(" =======contract : "+contractInfo.getName()+" monthly stat saved======= ");
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Monthly Stat=====");
	}
	
	private void initContractHumanCost(ContractInfo contractInfo, Date beginTime, Date endTime){
		if(endTime.getTime() < beginTime.getTime()){
			return;
		}
		Long countDay = (endTime.getTime() - beginTime.getTime())/(24*60*60*1000);
		Integer contractType = contractInfo.getType();
		Date currentDay = beginTime;
		for(int i = 0; i <= countDay; i++){
			Long workDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", currentDay).toString());
			ContractCost contractCost = new ContractCost();
			contractCost.setContractId(contractInfo.getId());
			if(contractInfo.getDeptId() != null && contractInfo.getDeptId() != 0 && contractInfo.getConsultantsDeptId() != null && contractInfo.getConsultantsDeptId() != 0){
				//销售部分
				contractCost.setDeptId(contractInfo.getDeptId());
				DeptInfo deptInfo = deptInfoRepository.findOne(contractInfo.getDeptId());
				contractCost.setDept(deptInfo.getName());
				Double total = 0D;
				List<UserTimesheet> userTimesheets = new ArrayList<>();
				if(contractInfo.getType() == TYPE_CONTRACT_PUBLIC){
					userTimesheets = userTimesheetRepository.findByWorkDayAndNotDeptTypeAndType(workDay, TYPE_DEPT_CONSULT, UserTimesheet.TYPE_PUBLIC);
				}else{
					userTimesheets = userTimesheetRepository.findByWorkDayAndNotDeptTypeAndObjIdAndType(workDay, TYPE_DEPT_CONSULT, contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				
				if(userTimesheets != null && userTimesheets.size() > 0){
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
							log.error(" no UserCost founded belong to User : " + userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
						}
						
					}
				}
				contractCost.setTotal(total);
				//咨询部分
				ContractCost contractCost2 = new ContractCost();
				contractCost2.setContractId(contractInfo.getId());
				contractCost2.setDeptId(contractInfo.getConsultantsDeptId());
				DeptInfo deptInfo2 = deptInfoRepository.findOne(contractInfo.getConsultantsDeptId());
				contractCost2.setDept(deptInfo2.getName());
				Double total2 = 0D;
				List<UserTimesheet> userTimesheets2 = new ArrayList<>();
				if(contractInfo.getType() == TYPE_CONTRACT_PUBLIC){
					userTimesheets2 = userTimesheetRepository.findByWorkDayAndDeptTypeAndType(workDay, TYPE_DEPT_CONSULT, UserTimesheet.TYPE_PUBLIC);
				}else{
					userTimesheets2 = userTimesheetRepository.findByWorkDayAndDeptTypeAndObjIdAndType(workDay, TYPE_DEPT_CONSULT, contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				if(userTimesheets2 != null && userTimesheets2.size() > 0){
					for(UserTimesheet userTimesheet : userTimesheets2){
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
							log.error(" no UserCost Founded belong to User " + userTimesheet.getUserId() +":" +userTimesheet.getUserName());
						}
						
					}
				}
				contractCost2.setTotal(total2);
				contractCost2.setName(contractInfo.getSerialNum() + "-humanCost-" +  DateUtil.formatDate("yyyyMMdd", currentDay).toString());
				contractCost2.setType(TYPE_CONTRACT_COST_HUMAN_COST);
				contractCost2.setCostDesc(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
				contractCost2.setStatus(1);
				contractCost2.setCreator("admin");
				contractCost2.setCreateTime(ZonedDateTime.now());
				contractCost2.setUpdator("admin");
				contractCost2.setUpdateTime(ZonedDateTime.now());
				contractCost2.setCostDay(workDay);
				contractCostRepository.save(contractCost2);
			}else{
				if(contractInfo.getDeptId() == null || contractInfo.getDeptId() == 0){
					contractCost.setDeptId(contractInfo.getConsultantsDeptId());
					DeptInfo deptInfo = deptInfoRepository.findOne(contractInfo.getConsultantsDeptId());
					contractCost.setDept(deptInfo.getName());
				}else if(contractInfo.getConsultantsDeptId() == null || contractInfo.getConsultantsDeptId() == 0){
					contractCost.setDeptId(contractInfo.getDeptId());
					DeptInfo deptInfo = deptInfoRepository.findOne(contractInfo.getDeptId());
					contractCost.setDept(deptInfo.getName());;
				}
				List<UserTimesheet> userTimesheets = new ArrayList<>();
				if(contractInfo.getType() == TYPE_CONTRACT_PUBLIC){
					if(contractInfo.getDeptId() != null && contractInfo.getDeptId() != 0){
						userTimesheets = userTimesheetRepository.findByWorkDayAndNotDeptTypeAndType(workDay, TYPE_DEPT_CONSULT, UserTimesheet.TYPE_PUBLIC);
					}else{
						userTimesheets = userTimesheetRepository.findByWorkDayAndDeptTypeAndType(workDay, TYPE_DEPT_CONSULT, UserTimesheet.TYPE_PUBLIC);
					}
				}else{
					userTimesheets = userTimesheetRepository.findByWorkDayAndObjIdAndType(workDay, contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				Double total = 0D;
				if(userTimesheets != null && userTimesheets.size() > 0){
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
							log.info("no userCost founded belong to User : "+ userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
						}
					}
				}
				contractCost.setTotal(total);
			}
			contractCost.setName(contractInfo.getSerialNum() + "-humanCost-" +  DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			contractCost.setType(TYPE_CONTRACT_COST_HUMAN_COST);
			contractCost.setCostDesc(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			contractCost.setStatus(1);
			contractCost.setCreator("admin");
			contractCost.setCreateTime(ZonedDateTime.now());
			contractCost.setUpdator("admin");
			contractCost.setUpdateTime(ZonedDateTime.now());
			contractCost.setCostDay(workDay);
			contractCostRepository.save(contractCost);
			currentDay = new Date(currentDay.getTime() + (24*60*60*1000));
		}
		
	}
	
	private void initIdentify(ContractInfo contractInfo){
		StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(contractInfo.getId(), StatIdentify.TYPE_CONTRACT);
		if(statIdentify == null){
			StatIdentify st = new StatIdentify();
			st.setObjId(contractInfo.getId());
			st.setType(StatIdentify.TYPE_CONTRACT);
			st.setStatus(StatIdentify.STATUS_UNAVALIABLE);
			statIdentifyRepository.saveAndFlush(st);
		}else{
			statIdentify.setStatus(StatIdentify.STATUS_UNAVALIABLE);
			statIdentifyRepository.saveAndFlush(statIdentify);
		}
	}
	
	private void overIdentify(ContractInfo contractInfo){
		StatIdentify statIdentify = statIdentifyRepository.findByObjIdAndType(contractInfo.getId(), StatIdentify.TYPE_CONTRACT);
		if(statIdentify == null){
			StatIdentify st = new StatIdentify();
			st.setObjId(contractInfo.getId());
			st.setType(StatIdentify.TYPE_CONTRACT);
			st.setStatus(StatIdentify.STATUS_AVALIABLE);
			statIdentifyRepository.saveAndFlush(st);
		}else{
			statIdentify.setStatus(StatIdentify.STATUS_AVALIABLE);
			statIdentifyRepository.saveAndFlush(statIdentify);
		}
	}
	
}
