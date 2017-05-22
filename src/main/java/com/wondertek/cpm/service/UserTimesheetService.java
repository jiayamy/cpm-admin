package com.wondertek.cpm.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.ContractInfoVo;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.ParticipateInfo;
import com.wondertek.cpm.domain.vo.ProjectInfoVo;
import com.wondertek.cpm.domain.vo.UserTimesheetForHardWorkingVo;
import com.wondertek.cpm.domain.vo.UserTimesheetForOther;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.domain.vo.UserTimesheetVo;
import com.wondertek.cpm.repository.ContractUserDao;
import com.wondertek.cpm.repository.HolidayInfoRepository;
import com.wondertek.cpm.repository.ProjectUserDao;
import com.wondertek.cpm.repository.SystemConfigRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetDao;
import com.wondertek.cpm.repository.UserTimesheetRepository;
import com.wondertek.cpm.security.SecurityUtils;

/**
 * Service Implementation for managing UserTimesheet.
 */
@Service
@Transactional
public class UserTimesheetService {

    private final Logger log = LoggerFactory.getLogger(UserTimesheetService.class);
    
    @Inject
    private UserTimesheetRepository userTimesheetRepository;
    @Autowired
    private UserTimesheetDao userTimesheetDao;
    @Inject
    private UserRepository userRepository;
    @Inject
    private HolidayInfoRepository holidayInfoRepository;
    @Inject
    private HolidayInfoService holidayInfoService;
    @Inject
    private ProjectInfoService projectInfoService;
    @Inject
    private ContractInfoService contractInfoService;
    @Inject
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private ProjectUserDao projectUserDao;
    
    @Autowired
    private ContractUserDao contractUserDao;
    
    /**
     * Save a userTimesheet.
     *
     * @param userTimesheet the entity to save
     * @return the persisted entity
     */
    public UserTimesheet save(UserTimesheet userTimesheet) {
        log.debug("Request to save UserTimesheet : {}", userTimesheet);
        UserTimesheet result = userTimesheetRepository.save(userTimesheet);
        return result;
    }

    /**
     *  Get all the userTimesheets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<UserTimesheet> findAll(Pageable pageable) {
        log.debug("Request to get all UserTimesheets");
        Page<UserTimesheet> result = userTimesheetRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one userTimesheet by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public UserTimesheet findOne(Long id) {
        log.debug("Request to get UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetRepository.findOne(id);
        return userTimesheet;
    }

    /**
     *  Delete the  userTimesheet by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserTimesheet : {}", id);
        UserTimesheet userTimesheet = userTimesheetRepository.findOne(id);
        if(userTimesheet != null){
        	userTimesheet.setStatus(CpmConstants.STATUS_DELETED);
        	userTimesheet.setUpdateTime(ZonedDateTime.now());
        	userTimesheet.setUpdator(SecurityUtils.getCurrentUserLogin());
        	//更新对应的合同金额
        	if(userTimesheet.getType().intValue() == UserTimesheet.TYPE_PROJECT){
        		//查询出外包合同月对应的工作日
        		Double monthWorkDay = StringUtil.nullToDouble(systemConfigRepository.findValueByKey("contract.external.month.day"));
        		//知道该员工在此项目中的报价
        		List<Object> offerList = userTimesheetDao.getOffer(userTimesheet.getUserId(),userTimesheet.getObjId(),userTimesheet.getWorkDay());
        		if (offerList != null && !offerList.isEmpty()) {
        			Double changeAmount =  - userTimesheet.getRealInput() * (Double)offerList.get(1) / monthWorkDay / 8;
            		ContractInfo contractInfo = new ContractInfo();
            		if (changeAmount.doubleValue() != 0) {
            			contractInfo.setAmount(changeAmount);
            			contractInfo.setId((Long)offerList.get(0));
    				}
            		userTimesheetDao.saveByDelete(userTimesheet,contractInfo);
				}
        	}else {
				userTimesheetRepository.save(userTimesheet);
			}
        }
    }
    
    /**
     * Search for the userTimesheet corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserTimesheet> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserTimesheets for query {}", query);
        Page<UserTimesheet> result = null;
        return result;
    }
    /**
     * 获取员工自己查看的列表
     * @param userTimesheet
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true) 
    public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<UserTimesheet> page = userTimesheetDao.getUserPage(userTimesheet,pageable,user.get());
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<UserTimesheet>(), pageable, 0);
    	}
    }
    @Transactional(readOnly = true) 
    public Page<UserTimesheet> getContractPage(UserTimesheet userTimesheet, Pageable pageable) {
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		Page<UserTimesheet> page = userTimesheetDao.getContractPage(userTimesheet,user,deptInfo,pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<UserTimesheet>(), pageable, 0);
    	}
	}
    @Transactional(readOnly = true) 
    public Page<UserTimesheet> getProjectPage(UserTimesheet userTimesheet, Pageable pageable) {
    	List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		Object[] o = objs.get(0);
    		User user = (User) o[0];
    		DeptInfo deptInfo = (DeptInfo) o[1];
    		Page<UserTimesheet> page = userTimesheetDao.getProjectPage(userTimesheet,user,deptInfo,pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<UserTimesheet>(), pageable, 0);
    	}
	}
    /**
     * 获取编辑列表中的数据
     * @return
     */
    @Transactional(readOnly = true) 
	public List<UserTimesheetForUser> queryEditByUser(Date workDayDate) {
		//查询现有的所有日报
		List<UserTimesheetForUser> returnList = new ArrayList<UserTimesheetForUser>();
		//查询当天的周一至周日
		String[] ds = DateUtil.getWholeWeekByDate(workDayDate);
		//添加第一行日期
		returnList.add(new UserTimesheetForUser(UserTimesheet.TYPE_DAY,ds[0],ds[1],ds[2],ds[3],ds[4],ds[5],ds[6]));
		Long[] lds = new Long[7];
		lds[0] = StringUtil.nullToLong(ds[0]);
		lds[1] = StringUtil.nullToLong(ds[1]);
		lds[2] = StringUtil.nullToLong(ds[2]);
		lds[3] = StringUtil.nullToLong(ds[3]);
		lds[4] = StringUtil.nullToLong(ds[4]);
		lds[5] = StringUtil.nullToLong(ds[5]);
		lds[6] = StringUtil.nullToLong(ds[6]);
		
		//添加第二行地区
		UserTimesheetForUser areaTimesheet = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_AREA,null,null,null,null);
		//添加默认的公共成本(正常工时)
		UserTimesheetForUser publicTimesheet = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_PUBLIC,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_NORMAL);
		//添加默认的公共成本(加班工时)
		UserTimesheetForUser publicTimesheetExtra = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_PUBLIC,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_EXTRA);
		//添加项目和合同
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Long userId = user.get().getId();
    		String workArea = user.get().getWorkArea();
    		publicTimesheet.setUserId(userId);
    		publicTimesheetExtra.setUserId(userId);
    		//查询现有的所有记录
    		List<UserTimesheet> list = userTimesheetDao.getByWorkDayAndUser(lds[0],lds[6],userId);
    		//转换为MAP
    		Map<String,Map<Long,UserTimesheet>> map = trans2Map(list);
    		//获取用户现有的所有项目和合同
    		List<LongValue> ids = contractUserDao.getByUserAndDay(userId,lds);
    		ids.addAll(projectUserDao.getByUserAndDay(userId,lds));
    		
    		//添加默认的公共成本
    		String key = getTransMapKey(userId,UserTimesheet.TYPE_PUBLIC,null);
    		Map<Long, UserTimesheet> childs = map.get(key);
    		if(childs != null){
    			setUserTimesheetForUser(areaTimesheet,publicTimesheet,lds,childs);
    			setExtraUserTimesheetForUser(areaTimesheet,publicTimesheetExtra,lds,childs);
    			map.remove(key);
    		}
    		//现有的所有项目和合同
    		for(LongValue longValue : ids){
    			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,longValue.getType(),longValue.getKey(),longValue.getVal(),CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_NORMAL);
    			UserTimesheetForUser timesheetExtra = getDefaultUserTimesheetForUser(userId,longValue.getType(),longValue.getKey(),longValue.getVal(),CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_EXTRA);
    			key = getTransMapKey(userId,longValue.getType(),longValue.getKey());
    			childs = map.get(key);
        		if(childs != null){
        			setUserTimesheetForUser(areaTimesheet,timesheet,lds,childs);
        			setExtraUserTimesheetForUser(areaTimesheet,timesheetExtra,lds,childs);
        			map.remove(key);
        		}
        		returnList.add(timesheet);
        		returnList.add(timesheetExtra);
    		}
    		//以前参与了项目或合同，现在在参与者中没找到
    		if(!map.isEmpty()){
    			getOtherTimesheetForUser(returnList,areaTimesheet,map,userId,lds);
    		}
    		//填充默认的工作地点
    		checkAreaTimesheet(areaTimesheet,workArea);
    	}
    	//添加地区
    	returnList.add(1, areaTimesheet);
    	returnList.add(2, publicTimesheet);
    	returnList.add(3, publicTimesheetExtra);
		return returnList;
	}
    /**
     * 检查地点是否都已经填充
     */
	private void checkAreaTimesheet(UserTimesheetForUser areaTimesheet, String workArea) {
		if(areaTimesheet.getData1() == null){
			areaTimesheet.setData1(workArea);
		}
		if(areaTimesheet.getData2() == null){
			areaTimesheet.setData2(workArea);
		}
		if(areaTimesheet.getData3() == null){
			areaTimesheet.setData3(workArea);
		}
		if(areaTimesheet.getData4() == null){
			areaTimesheet.setData4(workArea);
		}
		if(areaTimesheet.getData5() == null){
			areaTimesheet.setData5(workArea);
		}
		if(areaTimesheet.getData6() == null){
			areaTimesheet.setData6(workArea);
		}
		if(areaTimesheet.getData7() == null){
			areaTimesheet.setData7(workArea);
		}
	}

	private void getOtherTimesheetForUser(List<UserTimesheetForUser> returnList, UserTimesheetForUser areaTimesheet, Map<String, Map<Long, UserTimesheet>> map, Long userId, Long[] lds) {
		UserTimesheet tmpTimesheet = null;
		Map<Long, UserTimesheet> childs = null;
		long workDayL = 0;
		for(String mapKey : map.keySet()){
			childs = map.get(mapKey);
			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,null,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_NORMAL);
			UserTimesheetForUser timesheetExtra = getDefaultUserTimesheetForUser(userId,null,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_EXTRA);
			for(Long workDay : childs.keySet()){
				tmpTimesheet = childs.get(workDay);
				//正常工时
				timesheet.setType(tmpTimesheet.getType());
				timesheet.setObjId(tmpTimesheet.getObjId());
				timesheet.setObjName(tmpTimesheet.getObjName());
				//加班工时
				timesheetExtra.setType(tmpTimesheet.getType());
				timesheetExtra.setObjId(tmpTimesheet.getObjId());
				timesheetExtra.setObjName(tmpTimesheet.getObjName());
				
				workDayL = workDay.longValue();
				if(lds[0] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData1(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData1(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData1() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData1(tmpTimesheet.getWorkArea());
					}
					timesheet.setId1(tmpTimesheet.getId());
					timesheetExtra.setId1(tmpTimesheet.getId());	//加班工时
				}else if(lds[1] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData2(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData2(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData2() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData2(tmpTimesheet.getWorkArea());
					}
					timesheet.setId2(tmpTimesheet.getId());
					timesheetExtra.setId2(tmpTimesheet.getId());	//加班工时
				}else if(lds[2] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData3(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData3(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData3() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData3(tmpTimesheet.getWorkArea());
					}
					timesheet.setId3(tmpTimesheet.getId());
					timesheetExtra.setId3(tmpTimesheet.getId());	//加班工时
				}else if(lds[3] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData4(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData4(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData4() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData4(tmpTimesheet.getWorkArea());
					}
					timesheet.setId4(tmpTimesheet.getId());
					timesheetExtra.setId4(tmpTimesheet.getId());	//加班工时
				}else if(lds[4] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData5(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData5(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData5() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData5(tmpTimesheet.getWorkArea());
					}
					timesheet.setId5(tmpTimesheet.getId());
					timesheetExtra.setId5(tmpTimesheet.getId());	//加班工时
				}else if(lds[5] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData6(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData6(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData6() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData6(tmpTimesheet.getWorkArea());
					}
					timesheet.setId6(tmpTimesheet.getId());
					timesheetExtra.setId6(tmpTimesheet.getId());	//加班工时
				}else if(lds[6] == workDayL){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData7(tmpTimesheet.getRealInput().toString());			//正常工时
						timesheetExtra.setData7(tmpTimesheet.getExtraInput().toString());	//加班工时
					}
					if(areaTimesheet.getData7() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData7(tmpTimesheet.getWorkArea());
					}
					timesheet.setId7(tmpTimesheet.getId());
					timesheetExtra.setId7(tmpTimesheet.getId());	//加班工时
				}
			}
			returnList.add(timesheet);
			returnList.add(timesheetExtra);		//添加加班工时
		}
	}
	
	private void getExtraOtherTimesheetForUser(List<UserTimesheetForUser> returnList, UserTimesheetForUser areaTimesheet, Map<String, Map<Long, UserTimesheet>> map, Long userId, Long[] lds) {
		UserTimesheet tmpTimesheet = null;
		Map<Long, UserTimesheet> childs = null;
		long workDayL = 0;
		for(String mapKey : map.keySet()){
			childs = map.get(mapKey);
			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,null,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT,UserTimesheet.TYPE_INPUT_EXTRA);
			for(Long workDay : childs.keySet()){
				tmpTimesheet = childs.get(workDay);
				timesheet.setType(tmpTimesheet.getType());
				timesheet.setObjId(tmpTimesheet.getObjId());
				timesheet.setObjName(tmpTimesheet.getObjName());
				workDayL = workDay.longValue();
				if(lds[0] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData1(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData1() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData1(tmpTimesheet.getWorkArea());
					}
					timesheet.setId1(tmpTimesheet.getId());
				}else if(lds[1] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData2(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData2() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData2(tmpTimesheet.getWorkArea());
					}
					timesheet.setId2(tmpTimesheet.getId());
				}else if(lds[2] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData3(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData3() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData3(tmpTimesheet.getWorkArea());
					}
					timesheet.setId3(tmpTimesheet.getId());
				}else if(lds[3] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData4(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData4() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData4(tmpTimesheet.getWorkArea());
					}
					timesheet.setId4(tmpTimesheet.getId());
				}else if(lds[4] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData5(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData5() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData5(tmpTimesheet.getWorkArea());
					}
					timesheet.setId5(tmpTimesheet.getId());
				}else if(lds[5] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData6(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData6() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData6(tmpTimesheet.getWorkArea());
					}
					timesheet.setId6(tmpTimesheet.getId());
				}else if(lds[6] == workDayL){
					if(tmpTimesheet.getExtraInput() != null){
						timesheet.setData7(tmpTimesheet.getExtraInput().toString());
					}
					if(areaTimesheet.getData7() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData7(tmpTimesheet.getWorkArea());
					}
					timesheet.setId7(tmpTimesheet.getId());
				}
			}
			returnList.add(timesheet);
		}
	}

	/**
	 * 获取该用户在该项目或者合同或公共成本中的一周记录
	 */
	private void setUserTimesheetForUser(UserTimesheetForUser areaTimesheet, UserTimesheetForUser userTimesheet, Long[] lds,Map<Long, UserTimesheet> childs) {
		UserTimesheet tmp = childs.get(lds[0]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData1(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData1() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData1(tmp.getWorkArea());
			}
			userTimesheet.setId1(tmp.getId());
		}
		tmp = childs.get(lds[1]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData2(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData2() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData2(tmp.getWorkArea());
			}
			userTimesheet.setId2(tmp.getId());
		}
		tmp = childs.get(lds[2]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData3(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData3() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData3(tmp.getWorkArea());
			}
			userTimesheet.setId3(tmp.getId());
		}
		tmp = childs.get(lds[3]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData4(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData4() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData4(tmp.getWorkArea());
			}
			userTimesheet.setId4(tmp.getId());
		}
		tmp = childs.get(lds[4]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData5(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData5() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData5(tmp.getWorkArea());
			}
			userTimesheet.setId5(tmp.getId());
		}
		tmp = childs.get(lds[5]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData6(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData6() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData6(tmp.getWorkArea());
			}
			userTimesheet.setId6(tmp.getId());
		}
		tmp = childs.get(lds[6]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData7(tmp.getRealInput().toString());
			}
			if(areaTimesheet.getData7() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData7(tmp.getWorkArea());
			}
			userTimesheet.setId7(tmp.getId());
		}
	}
	/**
	 * 获取该用户在该项目或者合同或公共成本中的一周记录(加班工时)
	 */
	private void setExtraUserTimesheetForUser(UserTimesheetForUser areaTimesheet, UserTimesheetForUser userTimesheet, Long[] lds,Map<Long, UserTimesheet> childs){
		UserTimesheet tmp = childs.get(lds[0]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData1(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData1() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData1(tmp.getWorkArea());
			}
			userTimesheet.setId1(tmp.getId());
		}
		tmp = childs.get(lds[1]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData2(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData2() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData2(tmp.getWorkArea());
			}
			userTimesheet.setId2(tmp.getId());
		}
		tmp = childs.get(lds[2]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData3(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData3() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData3(tmp.getWorkArea());
			}
			userTimesheet.setId3(tmp.getId());
		}
		tmp = childs.get(lds[3]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData4(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData4() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData4(tmp.getWorkArea());
			}
			userTimesheet.setId4(tmp.getId());
		}
		tmp = childs.get(lds[4]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData5(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData5() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData5(tmp.getWorkArea());
			}
			userTimesheet.setId5(tmp.getId());
		}
		tmp = childs.get(lds[5]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData6(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData6() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData6(tmp.getWorkArea());
			}
			userTimesheet.setId6(tmp.getId());
		}
		tmp = childs.get(lds[6]);
		if(tmp != null){
			if(tmp.getExtraInput() != null){
				userTimesheet.setData7(tmp.getExtraInput().toString());
			}
			if(areaTimesheet.getData7() == null && !StringUtil.isNullStr(tmp.getWorkArea())){
				areaTimesheet.setData7(tmp.getWorkArea());
			}
			userTimesheet.setId7(tmp.getId());
		}
	}

	private UserTimesheetForUser getDefaultUserTimesheetForUser(Long userId, Integer type, Long objId, String objName, String defaultData, String inputType) {
		UserTimesheetForUser userTimesheetForUser = new UserTimesheetForUser();
		userTimesheetForUser.setUserId(userId);
		userTimesheetForUser.setType(type);
		userTimesheetForUser.setObjId(objId);
		userTimesheetForUser.setObjName(objName);
		userTimesheetForUser.setInputType(inputType);
		userTimesheetForUser.setData1(defaultData);
		userTimesheetForUser.setData2(defaultData);
		userTimesheetForUser.setData3(defaultData);
		userTimesheetForUser.setData4(defaultData);
		userTimesheetForUser.setData5(defaultData);
		userTimesheetForUser.setData6(defaultData);
		userTimesheetForUser.setData7(defaultData);
		return userTimesheetForUser;
	}

	/**
	 * 把所有的记录放到MAP中去
	 * @param list
	 * @return
	 */
	private Map<String, Map<Long, UserTimesheet>> trans2Map(List<UserTimesheet> list) {
		Map<String,Map<Long,UserTimesheet>> maps = new HashMap<String,Map<Long,UserTimesheet>>();
		if(list != null){
			String key = null;
			for(UserTimesheet sheet : list){
				key = getTransMapKey(sheet.getUserId(),sheet.getType(),sheet.getObjId());
				if(!maps.containsKey(key)){
					maps.put(key, new HashMap<Long,UserTimesheet>());
				}
				maps.get(key).put(sheet.getWorkDay(), sheet);
			}
		}
		return maps;
	}
	/**
	 * 获取MAP的key
	 * @return
	 */
	private String getTransMapKey(Long userId,Integer type,Long objId){
		if(type == UserTimesheet.TYPE_PUBLIC){
			return userId + "_"+ type;
		}else{
			return userId + "_"+ type + "_" + objId;
		}
	}
	/**
	 * 保存用户编辑的结果
	 * @return
	 */
	public String updateEditByUser(List<UserTimesheetForUser> userTimesheetForUsers) {
		if (userTimesheetForUsers == null || userTimesheetForUsers.isEmpty() || userTimesheetForUsers.size() < 3) {
			return "cpmApp.userTimesheet.save.paramError";
		}
		//查看当前用户
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		User currUser = user.get();
    		Long userId = currUser.getId();
    		String userName = currUser.getLastName();
    		String updator = currUser.getLogin();
    		//日期
    		UserTimesheetForUser dayTimesheet = userTimesheetForUsers.get(0);
    		Long[] lds = new Long[7];
    		lds[0] = StringUtil.nullToLong(dayTimesheet.getData1());
    		lds[1] = StringUtil.nullToLong(dayTimesheet.getData2());
    		lds[2] = StringUtil.nullToLong(dayTimesheet.getData3());
    		lds[3] = StringUtil.nullToLong(dayTimesheet.getData4());
    		lds[4] = StringUtil.nullToLong(dayTimesheet.getData5());
    		lds[5] = StringUtil.nullToLong(dayTimesheet.getData6());
    		lds[6] = StringUtil.nullToLong(dayTimesheet.getData7());
    		if(lds[0] == 0 || lds[1] == 0 || lds[2] == 0 || lds[3] == 0 || lds[4] == 0 || lds[5] == 0 || lds[6] == 0){
    			return "cpmApp.userTimesheet.save.paramError";
    		}
    		if(dayTimesheet.getType() != UserTimesheet.TYPE_DAY){//类型不是日期
    			return "cpmApp.userTimesheet.save.paramError";
    		}
    		//初始化用户在该周参与的项目
    		List<ParticipateInfo> participateInfos = contractUserDao.getInfoByUserAndDay(userId, lds);
    		participateInfos.addAll(projectUserDao.getInfoByUserAndDay(userId, lds));
    		
    		//地区
    		UserTimesheetForUser areaTimesheet = userTimesheetForUsers.get(1);
    		String[] areas = new String[7];
    		areas[0] = StringUtil.null2Str(areaTimesheet.getData1());
    		areas[1] = StringUtil.null2Str(areaTimesheet.getData2());
    		areas[2] = StringUtil.null2Str(areaTimesheet.getData3());
    		areas[3] = StringUtil.null2Str(areaTimesheet.getData4());
    		areas[4] = StringUtil.null2Str(areaTimesheet.getData5());
    		areas[5] = StringUtil.null2Str(areaTimesheet.getData6());
    		areas[6] = StringUtil.null2Str(areaTimesheet.getData7());
    		if(areaTimesheet.getType() != UserTimesheet.TYPE_AREA){//类型不是地区
    			return "cpmApp.userTimesheet.save.paramError";
    		}
    		//判定日报
    		Double td1 = 0d;
    		Double td2 = 0d;
    		Double td3 = 0d;
    		Double td4 = 0d;
    		Double td5 = 0d;
    		Double td6 = 0d;
    		Double td7 = 0d;
    		//加班日报
    		Double tds1 = 0d;
    		Double tds2 = 0d;
    		Double tds3 = 0d;
    		Double tds4 = 0d;
    		Double tds5 = 0d;
    		Double tds6 = 0d;
    		Double tds7 = 0d;
    		
    		Double d1 = 0d;
    		Double d2 = 0d;
    		Double d3 = 0d;
    		Double d4 = 0d;
    		Double d5 = 0d;
    		Double d6 = 0d;
    		Double d7 = 0d;
    		
    		List<UserTimesheet> saveList = new ArrayList<UserTimesheet>();
    		List<UserTimesheet> updateList = new ArrayList<UserTimesheet>();
    		List<Long> ids = new ArrayList<Long>();
    		
    		Map<String,UserTimesheet> updateMap = new HashMap<String,UserTimesheet>();
    		Map<String,UserTimesheet> saveMap = new HashMap<String,UserTimesheet>();
    		
    		for(int i = 2; i < userTimesheetForUsers.size(); i++){
    			//一条记录就是一个项目或者合同或者公共成本
    			UserTimesheetForUser userTimesheetForUser = userTimesheetForUsers.get(i);
    			if(userTimesheetForUser.getUserId() == null || userTimesheetForUser.getUserId() != userId.longValue()){
    				return "cpmApp.userTimesheet.save.noPermit";
    			}
    			if(!(userTimesheetForUser.getType() == UserTimesheet.TYPE_CONTRACT || userTimesheetForUser.getType() == UserTimesheet.TYPE_PROJECT
    					|| userTimesheetForUser.getType() == UserTimesheet.TYPE_PUBLIC)){//类型不是合同、项目、公共成本的
    				return "cpmApp.userTimesheet.save.paramError";
    			}
    			d1 = StringUtil.nullToDouble(userTimesheetForUser.getData1());
    			d2 = StringUtil.nullToDouble(userTimesheetForUser.getData2());
    			d3 = StringUtil.nullToDouble(userTimesheetForUser.getData3());
    			d4 = StringUtil.nullToDouble(userTimesheetForUser.getData4());
    			d5 = StringUtil.nullToDouble(userTimesheetForUser.getData5());
    			d6 = StringUtil.nullToDouble(userTimesheetForUser.getData6());
    			d7 = StringUtil.nullToDouble(userTimesheetForUser.getData7());
    			
    			if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
					if (d1 < 0 || d1 > 8 || d2 < 0 || d2 > 8 || d3 < 0 || d3 > 8 || d4 < 0 || d4 > 8 || d5 < 0 || d5 > 8
							|| d6 < 0 || d6 > 8 || d7 < 0 || d7 > 8) {
						return "cpmApp.userTimesheet.save.dataError";
					} 
					td1 += d1;
	        		td2 += d2;
	        		td3 += d3;
	        		td4 += d4;
	        		td5 += d5;
	        		td6 += d6;
	        		td7 += d7;
	        		if(td1 > 8 || td2 > 8 || td3 > 8 || td4 > 8
	        				 || td5 > 8 || td6 > 8 || td7 > 8){
	        			return "cpmApp.userTimesheet.save.dayDataMax";
	        		}
				}else{
					if ((d1 < 2 && d1 != 0) || d1 > 8 || (d2 < 2 && d2 != 0) || d2 > 8 || (d3 < 2 && d3 != 0) || d3 > 8 || (d4 < 2 && d4 != 0) || d4 > 8 || (d5 < 2 && d5 != 0) || d5 > 8
							|| (d6 < 2 && d6 != 0) || d6 > 8 || (d7 < 2 && d7 != 0) || d7 > 8) {
						return "cpmApp.userTimesheet.save.extraDataError";
					} 
					tds1 += d1;
	        		tds2 += d2;
	        		tds3 += d3;
	        		tds4 += d4;
	        		tds5 += d5;
	        		tds6 += d6;
	        		tds7 += d7;
	        		if(tds1 > 8 || tds2 > 8 || tds3 > 8 || tds4 > 8
	        				 || tds5 > 8 || tds6 > 8 || tds7 > 8){
	        			return "cpmApp.userTimesheet.save.dayExtraDataMax";
	        		}
				}
        		//校验用户在该项目中是否可以填数据
        		if(userTimesheetForUser.getType() == UserTimesheet.TYPE_CONTRACT || userTimesheetForUser.getType() == UserTimesheet.TYPE_PROJECT){
        			String result = checkParticipate(participateInfos,userTimesheetForUser,lds,d1,d2,d3,d4,d5,d6,d7);
        			if(result != null){
        				return "cpmApp.userTimesheet.save.objId#"+result;
        			}
        		}
        		if(userTimesheetForUser.getType() == UserTimesheet.TYPE_PUBLIC && UserTimesheet.TYPE_INPUT_EXTRA.equals(userTimesheetForUser.getInputType()) 
        				&& (d1 != 0 || d2 != 0 || d3 != 0 || d4 != 0|| d5 != 0|| d6 != 0 || d7 != 0)){//检验公共成本类型的加班时数是否为0
        			return "cpmApp.userTimesheet.save.extraError";
        		}
        		String key = null;
        		if(userTimesheetForUser.getId1() != null || d1 != 0){
        			if(userTimesheetForUser.getId1() != null){
        				if (!ids.contains(userTimesheetForUser.getId1())) {
							ids.add(userTimesheetForUser.getId1());
						}
        				key = lds[0]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
						if(UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())){
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d1);
	        					updateMap.get(key).setAcceptInput(d1);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId1(),d1,lds[0],areas[0]));
	        				}
        				}else{
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d1);
	        					updateMap.get(key).setAcceptExtraInput(d1);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId1(),d1,lds[0],areas[0]));
	        				}
        				}
        			}else{
        				key = lds[0]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d1);
        						saveMap.get(key).setAcceptInput(d1);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId1(), d1, lds[0], areas[0]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d1);
        						saveMap.get(key).setAcceptExtraInput(d1);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId1(), d1, lds[0], areas[0]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId2() != null || d2 != 0){
        			if(userTimesheetForUser.getId2() != null){
        				if (!ids.contains(userTimesheetForUser.getId2())) {
							ids.add(userTimesheetForUser.getId2());
						}
        				key = lds[1]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d2);
	        					updateMap.get(key).setAcceptInput(d2);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId2(),d2,lds[1],areas[1]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d2);
	        					updateMap.get(key).setAcceptExtraInput(d2);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId2(),d2,lds[1],areas[1]));
	        				}
						}
        			}else{
        				key = lds[1]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d2);
        						saveMap.get(key).setAcceptInput(d2);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId2(), d2, lds[1], areas[1]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d2);
        						saveMap.get(key).setAcceptExtraInput(d2);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId2(), d2, lds[1], areas[1]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId3() != null || d3 != 0){
        			if(userTimesheetForUser.getId3() != null){
        				if (!ids.contains(userTimesheetForUser.getId3())) {
							ids.add(userTimesheetForUser.getId3());
						}
        				key = lds[2]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d3);
	        					updateMap.get(key).setAcceptInput(d3);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId3(),d3,lds[2],areas[2]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d3);
	        					updateMap.get(key).setAcceptExtraInput(d3);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId3(),d3,lds[2],areas[2]));
	        				}
						}
        			}else{
        				key = lds[2]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d3);
        						saveMap.get(key).setAcceptInput(d3);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId3(), d3, lds[2], areas[2]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d3);
        						saveMap.get(key).setAcceptExtraInput(d3);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId3(), d3, lds[2], areas[2]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId4() != null || d4 != 0){
        			if(userTimesheetForUser.getId4() != null){
        				if (!ids.contains(userTimesheetForUser.getId4())) {
							ids.add(userTimesheetForUser.getId4());
						}
        				key = lds[3]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d4);
	        					updateMap.get(key).setAcceptInput(d4);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId4(),d4,lds[3],areas[3]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d4);
	        					updateMap.get(key).setAcceptExtraInput(d4);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId4(),d4,lds[3],areas[3]));
	        				}
						}
        			}else{
        				key = lds[3]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d4);
        						saveMap.get(key).setAcceptInput(d4);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId4(), d4, lds[3], areas[3]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d4);
        						saveMap.get(key).setAcceptExtraInput(d4);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId4(), d4, lds[3], areas[3]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId5() != null || d5 != 0){
        			if(userTimesheetForUser.getId5() != null){
        				if (!ids.contains(userTimesheetForUser.getId5())) {
							ids.add(userTimesheetForUser.getId5());
						}
        				key = lds[4]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d5);
	        					updateMap.get(key).setAcceptInput(d5);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId5(),d5,lds[4],areas[4]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d5);
	        					updateMap.get(key).setAcceptExtraInput(d5);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId5(),d5,lds[4],areas[4]));
	        				}
						}
        			}else{
        				key = lds[4]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d5);
        						saveMap.get(key).setAcceptInput(d5);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId5(), d5, lds[4], areas[4]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d5);
        						saveMap.get(key).setAcceptExtraInput(d5);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId5(), d5, lds[4], areas[4]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId6() != null || d6 != 0){
        			if(userTimesheetForUser.getId6() != null){
        				if (!ids.contains(userTimesheetForUser.getId6())) {
							ids.add(userTimesheetForUser.getId6());
						}
        				key = lds[5]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d6);
	        					updateMap.get(key).setAcceptInput(d6);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId6(),d6,lds[5],areas[5]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d6);
	        					updateMap.get(key).setAcceptExtraInput(d6);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId6(),d6,lds[5],areas[5]));
	        				}
						}
        			}else{
        				key = lds[5]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d6);
        						saveMap.get(key).setAcceptInput(d6);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId6(), d6, lds[5], areas[5]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d6);
        						saveMap.get(key).setAcceptExtraInput(d6);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId6(), d6, lds[5], areas[5]));
        					}
						}
        			}
        		}
        		if(userTimesheetForUser.getId7() != null || d7 != 0){
        			if(userTimesheetForUser.getId7() != null){
        				if (!ids.contains(userTimesheetForUser.getId7())) {
							ids.add(userTimesheetForUser.getId7());
						}
        				key = lds[6]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(updateMap.containsKey(key)){
	        					updateMap.get(key).setRealInput(d7);
	        					updateMap.get(key).setAcceptInput(d7);
	        				}else{
	        					updateMap.put(key, createUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId7(),d7,lds[6],areas[6]));
	        				}
						}else{
							if(updateMap.containsKey(key)){
	        					updateMap.get(key).setExtraInput(d7);
	        					updateMap.get(key).setAcceptExtraInput(d7);
	        				}else{
	        					updateMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser,userName,updator,userTimesheetForUser.getId7(),d7,lds[6],areas[6]));
	        				}
						}
        			}else{
        				key = lds[6]+"_"+userTimesheetForUser.getType()+"_"+StringUtil.nullToLong(userTimesheetForUser.getObjId());
        				if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForUser.getInputType())) {
        					if(saveMap.containsKey(key)){
        						saveMap.get(key).setRealInput(d7);
        						saveMap.get(key).setAcceptInput(d7);
        					}else{
        						saveMap.put(key, createUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId7(), d7, lds[6], areas[6]));
        					}
						}else{
							if(saveMap.containsKey(key)){
        						saveMap.get(key).setExtraInput(d7);
        						saveMap.get(key).setAcceptExtraInput(d7);
        					}else{
        						saveMap.put(key, createExtraUserTimesheetForUser(userTimesheetForUser, userName, updator,userTimesheetForUser.getId7(), d7, lds[6], areas[6]));
        					}
						}
        			}
        		}
    		}
    		//同一id的正常工时与加班工时合并到同一userTimesheet对象中
    		saveMap = getSaveMapUserTimesheets(saveMap,updateMap);
    		updateMap = getUpdateMapUserTimesheets(saveMap,updateMap);
    		
    		updateList.addAll(updateMap.values());
    		saveList.addAll(saveMap.values());
    		//判定是否超过今天
    		Long today = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date())).longValue();
			if((td1 > 0 && today < lds[0]) || (tds1 > 0 && today < lds[0])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td2 > 0 && today < lds[1]) || (tds2 > 0 && today < lds[1])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td3 > 0 && today < lds[2]) || (tds3 > 0 && today < lds[2])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td4 > 0 && today < lds[3]) || (tds4 > 0 && today < lds[3])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td5 > 0 && today < lds[4]) || (tds5 > 0 && today < lds[4])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td6 > 0 && today < lds[5]) || (tds6 > 0 && today < lds[5])){
				return "cpmApp.userTimesheet.save.overDay";
			}else if((td7 > 0 && today < lds[6]) || (tds7 > 0 && today < lds[6])){
				return "cpmApp.userTimesheet.save.overDay";
			}
    		//判定是否假期
    		List<HolidayInfo> list = holidayInfoRepository.findHolidayByCurrDay(StringUtil.longArrayToLongArray(lds));
    		if(list != null && !list.isEmpty()){
    			StringBuffer sb = new StringBuffer();		//正常工时的假期信息
    			StringBuffer sbExtra = new StringBuffer();	//加班工时的假期信息
    			long currDay = 0;
    			for(HolidayInfo holidayInfo : list){
    				currDay = holidayInfo.getCurrDay().longValue();
    				if(currDay == lds[0]){
    					if(td1 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds1 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[1]){
    					if(td2 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds2 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[2]){
    					if(td3 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					} 
    					if(tds3 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[3]){
    					if(td4 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds4 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[4]){
    					if(td5 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds5 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[5]){
    					if(td6 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds6 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[6]){
    					if(td7 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds7 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}
    			}
    			if(sb.length() != 0){
    				return "cpmApp.userTimesheet.save.holiday#" + sb.toString();
    			}
    			if(sbExtra.length() != 0){
    				return "cpmApp.userTimesheet.save.holiday#" + sb.toString();
    			}
    		}
    		//查看当周的记录是否还有没被覆盖的
    		long count = 0;
    		if(ids.isEmpty()){
    			count = userTimesheetRepository.findByUserId(userId,lds[0],lds[6]);
    		}else{
    			count = userTimesheetRepository.findByUserIdAndId(userId,lds[0],lds[6],ids);
    		}
    		if(count > 0){
    			return "cpmApp.userTimesheet.save.dataChanged";
    		}
    		//保存记录
    		userTimesheetDao.saveByUser(saveList,updateList);
    		return "cpmApp.userTimesheet.save.success";
    	}else{
    		return "cpmApp.userTimesheet.save.paramError";
    	}
	}
	/**
	 * 检查用户在某个时间段内是否参与了项目或者合同
	 * @return
	 */
	private String checkParticipate(List<ParticipateInfo> participateInfos, UserTimesheetForUser userTimesheetForUser,
			Long[] lds, Double d1, Double d2, Double d3, Double d4, Double d5, Double d6, Double d7) {
		Boolean c1 = Boolean.FALSE;
		if(d1 == null || d1 == 0){
			c1 = Boolean.TRUE;
		}
		Boolean c2 = Boolean.FALSE;
		if(d2 == null || d2 == 0){
			c2 = Boolean.TRUE;
		}
		Boolean c3 = Boolean.FALSE;
		if(d3 == null || d3 == 0){
			c3 = Boolean.TRUE;
		}
		Boolean c4 = Boolean.FALSE;
		if(d4 == null || d4 == 0){
			c4 = Boolean.TRUE;
		}
		Boolean c5 = Boolean.FALSE;
		if(d5 == null || d5 == 0){
			c5 = Boolean.TRUE;
		}
		Boolean c6 = Boolean.FALSE;
		if(d6 == null || d6 == 0){
			c6 = Boolean.TRUE;
		}
		Boolean c7 = Boolean.FALSE;
		if(d7 == null || d7 == 0){
			c7 = Boolean.TRUE;
		}
		if(c1 && c2 && c3 && c4 && c5 && c6 && c7){
			return null;
		}
		
		if(participateInfos != null && !participateInfos.isEmpty()){
			StringBuffer sb = new StringBuffer();
			for(ParticipateInfo info : participateInfos){
				if(c1 && c2 && c3 && c4 && c5 && c6 && c7){
					return null;
				}
				if(info.getType() == userTimesheetForUser.getType() 
						&& info.getObjId().longValue() == userTimesheetForUser.getObjId()){//同一个类型的同一个项目或合同
					if(!c1){
						if(info.getJoinDay().longValue() <= lds[0] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[0])){
							c1 = Boolean.TRUE;
						}
					}
					if(!c2){
						if(info.getJoinDay().longValue() <= lds[1] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[1])){
							c2 = Boolean.TRUE;
						}
					}
					if(!c3){
						if(info.getJoinDay().longValue() <= lds[2] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[2])){
							c3 = Boolean.TRUE;
						}
					}
					if(!c4){
						if(info.getJoinDay().longValue() <= lds[3] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[3])){
							c4 = Boolean.TRUE;
						}
					}
					if(!c5){
						if(info.getJoinDay().longValue() <= lds[4] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[4])){
							c5 = Boolean.TRUE;
						}
					}
					if(!c6){
						if(info.getJoinDay().longValue() <= lds[5] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[5])){
							c6 = Boolean.TRUE;
						}
					}
					if(!c7){
						if(info.getJoinDay().longValue() <= lds[6] && (info.getLeaveDay() == null || info.getLeaveDay().longValue() >= lds[6])){
							c7 = Boolean.TRUE;
						}
					}
				}
			}
			if(c1 && c2 && c3 && c4 && c5 && c6 && c7){
				return null;
			}
			if(!c1){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[0]);
			}
			if(!c2){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[1]);
			}
			if(!c3){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[2]);
			}
			if(!c4){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[3]);
			}
			if(!c5){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[4]);
			}
			if(!c6){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[5]);
			}
			if(!c7){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(lds[6]);
			}
			return userTimesheetForUser.getObjName() +"[" + sb.toString() + "]";
		}
		return userTimesheetForUser.getObjName();
	}

	private UserTimesheet createUserTimesheetForUser(UserTimesheetForUser userTimesheetForUser,String userName, String updator, Long id, Double realInput,Long workDay,String workArea) {
		UserTimesheet userTimesheet = new UserTimesheet();
		userTimesheet.setAcceptInput(realInput);
		userTimesheet.setCreateTime(ZonedDateTime.now());
		userTimesheet.setCreator(updator);
		userTimesheet.setId(id);
		userTimesheet.setObjId(userTimesheetForUser.getObjId());
		userTimesheet.setObjName(userTimesheetForUser.getObjName());
		userTimesheet.setRealInput(realInput);
		userTimesheet.setStatus(CpmConstants.STATUS_VALID);
		userTimesheet.setType(userTimesheetForUser.getType());
		userTimesheet.setUpdateTime(userTimesheet.getCreateTime());
		userTimesheet.setUpdator(updator);
		userTimesheet.setUserId(userTimesheetForUser.getUserId());
		userTimesheet.setUserName(userName);
		userTimesheet.setWorkArea(workArea);
		userTimesheet.setWorkDay(workDay);
		return userTimesheet;
	}
	
	private UserTimesheet createExtraUserTimesheetForUser(UserTimesheetForUser userTimesheetForUser,String userName, String updator, Long id, Double extraInput,Long workDay,String workArea) {
		UserTimesheet userTimesheet = new UserTimesheet();
		userTimesheet.setExtraInput(extraInput);
		userTimesheet.setCreateTime(ZonedDateTime.now());
		userTimesheet.setCreator(updator);
		userTimesheet.setId(id);
		userTimesheet.setObjId(userTimesheetForUser.getObjId());
		userTimesheet.setObjName(userTimesheetForUser.getObjName());
		userTimesheet.setAcceptExtraInput(extraInput);
		userTimesheet.setStatus(CpmConstants.STATUS_VALID);
		userTimesheet.setType(userTimesheetForUser.getType());
		userTimesheet.setUpdateTime(userTimesheet.getCreateTime());
		userTimesheet.setUpdator(updator);
		userTimesheet.setUserId(userTimesheetForUser.getUserId());
		userTimesheet.setUserName(userName);
		userTimesheet.setWorkArea(workArea);
		userTimesheet.setWorkDay(workDay);
		return userTimesheet;
	}
	
	private Map<String,UserTimesheet> getSaveMapUserTimesheets(Map<String,UserTimesheet> saveMap,Map<String,UserTimesheet> updateMap){
		Set<String> updateKeySet = updateMap.keySet();
		for(String key : updateKeySet){
			if(saveMap.containsKey(key)){
				if(StringUtil.nullToDouble(updateMap.get(key).getRealInput()) == 0){
					updateMap.get(key).setRealInput(saveMap.get(key).getRealInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getAcceptInput()) == 0){
					updateMap.get(key).setAcceptInput(saveMap.get(key).getAcceptInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getExtraInput()) == 0){
					updateMap.get(key).setExtraInput(saveMap.get(key).getExtraInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getRealInput()) == 0){
					updateMap.get(key).setAcceptExtraInput(saveMap.get(key).getAcceptExtraInput());
				}
			}
			saveMap.remove(key);
		}
		return saveMap;
	}
	
	private Map<String,UserTimesheet> getUpdateMapUserTimesheets(Map<String,UserTimesheet> saveMap,Map<String,UserTimesheet> updateMap){
		Set<String> updateKeySet = updateMap.keySet();
		for(String key : updateKeySet){
			if(saveMap.containsKey(key)){
				if(StringUtil.nullToDouble(updateMap.get(key).getRealInput()) == 0){
					updateMap.get(key).setRealInput(saveMap.get(key).getRealInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getAcceptInput()) == 0){
					updateMap.get(key).setAcceptInput(saveMap.get(key).getAcceptInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getExtraInput()) == 0){
					updateMap.get(key).setExtraInput(saveMap.get(key).getExtraInput());
				}
				if(StringUtil.nullToDouble(updateMap.get(key).getRealInput()) == 0){
					updateMap.get(key).setAcceptExtraInput(saveMap.get(key).getAcceptExtraInput());
				}
			}
			saveMap.remove(key);
		}
		return updateMap;
	}
	@Transactional(readOnly = true) 
	public UserTimesheet getUserTimesheetForUser(Long id) {
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		return userTimesheetRepository.findOneByIdAndUserId(id,user.get().getId());
    	}
		return null;
	}

	public UserTimesheet getUserTimesheetForContract(Long id) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
			return userTimesheetDao.getUserTimesheetForContract(id,user,deptInfo);
		}
		return null;
	}

	public UserTimesheet getUserTimesheetForProject(Long id) {
		
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
			return userTimesheetDao.getUserTimesheetForProject(id,user,deptInfo);
		}
		return null;
	}
	/**
	 * 获取合同或者项目中看到的员工日报编辑页面数据
	 */
	public List<UserTimesheetForOther> queryEditByOther(Long id, Integer type, Date workDay) {
		UserTimesheet userTimesheet = null;
		if(type == UserTimesheet.TYPE_CONTRACT){
			userTimesheet = this.getUserTimesheetForContract(id);
		}else{
			userTimesheet = this.getUserTimesheetForProject(id);
		}
		if(userTimesheet != null && workDay == null){
			workDay = DateUtil.parseDate(DateUtil.DATE_YYYYMMDD_PATTERN, userTimesheet.getWorkDay().toString());
		}
		if(workDay == null){
			workDay = new Date();
		}
		//查询现有的所有日报
		List<UserTimesheetForOther> returnList = new ArrayList<UserTimesheetForOther>();
		//查询当天的周一至周日
		String[] ds = DateUtil.getWholeWeekByDate(workDay);
		Long[] lds = new Long[7];
		lds[0] = StringUtil.nullToLong(ds[0]);
		lds[1] = StringUtil.nullToLong(ds[1]);
		lds[2] = StringUtil.nullToLong(ds[2]);
		lds[3] = StringUtil.nullToLong(ds[3]);
		lds[4] = StringUtil.nullToLong(ds[4]);
		lds[5] = StringUtil.nullToLong(ds[5]);
		lds[6] = StringUtil.nullToLong(ds[6]);
		
		//添加默认日期
		addDays(returnList,ds,lds);
		if(userTimesheet == null){//没有权限，直接返回日期列表
			return returnList;
		}
		//日期重新整理
		Long objId = userTimesheet.getObjId();
		UserTimesheetForOther dayInfo = returnList.get(0);
		dayInfo.setObjId(objId);
		returnList.clear();
		returnList.add(dayInfo);
		
		//查询现有的所有记录
		List<UserTimesheet> list = userTimesheetDao.getByWorkDayAndObjType(lds[0],lds[6],objId,type);
		
		//转换为MAP
		Map<Long,Map<Long,UserTimesheet>> map = trans2OtherMap(list);
		Map<Long,UserTimesheet> childs = null;
		for(Long userId : map.keySet()){
			childs = map.get(userId);
			if(childs != null){
				UserTimesheetForOther timesheet = getUserTimesheetForOther(userId,childs,lds,objId,type);
				UserTimesheetForOther timesheetExtra = getExtraUserTimesheetForOther(userId,childs,lds,objId,type);//加班工时
				returnList.add(timesheet);
				returnList.add(timesheetExtra);
			}
		}
		return returnList;
	}
	/**
	 * 添加日期
	 */
	private void addDays(List<UserTimesheetForOther> returnList, String[] ds, Long[] lds) {
		Map<Long,Long> holidayMaps = holidayInfoService.findHolidayByCurrDay(StringUtil.longArrayToLongArray(lds));
		String data1 = ds[0];
		String data2 = ds[1];
		String data3 = ds[2];
		String data4 = ds[3];
		String data5 = ds[4];
		String data6 = ds[5];
		String data7 = ds[6];
		if(!holidayMaps.containsKey(lds[0])){
			data1 += "*";
		}
		if(!holidayMaps.containsKey(lds[1])){
			data2 += "*";
		}
		if(!holidayMaps.containsKey(lds[2])){
			data3 += "*";
		}
		if(!holidayMaps.containsKey(lds[3])){
			data4 += "*";
		}
		if(!holidayMaps.containsKey(lds[4])){
			data5 += "*";
		}
		if(!holidayMaps.containsKey(lds[5])){
			data6 += "*";
		}
		if(!holidayMaps.containsKey(lds[6])){
			data7 += "*";
		}
		//添加第一行日期
		returnList.add(new UserTimesheetForOther(UserTimesheet.TYPE_DAY,data1,data2,data3,data4,data5,data6,data7));
	}

	private UserTimesheetForOther getUserTimesheetForOther(Long userId, Map<Long, UserTimesheet> childs, Long[] lds, Long objId, Integer type) {
		UserTimesheetForOther userTimesheetForOther = new UserTimesheetForOther();
		userTimesheetForOther.setObjId(objId);
		userTimesheetForOther.setUserId(userId);
		userTimesheetForOther.setUserName(null);
		userTimesheetForOther.setType(type);
		userTimesheetForOther.setInputType(UserTimesheet.TYPE_INPUT_NORMAL);
		
		UserTimesheet tmp = childs.get(lds[0]);
		if(tmp != null){
			userTimesheetForOther.setData1(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck1(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId1(tmp.getId());
		}else{
			userTimesheetForOther.setData1(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck1(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[1]);
		if(tmp != null){
			userTimesheetForOther.setData2(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck2(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId2(tmp.getId());
		}else{
			userTimesheetForOther.setData2(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck2(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[2]);
		if(tmp != null){
			userTimesheetForOther.setData3(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck3(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId3(tmp.getId());
		}else{
			userTimesheetForOther.setData3(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck3(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[3]);
		if(tmp != null){
			userTimesheetForOther.setData4(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck4(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId4(tmp.getId());
		}else{
			userTimesheetForOther.setData4(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck4(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[4]);
		if(tmp != null){
			userTimesheetForOther.setData5(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck5(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId5(tmp.getId());
		}else{
			userTimesheetForOther.setData5(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck5(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[5]);
		if(tmp != null){
			userTimesheetForOther.setData6(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck6(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId6(tmp.getId());
		}else{
			userTimesheetForOther.setData6(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck6(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[6]);
		if(tmp != null){
			userTimesheetForOther.setData7(StringUtil.nullToDouble(tmp.getRealInput()).toString());
			userTimesheetForOther.setCheck7(StringUtil.nullToDouble(tmp.getAcceptInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId7(tmp.getId());
		}else{
			userTimesheetForOther.setData7(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck7(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		
		return userTimesheetForOther;
	}

	private Map<Long, Map<Long, UserTimesheet>> trans2OtherMap(List<UserTimesheet> list) {
		Map<Long, Map<Long, UserTimesheet>> returnMap = new HashMap<Long, Map<Long, UserTimesheet>>();
		if(list != null){
			for(UserTimesheet userTimesheet : list){
				if(!returnMap.containsKey(userTimesheet.getUserId())){
					returnMap.put(userTimesheet.getUserId(), new HashMap<Long, UserTimesheet>());
				}
				returnMap.get(userTimesheet.getUserId()).put(userTimesheet.getWorkDay(), userTimesheet);
			}
		}
		return returnMap;
	}
	
	private UserTimesheetForOther getExtraUserTimesheetForOther(Long userId, Map<Long, UserTimesheet> childs, Long[] lds, Long objId, Integer type) {
		UserTimesheetForOther userTimesheetForOther = new UserTimesheetForOther();
		userTimesheetForOther.setObjId(objId);
		userTimesheetForOther.setUserId(userId);
		userTimesheetForOther.setUserName(null);
		userTimesheetForOther.setType(type);
		userTimesheetForOther.setInputType(UserTimesheet.TYPE_INPUT_EXTRA);
		UserTimesheet tmp = childs.get(lds[0]);
		if(tmp != null){
			userTimesheetForOther.setData1(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck1(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId1(tmp.getId());
		}else{
			userTimesheetForOther.setData1(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck1(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[1]);
		if(tmp != null){
			userTimesheetForOther.setData2(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck2(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId2(tmp.getId());
		}else{
			userTimesheetForOther.setData2(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck2(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[2]);
		if(tmp != null){
			userTimesheetForOther.setData3(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck3(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId3(tmp.getId());
		}else{
			userTimesheetForOther.setData3(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck3(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[3]);
		if(tmp != null){
			userTimesheetForOther.setData4(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck4(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId4(tmp.getId());
		}else{
			userTimesheetForOther.setData4(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck4(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[4]);
		if(tmp != null){
			userTimesheetForOther.setData5(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck5(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId5(tmp.getId());
		}else{
			userTimesheetForOther.setData5(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck5(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[5]);
		if(tmp != null){
			userTimesheetForOther.setData6(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck6(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId6(tmp.getId());
		}else{
			userTimesheetForOther.setData6(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck6(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		tmp = childs.get(lds[6]);
		if(tmp != null){
			userTimesheetForOther.setData7(StringUtil.nullToDouble(tmp.getExtraInput()).toString());
			userTimesheetForOther.setCheck7(StringUtil.nullToDouble(tmp.getAcceptExtraInput()).toString());
			userTimesheetForOther.setUserName(tmp.getUserName());
			userTimesheetForOther.setId7(tmp.getId());
		}else{
			userTimesheetForOther.setData7(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_INPUT);
			userTimesheetForOther.setCheck7(CpmConstants.DFAULT_USER_TIMESHEET_OTHER_CHECK);
		}
		
		return userTimesheetForOther;
	}

	/**
	 * 合同或者项目中更新用户的日报
	 * @return
	 */
	public String updateEditByOther(List<UserTimesheetForOther> userTimesheetForOthers, int type) {
		if (userTimesheetForOthers == null || userTimesheetForOthers.isEmpty() || userTimesheetForOthers.size() < 2) {
			return "cpmApp.contractTimesheet.save.paramError";
		}
		//查看当前用户
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		User currUser = user.get();
    		String updator = currUser.getLogin();
    		//日期
    		UserTimesheetForOther dayTimesheet = userTimesheetForOthers.get(0);
    		Long[] lds = new Long[7];
    		lds[0] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData1()));
    		lds[1] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData2()));
    		lds[2] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData3()));
    		lds[3] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData4()));
    		lds[4] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData5()));
    		lds[5] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData6()));
    		lds[6] = StringUtil.nullToLong(removeAsteriskFromDay(dayTimesheet.getData7()));
    		if(dayTimesheet.getType() != UserTimesheet.TYPE_DAY){
    			return "cpmApp.contractTimesheet.save.paramError";
    		}
    		Long objId = dayTimesheet.getObjId();
    		Integer objType = userTimesheetForOthers.get(1).getType();
    		if(lds[0] == 0 || lds[1] == 0 || lds[2] == 0 || lds[3] == 0 || lds[4] == 0 || lds[5] == 0 || lds[6] == 0 || objId == null || objType == null){
    			return "cpmApp.contractTimesheet.save.paramError";
    		}
    		if(objType != type){
    			return "cpmApp.contractTimesheet.save.paramError";
    		}
    		//查看是否有权限
    		if(objType == UserTimesheet.TYPE_CONTRACT){//合同
    			ContractInfoVo vo = contractInfoService.getUserContractInfo(objId);
    			if(vo == null){
   	    			return "cpmApp.contractTimesheet.save.noPermit";
    			}
    		}else{//项目
    			ProjectInfoVo vo = projectInfoService.getUserProjectInfo(objId);
    			if(vo == null){
    				return "cpmApp.contractTimesheet.save.noPermit";
    			}
    		}
    		//判定日报
    		//每天的总工时
    		Double td1 = 0d;
    		Double td2 = 0d;
    		Double td3 = 0d;
    		Double td4 = 0d;
    		Double td5 = 0d;
    		Double td6 = 0d;
    		Double td7 = 0d;
    		//实际投入工时
    		Double d1 = 0d;
    		Double d2 = 0d;
    		Double d3 = 0d;
    		Double d4 = 0d;
    		Double d5 = 0d;
    		Double d6 = 0d;
    		Double d7 = 0d;
    		//认可工时
    		Double cd1 = 0d;
    		Double cd2 = 0d;
    		Double cd3 = 0d;
    		Double cd4 = 0d;
    		Double cd5 = 0d;
    		Double cd6 = 0d;
    		Double cd7 = 0d;
    		
    		//每天的总加班工时
    		Double tds1 = 0d;
    		Double tds2 = 0d;
    		Double tds3 = 0d;
    		Double tds4 = 0d;
    		Double tds5 = 0d;
    		Double tds6 = 0d;
    		Double tds7 = 0d;
    		//实际投入加班工时
    		Double ds1 = 0d;
    		Double ds2 = 0d;
    		Double ds3 = 0d;
    		Double ds4 = 0d;
    		Double ds5 = 0d;
    		Double ds6 = 0d;
    		Double ds7 = 0d;
    		//认可加班工时
    		Double cds1 = 0d;
    		Double cds2 = 0d;
    		Double cds3 = 0d;
    		Double cds4 = 0d;
    		Double cds5 = 0d;
    		Double cds6 = 0d;
    		Double cds7 = 0d;
    		
    		List<UserTimesheet> updateList = new ArrayList<UserTimesheet>();
    		List<UserTimesheet> updateExtraList = new ArrayList<UserTimesheet>();
    		for(int i = 1; i < userTimesheetForOthers.size(); i++){
    			UserTimesheetForOther userTimesheetForOther = userTimesheetForOthers.get(i);//一条记录是一个用户在某个项目或合同一周的记录
    			if(userTimesheetForOther.getType() != type){
        			return "cpmApp.contractTimesheet.save.paramError";
        		}
    			if (UserTimesheet.TYPE_INPUT_NORMAL.equals(userTimesheetForOther.getInputType())) {//正常工时
					//认可投入数据
					cd1 = StringUtil.nullToDouble(userTimesheetForOther.getCheck1());
					cd2 = StringUtil.nullToDouble(userTimesheetForOther.getCheck2());
					cd3 = StringUtil.nullToDouble(userTimesheetForOther.getCheck3());
					cd4 = StringUtil.nullToDouble(userTimesheetForOther.getCheck4());
					cd5 = StringUtil.nullToDouble(userTimesheetForOther.getCheck5());
					cd6 = StringUtil.nullToDouble(userTimesheetForOther.getCheck6());
					cd7 = StringUtil.nullToDouble(userTimesheetForOther.getCheck7());
					if (cd1 < 0 || cd1 > 8 || cd2 < 0 || cd2 > 8 || cd3 < 0 || cd3 > 8 || cd4 < 0 || cd4 > 8 || cd5 < 0
							|| cd5 > 8 || cd6 < 0 || cd6 > 8 || cd7 < 0 || cd7 > 8) {
						return "cpmApp.contractTimesheet.save.dataError";
					}
					td1 += cd1;
					td2 += cd2;
					td3 += cd3;
					td4 += cd4;
					td5 += cd5;
					td6 += cd6;
					td7 += cd7;
					//实际投入数据
					d1 = StringUtil.nullToDouble(userTimesheetForOther.getData1());
					d2 = StringUtil.nullToDouble(userTimesheetForOther.getData2());
					d3 = StringUtil.nullToDouble(userTimesheetForOther.getData3());
					d4 = StringUtil.nullToDouble(userTimesheetForOther.getData4());
					d5 = StringUtil.nullToDouble(userTimesheetForOther.getData5());
					d6 = StringUtil.nullToDouble(userTimesheetForOther.getData6());
					d7 = StringUtil.nullToDouble(userTimesheetForOther.getData7());
					//为空或大于实际投入
					if ((d1 == 0 && cd1 > 0) || (d2 == 0 && cd2 > 0) || (d3 == 0 && cd3 > 0) || (d4 == 0 && cd4 > 0)
							|| (d5 == 0 && cd5 > 0) || (d6 == 0 && cd6 > 0) || (d7 == 0 && cd7 > 0)) {
						return "cpmApp.contractTimesheet.save.inputZeroCheck";
					}
					if (cd1 > d1 || cd2 > d2 || cd3 > d3 || cd4 > d4 || cd5 > d5 || cd6 > d6 || cd7 > d7) {
						return "cpmApp.contractTimesheet.save.overInput";
					}
					//用户为空
					if (userTimesheetForOther.getUserId() == null) {
						return "cpmApp.contractTimesheet.save.paramError";
					}
					if (userTimesheetForOther.getId1() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId1(), cd1, objType));
					}
					if (userTimesheetForOther.getId2() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId2(), cd2, objType));
					}
					if (userTimesheetForOther.getId3() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId3(), cd3, objType));
					}
					if (userTimesheetForOther.getId4() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId4(), cd4, objType));
					}
					if (userTimesheetForOther.getId5() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId5(), cd5, objType));
					}
					if (userTimesheetForOther.getId6() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId6(), cd6, objType));
					}
					if (userTimesheetForOther.getId7() != null) {
						updateList.add(createUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId7(), cd7, objType));
					} 
				}
    			
    			if (UserTimesheet.TYPE_INPUT_EXTRA.equals(userTimesheetForOther.getInputType())) {//加班工时
					//认可投入数据
					cds1 = StringUtil.nullToDouble(userTimesheetForOther.getCheck1());
					cds2 = StringUtil.nullToDouble(userTimesheetForOther.getCheck2());
					cds3 = StringUtil.nullToDouble(userTimesheetForOther.getCheck3());
					cds4 = StringUtil.nullToDouble(userTimesheetForOther.getCheck4());
					cds5 = StringUtil.nullToDouble(userTimesheetForOther.getCheck5());
					cds6 = StringUtil.nullToDouble(userTimesheetForOther.getCheck6());
					cds7 = StringUtil.nullToDouble(userTimesheetForOther.getCheck7());
					if ((cds1 < 2 && cds1 != 0) || cds1 > 8 || (cds2 < 2 && cds2 != 0) || cds2 > 8 
							|| (cds3 < 2 && cds3 != 0) || cds3 > 8 || (cds4 < 2 && cds4 != 0)|| cds4 > 8 
							|| (cds5 < 2 && cds5 != 0) || cds5 > 8 || (cds6 < 2 && cds6 != 0) || cds6 > 8 
							|| (cds7 < 2 && cds7 != 0) || cds7 > 8) {
						return "cpmApp.contractTimesheet.save.dataExtraError";
					}
					tds1 += cds1;
					tds2 += cds2;
					tds3 += cds3;
					tds4 += cds4;
					tds5 += cds5;
					tds6 += cds6;
					tds7 += cds7;
					//实际投入数据
					ds1 = StringUtil.nullToDouble(userTimesheetForOther.getData1());
					ds2 = StringUtil.nullToDouble(userTimesheetForOther.getData2());
					ds3 = StringUtil.nullToDouble(userTimesheetForOther.getData3());
					ds4 = StringUtil.nullToDouble(userTimesheetForOther.getData4());
					ds5 = StringUtil.nullToDouble(userTimesheetForOther.getData5());
					ds6 = StringUtil.nullToDouble(userTimesheetForOther.getData6());
					ds7 = StringUtil.nullToDouble(userTimesheetForOther.getData7());
					//为空或大于实际投入
					if ((ds1 == 0 && cds1 > 0) || (ds2 == 0 && cds2 > 0) || (ds3 == 0 && cds3 > 0) || (ds4 == 0 && cds4 > 0)
							|| (ds5 == 0 && cds5 > 0) || (ds6 == 0 && cds6 > 0) || (ds7 == 0 && cds7 > 0)) {
						return "cpmApp.contractTimesheet.save.inputZeroCheck";
					}
					if (cds1 > ds1 || cds2 > ds2 || cds3 > ds3 || cds4 > ds4 || cds5 > ds5 || cds6 > ds6 || cds7 > ds7) {
						return "cpmApp.contractTimesheet.save.overExtraInput";
					}
					//用户为空
					if (userTimesheetForOther.getUserId() == null) {
						return "cpmApp.contractTimesheet.save.paramError";
					}
					if (userTimesheetForOther.getId1() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId1(), cds1, objType));
					}
					if (userTimesheetForOther.getId2() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId2(), cds2, objType));
					}
					if (userTimesheetForOther.getId3() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId3(), cds3, objType));
					}
					if (userTimesheetForOther.getId4() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId4(), cds4, objType));
					}
					if (userTimesheetForOther.getId5() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId5(), cds5, objType));
					}
					if (userTimesheetForOther.getId6() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId6(), cds6, objType));
					}
					if (userTimesheetForOther.getId7() != null) {
						updateExtraList.add(createExtraUserTimesheetForOther(userTimesheetForOther, updator,
								userTimesheetForOther.getId7(), cds7, objType));
					} 
				}
    		}
    		//将加班工时合并到一个bean中
    		getUpdateUserTimesheets(updateList,updateExtraList);
    		//判定是否超过今天
    		Long today = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date())).longValue();
			if((td1 > 0 && today < lds[0]) || (tds1 > 0 && today < lds[0])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td2 > 0 && today < lds[1]) || (tds2 > 0 && today < lds[1])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td3 > 0 && today < lds[2]) || (tds3 > 0 && today < lds[2])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td4 > 0 && today < lds[3]) || (tds4 > 0 && today < lds[3])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td5 > 0 && today < lds[4]) || (tds5 > 0 && today < lds[4])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td6 > 0 && today < lds[5]) || (tds6 > 0 && today < lds[5])){
				return "cpmApp.contractTimesheet.save.overDay";
			}else if((td7 > 0 && today < lds[6]) || (tds7 > 0 && today < lds[6])){
				return "cpmApp.contractTimesheet.save.overDay";
			}
    		//判定是否假期
    		List<HolidayInfo> list = holidayInfoRepository.findHolidayByCurrDay(StringUtil.longArrayToLongArray(lds));
    		if(list != null && !list.isEmpty()){
    			StringBuffer sb = new StringBuffer();
    			StringBuffer sbExtra = new StringBuffer();
    			long currDay = 0;
    			for(HolidayInfo holidayInfo : list){
    				currDay = holidayInfo.getCurrDay().longValue();
    				if(currDay == lds[0]){
    					if(td1 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds1 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[1]){
    					if(td2 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds2 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[2]){
    					if(td3 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					} 
    					if(tds3 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[3]){
    					if(td4 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds4 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[4]){
    					if(td5 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds5 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[5]){
    					if(td6 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds6 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}else if(currDay == lds[6]){
    					if(td7 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    					if(tds7 > 0){
    						if(sbExtra.length() != 0){
    							sbExtra.append(",");
    						}
    						sbExtra.append(currDay);
    					}
    				}
    			}
    			if(sb.length() != 0){
    				return "cpmApp.contractTimesheet.save.holiday#" + sb.toString();
    			}
    			if(sbExtra.length() != 0){
    				return "cpmApp.contractTimesheet.save.holiday#" + sb.toString();
    			}
    		}
    		//保存记录
    		userTimesheetDao.updateAcceptInput(updateList);
    		
    		return "cpmApp.contractTimesheet.save.success";
    	}else{
    		return "cpmApp.contractTimesheet.save.paramError";
    	}
	}
	/**
	 * 生成需要更新认可投入的model
	 * @return
	 */
	private UserTimesheet createUserTimesheetForOther(UserTimesheetForOther userTimesheetForOther,String updator, Long id, Double acceptInput, Integer type) {
		UserTimesheet userTimesheet = new UserTimesheet();
		userTimesheet.setId(id);
		userTimesheet.setObjId(userTimesheetForOther.getObjId());
		userTimesheet.setType(type);
		userTimesheet.setUpdateTime(ZonedDateTime.now());
		userTimesheet.setUpdator(updator);
		userTimesheet.setUserId(userTimesheetForOther.getUserId());
		userTimesheet.setAcceptInput(acceptInput);
		return userTimesheet;
	}
	/**
	 * 生成需要更新认可投入的加班model
	 * @return
	 */
	private UserTimesheet createExtraUserTimesheetForOther(UserTimesheetForOther userTimesheetForOther,String updator, Long id, Double acceptExtraInput, Integer type) {
		UserTimesheet userTimesheet = new UserTimesheet();
		userTimesheet.setId(id);
		userTimesheet.setObjId(userTimesheetForOther.getObjId());
		userTimesheet.setType(type);
		userTimesheet.setUpdateTime(ZonedDateTime.now());
		userTimesheet.setUpdator(updator);
		userTimesheet.setUserId(userTimesheetForOther.getUserId());
		userTimesheet.setAcceptExtraInput(acceptExtraInput);
		return userTimesheet;
	}
	/**
	 * 页面上的日期带有星号，需要去除
	 * @param pageDay
	 * @return
	 */
	private String removeAsteriskFromDay(String pageDay) {
		if(pageDay != null){
			pageDay = pageDay.replaceAll("[*]", "");
			if(pageDay.length() != 8){
				return null;
			}
			return pageDay;
		}
		return null;
	}

	public List<UserTimesheetForHardWorkingVo> findByWorkDay(Long fromDay, Long endDay) {
		List<UserTimesheetForHardWorkingVo> list = userTimesheetDao.findByWorkDay(fromDay,endDay);
		return list;
	}
	private void getUpdateUserTimesheets(List<UserTimesheet> updateList,List<UserTimesheet> updateExtraList){
		for(UserTimesheet extra : updateExtraList){
			for(UserTimesheet update : updateList){
				if(extra.getId().equals(update.getId())){
					update.setAcceptExtraInput(extra.getAcceptExtraInput());
				}
			}
		}
	}
}
