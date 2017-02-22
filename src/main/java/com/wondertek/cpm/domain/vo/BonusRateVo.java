package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.BonusRate;

/**
 * 奖金提成比率
 */
public class BonusRateVo{

    private Long id;
    /**
	 * 部门类型
	 */
	private Long deptType;
	private String dept;
	
	/**
	 * 提成比率
	 */
	private Double rate;
	
	/**
	 * 合同类型
	 */
	private Integer contractType;
	
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    
	public BonusRateVo(BonusRate bonusRate, String dept) {
		this.id = bonusRate.getId();
		this.deptType = bonusRate.getDeptType();
		this.dept = dept;
		this.contractType = bonusRate.getContractType();
		this.rate = bonusRate.getRate();
		this.creator = bonusRate.getCreator();
		this.createTime = bonusRate.getCreateTime();
		this.updator = bonusRate.getUpdator();
		this.updateTime = bonusRate.getUpdateTime();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDeptType() {
		return deptType;
	}
	public void setDeptType(Long deptType) {
		this.deptType = deptType;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Integer getContractType() {
		return contractType;
	}
	public void setContractType(Integer contractType) {
		this.contractType = contractType;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public ZonedDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}
	public String getUpdator() {
		return updator;
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
}
