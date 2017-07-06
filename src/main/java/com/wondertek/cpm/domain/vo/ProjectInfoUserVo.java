package com.wondertek.cpm.domain.vo;

public class ProjectInfoUserVo {

	private String serialNum;	//员工工号
	private String userName;	//员工姓名
	private Double totalInput;	//员工总工时
	
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Double getTotalInput() {
		return totalInput;
	}
	public void setTotalInput(Double totalInput) {
		this.totalInput = totalInput;
	}
	@Override
	public String toString() {
		return "ProjectInfoUserVo [serialNum=" + serialNum + ", userName=" + userName + ", totalInput=" + totalInput
				+ "]";
	}
	
}
