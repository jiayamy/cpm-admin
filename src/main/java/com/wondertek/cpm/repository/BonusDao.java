package com.wondertek.cpm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wondertek.cpm.domain.Bonus;
import com.wondertek.cpm.domain.DeptInfo;
import com.wondertek.cpm.domain.User;
import com.wondertek.cpm.domain.vo.BonusVo;

public interface BonusDao extends GenericDao<Bonus, Long> {

	public Page<BonusVo> getPageByParams(User user,DeptInfo deptInfo,Bonus bonus,Pageable pageable);

	public Page<BonusVo> getPageDetail(Long contractId,User user,DeptInfo deptInfo,Pageable pageable);

	public BonusVo getUserBonus(Long id, User user, DeptInfo deptInfo);

	public List<BonusVo> getBonusListl(Bonus bonus, User user, DeptInfo deptInfo);

}
