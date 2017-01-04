package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同月统计
 */
@Entity
@Table(name = "w_contract_monthly_stat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractMonthlyStat")
public class ContractMonthlyStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 合同主键
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 完成率（从合同中获取或者从统计时间前最后一条完成详情中获取）
     */
    @Column(name = "finish_rate")
    private Double finishRate;
    /**
     * 合同回款总额（从合同中获取或者从统计时间前所有回款详情中相加）
     */
    @Column(name = "receive_total")
    private Double receiveTotal;
    /**
     * 所有成本(下面所有成本相加+公摊成本+税金)
     */
    @Column(name = "cost_total")
    private Double costTotal;
    /**
     * 合同毛利（回款总额-所有成本）
     */
    @Column(name = "gross_profit")
    private Double grossProfit;
    /**
     * 销售人工成本
     */
    @Column(name = "sales_human_cost")
    private Double salesHumanCost;
    /**
     * 销售报销成本
     */
    @Column(name = "sales_payment")
    private Double salesPayment;
    /**
     * 咨询人工成本
     */
    @Column(name = "consult_human_cost")
    private Double consultHumanCost;
    /**
     * 咨询报销成本
     */
    @Column(name = "consult_payment")
    private Double consultPayment;
    /**
     * 硬件采购成本
     */
    @Column(name = "hardware_purchase")
    private Double hardwarePurchase;
    /**
     * 外部软件采购成本
     */
    @Column(name = "external_software")
    private Double externalSoftware;
    /**
     * 内容软件采购成本
     */
    @Column(name = "internal_software")
    private Double internalSoftware;
    /**
     * 项目人工成本
     */
    @Column(name = "project_human_cost")
    private Double projectHumanCost;
    /**
     * 项目报销成本
     */
    @Column(name = "project_payment")
    private Double projectPayment;
    /**
     * 统计月(或周),格式:201612
     */
    @Column(name = "stat_week")
    private Long statWeek;
    /**
     * 统计日期，当前时间
     */
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public ContractMonthlyStat contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Double getFinishRate() {
		return finishRate;
	}

	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}

	public Double getReceiveTotal() {
        return receiveTotal;
    }

    public ContractMonthlyStat receiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
        return this;
    }

    public void setReceiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public Double getCostTotal() {
        return costTotal;
    }

    public ContractMonthlyStat costTotal(Double costTotal) {
        this.costTotal = costTotal;
        return this;
    }

    public void setCostTotal(Double costTotal) {
        this.costTotal = costTotal;
    }

    public Double getGrossProfit() {
        return grossProfit;
    }

    public ContractMonthlyStat grossProfit(Double grossProfit) {
        this.grossProfit = grossProfit;
        return this;
    }

    public void setGrossProfit(Double grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Double getSalesHumanCost() {
        return salesHumanCost;
    }

    public ContractMonthlyStat salesHumanCost(Double salesHumanCost) {
        this.salesHumanCost = salesHumanCost;
        return this;
    }

    public void setSalesHumanCost(Double salesHumanCost) {
        this.salesHumanCost = salesHumanCost;
    }

    public Double getSalesPayment() {
        return salesPayment;
    }

    public ContractMonthlyStat salesPayment(Double salesPayment) {
        this.salesPayment = salesPayment;
        return this;
    }

    public void setSalesPayment(Double salesPayment) {
        this.salesPayment = salesPayment;
    }

    public Double getConsultHumanCost() {
        return consultHumanCost;
    }

    public ContractMonthlyStat consultHumanCost(Double consultHumanCost) {
        this.consultHumanCost = consultHumanCost;
        return this;
    }

    public void setConsultHumanCost(Double consultHumanCost) {
        this.consultHumanCost = consultHumanCost;
    }

    public Double getConsultPayment() {
        return consultPayment;
    }

    public ContractMonthlyStat consultPayment(Double consultPayment) {
        this.consultPayment = consultPayment;
        return this;
    }

    public void setConsultPayment(Double consultPayment) {
        this.consultPayment = consultPayment;
    }

    public Double getHardwarePurchase() {
        return hardwarePurchase;
    }

    public ContractMonthlyStat hardwarePurchase(Double hardwarePurchase) {
        this.hardwarePurchase = hardwarePurchase;
        return this;
    }

    public void setHardwarePurchase(Double hardwarePurchase) {
        this.hardwarePurchase = hardwarePurchase;
    }

    public Double getExternalSoftware() {
        return externalSoftware;
    }

    public ContractMonthlyStat externalSoftware(Double externalSoftware) {
        this.externalSoftware = externalSoftware;
        return this;
    }

    public void setExternalSoftware(Double externalSoftware) {
        this.externalSoftware = externalSoftware;
    }

    public Double getInternalSoftware() {
        return internalSoftware;
    }

    public ContractMonthlyStat internalSoftware(Double internalSoftware) {
        this.internalSoftware = internalSoftware;
        return this;
    }

    public void setInternalSoftware(Double internalSoftware) {
        this.internalSoftware = internalSoftware;
    }

    public Double getProjectHumanCost() {
        return projectHumanCost;
    }

    public ContractMonthlyStat projectHumanCost(Double projectHumanCost) {
        this.projectHumanCost = projectHumanCost;
        return this;
    }

    public void setProjectHumanCost(Double projectHumanCost) {
        this.projectHumanCost = projectHumanCost;
    }

    public Double getProjectPayment() {
        return projectPayment;
    }

    public ContractMonthlyStat projectPayment(Double projectPayment) {
        this.projectPayment = projectPayment;
        return this;
    }

    public void setProjectPayment(Double projectPayment) {
        this.projectPayment = projectPayment;
    }

    public Long getStatWeek() {
        return statWeek;
    }

    public ContractMonthlyStat statWeek(Long statWeek) {
        this.statWeek = statWeek;
        return this;
    }

    public void setStatWeek(Long statWeek) {
        this.statWeek = statWeek;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractMonthlyStat createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractMonthlyStat contractWeeklyStat = (ContractMonthlyStat) o;
        if (contractWeeklyStat.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractWeeklyStat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractWeeklyStat{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", receiveTotal='" + receiveTotal + "'" +
            ", costTotal='" + costTotal + "'" +
            ", grossProfit='" + grossProfit + "'" +
            ", salesHumanCost='" + salesHumanCost + "'" +
            ", salesPayment='" + salesPayment + "'" +
            ", consultHumanCost='" + consultHumanCost + "'" +
            ", consultPayment='" + consultPayment + "'" +
            ", hardwarePurchase='" + hardwarePurchase + "'" +
            ", externalSoftware='" + externalSoftware + "'" +
            ", internalSoftware='" + internalSoftware + "'" +
            ", projectHumanCost='" + projectHumanCost + "'" +
            ", projectPayment='" + projectPayment + "'" +
            ", statWeek='" + statWeek + "'" +
            ", createTime='" + createTime + "'" +
            '}';
    }
}
