package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProjectMonthlyStatVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long projectId;
	private String serialNum;
	private String name;
	private Double finishRate;
	private Double humanCost;
	private Double payment;
	private Long statWeek;
	private ZonedDateTime createTime;
	private Double totalInput;			//项目总工时
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public void setHumanCost(Double humanCost) {
		this.humanCost = humanCost;
	}
	public Double getPayment() {
		return payment;
	}
	public void setPayment(Double payment) {
		this.payment = payment;
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
	public Double getTotalInput() {
		return totalInput;
	}
	public void setTotalInput(Double totalInput) {
		this.totalInput = totalInput;
	}
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((finishRate == null) ? 0 : finishRate.hashCode());
		result = prime * result + ((humanCost == null) ? 0 : humanCost.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((payment == null) ? 0 : payment.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result + ((serialNum == null) ? 0 : serialNum.hashCode());
		result = prime * result + ((statWeek == null) ? 0 : statWeek.hashCode());
		result = prime * result + ((totalInput == null) ? 0 : totalInput.hashCode());
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
		ProjectMonthlyStatVo other = (ProjectMonthlyStatVo) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (finishRate == null) {
			if (other.finishRate != null)
				return false;
		} else if (!finishRate.equals(other.finishRate))
			return false;
		if (humanCost == null) {
			if (other.humanCost != null)
				return false;
		} else if (!humanCost.equals(other.humanCost))
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
		if (payment == null) {
			if (other.payment != null)
				return false;
		} else if (!payment.equals(other.payment))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (serialNum == null) {
			if (other.serialNum != null)
				return false;
		} else if (!serialNum.equals(other.serialNum))
			return false;
		if (statWeek == null) {
			if (other.statWeek != null)
				return false;
		} else if (!statWeek.equals(other.statWeek))
			return false;
		if (totalInput == null) {
			if (other.totalInput != null)
				return false;
		} else if (!totalInput.equals(other.totalInput))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ProjectMonthlyStatVo [id=" + id + ", projectId=" + projectId + ", serialNum=" + serialNum + ", name="
				+ name + ", finishRate=" + finishRate + ", humanCost=" + humanCost + ", payment=" + payment
				+ ", statWeek=" + statWeek + ", createTime=" + createTime + ", totalInput=" + totalInput + "]";
	}
}
