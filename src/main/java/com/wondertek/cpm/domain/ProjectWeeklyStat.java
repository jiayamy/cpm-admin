package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 项目周统计
 */
@Entity
@Table(name = "w_project_weekly_stat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectweeklystat")
public class ProjectWeeklyStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;			//描述同项目月报ProjectMonthlyStat

    @Column(name = "finish_rate")
    private Double finishRate;
    
    @Column(name = "human_cost")
    private Double humanCost;

    @Column(name = "payment_")
    private Double payment;

    @Column(name = "stat_week")
    private Long statWeek;				//周所在周日，格式20161224

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectWeeklyStat projectId(Long projectId) {
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

    public ProjectWeeklyStat humanCost(Double humanCost) {
        this.humanCost = humanCost;
        return this;
    }

    public void setHumanCost(Double humanCost) {
        this.humanCost = humanCost;
    }

    public Double getPayment() {
        return payment;
    }

    public ProjectWeeklyStat payment(Double payment) {
        this.payment = payment;
        return this;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public Long getStatWeek() {
        return statWeek;
    }

    public ProjectWeeklyStat statWeek(Long statWeek) {
        this.statWeek = statWeek;
        return this;
    }

    public void setStatWeek(Long statWeek) {
        this.statWeek = statWeek;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectWeeklyStat createTime(ZonedDateTime createTime) {
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
        ProjectWeeklyStat projectWeeklyStat = (ProjectWeeklyStat) o;
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
            '}';
    }
}
