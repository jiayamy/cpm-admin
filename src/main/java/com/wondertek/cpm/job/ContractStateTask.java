package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.wondertek.cpm.domain.ContractCost;
import com.wondertek.cpm.domain.ContractFinishInfo;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.domain.ProjectCost;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.SaleWeeklyStat;
import com.wondertek.cpm.domain.SalesAnnualIndex;
import com.wondertek.cpm.domain.StatIdentify;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.ContractCostRepository;
import com.wondertek.cpm.repository.ContractFinishInfoRepository;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ContractMonthlyStatRepository;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.ContractWeeklyStatRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.ExternalQuotationRepository;
import com.wondertek.cpm.repository.ProjectCostRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.SaleWeeklyStatRepository;
import com.wondertek.cpm.repository.SalesAnnualIndexRepository;
import com.wondertek.cpm.repository.StatIdentifyRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class ContractStateTask {

	private Logger log = LoggerFactory.getLogger(ContractStateTask.class);
	
	private Map<Long, Integer> userIdGradeMap = new HashMap<>();
	
	private Map<Integer, Double> externalQuotationMap = new HashMap<>();
	
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
	private UserRepository userRepository;
	
	@Inject
	private StatIdentifyRepository statIdentifyRepository;
	
	@Inject
	private ExternalQuotationRepository externalQuotationRepository;
	
	@Inject
	private SaleWeeklyStatRepository saleWeeklyStatRepository;
	
	@Inject
	private SalesAnnualIndexRepository salesAnnualIndexRepository;
	
	private void init(){
		List<ExternalQuotation> externalQuotations = externalQuotationRepository.findAll();
		if(externalQuotations != null && externalQuotations.size() > 0){
			for(ExternalQuotation externalQuotation : externalQuotations){
				externalQuotationMap.put(externalQuotation.getGrade(), externalQuotation.getHourCost());
			}
		}
		List<User> users = userRepository.findAll();
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
	
	@Scheduled(cron = "0 0 23 ? * MON")
	protected void generateContractWeeklyStat(){
		Date now = new Date();
		generateContractWeeklyStat(now);
	}
	protected void generateContractWeeklyStat(Date now){
		log.info("=====begin generate Contract Weekly Stat=====");
		init();
		
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday(now));
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd(now).getTime());
		Long fDay = StringUtil.nullToLong(dates[0]);
		Long statWeek = StringUtil.nullToLong(dates[6]);
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrEndTime(ContractInfo.STATUS_VALIDABLE, beginTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				log.info("=========begin generate Contract : "+contractInfo.getSerialNum()+"=========");
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
					ContractCost contractCost2 = contractCostRepository.findMaxByContractIdAndCostDayAndType(contractInfo.getId(), fDay, statWeek, ContractCost.TYPE_HUMAN_COST);
					if(contractCost2 != null){
						Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", contractCost2.getCostDay().toString()));
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, initDate, DateUtil.lastSundayEnd(now));
						}else{
							initContractHumanCost(contractInfo, initDate, Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}else{
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, DateUtil.lastMonday(now), DateUtil.lastSundayEnd(now));
						}else{
							initContractHumanCost(contractInfo, DateUtil.lastMonday(now), Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					overIdentify(contractInfo);
				}
				//初始上周stat
				List<ContractWeeklyStat> contractWeeklyStats = contractWeeklyStatRepository.findByStatWeekAndContractId(statWeek, contractInfo.getId());
				if(contractWeeklyStats != null){
					contractWeeklyStatRepository.delete(contractWeeklyStats);
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
					log.error("no finish rate found belong to " + contractInfo.getSerialNum());
					contractWeeklyStat.setFinishRate(0D);
				}
				//合同回款总额
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndReceiveDayBefore(id, statWeek);
				Double receiveTotal = 0D;
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						receiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.error("no contract receives found belong to " + contractInfo.getSerialNum());
				}
				contractWeeklyStat.setReceiveTotal(receiveTotal);
				
				if(contractInfo.getType() != ContractInfo.TYPE_PUBLIC){//正常合同
					//销售人工成本
					Double salesHumanCost = 0D;
					//销售报销成本
					Double salesPayment = 0D;
					Long salesDeptId = contractInfo.getDeptId();
					if(salesDeptId != null && salesDeptId != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(salesDeptId);
						List<Long> deptIds = deptInfoRepository.findIdsByType(StringUtil.nullToLong(deptInfo.getType()));
						if(deptIds != null && deptIds.size() > 0){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts != null && contractCosts.size() > 0){
								for(ContractCost contractCost : contractCosts){
									salesHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									salesPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no sales deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractWeeklyStat.setSalesHumanCost(salesHumanCost);
					contractWeeklyStat.setSalesPayment(salesPayment);
					//咨询人工成本
					Double consultHumanCost = 0D;
					//咨询报销成本
					Double consultPayment = 0D;
					Long consultDeptId = contractInfo.getConsultantsDeptId();
					if(consultDeptId != null && consultDeptId != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(consultDeptId);
						List<Long> deptIds2 = deptInfoRepository.findIdsByType(StringUtil.nullToLong(deptInfo.getType()));
						if(deptIds2 != null && deptIds2.size() > 0){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds2, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts != null && contractCosts.size() > 0){
								for(ContractCost contractCost : contractCosts){
									consultHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds2, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									consultPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractWeeklyStat.setConsultHumanCost(consultHumanCost);
					contractWeeklyStat.setConsultPayment(consultPayment);
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndType(id, PurchaseItem.TYPE_HARDWARE);
					Double hardwarePurchase = 0D;
					if(purchaseItems != null && purchaseItems.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems){
							hardwarePurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no hardware purchase item found belong to " + contractInfo.getSerialNum());
					}
					contractWeeklyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndType(id, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE);
					Double externalSoftware = 0D;
					if(purchaseItems2 != null && purchaseItems2.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems2){
							externalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no external software purchase item found belong to " + contractInfo.getSerialNum());
					}
					contractWeeklyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndType(id, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE);
					Double internalSoftware = 0D;
					if(purchaseItems3 != null && purchaseItems3.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems3){
							internalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no internal software purchase item found belong to " + contractInfo.getSerialNum());
					}
					contractWeeklyStat.setInternalSoftware(internalSoftware);
					//项目人工成本
					Double projectHumanCost = 0D;
					//项目报销成本
					Double projectPayment = 0D;
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(id);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							List<UserTimesheet> userTimesheets3 = userTimesheetRepository.
									findByDateAndObjIdAndType(statWeek, projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
							if(userTimesheets3 != null && userTimesheets3.size() > 0){
								for(UserTimesheet userTimesheet : userTimesheets3){
									Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd(now)).toString());
									UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
									if(userCost != null){
										if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
										}else{
											projectHumanCost += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
										}
									}else{
										log.error("no UserCost founded belong to User : " + userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
									}
									
								}
							}else{
								log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getSerialNum());
							}
							//报销成本
							List<ProjectCost> projectCosts2 = projectCostRepository.findAllByProjectIdAndNoType(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST);
							if(projectCosts2 != null && projectCosts2.size() > 0){
								for(ProjectCost projectCost : projectCosts2){
									projectPayment += projectCost.getTotal();
								}
							}
						}
					}else{
						log.error("no project found belong to " + contractInfo.getSerialNum());
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
					List<ContractCost> contractCosts = contractCostRepository.findByContractIdAndType(id, ContractCost.TYPE_HUMAN_COST);
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
				log.info(" =======contract : "+contractInfo.getSerialNum()+" weekly stat saved======= ");
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Weekly Stat=====");
	}
	
	@Scheduled(cron = "0 30 23 1 * ?")
	protected void generateContractMonthlyStat(){
		Date now = new Date();
		generateContractMonthlyStat(now);
	}
	
	protected void generateContractMonthlyStat(Date now){
		log.info("=====begin generate Contract Monthly Stat=====");
		init();
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonthBegin(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastMonthend(now).getTime());
		String fDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthBegin(now));
		String lDay = DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthend(now));
		String lMonth = DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now));
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrEndTime(ContractInfo.STATUS_VALIDABLE, beginTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				log.info("=====begin generate Contract : "+contractInfo.getSerialNum()+"=======");
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
					ContractCost contractCost2 = contractCostRepository.findMaxByContractIdAndCostDayAndType(contractInfo.getId(), StringUtil.nullToLong(fDay), StringUtil.nullToLong(lDay), ContractCost.TYPE_HUMAN_COST);
					if(contractCost2 != null){
						Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", contractCost2.getCostDay().toString()));
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, initDate, DateUtil.lastMonthend(now));
						}else{
							initContractHumanCost(contractInfo, initDate, Date.from(contractInfo.getUpdateTime().toInstant()));
						}
					}else{
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							initContractHumanCost(contractInfo, DateUtil.lastMonthBegin(now), DateUtil.lastMonthend(now));
						}else{
							initContractHumanCost(contractInfo, DateUtil.lastMonthBegin(now), Date.from(contractInfo.getUpdateTime().toInstant()));
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
					log.error("no finish rate found belong to " + contractInfo.getSerialNum());
					contractMonthlyStat.setFinishRate(0D);
				}
				//合同回款总额
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndReceiveDayBefore(id, StringUtil.nullToLong(lDay));
				Double receiveTotal = 0D;
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						receiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.error("no contract receives found belong to " + contractInfo.getSerialNum());
				}
				contractMonthlyStat.setReceiveTotal(receiveTotal);
				
				if(contractInfo.getType() != ContractInfo.TYPE_PUBLIC){//正常合同
					//销售人工成本
					Double salesHumanCost = 0D;
					//销售报销成本
					Double salesPayment = 0D;
					Long salesDeptId = contractInfo.getDeptId();
					if(salesDeptId != null && salesDeptId != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(salesDeptId);
						List<Long> deptIds = deptInfoRepository.findIdsByType(StringUtil.nullToLong(deptInfo.getType()));
						if(deptIds != null && deptIds.size() > 0){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts != null && contractCosts.size() >0){
								for(ContractCost contractCost : contractCosts){
									salesHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									salesPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no sales deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractMonthlyStat.setSalesHumanCost(salesHumanCost);
					contractMonthlyStat.setSalesPayment(salesPayment);
					
					//咨询人工成本
					Double consultHumanCost = 0D;
					//咨询报销成本
					Double consultPayment = 0D;
					Long consultDeptId = contractInfo.getConsultantsDeptId();
					if(consultDeptId != null && consultDeptId != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(consultDeptId);
						List<Long> deptIds2 = deptInfoRepository.findIdsByType(StringUtil.nullToLong(deptInfo.getType()));
						if(deptIds2 != null && deptIds2.size() > 0){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdsAndTypeAndContractId(deptIds2, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts != null && contractCosts.size() >0){
								for(ContractCost contractCost : contractCosts){
									consultHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdsAndNoTypeAndContractId(deptIds2, ContractCost.TYPE_HUMAN_COST, id);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									consultPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractMonthlyStat.setConsultHumanCost(consultHumanCost);
					contractMonthlyStat.setConsultPayment(consultPayment);
					
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndType(id, PurchaseItem.TYPE_HARDWARE);
					Double hardwarePurchase = 0D;
					if(purchaseItems != null && purchaseItems.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems){
							hardwarePurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no hardware purchase item found belong to " + contractInfo.getSerialNum());
					}
					contractMonthlyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndType(id, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE);
					Double externalSoftware = 0D;
					if(purchaseItems2 != null && purchaseItems2.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems2){
							externalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no external software purchase item found belong to " + contractInfo.getSerialNum());
					}
					contractMonthlyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndType(id, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_HARDWARE);
					Double internalSoftware = 0D;
					if(purchaseItems3 != null && purchaseItems3.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems3){
							internalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no internal software purchase item found belong to " + contractInfo.getSerialNum());
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
									UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(lMonth), userTimesheet.getUserId());
									if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
										projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
									}else{
										projectHumanCost += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
									}
								}
							}else{
								log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getSerialNum());
							}
							//报销成本
							List<ProjectCost> projectCosts2 = projectCostRepository.findAllByProjectIdAndNoType(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST);
							if(projectCosts2 != null && projectCosts2.size() > 0){
								for(ProjectCost projectCost : projectCosts2){
									projectPayment += projectCost.getTotal();
								}
							}
						}
					}else{
						log.error("no project found belong to " + contractInfo.getSerialNum());
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
					List<ContractCost> contractCosts = contractCostRepository.findByContractIdAndType(id, ContractCost.TYPE_HUMAN_COST);
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
				log.info(" =======contract : "+contractInfo.getSerialNum()+" monthly stat saved======= ");
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Monthly Stat=====");
	}
	
	/**
	 * 每周 汇总大销售部门下的子销售部门下面 一年内所有销售的合同情况。
	 */
	@Scheduled(cron = "0 59 23 ? * MON")
//	@Scheduled(cron = "0 25 17 ? * WED")
	protected void generateSaleContractWeeklyStat(){
		Date now = new Date();
		generateSaleContractWeeklyStat(now);
	}
	
	protected void generateSaleContractWeeklyStat(Date now){
		log.info("=====begin generate Sale Contract Weekly Stat=====");
		init();
		
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday(now));
		//ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(now).getTime());	//上周开始时间
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd(now).getTime());	//上周结束时间
		//Long fDay = StringUtil.nullToLong(dates[0]);
		Long statWeek = StringUtil.nullToLong(dates[6]);//上周日
		//该年第一天
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		ZonedDateTime firstDayOfYear = DateUtil.getZonedDateTime(calendar.getTime().getTime());
		
		//归属于销售部门下的合同
		List<ContractInfo> contractInfos = contractInfoRepository.findByDeptIdAndEndTime(StringUtil.nullToLong(DeptType.TYPE_DEPT_SALE), firstDayOfYear);
		if(contractInfos != null && contractInfos.size() > 0){
			//初始上周stat
			List<SaleWeeklyStat> saleWeeklyStats = saleWeeklyStatRepository.findByStatWeek(statWeek);
			if(saleWeeklyStats != null){
				saleWeeklyStatRepository.delete(saleWeeklyStats);
			}
			//销售部门id
			List<Long> saleDeptIds = deptInfoRepository.findIdsByType(StringUtil.nullToLong(DeptType.TYPE_DEPT_SALE));
			//所有销售部门
			List<DeptInfo> saleDeptInfos = deptInfoRepository.findDeptInfosByType(StringUtil.nullToLong(DeptType.TYPE_DEPT_SALE));
			Map<Long,DeptInfo> saleDeptInfosMap = new HashMap<Long,DeptInfo>();
			if (saleDeptInfos != null) {
				for (DeptInfo info : saleDeptInfos) {
					saleDeptInfosMap.put(info.getId(), info);
				} 
			}
			//大销售部门下的一级销售部门
			List<DeptInfo> primarySaleDeptInfos = deptInfoRepository.findByIdPath(saleDeptInfos.get(0).getIdPath() + saleDeptInfos.get(0).getId() + "/");
			Map<Long,DeptInfo> primarySaleDeptInfosMap = new HashMap<Long,DeptInfo>();
			if (primarySaleDeptInfos != null) {
				for (DeptInfo info : primarySaleDeptInfos) {
					primarySaleDeptInfosMap.put(info.getId(), info);
				} 
			}
			//每个一级部门下的所有子销售部门
			Map<Long,List<DeptInfo>> childrenSaleDeptInfosMap = new HashMap<Long,List<DeptInfo>>();
			Map<Long,List<Long>> childrenSaleDeptIdsMap = new HashMap<Long,List<Long>>();
			for(Long key : primarySaleDeptInfosMap.keySet()){
				List<DeptInfo> childrenSaleDeptInfos = deptInfoRepository.findByIdPath(primarySaleDeptInfosMap.get(key).getIdPath() + primarySaleDeptInfosMap.get(key).getId() + "/%");
				if(childrenSaleDeptInfos != null){
					childrenSaleDeptInfosMap.put(key, childrenSaleDeptInfos);
					for(DeptInfo info : childrenSaleDeptInfos){
						if(childrenSaleDeptIdsMap.keySet().contains(key)){
							childrenSaleDeptIdsMap.get(key).add(info.getId());
							childrenSaleDeptIdsMap.put(key, childrenSaleDeptIdsMap.get(key));
						}else{
							List<Long> list = new ArrayList<Long>();
							list.add(info.getId());
							childrenSaleDeptIdsMap.put(key, list);
						}
					}
				}
			}
			
			Map<Long,SaleWeeklyStat> saleWeeklyStatsMap = new HashMap<Long,SaleWeeklyStat>();
			
			for(ContractInfo contractInfo : contractInfos){
				log.info("=========begin generate Sale Contract : "+contractInfo.getSerialNum()+"=========");
				SaleWeeklyStat saleWeeklyStat = new SaleWeeklyStat();
				//合同id
				Long contractId = contractInfo.getId();
				//年份
				Calendar cal = Calendar.getInstance();
				cal.setTime(now);
				saleWeeklyStat.setOriginYear((long)cal.get(Calendar.YEAR));
				//销售部门
				if(contractInfo.getDeptId() == null || !saleDeptIds.contains(contractInfo.getDeptId())){
					continue;
				}
				saleWeeklyStat.setDeptId(contractInfo.getDeptId());
				//合同年指标
				List<SalesAnnualIndex> salesAnnualIndexs = salesAnnualIndexRepository.findByStatYearAndDeptId(saleWeeklyStat.getOriginYear(), saleWeeklyStat.getDeptId());
				if(salesAnnualIndexs == null || salesAnnualIndexs.isEmpty()){
					saleWeeklyStat.setAnnualIndex(0D);
				}else{
					Double sumTmp = 0d;
					for(SalesAnnualIndex sai : salesAnnualIndexs){
						sumTmp += sai.getAnnualIndex(); 
					}
					saleWeeklyStat.setAnnualIndex(sumTmp);
				}
				//合同累计完成金额
				//合同完成率
				List<ContractFinishInfo> contractFinishInfos = contractFinishInfoRepository.findAllByContractIdAndCreateTimeBetween(contractId, firstDayOfYear, endTime);
				//计算新增合同完成率-所参照的基数
				List<ContractFinishInfo> comparedFinishInfos = contractFinishInfoRepository.findAllByContractIdAndCreateTimeBefore(contractId, firstDayOfYear);
				if(contractFinishInfos != null && contractFinishInfos.size() > 0){
					ContractFinishInfo contractFinishInfo = contractFinishInfos.get(contractFinishInfos.size() - 1);
					if(comparedFinishInfos != null && comparedFinishInfos.size() > 0){
						saleWeeklyStat.setFinishTotal(contractInfo.getAmount() * 
								(contractFinishInfo.getFinishRate() - comparedFinishInfos.get(comparedFinishInfos.size() - 1).getFinishRate())/100);
					}else{
						saleWeeklyStat.setFinishTotal(contractInfo.getAmount() * (contractFinishInfo.getFinishRate() - 0)/100);
					}
				}else{
					log.error("no finish rate found belong to " + contractInfo.getSerialNum());
					saleWeeklyStat.setFinishTotal(0D);
				}
				//合同该年回款总额
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndReceiveDayBetween(
						contractId, StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
				Double receiveTotal = 0D;
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						receiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.error("no contract receives found belong to " + contractInfo.getSerialNum());
				}
				saleWeeklyStat.setReceiveTotal(receiveTotal);
				
				if(contractInfo.getType() != ContractInfo.TYPE_PUBLIC){//正常合同
					//销售人工成本
					Double salesHumanCost = 0D;
					//销售报销成本
					Double salesPayment = 0D;
					Long salesDeptId = contractInfo.getDeptId();
					if(salesDeptId != null && salesDeptId != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(salesDeptId);
						//List<Long> deptIds = deptInfoRepository.findIdsByType(StringUtil.nullToLong(deptInfo.getType()));
						if(deptInfo != null){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdAndTypeAndContractIdAndCostDayBetween(
									salesDeptId,ContractCost.TYPE_HUMAN_COST, contractId, StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(contractCosts != null && contractCosts.size() > 0){
								for(ContractCost contractCost : contractCosts){
									salesHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdAndNoTypeAndContractIdAndCostDayBetween(
									salesDeptId, ContractCost.TYPE_HUMAN_COST, contractId,StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									salesPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no sales deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					saleWeeklyStat.setSalesHumanCost(salesHumanCost);
					saleWeeklyStat.setSalesPayment(salesPayment);
					//咨询人工成本
					Double consultHumanCost = 0D;
					//咨询报销成本
					Double consultPayment = 0D;
					Long consultDeptId = contractInfo.getConsultantsDeptId();
					if(consultDeptId != null && consultDeptId != 0){
						DeptInfo consultDeptInfo = deptInfoRepository.findOne(consultDeptId);
						if(consultDeptInfo != null){
							List<ContractCost> contractCosts = contractCostRepository.findByDeptIdAndTypeAndContractIdAndCostDayBetween(
									consultDeptId,ContractCost.TYPE_HUMAN_COST, contractId, StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(contractCosts != null && contractCosts.size() > 0){
								for(ContractCost contractCost : contractCosts){
									consultHumanCost += contractCost.getTotal();
								}
							}
							List<ContractCost> contractCosts2 = contractCostRepository.findByDeptIdAndNoTypeAndContractIdAndCostDayBetween(
									consultDeptId, ContractCost.TYPE_HUMAN_COST, contractId, StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(contractCosts2 != null && contractCosts2.size() >0){
								for(ContractCost contractCost : contractCosts2){
									consultPayment += contractCost.getTotal();
								}
							}
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					saleWeeklyStat.setConsultHumanCost(consultHumanCost);
					saleWeeklyStat.setConsultPayment(consultPayment);
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndTypeAndUpdateBetween(contractId, PurchaseItem.TYPE_HARDWARE, firstDayOfYear, endTime);
					Double hardwarePurchase = 0D;
					if(purchaseItems != null && purchaseItems.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems){
							hardwarePurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no hardware purchase item found belong to " + contractInfo.getSerialNum());
					}
					saleWeeklyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndUpdateBetween(contractId, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE, firstDayOfYear, endTime);
					Double externalSoftware = 0D;
					if(purchaseItems2 != null && purchaseItems2.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems2){
							externalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no external software purchase item found belong to " + contractInfo.getSerialNum());
					}
					saleWeeklyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndUpdateBetween(contractId, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE, firstDayOfYear, endTime);
					Double internalSoftware = 0D;
					if(purchaseItems3 != null && purchaseItems3.size() > 0){
						for(PurchaseItem purchaseItem : purchaseItems3){
							internalSoftware += purchaseItem.getQuantity()*purchaseItem.getPrice();
						}
					}else{
						log.error("no internal software purchase item found belong to " + contractInfo.getSerialNum());
					}
					saleWeeklyStat.setInternalSoftware(internalSoftware);
					//项目人工成本
					Double projectHumanCost = 0D;
					//项目报销成本
					Double projectPayment = 0D;
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(contractId);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							List<UserTimesheet> userTimesheets3 = userTimesheetRepository.
									findByObjIdAndTypeAndWordDayBetween(projectInfo.getId(), UserTimesheet.TYPE_PROJECT, StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(userTimesheets3 != null && userTimesheets3.size() > 0){
								for(UserTimesheet userTimesheet : userTimesheets3){
									Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd(now)).toString());
									UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
									if(userCost != null){
										if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
										}else{
											projectHumanCost += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
										}
									}else{
										log.error("no UserCost founded belong to User : " + userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
									}
									
								}
							}else{
								log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getSerialNum());
							}
							//报销成本
							List<ProjectCost> projectCosts2 = projectCostRepository.findAllByProjectIdAndNoTypeAndCostDayBetween(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST, StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
							if(projectCosts2 != null && projectCosts2.size() > 0){
								for(ProjectCost projectCost : projectCosts2){
									projectPayment += projectCost.getTotal();
								}
							}
						}
					}else{
						log.error("no project found belong to " + contractInfo.getSerialNum());
					}
					saleWeeklyStat.setProjectHumanCost(projectHumanCost);
					saleWeeklyStat.setProjectPayment(projectPayment);
					//所有成本
					Double costTotal = salesHumanCost + salesPayment + consultHumanCost + consultPayment + hardwarePurchase + externalSoftware
							+ internalSoftware + projectHumanCost + projectPayment + StringUtil.nullToDouble(contractInfo.getShareCost()) + StringUtil.nullToDouble(contractInfo.getTaxes());
					saleWeeklyStat.setCostTotal(costTotal);
				}else{ //公共合同
					//项目人工成本
					Double projectHumanCost = 0D;
					List<ContractCost> contractCosts = contractCostRepository.findByContractIdAndTypeAndDeptIdAndCostDayBetween(contractId, ContractCost.TYPE_HUMAN_COST,saleWeeklyStat.getDeptId(), StringUtil.nullToLong(saleWeeklyStat.getOriginYear() + "0101"), statWeek);
					for(ContractCost contractCost : contractCosts){
						projectHumanCost += contractCost.getTotal();
					}
					saleWeeklyStat.setProjectHumanCost(projectHumanCost);
					Double costTotal = projectHumanCost;
					saleWeeklyStat.setCostTotal(costTotal);
				}
				//统计周
				saleWeeklyStat.setStatWeek(Long.parseLong(dates[6]));
				//统计日期
				saleWeeklyStat.setCreateTime(ZonedDateTime.now());
				//归属于同一级的部门合并一起
				Long primaryKey = null;
				if(primarySaleDeptInfosMap.containsKey(saleWeeklyStat.getDeptId())){//找到对应的一级部门
					primaryKey = saleWeeklyStat.getDeptId();
				}else{
					for(Long key : childrenSaleDeptIdsMap.keySet()){
						if(childrenSaleDeptIdsMap.get(key).contains(saleWeeklyStat.getDeptId())){
							primaryKey = key;
							break;
						}
					}
				}
				if(primaryKey == null){//没有对应的一级部门
					continue;
				}
				if(saleWeeklyStatsMap.containsKey(primaryKey)){
					SaleWeeklyStat tmp = saleWeeklyStatsMap.get(primaryKey);
					tmp.setFinishTotal(StringUtil.nullToDouble(tmp.getFinishTotal()) + StringUtil.nullToDouble(saleWeeklyStat.getFinishTotal()));
					tmp.setReceiveTotal(StringUtil.nullToDouble(tmp.getReceiveTotal()) + StringUtil.nullToDouble(saleWeeklyStat.getReceiveTotal()));
					tmp.setCostTotal(StringUtil.nullToDouble(tmp.getCostTotal()) + StringUtil.nullToDouble(saleWeeklyStat.getCostTotal()));
					tmp.setSalesHumanCost(StringUtil.nullToDouble(tmp.getSalesHumanCost()) + StringUtil.nullToDouble(saleWeeklyStat.getSalesHumanCost()));
					tmp.setSalesPayment(StringUtil.nullToDouble(tmp.getSalesPayment()) + StringUtil.nullToDouble(saleWeeklyStat.getSalesPayment()));
					tmp.setConsultHumanCost(StringUtil.nullToDouble(tmp.getConsultHumanCost()) + StringUtil.nullToDouble(saleWeeklyStat.getConsultHumanCost()));
					tmp.setConsultPayment(StringUtil.nullToDouble(tmp.getConsultPayment()) + StringUtil.nullToDouble(saleWeeklyStat.getConsultPayment()));
					tmp.setHardwarePurchase(StringUtil.nullToDouble(tmp.getHardwarePurchase()) + StringUtil.nullToDouble(saleWeeklyStat.getHardwarePurchase()));
					tmp.setExternalSoftware(StringUtil.nullToDouble(tmp.getExternalSoftware()) + StringUtil.nullToDouble(saleWeeklyStat.getExternalSoftware()));
					tmp.setInternalSoftware(StringUtil.nullToDouble(tmp.getInternalSoftware()) + StringUtil.nullToDouble(saleWeeklyStat.getInternalSoftware()));
					tmp.setProjectHumanCost(StringUtil.nullToDouble(tmp.getProjectHumanCost()) + StringUtil.nullToDouble(saleWeeklyStat.getProjectHumanCost()));
					tmp.setProjectPayment(StringUtil.nullToDouble(tmp.getProjectPayment()) + StringUtil.nullToDouble(saleWeeklyStat.getProjectPayment()));
					saleWeeklyStatsMap.put(primaryKey, tmp);
				}else{
					saleWeeklyStat.setDeptId(primaryKey);//把deptId改为所属的一级部门id
					saleWeeklyStatsMap.put(primaryKey, saleWeeklyStat);
				}
			}
			//填充其余销售部门
			for(Long deptId : saleWeeklyStatsMap.keySet()){
				if(!primarySaleDeptInfosMap.keySet().contains(deptId)){
					SaleWeeklyStat saleWeeklyStat = new SaleWeeklyStat();
					saleWeeklyStat.setOriginYear((long)firstDayOfYear.getYear());
					saleWeeklyStat.setDeptId(deptId);
					saleWeeklyStat.setStatWeek(statWeek);
					saleWeeklyStat.setCreateTime(ZonedDateTime.now());
					saleWeeklyStatsMap.put(deptId, saleWeeklyStat);
				}
			}
			for(SaleWeeklyStat stat : saleWeeklyStatsMap.values()){
				saleWeeklyStatRepository.save(stat);
			}
			log.info(" =======sale contract weekly stat saved======= ");
		}else{
			log.error("no sale contractInfos found");
		}
		log.info("=====end generate Sale Contract Weekly Stat=====");
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
				DeptInfo deptInfo2 = deptInfoRepository.findOne(contractInfo.getConsultantsDeptId());
				contractCost.setDept(deptInfo.getName());
				Double total = 0D;
				Double totalHour = 0D;
				List<UserTimesheet> userTimesheets = new ArrayList<>();
				if(contractInfo.getType() == ContractInfo.TYPE_PUBLIC){
					userTimesheets = userTimesheetRepository.findByWorkDayAndNotDeptTypeAndType(workDay, deptInfo2.getType(), UserTimesheet.TYPE_PUBLIC);
				}else{
					userTimesheets = userTimesheetRepository.findByWorkDayAndNotDeptTypeAndObjIdAndType(workDay, deptInfo2.getType(), contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				
				if(userTimesheets != null && userTimesheets.size() > 0){
					for(UserTimesheet userTimesheet : userTimesheets){
						UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
						if(userCost != null){
							if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
								total += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
							}else{
								total += userTimesheet.getRealInput() * userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
							}
						}else{
							log.error(" no UserCost founded belong to User : " + userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
						}
						if(userTimesheet.getRealInput() != null){
							totalHour += userTimesheet.getRealInput();
						}
					}
				}
				contractCost.setTotal(total);
				contractCost.setCostDesc(StringUtil.getScaleDouble(totalHour, 1).toString());
				//咨询部分
				ContractCost contractCost2 = new ContractCost();
				contractCost2.setContractId(contractInfo.getId());
				contractCost2.setDeptId(contractInfo.getConsultantsDeptId());
				contractCost2.setDept(deptInfo2.getName());
				Double total2 = 0D;
				Double total2Hour = 0D;
				List<UserTimesheet> userTimesheets2 = new ArrayList<>();
				if(contractInfo.getType() == ContractInfo.TYPE_PUBLIC){
					userTimesheets2 = userTimesheetRepository.findByWorkDayAndDeptTypeAndType(workDay, deptInfo2.getType(), UserTimesheet.TYPE_PUBLIC);
				}else{
					userTimesheets2 = userTimesheetRepository.findByWorkDayAndDeptTypeAndObjIdAndType(workDay, deptInfo2.getType(), contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				if(userTimesheets2 != null && userTimesheets2.size() > 0){
					for(UserTimesheet userTimesheet : userTimesheets2){
						UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
						if(userCost != null){
							if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
								total2 += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
							}else{
								total2 += userTimesheet.getRealInput() * userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
							}
						}else{
							log.error(" no UserCost Founded belong to User " + userTimesheet.getUserId() +":" +userTimesheet.getUserName());
						}
						if(userTimesheet.getRealInput() != null){
							total2Hour += userTimesheet.getRealInput();
						}
					}
				}
				contractCost2.setTotal(total2);
				contractCost.setCostDesc(StringUtil.getScaleDouble(total2Hour, 1).toString());
				contractCost2.setName(contractInfo.getSerialNum() + "-" +  DateUtil.formatDate("yyyyMMdd", currentDay).toString());
				contractCost2.setType(ContractCost.TYPE_HUMAN_COST);
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
				if(contractInfo.getType() == ContractInfo.TYPE_PUBLIC){
					if(contractInfo.getDeptId() != null && contractInfo.getDeptId() != 0){
						DeptInfo deptInfo = deptInfoRepository.findOne(contractInfo.getDeptId());
						userTimesheets = userTimesheetRepository.findByWorkDayAndDeptTypeAndType(workDay, StringUtil.nullToLong(deptInfo.getType()), UserTimesheet.TYPE_PUBLIC);
					}else{
						DeptInfo deptInfo = deptInfoRepository.findOne(contractInfo.getConsultantsDeptId());
						userTimesheets = userTimesheetRepository.findByWorkDayAndDeptTypeAndType(workDay, StringUtil.nullToLong(deptInfo.getType()), UserTimesheet.TYPE_PUBLIC);
					}
				}else{
					userTimesheets = userTimesheetRepository.findByWorkDayAndObjIdAndType(workDay, contractInfo.getId(), UserTimesheet.TYPE_CONTRACT);
				}
				Double total = 0D;
				Double totalHour = 0D;
				if(userTimesheets != null && userTimesheets.size() > 0){
					for(UserTimesheet userTimesheet : userTimesheets){
						UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, userTimesheet.getUserId());
						if(userCost != null){
							if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
								total += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
							}else{
								total += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
							}
						}else{
							log.info("no userCost founded belong to User : "+ userTimesheet.getUserId() + ":" + userTimesheet.getUserName());
						}
						if(userTimesheet.getRealInput() != null){
							totalHour += userTimesheet.getRealInput();
						}
					}
				}
				contractCost.setTotal(total);
				contractCost.setCostDesc(StringUtil.getScaleDouble(totalHour, 1).toString());
			}
			contractCost.setName(contractInfo.getSerialNum() + "-" +  DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			contractCost.setType(ContractCost.TYPE_HUMAN_COST);
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
