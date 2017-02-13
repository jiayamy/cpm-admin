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
 * 分成对象信息表
 */
@Entity
@Table(name = "w_share_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "shareinfo")
public class ShareInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 产品定价单主键
	 */
	@Column(name = "product_price_id")
	private Long productPriceId;
	
	/**
	 * 部门主键
	 */
	@Column(name = "dept_id")
	private Long deptId;
	
	/**
	 * 部门名称(除了实施部门外，有可能会有咨询的分成)
	 */
	@Column(name = "dept_name")
	private String deptName;
	
	/**
	 * 分成比例（所有记录之和一定要是100%）
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
	
	public Long getProductPriceId() {
		return productPriceId;
	}

	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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
