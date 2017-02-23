package com.wondertek.cpm.service;

import java.util.ArrayList;
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
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantBonusVo;
import com.wondertek.cpm.repository.ConsultantBonusDao;
import com.wondertek.cpm.repository.ConsultantBonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ConsultantBonus.
 */
@Service
@Transactional
public class ConsultantBonusService {

	private final Logger log = LoggerFactory.getLogger(ContractWeeklyStatService.class);
	
	@Inject
	private ConsultantBonusRepository consultantBonusRepository;
	
	@Inject
	private ConsultantBonusDao consultantBonusDao;
	
	@Inject
	private UserRepository userRepository;
	
	 /**
     * 根据参数获取列表
     * @param fromDate
     * @param endDate
     * @param statDate
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
	public Page<ConsultantBonusVo> getConsultantBonusPage(String contractId,String consultantManId,String statWeek,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ConsultantBonusVo> page = consultantBonusDao.getUserPage(user,deptInfo,contractId,consultantManId,statWeek,pageable);
    		List<ConsultantBonusVo> returnList = new ArrayList<ConsultantBonusVo>();
    		//填充数据
    		if(page != null && page.getContent() != null){
    			ConsultantBonusVo totalInfo = getInitConsultantBonusTotalInfo();
    			//填充累计已计提奖金
    			for(ConsultantBonusVo vo : page.getContent()){
    				vo.setAmount(StringUtil.getScaleDouble(vo.getAmount(), 2));
    				vo.setBonusBasis(StringUtil.getScaleDouble(vo.getBonusBasis(), 2));
    				vo.setBonusRate(StringUtil.getScaleDouble(vo.getBonusRate(), 2));
    				vo.setConsultantsShareRate(StringUtil.getScaleDouble(vo.getConsultantsShareRate(), 2));
    				vo.setCurrentBonus(StringUtil.getScaleDouble(vo.getCurrentBonus(), 2));
    				vo.setAccumulationBonus(vo.getCurrentBonus());	//累计已计提奖金
    				
    				returnList.add(vo);
    				//填充totalInfo
    				totalInfo.setAmount(totalInfo.getAmount()+vo.getAmount());
    				totalInfo.setBonusBasis(totalInfo.getBonusBasis()+vo.getBonusBasis());
    				totalInfo.setCurrentBonus(totalInfo.getCurrentBonus()+vo.getCurrentBonus());
    				totalInfo.setAccumulationBonus(totalInfo.getAccumulationBonus()+vo.getAccumulationBonus());
    			}
    			//处理合计的double值
        		handleDoubleScale(totalInfo);
        		//添加合计
//        		page.getContent().add(totalInfo);
        		returnList.add(totalInfo);
    		}
//        	return page;
    		return new PageImpl(returnList,pageable,page.getTotalElements());
    	}else{
    		return new PageImpl(new ArrayList<ConsultantBonusVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public ConsultantsBonus findOne(Long id){
    	return consultantBonusRepository.findOne(id);
    }
    
    /**
     * 查询某合同的统计记录
     * @param contractId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ConsultantBonusVo> getConsultantBonusRecordPage(String contractId,Long statWeek,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
    		Page<ConsultantBonusVo> page = consultantBonusDao.getConsultantBonusRecordPage(contractId,statWeek,pageable);
    		List<ConsultantBonusVo> returnList = new ArrayList<ConsultantBonusVo>();
    		//填充数据
    		if(page != null && page.getContent() != null){
    			ConsultantBonusVo totalInfo = getInitConsultantBonusTotalInfo();
    			//填充累计已计提奖金
    			for(ConsultantBonusVo vo : page.getContent()){
    				vo.setAmount(StringUtil.getScaleDouble(vo.getAmount(), 2));
    				vo.setBonusBasis(StringUtil.getScaleDouble(vo.getBonusBasis(), 2));
    				vo.setBonusRate(StringUtil.getScaleDouble(vo.getBonusRate(), 2));
    				vo.setConsultantsShareRate(StringUtil.getScaleDouble(vo.getConsultantsShareRate(), 2));
    				vo.setCurrentBonus(StringUtil.getScaleDouble(vo.getCurrentBonus(), 2));
    				vo.setAccumulationBonus(vo.getCurrentBonus());	//累计已计提奖金

    				returnList.add(vo);
    				//填充totalInfo
    				totalInfo.setAmount(totalInfo.getAmount()+vo.getAmount());
    				totalInfo.setBonusBasis(totalInfo.getBonusBasis()+vo.getBonusBasis());
    				totalInfo.setCurrentBonus(totalInfo.getCurrentBonus()+vo.getCurrentBonus());
    				totalInfo.setAccumulationBonus(totalInfo.getAccumulationBonus()+vo.getAccumulationBonus());
    			}
    			//处理合计的double值
    			handleDoubleScale(totalInfo);
    			//添加合计
    			returnList.add(totalInfo);
    		}
    		return new PageImpl(returnList,pageable,page.getTotalElements());
    	}else{
    		return new PageImpl(new ArrayList<ConsultantBonusVo>(), pageable, 0);
    	}
    }
    
    /**
     * 查询符合条件的最新数据记录
     * @param statWeek 
     * @param consultantManId 
     * @param contractId 
     * @return
     */
    @Transactional(readOnly = true)
    public List<ConsultantBonusVo> getConsultantBonusData(Long contractId, Long consultantManId, Long statWeek){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ConsultantBonusVo> page = consultantBonusDao.getConsultantBonusData(user,deptInfo,contractId,consultantManId,statWeek);
    		List<ConsultantBonusVo> returnList = new ArrayList<ConsultantBonusVo>();
    		//填充数据
    		if(page != null){
    			ConsultantBonusVo totalInfo = getInitConsultantBonusTotalInfo();
    			//填充累计已计提奖金
    			for(ConsultantBonusVo vo : page){
    				vo.setAmount(StringUtil.getScaleDouble(vo.getAmount(), 2));
    				vo.setBonusBasis(StringUtil.getScaleDouble(vo.getBonusBasis(), 2));
    				vo.setBonusRate(StringUtil.getScaleDouble(vo.getBonusRate(), 2));
    				vo.setConsultantsShareRate(StringUtil.getScaleDouble(vo.getConsultantsShareRate(), 2));
    				vo.setCurrentBonus(StringUtil.getScaleDouble(vo.getCurrentBonus(), 2));
    				vo.setAccumulationBonus(vo.getCurrentBonus());	//累计已计提奖金
    				
    				returnList.add(vo);
    				//填充totalInfo
    				totalInfo.setAmount(totalInfo.getAmount()+vo.getAmount());
    				totalInfo.setBonusBasis(totalInfo.getBonusBasis()+vo.getBonusBasis());
    				totalInfo.setCurrentBonus(totalInfo.getCurrentBonus()+vo.getCurrentBonus());
    				totalInfo.setAccumulationBonus(totalInfo.getAccumulationBonus()+vo.getAccumulationBonus());
    			}
    			//处理合计的double值
        		handleDoubleScale(totalInfo);
        		//添加合计
        		returnList.add(totalInfo);
    		}
    		return returnList;
    	}else{
    		return new ArrayList<ConsultantBonusVo>();
    	}
    }
    
    /**
     * 查询符合条件的所有数据记录
     * @param contractId
     * @param statWeek
     * @return
     */
    @Transactional(readOnly = true)
    public List<ConsultantBonusVo> getConsultantBonusDetailList(Long contractId,Long statWeek){
    	List<ConsultantBonusVo> resultList =  consultantBonusDao.getConsultantBonusDetailList(contractId,statWeek);
    	return resultList;
    }
    
    /**
     * 初始化咨询奖金合计
     * @return
     */
    private ConsultantBonusVo getInitConsultantBonusTotalInfo(){
    	ConsultantBonusVo totalInfo = new ConsultantBonusVo();
    	totalInfo.setConsultantsName("合计");
    	totalInfo.setAmount(0d);		//合同金额
    	totalInfo.setBonusBasis(0d);	//奖金基数
    	totalInfo.setCurrentBonus(0d);	//本期奖金
    	totalInfo.setAccumulationBonus(0d);		//累计已计提奖金
    	return totalInfo;
    }
    /**
     * 处理合计里面的double值，只精确到小数点后2位
     * @param totaoInfo
     * @return
     */
    private void handleDoubleScale(ConsultantBonusVo totalInfo){
    	totalInfo.setAmount(StringUtil.getScaleDouble(totalInfo.getAmount(), 2));
    	totalInfo.setBonusBasis(StringUtil.getScaleDouble(totalInfo.getBonusBasis(), 2));
    	totalInfo.setCurrentBonus(StringUtil.getScaleDouble(totalInfo.getCurrentBonus(), 2));
    	totalInfo.setAccumulationBonus(StringUtil.getScaleDouble(totalInfo.getAccumulationBonus(), 2));
    }
}
