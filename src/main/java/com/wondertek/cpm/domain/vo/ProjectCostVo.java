package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ProjectCost;

public class ProjectCostVo{

    private Long id;
    
    private Long projectId;
    private String projectNum;
    private String projectName;
    
    private String name;
    private Integer type;
    private Long costDay;
    private Double total;
    private String costDesc;
    private Integer status;
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    private Double input;
    
    
	public ProjectCostVo() {
	}
	public ProjectCostVo(ProjectCost projectCost, String projectNum, String projectName) {
		this.id = projectCost.getId();
		this.projectId = projectCost.getProjectId();
		this.name = projectCost.getName();
		this.type = projectCost.getType();
		this.costDay = projectCost.getCostDay();
		this.total = projectCost.getTotal();
		this.costDesc = projectCost.getCostDesc();
		this.status = projectCost.getStatus();
		this.creator = projectCost.getCreator();
		this.createTime = projectCost.getCreateTime();
		this.updator = projectCost.getUpdator();
		this.updateTime = projectCost.getUpdateTime();
		this.input = projectCost.getInput();
		
		this.projectNum = projectNum;
		this.projectName = projectName;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getCostDay() {
		return costDay;
	}
	public void setCostDay(Long costDay) {
		this.costDay = costDay;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getCostDesc() {
		return costDesc;
	}
	public void setCostDesc(String costDesc) {
		this.costDesc = costDesc;
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
	public Double getInput() {
		return input;
	}
	public void setInput(Double input) {
		this.input = input;
	}

}
