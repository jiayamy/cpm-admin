package com.wondertek.cpm.service;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.wondertek.cpm.domain.RoleHardWorking;
import com.wondertek.cpm.domain.vo.ChartReportDataVo;
import com.wondertek.cpm.domain.vo.ProjectMonthlyStatVo;
import com.wondertek.cpm.repository.RoleHardWorkingDao;
import com.wondertek.cpm.repository.RoleHardWorkingRepository;
import com.wondertek.cpm.repository.UserRepository;
import com.wondertek.cpm.security.SecurityUtils;

@Service
@Transactional
public class RoleHardWorkingService {
	private final Logger log = LoggerFactory.getLogger(RoleHardWorkingService.class);
	
	@Inject
	private RoleHardWorkingRepository roleHardWorkingRepository;
	
	@Inject
	private RoleHardWorkingDao roleHardWorkingDao;
	
	@Inject
	private UserRepository userRepository;
	


	public void saveRoleHardWorking(RoleHardWorking roleHardWorking) {
		roleHardWorkingRepository.save(roleHardWorking);
	}


	public Page<RoleHardWorking> searchPage(RoleHardWorking roleHardWorking,Pageable pageable) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if (objs != null) {
    		Page<RoleHardWorking> page = roleHardWorkingDao.getPageByParams(roleHardWorking,pageable);
        	return page;
    	}else{
    		return new PageImpl(new ArrayList<RoleHardWorking>(), pageable, 0);
    	}
	}


	public RoleHardWorking findOne(Long statId) {
		
		return roleHardWorkingRepository.findById(statId);
	}

	@Transactional(readOnly = true)
	public List<ChartReportDataVo> getChartData(Date fMonth, Date lMonth, Long userId) {
		List<Object[]> objs = userRepository.findUserInfoByLogin(SecurityUtils.getCurrentUserLogin());
    	if(objs != null && !objs.isEmpty()){
    		List<ChartReportDataVo> datas = new ArrayList<ChartReportDataVo>();
        	ChartReportDataVo data = new ChartReportDataVo();
        	data.setName("勤奋度");
        	data.setType("line");
        	List<Double> dataD1 = new ArrayList<>();
        	Calendar cal1 = Calendar.getInstance();
        	Calendar cal2 = Calendar.getInstance();
        	cal1.setTime(fMonth);
        	cal2.setTime(lMonth);
        	int yearCount = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
        	int count = 0;
        	count += 12*yearCount;
        	count += cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
        	for(int i = 0 ; i <= count; i++){
        		Long originMonth = StringUtil.nullToLong(DateUtil.formatDate("yyyyMM", cal1.getTime()));
        		RoleHardWorking RoleHardWorking = roleHardWorkingDao.getByOriginMonthAndUserId(originMonth, userId);
        		Double hardWorking = 0D;
        		if(RoleHardWorking != null ){
        			hardWorking = RoleHardWorking.getHardWorking();
        		}
        		dataD1.add(hardWorking);
        		cal1.add(Calendar.MONTH, 1);
        	}
        	data.setData(dataD1);
        	datas.add(data);
        	return datas;
    	}
    	return null;
	}

	
}
