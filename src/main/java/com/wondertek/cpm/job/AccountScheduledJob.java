package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractInternalPurchase;
import com.wondertek.cpm.domain.ContractProjectBonus;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.ExternalQuotation;
import com.wondertek.cpm.domain.ProductSalesBonus;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.ShareInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.BonusRateRepository;
import com.wondertek.cpm.repository.BonusRepository;
import com.wondertek.cpm.repository.ConsultantsBonusRepository;
import com.wondertek.cpm.repository.ContractBudgetRepository;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ContractInternalPurchaseRepository;
import com.wondertek.cpm.repository.ContractProjectBonusRepository;
import com.wondertek.cpm.repository.ContractReceiveRepository;
import com.wondertek.cpm.repository.DeptInfoRepository;
import com.wondertek.cpm.repository.DeptTypeRepository;
import com.wondertek.cpm.repository.ExternalQuotationRepository;
import com.wondertek.cpm.repository.ProductSalesBonusRepository;
import com.wondertek.cpm.repository.ProjectInfoRepository;
import com.wondertek.cpm.repository.ProjectOverallRepository;
import com.wondertek.cpm.repository.ProjectSupportBonusRepository;
import com.wondertek.cpm.repository.ProjectSupportCostRepository;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.SalesBonusRepository;
import com.wondertek.cpm.repository.ShareInfoRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class AccountScheduledJob {
	
	private Logger log = LoggerFactory.getLogger(AccountScheduledJob.class);
	
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
	
	@Inject
	private ContractBudgetRepository contractBudgetRepository;
	
	@Inject
	private ContractInternalPurchaseRepository contractInternalPurchaseRepository;
	
	@Inject
	private ContractProjectBonusRepository contractProjectBonusRepository;
	
	@Inject
	private DeptInfoRepository deptInfoRepository;
	
	@Inject
	private DeptTypeRepository deptTypeRepository;
	
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
	
	@Inject
	private ShareInfoRepository shareInfoRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserTimesheetRepository userTimesheetRepository;
	
	@Inject
	private UserCostRepository userCostRepository;
	
	protected void AccountScheduled(){
		log.info("=====begin Account Scheduled=====");
		String [] dates = DateUtil.getWholeWeekByDate(DateUtil.lastSaturday());
		ZonedDateTime beginTime = DateUtil.getZonedDateTime(DateUtil.lastMonday().getTime());
		ZonedDateTime endTime = DateUtil.getZonedDateTime(DateUtil.lastSundayEnd().getTime());
		Long lastStatWeek = StringUtil.nullToLong(DateUtil.getWholeWeekByDate(DateUtil.addDayNum(-7, DateUtil.lastSundayEnd()))[6]);
		Long fDay = StringUtil.nullToLong(dates[0]);
		Long statWeek = StringUtil.nullToLong(dates[6]);
		Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd()).toString());
		String creator = "admin";
		//初始化
		List<ProjectSupportCost> projectSupportCosts = projectSupportCostRepository.findByStatWeek(statWeek);
		if(projectSupportCosts != null){
			projectSupportCostRepository.delete(projectSupportCosts);
		}
		List<ProjectSupportBonus> projectSupportBonuses = projectSupportBonusRepository.findByStatWeek(statWeek);
		if(projectSupportBonuses != null){
			projectSupportBonusRepository.delete(projectSupportBonuses);
		}
		List<ProductSalesBonus> productSalesBonuses = productSalesBonusRepository.findByStatWeek(statWeek);
		if(productSalesBonuses != null){
			productSalesBonusRepository.delete(productSalesBonuses);
		}
		List<ContractInternalPurchase> contractInternalPurchases = contractInternalPurchaseRepository.findByStatWeek(statWeek);
		if(contractInternalPurchases != null){
			contractInternalPurchaseRepository.delete(contractInternalPurchases);
		}
		List<SalesBonus> salesBonuses = salesBonusRepository.findByStatWeek(statWeek);
		if(salesBonuses != null){
			salesBonusRepository.delete(salesBonuses);
		}
		List<ConsultantsBonus> consultantsBonuses = consultantsBonusRepository.findByStatWeek(statWeek);
		if(consultantsBonuses != null){
			consultantsBonusRepository.delete(consultantsBonuses);
		}
		List<ContractProjectBonus> contractProjectBonuses = contractProjectBonusRepository.findByStatWeek(statWeek);
		if(contractProjectBonuses != null){
			contractProjectBonusRepository.delete(contractProjectBonuses);
		}
		List<Bonus> bonuses = bonusRepository.findByStatWeek(statWeek);
		if(bonuses != null){
			bonusRepository.delete(bonuses);
		}
		List<ProjectOverall> projectOveralls = projectOverallRepository.findByStatWeek(statWeek);
		if(projectOveralls != null){
			projectOverallRepository.delete(projectOveralls);
		}
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrUpdateTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				Long contractId = contractInfo.getId();
				Integer contractType = contractInfo.getType();
				List<ProjectInfo> projectInfos = projectInfoRepository.findByContractIdAndStatusOrUpdateTime(contractId, ProjectInfo.STATUS_ADD, beginTime, endTime);
				if(projectInfos != null && projectInfos.size() > 0){
					for(ProjectInfo projectInfo : projectInfos){
						log.info("====begin generate Project Support Cost "+projectInfo.getSerialNum()+"===");
						Long projectId = projectInfo.getId();
						DeptInfo projectDept = deptInfoRepository.findOne(projectInfo.getDeptId());
						//上周报告
						Map<Long, Long> lastCostMap = new HashMap<>();
						List<ProjectSupportCost> projectSupportCosts2 =projectSupportCostRepository.findByContractIdAndDeptTypeAndStatWeek(contractId, projectDept.getType(), lastStatWeek);
						if(projectSupportCosts2 != null){
							for(ProjectSupportCost projectSupportCost : projectSupportCosts2){
								lastCostMap.put(projectSupportCost.getUserId(), 1L);
							}
						}
						List<UserTimesheet> userTimesheets = userTimesheetRepository.findByTypeAndObjIdAndEndDay(UserTimesheet.TYPE_PROJECT, projectId, statWeek);
						if(userTimesheets != null && userTimesheets.size() > 0){
							for(UserTimesheet userTimesheet : userTimesheets){
								User user = userRepository.findOne(userTimesheet.getUserId());
								if(user == null){
									continue;
								}
								ProjectSupportCost projectSupportCost = new ProjectSupportCost();
								projectSupportCost.setStatWeek(statWeek);
								projectSupportCost.setContractId(contractId);
								projectSupportCost.setDeptType(projectDept.getType());
								projectSupportCost.setUserId(userTimesheet.getUserId());
								projectSupportCost.setSerialNum(user.getSerialNum());
								projectSupportCost.setUserName(userTimesheet.getUserName());
								projectSupportCost.setGrade(user.getGrade());
								//结算成本
								Double settlementCost = 0D;
								ExternalQuotation externalQuotation = externalQuotationRepository.findByGrade(user.getGrade());
								if(externalQuotation != null){
									settlementCost = StringUtil.nullToDouble(externalQuotation.getHourCost());
								}
								projectSupportCost.setSettlementCost(settlementCost);
								if(lastCostMap.containsKey(user.getId())){
									//上周统计
									ProjectSupportCost projectSupportCost2 = projectSupportCostRepository.findByContractIdAndDeptTypeAndUserIdAndStatWeek(contractId, projectDept.getType(), user.getId(), lastStatWeek);
									//项目工时
									Double thisProjectHourCost = 0D;
									List<UserTimesheet> userTimesheets2 = userTimesheetRepository.findByUserIdAndTypeAndObjIdAndTime(user.getId(), UserTimesheet.TYPE_PROJECT, projectId, fDay, statWeek);
									if(userTimesheets != null && userTimesheets.size() > 0){
										for(UserTimesheet userTimesheet2 : userTimesheets2){
											thisProjectHourCost += userTimesheet2.getRealInput();
										}
									}else{
										log.info("No UserTimesheet founded belong to User: " + user.getLastName() + " Project : " + projectInfo.getSerialNum());
									}
									Double projectHourCost = StringUtil.nullToDouble(projectSupportCost2.getProjectHourCost()) + thisProjectHourCost;
									projectSupportCost.setProjectHourCost(projectHourCost);
									//内部采购成本
									Double thisInternalBudgetCost = thisProjectHourCost*settlementCost;
									Double internalBudgetCost = thisInternalBudgetCost + StringUtil.nullToDouble(projectSupportCost2.getInternalBudgetCost());
									projectSupportCost.setInternalBudgetCost(internalBudgetCost);
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
									Double productCost = StringUtil.nullToDouble(projectSupportCost2.getProductCost()) + thisProductCost;
									projectSupportCost.setProductCost(productCost);
									//生产毛利
									Double grossProfit = internalBudgetCost - productCost;
									projectSupportCost.setGrossProfit(grossProfit);
								}else{
									//项目工时
									Double projectHourCost = 0D;
									List<UserTimesheet> userTimesheets2 = userTimesheetRepository.findByUserIdAndTypeAndObjIdAndWorkDay(user.getId(), UserTimesheet.TYPE_PROJECT, projectId, statWeek);
									if(userTimesheets != null && userTimesheets.size() > 0){
										for(UserTimesheet userTimesheet2 : userTimesheets2){
											projectHourCost += userTimesheet2.getRealInput();
										}
									}else{
										log.info("No UserTimesheet founded belong to User: " + user.getLastName() + " Project : " + projectInfo.getSerialNum());
									}
									projectSupportCost.setProjectHourCost(projectHourCost);
									//内部采购成本
									Double internalBudgetCost = projectHourCost*settlementCost;
									projectSupportCost.setInternalBudgetCost(internalBudgetCost);
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
									Double productCost = userHourCost*projectHourCost;
									projectSupportCost.setProductCost(productCost);
									//生产毛利
									Double grossProfit = internalBudgetCost - productCost;
									projectSupportCost.setGrossProfit(grossProfit);
								}
								projectSupportCost.setCreator(creator);
								projectSupportCost.setCreateTime(ZonedDateTime.now());
								projectSupportCostRepository.save(projectSupportCost);
							}
						}else{
							log.info("No UserTimeSheet Founded Belong to : " + projectInfo.getSerialNum());
						}
						log.info("====end generate Project Support Cost "+projectInfo.getSerialNum()+"===");
						
						log.info("====begin generate Project Support Bonus "+projectInfo.getSerialNum()+"========");
						ProjectSupportBonus projectSupportBonus = new ProjectSupportBonus();
						projectSupportBonus.setStatWeek(statWeek);
						projectSupportBonus.setContractId(contractId);
						Long deptType = projectDept.getType();
						projectSupportBonus.setDeptType(deptType);
						projectSupportBonus.setPmId(projectInfo.getPmId());
						projectSupportBonus.setPmName(projectInfo.getPm());
						//项目确认交付时间
						int deliveryTime = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getEndDay().toInstant())) + 1;
						projectSupportBonus.setDeliveryTime(deliveryTime);
						//验收节点
						Double acceptanceRate = contractInfo.getFinishRate();
						projectSupportBonus.setAcceptanceRate(acceptanceRate);
						//计划天数
						Double planDays = (deliveryTime*acceptanceRate)/100;
						projectSupportBonus.setPlanDays(planDays);
						//实际使用天数
						int realDays = 0;
						if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), DateUtil.lastSundayEnd()) + 1;
						}else{
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getUpdateTime().toInstant())) + 1;
						}
						projectSupportBonus.setRealDays(realDays);
						//奖金调节比率
						Double bonusAdjustRate = ((planDays/realDays)-1)*100;
						projectSupportBonus.setBonusAdjustRate(bonusAdjustRate);
						//奖金比率
						Double bonusRate = 0D; 
						BonusRate br = bonusRateRepository.findByDeptTypeAndContractType(deptType, contractType);
						if(br != null){
							bonusRate = br.getRate();
						}else{
							log.info("No BonusRate Founded belong to DeptType " + deptType);
						}
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
						//确认收入
						Double acceptanceIncome = (contractAmount/(1+(taxRate/100)))*(acceptanceRate/100);
						projectSupportBonus.setAcceptanceIncome(acceptanceIncome);
						//成本
						Double cost = 0D;
						if(contractType == ContractInfo.TYPE_PROJECT){
							cost = contractInfo.getShareCost();
						}else if(contractType == ContractInfo.TYPE_INTERNAL){
							cost += StringUtil.nullToDouble(projectSupportCostRepository.findSumProductCostByContractIdAndDeptTypeAndStatWeek(contractId,DeptType.PRODUCT_DEVELOPMENT, statWeek));
							cost += StringUtil.nullToDouble(projectSupportCostRepository.findSumProductCostByContractIdAndDeptTypeAndStatWeek(contractId, DeptType.PROJECT_IMPLEMENTATION, statWeek));
						}
						projectSupportBonus.setCost(cost);
						//奖金基数
						Double bonusBasis = acceptanceIncome - cost;
						projectSupportBonus.setBonusBasis(bonusBasis);
						//当期奖金
						Double currentBonus = (bonusAcceptanceRate/100)*bonusBasis;
						projectSupportBonus.setCurrentBonus(currentBonus);
						projectSupportBonus.setCreator(creator);
						projectSupportBonus.setCreateTime(ZonedDateTime.now());
						projectSupportBonusRepository.save(projectSupportBonus);
						log.info("====end generate Project Support Bonus "+projectInfo.getSerialNum()+"========");
					}
				}else{
					log.info("No ProjectInfo Founded belong to Contract : " + contractInfo.getSerialNum());
				}
				log.info("====begin generate Product Sales Bonus to Contract : "+contractInfo.getSerialNum()+"======");
				List<DeptType> deptTypes = deptTypeRepository.findByContractIdAndContractBudgetAndPurchaseItem(contractId,PurchaseItem.TYPE_SOFTWARE,PurchaseItem.SOURCE_INTERNAL);
				if(deptTypes != null && deptTypes.size() > 0){
					for(DeptType deptType : deptTypes){
						ProductSalesBonus productSalesBonus = new ProductSalesBonus();
						productSalesBonus.setStatWeek(statWeek);
						productSalesBonus.setContractId(contractId);
						productSalesBonus.setDeptType(deptType.getId());
						//合同确认交付时间
						Integer psbDeliveryTime = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getEndDay().toInstant())) + 1;
						productSalesBonus.setDeliveryTime(psbDeliveryTime);
						//验收节点
						Double psbAcceptanceRate = contractInfo.getFinishRate();
						productSalesBonus.setAcceptanceRate(psbAcceptanceRate);
						//计划天数
						Double psbPlanDays = (psbDeliveryTime*psbAcceptanceRate)/100;
						productSalesBonus.setPlanDays(psbPlanDays);
						//实际使用天数
						Integer psbRealDays = 0;
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), DateUtil.lastSundayEnd()) + 1;
						}else{
							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getUpdateTime().toInstant())) + 1;
						}
						productSalesBonus.setRealDays(psbRealDays);
						//奖金调节比率
						Double psbBonusAdjustRate = ((psbPlanDays/psbRealDays)-1)*100;
						productSalesBonus.setBonusAdjustRate(psbBonusAdjustRate);
						//奖金比率
						Double psbBonusRate = 0D;
						BonusRate br = bonusRateRepository.findByDeptTypeAndContractType(deptType.getId(),contractType);
						if(br != null){
							psbBonusRate = br.getRate();
						}else{
							log.info("No BonusRate Founded belong to DeptType " + deptType.getId());
						}
						productSalesBonus.setBonusRate(psbBonusRate);
						//奖金确认比例
						Double psbBonusAcceptanceRate = (psbBonusRate/100)*(1+(psbBonusAdjustRate/100))*(psbAcceptanceRate/100)*100;
						productSalesBonus.setBonusAcceptanceRate(psbBonusAcceptanceRate);
						//奖金基数
						Double psbBonusBasis = 0D;
						List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndDeptTypeAndSourceAndType(contractId,deptType.getId(), PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE);
						if(purchaseItems != null && purchaseItems.size() > 0){
							for(PurchaseItem purchaseItem : purchaseItems){
								ShareInfo shareInfo = shareInfoRepository.findByProductPriceIdAndDeptId(purchaseItem.getProductPriceId(), contractBudgetRepository.findOne(purchaseItem.getBudgetId()).getDeptId());
								if(shareInfo == null){
									continue;
								}
								psbBonusBasis += purchaseItem.getTotalAmount()*(StringUtil.nullToDouble(shareInfo.getShareRate())/100);
							}
						}else{
							log.info("No Purchase Item founded belong to Contract " + contractInfo.getSerialNum());
						}
						productSalesBonus.setBonusBasis(psbBonusBasis);
						//当期奖金
						Double psbCurrentBonus = (psbBonusAcceptanceRate/100)*psbBonusBasis;
						productSalesBonus.setCurrentBonus(psbCurrentBonus);
						productSalesBonus.setCreator(creator);
						productSalesBonus.setCreateTime(ZonedDateTime.now());
						productSalesBonusRepository.save(productSalesBonus);
						
					}
				}else{
					log.info("No deptType founded");
				}
				log.info("====end generate Product Sales Bonus to Contract : "+contractInfo.getSerialNum()+"======");
				
				log.info("====begin generate Sales Bonus belong to Contract : "+contractInfo.getSerialNum()+"========");
				//收款金额
				Double sbReceiveTotal = 0D;
				List<ContractReceive> contractReceives = contractReceiveRepository.findAllByContractIdAndCreateTimeBefore(contractId, endTime);
				if(contractReceives != null && contractReceives.size() > 0){
					for(ContractReceive contractReceive : contractReceives){
						sbReceiveTotal += contractReceive.getReceiveTotal();
					}
				}else{
					log.info("No ContractRecevie Founded belong to Contract :" + contractInfo.getSerialNum());
				}
				//税收
				Double sbTaxes = sbReceiveTotal*((contractInfo.getTaxRate()/100)/(1+(contractInfo.getTaxRate()/100)));
				//公摊成本
				Double sbShareCost = contractInfo.getShareCost();
				//第三方采购
				Double sbThirdPartyPurchase = 0D;
				List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndSource(contractId, PurchaseItem.SOURCE_EXTERNAL);
				if(purchaseItems != null && purchaseItems.size() > 0){
					for(PurchaseItem purchaseItem : purchaseItems){
						sbThirdPartyPurchase += purchaseItem.getQuantity()*purchaseItem.getPrice();
					}
				}else{
					log.error("no Purchase item found belong to " + contractInfo.getSerialNum());
				}
				//内部采购总额
				Double sbInternalPurchase = 0D;
				sbInternalPurchase += StringUtil.nullToDouble(projectSupportCostRepository.findSumGrossProfitByContractIdAndStatWeek(contractId, statWeek));
				sbInternalPurchase += StringUtil.nullToDouble(productSalesBonusRepository.findSumBonusBasisByContractIdAndStatWeek(contractId, statWeek));
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
					salesBonus.setReceiveTotal(sbReceiveTotal);
					salesBonus.setTaxes(sbTaxes);
					salesBonus.setShareCost(sbShareCost);
					salesBonus.setThirdPartyPurchase(sbThirdPartyPurchase);
					//奖金基数
					Double sbBonusBasis = sbReceiveTotal - sbTaxes - sbShareCost - sbThirdPartyPurchase;
					salesBonus.setBonusBasis(sbBonusBasis);
					//奖金比例
					Double sbBonusRate = 0D;
					BonusRate br = bonusRateRepository.findByDeptIdAndContractType(contractInfo.getDeptId(),contractType);
					if(br != null){
						sbBonusRate = br.getRate();
					}
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
				
				log.info("====begin generate Consultants Bonus belong to Contract : "+contractInfo.getSerialNum()+"=======");
				if(contractInfo.getConsultantsId() != null && contractInfo.getConsultants() != null){
					ConsultantsBonus consultantsBonus = new ConsultantsBonus();
					consultantsBonus.setStatWeek(statWeek);
					consultantsBonus.setContractId(contractId);
					consultantsBonus.setContractAmount(contractInfo.getAmount());
					consultantsBonus.setConsultantsId(contractInfo.getConsultantsId());
					consultantsBonus.setConsultants(contractInfo.getConsultants());
					//奖金基数
					Double cbBonusBasis =  sbReceiveTotal - sbTaxes - sbShareCost - sbThirdPartyPurchase;
					consultantsBonus.setBonusBasis(cbBonusBasis);
					//奖金比例
					Double cbBonusRate = 0D;
					BonusRate br = bonusRateRepository.findByDeptIdAndContractType(contractInfo.getConsultantsDeptId(),contractType);
					if(br != null){
						cbBonusRate = br.getRate();
					}
					consultantsBonus.setBonusRate(cbBonusRate);
					//项目分润比例
					Double cbConsultantsShareRate = contractInfo.getConsultantsShareRate();
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
				Double poContractFinishRate = contractInfo.getFinishRate();
				projectOverall.setContractFinishRate(poContractFinishRate);
				//收入确认
				Double poAcceptanceIncome = poIdentifiableIncome*(poContractFinishRate/100);
				projectOverall.setAcceptanceIncome(poAcceptanceIncome);
				//收款金额
				Double poReceiveTotal = sbReceiveTotal;
				projectOverall.setReceiveTotal(poReceiveTotal);
				//应收账款
				Double poReceivableAccount = poContractAmount*(poContractFinishRate/100) - poReceiveTotal;
				projectOverall.setReceivableAccount(poReceivableAccount);
				//公摊成本
				Double poShareCost = contractInfo.getShareCost();
				projectOverall.setShareCost(poShareCost);
				//第三方采购
				Double poThirdPartyPurchase = sbThirdPartyPurchase;
				projectOverall.setThirdPartyPurchase(poThirdPartyPurchase);
				//内部采购总额
				Double poInternalPurchase = sbInternalPurchase;
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
				
				log.info("====begin generate Contract Internal Purchase && Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"=====");
				ProjectOverall projectOverall2 = projectOverallRepository.findByContractIdAndStatWeek(contractId, statWeek);
				Bonus bonus2 = bonusRepository.findByContractIdAndStatWeek(contractId, statWeek);
				List<DeptType> deptTypes2 = deptTypeRepository.findAll();
				for(DeptType deptType : deptTypes2){
					ContractInternalPurchase contractInternalPurchase = new ContractInternalPurchase();
					contractInternalPurchase.setStatWeek(statWeek);
					//项目总体控制表主键
					contractInternalPurchase.setProjectOverallId(projectOverall2.getId());
					contractInternalPurchase.setContractId(contractId);
					//部门类型主键
					contractInternalPurchase.setDeptType(deptType.getId());
					//总金额
					Double cipTotalAmount = 0D;
					cipTotalAmount += StringUtil.nullToDouble(projectSupportCostRepository.findSumGrossProfitByContractIdAndDeptTypeAndStatWeek(contractId, deptType.getId(), statWeek));
					cipTotalAmount += StringUtil.nullToDouble(productSalesBonusRepository.findSumBonusBasisByContractIdAndDeptTypeAndStatWeek(contractId, deptType.getId(), statWeek));
					contractInternalPurchase.setTotalAmount(cipTotalAmount);
					contractInternalPurchase.setCreator(creator);
					contractInternalPurchase.setCreateTime(ZonedDateTime.now());
					contractInternalPurchaseRepository.save(contractInternalPurchase);
					
					ContractProjectBonus contractProjectBonus = new ContractProjectBonus();
					contractProjectBonus.setStatWeek(statWeek);
					contractProjectBonus.setBonusId(bonus2.getId());
					contractProjectBonus.setContractId(contractId);
					contractProjectBonus.setDeptType(deptType.getId());
					//奖金合计
					Double cpbBonus = 0D;
					cpbBonus += StringUtil.nullToDouble(projectSupportBonusRepository.findSumCurrentBonusByContractIdAndDeptTypeAndStatWeek(contractId, deptType.getId(), statWeek));
					cpbBonus += StringUtil.nullToDouble(productSalesBonusRepository.findSumCurrentBonusByContractIdAndDeptTypeAndStatWeek(contractId, deptType.getId(), statWeek));
					contractProjectBonus.setBonus(cpbBonus);
					contractProjectBonus.setCreator(creator);
					contractProjectBonus.setCreateTime(ZonedDateTime.now());
					contractProjectBonusRepository.save(contractProjectBonus);
				}
				log.info("====end generate Contract Internal Purchase && Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"=====");
				log.info("=====end generate Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"======");
			}
		}else{
			log.info("No ContractInfo Founded");
		}
		log.info("=====end Account Scheduled=====");
	}
}
