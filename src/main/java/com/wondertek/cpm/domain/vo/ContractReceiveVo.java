package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ContractReceive;

public class ContractReceiveVo {
	private Long id;
	
	private Long contractId;
	private String contractNum;
    private String contractName;
	
    private Double receiveTotal;
    private Long receiveDay;
    private String receiver;
    private Integer status;
    private String creator;
    private ZonedDateTime createTime;
    private String updator;
    private ZonedDateTime updateTime;
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
	public Double getReceiveTotal() {
		return receiveTotal;
	}
	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}
	public Long getReceiveDay() {
		return receiveDay;
	}
	public void setReceiveDay(Long receiveDay) {
		this.receiveDay = receiveDay;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
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
	public ContractReceiveVo() {
	}
	public ContractReceiveVo(Long id, Long contractId, String contractNum, String contractName, Double receiveTotal,
			Long receiveDay, String receiver, Integer status, String creator, ZonedDateTime createTime, String updator,
			ZonedDateTime updateTime) {
		this.id = id;
		this.contractId = contractId;
		this.contractNum = contractNum;
		this.contractName = contractName;
		this.receiveTotal = receiveTotal;
		this.receiveDay = receiveDay;
		this.receiver = receiver;
		this.status = status;
		this.creator = creator;
		this.createTime = createTime;
		this.updator = updator;
		this.updateTime = updateTime;
	}
	public ContractReceiveVo(ContractReceive contractReceive,String contractNum,String contractName ) {
		this.id = contractReceive.getId();
		this.contractId = contractReceive.getContractId();
		this.contractNum = contractNum;
		this.contractName = contractName;
		this.receiveTotal = contractReceive.getReceiveTotal();
		this.receiveDay = contractReceive.getReceiveDay();
		this.receiver = contractReceive.getReceiver();
		this.status = contractReceive.getStatus();
		this.creator = contractReceive.getCreator();
		this.createTime = contractReceive.getCreateTime();
		this.updator = contractReceive.getUpdator();
		this.updateTime = contractReceive.getUpdateTime();
	}
    
    
}
