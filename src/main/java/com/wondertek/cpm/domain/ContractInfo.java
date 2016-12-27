package com.wondertek.cpm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 合同信息
 */
@Entity
@Table(name = "w_contract_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contractinfo")
public class ContractInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "serial_num")
    private String serialNum;			//	合同编号

    @Column(name = "name_")
    private String name;				//	合同名称

    @Column(name = "amount_")
    private Double amount;				//	合同金额

    @Column(name = "type_")
    private Integer type;				//	合同类型（产品/外包/硬件）

    @Column(name = "is_prepared")
    private Boolean isPrepared;			//	是否预立（正式合同/预立合同），预立合同可以转正式合同

    @Column(name = "is_epibolic")
    private Boolean isEpibolic;			//	是否外包（内部合同/外包合同）--- 
    
    @Column(name = "sales_man_id")
    private Long salesmanId;			//	销售人员ID（界面选择员工信息）
    
    @Column(name = "sales_man")
    private String salesman;			//	销售人员名称
    
    @Column(name = "dept_id")
    private Long deptId;				//	销售所属部门ID
    
    @Column(name = "dept_")
    private String dept;				//	销售所属部门
    
    @Column(name = "start_day")
    private ZonedDateTime startDay;		//	开始日期,页面格式20161227

    @Column(name = "end_day")
    private ZonedDateTime endDay;		//	结束日期

    @Column(name = "tax_rate")
    private Double taxRate;				//	税率,精确小数点后2位

    @Column(name = "taxes_")
    private Double taxes;				//	税费（元）,精确小数点后2位

    @Column(name = "share_rate")
    private Double shareRate;			//	公摊比例

    @Column(name = "share_cost")
    private Double shareCost;			//	公摊成本（合同金额*公摊比例）

    @Column(name = "payment_way")
    private String paymentWay;			//	付款方式（比如“3,6,1”）

    @Column(name = "contractor_")
    private String contractor;			//	合同方（公司名称）

    @Column(name = "address_")
    private String address;				//	合同方通信地址

    @Column(name = "postcode_")
    private String postcode;			//	合同方邮编

    @Column(name = "linkman_")
    private String linkman;				//	合同方联系人

    @Column(name = "contact_dept")
    private String contactDept;			//	合同方联系部门

    @Column(name = "telephone_")
    private String telephone;			//	合同方电话

    @Column(name = "receive_total")
    private Double receiveTotal;		//	收款总金额（不展示）

    @Column(name = "finish_total")
    private Double finishTotal;		//	合同累计完成金额（不展示）
    
    @Column(name = "finish_rate")
    private Double finishRate;			//	完成率（只展示）

    @Column(name = "status_")
    private Integer status;				//	状态（可用，删除）

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

    public String getSerialNum() {
        return serialNum;
    }

    public ContractInfo serialNum(String serialNum) {
        this.serialNum = serialNum;
        return this;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getName() {
        return name;
    }

    public ContractInfo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public ContractInfo amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public ContractInfo type(Integer type) {
        this.type = type;
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean isIsPrepared() {
        return isPrepared;
    }

    public ContractInfo isPrepared(Boolean isPrepared) {
        this.isPrepared = isPrepared;
        return this;
    }

    public void setIsPrepared(Boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

    public Boolean isIsEpibolic() {
        return isEpibolic;
    }

    public ContractInfo isEpibolic(Boolean isEpibolic) {
        this.isEpibolic = isEpibolic;
        return this;
    }

    public void setIsEpibolic(Boolean isEpibolic) {
        this.isEpibolic = isEpibolic;
    }

    public Long getSalesmanId() {
		return salesmanId;
	}

	public void setSalesmanId(Long salesmanId) {
		this.salesmanId = salesmanId;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Boolean getIsPrepared() {
		return isPrepared;
	}

	public Boolean getIsEpibolic() {
		return isEpibolic;
	}

	public ZonedDateTime getStartDay() {
        return startDay;
    }

    public ContractInfo startDay(ZonedDateTime startDay) {
        this.startDay = startDay;
        return this;
    }

    public void setStartDay(ZonedDateTime startDay) {
        this.startDay = startDay;
    }

    public ZonedDateTime getEndDay() {
        return endDay;
    }

    public ContractInfo endDay(ZonedDateTime endDay) {
        this.endDay = endDay;
        return this;
    }

    public void setEndDay(ZonedDateTime endDay) {
        this.endDay = endDay;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public ContractInfo taxRate(Double taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Double getTaxes() {
        return taxes;
    }

    public ContractInfo taxes(Double taxes) {
        this.taxes = taxes;
        return this;
    }

    public void setTaxes(Double taxes) {
        this.taxes = taxes;
    }

    public Double getShareRate() {
        return shareRate;
    }

    public ContractInfo shareRate(Double shareRate) {
        this.shareRate = shareRate;
        return this;
    }

    public void setShareRate(Double shareRate) {
        this.shareRate = shareRate;
    }

    public Double getShareCost() {
        return shareCost;
    }

    public ContractInfo shareCost(Double shareCost) {
        this.shareCost = shareCost;
        return this;
    }

    public void setShareCost(Double shareCost) {
        this.shareCost = shareCost;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public ContractInfo paymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
        return this;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public String getContractor() {
        return contractor;
    }

    public ContractInfo contractor(String contractor) {
        this.contractor = contractor;
        return this;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getAddress() {
        return address;
    }

    public ContractInfo address(String address) {
        this.address = address;
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public ContractInfo postcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLinkman() {
        return linkman;
    }

    public ContractInfo linkman(String linkman) {
        this.linkman = linkman;
        return this;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getContactDept() {
        return contactDept;
    }

    public ContractInfo contactDept(String contactDept) {
        this.contactDept = contactDept;
        return this;
    }

    public void setContactDept(String contactDept) {
        this.contactDept = contactDept;
    }

    public String getTelephone() {
        return telephone;
    }

    public ContractInfo telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Double getReceiveTotal() {
        return receiveTotal;
    }

    public ContractInfo receiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
        return this;
    }

    public void setReceiveTotal(Double receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public Double getFinishRate() {
        return finishRate;
    }

    public ContractInfo finishRate(Double finishRate) {
        this.finishRate = finishRate;
        return this;
    }

    public void setFinishRate(Double finishRate) {
        this.finishRate = finishRate;
    }

    public Integer getStatus() {
        return status;
    }

    public ContractInfo status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public ContractInfo creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ContractInfo createTime(ZonedDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdator() {
        return updator;
    }

    public ContractInfo updator(String updator) {
        this.updator = updator;
        return this;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public ContractInfo updateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Double getFinishTotal() {
		return finishTotal;
	}

	public void setFinishTotal(Double finishTotal) {
		this.finishTotal = finishTotal;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractInfo contractInfo = (ContractInfo) o;
        if (contractInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, contractInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ContractInfo{" +
            "id=" + id +
            ", serialNum='" + serialNum + "'" +
            ", name='" + name + "'" +
            ", amount='" + amount + "'" +
            ", type='" + type + "'" +
            ", isPrepared='" + isPrepared + "'" +
            ", isEpibolic='" + isEpibolic + "'" +
            ", startDay='" + startDay + "'" +
            ", endDay='" + endDay + "'" +
            ", taxRate='" + taxRate + "'" +
            ", taxes='" + taxes + "'" +
            ", shareRate='" + shareRate + "'" +
            ", shareCost='" + shareCost + "'" +
            ", paymentWay='" + paymentWay + "'" +
            ", contractor='" + contractor + "'" +
            ", address='" + address + "'" +
            ", postcode='" + postcode + "'" +
            ", linkman='" + linkman + "'" +
            ", contactDept='" + contactDept + "'" +
            ", telephone='" + telephone + "'" +
            ", receiveTotal='" + receiveTotal + "'" +
            ", finishRate='" + finishRate + "'" +
            ", status='" + status + "'" +
            ", creator='" + creator + "'" +
            ", createTime='" + createTime + "'" +
            ", updator='" + updator + "'" +
            ", updateTime='" + updateTime + "'" +
            '}';
    }
}
