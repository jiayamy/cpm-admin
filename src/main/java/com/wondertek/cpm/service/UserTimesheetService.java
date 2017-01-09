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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondertek.cpm.CpmConstants;
import com.wondertek.cpm.config.DateUtil;
import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.HolidayInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.repository.ContractUserDao;
import com.wondertek.cpm.repository.HolidayInfoRepository;
import com.wondertek.cpm.repository.ProjectUserDao;
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

//    @Inject
//    private UserTimesheetSearchRepository userTimesheetSearchRepository;
    
    @Autowired
    private UserTimesheetDao userTimesheetDao;
    @Inject
    private UserRepository userRepository;
    @Inject
    private HolidayInfoRepository holidayInfoRepository;
    
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
//        userTimesheetSearchRepository.save(result);
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
        	userTimesheetRepository.save(userTimesheet);
        }
//        userTimesheetSearchRepository.delete(id);
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
//        result = userTimesheetSearchRepository.search(queryStringQuery(query), pageable);
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
    /**
     * 获取编辑列表中的数据
     * @param workDayDate
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
		UserTimesheetForUser areaTimesheet = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_AREA,null,null,null);
		//添加默认的公共成本
		UserTimesheetForUser publicTimesheet = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_PUBLIC,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT);
		//添加项目和合同
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Long userId = user.get().getId();
    		publicTimesheet.setUserId(userId);
    		//查询现有的所有记录
    		List<UserTimesheet> list = userTimesheetDao.getByWorkDayAndUser(lds[0],lds[6],user.get().getId());
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
    			map.remove(key);
    		}
    		//现有的所有项目和合同
    		for(LongValue longValue : ids){
    			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,longValue.getType(),longValue.getKey(),longValue.getVal(),CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT);
    			key = getTransMapKey(userId,longValue.getType(),longValue.getKey());
    			childs = map.get(key);
        		if(childs != null){
        			setUserTimesheetForUser(areaTimesheet,timesheet,lds,childs);
        			map.remove(key);
        		}
        		returnList.add(timesheet);
    		}
    		//以前参与了项目或合同，现在在参与者中没找到
    		if(!map.isEmpty()){
    			getOtherTimesheetForUser(returnList,areaTimesheet,map,userId,lds);
    		}
    	}
    	//添加地区
    	returnList.add(1, areaTimesheet);
    	returnList.add(2, publicTimesheet);
		return returnList;
	}
	private void getOtherTimesheetForUser(List<UserTimesheetForUser> returnList, UserTimesheetForUser areaTimesheet, Map<String, Map<Long, UserTimesheet>> map, Long userId, Long[] lds) {
		UserTimesheet tmpTimesheet = null;
		Map<Long, UserTimesheet> childs = null;
		for(String mapKey : map.keySet()){
			childs = map.get(mapKey);
			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,null,null,null,CpmConstants.DFAULT_USER_TIMESHEET_USER_INPUT);
			for(Long workDay : childs.keySet()){
				tmpTimesheet = childs.get(workDay);
				timesheet.setType(tmpTimesheet.getType());
				timesheet.setObjId(tmpTimesheet.getObjId());
				timesheet.setObjName(tmpTimesheet.getObjName());
				if(lds[0] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData1(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData1() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData1(tmpTimesheet.getWorkArea());
					}
					timesheet.setId1(tmpTimesheet.getId());
				}else if(lds[1] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData2(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData2() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData2(tmpTimesheet.getWorkArea());
					}
					timesheet.setId2(tmpTimesheet.getId());
				}else if(lds[2] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData3(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData3() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData3(tmpTimesheet.getWorkArea());
					}
					timesheet.setId3(tmpTimesheet.getId());
				}else if(lds[3] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData4(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData4() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData4(tmpTimesheet.getWorkArea());
					}
					timesheet.setId4(tmpTimesheet.getId());
				}else if(lds[4] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData5(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData5() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData5(tmpTimesheet.getWorkArea());
					}
					timesheet.setId5(tmpTimesheet.getId());
				}else if(lds[5] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData6(tmpTimesheet.getRealInput().toString());
					}
					if(areaTimesheet.getData6() == null && !StringUtil.isNullStr(tmpTimesheet.getWorkArea())){
						areaTimesheet.setData6(tmpTimesheet.getWorkArea());
					}
					timesheet.setId6(tmpTimesheet.getId());
				}else if(lds[6] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData7(tmpTimesheet.getRealInput().toString());
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

	private UserTimesheetForUser getDefaultUserTimesheetForUser(Long userId, Integer type, Long objId, String objName, String defaultData) {
		UserTimesheetForUser userTimesheetForUser = new UserTimesheetForUser();
		userTimesheetForUser.setUserId(userId);
		userTimesheetForUser.setType(type);
		userTimesheetForUser.setObjId(objId);
		userTimesheetForUser.setObjName(objName);
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
    		//判定日报
    		Double td1 = 0d;
    		Double td2 = 0d;
    		Double td3 = 0d;
    		Double td4 = 0d;
    		Double td5 = 0d;
    		Double td6 = 0d;
    		Double td7 = 0d;
    		
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
    		for(int i = 2; i < userTimesheetForUsers.size(); i++){
    			//一条记录就是一个项目或者合同或者公共成本
    			UserTimesheetForUser userTimesheetForUser = userTimesheetForUsers.get(i);
    			if(userTimesheetForUser.getUserId() == null || userTimesheetForUser.getUserId() != userId.longValue()){
    				return "cpmApp.userTimesheet.save.noPermit";
    			}
    			d1 = StringUtil.nullToDouble(userTimesheetForUser.getData1());
    			d2 = StringUtil.nullToDouble(userTimesheetForUser.getData2());
    			d3 = StringUtil.nullToDouble(userTimesheetForUser.getData3());
    			d4 = StringUtil.nullToDouble(userTimesheetForUser.getData4());
    			d5 = StringUtil.nullToDouble(userTimesheetForUser.getData5());
    			d6 = StringUtil.nullToDouble(userTimesheetForUser.getData6());
    			d7 = StringUtil.nullToDouble(userTimesheetForUser.getData7());
    			
    			if(d1 < 0 || d1 > 8 || d2 < 0 || d2 > 8 || d3 < 0 || d3 > 8 || d4 < 0 || d4 > 8
       				 || d5 < 0 || d5 > 8 || d6 < 0 || d6 > 8 || d7 < 0 || d7 > 8){
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
        		if(userTimesheetForUser.getId1() != null || d1 != 0){
        			if(userTimesheetForUser.getId1() != null){
        				ids.add(userTimesheetForUser.getId1());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId1(),d1,lds[0],areas[0]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId1(),d1,lds[0],areas[0]));
        			}
        		}
        		if(userTimesheetForUser.getId2() != null || d2 != 0){
        			if(userTimesheetForUser.getId2() != null){
        				ids.add(userTimesheetForUser.getId2());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId2(),d2,lds[1],areas[1]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId2(),d2,lds[1],areas[1]));
        			}
        		}
        		if(userTimesheetForUser.getId3() != null || d3 != 0){
        			if(userTimesheetForUser.getId3() != null){
        				ids.add(userTimesheetForUser.getId3());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId3(),d3,lds[2],areas[2]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId3(),d3,lds[2],areas[2]));
        			}
        		}
        		if(userTimesheetForUser.getId4() != null || d4 != 0){
        			if(userTimesheetForUser.getId4() != null){
        				ids.add(userTimesheetForUser.getId4());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId4(),d4,lds[3],areas[3]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId4(),d4,lds[3],areas[3]));
        			}
        		}
        		if(userTimesheetForUser.getId5() != null || d5 != 0){
        			if(userTimesheetForUser.getId5() != null){
        				ids.add(userTimesheetForUser.getId5());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId5(),d5,lds[4],areas[4]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId5(),d5,lds[4],areas[4]));
        			}
        		}
        		if(userTimesheetForUser.getId6() != null || d6 != 0){
        			if(userTimesheetForUser.getId6() != null){
        				ids.add(userTimesheetForUser.getId6());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId6(),d6,lds[5],areas[5]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId6(),d6,lds[5],areas[5]));
        			}
        		}
        		if(userTimesheetForUser.getId7() != null || d7 != 0){
        			if(userTimesheetForUser.getId7() != null){
        				ids.add(userTimesheetForUser.getId7());
        				updateList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId7(),d7,lds[6],areas[6]));
        			}else{
        				saveList.add(createUserTimesheet(userTimesheetForUser,userName,updator,userTimesheetForUser.getId7(),d7,lds[6],areas[6]));
        			}
        		}
    		}
    		//判定是否超过今天
    		Long today = StringUtil.nullToLong(DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, new Date())).longValue();
			if(td1 > 0 && today < lds[0]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td2 > 0 && today < lds[1]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td3 > 0 && today < lds[2]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td4 > 0 && today < lds[3]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td5 > 0 && today < lds[4]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td6 > 0 && today < lds[5]){
				return "cpmApp.userTimesheet.save.overDay";
			}else if(td7 > 0 && today < lds[6]){
				return "cpmApp.userTimesheet.save.overDay";
			}
    		//判定是否假期
    		List<HolidayInfo> list = holidayInfoRepository.findHolidayByCurrDay(StringUtil.longArrayToLongArray(lds));
    		if(list != null && !list.isEmpty()){
    			StringBuffer sb = new StringBuffer();
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
    				}else if(currDay == lds[1]){
    					if(td2 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    				}else if(currDay == lds[2]){
    					if(td3 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}    					
    				}else if(currDay == lds[3]){
    					if(td4 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    				}else if(currDay == lds[4]){
    					if(td5 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    				}else if(currDay == lds[5]){
    					if(td6 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    				}else if(currDay == lds[6]){
    					if(td7 > 0){
    						if(sb.length() != 0){
    							sb.append(",");
    						}
    						sb.append(currDay);
    					}
    				}
    			}
    			if(sb.length() != 0){
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

	private UserTimesheet createUserTimesheet(UserTimesheetForUser userTimesheetForUser,String userName, String updator, Long id, Double realInput,Long workDay,String workArea) {
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
}
