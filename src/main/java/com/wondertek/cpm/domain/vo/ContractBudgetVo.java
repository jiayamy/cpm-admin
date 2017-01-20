package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.UserTimesheet;

public class ContractBudgetVo {
	private Long id;
	private Long contractId;
	private String serialNum;
	private String name;
	private String contractName;
	private Integer purchaseType;
	private String userName;
	private Long userId;
	private String dept;
	private Long deptId;
	private Double budgetTotal;
	private Integer status;
	private ZonedDateTime createTime;
	private ZonedDateTime updateTime;
	
	public ContractBudgetVo(){
		
	}
	
	public ContractBudgetVo(ContractBudget contractBudget,String serialNum,String contractName){
		this.id = contractBudget.getId();
		this.budgetTotal = contractBudget.getBudgetTotal();
		this.contractId = contractBudget.getContractId();
		this.dept = contractBudget.getDept();
		this.deptId = contractBudget.getUserId();
		this.purchaseType = contractBudget.getPurchaseType();
		this.createTime = contractBudget.getCreateTime();
		this.userId = contractBudget.getUserId();
		this.userName = contractBudget.getUserName();
		this.status = contractBudget.getStatus();
		this.updateTime = contractBudget.getUpdateTime();
		
		this.serialNum = serialNum;
		this.contractName = contractName;
		
	}
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
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
	public ZonedDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}
	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getPurchaseType() {
		return purchaseType;
	}
	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}
	public String getContractName() {
		return contractName;
	}
	public void setCtractName(String contractName) {
		this.contractName = contractName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
}
