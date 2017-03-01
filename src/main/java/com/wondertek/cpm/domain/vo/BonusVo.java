package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.Bonus;

/**
 * 奖金总表
 */
public class BonusVo {
	
    private Long id;
    
	/**
	 * 合同编号、
	 */
    private String serialNum;
	
	/**
	 * 统计日期、
	 */
	private Long statWeek;
	
	/**
	 * 合同主键、
	 */
	private Long contractId;
	
	/**
	 * 当期销售奖金(2.10的本期奖金)、
	 */
	private Double salesBonus;
	
	/**
	 * 合同金额、
	 */
	private Double contractAmount;
	
	/**
	 * 当期项目奖金（2.12之和）、
	 */
	private Double projectBonus;
	/**
	 * 当期项目实施奖金
	 */
	private Double implemtationBonus;
	/**
	 * 当期研发奖金
	 */
	private Double academicBonus;
	/**
	 * 当期业务咨询奖金(2.11的本期奖金)、
	 */
	private Double consultantsBonus;
	
	/**
	 * 奖金合计（销售+项目+咨询奖金）
	 */
	private Double bonusTotal;
	
    private String creator;

    private ZonedDateTime createTime;
    
    public BonusVo(){
    	
    }
    
    public BonusVo(Bonus bonus){
    	this.id = bonus.getId();
    	this.statWeek = bonus.getStatWeek();
    	this.contractId = bonus.getContractId();
    	this.contractAmount = bonus.getContractAmount();
    	this.salesBonus = bonus.getSalesBonus();
    	this.projectBonus = bonus.getProjectBonus();
    	this.consultantsBonus = bonus.getConsultantsBonus();
    	this.bonusTotal = bonus.getBonusTotal();
    	this.createTime = bonus.getCreateTime();
    	this.creator = bonus.getCreator();
    	
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

	public Double getSalesBonus() {
		return salesBonus;
	}

	public void setSalesBonus(Double salesBonus) {
		this.salesBonus = salesBonus;
	}

	public Double getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(Double contractAmount) {
		this.contractAmount = contractAmount;
	}

	public Double getProjectBonus() {
		return projectBonus;
	}

	public void setProjectBonus(Double projectBonus) {
		this.projectBonus = projectBonus;
	}


	public Double getImplemtationBonus() {
		return implemtationBonus;
	}

	public void setImplemtationBonus(Double implemtationBonus) {
		this.implemtationBonus = implemtationBonus;
	}

	public Double getAcademicBonus() {
		return academicBonus;
	}

	public void setAcademicBonus(Double academicBonus) {
		this.academicBonus = academicBonus;
	}

	public Double getConsultantsBonus() {
		return consultantsBonus;
	}

	public void setConsultantsBonus(Double consultantsBonus) {
		this.consultantsBonus = consultantsBonus;
	}

	public Double getBonusTotal() {
		return bonusTotal;
	}

	public void setBonusTotal(Double bonusTotal) {
		this.bonusTotal = bonusTotal;
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
    
}
