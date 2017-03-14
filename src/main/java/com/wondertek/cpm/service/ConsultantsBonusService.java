package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ConsultantsBonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ConsultantsBonusVo;
import com.wondertek.cpm.repository.ConsultantsBonusDao;
import com.wondertek.cpm.repository.ConsultantsBonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ConsultantsBonus.
 */
@Service
@Transactional
public class ConsultantsBonusService {

	@Inject
	private ConsultantsBonusRepository consultantsBonusRepository;
	
	@Inject
	private ConsultantsBonusDao consultantsBonusDao;
	
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
	public Page<ConsultantsBonusVo> getConsultantsBonusPage(ConsultantsBonus consultantsBonus,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		Page<ConsultantsBonusVo> page = consultantsBonusDao.getUserPage(user,deptInfo,consultantsBonus,pageable);
    		List<ConsultantsBonusVo> returnList = new ArrayList<ConsultantsBonusVo>();
    		//填充数据
    		if(page != null && page.getContent() != null){
    			ConsultantsBonusVo totalInfo = getInitConsultantsBonusTotalInfo();
    			//填充累计已计提奖金
    			for(ConsultantsBonusVo vo : page.getContent()){
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
    		return new PageImpl(new ArrayList<ConsultantsBonusVo>(), pageable, 0);
    	}
    }
    
    @Transactional(readOnly = true)
    public ConsultantsBonus findOne(Long id){
    	return consultantsBonusRepository.findOne(id);
    }
    
    /**
     * 查询某合同的统计记录
     * @param contractId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ConsultantsBonusVo> getConsultantsBonusRecordPage(ConsultantsBonus consultantsBonus,Pageable pageable){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
    		Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
    		Page<ConsultantsBonusVo> page = consultantsBonusDao.getConsultantsBonusRecordPage(user,deptInfo,consultantsBonus,pageable);
    		List<ConsultantsBonusVo> returnList = new ArrayList<ConsultantsBonusVo>();
    		//填充数据
    		if(page != null && page.getContent() != null){
    			//填充累计已计提奖金
    			for(ConsultantsBonusVo vo : page.getContent()){
    				vo.setAmount(StringUtil.getScaleDouble(vo.getAmount(), 2));
    				vo.setBonusBasis(StringUtil.getScaleDouble(vo.getBonusBasis(), 2));
    				vo.setBonusRate(StringUtil.getScaleDouble(vo.getBonusRate(), 2));
    				vo.setConsultantsShareRate(StringUtil.getScaleDouble(vo.getConsultantsShareRate(), 2));
    				vo.setCurrentBonus(StringUtil.getScaleDouble(vo.getCurrentBonus(), 2));
    				vo.setAccumulationBonus(vo.getCurrentBonus());	//累计已计提奖金

    				returnList.add(vo);
    			}
    		}
    		return new PageImpl(returnList,pageable,page.getTotalElements());
    	}else{
    		return new PageImpl(new ArrayList<ConsultantsBonusVo>(), pageable, 0);
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
    public List<ConsultantsBonusVo> getConsultantsBonusData(ConsultantsBonus consultantsBonus){
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
    		List<ConsultantsBonusVo> page = consultantsBonusDao.getConsultantsBonusData(user,deptInfo,consultantsBonus);
    		List<ConsultantsBonusVo> returnList = new ArrayList<ConsultantsBonusVo>();
    		//填充数据
    		if(page != null){
    			ConsultantsBonusVo totalInfo = getInitConsultantsBonusTotalInfo();
    			//填充累计已计提奖金
    			for(ConsultantsBonusVo vo : page){
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
    		return new ArrayList<ConsultantsBonusVo>();
    	}
    }
    
    /**
     * 查询符合条件的所有数据记录
     * @param contractId
     * @param statWeek
     * @return
     */
    @Transactional(readOnly = true)
    public List<ConsultantsBonusVo> getConsultantsBonusDetailList(Long contractId,Long statWeek){
    	List<ConsultantsBonusVo> resultList =  consultantsBonusDao.getConsultantsBonusDetailList(contractId,statWeek);
    	return resultList;
    }
    
    /**
     * 初始化咨询奖金合计
     * @return
     */
    private ConsultantsBonusVo getInitConsultantsBonusTotalInfo(){
    	ConsultantsBonusVo totalInfo = new ConsultantsBonusVo();
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
    private void handleDoubleScale(ConsultantsBonusVo totalInfo){
    	totalInfo.setAmount(StringUtil.getScaleDouble(totalInfo.getAmount(), 2));
    	totalInfo.setBonusBasis(StringUtil.getScaleDouble(totalInfo.getBonusBasis(), 2));
    	totalInfo.setCurrentBonus(StringUtil.getScaleDouble(totalInfo.getCurrentBonus(), 2));
    	totalInfo.setAccumulationBonus(StringUtil.getScaleDouble(totalInfo.getAccumulationBonus(), 2));
    }
}
