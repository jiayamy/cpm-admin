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
 * 奖金总表
 */
@Entity
@Table(name = "w_bonus")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "bonus")
public class Bonus implements Serializable{
	
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
	 * 当期销售奖金(2.10的本期奖金)、
	 */
	@Column(name = "sales_bonus")
	private Double salesBonus;
	
	/**
	 * 合同金额、
	 */
	@Column(name = "contract_amount")
	private Double contractAmount;
	
	/**
	 * 当期项目奖金（2.12之和）、
	 */
	@Column(name = "project_bonus")
	private Double projectBonus;
	
	/**
	 * 当期业务咨询奖金(2.11的本期奖金)、
	 */
	@Column(name = "consultants_bonus")
	private Double consultantsBonus;
	
	/**
	 * 奖金合计（销售+项目+咨询奖金）
	 */
	@Column(name = "bonus_total")
	private Double bonusTotal;
	
	@Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

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
