package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

public class UserCostVo {

	private Long id;
    private Long userId;
    private String userName;
    private Long costMonth;
    private Double internalCost;
    private Double externalCost;
    private Integer status;
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    private Double sal;
    private Double socialSecurity;
    private Double fund;
    private Double socialSecurityFund;
    private Double otherExpense;
    private String serialNum;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getCostMonth() {
		return costMonth;
	}
	public void setCostMonth(Long costMonth) {
		this.costMonth = costMonth;
	}
	public Double getInternalCost() {
		return internalCost;
	}
	public void setInternalCost(Double internalCost) {
		this.internalCost = internalCost;
	}
	public Double getExternalCost() {
		return externalCost;
	}
	public void setExternalCost(Double externalCost) {
		this.externalCost = externalCost;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public Double getSal() {
		return sal;
	}
	public void setSal(Double sal) {
		this.sal = sal;
	}
	public Double getSocialSecurityFund() {
		return socialSecurityFund;
	}
	public void setSocialSecurityFund(Double socialSecurityFund) {
		this.socialSecurityFund = socialSecurityFund;
	}
	public Double getOtherExpense() {
		return otherExpense;
	}
	public void setOtherExpense(Double otherExpense) {
		this.otherExpense = otherExpense;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public Double getSocialSecurity() {
		return socialSecurity;
	}
	public void setSocialSecurity(Double socialSecurity) {
		this.socialSecurity = socialSecurity;
	}
	public Double getFund() {
		return fund;
	}
	public void setFund(Double fund) {
		this.fund = fund;
	}
	@Override
	public String toString() {
		return "UserCostVo [id=" + id + ", userId=" + userId + ", userName=" + userName + ", costMonth=" + costMonth
				+ ", internalCost=" + internalCost + ", externalCost=" + externalCost + ", status=" + status
				+ ", creator=" + creator + ", createTime=" + createTime + ", updator=" + updator + ", updateTime="
				+ updateTime + ", sal=" + sal + ", socialSecurity=" + socialSecurity + ", fund=" + fund
				+ ", socialSecurityFund=" + socialSecurityFund + ", otherExpense=" + otherExpense + ", serialNum="
				+ serialNum + "]";
	}
}
