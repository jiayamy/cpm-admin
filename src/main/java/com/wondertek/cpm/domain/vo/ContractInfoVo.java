package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.wondertek.cpm.domain.ContractInfo;
import com.wondertek.cpm.domain.OutsourcingUser;

public class ContractInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
    private String serialNum;
    private String name;
    private Double amount;
    private Integer type;
    
    private Boolean isPrepared;
    private Boolean isEpibolic;
    
    private Long salesmanId;
    private String salesman;
    private Long deptId;
    private String dept;
    
    private Long consultantsId;
    private String consultants;
    private Long consultantsDeptId;
    private String consultantsDept;
    private Double consultantsShareRate;
    
    private ZonedDateTime startDay;
    private ZonedDateTime endDay;
    
    private Double taxRate;
    private Double taxes;
    private Double shareRate;
    private Double shareCost;
    private String paymentWay;
    
    private String contractor;
    private String address;
    private String postcode;
    private String linkman;
    private String contactDept;
    private String telephone;
    
    private Double receiveTotal;
    private Double finishTotal;
    private Double finishRate;
    private Integer status;

    private String creator;

    private ZonedDateTime createTime;

    private String updator;

    private ZonedDateTime updateTime;
    
    private String mark;
    
    private OutsourcingUser outsourcingUser;
    
    private ContractInfo contractInfo;

    public ContractInfoVo() {
	}
    //查看使用
    public ContractInfoVo(ContractInfo contractInfo) {
		this.id = contractInfo.getId();
		this.serialNum = contractInfo.getSerialNum();
		this.name = contractInfo.getName();
		this.amount = contractInfo.getAmount();
		this.type = contractInfo.getType();
		this.isPrepared = contractInfo.getIsPrepared();
		this.isEpibolic = contractInfo.getIsEpibolic();
		this.salesmanId = contractInfo.getSalesmanId();
		this.salesman = contractInfo.getSalesman();
		this.dept = contractInfo.getDept();
		this.deptId = contractInfo.getDeptId();
		this.consultants = contractInfo.getConsultants();
		this.consultantsId = contractInfo.getConsultantsId();
		this.consultantsDept = contractInfo.getConsultantsDept();
		this.consultantsDeptId = contractInfo.getConsultantsDeptId();
		this.consultantsShareRate = contractInfo.getConsultantsShareRate();
		this.startDay = contractInfo.getStartDay();
		this.endDay = contractInfo.getEndDay();
		this.taxRate = contractInfo.getTaxRate();
		this.taxes = contractInfo.getTaxes();
		this.shareRate = contractInfo.getShareRate();
		this.shareCost = contractInfo.getShareCost();
		this.paymentWay = contractInfo.getPaymentWay();
		this.contractor = contractInfo.getContractor();
		this.address = contractInfo.getAddress();
		this.postcode = contractInfo.getPostcode();
		this.linkman = contractInfo.getLinkman();
		this.contactDept = contractInfo.getContactDept();
		this.telephone = contractInfo.getTelephone();
		this.receiveTotal = contractInfo.getReceiveTotal();
		this.finishTotal = contractInfo.getFinishTotal();
		this.finishRate = contractInfo.getFinishRate();
		this.status = contractInfo.getStatus();
		this.creator = contractInfo.getCreator();
		this.createTime = contractInfo.getCreateTime();
		this.updator = contractInfo.getUpdator();
		this.updateTime = contractInfo.getUpdateTime();
		this.mark = contractInfo.getMark();
	}
    //列表页使用
	public ContractInfoVo(ContractInfo contractInfo, Integer key) {
		this.id = contractInfo.getId();
		this.serialNum = contractInfo.getSerialNum();
		this.name = contractInfo.getName();
		this.amount = contractInfo.getAmount();
		this.type = contractInfo.getType();
		this.isPrepared = contractInfo.getIsPrepared();
		this.isEpibolic = contractInfo.getIsEpibolic();
//		this.salesmanId = contractInfo.getSalesmanId();
		this.salesman = contractInfo.getSalesman();
//		this.dept = contractInfo.getDept();
//		this.deptId = contractInfo.getDeptId();
		this.consultants = contractInfo.getConsultants();
//		this.consultantsId = contractInfo.getConsultantsId();
//		this.consultantsDept = contractInfo.getConsultantsDept();
//		this.consultantsDeptId = contractInfo.getConsultantsDeptId();
		this.consultantsShareRate = contractInfo.getConsultantsShareRate();
		this.startDay = contractInfo.getStartDay();
		this.endDay = contractInfo.getEndDay();
//		this.taxRate = contractInfo.getTaxRate();
//		this.taxes = contractInfo.getTaxes();
//		this.shareRate = contractInfo.getShareRate();
//		this.shareCost = contractInfo.getShareCost();
//		this.paymentWay = contractInfo.getPaymentWay();
		this.contractor = contractInfo.getContractor();
//		this.address = contractInfo.getAddress();
//		this.postcode = contractInfo.getPostcode();
//		this.linkman = contractInfo.getLinkman();
//		this.contactDept = contractInfo.getContactDept();
//		this.telephone = contractInfo.getTelephone();
//		this.receiveTotal = contractInfo.getReceiveTotal();
//		this.finishTotal = contractInfo.getFinishTotal();
		this.finishRate = contractInfo.getFinishRate();
		this.status = contractInfo.getStatus();
		this.creator = contractInfo.getCreator();
		this.createTime = contractInfo.getCreateTime();
		this.updator = contractInfo.getUpdator();
		this.updateTime = contractInfo.getUpdateTime();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getIsPrepared() {
		return isPrepared;
	}

	public void setIsPrepared(Boolean isPrepared) {
		this.isPrepared = isPrepared;
	}

	public Boolean getIsEpibolic() {
		return isEpibolic;
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

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public Long getConsultantsId() {
		return consultantsId;
	}

	public void setConsultantsId(Long consultantsId) {
		this.consultantsId = consultantsId;
	}

	public String getConsultants() {
		return consultants;
	}

	public void setConsultants(String consultants) {
		this.consultants = consultants;
	}

	public Long getConsultantsDeptId() {
		return consultantsDeptId;
	}

	public void setConsultantsDeptId(Long consultantsDeptId) {
		this.consultantsDeptId = consultantsDeptId;
	}

	public String getConsultantsDept() {
		return consultantsDept;
	}

	public void setConsultantsDept(String consultantsDept) {
		this.consultantsDept = consultantsDept;
	}

	public ZonedDateTime getStartDay() {
		return startDay;
	}

	public void setStartDay(ZonedDateTime startDay) {
		this.startDay = startDay;
	}

	public ZonedDateTime getEndDay() {
		return endDay;
	}

	public void setEndDay(ZonedDateTime endDay) {
		this.endDay = endDay;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTaxes() {
		return taxes;
	}

	public void setTaxes(Double taxes) {
		this.taxes = taxes;
	}

	public Double getShareRate() {
		return shareRate;
	}

	public void setShareRate(Double shareRate) {
		this.shareRate = shareRate;
	}

	public Double getShareCost() {
		return shareCost;
	}

	public void setShareCost(Double shareCost) {
		this.shareCost = shareCost;
	}

	public String getPaymentWay() {
		return paymentWay;
	}

	public void setPaymentWay(String paymentWay) {
		this.paymentWay = paymentWay;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getContactDept() {
		return contactDept;
	}

	public void setContactDept(String contactDept) {
		this.contactDept = contactDept;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Double getReceiveTotal() {
		return receiveTotal;
	}

	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}

	public Double getFinishTotal() {
		return finishTotal;
	}

	public void setFinishTotal(Double finishTotal) {
		this.finishTotal = finishTotal;
	}

	public Double getFinishRate() {
		return finishRate;
	}

	public void setFinishRate(Double finishRate) {
		this.finishRate = finishRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
	public Double getConsultantsShareRate() {
		return consultantsShareRate;
	}
	public void setConsultantsShareRate(Double consultantsShareRate) {
		this.consultantsShareRate = consultantsShareRate;
	}
	
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public OutsourcingUser getOutsourcingUser() {
		return outsourcingUser;
	}
	public void setOutsourcingUser(OutsourcingUser outsourcingUser) {
		this.outsourcingUser = outsourcingUser;
	}
	public ContractInfo getContractInfo() {
		return contractInfo;
	}
	public void setContractInfo(ContractInfo contractInfo) {
		this.contractInfo = contractInfo;
	}
}

