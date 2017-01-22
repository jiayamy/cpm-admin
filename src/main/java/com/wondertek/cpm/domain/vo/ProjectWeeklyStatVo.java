package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProjectWeeklyStatVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private Long id;

    private Long projectId;			//描述同项目月报ProjectMonthlyStat

    private Double finishRate;
    
    private Double humanCost;

    private Double payment;

    private Long statWeek;				//周所在周日，格式20161224

    private ZonedDateTime createTime;
    
    private String serialNum;
    
    private String name;
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
    
    
}
