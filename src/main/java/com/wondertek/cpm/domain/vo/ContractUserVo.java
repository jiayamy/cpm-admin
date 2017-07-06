package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ContractUser;

public class ContractUserVo {
	private Long id;
	
	private Long contractId;
	private String contractNum;
    private String contractName;
	
	private Long userId;
	private String userName;
	private String userRole;
	private Long deptId;
	private String dept;
	private Long joinDay;
	private Long leaveDay;
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
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
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
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public Long getJoinDay() {
		return joinDay;
	}
	public void setJoinDay(Long joinDay) {
		this.joinDay = joinDay;
	}
	public Long getLeaveDay() {
		return leaveDay;
	}
	public void setLeaveDay(Long leaveDay) {
		this.leaveDay = leaveDay;
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
	public ContractUserVo() {
	}
	public ContractUserVo(Long id, Long contractId, String contractNum, String contractName, Long userId,
			String userName, String userRole, Long deptId, String dept, Long joinDay, Long leaveDay, String creator,
			ZonedDateTime createTime, String updator, ZonedDateTime updateTime) {
		this.id = id;
		this.contractId = contractId;
		this.contractNum = contractNum;
		this.contractName = contractName;
		this.userId = userId;
		this.userName = userName;
		this.userRole = userRole;
		this.deptId = deptId;
		this.dept = dept;
		this.joinDay = joinDay;
		this.leaveDay = leaveDay;
		this.creator = creator;
		this.createTime = createTime;
		this.updator = updator;
		this.updateTime = updateTime;
	}
	public ContractUserVo(ContractUser contractUser, String contractNum, String contractName) {
		this.id = contractUser.getId();
		
		this.contractId = contractUser.getContractId();
		this.userId = contractUser.getUserId();
		this.userName = contractUser.getUserName();
		this.deptId = contractUser.getDeptId();
		this.dept = contractUser.getDept();
		this.joinDay = contractUser.getJoinDay();
		this.leaveDay = contractUser.getLeaveDay();
		this.creator = contractUser.getCreator();
		this.createTime = contractUser.getCreateTime();
		this.updator = contractUser.getUpdator();
		this.updateTime = contractUser.getUpdateTime();
		
		this.contractNum = contractNum;
		this.contractName = contractName;
		
	}
	@Override
	public String toString() {
		return "ContractUserVo [id=" + id + ", contractId=" + contractId + ", contractNum=" + contractNum
				+ ", contractName=" + contractName + ", userId=" + userId + ", userName=" + userName + ", userRole="
				+ userRole + ", deptId=" + deptId + ", dept=" + dept + ", joinDay=" + joinDay + ", leaveDay=" + leaveDay
				+ ", creator=" + creator + ", createTime=" + createTime + ", updator=" + updator + ", updateTime="
				+ updateTime + "]";
	}
	
}
