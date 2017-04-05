package com.wondertek.cpm.domain.vo;

import java.io.Serializable;

import com.wondertek.cpm.domain.User;

/**
 * 员工基本信息
 */
public class UserBaseVo implements Serializable {
	private static final long serialVersionUID = -3126446921820410700L;
	/**
	 * 员工主键
	 */
	private Long id;
	/**
	 * 员工姓名
	 */
    private String lastName;
    /**
     * 员工级别
     */
    private Integer grade;
    /**
     * 员工工号
     */
    private String serialNum;
    /**
     * 员工部门
     */
    private Long deptId;
    
	public UserBaseVo(User user) {
		this.id = user.getId();
		this.lastName = user.getLastName();
		this.grade = user.getGrade();
		this.serialNum = user.getSerialNum();
		this.deptId = user.getDeptId();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}   
}
