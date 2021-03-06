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
 * 销售年指标信息
 */
@Entity
@Table(name = "w_sales_annual_index")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "salesannualindex")
public class SalesAnnualIndex implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 员工主键、
	 */
	@Column(name = "user_id")
	private Long userId;
	
	/**
	 * 员工姓名、
	 */
	@Column(name = "user_name")
	private String userName;
	
	/**
	 * 所属年份、
	 */
	@Column(name = "stat_year")
	private Long statYear;
	
	/**
	 * 年指标
	 */
	@Column(name = "annual_index")
	private Double annualIndex;
	
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getStatYear() {
		return statYear;
	}

	public void setStatYear(Long statYear) {
		this.statYear = statYear;
	}

	public Double getAnnualIndex() {
		return annualIndex;
	}

	public void setAnnualIndex(Double annualIndex) {
		this.annualIndex = annualIndex;
	}

	@Override
	public String toString() {
		return "SalesAnnualIndex [id=" + id + ", userId=" + userId + ", userName=" + userName + ", statYear=" + statYear
				+ ", annualIndex=" + annualIndex + ", creator=" + creator + ", createTime=" + createTime + ", updator="
				+ updator + ", updateTime=" + updateTime + "]";
	}
	
}
