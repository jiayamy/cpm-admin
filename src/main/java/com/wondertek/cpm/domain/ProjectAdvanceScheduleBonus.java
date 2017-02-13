package com.wondertek.cpm.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

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
 * 项目提前进度奖金加成信息
 */
@Entity
@Table(name = "w_project_advance_schedule_bonus")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectadvanceschedulebonus")
public class ProjectAdvanceScheduleBonus implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 进度最小值
	 */
	@Column(name = "min_schedule")
	private Double minSchedule;
	
	/**
	 * 奖金加成比率
	 */
	@Column(name = "bonus_rate")
	private Double bonusRate;
	
	@Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;
    
    @Column(name = "updator_")
    private String updator;

    @Column(name = "update_time")
    private ZonedDateTime updateTime;
    
    public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public ZonedDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(ZonedDateTime updateTime) {
		this.updateTime = updateTime;
	}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMinSchedule() {
		return minSchedule;
	}

	public void setMinSchedule(Double minSchedule) {
		this.minSchedule = minSchedule;
	}

	public Double getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(Double bonusRate) {
		this.bonusRate = bonusRate;
	}
	
}
