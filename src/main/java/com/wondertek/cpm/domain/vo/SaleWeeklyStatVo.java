package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.SaleWeeklyStat;

public class SaleWeeklyStatVo {


	private Long id;
	private Long originYear;
	private Long deptId;
	private Double annualIndex;
	private Double finishTotal;
	private Double receiveTotal;
	private Double costTotal;
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
	
	private String dept;			//部门名称

	public SaleWeeklyStatVo() {
	}

	public SaleWeeklyStatVo(SaleWeeklyStat saleWeeklyStat) {
		this.id = saleWeeklyStat.getId();
		this.originYear = saleWeeklyStat.getOriginYear();
		this.deptId = saleWeeklyStat.getDeptId();
		this.annualIndex = saleWeeklyStat.getAnnualIndex();
		this.finishTotal = saleWeeklyStat.getFinishTotal();
		this.receiveTotal = saleWeeklyStat.getReceiveTotal();
		this.costTotal = saleWeeklyStat.getCostTotal();
		this.salesHumanCost = saleWeeklyStat.getSalesHumanCost();
		this.salesPayment = saleWeeklyStat.getSalesPayment();
		this.consultHumanCost = saleWeeklyStat.getConsultHumanCost();
		this.consultPayment = saleWeeklyStat.getConsultPayment();
		this.hardwarePurchase = saleWeeklyStat.getHardwarePurchase();
		this.externalSoftware = saleWeeklyStat.getExternalSoftware();
		this.internalSoftware = saleWeeklyStat.getInternalSoftware();
		this.projectHumanCost = saleWeeklyStat.getProjectHumanCost();
		this.projectPayment = saleWeeklyStat.getProjectPayment();
		this.statWeek = saleWeeklyStat.getStatWeek();
		this.createTime = saleWeeklyStat.getCreateTime();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginYear() {
		return originYear;
	}

	public void setOriginYear(Long originYear) {
		this.originYear = originYear;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Double getAnnualIndex() {
		return annualIndex;
	}

	public void setAnnualIndex(Double annualIndex) {
		this.annualIndex = annualIndex;
	}

	public Double getFinishTotal() {
		return finishTotal;
	}

	public void setFinishTotal(Double finishTotal) {
		this.finishTotal = finishTotal;
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
	
	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}
}
