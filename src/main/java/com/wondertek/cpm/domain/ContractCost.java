package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同成本信息
 */
@Entity
@Table(name = "w_contract_cost")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractcost")
public class ContractCost implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final Integer TYPE_HUMAN_COST = 1;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 合同主键
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 合同预算主键（可能为空）
     */
    @Column(name = "budget_id")
    private Long budgetId;
    /**
     * 所属部门ID（这个是部门，跟着输入人员的部门走）
     */
    @Column(name = "dept_id")
    private Long deptId;
    /**
     * 所属部门（这个是部门，跟着输入人员的部门走）
     */
    @Column(name = "dept_")
    private String dept;
    /**
     * 名称
     */
    @Column(name = "name_")
    private String name;
    /**
     * 成本类型（1工时、2差旅、3采购、4商务）(工时不可输入，是统计新增的。其他可新增)
     */
    @Column(name = "type_")
    private Integer type;
    /**
     * 金额
     */
    @Column(name = "total_")
    private Double total;
    /**
     * 描述
     */
    @Column(name = "cost_desc")
    private String costDesc;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public ContractCost contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public ContractCost budgetId(Long budgetId) {
        this.budgetId = budgetId;
        return this;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public ContractCost deptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDept() {
        return dept;
    }

    public ContractCost dept(String dept) {
        this.dept = dept;
        return this;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getName() {
        return name;
    }

    public ContractCost name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public ContractCost type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getTotal() {
        return total;
    }

    public ContractCost total(Double total) {
        this.total = total;
        return this;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCostDesc() {
        return costDesc;
    }

    public ContractCost costDesc(String costDesc) {
        this.costDesc = costDesc;
        return this;
    }

    public void setCostDesc(String costDesc) {
        this.costDesc = costDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public ContractCost status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ContractCost creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractCost createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ContractCost updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ContractCost updateTime(ZonedDateTime updateTime) {
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
        ContractCost contractCost = (ContractCost) o;
        if (contractCost.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractCost.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractCost{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", budgetId='" + budgetId + "'" +
            ", deptId='" + deptId + "'" +
            ", dept='" + dept + "'" +
            ", name='" + name + "'" +
            ", type='" + type + "'" +
            ", total='" + total + "'" +
            ", costDesc='" + costDesc + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
