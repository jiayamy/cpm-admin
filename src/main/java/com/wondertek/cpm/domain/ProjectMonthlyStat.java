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
 * 项目月统计
 */
@Entity
@Table(name = "w_project_monthly_stat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectmonthlystat")
public class ProjectMonthlyStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 项目主键
     */
    @Column(name = "project_id")
    private Long projectId;
    /**
     * 完成率
     */
    @Column(name = "finish_rate")
    private Double finishRate;
    /**
     * 项目人工成本
     */
    @Column(name = "human_cost")
    private Double humanCost;
    /**
     * 项目报销成本
     */
    @Column(name = "payment_")
    private Double payment;
    /**
     * 统计月(或周)，比如201612
     */
    @Column(name = "stat_week")
    private Long statWeek;
    /**
     * 统计日期
     */
    @Column(name = "create_time")
    private ZonedDateTime createTime;
    
    /**
     * 项目总工时
     */
    @Column(name = "total_input")
    private Double totalInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectMonthlyStat projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Double getFinishRate() {
		return finishRate;
	}

	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}

	public Double getHumanCost() {
        return humanCost;
    }

    public ProjectMonthlyStat humanCost(Double humanCost) {
        this.humanCost = humanCost;
        return this;
    }

    public void setHumanCost(Double humanCost) {
        this.humanCost = humanCost;
    }

    public Double getPayment() {
        return payment;
    }

    public ProjectMonthlyStat payment(Double payment) {
        this.payment = payment;
        return this;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public Long getStatWeek() {
        return statWeek;
    }

    public ProjectMonthlyStat statWeek(Long statWeek) {
        this.statWeek = statWeek;
        return this;
    }

    public void setStatWeek(Long statWeek) {
        this.statWeek = statWeek;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectMonthlyStat createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public Double getTotalInput() {
		return totalInput;
	}

	public void setTotalInput(Double totalInput) {
		this.totalInput = totalInput;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectMonthlyStat projectWeeklyStat = (ProjectMonthlyStat) o;
        if (projectWeeklyStat.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectWeeklyStat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectWeeklyStat{" +
            "id=" + id +
            ", projectId='" + projectId + "'" +
            ", humanCost='" + humanCost + "'" +
            ", payment='" + payment + "'" +
            ", statWeek='" + statWeek + "'" +
            ", createTime='" + createTime + "'" +
            ", totalInput='" + totalInput + "'" +
            '}';
    }
}
