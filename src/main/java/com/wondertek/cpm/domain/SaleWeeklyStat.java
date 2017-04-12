package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

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
 * 销售部门周统计
 *
 */
@Entity
@Table(name = "w_sale_weekly_stat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "saleweeklystat")
public class SaleWeeklyStat implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final Long TYPE_DEPT_SALE = 2L;	//销售部门
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * 年份
	 */
	@Column(name = "origin_year")
	private Long originYear;
	
	/**
	 * 销售部门
	 */
	@Column(name = "dept_id")
	private Long deptId;
	
	/**
	 * 合同年指标（该销售部门（包括子部门）该年的所有销售年指标之和）
	 */
	@Column(name = "annual_index")
	private Double annualIndex;
	
	/**
	 * 合同累计完成金额（该销售部门（包括子部门）该年新增的合同金额总和）、
	 */
	@Column(name = "finish_total")
	private Double finishTotal;
	
	/**
	 * 当年收款金额（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年收款的金额总和）
	 */
	@Column(name = "receive_total")
	private Double receiveTotal;
	
	/**
	 *  当年新增所有成本、（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年以下所有成本之和）
	 */
	@Column(name = "cost_total")
	private Double costTotal;
	
	/**
	 * 当年销售人工成本、（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年人工成本之和、以下雷同）
	 */
	@Column(name = "sales_human_cost")
	private Double salesHumanCost;
	
	/**
	 * 当年销售报销成本
	 */
	@Column(name = "sales_payment")
	private Double salesPayment;
	
	/**
	 * 当年咨询人工成本
	 */
	@Column(name = "consult_human_cost")
	private Double consultHumanCost;
	
	/**
	 * 当年咨询报销成本
	 */
	@Column(name = "consult_payment")
	private Double consultPayment;
	
	/**
	 * 当年硬件成本
	 */
	@Column(name = "hardware_purchase")
	private Double hardwarePurchase;
	
	/**
	 * 当年外部软件成本
	 */
	@Column(name = "external_software")
	private Double externalSoftware;
	
	/**
	 * 当年内部软件成本
	 */
	@Column(name = "internal_software")
	private Double internalSoftware;
	
	/**
	 * 当年项目人工成本
	 */
	@Column(name = "project_human_cost")
	private Double projectHumanCost;
	
	/**
	 * 当年项目报销成本
	 */
	@Column(name = "project_payment")
	private Double projectPayment;
	
	/**
	 * 统计周
	 */
	@Column(name = "stat_week")
	private Long statWeek;
	
	/**
	 * 统计日期
	 */
	@Column(name = "create_time")
	private ZonedDateTime createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginYear() {
		return originYear;
	}

	public void setOriginYear(Long originYear) {
		this.originYear = originYear;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
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

	public Double getReceiveTotal() {
		return receiveTotal;
	}

	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}

	public Double getCostTotal() {
		return costTotal;
	}

	public void setCostTotal(Double costTotal) {
		this.costTotal = costTotal;
	}

	public Double getSalesHumanCost() {
		return salesHumanCost;
	}

	public void setSalesHumanCost(Double salesHumanCost) {
		this.salesHumanCost = salesHumanCost;
	}

	public Double getSalesPayment() {
		return salesPayment;
	}

	public void setSalesPayment(Double salesPayment) {
		this.salesPayment = salesPayment;
	}

	public Double getConsultHumanCost() {
		return consultHumanCost;
	}

	public void setConsultHumanCost(Double consultHumanCost) {
		this.consultHumanCost = consultHumanCost;
	}

	public Double getConsultPayment() {
		return consultPayment;
	}

	public void setConsultPayment(Double consultPayment) {
		this.consultPayment = consultPayment;
	}

	public Double getHardwarePurchase() {
		return hardwarePurchase;
	}

	public void setHardwarePurchase(Double hardwarePurchase) {
		this.hardwarePurchase = hardwarePurchase;
	}

	public Double getExternalSoftware() {
		return externalSoftware;
	}

	public void setExternalSoftware(Double externalSoftware) {
		this.externalSoftware = externalSoftware;
	}

	public Double getInternalSoftware() {
		return internalSoftware;
	}

	public void setInternalSoftware(Double internalSoftware) {
		this.internalSoftware = internalSoftware;
	}

	public Double getProjectHumanCost() {
		return projectHumanCost;
	}

	public void setProjectHumanCost(Double projectHumanCost) {
		this.projectHumanCost = projectHumanCost;
	}

	public Double getProjectPayment() {
		return projectPayment;
	}

	public void setProjectPayment(Double projectPayment) {
		this.projectPayment = projectPayment;
	}

	public Long getStatWeek() {
		return statWeek;
	}

	public void setStatWeek(Long statWeek) {
		this.statWeek = statWeek;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SaleWeeklyStat saleWeeklyStat = (SaleWeeklyStat) o;
        if (saleWeeklyStat.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, saleWeeklyStat.id);
	}

	@Override
	public String toString() {
		return "SaleWeeklyStat [id=" + id + ", originYear=" + originYear + ", deptId=" + deptId + ", annualIndex="
				+ annualIndex + ", finishTotal=" + finishTotal + ", receiveTotal=" + receiveTotal + ", costTotal="
				+ costTotal + ", salesHumanCost=" + salesHumanCost + ", salesPayment=" + salesPayment
				+ ", consultHumanCost=" + consultHumanCost + ", consultPayment=" + consultPayment
				+ ", hardwarePurchase=" + hardwarePurchase + ", externalSoftware=" + externalSoftware
				+ ", internalSoftware=" + internalSoftware + ", projectHumanCost=" + projectHumanCost
				+ ", projectPayment=" + projectPayment + ", statWeek=" + statWeek + ", createTime=" + createTime + "]";
	}
}
