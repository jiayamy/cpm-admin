package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.UserTimesheet;

public class UserTimesheetVo {
	private Long id;
	private Long workDay;		//	日期
	private Long userId;		//	员工ID
	private String userName;	//		员工名字
	private Integer type;		//	类型（合同、项目、公共成本）
	private Long objId;			//	对象ID
	private String objName;		//	对象名称
	private Double realInput;	//	工时投入（实际投入工时，项目成本需要）
	private Double acceptInput;	//	工时产出（认可工时，统计员工贡献度需要）
	private String workArea;	//		工作地区
	private Integer status;		//	状态（可用，删除）
	private String creator;
	private ZonedDateTime createTime;
	private String updator;
	private ZonedDateTime updateTime;
	/**
	 * 查看所有的
	 */
	public UserTimesheetVo(UserTimesheet userTimesheet){
		this.id = userTimesheet.getId();
		this.workDay = userTimesheet.getWorkDay();
		this.userId = userTimesheet.getUserId();
		this.userName = userTimesheet.getUserName();
		this.type = userTimesheet.getType();
		this.objId = userTimesheet.getObjId();
		this.objName = userTimesheet.getObjName();
		this.realInput = userTimesheet.getRealInput();
		this.acceptInput = userTimesheet.getAcceptInput();
		this.workArea = userTimesheet.getWorkArea();
		this.status = userTimesheet.getStatus();
		this.creator = userTimesheet.getCreator();
		this.createTime = userTimesheet.getCreateTime();
		this.updator = userTimesheet.getUpdator();
		this.updateTime = userTimesheet.getUpdateTime();
	}
	/**
	 * 用户访问的，部分数据不给用户看
	 * @param userTimesheet
	 * @param pm
	 */
	public UserTimesheetVo(UserTimesheet userTimesheet,String user){
		this.id = userTimesheet.getId();
		this.workDay = userTimesheet.getWorkDay();
		this.userName = userTimesheet.getUserName();
		this.type = userTimesheet.getType();
		this.objName = userTimesheet.getObjName();
		this.realInput = userTimesheet.getRealInput();
		this.workArea = userTimesheet.getWorkArea();
		this.status = userTimesheet.getStatus();
		this.creator = userTimesheet.getCreator();
		this.createTime = userTimesheet.getCreateTime();
		this.updator = userTimesheet.getUpdator();
		this.updateTime = userTimesheet.getUpdateTime();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWorkDay() {
		return workDay;
	}

	public void setWorkDay(Long workDay) {
		this.workDay = workDay;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getObjId() {
		return objId;
	}

	public void setObjId(Long objId) {
		this.objId = objId;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public Double getRealInput() {
		return realInput;
	}

	public void setRealInput(Double realInput) {
		this.realInput = realInput;
	}

	public Double getAcceptInput() {
		return acceptInput;
	}

	public void setAcceptInput(Double acceptInput) {
		this.acceptInput = acceptInput;
	}

	public String getWorkArea() {
		return workArea;
	}

	public void setWorkArea(String workArea) {
		this.workArea = workArea;
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
