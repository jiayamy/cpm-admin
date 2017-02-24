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
 * 外部报价
 */
@Entity
@Table(name = "w_external_quotation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "externalquotation")
public class ExternalQuotation implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 级别
	 */
	@Column(name = "grade_")
	private Integer grade;
	
	/**
	 * 对外报价
	 */
	@Column(name = "external_quotation")
	private Double externalQuotation;
	
	/**
	 * 社保公积金
	 */
	@Column(name = "social_security_fund")
	private Double socialSecurityFund;
	
	/**
	 * 其他费用
	 */
	@Column(name = "other_expense")
	private Double otherExpense;
	
	/**
	 * 成本依据
	 */
	@Column(name = "cost_basis")
	private Double costBasis;
	
	/**
	 * 标准工时
	 */
	@Column(name = "hour_cost")
	private Double hourCost;
	
	@Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;
    
    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;
    
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Double getExternalQuotation() {
		return externalQuotation;
	}

	public void setExternalQuotation(Double externalQuotation) {
		this.externalQuotation = externalQuotation;
	}

	public Double getSocialSecurityFund() {
		return socialSecurityFund;
	}

	public void setSocialSecurityFund(Double socialSecurityFund) {
		this.socialSecurityFund = socialSecurityFund;
	}

	public Double getOtherExpense() {
		return otherExpense;
	}

	public void setOtherExpense(Double otherExpense) {
		this.otherExpense = otherExpense;
	}

	public Double getCostBasis() {
		return costBasis;
	}

	public void setCostBasis(Double costBasis) {
		this.costBasis = costBasis;
	}

	public Double getHourCost() {
		return hourCost;
	}

	public void setHourCost(Double hourCost) {
		this.hourCost = hourCost;
	}
	
	
}
