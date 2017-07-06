package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.SalesBonus;

/**
 * 销售奖金
 */
public class SalesBonusVo{

	private Long id;
	/**
	 * 统计日期、
	 */
	private Long statWeek;
	/**
	 * 销售主键、
	 */
	private Long salesManId;
	/**
	 * 销售姓名、
	 */
	private String salesMan;
	/**
	 * @Transient
	 * 合同年指标
	 */
	private Double annualIndex;
	/**
	 * @Transient
	 * 合同累计完成金额
	 */
	private Double finishTotal;
	/**
	 * 所属年份
	 */
	private Long originYear;
	/**
	 * 合同主键、
	 */
	private Long contractId;
	/**
	 * @Transient
	 * 合同编号
	 */
	private String contractNum;
	/**
	 * 合同金额、
	 */
	private Double contractAmount;
	/**
	 * 税率（合同上的）、
	 */
	private Double taxRate;
	/**
	 * 收款金额(收款记录相加总额)、
	 */
	private Double receiveTotal;
	
	/**
	 * 税收(收款金额*税率/（1+税率）)、
	 */
	private Double taxes;
	/**
	 * 公摊成本（合同金额*公摊比例）、
	 */
	private Double shareCost;
	/**
	 * 第三方采购（外部采购成本之和）、
	 */
	private Double thirdPartyPurchase;
	/**
	 * 奖金基数（收款金额-税收-公摊成本-第三方采购）、
	 */
	private Double bonusBasis;
	/**
	 * 奖金比例（2.3中销售提成比率）、
	 */
	private Double bonusRate;
	/**
	 * 本期奖金（奖金基数*奖金比例）、
	 */
	private Double currentBonus;
	/**
	 * 累计已计提奖金
	 */
	private Double totalBonus;
	/**
	 * 合同累计完成率
	 */
	private Double finishRate;
	/**
	 * 可发放奖金
	 */
	private Double payBonus;
	
    private String creator;
    private ZonedDateTime createTime;
    
    public SalesBonusVo() {
		
	}

	public SalesBonusVo(SalesBonus salesBonus, String contractNum) {
		this.id = salesBonus.getId();
		this.statWeek = salesBonus.getStatWeek();
		this.salesManId = salesBonus.getSalesManId();
		this.salesMan = salesBonus.getSalesMan();
		this.originYear = salesBonus.getOriginYear();
		this.contractId = salesBonus.getContractId();
		this.contractNum = contractNum;
		this.contractAmount = salesBonus.getContractAmount();
		this.taxRate = salesBonus.getTaxRate();
		this.receiveTotal = salesBonus.getReceiveTotal();
		this.taxes = salesBonus.getTaxes();
		this.shareCost = salesBonus.getShareCost();
		this.thirdPartyPurchase = salesBonus.getThirdPartyPurchase();
		this.bonusBasis = salesBonus.getBonusBasis();
		this.bonusRate = salesBonus.getBonusRate();
		this.currentBonus = salesBonus.getCurrentBonus();
		this.creator = salesBonus.getCreator();
		this.createTime = salesBonus.getCreateTime();
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
	public Long getOriginYear() {
		return originYear;
	}
	public void setOriginYear(Long originYear) {
		this.originYear = originYear;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNum() {
		return contractNum;
	}
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
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
	public Double getTotalBonus() {
		return totalBonus;
	}
	public void setTotalBonus(Double totalBonus) {
		this.totalBonus = totalBonus;
	}
	public Double getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}
	public Double getPayBonus() {
		return payBonus;
	}
	public void setPayBonus(Double payBonus) {
		this.payBonus = payBonus;
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

	@Override
	public String toString() {
		return "SalesBonusVo [id=" + id + ", statWeek=" + statWeek + ", salesManId=" + salesManId + ", salesMan="
				+ salesMan + ", annualIndex=" + annualIndex + ", finishTotal=" + finishTotal + ", originYear="
				+ originYear + ", contractId=" + contractId + ", contractNum=" + contractNum + ", contractAmount="
				+ contractAmount + ", taxRate=" + taxRate + ", receiveTotal=" + receiveTotal + ", taxes=" + taxes
				+ ", shareCost=" + shareCost + ", thirdPartyPurchase=" + thirdPartyPurchase + ", bonusBasis="
				+ bonusBasis + ", bonusRate=" + bonusRate + ", currentBonus=" + currentBonus + ", totalBonus="
				+ totalBonus + ", finishRate=" + finishRate + ", payBonus=" + payBonus + ", creator=" + creator
				+ ", createTime=" + createTime + "]";
	}
    
}
