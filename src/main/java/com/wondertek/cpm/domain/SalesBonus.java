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
 * 销售奖金
 */
@Entity
@Table(name = "w_sales_bonus")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "salesbonus")
public class SalesBonus implements Serializable{
	
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
	 * 销售主键、
	 */
	@Column(name = "sales_man_id")
	private Long salesManId;
	
	/**
	 * 销售姓名、
	 */
	@Column(name = "sales_man")
	private String salesMan;
	
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
	 * 税率（合同上的）、
	 */
	@Column(name = "tax_rate")
	private Double taxRate;
	
	/**
	 * 收款金额(收款记录相加总额)、
	 */
	@Column(name = "receive_total")
	private Double receiveTotal;
	
	/**
	 * 税收(收款金额*税率/（1+税率）)、
	 */
	@Column(name = "taxes_")
	private Double taxes;
	
	/**
	 * 公摊成本（合同金额*公摊比例）、
	 */
	@Column(name = "share_cost")
	private Double shareCost;
	
	/**
	 * 第三方采购（外部采购成本之和）、
	 */
	@Column(name = "third_party_purchase")
	private Double thirdPartyPurchase;
	
	/**
	 * 奖金基数（收款金额-税收-公摊成本-第三方采购）、
	 */
	@Column(name = "bonus_basis")
	private Double bonusBasis;
	
	/**
	 * 奖金比例（2.3中销售提成比率）、
	 */
	@Column(name = "bonus_rate")
	private Double bonusRate;
	
	/**
	 * 本期奖金（奖金基数*奖金比例）、
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

	public Long getSalesManId() {
		return salesManId;
	}

	public void setSalesManId(Long salesManId) {
		this.salesManId = salesManId;
	}

	public String getSalesMan() {
		return salesMan;
	}

	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
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

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getReceiveTotal() {
		return receiveTotal;
	}

	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}

	public Double getTaxes() {
		return taxes;
	}

	public void setTaxes(Double taxes) {
		this.taxes = taxes;
	}

	public Double getShareCost() {
		return shareCost;
	}

	public void setShareCost(Double shareCost) {
		this.shareCost = shareCost;
	}

	public Double getThirdPartyPurchase() {
		return thirdPartyPurchase;
	}

	public void setThirdPartyPurchase(Double thirdPartyPurchase) {
		this.thirdPartyPurchase = thirdPartyPurchase;
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

	public Double getCurrentBonus() {
		return currentBonus;
	}

	public void setCurrentBonus(Double currentBonus) {
		this.currentBonus = currentBonus;
	}

}
