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
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.SaleWeeklyStatVo;
import com.wondertek.cpm.repository.SaleWeeklyStatDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing SaleWeeklyStat.
 */
@Service
@Transactional
public class SaleWeeklyStatService {
	
	private final Logger log = LoggerFactory.getLogger(SaleWeeklyStatService.class);
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SaleWeeklyStatDao saleWeeklyStatDao;
	
	/**
     *  Get one saleWeeklyStat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public SaleWeeklyStatVo findOne(Long id) {
        log.debug("Request to get SaleWeeklyStat : {}", id);
        List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
	        SaleWeeklyStatVo saleWeeklyStat = saleWeeklyStatDao.getById(id , user, deptInfo);
	        return saleWeeklyStat;
		}
		return null;
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
    public Page<SaleWeeklyStatVo> getStatPage(String deptId, Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<SaleWeeklyStatVo> page = saleWeeklyStatDao.getUserPage(deptId, pageable, user, deptInfo);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<SaleWeeklyStatVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public List<ChartReportDataVo> getChartDate(Date fromDate,Date toDate,Long deptId){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ChartReportDataVo> datas = new ArrayList<>();
        	ChartReportDataVo data1 = new ChartReportDataVo();//annual_index
        	ChartReportDataVo data2 = new ChartReportDataVo();//finish_total
        	ChartReportDataVo data3 = new ChartReportDataVo();//receive_total
        	ChartReportDataVo data4 = new ChartReportDataVo();//cost_total
        	ChartReportDataVo data5 = new ChartReportDataVo();//sales_human_cost
        	ChartReportDataVo data6 = new ChartReportDataVo();//sales_payment
        	ChartReportDataVo data7 = new ChartReportDataVo();//consult_payment
        	ChartReportDataVo data8 = new ChartReportDataVo();//consult_human_cost
        	ChartReportDataVo data9 = new ChartReportDataVo();//hardware_purchase
        	ChartReportDataVo data10 = new ChartReportDataVo();//external_software
        	ChartReportDataVo data11 = new ChartReportDataVo();//internal_software
        	ChartReportDataVo data12 = new ChartReportDataVo();//project_human_cost
        	ChartReportDataVo data13 = new ChartReportDataVo();//project_payment
        	String[] names = new String[]{"合同年指标","合同累计完成金额","当年收款金额","当年新增所有成本","当年销售人工成本","当年销售报销成本",
        			"当年咨询报销成本","当年咨询人工成本","当年硬件成本","当年外部软件成本","当年内部软件成本","当年项目人工成本","当年项目报销成本"};
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
        	List<Double> dataD13 = new ArrayList<>();
        	Long temp = fromDate.getTime();
        	Long sevenDay = 7*24*60*60*1000L;
        	while(temp <= toDate.getTime()){
        		Long statWeek = StringUtil.nullToLong(DateUtil.formatDate("yyyyMMdd", new Date(temp)));
        		SaleWeeklyStatVo saleWeeklyStatVo = saleWeeklyStatDao.getByStatWeekAndDeptId(statWeek, deptId, user, deptInfo);
        		Double annualIndex = 0D;
        		Double finishTotal = 0D;
        		Double receiveTotal = 0D;
        		Double costTotal = 0D;
        		Double salesHumanCost = 0D;
        		Double salesPayment = 0D;
        		Double consultHumanCost = 0D;
        		Double consultPayment = 0D;
        		Double hardwarePurchase = 0D;
        		Double externalSoftware = 0D;
        		Double internalSoftware = 0D;
        		Double projectHumanCost = 0D;
        		Double projectPayment = 0D;
        		if(saleWeeklyStatVo != null){
        			annualIndex = saleWeeklyStatVo.getAnnualIndex();
        			finishTotal = saleWeeklyStatVo.getFinishTotal();
        			receiveTotal = saleWeeklyStatVo.getReceiveTotal();
        			costTotal = saleWeeklyStatVo.getCostTotal();
        			salesHumanCost = saleWeeklyStatVo.getSalesHumanCost();
        			salesPayment = saleWeeklyStatVo.getSalesPayment();
        			consultHumanCost = saleWeeklyStatVo.getConsultHumanCost();
        			consultPayment = saleWeeklyStatVo.getConsultPayment();
        			hardwarePurchase = saleWeeklyStatVo.getHardwarePurchase();
        			externalSoftware = saleWeeklyStatVo.getExternalSoftware();
        			internalSoftware = saleWeeklyStatVo.getInternalSoftware();
        			projectHumanCost = saleWeeklyStatVo.getProjectHumanCost();
        			projectPayment = saleWeeklyStatVo.getProjectPayment();
        		}
        		dataD1.add(annualIndex);
        		dataD2.add(finishTotal);
        		dataD3.add(receiveTotal);
        		dataD4.add(costTotal);
        		dataD5.add(salesHumanCost);
        		dataD6.add(salesPayment);
        		dataD7.add(consultPayment);
        		dataD8.add(consultHumanCost);
        		dataD9.add(hardwarePurchase);
        		dataD10.add(externalSoftware);
        		dataD11.add(internalSoftware);
        		dataD12.add(projectHumanCost);
        		dataD13.add(projectPayment);
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
        	data13.setData(dataD13);
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
        	datas.add(data13);
        	for(int i = 0; i < 13; i++){
        		datas.get(i).setName(names[i]);
        		datas.get(i).setType("line");
        	}
        	return datas;
    	}
    	return null;
    }

}
