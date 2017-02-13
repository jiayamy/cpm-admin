package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同预算信息，除了采购单，其他可填可不填
 * 改名为内部采购单，type默认都是3，purchaseType默认都展现
 */
@Entity
@Table(name = "w_contract_budget")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractbudget")
public class ContractBudget implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int TYPE_SALE = 1;
    public static final int TYPE_CONSULATION = 2;
    public static final int TYPE_PURCHASE = 3;
    
    public static final int PURCHASETYPE_HARDWARE = 1;
    public static final int PURCHASETYPE_SOFTWARE = 2;
    public static final int PURCHASETYPE_SERVICE = 3;
    
    public static final int STATUS_VALIDABLE = 1;
    public static final int STATUS_DELETED = 2;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 合同主键 
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 采购单名称
     */
    @Column(name = "name_")
    private String name;
    /**
     * 预算类型（1销售/2咨询/3内部采购单）
     */
    @Column(name = "type_")
    private Integer type;
    /**
     * 负责人ID（对应一个具体的员工，弹窗选择员工）
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     * 负责人名称
     */
    @Column(name = "user_name")
    private String userName;
    /**
     * 所属部门ID（跟着负责人走，用户所属部门，只展示，不可更改）
     */
    @Column(name = "dept_id")
    private Long deptId;
    /**
     * 所属部门
     */
    @Column(name = "dept_")
    private String dept;
    /**
     * 产品定价单主键
     */
    @Column(name = "product_price_id")
    private Long productPriceId;
    /**
     * 采购单类型（预算类型为内部采购单时填写，1硬件/2软件/3服务---服务可以创建项目，其他的不可以）
     */
    @Column(name = "purchase_type")
    private Integer purchaseType;
    /**
     * 预算金额
     */
    @Column(name = "budget_total")
    private Double budgetTotal;
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

    public ContractBudget contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getType() {
        return type;
    }

    public ContractBudget type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public ContractBudget userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public ContractBudget userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDept() {
        return dept;
    }

    public ContractBudget dept(String dept) {
        this.dept = dept;
        return this;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
    
    public String getName() {
        return name;
    }

    public ContractBudget Name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getPurchaseType() {
        return purchaseType;
    }

    public ContractBudget purchaseType(Integer purchaseType) {
        this.purchaseType = purchaseType;
        return this;
    }

    public void setPurchaseType(Integer purchaseType) {
        this.purchaseType = purchaseType;
    }

    public Double getBudgetTotal() {
        return budgetTotal;
    }

    public ContractBudget budgetTotal(Double budgetTotal) {
        this.budgetTotal = budgetTotal;
        return this;
    }

    public void setBudgetTotal(Double budgetTotal) {
        this.budgetTotal = budgetTotal;
    }

    public Integer getStatus() {
        return status;
    }

    public ContractBudget status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ContractBudget creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractBudget createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ContractBudget updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ContractBudget updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getProductPriceId() {
		return productPriceId;
	}

	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractBudget contractBudget = (ContractBudget) o;
        if (contractBudget.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractBudget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractBudget{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", type='" + type + "'" +
            ", userId='" + userId + "'" +
            ", userName='" + userName + "'" +
            ", dept='" + dept + "'" +
            ", purchaseType='" + purchaseType + "'" +
            ", budgetTotal='" + budgetTotal + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
