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
import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.ContractFinishInfo;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractInternalPurchase;
import com.wondertek.cpm.domain.ContractProjectBonus;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.domain.ProductSalesBonus;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.ShareCostRate;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.BonusRateRepository;
import com.wondertek.cpm.repository.BonusRepository;
import com.wondertek.cpm.repository.ConsultantsBonusRepository;
import com.wondertek.cpm.repository.ContractFinishInfoRepository;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ContractInternalPurchaseRepository;
import com.wondertek.cpm.repository.ContractProjectBonusRepository;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.ExternalQuotationRepository;
import com.wondertek.cpm.repository.ProductSalesBonusRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectOverallRepository;
import com.wondertek.cpm.repository.ProjectSupportBonusRepository;
import com.wondertek.cpm.repository.ProjectSupportCostRepository;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.SalesBonusRepository;
import com.wondertek.cpm.repository.ShareCostRateRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;
/**
 * 财务部需求，周统计，可单独跑
 * 与合同相关的统计
 * 	销售内部采购成本
 * 	项目支撑奖金
 * 	销售项目信息
 * 	咨询项目信息
 * 	奖金总表
 * 	项目总体情况控制
 * @author lvliuzhong
 *
 */
@Component
@EnableScheduling
public class AccountScheduledJob {
	
	private Logger log = LoggerFactory.getLogger(AccountScheduledJob.class);
	
	private Map<String, Double> shareCostRateMap = new HashMap<>();
	
	private Map<String, Double> bonusRateMap = new HashMap<>();
	
	private Map<Integer, Double> externalQuotationMap = new HashMap<>();
	
	private Map<Long, Long> deptIdTypeMap = new HashMap<>();
	
	@Inject
	private BonusRepository bonusRepository;
	
	@Inject
	private BonusRateRepository bonusRateRepository;
	
	@Inject
	private ConsultantsBonusRepository consultantsBonusRepository;
	
	@Inject
	private ContractInfoRepository contractInfoRepository;
	
	@Inject
	private ContractReceiveRepository contractReceiveRepository;
	
//	@Inject
//	private ContractBudgetRepository contractBudgetRepository;
	
	@Inject
	private ContractInternalPurchaseRepository contractInternalPurchaseRepository;
	
	@Inject
	private ContractProjectBonusRepository contractProjectBonusRepository;
	
	@Inject
	private ContractFinishInfoRepository contractFinishInfoRepository;
	
	@Inject
	private DeptInfoRepository deptInfoRepository;
	
	@Inject
	private ExternalQuotationRepository externalQuotationRepository;
	
	@Inject
	private ProjectInfoRepository projectInfoRepository;
	
	@Inject
	private ProjectSupportCostRepository projectSupportCostRepository;
	
	@Inject
	private ProjectSupportBonusRepository projectSupportBonusRepository;
	
	@Inject
	private ProjectOverallRepository projectOverallRepository;
	
	@Inject
	private PurchaseItemRepository purchaseItemRepository;
	
	@Inject
	private ProductSalesBonusRepository productSalesBonusRepository;
	
	@Inject
	private SalesBonusRepository salesBonusRepository;
	
//	@Inject
//	private ShareInfoRepository shareInfoRepository;
	
	@Inject
	private ShareCostRateRepository shareCostRateRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserTimesheetRepository userTimesheetRepository;
	
	@Inject
	private UserCostRepository userCostRepository;
	/**
	 * 数据初始化
	 */
	private void init(){
		//奖金提成比率
		List<BonusRate> bonusRates = bonusRateRepository.findAll();
		if(bonusRates != null && bonusRates.size() > 0){
			for(BonusRate bonusRate : bonusRates){
				bonusRateMap.put(bonusRate.getDeptType()+"-"+bonusRate.getContractType(), bonusRate.getRate());
			}
		}
		//公摊成本比例
		List<ShareCostRate> shareCostRates = shareCostRateRepository.findAll();
		if(shareCostRates != null && shareCostRates.size() > 0){
			for(ShareCostRate shareCostRate : shareCostRates){
				shareCostRateMap.put(shareCostRate.getDeptType()+"-"+shareCostRate.getContractType(), shareCostRate.getShareRate());
			}
		}
		//外部报价
		List<ExternalQuotation> externalQuotations = externalQuotationRepository.findAll();
		if(externalQuotations != null && externalQuotations.size() > 0){
			for(ExternalQuotation externalQuotation : externalQuotations){
				externalQuotationMap.put(externalQuotation.getGrade(), externalQuotation.getHourCost());
			}
		}
		//部门信息
		List<DeptInfo> deptInfos = deptInfoRepository.findAll();
		if(deptInfos != null && deptInfos.size() > 0){
			for(DeptInfo deptInfo : deptInfos){
				deptIdTypeMap.put(deptInfo.getId(), deptInfo.getType());
			}
		}
	}
	/**
	 * 删除记录
	 * @param statWeek
	 */
	private void clear(Long statWeek){
		//后期全部修改为直接使用sql或hql删除、不要查询后删除
		//项目支撑成本信息
		List<ProjectSupportCost> projectSupportCosts = projectSupportCostRepository.findByStatWeek(statWeek);
		if(projectSupportCosts != null){
			projectSupportCostRepository.delete(projectSupportCosts);
		}
		//项目支撑奖金
		List<ProjectSupportBonus> projectSupportBonuses = projectSupportBonusRepository.findByStatWeek(statWeek);
		if(projectSupportBonuses != null){
			projectSupportBonusRepository.delete(projectSupportBonuses);
		}
		//产品销售奖金
		List<ProductSalesBonus> productSalesBonuses = productSalesBonusRepository.findByStatWeek(statWeek);
		if(productSalesBonuses != null){
			productSalesBonusRepository.delete(productSalesBonuses);
		}
		//合同内部采购信息
		List<ContractInternalPurchase> contractInternalPurchases = contractInternalPurchaseRepository.findByStatWeek(statWeek);
		if(contractInternalPurchases != null){
			contractInternalPurchaseRepository.delete(contractInternalPurchases);
		}
		//销售奖金
		List<SalesBonus> salesBonuses = salesBonusRepository.findByStatWeek(statWeek);
		if(salesBonuses != null){
			salesBonusRepository.delete(salesBonuses);
		}
		//咨询奖金
		List<ConsultantsBonus> consultantsBonuses = consultantsBonusRepository.findByStatWeek(statWeek);
		if(consultantsBonuses != null){
			consultantsBonusRepository.delete(consultantsBonuses);
		}
		//合同的项目奖金
		List<ContractProjectBonus> contractProjectBonuses = contractProjectBonusRepository.findByStatWeek(statWeek);
		if(contractProjectBonuses != null){
			contractProjectBonusRepository.delete(contractProjectBonuses);
		}
		//奖金总表
		List<Bonus> bonuses = bonusRepository.findByStatWeek(statWeek);
		if(bonuses != null){
			bonusRepository.delete(bonuses);
		}
		//项目总体情况控制表
		List<ProjectOverall> projectOveralls = projectOverallRepository.findByStatWeek(statWeek);
		if(projectOveralls != null){
			projectOverallRepository.delete(projectOveralls);
		}
	}
	/**
	 * 合同和项目相关的统计，每周一晚上22点开始执行
	 * 关系：
	 * 	
	 */
	@Scheduled(cron="0 0 22 * * MON")
	protected void accountScheduled(){
		//TODO 统计开始
		//每周一晚上22点开始跑定时任务
		accountScheduled(new Date());
	}
	protected void accountScheduled(Date statTime){
		log.info("=====begin Account Scheduled=====statTime:" + DateUtil.formatDate(DateUtil.DATE_FORMAT, statTime));
		//数据初始化
		init();
		//日期初始化
		//上周的周一到周日
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastMonday(statTime));
		//上周周一
		Long fDay = StringUtil.nullToLong(dates[0]);
		//上周周日
		Long statWeek = StringUtil.nullToLong(dates[6]);
		//上周周一0点0分0秒
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday(statTime).getTime());
		//上周周日23点59分59秒
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd(statTime).getTime());
		//上上周周日
		Long lastStatWeek = StringUtil.nullToLong(DateUtil.getWholeWeekByDate(DateUtil.addDayNum(-7, DateUtil.lastSundayEnd(statTime)))[6]);
		
		Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd(statTime)).toString());//上周周日所在月
		String creator = "admin";
		
		//删除历史记录
		clear(statWeek);
		
		//TODO 统计的合同
		List<ContractInfo> contractInfos = contractInfoRepository.findByStartDayAndStatusOrUpdateTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				Long contractId = contractInfo.getId();
				Integer contractType = contractInfo.getType();
				//收款金额
				Double contractReceiveTotal = 0D;
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndReceiveDayBefore(contractId, statWeek);
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						contractReceiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.info("No ContractRecevie Founded belong to Contract :" + contractInfo.getSerialNum());
				}
				//税收
				Double contractTaxes = contractReceiveTotal*((contractInfo.getTaxRate()/100)/(1+(contractInfo.getTaxRate()/100)));
				//第三方采购
				Double contractThirdPartyPurchase = 0D;
				List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndSourceAndUpdateBefore(contractId, PurchaseItem.SOURCE_EXTERNAL, endTime);
				if(purchaseItems != null && purchaseItems.size() > 0){
					for(PurchaseItem purchaseItem : purchaseItems){
						contractThirdPartyPurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
					}
				}else{
					log.error("no Purchase item found belong to " + contractInfo.getSerialNum());
				}
				//合同完成率
				Double contractFinishRate = 0D;
				ContractFinishInfo contractFinishInfo = contractFinishInfoRepository.findMaxByContractIdAndCreateTimeBefore(contractId, endTime);
				if(contractFinishInfo != null){
					contractFinishRate = StringUtil.nullToDouble(contractFinishInfo.getFinishRate());
				}
				//TODO 销售奖金
				log.info("====begin generate Sales Bonus belong to Contract : "+contractInfo.getSerialNum()+"========");
				if(contractInfo.getSalesmanId() != null && contractInfo.getSalesman() != null){
					SalesBonus salesBonus = new  SalesBonus();
					salesBonus.setStatWeek(statWeek);
					salesBonus.setSalesManId(contractInfo.getSalesmanId());
					salesBonus.setSalesMan(contractInfo.getSalesman());
					salesBonus.setContractId(contractId);
					//所属年份
					Calendar c = Calendar.getInstance();
					c.setTime(Date.from(contractInfo.getStartDay().toInstant()));
					Long sbOriginYear = (long) c.get(Calendar.YEAR);
					salesBonus.setOriginYear(sbOriginYear);
					salesBonus.setContractAmount(contractInfo.getAmount());
					salesBonus.setTaxRate(contractInfo.getTaxRate());
					salesBonus.setReceiveTotal(contractReceiveTotal);
					salesBonus.setTaxes(contractTaxes);
					//公摊成本
					String key = deptIdTypeMap.get(contractInfo.getDeptId()) + "-" + contractType;
					Double sbShareCostRate = StringUtil.nullToDouble(shareCostRateMap.get(key));
					Double sbShareCost = contractInfo.getAmount()*(sbShareCostRate/100);
					salesBonus.setShareCost(sbShareCost);
					salesBonus.setThirdPartyPurchase(contractThirdPartyPurchase);
					//奖金基数
					Double sbBonusBasis = contractReceiveTotal - contractTaxes - sbShareCost - contractThirdPartyPurchase;
					salesBonus.setBonusBasis(sbBonusBasis);
					//奖金比例
					Double sbBonusRate = StringUtil.nullToDouble(bonusRateMap.get(key));
					salesBonus.setBonusRate(sbBonusRate);
					//本期奖金
					Double sbCurrentBonus = sbBonusBasis*sbBonusRate/100;
					salesBonus.setCurrentBonus(sbCurrentBonus);
					salesBonus.setCreator(creator);
					salesBonus.setCreateTime(ZonedDateTime.now());
					salesBonusRepository.save(salesBonus);
				}else{
					log.info("No Sales Founded belong to contract : " + contractInfo.getSerialNum());
				}
				log.info("====end generate Sales Bonus belong to Contract : "+contractInfo.getSerialNum()+"========");
				//TODO 咨询奖金
				log.info("====begin generate Consultants Bonus belong to Contract : "+contractInfo.getSerialNum()+"=======");
				if(contractInfo.getConsultantsId() != null && contractInfo.getConsultants() != null){
					ConsultantsBonus consultantsBonus = new ConsultantsBonus();
					consultantsBonus.setStatWeek(statWeek);
					consultantsBonus.setContractId(contractId);
					consultantsBonus.setContractAmount(contractInfo.getAmount());
					consultantsBonus.setConsultantsId(contractInfo.getConsultantsId());
					consultantsBonus.setConsultants(contractInfo.getConsultants());
					//公摊成本
					String key = deptIdTypeMap.get(contractInfo.getConsultantsDeptId()) +"-"+contractType;
					Double cbShareCostRate = StringUtil.nullToDouble(shareCostRateMap.get(key));
					Double cbShareCost = contractInfo.getAmount()*(cbShareCostRate/100);
					//奖金基数
					Double cbBonusBasis =  contractReceiveTotal - contractTaxes - cbShareCost - contractThirdPartyPurchase;
					consultantsBonus.setBonusBasis(cbBonusBasis);
					//奖金比例
					Double cbBonusRate = StringUtil.nullToDouble(bonusRateMap.get(key));
					consultantsBonus.setBonusRate(cbBonusRate);
					//项目分润比例
					Double cbConsultantsShareRate = StringUtil.nullToDouble(contractInfo.getConsultantsShareRate());
					consultantsBonus.setConsultantsShareRate(cbConsultantsShareRate);
					//本期奖金
					Double cbCurrentBonus = cbBonusBasis*(cbBonusRate/100)*(cbConsultantsShareRate/100);
					consultantsBonus.setCurrentBonus(cbCurrentBonus);
					consultantsBonus.setCreator(creator);
					consultantsBonus.setCreateTime(ZonedDateTime.now());
					consultantsBonusRepository.save(consultantsBonus);
				}else{
					log.info("No Consultants Founded belong to contract :" + contractInfo.getSerialNum());
				}
				log.info("====end generate Consultants Bonus belong to Contract : "+contractInfo.getSerialNum()+"=======");
				//TODO 合同下项目
				List<Long> contractDeptTypes = new ArrayList<>();//合同内部门类型列表
				List<ProjectInfo> projectInfos = projectInfoRepository.findAllByContractId(contractId);
				if(projectInfos != null && projectInfos.size() > 0){
					Map<String, ProjectSupportCost> lastSupportCostMap = new HashMap<>();//截止到上上一周的记录
					List<ProjectSupportCost> projectSupportCosts2 =projectSupportCostRepository.findByContractIdAndStatWeek(contractId, lastStatWeek);
					if(projectSupportCosts2 != null && projectSupportCosts2.size() > 0){
						for(ProjectSupportCost projectSupportCost : projectSupportCosts2){
							String key = projectSupportCost.getDeptType()+"-"+projectSupportCost.getUserId();
							lastSupportCostMap.put(key, projectSupportCost);
						}
					}
					Map<String, ProjectSupportCost> thisSupportCostMap = new HashMap<>();//本周统计新增的
					for(ProjectInfo projectInfo : projectInfos){
						log.info("====begin generate Project Support Cost "+projectInfo.getSerialNum()+"===");
						Long projectId = projectInfo.getId();
						//部门类型
						Long deptType = deptIdTypeMap.get(projectInfo.getDeptId());
						if(deptType != null && !contractDeptTypes.contains(deptType)){
							contractDeptTypes.add(deptType);
						}
						//TODO 统计项目支撑成本
						//上一周的（统计都是当前统计上一周的）
						List<UserTimesheet> userTimesheets = userTimesheetRepository.findByTypeAndObjIdAndEndDay(UserTimesheet.TYPE_PROJECT, projectId, statWeek);
						if(userTimesheets != null && userTimesheets.size() > 0){
							for(UserTimesheet userTimesheet : userTimesheets){
								User user = userRepository.findOne(userTimesheet.getUserId());
								if(user == null){
									continue;
								}
								String key = deptType +"-"+user.getId();
								
								ProjectSupportCost projectSupportCost = new ProjectSupportCost();
								projectSupportCost.setStatWeek(statWeek);
								projectSupportCost.setContractId(contractId);
								projectSupportCost.setProjectId(projectId);
								projectSupportCost.setDeptType(deptType);
								projectSupportCost.setUserId(userTimesheet.getUserId());
								projectSupportCost.setSerialNum(user.getSerialNum());
								projectSupportCost.setUserName(userTimesheet.getUserName());
								projectSupportCost.setGrade(user.getGrade() == null ? 1 : user.getGrade());
								//结算成本
								Double settlementCost = StringUtil.nullToDouble(externalQuotationMap.get(user.getGrade()));
								projectSupportCost.setSettlementCost(settlementCost);
								//项目工时
								Double thisProjectHourCost = 0D;
								List<UserTimesheet> userTimesheets2 = null;
								if(lastSupportCostMap.get(key) != null){//上上周有记录，查询上一周
									userTimesheets2 = userTimesheetRepository.findByUserIdAndTypeAndObjIdAndTime(user.getId(), UserTimesheet.TYPE_PROJECT, projectId, fDay, statWeek);
								}else{//查询当前用户截止到上一周的所有日报
									userTimesheets2 = userTimesheetRepository.findByUserIdAndTypeAndObjIdAndWorkDay(user.getId(), UserTimesheet.TYPE_PROJECT, projectId, statWeek);
								}
								if(userTimesheets != null && userTimesheets.size() > 0){
									for(UserTimesheet userTimesheet2 : userTimesheets2){
										thisProjectHourCost += userTimesheet2.getRealInput();
									}
								}else{
									log.info("No UserTimesheet founded belong to User: " + user.getLastName() + " Project : " + projectInfo.getSerialNum());
								}
								projectSupportCost.setProjectHourCost(thisProjectHourCost);
								//内部采购成本
								Double thisInternalBudgetCost = thisProjectHourCost*settlementCost;
								projectSupportCost.setInternalBudgetCost(thisInternalBudgetCost);
								//工资,社保公积金,其他费用
								Double sal = 0D;
								Double socialSecurityFund = 0D;
								Double otherExpense = 0D;
								UserCost userCost = userCostRepository.findMaxByCostMonthAndUserId(costMonth, user.getId());
								if(userCost != null){
									sal = StringUtil.nullToDouble(userCost.getSal());
									socialSecurityFund = StringUtil.nullToDouble(userCost.getSocialSecurityFund());
									otherExpense = StringUtil.nullToDouble(userCost.getOtherExpense());
								}else{
									log.info("No UserCost founded belong to " + user.getLastName());
								}
								projectSupportCost.setSal(sal);
								projectSupportCost.setSocialSecurityFund(socialSecurityFund);
								projectSupportCost.setOtherExpense(otherExpense);
								//单人月成本小计
								Double userMonthCost = sal + socialSecurityFund + otherExpense;
								projectSupportCost.setUserMonthCost(userMonthCost);
								//工时成本
								Double userHourCost = userMonthCost/168;
								projectSupportCost.setUserHourCost(userHourCost);
								//生产成本合计
								Double thisProductCost = userHourCost*thisProjectHourCost;
								projectSupportCost.setProductCost(thisProductCost);
								//生产毛利
								Double grossProfit = thisInternalBudgetCost - thisProductCost;
								projectSupportCost.setGrossProfit(grossProfit);
								projectSupportCost.setCreator(creator);
								projectSupportCost.setCreateTime(ZonedDateTime.now());
								
								ProjectSupportCost oldProjectSupportCost = thisSupportCostMap.get(key);
								if(oldProjectSupportCost != null){//将上一个项目中的成本等加到当前项目中去
									projectSupportCost.setProjectHourCost(projectSupportCost.getProjectHourCost() + oldProjectSupportCost.getProjectHourCost());
									projectSupportCost.setInternalBudgetCost(projectSupportCost.getInternalBudgetCost() + oldProjectSupportCost.getInternalBudgetCost());
									projectSupportCost.setProductCost(projectSupportCost.getProductCost() + oldProjectSupportCost.getProductCost());
									projectSupportCost.setGrossProfit(projectSupportCost.getGrossProfit() + oldProjectSupportCost.getGrossProfit());
									projectSupportCost.setProjectId(oldProjectSupportCost.getProjectId());//以最新的一个项目ID为准
								}
								thisSupportCostMap.put(key, projectSupportCost);
							}
						}else{
							log.info("No UserTimeSheet Founded Belong to : " + projectInfo.getSerialNum());
						}
						
						//TODO 项目支撑奖金
						log.info("====begin generate Project Support Bonus "+projectInfo.getSerialNum()+"========");
						ProjectSupportBonus projectSupportBonus = new ProjectSupportBonus();
						projectSupportBonus.setStatWeek(statWeek);
						projectSupportBonus.setContractId(contractId);
						projectSupportBonus.setProjectId(projectId);
						projectSupportBonus.setDeptType(deptType);
						projectSupportBonus.setPmId(projectInfo.getPmId());
						projectSupportBonus.setPmName(projectInfo.getPm());
						//项目确认交付时间
						int deliveryTime = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getEndDay().toInstant())) + 1;
						projectSupportBonus.setDeliveryTime(deliveryTime);
						//验收节点
						Double acceptanceRate = contractFinishRate;
						projectSupportBonus.setAcceptanceRate(acceptanceRate);
						//计划天数
						Double planDays = (deliveryTime*acceptanceRate)/100;
						projectSupportBonus.setPlanDays(planDays);
						//实际使用天数
						int realDays = 0;
						if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), DateUtil.lastSundayEnd(statTime)) + 1;
						}else{
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getUpdateTime().toInstant())) + 1;
						}
						projectSupportBonus.setRealDays(realDays);
						//奖金调节比率
						Double bonusAdjustRate = ((planDays/realDays)-1)*100;
						projectSupportBonus.setBonusAdjustRate(bonusAdjustRate);
						//奖金比率
						String key = deptType + "-" + contractType;
						Double bonusRate = StringUtil.nullToDouble(bonusRateMap.get(key));
						projectSupportBonus.setBonusRate(bonusRate);
						//奖金确认比例
						Double bonusAcceptanceRate = (bonusRate/100)*(1+(bonusAdjustRate/100))*(acceptanceRate/100)*100;
						projectSupportBonus.setBonusAcceptanceRate(bonusAcceptanceRate);
						//合同金额
						Double contractAmount = contractInfo.getAmount();
						projectSupportBonus.setContractAmount(contractAmount);
						//税率
						Double taxRate = contractInfo.getTaxRate();
						projectSupportBonus.setTaxRate(taxRate);
						//公摊成本
						Double psbShareCost = 0D;
						Double psbShareCostRate = StringUtil.nullToDouble(shareCostRateMap.get(key));
						psbShareCost = contractAmount*(psbShareCostRate/100);
						//奖金基数
						Double bonusBasis = contractReceiveTotal - contractTaxes - psbShareCost - contractThirdPartyPurchase;
						projectSupportBonus.setBonusBasis(bonusBasis);
						//当期奖金
						Double currentBonus = (bonusAcceptanceRate/100)*bonusBasis;
						projectSupportBonus.setCurrentBonus(currentBonus);
						projectSupportBonus.setCreator(creator);
						projectSupportBonus.setCreateTime(ZonedDateTime.now());
						projectSupportBonusRepository.save(projectSupportBonus);
						log.info("====end generate Project Support Bonus "+projectInfo.getSerialNum()+"========");
					}
					
					//TODO 项目支撑成本
					List<ProjectSupportCost> saveList = new ArrayList<ProjectSupportCost>();
					for(String key : thisSupportCostMap.keySet()){//每个部门类型中的每个用户都有一条记录
						if(lastSupportCostMap.containsKey(key)){
							ProjectSupportCost thisSupportCost = thisSupportCostMap.get(key);
							ProjectSupportCost lastSupportCost = lastSupportCostMap.get(key);
							//项目工时
							thisSupportCost.setProjectHourCost(thisSupportCost.getProjectHourCost() + lastSupportCost.getProjectHourCost());
							//内部采购成本
							thisSupportCost.setInternalBudgetCost(thisSupportCost.getInternalBudgetCost() + lastSupportCost.getInternalBudgetCost());
							//生产成本合计
							thisSupportCost.setProductCost(thisSupportCost.getProductCost() + lastSupportCost.getProductCost());
							//生产毛利
							thisSupportCost.setGrossProfit(thisSupportCost.getGrossProfit() + lastSupportCost.getGrossProfit());
							
							thisSupportCostMap.put(key, thisSupportCost);
							lastSupportCostMap.remove(key);
						}
						saveList.add(thisSupportCostMap.get(key));
					}
					//添加上上周有记录的，上周没有记录的
					for(String key : lastSupportCostMap.keySet()){
						ProjectSupportCost lastSupportCost = lastSupportCostMap.get(key);
						lastSupportCost.setId(null);
						lastSupportCost.setStatWeek(statWeek);
						saveList.add(lastSupportCost);
					}
					projectSupportCostRepository.save(saveList);
					log.info("====end generate Project Support Cost ");
				}else{
					log.info("No ProjectInfo Founded belong to Contract : " + contractInfo.getSerialNum());
				}
				//产品销售奖金
//				log.info("====begin generate Product Sales Bonus to Contract : "+contractInfo.getSerialNum()+"======");
//				List<DeptType> deptTypes = deptTypeRepository.findByContractIdAndContractBudgetAndPurchaseItem(contractId,PurchaseItem.TYPE_SOFTWARE,PurchaseItem.SOURCE_INTERNAL);
//				if(deptTypes != null && deptTypes.size() > 0){
//					for(DeptType deptType : deptTypes){
//						ProductSalesBonus productSalesBonus = new ProductSalesBonus();
//						productSalesBonus.setStatWeek(statWeek);
//						productSalesBonus.setContractId(contractId);
//						productSalesBonus.setDeptType(deptType.getId());
//						//合同确认交付时间
//						Integer psbDeliveryTime = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getEndDay().toInstant())) + 1;
//						productSalesBonus.setDeliveryTime(psbDeliveryTime);
//						//验收节点
//						Double psbAcceptanceRate = contractFinishRate;
//						productSalesBonus.setAcceptanceRate(psbAcceptanceRate);
//						//计划天数
//						Double psbPlanDays = (psbDeliveryTime*psbAcceptanceRate)/100;
//						productSalesBonus.setPlanDays(psbPlanDays);
//						//实际使用天数
//						Integer psbRealDays = 0;
//						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
//							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), DateUtil.lastSundayEnd()) + 1;
//						}else{
//							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getUpdateTime().toInstant())) + 1;
//						}
//						productSalesBonus.setRealDays(psbRealDays);
//						//奖金调节比率
//						Double psbBonusAdjustRate = ((psbPlanDays/psbRealDays)-1)*100;
//						productSalesBonus.setBonusAdjustRate(psbBonusAdjustRate);
//						//奖金比率
//						Double psbBonusRate = 0D;
//						BonusRate br = bonusRateRepository.findByDeptTypeAndContractType(deptType.getId(),contractType);
//						if(br != null){
//							psbBonusRate = br.getRate();
//						}else{
//							log.info("No BonusRate Founded belong to DeptType " + deptType.getId());
//						}
//						productSalesBonus.setBonusRate(psbBonusRate);
//						//奖金确认比例
//						Double psbBonusAcceptanceRate = (psbBonusRate/100)*(1+(psbBonusAdjustRate/100))*(psbAcceptanceRate/100)*100;
//						productSalesBonus.setBonusAcceptanceRate(psbBonusAcceptanceRate);
//						//奖金基数
//						Double psbBonusBasis = 0D;
//						List<PurchaseItem> purchaseItems2 = purchaseItemRepository.findByContractIdAndDeptTypeAndSourceAndType(contractId,deptType.getId(), PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE);
//						if(purchaseItems != null && purchaseItems2.size() > 0){
//							for(PurchaseItem purchaseItem : purchaseItems2){
//								ShareInfo shareInfo = shareInfoRepository.findByProductPriceIdAndDeptId(purchaseItem.getProductPriceId(), contractBudgetRepository.findOne(purchaseItem.getBudgetId()).getDeptId());
//								if(shareInfo == null){
//									continue;
//								}
//								psbBonusBasis += purchaseItem.getTotalAmount()*(StringUtil.nullToDouble(shareInfo.getShareRate())/100);
//							}
//						}else{
//							log.info("No Purchase Item founded belong to Contract " + contractInfo.getSerialNum());
//						}
//						productSalesBonus.setBonusBasis(psbBonusBasis);
//						//当期奖金
//						Double psbCurrentBonus = (psbBonusAcceptanceRate/100)*psbBonusBasis;
//						productSalesBonus.setCurrentBonus(psbCurrentBonus);
//						productSalesBonus.setCreator(creator);
//						productSalesBonus.setCreateTime(ZonedDateTime.now());
//						productSalesBonusRepository.save(productSalesBonus);
//						
//					}
//				}else{
//					log.info("No deptType founded");
//				}
//				log.info("====end generate Product Sales Bonus to Contract : "+contractInfo.getSerialNum()+"======");
				
				//TODO 项目总体情况控制表
				log.info("====begin generate Project Overall to Contract : "+contractInfo.getSerialNum()+"=======");
				ProjectOverall projectOverall = new ProjectOverall();
				projectOverall.setStatWeek(statWeek);
				//合同负责人
				Long poContractResponse = 0L;
				if(contractInfo.getSalesmanId() != null){
					poContractResponse = contractInfo.getSalesmanId();
				}else{
					poContractResponse = contractInfo.getConsultantsId();
				}
				projectOverall.setContractResponse(poContractResponse);
				projectOverall.setContractId(contractId);
				//合同金额
				Double poContractAmount = contractInfo.getAmount();
				projectOverall.setContractAmount(poContractAmount);
				//税率
				Double poTaxRate = contractInfo.getTaxRate();
				projectOverall.setTaxRate(poTaxRate);
				//可确认收入
				Double poIdentifiableIncome = poContractAmount/(1+(poTaxRate/100));
				projectOverall.setIdentifiableIncome(poIdentifiableIncome);
				//合同完成节点
				Double poContractFinishRate = contractFinishRate;
				projectOverall.setContractFinishRate(poContractFinishRate);
				//收入确认
				Double poAcceptanceIncome = poIdentifiableIncome*(poContractFinishRate/100);
				projectOverall.setAcceptanceIncome(poAcceptanceIncome);
				//收款金额
				Double poReceiveTotal = contractReceiveTotal;
				projectOverall.setReceiveTotal(poReceiveTotal);
				//应收账款
				Double poReceivableAccount = poContractAmount*(poContractFinishRate/100) - poReceiveTotal;
				projectOverall.setReceivableAccount(poReceivableAccount);
				//公摊成本
				Double poShareCost = contractInfo.getShareCost();
				projectOverall.setShareCost(poShareCost);
				//第三方采购
				Double poThirdPartyPurchase = contractThirdPartyPurchase;
				projectOverall.setThirdPartyPurchase(poThirdPartyPurchase);
				//内部采购总额
				Double poInternalPurchase = 0D;
				poInternalPurchase += StringUtil.nullToDouble(projectSupportCostRepository.findSumGrossProfitByContractIdAndStatWeek(contractId, statWeek));
//				poInternalPurchase += StringUtil.nullToDouble(productSalesBonusRepository.findSumBonusBasisByContractIdAndStatWeek(contractId, statWeek));
				projectOverall.setInternalPurchase(poInternalPurchase);
				//当期销售奖金
				Double poSalesBonus = 0D;
				poSalesBonus += StringUtil.nullToDouble(salesBonusRepository.findSumCurrentBonusByContractIdAndStatWeek(contractId, statWeek));
				//当期业务咨询奖金
				Double poConsultantsBonus = 0D;
				poConsultantsBonus += StringUtil.nullToDouble(consultantsBonusRepository.findSumCurrentBonusByContractIdAndStatWeek(contractId, statWeek));
				//当期项目奖金
				Double poProjectSupportBonus = 0D;
				poProjectSupportBonus += StringUtil.nullToDouble(projectSupportBonusRepository.findSumCurrentBonusByContractIdAndStatWeek(contractId, statWeek));
				Double poProductSalesBonus = 0D;
				poProductSalesBonus += StringUtil.nullToDouble(productSalesBonusRepository.findSumCurrentBonusByContractIdAndStatWeek(contractId, statWeek));
				//奖金
				Double poBonus = poSalesBonus + poConsultantsBonus + poProjectSupportBonus + poProductSalesBonus;
				projectOverall.setBonus(poBonus);
				//毛利
				Double poGrossProfit = poIdentifiableIncome*(poContractFinishRate/100) - poShareCost - poThirdPartyPurchase - poInternalPurchase - poBonus;
				projectOverall.setGrossProfit(poGrossProfit);
				//毛利率
				Double poGrossProfitRate = 0D;
				if(poIdentifiableIncome*poContractFinishRate != 0){
					poGrossProfitRate = (poGrossProfit/(poIdentifiableIncome*(poContractFinishRate/100)))*100;
				}
				projectOverall.setGrossProfitRate(poGrossProfitRate);
				projectOverall.setCreator(creator);
				projectOverall.setCreateTime(ZonedDateTime.now());
				projectOverallRepository.save(projectOverall);
				log.info("====end generate Project Overall to Contract : "+contractInfo.getSerialNum()+"=======");
				
				//TODO 奖金总表
				log.info("====begin generate Bonus to Contract : "+contractInfo.getSerialNum()+"=======");
				Bonus bonus = new Bonus();
				bonus.setStatWeek(statWeek);
				bonus.setContractId(contractId);
				bonus.setContractAmount(contractInfo.getAmount());
				bonus.setSalesBonus(poSalesBonus);
				bonus.setProjectBonus(poProjectSupportBonus + poProductSalesBonus);
				bonus.setConsultantsBonus(poConsultantsBonus);
				bonus.setBonusTotal(poBonus);
				bonus.setCreator(creator);
				bonus.setCreateTime(ZonedDateTime.now());
				bonusRepository.save(bonus);
				log.info("====end generate Bonus to Contract : "+contractInfo.getSerialNum()+"=======");
				
				log.info("====begin generate Contract Internal Purchase & Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"=====");
				ProjectOverall projectOverall2 = projectOverallRepository.findByContractIdAndStatWeek(contractId, statWeek);
				Bonus bonus2 = bonusRepository.findByContractIdAndStatWeek(contractId, statWeek);
				for(int i = 0; i < contractDeptTypes.size(); i++){
					//TODO 合同内部采购信息
					ContractInternalPurchase contractInternalPurchase = new ContractInternalPurchase();
					contractInternalPurchase.setStatWeek(statWeek);
					//项目总体控制表主键
					contractInternalPurchase.setProjectOverallId(projectOverall2.getId());
					contractInternalPurchase.setContractId(contractId);
					//部门类型主键
					contractInternalPurchase.setDeptType(contractDeptTypes.get(i));
					//总金额
					Double cipTotalAmount = 0D;
					cipTotalAmount += StringUtil.nullToDouble(projectSupportCostRepository.findSumGrossProfitByContractIdAndDeptTypeAndStatWeek(contractId, contractDeptTypes.get(i), statWeek));
					contractInternalPurchase.setTotalAmount(cipTotalAmount);
					contractInternalPurchase.setCreator(creator);
					contractInternalPurchase.setCreateTime(ZonedDateTime.now());
					contractInternalPurchaseRepository.save(contractInternalPurchase);
					//TODO 合同项目奖金
					ContractProjectBonus contractProjectBonus = new ContractProjectBonus();
					contractProjectBonus.setStatWeek(statWeek);
					contractProjectBonus.setBonusId(bonus2.getId());
					contractProjectBonus.setContractId(contractId);
					contractProjectBonus.setDeptType(contractDeptTypes.get(i));
					//奖金合计
					Double cpbBonus = 0D;
					cpbBonus += StringUtil.nullToDouble(projectSupportBonusRepository.findSumCurrentBonusByContractIdAndDeptTypeAndStatWeek(contractId, contractDeptTypes.get(i), statWeek));
					contractProjectBonus.setBonus(cpbBonus);
					contractProjectBonus.setCreator(creator);
					contractProjectBonus.setCreateTime(ZonedDateTime.now());
					contractProjectBonusRepository.save(contractProjectBonus);
				}
				log.info("====end generate Contract Internal Purchase & Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"=====");
			}
		}else{
			log.info("No ContractInfo Founded");
		}
		log.info("=====end Account Scheduled=====statTime:" + DateUtil.formatDate(DateUtil.DATE_FORMAT, statTime));
	}
}
