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
 * 项目成本
 */
@Entity
@Table(name = "w_project_cost")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectcost")
public class ProjectCost implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final Integer TYPE_HUMAN_COST = 1;
    public static final Integer TYPE_TRAVEL_COST = 2;
    public static final Integer TYPE_PURCHASE_COST = 3;
    public static final Integer TYPE_BUSINESS_COST = 4;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 项目主键
     */
    @Column(name = "project_id")
    private Long projectId;
    /**
     * 名称
     */
    @Column(name = "name_")
    private String name;
    /**
     * 成本类型（1工时、2差旅、3采购、4商务）(工时不可输入，是统计新增的。其他可新增)
     */
    @Column(name = "type_")
    private Integer type;
    /**
     * 成本日期
     */
    @Column(name="COST_DAY")
    private Long costDay;
    /**
     * 金额
     */
    @Column(name = "total_")
    private Double total;
    /**
     * 描述
     */
    @Column(name = "cost_desc")
    private String costDesc;
    /**
     * 状态（1可用，2删除）
     */
    @Column(name = "status_")
    private Integer status;

    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;
    
    /**
     * 工时
     */
    @Column(name = "input_")
    private Double input;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectCost projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public ProjectCost name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public ProjectCost type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCostDay() {
		return costDay;
	}

	public void setCostDay(Long costDay) {
		this.costDay = costDay;
	}

	public Double getTotal() {
        return total;
    }

    public ProjectCost total(Double total) {
        this.total = total;
        return this;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCostDesc() {
        return costDesc;
    }

    public ProjectCost costDesc(String costDesc) {
        this.costDesc = costDesc;
        return this;
    }

    public void setCostDesc(String costDesc) {
        this.costDesc = costDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public ProjectCost status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ProjectCost creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectCost createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ProjectCost updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ProjectCost updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Double getInput() {
		return input;
	}

	public void setInput(Double input) {
		this.input = input;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectCost projectCost = (ProjectCost) o;
        if (projectCost.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectCost.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectCost{" +
            "id=" + id +
            ", projectId='" + projectId + "'" +
            ", name='" + name + "'" +
            ", type='" + type + "'" +
            ", total='" + total + "'" +
            ", costDesc='" + costDesc + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            ", input='" + input + "'" +
            '}';
    }
}
