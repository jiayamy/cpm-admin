package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractBudget;

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
	
	private Boolean isEdit = Boolean.FALSE;		//能够编辑采购单
	private Boolean isCreate = Boolean.FALSE;	//能够创建项目或者采购子项
	
	public ContractBudgetVo(){
		
	}
	
	public ContractBudgetVo(ContractBudget contractBudget,String serialNum,String contractName){
		this.id = contractBudget.getId();
		this.name = contractBudget.getName();
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
	public ContractBudgetVo(Object[] o, long userId, String login, long deptId, String idPath){
		ContractBudget contractBudget = (ContractBudget)o[0];
		
		this.id = contractBudget.getId();
		this.name = contractBudget.getName();
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
		
		this.serialNum = StringUtil.null2Str(o[1]);
		this.contractName = StringUtil.null2Str(o[2]);
		
		String contractCreator = StringUtil.null2Str(o[3]);
		Long salesmanId = StringUtil.nullToCloneLong(o[4]);
		Long consultantsId = StringUtil.nullToCloneLong(o[5]);
		//合同相关
		Long wdiId = StringUtil.nullToCloneLong(o[6]);
		String wdiIdPath = StringUtil.null2Str(o[7]);
		
		Long wdi2Id = StringUtil.nullToCloneLong(o[8]);
		String wdi2IdPath = StringUtil.null2Str(o[9]);
		//预算相关
		Long wdi3Id = StringUtil.nullToLong(o[10]).longValue();
		String wdi3IdPath = StringUtil.null2Str(o[11]);
		
		//判定 isEdit
		if(contractCreator.equals(login) || (salesmanId != null && salesmanId == userId) || (consultantsId != null && consultantsId == userId) || login.equals(contractBudget.getCreator())
				|| (wdiId != null && wdiId == deptId) || wdiIdPath.startsWith(idPath)
				|| (wdi2Id != null && wdi2Id == deptId) || wdi2IdPath.startsWith(idPath)){
			this.isEdit = Boolean.TRUE;
		}
		
		//判定 isCreate
		if(contractBudget.getUserId() == userId || wdi3Id == deptId || wdi3IdPath.startsWith(idPath)){
			this.isCreate = Boolean.TRUE;
		}
	}
	
	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Boolean getIsCreate() {
		return isCreate;
	}

	public void setIsCreate(Boolean isCreate) {
		this.isCreate = isCreate;
	}

	public void setContractName(String contractName) {
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
