package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

public class ProjectInfoVo {
    private Long id;
    private String serialNum;
    
    private Long contractId;
    private String contractNum;
    
    private Long budgetId;
    private String budgetName;
    private Double budgetOriginal;
    
    private String name;
    
    private String pm;
    private String dept;
    
    private ZonedDateTime startDay;
    private ZonedDateTime endDay;
    
    private Double budgetTotal;
    
    private Integer status;
    private Double finishRate;
    
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNum() {
		return contractNum;
	}
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}
	public Long getBudgetId() {
		return budgetId;
	}
	public void setBudgetId(Long budgetId) {
		this.budgetId = budgetId;
	}
	public String getBudgetName() {
		return budgetName;
	}
	public void setBudgetName(String budgetName) {
		this.budgetName = budgetName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPm() {
		return pm;
	}
	public void setPm(String pm) {
		this.pm = pm;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public ZonedDateTime getStartDay() {
		return startDay;
	}
	public void setStartDay(ZonedDateTime startDay) {
		this.startDay = startDay;
	}
	public ZonedDateTime getEndDay() {
		return endDay;
	}
	public void setEndDay(ZonedDateTime endDay) {
		this.endDay = endDay;
	}
	public Double getBudgetTotal() {
		return budgetTotal;
	}
	public void setBudgetTotal(Double budgetTotal) {
		this.budgetTotal = budgetTotal;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Double getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
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
	public Double getBudgetOriginal() {
		return budgetOriginal;
	}
	public void setBudgetOriginal(Double budgetOriginal) {
		this.budgetOriginal = budgetOriginal;
	}
	
}
