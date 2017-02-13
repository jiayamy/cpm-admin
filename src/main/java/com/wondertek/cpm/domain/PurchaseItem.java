package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 采购子项
 */
@Entity
@Table(name = "w_purchase_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "purchaseitem")
public class PurchaseItem implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Integer TYPE_HARDWARE = 1;
	public static final Integer TYPE_SOFTWARE = 2;
	public static final Integer SOURCE_INTERNAL = 1;
	public static final Integer SOURCE_EXTERNAL = 2;
	public static final Integer STATUS_VALIBLE = 1;
	public static final Integer STATUS_DELETED = 2;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 合同主键 
     */
    @Column(name = "contract_id")
    private Long contractId;
    /**
     * 合同预算主键
     */
    @Column(name = "budget_id")
    private Long budgetId;
    /**
     * 产品定价单主键
     */
    @Column(name = "product_price_id")
    private Long productPriceId;
    /**
     * 采购项目----采购的是什么？用户填写后，可以点击“参考价”显示该采购项目的产品定价单
     */
    @Column(name = "name_")
    private String name;
    /**
     * 采购数量
     */
    @Column(name = "quantity_")
    private Integer quantity;
    /**
     * 采购单价
     */
    @Column(name = "price_")
    private Double price;
    /**
     * 采购单位
     */
    @Column(name = "units_")
    private String units;
    /**
     * 采购类型（硬件/软件）
     */
    @Column(name = "type_")
    private Integer type;
    /**
     * 采购来源（内部采购/外部采购）
     */
    @Column(name = "source_")
    private Integer source;
    /**
     * 采购方（从哪里采购的）
     */
    @Column(name = "purchaser_")
    private String purchaser;
    /**
     * 采购总金额（可以填写，也可以通过修改采购数量和采购单价相乘）
     */
    @Column(name = "total_amount")
    private Double totalAmount;
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

    public PurchaseItem contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public PurchaseItem budgetId(Long budgetId) {
        this.budgetId = budgetId;
        return this;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }
    
    public Long getProductPriceId() {
		return productPriceId;
	}
    public PurchaseItem productPriceId(Long productPriceId) {
        this.productPriceId = productPriceId;
        return this;
    }

	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}
	
    public String getName() {
        return name;
    }

    public PurchaseItem name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public PurchaseItem quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public PurchaseItem price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnits() {
        return units;
    }

    public PurchaseItem units(String units) {
        this.units = units;
        return this;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getType() {
        return type;
    }

    public PurchaseItem type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public PurchaseItem source(Integer source) {
        this.source = source;
        return this;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public PurchaseItem purchaser(String purchaser) {
        this.purchaser = purchaser;
        return this;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public PurchaseItem totalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public PurchaseItem status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public PurchaseItem creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public PurchaseItem createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public PurchaseItem updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public PurchaseItem updateTime(ZonedDateTime updateTime) {
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
        PurchaseItem purchaseItem = (PurchaseItem) o;
        if (purchaseItem.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, purchaseItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PurchaseItem{" +
            "id=" + id +
            ", contractId='" + contractId + "'" +
            ", budgetId='" + budgetId + "'" +
            ", productPriceId='" + productPriceId + "'" +
            ", name='" + name + "'" +
            ", quantity='" + quantity + "'" +
            ", price='" + price + "'" +
            ", units='" + units + "'" +
            ", type='" + type + "'" +
            ", source='" + source + "'" +
            ", purchaser='" + purchaser + "'" +
            ", totalAmount='" + totalAmount + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
