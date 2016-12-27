package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同周统计
 */
@Entity
@Table(name = "w_contract_weekly_stat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractweeklystat")
public class ContractWeeklyStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "contract_id")
    private Double contractId;			//字段描述同ContractMonthlyStat

    @Column(name = "finish_rate")
    private Double finishRate;
    
    @Column(name = "receive_total")
    private Double receiveTotal;

    @Column(name = "cost_total")
    private Double costTotal;

    @Column(name = "gross_profit")
    private Double grossProfit;

    @Column(name = "sales_human_cost")
    private Double salesHumanCost;

    @Column(name = "sales_payment")
    private Double salesPayment;

    @Column(name = "consult_human_cost")
    private Double consultHumanCost;

    @Column(name = "consult_payment")
    private Double consultPayment;

    @Column(name = "hardware_purchase")
    private Double hardwarePurchase;

    @Column(name = "external_software")
    private Double externalSoftware;

    @Column(name = "internal_software")
    private Double internalSoftware;

    @Column(name = "project_human_cost")
    private Double projectHumanCost;

    @Column(name = "project_payment")
    private Double projectPayment;

    @Column(name = "stat_week")
    private Long statWeek;					//格式 20161224，周所在周日

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getContractId() {
        return contractId;
    }

    public ContractWeeklyStat contractId(Double contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Double contractId) {
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

    public ContractWeeklyStat receiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
        return this;
    }

    public void setReceiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public Double getCostTotal() {
        return costTotal;
    }

    public ContractWeeklyStat costTotal(Double costTotal) {
        this.costTotal = costTotal;
        return this;
    }

    public void setCostTotal(Double costTotal) {
        this.costTotal = costTotal;
    }

    public Double getGrossProfit() {
        return grossProfit;
    }

    public ContractWeeklyStat grossProfit(Double grossProfit) {
        this.grossProfit = grossProfit;
        return this;
    }

    public void setGrossProfit(Double grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Double getSalesHumanCost() {
        return salesHumanCost;
    }

    public ContractWeeklyStat salesHumanCost(Double salesHumanCost) {
        this.salesHumanCost = salesHumanCost;
        return this;
    }

    public void setSalesHumanCost(Double salesHumanCost) {
        this.salesHumanCost = salesHumanCost;
    }

    public Double getSalesPayment() {
        return salesPayment;
    }

    public ContractWeeklyStat salesPayment(Double salesPayment) {
        this.salesPayment = salesPayment;
        return this;
    }

    public void setSalesPayment(Double salesPayment) {
        this.salesPayment = salesPayment;
    }

    public Double getConsultHumanCost() {
        return consultHumanCost;
    }

    public ContractWeeklyStat consultHumanCost(Double consultHumanCost) {
        this.consultHumanCost = consultHumanCost;
        return this;
    }

    public void setConsultHumanCost(Double consultHumanCost) {
        this.consultHumanCost = consultHumanCost;
    }

    public Double getConsultPayment() {
        return consultPayment;
    }

    public ContractWeeklyStat consultPayment(Double consultPayment) {
        this.consultPayment = consultPayment;
        return this;
    }

    public void setConsultPayment(Double consultPayment) {
        this.consultPayment = consultPayment;
    }

    public Double getHardwarePurchase() {
        return hardwarePurchase;
    }

    public ContractWeeklyStat hardwarePurchase(Double hardwarePurchase) {
        this.hardwarePurchase = hardwarePurchase;
        return this;
    }

    public void setHardwarePurchase(Double hardwarePurchase) {
        this.hardwarePurchase = hardwarePurchase;
    }

    public Double getExternalSoftware() {
        return externalSoftware;
    }

    public ContractWeeklyStat externalSoftware(Double externalSoftware) {
        this.externalSoftware = externalSoftware;
        return this;
    }

    public void setExternalSoftware(Double externalSoftware) {
        this.externalSoftware = externalSoftware;
    }

    public Double getInternalSoftware() {
        return internalSoftware;
    }

    public ContractWeeklyStat internalSoftware(Double internalSoftware) {
        this.internalSoftware = internalSoftware;
        return this;
    }

    public void setInternalSoftware(Double internalSoftware) {
        this.internalSoftware = internalSoftware;
    }

    public Double getProjectHumanCost() {
        return projectHumanCost;
    }

    public ContractWeeklyStat projectHumanCost(Double projectHumanCost) {
        this.projectHumanCost = projectHumanCost;
        return this;
    }

    public void setProjectHumanCost(Double projectHumanCost) {
        this.projectHumanCost = projectHumanCost;
    }

    public Double getProjectPayment() {
        return projectPayment;
    }

    public ContractWeeklyStat projectPayment(Double projectPayment) {
        this.projectPayment = projectPayment;
        return this;
    }

    public void setProjectPayment(Double projectPayment) {
        this.projectPayment = projectPayment;
    }

    public Long getStatWeek() {
        return statWeek;
    }

    public ContractWeeklyStat statWeek(Long statWeek) {
        this.statWeek = statWeek;
        return this;
    }

    public void setStatWeek(Long statWeek) {
        this.statWeek = statWeek;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractWeeklyStat createTime(ZonedDateTime createTime) {
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
        ContractWeeklyStat contractWeeklyStat = (ContractWeeklyStat) o;
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
