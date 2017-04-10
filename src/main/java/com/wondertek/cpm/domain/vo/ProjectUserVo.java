package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ProjectUser;

public class ProjectUserVo {
    private Long id;
	
    private Long projectId;
    private String projectNum;
    private String projectName;
    private Integer type;
    private Long contractId;
    private Long outsourcingUserId;
    
    private Long userId;
    private String userName;
    private String userRole;
    private Long joinDay;
    private Long leaveDay;
    private String rank;
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    
    
	public ProjectUserVo() {
	}
	public ProjectUserVo(ProjectUser projectUser, String projectNum, String projectName,Integer type,Long contractId,Long outsourcingUserId) {
		this.id = projectUser.getId();
		this.projectId = projectUser.getProjectId();
		this.userId = projectUser.getUserId();
		this.userName = projectUser.getUserName();
		this.userRole = projectUser.getUserRole();
		this.joinDay = projectUser.getJoinDay();
		this.leaveDay = projectUser.getLeaveDay();
		this.rank = projectUser.getRank();
		this.creator = projectUser.getCreator();
		this.createTime = projectUser.getCreateTime();
		this.updator = projectUser.getUpdator();
		this.updateTime = projectUser.getUpdateTime();
		
		this.projectNum = projectNum;
		this.projectName = projectName;
		this.type = type;
		this.contractId = contractId;
		this.outsourcingUserId = outsourcingUserId;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectNum() {
		return projectNum;
	}
	public void setProjectNum(String projectNum) {
		this.projectNum = projectNum;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public Long getOutsourcingUserId() {
		return outsourcingUserId;
	}
	public void setOutsourcingUserId(Long outsourcingUserId) {
		this.outsourcingUserId = outsourcingUserId;
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
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
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
