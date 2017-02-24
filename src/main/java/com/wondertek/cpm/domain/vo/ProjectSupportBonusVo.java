package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * 项目支撑奖金
 */
public class ProjectSupportBonusVo {
	

    private Long id;
    
    /**
     * 合同编号
     * 
     */
	private String serialNum;
	/**
     * 部门类型
     * 
     */
	private Long deptId;
	
	private String deptTypeName;
	/**
	 * 统计日期、
	 */
	private Long statWeek;
	
	/**
	 * 合同主键、
	 */
	private Long contractId;
	
	/**
	 * 部门类型主键（走项目所属部门的部门类型）、
	 */
	private Long deptType;
	
	/**
	 * 员工主键、
	 */
	private Long pmId;
	
	/**
	 * 项目经理姓名、
	 */
	private String pmName;
	
	/**
	 * 项目确认交付时间（项目的结束到开始日期）、
	 */
	private Integer deliveryTime;
	
	/**
	 * 验收节点（走合同的完成率）、
	 */
	private Double acceptanceRate;
	
	/**
	 * 计划天数（项目确认交付时间*验收节点）、
	 */
	private Double planDays;
	
	/**
	 * 实际使用天数（项目结项日期（状态为已结项的更新时间）或统计时间-项目开始日期）、
	 */
	private Integer realDays;
	
	/**
	 * 奖金调节比率（计划天数/实际使用天数-1）、
	 */
	private Double bonusAdjustRate;
	
	/**
	 * 奖金比率（2.3中对应部门类型的提成比率）、
	 */
	private Double bonusRate;
	
	/**
	 * 奖金确认比例（奖金比例*(1+奖金调节比例)*验收节点）、
	 */
	private Double bonusAcceptanceRate;
	
	/**
	 * 合同金额
	 */
	private Double contractAmount;
	
	/**
	 * 税率
	 */
	private Double taxRate;
	
	/**
	 * 奖金基数（现在是走可确认收入-成本）、
	 */
	private Double bonusBasis;
	
	/**
	 * 当期奖金（奖金确认比例*奖金基数）
	 */
	private Double currentBonus;
	
    private String creator;

    private ZonedDateTime createTime;
    
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

	public Long getDeptType() {
		return deptType;
	}

	public void setDeptType(Long deptType) {
		this.deptType = deptType;
	}

	public Long getPmId() {
		return pmId;
	}

	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}

	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	public Integer getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Integer deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Double getAcceptanceRate() {
		return acceptanceRate;
	}

	public void setAcceptanceRate(Double acceptanceRate) {
		this.acceptanceRate = acceptanceRate;
	}

	public Double getPlanDays() {
		return planDays;
	}

	public void setPlanDays(Double planDays) {
		this.planDays = planDays;
	}

	public Integer getRealDays() {
		return realDays;
	}

	public void setRealDays(Integer realDays) {
		this.realDays = realDays;
	}

	public Double getBonusAdjustRate() {
		return bonusAdjustRate;
	}

	public void setBonusAdjustRate(Double bonusAdjustRate) {
		this.bonusAdjustRate = bonusAdjustRate;
	}

	public Double getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(Double bonusRate) {
		this.bonusRate = bonusRate;
	}

	public Double getBonusAcceptanceRate() {
		return bonusAcceptanceRate;
	}

	public void setBonusAcceptanceRate(Double bonusAcceptanceRate) {
		this.bonusAcceptanceRate = bonusAcceptanceRate;
	}
	
	public Double getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(Double contractAmount) {
		this.contractAmount = contractAmount;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getBonusBasis() {
		return bonusBasis;
	}

	public void setBonusBasis(Double bonusBasis) {
		this.bonusBasis = bonusBasis;
	}

	public Double getCurrentBonus() {
		return currentBonus;
	}

	public void setCurrentBonus(Double currentBonus) {
		this.currentBonus = currentBonus;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptTypeName() {
		return deptTypeName;
	}

	public void setDeptTypeName(String deptTypeName) {
		this.deptTypeName = deptTypeName;
	}
	
}
