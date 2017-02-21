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
 * 奖金公摊成本比例
 */
@Entity
@Table(name = "w_share_cost_rate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sharecostrate")
public class ShareCostRate implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 部门类型ID
	 */
	@Column(name = "dept_type")
	private Long deptType;
	
	/**
	 * 部门类型名称
	 */
	@Column(name = "dept_")
	private String dept;
	
	/**
	 * 合同类型
	 */
	@Column(name = "contract_type")
	private Long contractType;
	
	/**
	 * 公摊比例
	 */
	@Column(name = "share_rate")
	private Double shareRate;
	
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

	public Long getDeptType() {
		return deptType;
	}

	public void setDeptType(Long deptType) {
		this.deptType = deptType;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Long getContractType() {
		return contractType;
	}

	public void setContractType(Long contractType) {
		this.contractType = contractType;
	}

	public Double getShareRate() {
		return shareRate;
	}

	public void setShareRate(Double shareRate) {
		this.shareRate = shareRate;
	}

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
    
}
