package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.config.StringUtil;
import com.wondertek.cpm.domain.ContractBudget;
import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.OutsourcingUser;

public class OutsourcingUserVo {
	private Long id;
	private Long contractId;
	private String serialNum;
	private String contractName;
	private Integer targetAmount;
	private String rank;
	private Double offer;
	private String creator;
	private ZonedDateTime createTime;
	private String updator;
	private ZonedDateTime updateTime;
	
	
	public OutsourcingUserVo(){
		
	}
	
	public OutsourcingUserVo(OutsourcingUser outsourcingUser,String contractName,String serialNum){
		this.id = outsourcingUser.getId();
		this.contractId = outsourcingUser.getContractId();
		this.offer = outsourcingUser.getOffer();
		this.rank = outsourcingUser.getRank();
		this.targetAmount = outsourcingUser.getTargetAmount();
		this.creator = outsourcingUser.getCreator();
		this.createTime = outsourcingUser.getCreateTime();
		this.updator = outsourcingUser.getUpdator();
		this.updateTime = outsourcingUser.getUpdateTime();
		
		this.serialNum = serialNum;
		this.contractName = contractName;
		
	}

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

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public Integer getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(Integer targetAmount) {
		this.targetAmount = targetAmount;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Double getOffer() {
		return offer;
	}

	public void setOffer(Double offer) {
		this.offer = offer;
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
