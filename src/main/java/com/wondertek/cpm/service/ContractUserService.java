package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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

import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.ContractUser;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ContractUserVo;
import com.wondertek.cpm.domain.vo.ProjectUserVo;
import com.wondertek.cpm.repository.ContractUserDao;
import com.wondertek.cpm.repository.ContractUserRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing ContractUser.
 */
@Service
@Transactional
public class ContractUserService {

    private final Logger log = LoggerFactory.getLogger(ContractUserService.class);
    
    @Inject
    private ContractUserRepository contractUserRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ContractUserDao contractUserDao;
    @Inject
    private UserTimesheetService userTimesheetService;
    /**
     * Save a contractUser.
     *
     * @param contractUser the entity to save
     * @return the persisted entity
     */
    public ContractUser save(ContractUser contractUser) {
        log.debug("Request to save ContractUser : {}", contractUser);
        ContractUser result = contractUserRepository.save(contractUser);
        return result;
    }

    /**
     *  Get all the contractUsers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ContractUser> findAll(Pageable pageable) {
        log.debug("Request to get all ContractUsers");
        Page<ContractUser> result = contractUserRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one contractUser by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ContractUser findOne(Long id) {
        log.debug("Request to get ContractUser : {}", id);
        ContractUser contractUser = contractUserRepository.findOne(id);
        return contractUser;
    }

    /**
     *  Delete the  contractUser by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ContractUser : {}", id);
        ContractUser contractUser = contractUserRepository.findOne(id);
        if (contractUser != null) {
        	long leaveDay = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date()));
        	if(contractUser.getJoinDay() != null && contractUser.getJoinDay().longValue() > leaveDay){//加盟日大于当前日期，直接删除
        		contractUserRepository.delete(id);
        	}else{
        		//判定加盟日之间是否有日报，没有的话，直接删除
        		Long workDay = null;
        		if(contractUser.getLeaveDay() == null){
        			workDay = userTimesheetService.getWorkDayByParam(contractUser.getUserId(), contractUser.getContractId(), UserTimesheet.TYPE_CONTRACT, 
        					contractUser.getJoinDay(), leaveDay, 3);
        		}else{
        			workDay = userTimesheetService.getWorkDayByParam(contractUser.getUserId(), contractUser.getContractId(), UserTimesheet.TYPE_CONTRACT, 
        					contractUser.getJoinDay(), contractUser.getLeaveDay(), 3);
        		}
        		if(workDay == null){
        			contractUserRepository.delete(id);
        		}else if(contractUser.getLeaveDay() == null || contractUser.getLeaveDay() > leaveDay){
        			contractUser.setLeaveDay(leaveDay);
        			if(contractUser.getJoinDay() > leaveDay){
        				contractUser.setJoinDay(leaveDay);
        			}
        			contractUser.setUpdateTime(ZonedDateTime.now());
        			contractUser.setUpdator(SecurityUtils.getCurrentUserLogin());
        			contractUserRepository.save(contractUser);
        		}
        	}
		}
    }

    /**
     * Search for the contractUser corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ContractUser> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ContractUsers for query {}", query);
        Page<ContractUser> result = null;
        return result;
    }
    @Transactional(readOnly = true) 
	public Page<ContractUserVo> getUserPage(ContractUser contractUser, Pageable pageable) {
    	log.debug("Request to get all ContractUsers");
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return contractUserDao.getUserPage(contractUser,user,deptInfo,pageable);
    	}
    	
    	return new PageImpl(new ArrayList<ProjectUserVo>(), pageable, 0);
			
	}

	public ContractUserVo getContractUser(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return contractUserDao.getContractUser(user,deptInfo,id);
    	}
    	return null;
	}

	public boolean checkUserExist(ContractUser contractUser) {
		List<ContractUser> list = contractUserRepository.findByUserId(contractUser.getUserId(),contractUser.getContractId());
		if (list != null) {
			long joinDay = 0;
			long id = contractUser.getId() == null ? 0:contractUser.getId().longValue();
			
			for (ContractUser tmp : list) {
				joinDay = tmp.getJoinDay().longValue();
				if (contractUser.getId() != null && id == tmp.getId()) {
					continue;
				}else if (joinDay == contractUser.getJoinDay()) {
					return true;
				}else if(joinDay < contractUser.getJoinDay() && (tmp.getLeaveDay() == null || tmp.getLeaveDay().longValue() >= contractUser.getJoinDay())){
					return true;
				}else if(joinDay > contractUser.getJoinDay() && (contractUser.getLeaveDay() == null || contractUser.getLeaveDay() >= joinDay)){
					return true;
				}
			}
		}
		
		return false;
	}

	public List<ContractUserVo> getContractUserList(ContractUser contractUser) {
		log.debug("Request to get all ContractUsers");
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		
    		return contractUserDao.getContractUserData(contractUser,user,deptInfo);
    	}
    	
    	return null;
	}


	public Map<Long, Long> getdates(Long contractId, Long userId) {
		Map<Long,Long> map = new HashMap<Long,Long>();
		List<ContractUser> contractUserList = contractUserRepository.getdatesByContractIdAndUserId(contractId,userId);
		for(ContractUser contractUser : contractUserList){
			map.put(contractUser.getJoinDay(), contractUser.getLeaveDay());
		}
		return map;
	}

	public void saveAll(ContractUser contractUser) {
		contractUserRepository.save(contractUser);
	}

}
