package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同人员信息
 */
@Entity
@Table(name = "w_contract_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractuser")
public class ContractUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "contract_id")
    private Long contractId;		//	合同主键

    @Column(name = "user_id")
    private Long userId;			//	人员ID

    @Column(name = "user_name")
    private String userName;		//	人员名称

    @Column(name = "dept_id")
    private Long deptId;			//	所属部门ID（跟着人员ID的所属部门走，方便后面填写工时详情分数据权限）

    @Column(name = "dept_")
    private String dept;			//	所属部门（跟着人员ID的所属部门走，方便后面填写工时详情分数据权限）
    
    @Column(name = "join_day")
    private Long joinDay;			//	加盟日，格式20161227

    @Column(name = "leave_day")
    private Long leaveDay;			//	离开日，格式20161227

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

    public Long getContractId() {
        return contractId;
    }

    public ContractUser contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getUserId() {
        return userId;
    }

    public ContractUser userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public ContractUser userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public ContractUser deptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Long getJoinDay() {
        return joinDay;
    }

    public ContractUser joinDay(Long joinDay) {
        this.joinDay = joinDay;
        return this;
    }

    public void setJoinDay(Long joinDay) {
        this.joinDay = joinDay;
    }

    public Long getLeaveDay() {
        return leaveDay;
    }

    public ContractUser leaveDay(Long leaveDay) {
        this.leaveDay = leaveDay;
        return this;
    }

    public void setLeaveDay(Long leaveDay) {
        this.leaveDay = leaveDay;
    }

    public String getCreator() {
        return creator;
    }

    public ContractUser creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractUser createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ContractUser updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ContractUser updateTime(ZonedDateTime updateTime) {
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
        ContractUser contractUser = (ContractUser) o;
        if (contractUser.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractUser{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", userId='" + userId + "'" +
            ", userName='" + userName + "'" +
            ", deptId='" + deptId + "'" +
            ", joinDay='" + joinDay + "'" +
            ", leaveDay='" + leaveDay + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
