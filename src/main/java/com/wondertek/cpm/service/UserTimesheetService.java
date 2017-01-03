package com.wondertek.cpm.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

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
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.UserTimesheet;
import com.wondertek.cpm.domain.vo.LongValue;
import com.wondertek.cpm.domain.vo.UserTimesheetForUser;
import com.wondertek.cpm.repository.ContractUserDao;
import com.wondertek.cpm.repository.ProjectUserDao;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetDao;
import com.wondertek.cpm.repository.UserTimesheetRepository;
import com.wondertek.cpm.repository.search.UserTimesheetSearchRepository;
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

    @Inject
    private UserTimesheetSearchRepository userTimesheetSearchRepository;
    
    @Autowired
    private UserTimesheetDao userTimesheetDao;
    @Inject
    private UserRepository userRepository;
    
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
        userTimesheetSearchRepository.save(result);
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
        	userTimesheetRepository.save(userTimesheet);
        }
        userTimesheetSearchRepository.delete(id);
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
        Page<UserTimesheet> result = userTimesheetSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
    
    public Page<UserTimesheet> getUserPage(UserTimesheet userTimesheet, Pageable pageable){
    	Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Page<UserTimesheet> page = userTimesheetDao.getUserPage(userTimesheet,pageable,user);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<UserTimesheet>(), pageable, 0);
    	}
    }
    
	public List<UserTimesheetForUser> queryEditByUser(Date workDayDate) {
		//查询现有的所有日报
		List<UserTimesheetForUser> returnList = new ArrayList<UserTimesheetForUser>();
		//查询当天的周一至周日
		String[] ds = DateUtil.getWholeWeekByDate(workDayDate);
		//添加第一行日期
		returnList.add(new UserTimesheetForUser(ds[0],ds[1],ds[2],ds[3],ds[4],ds[5],ds[6]));
		Long[] lds = new Long[7];
		lds[0] = StringUtil.nullToLong(ds[0]);
		lds[1] = StringUtil.nullToLong(ds[1]);
		lds[2] = StringUtil.nullToLong(ds[2]);
		lds[3] = StringUtil.nullToLong(ds[3]);
		lds[4] = StringUtil.nullToLong(ds[4]);
		lds[5] = StringUtil.nullToLong(ds[5]);
		lds[6] = StringUtil.nullToLong(ds[6]);
		
		//添加第二行地区
		String area1 = null;
		String area2 = null;
		String area3 = null;
		String area4 = null;
		String area5 = null;
		String area6 = null;
		String area7 = null;
		//添加默认的公共成本
		UserTimesheetForUser defaultTimesheet = getDefaultUserTimesheetForUser(null,UserTimesheet.TYPE_PUBLIC,null,null);
		//添加项目和合同
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
    	if(user.isPresent()){
    		Long userId = user.get().getId();
    		defaultTimesheet.setUserId(userId);
    		//查询现有的所有记录
    		List<UserTimesheet> list = userTimesheetDao.getByWorkDayAndUser(lds[0],lds[6],user.get().getId());
    		//转换为MAP
    		Map<String,Map<Long,UserTimesheet>> map = trans2Map(list);
    		//获取用户现有的所有项目和合同
    		List<LongValue> ids = contractUserDao.getAllByUser(userId);
    		ids.addAll(projectUserDao.getAllByUser(userId));
    		
    		//添加默认的公共成本
    		String key = getTransMapKey(userId,UserTimesheet.TYPE_PUBLIC,null);
    		Map<Long, UserTimesheet> childs = map.get(key);
    		if(childs != null){
    			setUserTimesheetForUser(defaultTimesheet,lds,childs);
    			map.remove(key);
    		}
    		//现有的所有项目和合同
    		for(LongValue longValue : ids){
    			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,longValue.getType(),longValue.getKey(),longValue.getVal());
    			key = getTransMapKey(userId,longValue.getType(),longValue.getKey());
    			childs = map.get(key);
        		if(childs != null){
        			setUserTimesheetForUser(defaultTimesheet,lds,childs);
        			map.remove(key);
        		}
        		returnList.add(timesheet);
    		}
    		//以前参与了项目或合同，现在在参与者中没找到
    		if(!map.isEmpty()){
    			getOtherTimesheetForUser(returnList,map,userId,lds);
    		}
    	}
    	//添加地区
    	returnList.add(1, new UserTimesheetForUser(area1,area2,area3,area4,area5,area6,area7));
    	returnList.add(2, defaultTimesheet);
		return returnList;
	}
	private void getOtherTimesheetForUser(List<UserTimesheetForUser> returnList, Map<String, Map<Long, UserTimesheet>> map, Long userId, Long[] lds) {
		UserTimesheet tmpTimesheet = null;
		Map<Long, UserTimesheet> childs = null;
		for(String mapKey : map.keySet()){
			childs = map.get(mapKey);
			UserTimesheetForUser timesheet = getDefaultUserTimesheetForUser(userId,null,null,null);
			for(Long workDay : childs.keySet()){
				tmpTimesheet = childs.get(workDay);
				timesheet.setType(tmpTimesheet.getType());
				timesheet.setObjId(tmpTimesheet.getObjId());
				timesheet.setObjName(tmpTimesheet.getObjName());
				if(lds[0] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData1(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId1(tmpTimesheet.getId());
				}else if(lds[1] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData2(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId2(tmpTimesheet.getId());
				}else if(lds[2] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData3(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId3(tmpTimesheet.getId());
				}else if(lds[3] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData4(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId4(tmpTimesheet.getId());
				}else if(lds[4] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData5(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId5(tmpTimesheet.getId());
				}else if(lds[5] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData6(tmpTimesheet.getRealInput().toString());
					}
					timesheet.setId6(tmpTimesheet.getId());
				}else if(lds[6] == workDay){
					if(tmpTimesheet.getRealInput() != null){
						timesheet.setData7(tmpTimesheet.getRealInput().toString());
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
	private void setUserTimesheetForUser(UserTimesheetForUser userTimesheet, Long[] lds,Map<Long, UserTimesheet> childs) {
		UserTimesheet tmp = childs.get(lds[0]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData1(tmp.getRealInput().toString());
			}
			userTimesheet.setId1(tmp.getId());
		}
		tmp = childs.get(lds[1]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData2(tmp.getRealInput().toString());
			}
			userTimesheet.setId2(tmp.getId());
		}
		tmp = childs.get(lds[2]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData3(tmp.getRealInput().toString());
			}
			userTimesheet.setId3(tmp.getId());
		}
		tmp = childs.get(lds[3]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData4(tmp.getRealInput().toString());
			}
			userTimesheet.setId4(tmp.getId());
		}
		tmp = childs.get(lds[4]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData5(tmp.getRealInput().toString());
			}
			userTimesheet.setId5(tmp.getId());
		}
		tmp = childs.get(lds[5]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData6(tmp.getRealInput().toString());
			}
			userTimesheet.setId6(tmp.getId());
		}
		tmp = childs.get(lds[6]);
		if(tmp != null){
			if(tmp.getRealInput() != null){
				userTimesheet.setData7(tmp.getRealInput().toString());
			}
			userTimesheet.setId7(tmp.getId());
		}
	}

	private UserTimesheetForUser getDefaultUserTimesheetForUser(Long userId, Integer type, Long objId, String objName) {
		UserTimesheetForUser userTimesheetForUser = new UserTimesheetForUser();
		userTimesheetForUser.setUserId(userId);
		userTimesheetForUser.setType(type);
		userTimesheetForUser.setObjId(objId);
		userTimesheetForUser.setObjName(objName);
		userTimesheetForUser.setData1(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData2(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData3(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData4(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData5(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData6(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
		userTimesheetForUser.setData7(CpmConstants.DFAULT_USER_TIMESHEET_INPUT);
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
}
