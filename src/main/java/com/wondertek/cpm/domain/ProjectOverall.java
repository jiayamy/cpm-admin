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
 * 项目总体情况控制表
 */
@Entity
@Table(name = "w_project_overall")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectoverall")
public class ProjectOverall implements Serializable{
	
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
	 * 合同负责人（有销售就是销售，没销售就是咨询）、
	 */
	@Column(name = "contract_response")
	private Long contractResponse;
	
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
	 * 可确认收入（合同金额/(1+税率)）、
	 */
	@Column(name = "identifiable_income")
	private Double identifiableIncome;
	
	/**
	 * 合同完成节点（合同上的完成率）、
	 */
	@Column(name = "contract_finish_rate")
	private Double contractFinishRate;
	
	/**
	 * 收入确认（可确认收入*合同完成节点）、
	 */
	@Column(name = "acceptance_income")
	private Double acceptanceIncome;
	
	/**
	 * 收款金额(收款记录相加总额，同2.10)、
	 */
	@Column(name = "receive_total")
	private Double receiveTotal;
	
	/**
	 * 应收账款（合同金额*合同完成节点-收款金额）、
	 */
	@Column(name = "receivable_account")
	private Double receivableAccount;
	
	/**
	 * 公摊成本（收款金额*合同上的公摊比例）、
	 */
	@Column(name = "share_cost")
	private Double shareCost;
	
	/**
	 * 第三方采购（外部采购记录之和、同2.10）、
	 */
	@Column(name = "third_party_purchase")
	private Double thirdPartyPurchase;
	
	/**
	 * 内部采购总额（2.8的记录之和、同2.10）、
	 */
	@Column(name = "internal_purchase")
	private Double internalPurchase;
	
	/**
	 * 奖金(2.13奖金合计)、
	 */
	@Column(name = "bonus_")
	private Double bonus;
	
	/**
	 * 毛利（可确认收入*合同完成节点-公摊成本-第三方采购-内部采购总额-奖金）、、
	 */
	@Column(name = "gross_profit")
	private Double grossProfit;
	
	/**
	 * 毛利率（毛利/（可确认收入*合同完成节点））
	 */
	@Column(name = "gross_profit_rate")
	private Double grossProfitRate;
	
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

	public Long getContractResponse() {
		return contractResponse;
	}

	public void setContractResponse(Long contractResponse) {
		this.contractResponse = contractResponse;
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

	public Double getIdentifiableIncome() {
		return identifiableIncome;
	}

	public void setIdentifiableIncome(Double identifiableIncome) {
		this.identifiableIncome = identifiableIncome;
	}

	public Double getContractFinishRate() {
		return contractFinishRate;
	}

	public void setContractFinishRate(Double contractFinishRate) {
		this.contractFinishRate = contractFinishRate;
	}

	public Double getAcceptanceIncome() {
		return acceptanceIncome;
	}

	public void setAcceptanceIncome(Double acceptanceIncome) {
		this.acceptanceIncome = acceptanceIncome;
	}

	public Double getReceiveTotal() {
		return receiveTotal;
	}

	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}

	public Double getReceivableAccount() {
		return receivableAccount;
	}

	public void setReceivableAccount(Double receivableAccount) {
		this.receivableAccount = receivableAccount;
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

	public Double getInternalPurchase() {
		return internalPurchase;
	}

	public void setInternalPurchase(Double internalPurchase) {
		this.internalPurchase = internalPurchase;
	}

	public Double getBonus() {
		return bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	public Double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}

	public Double getGrossProfitRate() {
		return grossProfitRate;
	}

	public void setGrossProfitRate(Double grossProfitRate) {
		this.grossProfitRate = grossProfitRate;
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
