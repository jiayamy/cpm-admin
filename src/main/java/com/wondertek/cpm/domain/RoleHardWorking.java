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
 * 员工勤奋度信息
 */
@Entity
@Table(name = "w_role_hardworking")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "rolehardworking")
public class RoleHardWorking implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 员工ID
	 */
	@Column(name = "user_id")
	private Long userId;
	
	/**
     * 员工编号
     */
    @Column(name = "serial_num")
    private String serialNum;
    
    
    /**
     * 员工姓名
     */
    @Column(name = "last_name")
    private String roleName;
    
    /**
     * 员工勤奋度
     */
    @Column(name = "hardworking")
    private Double hardWorking;
    
    /**
     * 统计月
     */
    @Column(name = "origin_month")
    private Long originMonth;
    
  
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private ZonedDateTime createTime;
    
    
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

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public Double getHardWorking() {
		return hardWorking;
	}

	public void setHardWorking(Double hardWorking) {
		this.hardWorking = hardWorking;
	}

	public Long getOriginMonth() {
		return originMonth;
	}

	public void setOriginMonth(Long originMonth) {
		this.originMonth = originMonth;
	}


	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(ZonedDateTime createTime) {
		this.createTime = createTime;
	}


    
    
	
}
