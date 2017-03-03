package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractMonthlyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ContractMonthlyStatVo;
import com.wondertek.cpm.repository.ContractMonthlyStatDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractMonthlyStat.
 */
@Service
@Transactional
public class ContractMonthlyStatService {
	
	private final Logger log = LoggerFactory.getLogger(ContractMonthlyStatService.class);
	
	@Inject
    private UserRepository userRepository;
	
	@Inject
	private ContractMonthlyStatDao contractMonthlyStatDao;
	
	/**
     *  Get one contractMonthlyStats by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractMonthlyStatVo findOne(Long id) {
        log.debug("Request to get ContractMonthlyStat : {}", id);
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
	        ContractMonthlyStatVo contractMonthlyStat = contractMonthlyStatDao.getById(id, user, deptInfo);
	        return contractMonthlyStat;
		}
		return null;
    }
    
    /**
     * Search for the contractMonthlyStats corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractMonthlyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractMonthlyStat for query {}", query);
        Page<ContractMonthlyStat> result = null;
        return result;
    }
    
    /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ContractMonthlyStatVo> getStatPage(String contractId, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ContractMonthlyStatVo> page = contractMonthlyStatDao.getUserPage(contractId, pageable, user, deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ContractMonthlyStatVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartData(Date fromMonth, Date toMonth, Long contractId){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ChartReportDataVo> datas = new ArrayList<>();
        	ChartReportDataVo data1 = new ChartReportDataVo();//receive_total
        	ChartReportDataVo data2 = new ChartReportDataVo();//cost_total
        	ChartReportDataVo data3 = new ChartReportDataVo();//gross_profit
        	ChartReportDataVo data4 = new ChartReportDataVo();//sales_human_cost
        	ChartReportDataVo data5 = new ChartReportDataVo();//sales_payment
        	ChartReportDataVo data6 = new ChartReportDataVo();//consult_human_cost
        	ChartReportDataVo data7 = new ChartReportDataVo();//consult_payment
        	ChartReportDataVo data8 = new ChartReportDataVo();//hardware_purchase
        	ChartReportDataVo data9 = new ChartReportDataVo();//external_software
        	ChartReportDataVo data10 = new ChartReportDataVo();//internal_software
        	ChartReportDataVo data11 = new ChartReportDataVo();//project_human_cost
        	ChartReportDataVo data12 = new ChartReportDataVo();//project_payment
        	String[] names = new String[]{"合同回款总额","所有成本","合同毛利","销售人工成本","销售报销成本","咨询人工成本","咨询报销成本","硬件采购成本","外部软件采购成本","内部软件采购成本","项目人工成本","项目报销成本"};
        	List<Double> dataD1 = new ArrayList<>();
        	List<Double> dataD2 = new ArrayList<>();
        	List<Double> dataD3 = new ArrayList<>();
        	List<Double> dataD4 = new ArrayList<>();
        	List<Double> dataD5 = new ArrayList<>();
        	List<Double> dataD6 = new ArrayList<>();
        	List<Double> dataD7 = new ArrayList<>();
        	List<Double> dataD8 = new ArrayList<>();
        	List<Double> dataD9 = new ArrayList<>();
        	List<Double> dataD10 = new ArrayList<>();
        	List<Double> dataD11 = new ArrayList<>();
        	List<Double> dataD12 = new ArrayList<>();
        	Calendar cal1 = Calendar.getInstance();
        	Calendar cal2 = Calendar.getInstance();
        	cal1.setTime(fromMonth);
        	cal2.setTime(toMonth);
        	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
        	int count = 0;
        	count += 12*yearCount;
        	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
        	for(int i = 0 ; i <= count; i++){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
        		ContractMonthlyStatVo contractMonthlyStatVo = contractMonthlyStatDao.getByStatWeekAndContractId(statWeek, contractId, user, deptInfo);
        		Double receiveTotal = 0D;
        		Double costTotal = 0D;
        		Double grossProfit = 0D;
        		Double salesHumanCost = 0D;
        		Double salesPayment = 0D;
        		Double consultHumanCost = 0D;
        		Double consultPayment = 0D;
        		Double hardwarePurchase = 0D;
        		Double externalSoftware = 0D;
        		Double internalSoftware = 0D;
        		Double projectHumanCost = 0D;
        		Double projectPayment = 0D;
        		if(contractMonthlyStatVo != null){
        			receiveTotal = contractMonthlyStatVo.getReceiveTotal();
        			costTotal = contractMonthlyStatVo.getCostTotal();
        			grossProfit = contractMonthlyStatVo.getGrossProfit();
        			salesHumanCost = contractMonthlyStatVo.getSalesHumanCost();
        			salesPayment = contractMonthlyStatVo.getSalesPayment();
        			consultHumanCost = contractMonthlyStatVo.getConsultHumanCost();
        			consultPayment = contractMonthlyStatVo.getConsultPayment();
        			hardwarePurchase = contractMonthlyStatVo.getHardwarePurchase();
        			externalSoftware = contractMonthlyStatVo.getExternalSoftware();
        			internalSoftware = contractMonthlyStatVo.getInternalSoftware();
        			projectHumanCost = contractMonthlyStatVo.getProjectHumanCost();
        			projectPayment = contractMonthlyStatVo.getProjectPayment();
        		}
        		dataD1.add(receiveTotal);
        		dataD2.add(costTotal);
        		dataD3.add(grossProfit);
        		dataD4.add(salesHumanCost);
        		dataD5.add(salesPayment);
        		dataD6.add(consultHumanCost);
        		dataD7.add(consultPayment);
        		dataD8.add(hardwarePurchase);
        		dataD9.add(externalSoftware);
        		dataD10.add(internalSoftware);
        		dataD11.add(projectHumanCost);
        		dataD12.add(projectPayment);
        		cal1.add(Calendar.MONTH, 1);
        	}
        	data1.setData(dataD1);
        	data2.setData(dataD2);
        	data3.setData(dataD3);
        	data4.setData(dataD4);
        	data5.setData(dataD5);
        	data6.setData(dataD6);
        	data7.setData(dataD7);
        	data8.setData(dataD8);
        	data9.setData(dataD9);
        	data10.setData(dataD10);
        	data11.setData(dataD11);
        	data12.setData(dataD12);
        	datas.add(data1);
        	datas.add(data2);
        	datas.add(data3);
        	datas.add(data4);
        	datas.add(data5);
        	datas.add(data6);
        	datas.add(data7);
        	datas.add(data8);
        	datas.add(data9);
        	datas.add(data10);
        	datas.add(data11);
        	datas.add(data12);
        	for(int i = 0; i < 12; i++){
        		datas.get(i).setName(names[i]);
        		datas.get(i).setType("line");
        	}
        	return datas;
    	}
    	return null;
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getFinishRateData(Date fromMonth, Date toMonth, Long contractId){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ChartReportDataVo> datas = new ArrayList<>();
        	ChartReportDataVo data1 = new ChartReportDataVo();//finish_rate
        	data1.setName("完成率");
        	data1.setType("line");
        	List<Double> dataD1 = new ArrayList<>();
        	Calendar cal1 = Calendar.getInstance();
        	Calendar cal2 = Calendar.getInstance();
        	cal1.setTime(fromMonth);
        	cal2.setTime(toMonth);
        	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
        	int count = 0;
        	count += 12*yearCount;
        	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
        	for(int i = 0 ; i <= count; i++){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
        		ContractMonthlyStatVo contractMonthlyStatVo = contractMonthlyStatDao.getByStatWeekAndContractId(statWeek, contractId, user, deptInfo);
        		Double finishRate = 0D;
        		if(contractMonthlyStatVo != null){
        			finishRate = contractMonthlyStatVo.getFinishRate();
        		}
        		dataD1.add(finishRate);
        		cal1.add(Calendar.MONTH, 1);
        	}
        	data1.setData(dataD1);
        	datas.add(data1);
        	return datas;
    	}
    	return null;
    }
    
}
