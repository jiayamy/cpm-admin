package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 项目信息
 */
@Entity
@Table(name = "W_PROJECT_INFO")
public class ProjectInfo implements Serializable {
	private static final long serialVersionUID = 6937142017155503769L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;			//项目主键
    
    @Column(name="SERIAL_NUM")
    private String serialNum;	//项目编号

    @Column(name="CONTRACT_ID")
    private Long contractId;	//合同主键

    @Column(name="BUDGET_ID")
    private Long budgetId;		//合同预算主键
    
    @Column(name="NAME_")
    private String name;		//项目名称
    
    @Column(name="PM_")
    private String pm;			//项目经理
    
    @Column(name="DEPT_")
    private String dept;		//所属部门
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Column(name="START_DAY")
    private Date startDay;		//开始日期
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Column(name="END_DAY")
    private Date endDay;		//结束日期
    
    @Column(name="BUDGET_TOTAL")
    private Double budgetTotal;	//预算总额
    
    @Column(name="STATUS_")
    private int status;			//状态（1开发中，2结项，3删除）
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="CREATE_TIME")
    private Date createTime;	//创建时间
    
    @Column(name="CREATOR_")
    private String creator;		//创建人
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="UPDATE_TIME")
    private Date updateTime;	//更新时间
    
    @Column(name="UPDATOR_")
    private String updator;		//更新人

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(Long budgetId) {
		this.budgetId = budgetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Date getStartDay() {
		return startDay;
	}

	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}

	public Date getEndDay() {
		return endDay;
	}

	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}

	public Double getBudgetTotal() {
		return budgetTotal;
	}

	public void setBudgetTotal(Double budgetTotal) {
		this.budgetTotal = budgetTotal;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((budgetId == null) ? 0 : budgetId.hashCode());
		result = prime * result + ((budgetTotal == null) ? 0 : budgetTotal.hashCode());
		result = prime * result + ((contractId == null) ? 0 : contractId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((dept == null) ? 0 : dept.hashCode());
		result = prime * result + ((endDay == null) ? 0 : endDay.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pm == null) ? 0 : pm.hashCode());
		result = prime * result + ((serialNum == null) ? 0 : serialNum.hashCode());
		result = prime * result + ((startDay == null) ? 0 : startDay.hashCode());
		result = prime * result + status;
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((updator == null) ? 0 : updator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectInfo other = (ProjectInfo) obj;
		if (budgetId == null) {
			if (other.budgetId != null)
				return false;
		} else if (!budgetId.equals(other.budgetId))
			return false;
		if (budgetTotal == null) {
			if (other.budgetTotal != null)
				return false;
		} else if (!budgetTotal.equals(other.budgetTotal))
			return false;
		if (contractId == null) {
			if (other.contractId != null)
				return false;
		} else if (!contractId.equals(other.contractId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (dept == null) {
			if (other.dept != null)
				return false;
		} else if (!dept.equals(other.dept))
			return false;
		if (endDay == null) {
			if (other.endDay != null)
				return false;
		} else if (!endDay.equals(other.endDay))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pm == null) {
			if (other.pm != null)
				return false;
		} else if (!pm.equals(other.pm))
			return false;
		if (serialNum == null) {
			if (other.serialNum != null)
				return false;
		} else if (!serialNum.equals(other.serialNum))
			return false;
		if (startDay == null) {
			if (other.startDay != null)
				return false;
		} else if (!startDay.equals(other.startDay))
			return false;
		if (status != other.status)
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (updator == null) {
			if (other.updator != null)
				return false;
		} else if (!updator.equals(other.updator))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProjectInfo [id=" + id + ", serialNum=" + serialNum + ", contractId=" + contractId + ", budgetId="
				+ budgetId + ", name=" + name + ", pm=" + pm + ", dept=" + dept + ", startDay=" + startDay + ", endDay="
				+ endDay + ", budgetTotal=" + budgetTotal + ", status=" + status + ", createTime=" + createTime
				+ ", creator=" + creator + ", updateTime=" + updateTime + ", updator=" + updator + "]";
	}
}