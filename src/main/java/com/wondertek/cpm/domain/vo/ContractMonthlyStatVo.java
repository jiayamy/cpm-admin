package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ContractMonthlyStatVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private Long id;
    private Long contractId;
    private String serialNum;
    private String name;
    private Double finishRate;
    private Double receiveTotal;
    private Double costTotal;
    private Double grossProfit;
    private Double salesHumanCost;
    private Double salesPayment;
    private Double consultHumanCost;
    private Double consultPayment;
    private Double hardwarePurchase;
    private Double externalSoftware;
    private Double internalSoftware;
    private Double projectHumanCost;
    private Double projectPayment;
    private Long statWeek;
    private ZonedDateTime createTime;
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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}
	public Double getReceiveTotal() {
		return receiveTotal;
	}
	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}
	public Double getCostTotal() {
		return costTotal;
	}
	public void setCostTotal(Double costTotal) {
		this.costTotal = costTotal;
	}
	public Double getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}
	public Double getSalesHumanCost() {
		return salesHumanCost;
	}
	public void setSalesHumanCost(Double salesHumanCost) {
		this.salesHumanCost = salesHumanCost;
	}
	public Double getSalesPayment() {
		return salesPayment;
	}
	public void setSalesPayment(Double salesPayment) {
		this.salesPayment = salesPayment;
	}
	public Double getConsultHumanCost() {
		return consultHumanCost;
	}
	public void setConsultHumanCost(Double consultHumanCost) {
		this.consultHumanCost = consultHumanCost;
	}
	public Double getConsultPayment() {
		return consultPayment;
	}
	public void setConsultPayment(Double consultPayment) {
		this.consultPayment = consultPayment;
	}
	public Double getHardwarePurchase() {
		return hardwarePurchase;
	}
	public void setHardwarePurchase(Double hardwarePurchase) {
		this.hardwarePurchase = hardwarePurchase;
	}
	public Double getExternalSoftware() {
		return externalSoftware;
	}
	public void setExternalSoftware(Double externalSoftware) {
		this.externalSoftware = externalSoftware;
	}
	public Double getInternalSoftware() {
		return internalSoftware;
	}
	public void setInternalSoftware(Double internalSoftware) {
		this.internalSoftware = internalSoftware;
	}
	public Double getProjectHumanCost() {
		return projectHumanCost;
	}
	public void setProjectHumanCost(Double projectHumanCost) {
		this.projectHumanCost = projectHumanCost;
	}
	public Double getProjectPayment() {
		return projectPayment;
	}
	public void setProjectPayment(Double projectPayment) {
		this.projectPayment = projectPayment;
	}
	public Long getStatWeek() {
		return statWeek;
	}
	public void setStatWeek(Long statWeek) {
		this.statWeek = statWeek;
	}
	public ZonedDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "ContractMonthlyStatVo [id=" + id + ", contractId=" + contractId + ", serialNum=" + serialNum + ", name="
				+ name + ", finishRate=" + finishRate + ", receiveTotal=" + receiveTotal + ", costTotal=" + costTotal
				+ ", grossProfit=" + grossProfit + ", salesHumanCost=" + salesHumanCost + ", salesPayment="
				+ salesPayment + ", consultHumanCost=" + consultHumanCost + ", consultPayment=" + consultPayment
				+ ", hardwarePurchase=" + hardwarePurchase + ", externalSoftware=" + externalSoftware
				+ ", internalSoftware=" + internalSoftware + ", projectHumanCost=" + projectHumanCost
				+ ", projectPayment=" + projectPayment + ", statWeek=" + statWeek + ", createTime=" + createTime + "]";
	}
	
}
