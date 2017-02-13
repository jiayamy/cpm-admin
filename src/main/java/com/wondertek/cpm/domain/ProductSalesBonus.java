package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 产品销售奖金
 */
@Entity
@Table(name = "w_product_sales_bonus")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "productsalesbonus")
public class ProductSalesBonus implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 统计日期、
	 */
	@Column(name = "stat_week")
	private Long statWeek;
	
	/**
	 * 合同主键、
	 */
	@Column(name = "contract_id")
	private Long contractId;
	
	/**
	 * 部门类型主键（走项目所属部门的部门类型）、
	 */
	@Column(name = "dept_type")
	private Long deptType;
	
	/**
	 * 项目确认交付时间（项目的结束到开始日期）、
	 */
	@Column(name = "delivery_time")
	private Integer deliveryTime;
	
	/**
	 * 验收节点（走合同的完成率）、
	 */
	@Column(name = "acceptance_rate")
	private Double acceptanceRate;
	
	/**
	 * 计划天数（项目确认交付时间*验收节点）、
	 */
	@Column(name = "plan_days")
	private Double planDays;
	
	/**
	 * 实际使用天数（项目结项日期（状态为已结项的更新时间）或统计时间-项目开始日期）、
	 */
	@Column(name = "real_days")
	private Integer realDays;
	
	/**
	 * 奖金调节比率（计划天数/实际使用天数-1）、
	 */
	@Column(name = "bonus_adjust_rate")
	private Double bonusAdjustRate;
	
	/**
	 * 奖金比率（2.3中对应部门类型的提成比率）、
	 */
	@Column(name = "bonus_rate")
	private Double bonusRate;
	
	/**
	 * 奖金确认比例（奖金比例*(1+奖金调节比例)*验收节点）、
	 */
	@Column(name = "bonus_acceptance_rate")
	private Double bonusAcceptanceRate;
	
	/**
	 * 奖金基数（走2.4里面的生产毛利）、
	 */
	@Column(name = "bonus_basis")
	private Double bonusBasis;
	
	/**
	 * 当期奖金（奖金确认比例*奖金基数）
	 */
	@Column(name = "current_bonus")
	private Double currentBonus;
	
	@Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
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
	
}
