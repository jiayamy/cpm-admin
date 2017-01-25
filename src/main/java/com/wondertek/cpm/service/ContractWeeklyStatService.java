package com.wondertek.cpm.service;

import java.util.ArrayList;
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
import com.wondertek.cpm.domain.ContractWeeklyStat;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ContractWeeklyStatVo;
import com.wondertek.cpm.repository.ContractWeeklyStatDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractWeeklyStat.
 */
@Service
@Transactional
public class ContractWeeklyStatService {

    private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatService.class);
    
//    @Inject
//    private ContractWeeklyStatSearchRepository contractWeeklyStatSearchRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ContractWeeklyStatDao contractWeeklyStatDao;

    /**
     *  Get one contractWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractWeeklyStatVo findOne(Long id) {
        log.debug("Request to get ContractWeeklyStat : {}", id);
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
	        ContractWeeklyStatVo contractWeeklyStat = contractWeeklyStatDao.getById(id , user, deptInfo);
	        return contractWeeklyStat;
		}
		return null;
    }

    /**
     * Search for the contractWeeklyStat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractWeeklyStat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractWeeklyStats for query {}", query);
        Page<ContractWeeklyStat> result = null;
//        Page<ContractWeeklyStat> result = contractWeeklyStatSearchRepository.search(queryStringQuery(query), pageable);
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
    public Page<ContractWeeklyStatVo> getStatPage(String contractId, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ContractWeeklyStatVo> page = contractWeeklyStatDao.getUserPage(contractId, pageable, user, deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<ContractWeeklyStatVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartData(Date fromDate, Date toDate, Long contractId){
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
        	Long temp = fromDate.getTime();
        	Long sevenDay = 7*24*60*60*1000L;
        	while(temp <= toDate.getTime()){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
        		ContractWeeklyStatVo contractWeeklyStatVo = contractWeeklyStatDao.getByStatWeekAndContractId(statWeek, contractId, user, deptInfo);
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
        		if(contractWeeklyStatVo != null){
        			receiveTotal = contractWeeklyStatVo.getReceiveTotal();
        			costTotal = contractWeeklyStatVo.getCostTotal();
        			grossProfit = contractWeeklyStatVo.getGrossProfit();
        			salesHumanCost = contractWeeklyStatVo.getSalesHumanCost();
        			salesPayment = contractWeeklyStatVo.getSalesPayment();
        			consultHumanCost = contractWeeklyStatVo.getConsultHumanCost();
        			consultPayment = contractWeeklyStatVo.getConsultPayment();
        			hardwarePurchase = contractWeeklyStatVo.getHardwarePurchase();
        			externalSoftware = contractWeeklyStatVo.getExternalSoftware();
        			internalSoftware = contractWeeklyStatVo.getInternalSoftware();
        			projectHumanCost = contractWeeklyStatVo.getProjectHumanCost();
        			projectPayment = contractWeeklyStatVo.getProjectPayment();
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
        		temp += sevenDay;
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
    public List<ChartReportDataVo> getFinishRateData(Date fromDate, Date toDate, Long contractId){
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
        	Long temp = fromDate.getTime();
        	Long sevenDay = 7*24*60*60*1000L;
        	while(temp <= toDate.getTime()){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
        		ContractWeeklyStatVo contractWeeklyStatVo = contractWeeklyStatDao.getByStatWeekAndContractId(statWeek, contractId, user, deptInfo);
        		Double finishRate = 0D;
        		if(contractWeeklyStatVo != null){
        			finishRate = contractWeeklyStatVo.getFinishRate();
        		}
        		dataD1.add(finishRate);
        		temp += sevenDay;
        	}
        	data1.setData(dataD1);
        	datas.add(data1);
        	return datas;
    	}
    	return null;
    }
}
