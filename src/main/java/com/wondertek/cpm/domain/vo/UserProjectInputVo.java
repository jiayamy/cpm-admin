package com.wondertek.cpm.domain.vo;
/**
 * 人员项目工时
 */
public class UserProjectInputVo {

	private String projectSerialNum;
	private String projectName;
	private String contractSerialNum;
	private String contractName;
	private String pmSerialNum;
	private String pmName;
	private String pmDeptType;
	private String userSerialNum;
	private String userName;
	private Double realInput;		//项目总工时
	private Double acceptInput;		//项目总认可工时
	private Double extraInput;		//项目总加班工时
	private Double acceptExtraInput;//项目总认可加班工时
	
	public UserProjectInputVo(){
		
	}
	
	public UserProjectInputVo(String userSerialNum, String userName,String projectName,Double realInput,
			Double acceptInput, Double extraInput, Double acceptExtraInput) {
		this.projectName = projectName;
		this.userSerialNum = userSerialNum;
		this.userName = userName;
		this.realInput = realInput;
		this.acceptInput = acceptInput;
		this.extraInput = extraInput;
		this.acceptExtraInput = acceptExtraInput;
	}

	public String getProjectSerialNum() {
		return projectSerialNum;
	}
	public void setProjectSerialNum(String projectSerialNum) {
		this.projectSerialNum = projectSerialNum;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getContractSerialNum() {
		return contractSerialNum;
	}
	public void setContractSerialNum(String contractSerialNum) {
		this.contractSerialNum = contractSerialNum;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getPmSerialNum() {
		return pmSerialNum;
	}
	public void setPmSerialNum(String pmSerialNum) {
		this.pmSerialNum = pmSerialNum;
	}
	public String getPmName() {
		return pmName;
	}
	public void setPmName(String pmName) {
		this.pmName = pmName;
	}
	public String getPmDeptType() {
		return pmDeptType;
	}
	public void setPmDeptType(String pmDeptType) {
		this.pmDeptType = pmDeptType;
	}
	public String getUserSerialNum() {
		return userSerialNum;
	}
	public void setUserSerialNum(String userSerialNum) {
		this.userSerialNum = userSerialNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public Double getExtraInput() {
		return extraInput;
	}
	public void setExtraInput(Double extraInput) {
		this.extraInput = extraInput;
	}
	public Double getAcceptExtraInput() {
		return acceptExtraInput;
	}
	public void setAcceptExtraInput(Double acceptExtraInput) {
		this.acceptExtraInput = acceptExtraInput;
	}
	@Override
	public String toString() {
		return "UserProjectInputVo [projectSerialNum=" + projectSerialNum + ", projectName=" + projectName
				+ ", contractSerialNum=" + contractSerialNum + ", contractName=" + contractName + ", pmSerialNum="
				+ pmSerialNum + ", pmName=" + pmName + ", pmDeptType=" + pmDeptType + ", userSerialNum=" + userSerialNum
				+ ", userName=" + userName + ", realInput=" + realInput + ", acceptInput=" + acceptInput
				+ ", extraInput=" + extraInput + ", acceptExtraInput=" + acceptExtraInput + "]";
	}
}
