package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.SalesBonus;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.SalesBonusVo;
import com.wondertek.cpm.repository.SalesBonusDao;
import com.wondertek.cpm.repository.SalesBonusRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ProjectInfo.
 */
@Service
@Transactional
public class SalesBonusService {

    private final Logger log = LoggerFactory.getLogger(SalesBonusService.class);
    
    @Inject
    private SalesBonusRepository salesBonusRepository;
    @Inject
    private SalesAnnualIndexService salesAnnualIndexService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private SalesBonusDao salesBonusDao;
    
    public SalesBonus save(SalesBonus salesBonus) {
        log.debug("Request to save ProjectInfo : {}", salesBonus);
        SalesBonus result = salesBonusRepository.save(salesBonus);
        return result;
    }
    @Transactional(readOnly = true) 
    public SalesBonus findOne(Long id) {
        log.debug("Request to get SalesBonus : {}", id);
        SalesBonus salesBonus = salesBonusRepository.findOne(id);
        return salesBonus;
    }
    /**
     * 获取销售年奖金
     * @return
     */
    @Transactional(readOnly = true)
	public List<SalesBonusVo> getUserPage(SalesBonus salesBonus) {
    	Long originYear = salesBonus.getOriginYear();//必须有值，前面控制了。
    	//获取所有销售的该年的所有年指标
    	Map<Long,Double> annualIndexMap = salesAnnualIndexService.getAnnualIndexByStatYear(originYear);
    	//获取列表页
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		List<SalesBonusVo> returnList = salesBonusDao.getUserPage(user,deptInfo,salesBonus);
    		if(returnList != null){
    			SalesBonusVo totalInfo = getInitSalesBonusTotalInfo();
    			//统计销售在该年里面的所有累计完成金额/合同累计完成率
    			Map<Long,Double> salesTotalMap = new HashMap<Long,Double>();
    			for(SalesBonusVo salesBonusVo : returnList){
    				if(salesTotalMap.containsKey(salesBonusVo.getSalesManId())){
    					salesTotalMap.put(salesBonusVo.getSalesManId(), salesTotalMap.get(salesBonusVo.getSalesManId()) + salesBonusVo.getContractAmount());
    				}else{
    					salesTotalMap.put(salesBonusVo.getSalesManId(), salesBonusVo.getContractAmount());
    				}
    			}
    			//填充数据
    			Long currentUser = null;
    			for(SalesBonusVo salesBonusVo : returnList){
    				if(currentUser == null || currentUser.longValue() != salesBonusVo.getSalesManId()){
    					currentUser = salesBonusVo.getSalesManId();
    					//填充合同年指标
    					if(annualIndexMap.containsKey(salesBonusVo.getSalesManId())){
    						salesBonusVo.setAnnualIndex(StringUtil.getScaleDouble(annualIndexMap.get(salesBonusVo.getSalesManId()),2));
    					}else{//一般不会进来,肯定走上面有值的
    						salesBonusVo.setAnnualIndex(0d);
    					}
    					//填充total
    					totalInfo.setAnnualIndex(totalInfo.getAnnualIndex() + salesBonusVo.getAnnualIndex());
    					
    					//填充合同累计完成金额
    					if(salesTotalMap.containsKey(salesBonusVo.getSalesManId())){
    						salesBonusVo.setFinishTotal(StringUtil.getScaleDouble(salesTotalMap.get(salesBonusVo.getSalesManId()),2));
    					}else{//一般不会进来,肯定走上面有值的
    						salesBonusVo.setFinishTotal(0d);
    					}
    					//填充total
    					totalInfo.setFinishTotal(totalInfo.getFinishTotal() + salesBonusVo.getFinishTotal());//合同累计完成金额
    				}
    				//填充累计已计提奖金
    				salesBonusVo.setTotalBonus(salesBonusVo.getCurrentBonus());//合同年指标
    				//填充合同累计完成率
    				if(!annualIndexMap.containsKey(salesBonusVo.getSalesManId())){//没有年指标，默认完成率就是100%
    					salesBonusVo.setFinishRate(100d);
    				}else if(salesTotalMap.containsKey(salesBonusVo.getSalesManId())){//有累计完成金额
    					salesBonusVo.setFinishRate(StringUtil.getScaleDouble(
    							salesTotalMap.get(salesBonusVo.getSalesManId())/(annualIndexMap.get(salesBonusVo.getSalesManId())) * 100,
    							2));
    				}else{//其他都是0
    					salesBonusVo.setFinishRate(0d);
    				}
    				//填充可发放奖金
    				salesBonusVo.setPayBonus(StringUtil.getScaleDouble(
    						salesBonusVo.getCurrentBonus() * salesBonusVo.getFinishRate() / 100,
    						2));
    				
    				//填充total
    				totalInfo.setContractAmount(totalInfo.getContractAmount() + salesBonusVo.getContractAmount());//合同金额
    				totalInfo.setReceiveTotal(totalInfo.getReceiveTotal() + salesBonusVo.getReceiveTotal());//收款金额
    				totalInfo.setTaxes(totalInfo.getTaxes() + salesBonusVo.getTaxes());//税收
    				totalInfo.setShareCost(totalInfo.getShareCost() + salesBonusVo.getShareCost());//公摊成本
    				totalInfo.setThirdPartyPurchase(totalInfo.getThirdPartyPurchase() + salesBonusVo.getThirdPartyPurchase());//第三方采购
    				totalInfo.setBonusBasis(totalInfo.getBonusBasis() + salesBonusVo.getBonusBasis());//奖金基数
    				totalInfo.setCurrentBonus(totalInfo.getCurrentBonus() + salesBonusVo.getCurrentBonus());//本期奖金
    				totalInfo.setTotalBonus(totalInfo.getTotalBonus() + salesBonusVo.getTotalBonus());//累计已计提奖金
    				totalInfo.setPayBonus(totalInfo.getPayBonus() + salesBonusVo.getPayBonus());//可发放奖金
    			}
    			//处理合计的Double值
    			handleDoubleScale(totalInfo);
    			//添加合计
    			returnList.add(totalInfo);
    			return returnList;
    		}
    	}
		return new ArrayList<SalesBonusVo>();
	}
    /**
     * 处理合计里面的double值，只精确到小数点后2位
     * @param totalInfo
     */
	private void handleDoubleScale(SalesBonusVo totalInfo) {
		totalInfo.setAnnualIndex(StringUtil.getScaleDouble(totalInfo.getAnnualIndex(), 2));//合同年指标
		totalInfo.setFinishTotal(StringUtil.getScaleDouble(totalInfo.getFinishTotal(), 2));//合同累计完成金额
		totalInfo.setContractAmount(StringUtil.getScaleDouble(totalInfo.getContractAmount(), 2));//合同金额
		totalInfo.setReceiveTotal(StringUtil.getScaleDouble(totalInfo.getReceiveTotal(), 2));//收款金额
		totalInfo.setTaxes(StringUtil.getScaleDouble(totalInfo.getTaxes(), 2));//税收
		totalInfo.setShareCost(StringUtil.getScaleDouble(totalInfo.getShareCost(), 2));//公摊成本
		totalInfo.setThirdPartyPurchase(StringUtil.getScaleDouble(totalInfo.getThirdPartyPurchase(), 2));//第三方采购
		totalInfo.setBonusBasis(StringUtil.getScaleDouble(totalInfo.getBonusBasis(), 2));//奖金基数
		totalInfo.setCurrentBonus(StringUtil.getScaleDouble(totalInfo.getCurrentBonus(), 2));//本期奖金
		totalInfo.setTotalBonus(StringUtil.getScaleDouble(totalInfo.getTotalBonus(), 2));//累计已计提奖金
		totalInfo.setPayBonus(StringUtil.getScaleDouble(totalInfo.getPayBonus(), 2));//可发放奖金
	}
	/**
     * 获取初始的销售合计
     * @return
     */
	private SalesBonusVo getInitSalesBonusTotalInfo() {
		SalesBonusVo totalInfo = new SalesBonusVo();
		totalInfo.setSalesMan("合计");
		totalInfo.setAnnualIndex(0d);//合同年指标
		totalInfo.setFinishTotal(0d);//合同累计完成金额
		totalInfo.setContractAmount(0d);//合同金额
		totalInfo.setReceiveTotal(0d);//收款金额
		totalInfo.setTaxes(0d);//税收
		totalInfo.setShareCost(0d);//公摊成本
		totalInfo.setThirdPartyPurchase(0d);//第三方采购
		totalInfo.setBonusBasis(0d);//奖金基数
		totalInfo.setCurrentBonus(0d);//本期奖金
		totalInfo.setTotalBonus(0d);//累计已计提奖金
		totalInfo.setPayBonus(0d);//可发放奖金
		return totalInfo;
	}
}
