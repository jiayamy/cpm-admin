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

import com.wondertek.cpm.CpmConstants;
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
import com.wondertek.cpm.domain.StatIdentify;
import com.wondertek.cpm.domain.SystemConfig;
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
import com.wondertek.cpm.repository.SystemConfigRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;
/**
 * 合同相关统计
 * @author lvliuzhong
 *
 */
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
	
	@Inject
	private SystemConfigRepository systemConfigRepository;
	
	private void init(){
		//外部报价
		List<ExternalQuotation> externalQuotations = externalQuotationRepository.findAll();
		externalQuotationMap.clear();
		if(externalQuotations != null && externalQuotations.size() > 0){
			for(ExternalQuotation externalQuotation : externalQuotations){
				externalQuotationMap.put(externalQuotation.getGrade(), externalQuotation.getHourCost());
			}
		}
		//用户信息
		List<User> users = userRepository.findAll();
		userIdGradeMap.clear();
		if(users != null && users.size() > 0){
			for(User user : users){
				userIdGradeMap.put(user.getId(), user.getGrade());
			}
		}
		//运行状态标识
		List<StatIdentify> statIdentifies = statIdentifyRepository.findByStatus(StatIdentify.STATUS_UNAVALIABLE);
		if(statIdentifies != null && statIdentifies.size() > 0){
			statIdentifyRepository.delete(statIdentifies);
		}
	}
	/**
	 * TODO 合同周统计，每周一晚上23点执行
	 */
	@Scheduled(cron = "0 0 23 ? * MON")
	protected void generateContractWeeklyStat(){
		Date now = new Date();
		generateContractWeeklyStat(null, now);
	}
	protected void generateContractWeeklyStat(Long contractId, Date now){
		log.info("=====begin generate Contract Weekly Stat=====");
		init();
		
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday(now));
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd(now).getTime());
		//上周一
		Long fDay = StringUtil.nullToLong(dates[0]);
		//上周日
		Long statWeek = StringUtil.nullToLong(dates[6]);
		//上上周日
		Long lstatWeek = StringUtil.nullToLong(DateUtil.getWholeWeekByDate(DateUtil.addDayNum(-1, DateUtil.lastMonday(now)))[6]);
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrEndTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				if(contractId != null && !contractId.equals(contractInfo.getId())){
					continue;
				}
				log.info("=========begin generate Contract : "+contractInfo.getSerialNum()+"=========");
				//初始化合同工时
				initContractCost(contractInfo, fDay, statWeek, DateUtil.lastMonday(now), DateUtil.lastSundayEnd(now));
				
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
							salesHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndTypeAndContractIdAndBeforeCostDay(deptIds, ContractCost.TYPE_HUMAN_COST, id, statWeek));
							salesPayment = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndNoTypeAndContractIdAndBeforeCostDay(deptIds, ContractCost.TYPE_HUMAN_COST, id, statWeek));
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
							consultHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndTypeAndContractIdAndBeforeCostDay(deptIds2, ContractCost.TYPE_HUMAN_COST, id,statWeek));
							consultPayment = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndNoTypeAndContractIdAndBeforeCostDay(deptIds2, ContractCost.TYPE_HUMAN_COST, id,statWeek));
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractWeeklyStat.setConsultHumanCost(consultHumanCost);
					contractWeeklyStat.setConsultPayment(consultPayment);
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndTypeAndBeforeUpdateTime(id, PurchaseItem.TYPE_HARDWARE, endTime);
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
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndBeforeUpdateTime(id, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE, endTime);
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
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndBeforeUpdateTime(id, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE, endTime);
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
					ContractWeeklyStat lastCWS = contractWeeklyStatRepository.findOneByStatWeekAndContractId(lstatWeek, id);//上上周记录
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							if(lastCWS != null && lastCWS.getProjectHumanCost() != null && lastCWS.getProjectHumanCost() > 0){
								//上上周记录
								projectHumanCost += lastCWS.getProjectHumanCost();
								//上周记录统计
								List<UserTimesheet> userTimesheets = userTimesheetRepository.findByObjIdAndTypeAndWordDayBetween(projectInfo.getId(), UserTimesheet.TYPE_PROJECT, fDay, statWeek);
								if(userTimesheets != null && userTimesheets.size() > 0){
									Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd(now)).toString());
									for(UserTimesheet userTimesheet : userTimesheets){
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
								}
							}else{//查找全部日报
								List<UserTimesheet> userTimesheets3 = userTimesheetRepository.findByDateAndObjIdAndType(statWeek, projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
								if(userTimesheets3 != null && userTimesheets3.size() > 0){
									for(UserTimesheet userTimesheet : userTimesheets3){
										Long costMonth = 0L;
										if(userTimesheet.getWorkDay() != null && userTimesheet.getWorkDay().toString().length() > 5){
											costMonth = StringUtil.nullToLong(StringUtil.nullToString(userTimesheet.getWorkDay()).substring(0, 6));
										}
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
							}
							//报销成本
							projectPayment = StringUtil.nullToDouble(projectCostRepository.findTotalByProjectIdAndNoTypeAndBeforeCostDay(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST, statWeek));
						}
					}else{
						log.error("no project found belong to " + contractInfo.getSerialNum());
					}
					contractWeeklyStat.setProjectHumanCost(projectHumanCost);
					contractWeeklyStat.setProjectPayment(projectPayment);
					//所有成本
					Double costTotal = salesHumanCost + salesPayment + consultHumanCost + consultPayment
							+ hardwarePurchase + externalSoftware + internalSoftware + projectHumanCost 
							+ projectPayment + StringUtil.nullToDouble(contractInfo.getShareCost()) + StringUtil.nullToDouble(contractInfo.getTaxes());
					contractWeeklyStat.setCostTotal(costTotal);
					//合同毛利
					Double grossProfit = receiveTotal - costTotal;
					contractWeeklyStat.setGrossProfit(grossProfit);
				}else{ //公共合同
					//项目人工成本
					Double projectHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByContractIdAndTypeAndBeforeCostDay(id, ContractCost.TYPE_HUMAN_COST, statWeek));
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
				if(contractId != null && contractId.equals(contractInfo.getId())){
					break;
				}
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Weekly Stat=====");
	}
	
	/**
	 * TODO 合同月统计，每个月的第一天的23点30分开始执行
	 */
	@Scheduled(cron = "0 30 3 10 * ?")
	protected void generateContractMonthlyStat(){
		Date now = new Date();
		generateContractMonthlyStat(null,now);
	}
	
	protected void generateContractMonthlyStat(Long contractId, Date now){
		log.info("=====begin generate Contract Monthly Stat=====");
		init();
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonthBegin(now).getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastMonthend(now).getTime());
		//上月第一天
		Long fDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthBegin(now)));
		//上月最后天
		Long lDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", DateUtil.lastMonthend(now)));
		//上个月
		String lMonth = DateUtil.formatDate("yyyyMM", DateUtil.lastMonthBegin(now));
		//上上个月
		String nMonth = DateUtil.formatDate("yyyyMM", DateUtil.addDayNum(-1, DateUtil.lastMonthBegin(now)));
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrEndTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				if(contractId != null && !contractId.equals(contractInfo.getId())){
					continue;
				}
				log.info("=====begin generate Contract : "+contractInfo.getSerialNum()+"=======");
				//初始contractCost
				initContractCost(contractInfo, fDay, lDay, DateUtil.lastMonthBegin(now), DateUtil.lastMonthend(now));
				
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
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndReceiveDayBefore(id, lDay);
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
							salesHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndTypeAndContractIdAndBeforeCostDay(deptIds, ContractCost.TYPE_HUMAN_COST, id, lDay));
							salesPayment = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndNoTypeAndContractIdAndBeforeCostDay(deptIds, ContractCost.TYPE_HUMAN_COST, id, lDay));
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
							consultHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndTypeAndContractIdAndBeforeCostDay(deptIds2, ContractCost.TYPE_HUMAN_COST, id, lDay));
							consultPayment = StringUtil.nullToDouble(contractCostRepository.findTotalByDeptIdsAndNoTypeAndContractIdAndBeforeCostDay(deptIds2, ContractCost.TYPE_HUMAN_COST, id, lDay));
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					contractMonthlyStat.setConsultHumanCost(consultHumanCost);
					contractMonthlyStat.setConsultPayment(consultPayment);
					//硬件采购成本
					List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndTypeAndBeforeUpdateTime(id, PurchaseItem.TYPE_HARDWARE, endTime);
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
					List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndBeforeUpdateTime(id, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE, endTime);
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
					List<PurchaseItem> purchaseItems3 = purchaseItemRepository.findByContractIdAndSourceAndTypeAndBeforeUpdateTime(id, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_HARDWARE, endTime);
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
					ContractMonthlyStat lastCMS = contractMonthlyStatRepository.findOneByStatWeekAndContractId(StringUtil.nullToLong(nMonth), id);//上上个月记录
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(id);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							if(lastCMS != null && lastCMS.getProjectHumanCost() != null && lastCMS.getProjectHumanCost() > 0){
								projectHumanCost += lastCMS.getProjectHumanCost();
								List<UserTimesheet> userTimesheets = userTimesheetRepository.findByObjIdAndTypeAndWordDayBetween(projectInfo.getId(), UserTimesheet.TYPE_PROJECT, fDay, lDay);
								if(userTimesheets != null && userTimesheets.size() > 0){
									for(UserTimesheet userTimesheet : userTimesheets){
										UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(StringUtil.nullToLong(lMonth), userTimesheet.getUserId());
										if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
										}else{
											projectHumanCost += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
										}
									}
								}
							}else{
								List<UserTimesheet> userTimesheets3 = userTimesheetRepository.
										findByDateAndObjIdAndType(lDay, projectInfo.getId(), UserTimesheet.TYPE_PROJECT);
								if(userTimesheets3 != null && userTimesheets3.size() > 0){
									for(UserTimesheet userTimesheet : userTimesheets3){
										Long cMonth = 0L;
										if(userTimesheet.getWorkDay() != null && userTimesheet.getWorkDay().toString().length() > 5){
											cMonth = StringUtil.nullToLong(StringUtil.nullToString(userTimesheet.getWorkDay()).substring(0, 6));
										}
										UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(cMonth, userTimesheet.getUserId());
										if(contractInfo.getIsEpibolic() != null && !contractInfo.getIsEpibolic()){
											projectHumanCost += userTimesheet.getRealInput() * (userCost.getInternalCost()/168);
										}else{
											projectHumanCost += userTimesheet.getRealInput() * StringUtil.nullToDouble((externalQuotationMap.get(userIdGradeMap.get(userTimesheet.getUserId()))));
										}
									}
								}else{
									log.error(" no user Timesheets founded for project human cost belong to " + contractInfo.getSerialNum());
								}
							}
							//报销成本
							projectPayment = StringUtil.nullToDouble(projectCostRepository.findTotalByProjectIdAndNoTypeAndBeforeCostDay(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST, lDay));
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
					Double projectHumanCost = StringUtil.nullToDouble(contractCostRepository.findTotalByContractIdAndTypeAndBeforeCostDay(id, ContractCost.TYPE_HUMAN_COST, lDay));
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
				if(contractId != null && contractId.equals(contractInfo.getId())){
					break;
				}
			}
		}else{
			log.error("no contractInfos found");
		}
		log.info("=====end generate Contract Monthly Stat=====");
	}
	
	/**
	 * 每周 汇总大销售部门下的子销售部门下面 一年内所有销售的合同情况。TODO 销售统计
	 */
	@Scheduled(cron = "0 59 23 ? * MON")
	protected void generateSaleContractWeeklyStat(){
		Date now = new Date();
		generateSaleContractWeeklyStat(null, now);
	}
	protected void generateSaleContractWeeklyStat(Long contrId, Date now){
		log.info("=====begin generate Sale Contract Weekly Stat=====");
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday(now));
		
//		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(now).getTime());	//上周开始时间
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
		List<ContractInfo> contractInfos = contractInfoRepository.findByDeptTypeAndStatusOrTime(StringUtil.nullToLong(DeptType.TYPE_DEPT_SALE),ContractInfo.STATUS_VALIDABLE, firstDayOfYear);
		if(contractInfos != null && contractInfos.size() > 0){
			//初始上周stat
			List<SaleWeeklyStat> saleWeeklyStats = saleWeeklyStatRepository.findByStatWeek(statWeek);
			if(saleWeeklyStats != null){
				saleWeeklyStatRepository.delete(saleWeeklyStats);
			}
			//所有销售部门信息
			List<DeptInfo> saleDeptInfos = deptInfoRepository.findDeptInfosByType(StringUtil.nullToLong(DeptType.TYPE_DEPT_SALE));
			Map<Long,DeptInfo> saleDeptInfosMap = new HashMap<Long,DeptInfo>();
			if (saleDeptInfos != null) {
				for (DeptInfo info : saleDeptInfos) {
					saleDeptInfosMap.put(info.getId(), info);
				} 
			}
			//所有的顶级销售部门id
			List<Long> topSaleDeptIds = null;
			SystemConfig systemConfig = systemConfigRepository.findByKey(CpmConstants.DEFAULT_Dept_SALE_TOPID);
			if(systemConfig == null){
				log.error("=====Top sale dept is not found=====");
			}else{
				String saleId = systemConfig.getValue();
				topSaleDeptIds = StringUtil.stringToLongArray(saleId);
			}
			
			//所有的一级销售部门id(包括各个顶级销售部门下的一级部门)
			List<Long> primarySaleDeptIdsList = new ArrayList<Long>();
			List<DeptInfo> primarySaleDeptInfos = null;
			if (topSaleDeptIds != null) {
				for (Long topId : topSaleDeptIds) {
					if(!saleDeptInfosMap.containsKey(topId)){
						continue;
					}
					primarySaleDeptInfos = deptInfoRepository.findByIdPath(saleDeptInfosMap.get(topId).getIdPath() + saleDeptInfosMap.get(topId).getId() + "/");
					if (primarySaleDeptInfos != null) {
						for (DeptInfo info : primarySaleDeptInfos) {
							primarySaleDeptIdsList.add(info.getId());
						}
					}
				}
			}
			//每个一级部门下的所有子销售部门id
			Map<Long,Long> childrenSaleDept2ParentsMap = new HashMap<Long,Long>();//子销售部门对应的一级销售部门
			Map<Long,List<Long>> primarySaleDepts = new HashMap<Long,List<Long>>();//一级部门下面的所有子部门
			List<DeptInfo> childrenSaleDeptInfos = null;
			for(Long primaryId : primarySaleDeptIdsList){
				childrenSaleDept2ParentsMap.put(primaryId, primaryId);
				primarySaleDepts.put(primaryId, new ArrayList<Long>());
				primarySaleDepts.get(primaryId).add(primaryId);
				
				childrenSaleDeptInfos = deptInfoRepository.findByIdPath(saleDeptInfosMap.get(primaryId).getIdPath() + saleDeptInfosMap.get(primaryId).getId() + "/%");
				if(childrenSaleDeptInfos != null){
					for(DeptInfo info : childrenSaleDeptInfos){
						childrenSaleDept2ParentsMap.put(info.getId(), primaryId);
						primarySaleDepts.get(primaryId).add(info.getId());
					}
				}
			}
			
			Map<Long,SaleWeeklyStat> saleWeeklyStatsMap = new HashMap<Long,SaleWeeklyStat>();//key:一级部门id
			for(ContractInfo contractInfo : contractInfos){
				if(contrId != null && !contrId.equals(contractInfo.getId())){
					continue;
				}
				//销售部门
				if(contractInfo.getDeptId() == null || !saleDeptInfosMap.containsKey(contractInfo.getDeptId())){
					continue;
				}
				log.info("=========begin generate Sale Contract : "+contractInfo.getSerialNum()+"=========");
				SaleWeeklyStat saleWeeklyStat = new SaleWeeklyStat();
				//合同id
				Long contractId = contractInfo.getId();
				//年份
				Calendar cal = Calendar.getInstance();
				cal.setTime(now);
				saleWeeklyStat.setOriginYear((long)cal.get(Calendar.YEAR));
				saleWeeklyStat.setDeptId(contractInfo.getDeptId());
				//合同年指标
				saleWeeklyStat.setAnnualIndex(0D);
				//合同累计完成金额
				saleWeeklyStat.setFinishTotal(0D);
				if(contractInfo.getAmount() != null && contractInfo.getStartDay() != null 
						&& !contractInfo.getStartDay().isBefore(firstDayOfYear)
						&& contractInfo.getStartDay().isBefore(endTime)){
					saleWeeklyStat.setFinishTotal(contractInfo.getAmount());
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
						DeptInfo deptInfo = saleDeptInfosMap.get(salesDeptId);
						if(deptInfo != null){
							salesHumanCost = contractCostRepository.findByDeptIdAndTypeAndContractIdAndCostDayBetween(
									salesDeptId,ContractCost.TYPE_HUMAN_COST, contractId, 
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
							
							salesPayment = contractCostRepository.findByDeptIdAndNoTypeAndContractIdAndCostDayBetween(
									salesDeptId, ContractCost.TYPE_HUMAN_COST, contractId,
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
							
							if(salesHumanCost == null){
								salesHumanCost = 0d;
							}
							if(salesPayment == null){
								salesPayment = 0d;
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
							consultHumanCost = contractCostRepository.findByDeptIdAndTypeAndContractIdAndCostDayBetween(
									consultDeptId,ContractCost.TYPE_HUMAN_COST, contractId, 
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
							
							consultPayment = contractCostRepository.findByDeptIdAndNoTypeAndContractIdAndCostDayBetween(
									consultDeptId, ContractCost.TYPE_HUMAN_COST, contractId, 
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
							
							if(consultHumanCost == null){
								consultHumanCost = 0d;
							}
							if(consultPayment == null){
								consultPayment = 0d;
							}
						}else{
							log.error("no consult deptInfo found belong to " + contractInfo.getSerialNum());
						}
					}
					saleWeeklyStat.setConsultHumanCost(consultHumanCost);
					saleWeeklyStat.setConsultPayment(consultPayment);
					
					//硬件采购成本
					Double hardwarePurchase = purchaseItemRepository.findByContractIdAndTypeAndUpdateBetween(contractId, PurchaseItem.TYPE_HARDWARE, firstDayOfYear, endTime);
					saleWeeklyStat.setHardwarePurchase(hardwarePurchase);
					//外部软件采购成本
					Double externalSoftware = purchaseItemRepository.findByContractIdAndSourceAndTypeAndUpdateBetween(contractId, PurchaseItem.SOURCE_EXTERNAL, PurchaseItem.TYPE_SOFTWARE, firstDayOfYear, endTime);
					saleWeeklyStat.setExternalSoftware(externalSoftware);
					//内部软件采购成本
					Double internalSoftware = purchaseItemRepository.findByContractIdAndSourceAndTypeAndUpdateBetween(
							contractId, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE, firstDayOfYear, endTime);
					saleWeeklyStat.setInternalSoftware(internalSoftware);
					//项目人工成本
					Double projectHumanCost = 0D;
					//项目报销成本
					Double projectPayment = 0D;
					List<ProjectInfo> projectInfos = projectInfoRepository.findByContractId(contractId);
					if(projectInfos != null && projectInfos.size() > 0){
						for(ProjectInfo projectInfo : projectInfos){
							//人工成本
							projectHumanCost += projectCostRepository.findAllByProjectIdAndTypeAndCostDayBetween(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST, 
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
							//报销成本
							projectPayment += projectCostRepository.findAllByProjectIdAndNoTypeAndCostDayBetween(projectInfo.getId(), ProjectCost.TYPE_HUMAN_COST, 
									StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
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
					saleWeeklyStat.setConsultHumanCost(0d);
					saleWeeklyStat.setConsultPayment(0d);
					saleWeeklyStat.setExternalSoftware(0d);
					saleWeeklyStat.setHardwarePurchase(0d);
					saleWeeklyStat.setInternalSoftware(0d);
					saleWeeklyStat.setProjectPayment(0d);
					saleWeeklyStat.setSalesHumanCost(0d);
					saleWeeklyStat.setSalesPayment(0d);
					
					//项目人工成本
					Double projectHumanCost = contractCostRepository.findByDeptIdAndTypeAndContractIdAndCostDayBetween(saleWeeklyStat.getDeptId(),ContractCost.TYPE_HUMAN_COST,
							contractId, StringUtil.stringToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, DateUtil.convertZonedDateTime(firstDayOfYear))), statWeek);
					saleWeeklyStat.setProjectHumanCost(projectHumanCost);
					
					Double costTotal = projectHumanCost;
					
					saleWeeklyStat.setCostTotal(costTotal);
				}
				//统计周
				saleWeeklyStat.setStatWeek(Long.parseLong(dates[6]));
				//统计日期
				saleWeeklyStat.setCreateTime(ZonedDateTime.now());
				//归属于同一级的部门合并一起
				Long primaryKey = childrenSaleDept2ParentsMap.get(saleWeeklyStat.getDeptId());
				if(primaryKey == null){//没有对应的一级部门
					continue;
				}
				if(saleWeeklyStatsMap.containsKey(primaryKey)){
					SaleWeeklyStat tmp = saleWeeklyStatsMap.get(primaryKey);
					tmp.setAnnualIndex(StringUtil.nullToDouble(tmp.getAnnualIndex()) + StringUtil.nullToDouble(saleWeeklyStat.getAnnualIndex()));//合同年指标
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
				if(contrId != null && contrId.equals(contractInfo.getId())){
					break;
				}
			}
			//填充其余销售部门
			for(Long deptId : primarySaleDeptIdsList){
				if(!saleWeeklyStatsMap.keySet().contains(deptId)){
					SaleWeeklyStat saleWeeklyStat = new SaleWeeklyStat();
					saleWeeklyStat.setOriginYear((long)firstDayOfYear.getYear());
					saleWeeklyStat.setDeptId(deptId);
					saleWeeklyStat.setStatWeek(statWeek);
					saleWeeklyStat.setCreateTime(ZonedDateTime.now());
					
					saleWeeklyStat.setAnnualIndex(0d);
					saleWeeklyStat.setFinishTotal(0d);
					saleWeeklyStat.setReceiveTotal(0d);
					saleWeeklyStat.setCostTotal(0d);
					saleWeeklyStat.setConsultHumanCost(0d);
					saleWeeklyStat.setConsultPayment(0d);
					saleWeeklyStat.setExternalSoftware(0d);
					saleWeeklyStat.setHardwarePurchase(0d);
					saleWeeklyStat.setInternalSoftware(0d);
					saleWeeklyStat.setProjectHumanCost(0d);
					saleWeeklyStat.setProjectPayment(0d);
					saleWeeklyStat.setSalesHumanCost(0d);
					saleWeeklyStat.setSalesPayment(0d);
					
					saleWeeklyStatsMap.put(deptId, saleWeeklyStat);
				}
			}
			for(SaleWeeklyStat stat : saleWeeklyStatsMap.values()){
				//合同年指标
				if(primarySaleDepts.containsKey(stat.getDeptId())){
					Double salesAnnualIndexs = salesAnnualIndexRepository.findByStatYearAndDeptId(stat.getOriginYear(), primarySaleDepts.get(stat.getDeptId()));
					stat.setAnnualIndex(salesAnnualIndexs);
				}
				saleWeeklyStatRepository.save(stat);
			}
			log.info(" =======sale contract weekly stat saved======= ");
		}else{
			log.error("no sale contractInfos found");
		}
		log.info("=====end generate Sale Contract Weekly Stat=====");
	}
	/**
	 * 初始合同工时
	 */
	private void initContractCost(ContractInfo contractInfo, Long beginDayStr, Long endDayStr, Date beginDay, Date endDay) {
		try {
			//TODO 初始化合同工时
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
			ContractCost contractCost2 = contractCostRepository.findMaxByContractIdAndCostDayAndType(contractInfo.getId(), beginDayStr, endDayStr, ContractCost.TYPE_HUMAN_COST);
			if(contractCost2 != null){
				Date initDate = DateUtil.addOneDay(DateUtil.parseDate("yyyyMMdd", contractCost2.getCostDay().toString()));
				if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
					initContractHumanCost(contractInfo, initDate, endDay);
				}else{
					initContractHumanCost(contractInfo, initDate, Date.from(contractInfo.getUpdateTime().toInstant()));
				}
			}else{
				if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
					initContractHumanCost(contractInfo, beginDay, endDay);
				}else{
					initContractHumanCost(contractInfo, beginDay, Date.from(contractInfo.getUpdateTime().toInstant()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			overIdentify(contractInfo);
		}
	}
	
	/**
	 * 初始化合同工时成本
	 */
	private void initContractHumanCost(ContractInfo contractInfo, Date beginTime, Date endTime){
		if(endTime.getTime() < beginTime.getTime()){
			return;
		}
		Long countDay = (endTime.getTime() - beginTime.getTime())/(24*60*60*1000);
		Date currentDay = beginTime;
		
		for(int i = 0; i <= countDay; i++){
			Long workDay = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", currentDay).toString());
			Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", currentDay).toString());
			ContractCost contractCost = new ContractCost();
			contractCost.setContractId(contractInfo.getId());
			List<Long> userTimesheetIds = new ArrayList<Long>();//被统计的员工日报id
			//销售和咨询都有
			if(contractInfo.getDeptId() != null && contractInfo.getDeptId() != 0 
					&& contractInfo.getConsultantsDeptId() != null && contractInfo.getConsultantsDeptId() != 0){
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
						userTimesheetIds.add(userTimesheet.getId());
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
				contractCost.setInput(totalHour);
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
						userTimesheetIds.add(userTimesheet.getId());
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
				contractCost2.setInput(total2Hour);
				contractCost2.setTotal(total2);
				contractCost2.setCostDesc(StringUtil.getScaleDouble(total2Hour, 1).toString());
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
						userTimesheetIds.add(userTimesheet.getId());
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
				contractCost.setInput(totalHour);
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
