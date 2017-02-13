package com.wondertek.cpm.job;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.BonusRate;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractInternalPurchase;
import com.wondertek.cpm.domain.ContractProjectBonus;
import com.wondertek.cpm.domain.ContractReceive;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.DeptType;
import com.wondertek.cpm.domain.ProductSalesBonus;
import com.wondertek.cpm.domain.ProjectInfo;
import com.wondertek.cpm.domain.ProjectOverall;
import com.wondertek.cpm.domain.ProjectSupportBonus;
import com.wondertek.cpm.domain.ProjectSupportCost;
import com.wondertek.cpm.domain.ProjectUser;
import com.wondertek.cpm.domain.PurchaseItem;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.ShareInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserCost;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.repository.BonusRateRepository;
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
import com.wondertek.cpm.repository.ProjectUserRepository;
import com.wondertek.cpm.repository.PurchaseItemRepository;
import com.wondertek.cpm.repository.SalesBonusRespository;
import com.wondertek.cpm.repository.ShareInfoRepository;
import com.wondertek.cpm.repository.UserCostRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetRepository;

@Component
@EnableScheduling
public class AccountScheduledJob {
	
	private Logger log = LoggerFactory.getLogger(AccountScheduledJob.class);
	
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
	private ProjectUserRepository projectUserRepository;
	
	@Inject
	private ProjectOverallRepository projectOverallRepository;
	
	@Inject
	private PurchaseItemRepository purchaseItemRepository;
	
	@Inject
	private ProductSalesBonusRepository productSalesBonusRepository;
	
	@Inject
	private SalesBonusRespository salesBonusRespository;
	
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
		Long statWeek = StringUtil.nullToLong(dates[6]);
		List<ContractInfo> contractInfos = contractInfoRepository.findByStatusOrUpdateTime(ContractInfo.STATUS_VALIDABLE, beginTime, endTime);
		if(contractInfos != null && contractInfos.size() > 0){
			for(ContractInfo contractInfo : contractInfos){
				Long contractId = contractInfo.getId();
				List<ProjectInfo> projectInfos = projectInfoRepository.findByContractIdAndStatusOrUpdateTime(contractId, ProjectInfo.STATUS_ADD, beginTime, endTime);
				if(projectInfos != null && projectInfos.size() > 0){
					for(ProjectInfo projectInfo : projectInfos){
						log.info("====begin generate Project Support Cost "+projectInfo.getSerialNum()+"===");
						List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectInfo.getId());
						Long projectId = projectInfo.getId();
						DeptInfo projectDept = deptInfoRepository.findOne(projectInfo.getDeptId());
						if(projectUsers != null && projectUsers.size() > 0){
							for(ProjectUser projectUser : projectUsers){
								User user = userRepository.findOne(projectUser.getUserId());
								ProjectSupportCost projectSupportCost = new ProjectSupportCost();
								projectSupportCost.setStatWeek(statWeek);
								projectSupportCost.setContractId(contractId);
								projectSupportCost.setDeptType(projectDept.getType());
								projectSupportCost.setUserId(projectUser.getUserId());
								projectSupportCost.setSerialNum(user.getSerialNum());
								projectSupportCost.setUserName(projectUser.getUserName());
								projectSupportCost.setGrade(user.getGender());
								//结算成本
								Double settlementCost = StringUtil.nullToDouble(externalQuotationRepository.findByGrade(user.getGender()).getHourCost());
								projectSupportCost.setSettlementCost(settlementCost);
								//项目工时
								Double projectHourCost = 0D;
								List<UserTimesheet> userTimesheets = userTimesheetRepository.findByUserIdAndTypeAndObjId(user.getId(), UserTimesheet.TYPE_PROJECT, projectId);
								if(userTimesheets != null && userTimesheets.size() > 0){
									for(UserTimesheet userTimesheet : userTimesheets){
										projectHourCost += userTimesheet.getRealInput();
									}
								}else{
									log.info("No UserTimesheet founded belong to User: " + user.getLastName() + " Project : " + projectInfo.getSerialNum());
								}
								projectSupportCost.setProjectHourCost(projectHourCost);
								//内部采购成本
								Double internalBudgetCost = projectHourCost*settlementCost;
								projectSupportCost.setInternalBdgetCost(internalBudgetCost);
								//工资,社保公积金,其他费用
								Double sal = 0D;
								Double socialSecurityFund = 0D;
								Double otherExpense = 0D;
								Long costMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", DateUtil.lastSundayEnd()).toString());
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
								projectSupportCost.setCreator("admin");
								projectSupportCost.setCreateTime(ZonedDateTime.now());
								projectSupportCostRepository.save(projectSupportCost);
							}
						}else{
							log.info("No ProjectUser Founded Belong to : " + projectInfo.getSerialNum());
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
						Double planDays = deliveryTime*acceptanceRate;
						projectSupportBonus.setPlanDays(planDays);
						//实际使用天数
						int realDays = 0;
						if(projectInfo.getStatus() == ProjectInfo.STATUS_ADD){
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getEndDay().toInstant())) + 1;
						}else{
							realDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(projectInfo.getStartDay().toInstant()), Date.from(projectInfo.getUpdateTime().toInstant())) + 1;
						}
						projectSupportBonus.setRealDays(realDays);
						//奖金调节比率
						Double bonusAdjustRate = planDays/(realDays-1);
						projectSupportBonus.setBonusAdjustRate(bonusAdjustRate);
						//奖金比率
						Double bonusRate = 0D; 
						BonusRate br = bonusRateRepository.findByDeptType(deptType);
						if(br != null){
							bonusRate = br.getRate();
						}else{
							log.info("No BonusRate Founded belong to DeptType " + deptType);
						}
						projectSupportBonus.setBonusRate(bonusRate);
						//奖金确认比例
						Double bonusAcceptanceRate = bonusRate*(1+bonusAdjustRate)*acceptanceRate;
						projectSupportBonus.setBonusAcceptanceRate(bonusAcceptanceRate);
						//奖金基数
						Double bonusBasis = 0D;
						List<ProjectSupportCost> projectSupportCosts = projectSupportCostRepository.findByDeptTypeAndStatWeek(deptType,statWeek);
						if(projectSupportCosts != null && projectSupportCosts.size() > 0){
							for(ProjectSupportCost projectSupportCost : projectSupportCosts){
								bonusBasis += projectSupportCost.getGrossProfit();
							}
						}else{
							log.info("No ProjectSupportCost Founded belong to DeptType : " + deptType);
						}
						projectSupportBonus.setBonusBasis(bonusBasis);
						//当期奖金
						Double currentBonus = bonusAcceptanceRate*bonusBasis;
						projectSupportBonus.setCurrentBonus(currentBonus);
						projectSupportBonus.setCreator("admin");
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
						//合同确认交付时间
						Integer psbDeliveryTime = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getEndDay().toInstant())) + 1;
						productSalesBonus.setDeliveryTime(psbDeliveryTime);
						//验收节点
						Double psbAcceptanceRate = contractInfo.getFinishRate();
						productSalesBonus.setAcceptanceRate(psbAcceptanceRate);
						//计划天数
						Double psbPlanDays = psbDeliveryTime*psbAcceptanceRate;
						productSalesBonus.setPlanDays(psbPlanDays);
						//实际使用天数
						Integer psbRealDays = 0;
						if(contractInfo.getStatus() == ContractInfo.STATUS_VALIDABLE){
							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getEndDay().toInstant())) + 1;
						}else{
							psbRealDays = DateUtil.getIntervalDaysOfExitDate2(Date.from(contractInfo.getStartDay().toInstant()), Date.from(contractInfo.getUpdateTime().toInstant())) + 1;
						}
						productSalesBonus.setRealDays(psbRealDays);
						//奖金调节比率
						Double psbBonusAdjustRate = psbPlanDays/(psbRealDays-1);
						productSalesBonus.setBonusAdjustRate(psbBonusAdjustRate);
						//奖金比率
						Double psbBonusRate = StringUtil.nullToDouble(bonusRateRepository.findByDeptType(deptType.getId()));
						productSalesBonus.setBonusRate(psbBonusRate);
						//奖金确认比例
						Double psbBonusAcceptanceRate = psbBonusRate*(1+psbBonusAdjustRate)*psbAcceptanceRate;
						productSalesBonus.setBonusAcceptanceRate(psbBonusAcceptanceRate);
						//奖金基数
						Double psbBonusBasis = 0D;
						List<PurchaseItem> purchaseItems = purchaseItemRepository.findByContractIdAndSourceAndType(contractId, PurchaseItem.SOURCE_INTERNAL, PurchaseItem.TYPE_SOFTWARE);
						if(purchaseItems != null && purchaseItems.size() > 0){
							for(PurchaseItem purchaseItem : purchaseItems){
								ShareInfo shareInfo = shareInfoRepository.findByProductPriceIdAndDeptId(purchaseItem.getProductPriceId(), contractBudgetRepository.findOne(purchaseItem.getContractId()).getDeptId());
								psbBonusBasis += purchaseItem.getTotalAmount()*StringUtil.nullToDouble(shareInfo.getShareRate());
							}
						}else{
							log.info("No Purchase Item founded belong to Contract " + contractInfo.getSerialNum());
						}
						productSalesBonus.setBonusBasis(psbBonusBasis);
						//当期奖金
						Double psbCurrentBonus = psbBonusAcceptanceRate*psbBonusBasis;
						productSalesBonus.setCurrentBonus(psbCurrentBonus);
						productSalesBonus.setCreator("admin");
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
				Double sbTaxes = contractInfo.getTaxRate()*sbReceiveTotal;
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
				List<ProjectSupportCost> projectSupportCosts = projectSupportCostRepository.findByContractIdAndStatWeek(contractId,statWeek);
				if(projectSupportCosts != null && projectSupportCosts.size()>0){
					for(ProjectSupportCost projectSupportCost : projectSupportCosts){
						sbInternalPurchase += projectSupportCost.getGrossProfit();
					}
				}
				List<ProductSalesBonus> productSalesBonuses = productSalesBonusRepository.findByContractIdAndStatWeek(contractId,statWeek);
				if(productSalesBonuses != null && productSalesBonuses.size() > 0){
					for(ProductSalesBonus productSalesBonus : productSalesBonuses){
						sbInternalPurchase += productSalesBonus.getBonusBasis();
					}
				}
				if(contractInfo.getSalesmanId() != null && contractInfo.getSalesman() != null){
					SalesBonus salesBonus = new  SalesBonus();
					salesBonus.setStatWeek(statWeek);
					salesBonus.setSalesManId(contractInfo.getSalesmanId());
					salesBonus.setSalesMan(contractInfo.getSalesman());
					salesBonus.setContractId(contractId);
					salesBonus.setContractAmount(contractInfo.getAmount());
					salesBonus.setTaxRate(contractInfo.getTaxRate());
					salesBonus.setReceiveTotal(sbReceiveTotal);
					salesBonus.setTaxes(sbTaxes);
					salesBonus.setShareCost(sbShareCost);
					salesBonus.setThirdPartyPurchase(sbThirdPartyPurchase);
					salesBonus.setInternalPurchase(sbInternalPurchase);
					//奖金基数
					Double sbBonusBasis = sbReceiveTotal - sbTaxes - sbShareCost - sbThirdPartyPurchase - sbInternalPurchase;
					salesBonus.setBonusBasis(sbBonusBasis);
					//奖金比例
					Double sbBonusRate = StringUtil.nullToDouble(bonusRateRepository.findByDeptId(contractInfo.getDeptId()).getRate());
					salesBonus.setBonusRate(sbBonusRate);
					//本期奖金
					Double sbCurrentBonus = sbBonusBasis*sbBonusRate;
					salesBonus.setCurrentBonus(sbCurrentBonus);
					salesBonus.setCreator("admin");
					salesBonus.setCreateTime(ZonedDateTime.now());
					salesBonusRespository.save(salesBonus);
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
					Double cbBonusBasis =  sbReceiveTotal - sbTaxes - sbShareCost - sbThirdPartyPurchase - sbInternalPurchase;
					consultantsBonus.setBonusBasis(cbBonusBasis);
					//奖金比例
					Double cbBonusRate = StringUtil.nullToDouble(bonusRateRepository.findByDeptId(contractInfo.getConsultantsDeptId()).getRate());
					consultantsBonus.setBonusRate(cbBonusRate);
					//项目分润比例
					Double cbConsultantsShareRate = contractInfo.getConsultantsShareRate();
					consultantsBonus.setConsultantsShareRate(cbConsultantsShareRate);
					//本期奖金
					Double cbCurrentBonus = cbBonusBasis*cbBonusRate*cbConsultantsShareRate;
					consultantsBonus.setCurrentBonus(cbCurrentBonus);
					consultantsBonus.setCreator("admin");
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
				Double poIdentifiableIncome = poContractAmount*(1-poTaxRate);
				projectOverall.setIdentifiableIncome(poIdentifiableIncome);
				//合同完成节点
				Double poContractFinishRate = contractInfo.getFinishRate();
				projectOverall.setContractFinishRate(poContractFinishRate);
				//收入确认
				Double poAcceptanceIncome = poIdentifiableIncome*poContractFinishRate;
				projectOverall.setAcceptanceIncome(poAcceptanceIncome);
				//收款金额
				Double poReceiveTotal = contractInfo.getReceiveTotal();
				projectOverall.setReceiveTotal(poReceiveTotal);
				//应收账款
				Double poReceivableAccount = poContractAmount*poContractFinishRate - poReceiveTotal;
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
				//奖金
				Double poBonus = 0D;
				
				projectOverall.setBonus(poBonus);
				//毛利
				Double poGrossProfit = poIdentifiableIncome*poContractFinishRate - poShareCost - poThirdPartyPurchase - poInternalPurchase - poBonus;
				projectOverall.setGrossProfit(poGrossProfit);
				//毛利率
				Double poGrossProfitRate = poGrossProfit/(poIdentifiableIncome*poContractFinishRate);
				projectOverall.setGrossProfitRate(poGrossProfitRate);
				projectOverall.setCreator("admin");
				projectOverall.setCreateTime(ZonedDateTime.now());
				projectOverallRepository.save(projectOverall);
				log.info("====begin generate Project Overall to Contract : "+contractInfo.getSerialNum()+"=======");
				
				log.info("====begin generate Contract Internal Purchase to Contract : "+contractInfo.getSerialNum()+"=====");
				ContractInternalPurchase contractInternalPurchase = new ContractInternalPurchase();
				contractInternalPurchase.setStatWeek(statWeek);
				contractInternalPurchase.setProjectOverallId(0L);
				contractInternalPurchase.setContractId(contractId);
				
				contractInternalPurchaseRepository.save(contractInternalPurchase);
				log.info("====end generate Contract Internal Purchase to Contract : "+contractInfo.getSerialNum()+"=====");
				
				log.info("=====begin generate Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"======");
				ContractProjectBonus contractProjectBonus = new ContractProjectBonus();
				contractProjectBonus.setStatWeek(statWeek);
				
				contractProjectBonus.setCreator("admin");
				contractProjectBonus.setCreateTime(ZonedDateTime.now());
				contractProjectBonusRepository.save(contractProjectBonus);
				log.info("=====end generate Contract Project Bonus to Contract : "+contractInfo.getSerialNum()+"======");
			}
		}else{
			log.info("No ContractInfo Founded");
		}
		log.info("=====end Account Scheduled=====");
	}
}
