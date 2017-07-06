package com.wondertek.cpm.domain.vo;

import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.PurchaseItem;

/**
 * 采购子项
 */
public class PurchaseItemVo  {

    private Long id;
    /**
     * 合同主键 
     */
    private Long contractId;
    /**
     * 合同编号
     */
    private String contractNum;
    /**
     * 合同名称 
     */
    private String contractName;
    /**
     * 合同预算主键
     */
    private Long budgetId;
    /**
     * 采购单名称 
     */
    private String budgetName;
    /**
     * 产品定价单主键 
     */
    private Long productPriceId;
	/**
     * 采购项目----采购的是什么？用户填写后，可以点击“参考价”显示该采购项目的产品定价单
     */
    private String name;
    /**
     * 采购数量
     */
    private Integer quantity;
    /**
     * 采购单价
     */
    private Double price;
    /**
     * 采购单位
     */
    private String units;
    /**
     * 采购类型（硬件/软件）
     */
    private Integer type;
    /**
     * 采购来源（内部采购/外部采购）
     */
    private Integer source;
    /**
     * 采购方（从哪里采购的）
     */
    private String purchaser;
    /**
     * 采购总金额（可以填写，也可以通过修改采购数量和采购单价相乘）
     */
    private Double totalAmount;
    /**
     * 状态（1可用，2删除）
     */
    private Integer status;

    private String creator;

    private ZonedDateTime createTime;

    private String updator;

    private ZonedDateTime updateTime;

    
    public PurchaseItemVo() {
	}
    
    public PurchaseItemVo(PurchaseItem item ,String contractNum,String contractName, String budgetName) {
    	this.id = item.getId();
    	this.contractId = item.getContractId();
    	this.budgetId = item.getBudgetId();
    	this.productPriceId = item.getProductPriceId();
    	this.name = item.getName();
    	this.quantity = item.getQuantity();
    	this.price = item.getPrice();
    	this.units = item.getUnits();
    	this.type = item.getType();
    	this.source = item.getSource();
    	this.purchaser = item.getPurchaser();
    	this.totalAmount = item.getTotalAmount();
    	this.status = item.getStatus();
    	this.creator = item.getCreator();
    	this.createTime = item.getCreateTime();
    	this.updator = item.getUpdator();
    	this.updateTime = item.getUpdateTime();
    	
    	this.contractNum = contractNum;
    	this.contractName = contractName;
    	
    	this.budgetName = budgetName;
	}
    
    public Long getId() {
        return id;
    }

  public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public PurchaseItemVo contractId(Long contractId) {
        this.contractId = contractId;
        return this;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public PurchaseItemVo budgetId(Long budgetId) {
        this.budgetId = budgetId;
        return this;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getName() {
        return name;
    }

    public PurchaseItemVo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public PurchaseItemVo quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public PurchaseItemVo price(Double price) {
        this.price = price;
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnits() {
        return units;
    }

    public PurchaseItemVo units(String units) {
        this.units = units;
        return this;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getType() {
        return type;
    }

    public PurchaseItemVo type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public PurchaseItemVo source(Integer source) {
        this.source = source;
        return this;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public PurchaseItemVo purchaser(String purchaser) {
        this.purchaser = purchaser;
        return this;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public PurchaseItemVo totalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public PurchaseItemVo status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public PurchaseItemVo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public PurchaseItemVo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public PurchaseItemVo updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public PurchaseItemVo updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getBudgetName() {
		return budgetName;
	}

	public void setBudgetName(String budgetName) {
		this.budgetName = budgetName;
	}
	public String getContractNum() {
		return contractNum;
	}
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}
	public Long getProductPriceId() {
		return productPriceId;
	}
	public void setProductPriceId(Long productPriceId) {
		this.productPriceId = productPriceId;
	}

	@Override
	public String toString() {
		return "PurchaseItemVo [id=" + id + ", contractId=" + contractId + ", contractNum=" + contractNum
				+ ", contractName=" + contractName + ", budgetId=" + budgetId + ", budgetName=" + budgetName
				+ ", productPriceId=" + productPriceId + ", name=" + name + ", quantity=" + quantity + ", price="
				+ price + ", units=" + units + ", type=" + type + ", source=" + source + ", purchaser=" + purchaser
				+ ", totalAmount=" + totalAmount + ", status=" + status + ", creator=" + creator + ", createTime="
				+ createTime + ", updator=" + updator + ", updateTime=" + updateTime + "]";
	}
	
}
