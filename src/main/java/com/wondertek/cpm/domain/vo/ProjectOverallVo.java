package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ProjectOverall;


public class ProjectOverallVo {
	 private Long id;
		
		/**
		 * 统计日期、
		 */
		private Long statWeek;
		
		/**
		 * 合同负责人（有销售就是销售，没销售就是咨询）、
		 */
		private Long contractResponse;
		
		/**
		 * 合同主键、
		 */
		private Long contractId;
		
		/**
		 * 合同金额、
		 */
		private Double contractAmount;
		
		/**
		 * 税率（合同上的）、
		 */
		private Double taxRate;
		
		/**
		 * 可确认收入（合同金额/(1+税率)）、
		 */
		private Double identifiableIncome;
		
		/**
		 * 合同完成节点（合同上的完成率）、
		 */
		private Double contractFinishRate;
		
		/**
		 * 收入确认（可确认收入*合同完成节点）、
		 */
		private Double acceptanceIncome;
		
		/**
		 * 收款金额(收款记录相加总额，同2.10)、
		 */
		private Double receiveTotal;
		
		/**
		 * 应收账款（合同金额*合同完成节点-收款金额）、
		 */
		private Double receivableAccount;
		
		/**
		 * 公摊成本（收款金额*合同上的公摊比例）、
		 */
		private Double shareCost;
		
		/**
		 * 第三方采购（外部采购记录之和、同2.10）、
		 */
		private Double thirdPartyPurchase;
		
		/**
		 * 内部采购总额（2.8的记录之和、同2.10）、
		 */
		private Double internalPurchase;
		
		/**
		 * 奖金(2.13奖金合计)、
		 */
		private Double bonus;
		
		/**
		 * 毛利（可确认收入*合同完成节点-公摊成本-第三方采购-内部采购总额-奖金）、、
		 */
		private Double grossProfit;
		
		/**
		 * 毛利率（毛利/（可确认收入*合同完成节点））
		 */
		private Double grossProfitRate;
		
	    private String creator;

	    private ZonedDateTime createTime;
	    
	    /**
		 * 合同编号
		 */
	    private String serialNum;
	    /**
		 * 销售
		 */
	    public String salesman;
	    /**
		 *咨询
		 */
	    public String consultants;
	    /**
	     * 实施成本
	     */
	    public Double implementationCost;
	    /**
	     * 研究院
	     */
	    public Double academicCost;
	    
	    public ProjectOverallVo(){
	    	
	    }
	    
	    public ProjectOverallVo(ProjectOverall projectOverall,String serialNum,Double implementationCost,Double academicCost){
	    	this.id = projectOverall.getId();
	    	this.statWeek = projectOverall.getStatWeek();
	    	this.contractResponse = projectOverall.getContractResponse();
	    	this.contractId = projectOverall.getContractId();
	    	this.contractAmount = projectOverall.getContractAmount();
	    	this.taxRate = projectOverall.getTaxRate();
	    	this.identifiableIncome = projectOverall.getIdentifiableIncome();
	    	this.contractFinishRate = projectOverall.getContractFinishRate();
	    	this.acceptanceIncome = projectOverall.getAcceptanceIncome();
	    	this.receiveTotal = projectOverall.getReceiveTotal();
	    	this.receivableAccount = projectOverall.getReceivableAccount();
	    	this.shareCost = projectOverall.getShareCost();
	    	this.thirdPartyPurchase = projectOverall.getThirdPartyPurchase();
	    	this.internalPurchase = projectOverall.getInternalPurchase();
	    	this.bonus = projectOverall.getBonus();
	    	this.grossProfit = projectOverall.getGrossProfit();
	    	this.grossProfitRate = projectOverall.getGrossProfitRate();
	    	this.createTime = projectOverall.getCreateTime();
	    	this.creator = projectOverall.getCreator();
	    	
	    	this.serialNum = serialNum;
	    	this.implementationCost = implementationCost;
	    	this.academicCost = academicCost;
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

		public String getSerialNum() {
			return serialNum;
		}

		public void setSerialNum(String serialNum) {
			this.serialNum = serialNum;
		}

		public String getSalesman() {
			return salesman;
		}

		public Double getImplementationCost() {
			return implementationCost;
		}

		public void setImplementationCost(Double implementationCost) {
			this.implementationCost = implementationCost;
		}

		public Double getAcademicCost() {
			return academicCost;
		}

		public void setAcademicCost(Double academicCost) {
			this.academicCost = academicCost;
		}

		public void setSalesman(String salesman) {
			this.salesman = salesman;
		}

		public String getConsultants() {
			return consultants;
		}

		public void setConsultants(String consultants) {
			this.consultants = consultants;
		}
	    
}
