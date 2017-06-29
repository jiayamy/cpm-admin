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

import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.UserProjectInputVo;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.repository.UserTimesheetDao;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class UserProjectInputService {

	private final Logger log = LoggerFactory.getLogger(UserProjectInputService.class);
	
	@Inject
	private UserTimesheetDao userTimesheetDao;
	
	@Inject
	private UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public List<UserProjectInputVo> getUserProjectInputs(Long startTime,Long endTime,List<Long> userIds,List<Long> projectIds,Boolean showTotal){
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
		if (objs != null) {
			Object[] o = objs.get(0);
			User user = (User)o[0];
			DeptInfo deptInfo = (DeptInfo)o[1];
			
			List<UserProjectInputVo> dbList = userTimesheetDao.getUserProjectInputsByParam(startTime, endTime, userIds, projectIds, user, deptInfo);
			List<UserProjectInputVo> returnList = new ArrayList<UserProjectInputVo>();
			Map<String,UserProjectInputVo> userMap = new HashMap<String,UserProjectInputVo>();//key-userSerialNUm
			if(!showTotal){//不显示合计
				if(dbList != null && dbList.size() > 0){
					for(UserProjectInputVo vo : dbList){
						if (userMap.containsKey(vo.getUserSerialNum())) {
							//vo.setUserSerialNum("");
							//vo.setUserName("");
							returnList.add(vo);
						}else{
							userMap.put(vo.getUserSerialNum(), null);
							returnList.add(vo);
						}
					}
				}
			}else{//显示合计
				if(dbList != null && dbList.size() > 0){
					UserProjectInputVo temp = null;
					int i = 0;
					for(UserProjectInputVo vo : dbList){
						if (userMap.containsKey(vo.getUserSerialNum())) {
							temp = userMap.get(vo.getUserSerialNum());
							temp.setRealInput(vo.getRealInput() + temp.getRealInput());
							temp.setAcceptInput(vo.getAcceptInput() + temp.getAcceptInput());
							temp.setExtraInput(vo.getExtraInput() + temp.getExtraInput());
							temp.setAcceptExtraInput(vo.getAcceptExtraInput() + temp.getAcceptExtraInput());
							//vo.setUserSerialNum("");
							//vo.setUserName("");
							returnList.add(vo);
						}else{
							if (returnList.size() > 0) {
								returnList.add(userMap.get(returnList.get(returnList.size() - 1).getUserSerialNum()));
								returnList.add(vo);
							} else {
								returnList.add(vo);
							}
							userMap.put(vo.getUserSerialNum(), new UserProjectInputVo(
									vo.getUserSerialNum(),vo.getUserName(),"合计",vo.getRealInput(),
									vo.getAcceptInput(),vo.getExtraInput(),vo.getAcceptExtraInput()));
						}
						if (i == dbList.size() - 1) {
							returnList.add(userMap.get(vo.getUserSerialNum()));
						}
						i++;
					}
				}
			}
			return returnList;
		}
		return null;
	}
}
