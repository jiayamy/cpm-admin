package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 员工成本信息
 */
@Entity
@Table(name = "w_user_cost")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "usercost")
public class UserCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 员工主键
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     * 员工名字
     */
    @Column(name = "user_name")
    private String userName;
    /**
     * 所属年月，比如201612
     */
    @Column(name = "cost_month")
    private Long costMonth;
    /**
     * 内部成本，单位元
     */
    @Column(name = "internal_cost")
    private Double internalCost;
    /**
     * 外部成本，单位元
     */
    @Column(name = "external_cost")
    private Double externalCost;
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
    
    @Column(name = "sal_")
    private Double sal;
    
    @Column(name = "social_security")
    private Double socialSecurity;
    
    @Column(name = "fund_")
    private Double fund;

    public Double getSal() {
		return sal;
	}
    
    public UserCost sal(Double sal) {
        this.sal = sal;
        return this;
    }

	public void setSal(Double sal) {
		this.sal = sal;
	}

	public Double getSocialSecurity() {
		return socialSecurity;
	}
	
	public UserCost socialSecurity(Double socialSecurity) {
        this.socialSecurity = socialSecurity;
        return this;
    }

	public void setSocialSecurity(Double socialSecurity) {
		this.socialSecurity = socialSecurity;
	}

	public Double getFund() {
		return fund;
	}
	
	public UserCost fund(Double fund) {
        this.fund = fund;
        return this;
    }

	public void setFund(Double fund) {
		this.fund = fund;
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

    public UserCost userId(Long userId) {
        this.userId = userId;
        return this;
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

	public Long getCostMonth() {
        return costMonth;
    }

    public UserCost costMonth(Long costMonth) {
        this.costMonth = costMonth;
        return this;
    }

    public void setCostMonth(Long costMonth) {
        this.costMonth = costMonth;
    }

    public Double getInternalCost() {
        return internalCost;
    }

    public UserCost internalCost(Double internalCost) {
        this.internalCost = internalCost;
        return this;
    }

    public void setInternalCost(Double internalCost) {
        this.internalCost = internalCost;
    }

    public Double getExternalCost() {
        return externalCost;
    }

    public UserCost externalCost(Double externalCost) {
        this.externalCost = externalCost;
        return this;
    }

    public void setExternalCost(Double externalCost) {
        this.externalCost = externalCost;
    }

    public Integer getStatus() {
        return status;
    }

    public UserCost status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public UserCost creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public UserCost createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public UserCost updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public UserCost updateTime(ZonedDateTime updateTime) {
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
        UserCost userCost = (UserCost) o;
        if (userCost.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, userCost.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "UserCost [id=" + id + ", userId=" + userId + ", userName=" + userName + ", costMonth=" + costMonth
				+ ", internalCost=" + internalCost + ", externalCost=" + externalCost + ", status=" + status
				+ ", creator=" + creator + ", createTime=" + createTime + ", updator=" + updator + ", updateTime="
				+ updateTime + ", sal=" + sal + ", socialSecurity=" + socialSecurity + ", fund=" + fund + "]";
	}
}
