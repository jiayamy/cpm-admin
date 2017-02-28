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
	
	public List<ProjectSupportCostVo> getAllSalePurchaseInternalPage(Long contractId,Long userId,Long statWeek,Long deptType){
		List<ProjectSupportCostVo> page = projectSupportCostDao.getAllSalePurchaseInternalPage(contractId, userId, statWeek, deptType);
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同部门合计
		//填充数据
		if(page != null){
			for(ProjectSupportCostVo vo : page){
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
				//分合同部门合计
				String key = vo.getContractId()+"_"+vo.getDeptType();
				if (!contractSerialNumMap.containsKey(key)) {
					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
					contractSerialNumMap.put(key, totalInfo);
					contractSerialNumMap.get(key).setContractSerialNum(vo.getContractSerialNum());
					contractSerialNumMap.get(key).setContractSerialNum(vo.getContractSerialNum());//填充合同编号
					contractSerialNumMap.get(key).setDeptName(vo.getDeptName());//填充部门类型
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
			return returnList;
		}
		return new ArrayList<ProjectSupportCostVo>();
	}
	
	public Page<ProjectSupportCostVo> getAllSalePurchaseInternalDetailPage(Long userId,Long statWeek,Pageable pageable){
		Page<ProjectSupportCostVo> page = projectSupportCostDao.getAllSalePurchaseInternalDetailPage(userId,statWeek,pageable);
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
//		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同合计
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
//				//分合同合计
//				String key = vo.getContractSerialNum();
//				if (!contractSerialNumMap.containsKey(key)) {
//					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
//					contractSerialNumMap.put(key, totalInfo);
//					contractSerialNumMap.get(key).setSerialNum(vo.getContractSerialNum());
//				}
//				//填充合计
//				contractSerialNumMap.get(key).setInternalBudgetCost(contractSerialNumMap.get(key).getInternalBudgetCost()+vo.getInternalBudgetCost());
//				contractSerialNumMap.get(key).setProductCost(contractSerialNumMap.get(key).getProductCost()+vo.getProductCost());
//				contractSerialNumMap.get(key).setGrossProfit(contractSerialNumMap.get(key).getGrossProfit()+vo.getGrossProfit());
			}
//			//处理contractSerialNumMap中totalInfo中的double值
//			List<ProjectSupportCostVo> resultHandle = handleDoubleScale(contractSerialNumMap);
//			//添加totalInfo到returnList
//			returnList.addAll(resultHandle);
			log.info("----------------------service-page2:"+page.getTotalElements());
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
		Map<String,ProjectSupportCostVo> contractSerialNumMap = new HashMap<String,ProjectSupportCostVo>();	//分合同部门合计
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
				String key = vo.getContractId()+"_"+vo.getDeptType();
				if (!contractSerialNumMap.containsKey(key)) {
					ProjectSupportCostVo totalInfo = getInitProjecSupportCostTotalInfo();
					contractSerialNumMap.put(key, totalInfo);
					contractSerialNumMap.get(key).setContractSerialNum(vo.getContractSerialNum());//填充合同编号
					contractSerialNumMap.get(key).setDeptName(vo.getDeptName());//填充部门类型
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
		projectSupportCostVo.setUserName("分部门合计");
		projectSupportCostVo.setInternalBudgetCost(0d);	//内部采购成本
		projectSupportCostVo.setProductCost(0d);		//生产成本合计
		projectSupportCostVo.setGrossProfit(0d);		//生产毛利
		return projectSupportCostVo;
	}
	/**
	 * 处理合计里面的double值，只精确到小数点后2位
	 * @param totalInfo
	 */
	private List<ProjectSupportCostVo> handleDoubleScale(Map<String,ProjectSupportCostVo> contractSerialNumMap){
		Long j = -1L;
		List<ProjectSupportCostVo> returnList = new ArrayList<ProjectSupportCostVo>();
		ProjectSupportCostVo tmp = new ProjectSupportCostVo();
		for (String key : contractSerialNumMap.keySet()) {
			tmp = contractSerialNumMap.get(key);
			tmp.setId(j--);	//设置界面循环显示数据所依据的id值
			tmp.setInternalBudgetCost(StringUtil.getScaleDouble(tmp.getInternalBudgetCost(), 2));
			tmp.setProductCost(StringUtil.getScaleDouble(tmp.getProductCost(), 2));
			tmp.setGrossProfit(StringUtil.getScaleDouble(tmp.getGrossProfit(), 2));
			returnList.add(tmp);
		} 
		return returnList;
	}
}
