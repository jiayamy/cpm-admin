package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ContractCost;

public class ContractCostVo {
	
	    private Long id;
	    private Long contractId;
	    private Long budgetId;
	    private Long deptId;
	    private String dept;
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
	    
	    
	    private String contractNum;
	    private String contractName;
	    private Double budgetTotal;
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
		public Long getBudgetId() {
			return budgetId;
		}
		public void setBudgetId(Long budgetId) {
			this.budgetId = budgetId;
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
		public Double getBudgetTotal() {
			return budgetTotal;
		}
		public void setBudgetTotal(Double budgetTotal) {
			this.budgetTotal = budgetTotal;
		}
		public ContractCostVo() {
		}
		public ContractCostVo(Long id, Long contractId, Long budgetId, Long deptId, String dept, String name,
				Integer type, Long costDay, Double total, String costDesc, Integer status, String creator,
				ZonedDateTime createTime, String updator, ZonedDateTime updateTime, String contractNum,
				String contractName, Double budgetTotal) {
			this.id = id;
			this.contractId = contractId;
			this.budgetId = budgetId;
			this.deptId = deptId;
			this.dept = dept;
			this.name = name;
			this.type = type;
			this.costDay = costDay;
			this.total = total;
			this.costDesc = costDesc;
			this.status = status;
			this.creator = creator;
			this.createTime = createTime;
			this.updator = updator;
			this.updateTime = updateTime;
			this.contractNum = contractNum;
			this.contractName = contractName;
			this.budgetTotal = budgetTotal;
		}
		
		public  ContractCostVo(ContractCost contractCost, String contractNum,String contractName ,Double budgetTotal) {
			this.id = contractCost.getId();
			this.contractId = contractCost.getContractId();
			this.budgetId = contractCost.getBudgetId();
			this.deptId = contractCost.getDeptId();
			this.dept = contractCost.getDept();
			this.name = contractCost.getName();
			this.type = contractCost.getType();
			this.costDay = contractCost.getCostDay();
			this.total = contractCost.getTotal();
			this.costDesc = contractCost.getCostDesc();
			this.status = contractCost.getStatus();
			this.creator = contractCost.getCreator();
			this.createTime = contractCost.getCreateTime();
			this.updator = contractCost.getUpdator();
			this.updateTime = contractCost.getUpdateTime();
			this.contractNum = contractNum;
			this.contractName = contractName;
			this.budgetTotal = budgetTotal;
		}
}
