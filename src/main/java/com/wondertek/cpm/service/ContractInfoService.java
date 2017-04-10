package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.wondertek.cpm.domain.ContractFinishInfo;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.repository.ContractFinishInfoRepository;
import com.wondertek.cpm.repository.ContractInfoDao;
import com.wondertek.cpm.repository.ContractInfoRepository;
import com.wondertek.cpm.repository.ContractUserDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractInfo.
 */
@Service
@Transactional
public class ContractInfoService {

    private final Logger log = LoggerFactory.getLogger(ContractInfoService.class);
    @Inject
    private ContractInfoRepository contractInfoRepository;
    @Inject
    private ContractInfoDao contractInfoDao;
    @Inject
    private ContractUserDao contractUserDao;
    @Inject
    private ContractFinishInfoRepository contractFinishInfoRepository;
    
    @Inject
    private UserRepository userRepository;
    /**
     * Save a contractInfo.
     *
     * @param contractInfo the entity to save
     * @return the persisted entity
     */
    public ContractInfo save(ContractInfo contractInfo) {
        log.debug("Request to save ContractInfo : {}", contractInfo);
        ContractInfo result = contractInfoRepository.save(contractInfo);
        return result;
    }

    /**
     *  Get all the contractInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ContractInfos");
        Page<ContractInfo> result = contractInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractInfo findOne(Long id) {
        log.debug("Request to get ContractInfo : {}", id);
        ContractInfo contractInfo = contractInfoRepository.findOne(id);
        return contractInfo;
    }

    /**
     *  Delete the  contractInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractInfo : {}", id);
        ContractInfo contractInfo = contractInfoRepository.findOne(id);
        if (contractInfo != null) {
        	//更新合同人员的离开日期
        	long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
        	contractUserDao.updateLeaveDayByContract(id,leaveDay,SecurityUtils.getCurrentUserLogin());
        	
        	contractInfo.setStatus(ContractInfo.STATUS_DELETED);
        	contractInfo.setUpdateTime(ZonedDateTime.now());
        	contractInfo.setUpdator(SecurityUtils.getCurrentUserLogin());
            contractInfoRepository.save(contractInfo);
            
		}
    }

    /**
     * Search for the contractInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractInfo> search(String query, Pageable pageable) {
    	log.debug("Request to search for a page of ContractInfos for query {}", query);
    	Page<ContractInfo> result = null;
        return result;
    }
    @Transactional(readOnly = true) 
	public Page<ContractInfoVo> getContractInfoPage(ContractInfo contractInfo, Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
			return contractInfoDao.getContractInfoPage(contractInfo, pageable,user,deptInfo);
		}
		return new PageImpl(new ArrayList<ContractInfoVo>(), pageable, 0);
	}
    @Transactional(readOnly = true) 
	public boolean checkByContract(String serialNum, Long id) {
		return contractInfoDao.checkByContract(serialNum, id);
	}
    @Transactional(readOnly = true) 
	public ContractInfoVo getUserContractInfo(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
			return contractInfoDao.getUserContractInfo(id,user,deptInfo);
		}
		return null;
	}
    @Transactional(readOnly = true) 
	public List<LongValue> queryUserContract() {
		List<LongValue> returnList = new ArrayList<LongValue>();
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		returnList = contractInfoDao.queryUserContract(user,deptInfo);
    	}
		return returnList;
	}
    
    @Transactional(readOnly = true)
    public Map<String,ContractInfo> getContractInfoMapBySerialnum(){
    	List<ContractInfo> contractInfos = contractInfoRepository.findAll();
    	Map<String,ContractInfo> returnMap = new HashMap<String,ContractInfo>();
    	if (contractInfos != null) {
			for (ContractInfo contractInfo : contractInfos) {
				returnMap.put(contractInfo.getSerialNum(), contractInfo);
			} 
		}
		return returnMap;
    }

	public int finishContractInfo(Long id, Double finishRate) {
		String updator = SecurityUtils.getCurrentUserLogin();
		//保存记录
		ContractFinishInfo contractFinishInfo = new ContractFinishInfo();
		contractFinishInfo.setCreateTime(ZonedDateTime.now());
		contractFinishInfo.setCreator(updator);
		contractFinishInfo.setFinishRate(finishRate);
		contractFinishInfo.setId(null);
		contractFinishInfo.setContractId(id);
		contractFinishInfoRepository.save(contractFinishInfo);
		
		return contractInfoDao.finishContractInfo(id,finishRate,updator);
	}

	public int endContractInfo(Long id) {
		String updator = SecurityUtils.getCurrentUserLogin();
//		//保存记录
//		ContractFinishInfo contractFinishInfo = new ContractFinishInfo();
//		contractFinishInfo.setCreateTime(ZonedDateTime.now());
//		contractFinishInfo.setCreator(updator);
//		contractFinishInfo.setFinishRate(100d);
//		contractFinishInfo.setId(null);
//		contractFinishInfo.setContractId(id);
//		contractFinishInfoRepository.save(contractFinishInfo);
		
		//更新合同人员的离开日期
		long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
		contractUserDao.updateLeaveDayByContract(id,leaveDay,updator);
				
		return contractInfoDao.endContractInfo(id,updator);
	}
	
	/**
	 * 更新、新增合同信息
	 * @param contractInfos
	 */
	public void saveOrUpdateUploadRecord(List<ContractInfo> contractInfos){
		if(contractInfos != null){
			Optional<ContractInfo> oldInfo = null;
			for(ContractInfo contractInfo : contractInfos){
				oldInfo = contractInfoRepository.findOneBySerialNum(contractInfo.getSerialNum());
				if(oldInfo.isPresent()){//修改
					contractInfo.setId(oldInfo.get().getId());
					contractInfo.setCreator(oldInfo.get().getCreator());
					contractInfo.setCreateTime(oldInfo.get().getCreateTime());
				}
			}
			contractInfoRepository.save(contractInfos);
		}
	}

	public Map<String, Long> getContractInfo() {
		List<ContractInfo> infos = contractInfoRepository.findContractInfo();
		Map<String,Long> returnMap = new HashMap<String,Long>();
		if(infos != null){
			for(ContractInfo contractInfo : infos){
				returnMap.put(contractInfo.getSerialNum(), contractInfo.getId());
			}
		}
		return returnMap;
	}

}
