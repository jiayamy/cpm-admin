package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 项目信息
 */
@Entity
@Table(name = "w_project_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectinfo")
public class ProjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_ADD = 1;
    public static final int STATUS_CLOSED = 2;
	public static final int STATUS_DELETED = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 项目编号
     */
    @Column(name = "serial_num")
    private String serialNum;
    /**
     * 合同主键 
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 合同预算主键
     */
    @Column(name = "budget_id")
    private Long budgetId;
    /**
     * 项目名称
     */
    @Column(name = "name_")
    private String name;
    /**
     * 项目经理ID（对应一个具体的员工，弹窗选择员工）
     */
    @Column(name = "pm_id")
    private Long pmId;
    /**
     * 项目经理（对应一个具体的员工，弹窗选择员工）
     */
    @Column(name = "pm_")
    private String pm;
    /**
     * 所属部门（跟着项目经理走，用户所属部门，只展示，不可更改）
     */
    @Column(name = "dept_id")
    private Long deptId;
    /**
     * 所属部门（跟着项目经理走，用户所属部门，只展示，不可更改）
     */
    @Column(name = "dept_")
    private String dept;
    /**
     * 开始日期
     */
    @Column(name = "start_day")
    private ZonedDateTime startDay;
    /**
     * 结束日期
     */
    @Column(name = "end_day")
    private ZonedDateTime endDay;
    /**
     * 预算总额
     */
    @Column(name = "budget_total")
    private Double budgetTotal;
    /**
     * 状态（1开发中/2结项/3终止）
     */
    @Column(name = "status_")
    private Integer status;
    /**
     * 完成率（只展示）
     */
    @Column(name = "finish_rate")
    private Double finishRate;
    
    @Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;

    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public ProjectInfo serialNum(String serialNum) {
        this.serialNum = serialNum;
        return this;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public Long getContractId() {
        return contractId;
    }

    public ProjectInfo contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public ProjectInfo budgetId(Long budgetId) {
        this.budgetId = budgetId;
        return this;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getName() {
        return name;
    }

    public ProjectInfo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPmId() {
		return pmId;
	}

	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getPm() {
		return pm;
	}

	public String getDept() {
		return dept;
	}

	public Double getFinishRate() {
		return finishRate;
	}

	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

    public ProjectInfo pm(String pm) {
        this.pm = pm;
        return this;
    }

    public ProjectInfo dept(String dept) {
        this.dept = dept;
        return this;
    }

    public ZonedDateTime getStartDay() {
        return startDay;
    }

    public ProjectInfo startDay(ZonedDateTime startDay) {
        this.startDay = startDay;
        return this;
    }

    public void setStartDay(ZonedDateTime startDay) {
        this.startDay = startDay;
    }

    public ZonedDateTime getEndDay() {
        return endDay;
    }

    public ProjectInfo endDay(ZonedDateTime endDay) {
        this.endDay = endDay;
        return this;
    }

    public void setEndDay(ZonedDateTime endDay) {
        this.endDay = endDay;
    }

    public Double getBudgetTotal() {
        return budgetTotal;
    }

    public ProjectInfo budgetTotal(Double budgetTotal) {
        this.budgetTotal = budgetTotal;
        return this;
    }

    public void setBudgetTotal(Double budgetTotal) {
        this.budgetTotal = budgetTotal;
    }

    public Integer getStatus() {
        return status;
    }

    public ProjectInfo status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ProjectInfo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ProjectInfo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ProjectInfo updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ProjectInfo updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectInfo projectInfo = (ProjectInfo) o;
        if (projectInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectInfo{" +
            "id=" + id +
            ", serialNum='" + serialNum + "'" +
            ", contractId='" + contractId + "'" +
            ", budgetId='" + budgetId + "'" +
            ", name='" + name + "'" +
            ", pm='" + pm + "'" +
            ", dept='" + dept + "'" +
            ", startDay='" + startDay + "'" +
            ", endDay='" + endDay + "'" +
            ", budgetTotal='" + budgetTotal + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
