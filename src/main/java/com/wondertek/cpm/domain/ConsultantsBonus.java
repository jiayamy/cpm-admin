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
 * 咨询奖金
 */
@Entity
@Table(name = "w_consultants_bonus")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "consultantsbonus")
public class ConsultantsBonus implements Serializable{
	
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
	 * 合同金额、
	 */
	@Column(name = "contract_amount")
	private Double contractAmount;
	
	/**
	 * 咨询负责人主键、
	 */
	@Column(name = "consultants_id")
	private Long consultantsId;
	
	/**
	 * 咨询负责人名称、
	 */
	@Column(name = "consultants_")
	private String consultants;
	
	/**
	 * 奖金基数（收款金额-税收-公摊成本-第三方采购-内部采购总额）、
	 */
	@Column(name = "bonus_basis")
	private Double bonusBasis;
	
	/**
	 * 奖金比例（2.3中销售提成比率）、
	 */
	@Column(name = "bonus_rate")
	private Double bonusRate;
	
	/**
	 * 项目分润比率（合同上的字段）、
	 */
	@Column(name = "consultants_share_rate")
	private Double consultantsShareRate;
	
	/**
	 * 本期奖金（奖金基数*奖金比例*分润比例）
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

	public String getConsultants() {
		return consultants;
	}

	public void setConsultants(String consultants) {
		this.consultants = consultants;
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
	
}
