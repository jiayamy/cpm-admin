package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.wondertek.cpm.domain.DeptInfo;

public class DeptInfoVo {

    private Long id;
    private String name;
    private Long parentId;
    
    private String parentName;

    private Long type;
    private String typeName;
    
    private Integer status;
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
    
    public DeptInfoVo() {
    	
	}
	public DeptInfoVo(DeptInfo deptInfo,String parentName,String typeName) {
		this.id = deptInfo.getId();
		this.name = deptInfo.getName();
		this.parentId = deptInfo.getParentId();
		this.type = deptInfo.getType();
		
		this.status = deptInfo.getStatus();
		this.creator = deptInfo.getCreator();
		this.createTime = deptInfo.getCreateTime();
		this.updator = deptInfo.getUpdator();
		this.updateTime = deptInfo.getUpdateTime();
		
		this.parentName = parentName;
		this.typeName = typeName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
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
}
