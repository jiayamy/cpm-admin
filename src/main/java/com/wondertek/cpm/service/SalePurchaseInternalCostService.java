package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.vo.ProjectSupportCostVo;
import com.wondertek.cpm.repository.ProjectSupportCostDao;

@Service
@Transactional
public class SalePurchaseInternalCostService {

	private final Logger log = LoggerFactory.getLogger(SalePurchaseInternalCostService.class);
	
	@Inject
	private ProjectSupportCostDao projectSupportCostDao;
	
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalPage(Long contractId,Long userId,Long statWeek,Long deptType,Pageable pageable){
		Page<ProjectSupportCostVo> page = projectSupportCostDao.getAllSalePurchaseInternalPage(contractId, userId, statWeek, deptType, pageable);
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同合计
		log.info("----------------------service-page:"+page.getTotalElements());
		//填充数据
		if(page != null && page.getContent() != null){
//			ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
			for(ProjectSupportCostVo vo : page.getContent()){
				vo.setSettlementCost(StringUtil.getScaleDouble(vo.getSettlementCost(), 2));
				vo.setProjectHourCost(StringUtil.getScaleDouble(vo.getProjectHourCost(), 2));
				vo.setInternalBudgetCost(StringUtil.getScaleDouble(vo.getInternalBudgetCost(), 2));
				vo.setSal(StringUtil.getScaleDouble(vo.getSal(), 2));
				vo.setSocialSecurityFund(StringUtil.getScaleDouble(vo.getSocialSecurityFund(), 2));
				vo.setOtherExpense(StringUtil.getScaleDouble(vo.getOtherExpense(), 2));
				vo.setUserMonthCost(StringUtil.getScaleDouble(vo.getUserMonthCost(), 2));
				vo.setUserHourCost(StringUtil.getScaleDouble(vo.getUserHourCost(), 2));
				vo.setProductCost(StringUtil.getScaleDouble(vo.getProductCost(), 2));
				vo.setGrossProfit(StringUtil.getScaleDouble(vo.getGrossProfit(), 2));
				
				returnList.add(vo);
				//分合同合计
				String key = vo.getContractSerialNum();
				if (!contractSerialNumMap.containsKey(key)) {
					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
					contractSerialNumMap.put(key, totalInfo);
					contractSerialNumMap.get(key).setSerialNum(vo.getContractSerialNum());
				}
				//填充合计
				contractSerialNumMap.get(key).setInternalBudgetCost(contractSerialNumMap.get(key).getInternalBudgetCost()+vo.getInternalBudgetCost());
				contractSerialNumMap.get(key).setProductCost(contractSerialNumMap.get(key).getProductCost()+vo.getProductCost());
				contractSerialNumMap.get(key).setGrossProfit(contractSerialNumMap.get(key).getGrossProfit()+vo.getGrossProfit());
//				totalInfo.setSettlementCost(totalInfo.getSettlementCost()+vo.getSettlementCost());
//				totalInfo.setProjectHourCost(totalInfo.getProjectHourCost()+vo.getProjectHourCost());
//				totalInfo.setSal(totalInfo.getSal()+vo.getSal());
//				totalInfo.setSocialSecurityFund(totalInfo.getSocialSecurityFund()+vo.getSocialSecurityFund());
//				totalInfo.setOtherExpense(totalInfo.getOtherExpense()+vo.getOtherExpense());
//				totalInfo.setUserMonthCost(totalInfo.getUserMonthCost()+vo.getUserMonthCost());
//				totalInfo.setUserHourCost(totalInfo.getUserHourCost()+vo.getUserHourCost());
			}
			//处理contractSerialNumMap中totalInfo中的double值
			List<ProjectSupportCostVo> resultHandle = handleDoubleScale(contractSerialNumMap);
			//添加totalInfo到returnList
			returnList.addAll(resultHandle);
			log.info("------------------service0:"+returnList.size());
			for(ProjectSupportCostVo pcv : returnList){
				log.info("-------------service3:"+pcv);
			}
			return new PageImpl<>(returnList, pageable, page.getTotalElements());
		}
		return new PageImpl<>(new ArrayList<ProjectSupportCostVo>(), pageable, 0);
	}
	
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalDetailPage(Long contractId,Long statWeek,Pageable pageable){
		Page<ProjectSupportCostVo> page = projectSupportCostDao.getAllSalePurchaseInternalDetailPage(contractId,statWeek,pageable);
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同合计
		log.info("----------------------service-page:"+page.getTotalElements());
		//填充数据
		if(page != null && page.getContent() != null){
			for(ProjectSupportCostVo vo : page.getContent()){
				vo.setSettlementCost(StringUtil.getScaleDouble(vo.getSettlementCost(), 2));
				vo.setProjectHourCost(StringUtil.getScaleDouble(vo.getProjectHourCost(), 2));
				vo.setInternalBudgetCost(StringUtil.getScaleDouble(vo.getInternalBudgetCost(), 2));
				vo.setSal(StringUtil.getScaleDouble(vo.getSal(), 2));
				vo.setSocialSecurityFund(StringUtil.getScaleDouble(vo.getSocialSecurityFund(), 2));
				vo.setOtherExpense(StringUtil.getScaleDouble(vo.getOtherExpense(), 2));
				vo.setUserMonthCost(StringUtil.getScaleDouble(vo.getUserMonthCost(), 2));
				vo.setUserHourCost(StringUtil.getScaleDouble(vo.getUserHourCost(), 2));
				vo.setProductCost(StringUtil.getScaleDouble(vo.getProductCost(), 2));
				vo.setGrossProfit(StringUtil.getScaleDouble(vo.getGrossProfit(), 2));
				
				returnList.add(vo);
				//分合同合计
				String key = vo.getContractSerialNum();
				if (!contractSerialNumMap.containsKey(key)) {
					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
					contractSerialNumMap.put(key, totalInfo);
					contractSerialNumMap.get(key).setSerialNum(vo.getContractSerialNum());
				}
				//填充合计
				contractSerialNumMap.get(key).setInternalBudgetCost(contractSerialNumMap.get(key).getInternalBudgetCost()+vo.getInternalBudgetCost());
				contractSerialNumMap.get(key).setProductCost(contractSerialNumMap.get(key).getProductCost()+vo.getProductCost());
				contractSerialNumMap.get(key).setGrossProfit(contractSerialNumMap.get(key).getGrossProfit()+vo.getGrossProfit());
			}
			//处理contractSerialNumMap中totalInfo中的double值
			List<ProjectSupportCostVo> resultHandle = handleDoubleScale(contractSerialNumMap);
			//添加totalInfo到returnList
			returnList.addAll(resultHandle);
			log.info("------------------service0:"+returnList.size());
			for(ProjectSupportCostVo pcv : returnList){
				log.info("-------------service3:"+pcv);
			}
			return new PageImpl<>(returnList, pageable, page.getTotalElements());
		}
		return new PageImpl<>(new ArrayList<ProjectSupportCostVo>(), pageable, 0);
	}
	/**
	 * 获取下载excel数据
	 * @param contractId
	 * @param userName
	 * @param statWeek
	 * @param deptType
	 * @return
	 */
	public List<ProjectSupportCostVo> getAllSalePurchaseInternalList(Long contractId,Long userId,Long statWeek,Long deptType){
		List<ProjectSupportCostVo> pageList = projectSupportCostDao.getAllSalePurchaseInternalList(contractId, userId, statWeek, deptType);
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同合计
		//填充数据
		if(pageList != null){
			for(ProjectSupportCostVo vo : pageList){
				vo.setSettlementCost(StringUtil.getScaleDouble(vo.getSettlementCost(), 2));
				vo.setProjectHourCost(StringUtil.getScaleDouble(vo.getProjectHourCost(), 2));
				vo.setInternalBudgetCost(StringUtil.getScaleDouble(vo.getInternalBudgetCost(), 2));
				vo.setSal(StringUtil.getScaleDouble(vo.getSal(), 2));
				vo.setSocialSecurityFund(StringUtil.getScaleDouble(vo.getSocialSecurityFund(), 2));
				vo.setOtherExpense(StringUtil.getScaleDouble(vo.getOtherExpense(), 2));
				vo.setUserMonthCost(StringUtil.getScaleDouble(vo.getUserMonthCost(), 2));
				vo.setUserHourCost(StringUtil.getScaleDouble(vo.getUserHourCost(), 2));
				vo.setProductCost(StringUtil.getScaleDouble(vo.getProductCost(), 2));
				vo.setGrossProfit(StringUtil.getScaleDouble(vo.getGrossProfit(), 2));
				
				returnList.add(vo);
				//分合同合计
				String key = vo.getContractSerialNum();
				if (!contractSerialNumMap.containsKey(key)) {
					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
					contractSerialNumMap.put(key, totalInfo);
					contractSerialNumMap.get(key).setSerialNum(vo.getContractSerialNum());
				}
				//填充合计
				contractSerialNumMap.get(key).setInternalBudgetCost(contractSerialNumMap.get(key).getInternalBudgetCost()+vo.getInternalBudgetCost());
				contractSerialNumMap.get(key).setProductCost(contractSerialNumMap.get(key).getProductCost()+vo.getProductCost());
				contractSerialNumMap.get(key).setGrossProfit(contractSerialNumMap.get(key).getGrossProfit()+vo.getGrossProfit());
			}
			//处理contractSerialNumMap中totalInfo中的double值
			List<ProjectSupportCostVo> resultHandle = handleDoubleScale(contractSerialNumMap);
			//添加totalInfo到returnList
			returnList.addAll(resultHandle);
			log.info("------------------0:"+returnList.size());
			for(ProjectSupportCostVo pcv : returnList){
				log.info("-------------3:"+pcv);
			}
			return returnList;
		}
		return returnList;
	}
	/**
	 * 初始化合计信息
	 * @return
	 */
	private ProjectSupportCostVo getInitProjecSupportCostTotalInfo(){
		ProjectSupportCostVo projectSupportCostVo = new ProjectSupportCostVo();
//		projectSupportCostVo.setContractSerialNum("分合同合计");
//		projectSupportCostVo.setSettlementCost(0d);		//结算成本
//		projectSupportCostVo.setProjectHourCost(0d);	//项目工时
		projectSupportCostVo.setInternalBudgetCost(0d);	//内部采购成本
//		projectSupportCostVo.setSal(0d);				//工资
//		projectSupportCostVo.setSocialSecurityFund(0d);	//社保公积金
//		projectSupportCostVo.setOtherExpense(0d);		//其他费用
//		projectSupportCostVo.setUserMonthCost(0d);		//单人月成本小计
//		projectSupportCostVo.setUserHourCost(0d);		//单人标准工时成本
		projectSupportCostVo.setProductCost(0d);		//生产成本合计
		projectSupportCostVo.setGrossProfit(0d);		//生产毛利
		return projectSupportCostVo;
	}
	/**
	 * 处理合计里面的double值，只精确到小数点后2位
	 * @param totalInfo
	 */
	private List<ProjectSupportCostVo> handleDoubleScale(Map<String,ProjectSupportCostVo> contractSerialNumMap){
		int i = 0;
		int j = -1;
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		ProjectSupportCostVo tmp = new ProjectSupportCostVo();
		if (!contractSerialNumMap.isEmpty()) {
			for (String key : contractSerialNumMap.keySet()) {
				tmp = contractSerialNumMap.get(key);
				if (i == 0) {
					tmp.setContractSerialNum("分合同合计");
				}
				tmp.setId(StringUtil.nullToLong(--j));
				tmp.setInternalBudgetCost(StringUtil.getScaleDouble(tmp.getInternalBudgetCost(), 2));
				tmp.setProductCost(StringUtil.getScaleDouble(tmp.getProductCost(), 2));
				tmp.setGrossProfit(StringUtil.getScaleDouble(tmp.getGrossProfit(), 2));
				returnList.add(tmp);
				log.debug("---------------1:" + tmp);
				i++;
			} 
		}else{
			tmp = getInitProjecSupportCostTotalInfo();
			tmp.setContractSerialNum("分合同合计");
			returnList.add(tmp);
		}
		return returnList;
//		totalInfo.setSettlementCost(StringUtil.getScaleDouble(totalInfo.getSettlementCost(), 2));
//		totalInfo.setProjectHourCost(StringUtil.getScaleDouble(totalInfo.getProjectHourCost(), 2));
//		totalInfo.setInternalBudgetCost(StringUtil.getScaleDouble(totalInfo.getInternalBudgetCost(), 2));
//		totalInfo.setSal(StringUtil.getScaleDouble(totalInfo.getSal(), 2));
//		totalInfo.setSocialSecurityFund(StringUtil.getScaleDouble(totalInfo.getSocialSecurityFund(), 2));
//		totalInfo.setOtherExpense(StringUtil.getScaleDouble(totalInfo.getOtherExpense(), 2));
//		totalInfo.setUserMonthCost(StringUtil.getScaleDouble(totalInfo.getUserMonthCost(), 2));
//		totalInfo.setUserHourCost(StringUtil.getScaleDouble(totalInfo.getUserHourCost(), 2));
//		totalInfo.setProductCost(StringUtil.getScaleDouble(totalInfo.getProductCost(), 2));
//		totalInfo.setGrossProfit(StringUtil.getScaleDouble(totalInfo.getGrossProfit(), 2));
	}
}
