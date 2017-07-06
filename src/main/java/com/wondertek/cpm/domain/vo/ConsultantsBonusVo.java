package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ConsultantsBonusVo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long statWeek;
	private Long contractId;
	private Double contractAmount;
	private Long consultantsId;
	private String consultantsSerialNum;	//咨询负责人工号
	private String consultantsName;
	private Double bonusBasis;
	private Double bonusRate;
	private Double consultantsShareRate;
	private Double currentBonus;
	private String creator;
	private ZonedDateTime createTime;
	private String serialNum;	//合同编号
	private Double amount;		//合同金额
	private String name;		//合同名称
	private Double accumulationBonus;	//累计已计提奖金
	
	public Double getAccumulationBonus() {
		return accumulationBonus;
	}
	public void setAccumulationBonus(Double accumulationBonus) {
		this.accumulationBonus = accumulationBonus;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStatWeek() {
		return statWeek;
	}
	public void setStatWeek(Long statWeek) {
		this.statWeek = statWeek;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public Double getContractAmount() {
		return contractAmount;
	}
	public void setContractAmount(Double contractAmount) {
		this.contractAmount = contractAmount;
	}
	public Long getConsultantsId() {
		return consultantsId;
	}
	public void setConsultantsId(Long consultantsId) {
		this.consultantsId = consultantsId;
	}
	public String getConsultantsName() {
		return consultantsName;
	}
	public void setConsultantsName(String consultantsName) {
		this.consultantsName = consultantsName;
	}
	public Double getBonusBasis() {
		return bonusBasis;
	}
	public void setBonusBasis(Double bonusBasis) {
		this.bonusBasis = bonusBasis;
	}
	public Double getBonusRate() {
		return bonusRate;
	}
	public void setBonusRate(Double bonusRate) {
		this.bonusRate = bonusRate;
	}
	public Double getConsultantsShareRate() {
		return consultantsShareRate;
	}
	public void setConsultantsShareRate(Double consultantsShareRate) {
		this.consultantsShareRate = consultantsShareRate;
	}
	public Double getCurrentBonus() {
		return currentBonus;
	}
	public void setCurrentBonus(Double currentBonus) {
		this.currentBonus = currentBonus;
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
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getConsultantsSerialNum() {
		return consultantsSerialNum;
	}
	public void setConsultantsSerialNum(String consultantsSerialNum) {
		this.consultantsSerialNum = consultantsSerialNum;
	}
	@Override
	public String toString() {
		return "ConsultantsBonusVo [id=" + id + ", statWeek=" + statWeek + ", contractId=" + contractId
				+ ", contractAmount=" + contractAmount + ", consultantsId=" + consultantsId + ", consultantsSerialNum="
				+ consultantsSerialNum + ", consultantsName=" + consultantsName + ", bonusBasis=" + bonusBasis
				+ ", bonusRate=" + bonusRate + ", consultantsShareRate=" + consultantsShareRate + ", currentBonus="
				+ currentBonus + ", creator=" + creator + ", createTime=" + createTime + ", serialNum=" + serialNum
				+ ", amount=" + amount + ", name=" + name + ", accumulationBonus=" + accumulationBonus + "]";
	}
	
}
