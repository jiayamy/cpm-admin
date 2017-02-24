package com.wondertek.cpm.domain.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * 销售内部采购成本信息(项目支撑成本信息)
 */
public class ProjectSupportCostVo implements Serializable{
	
	private static final long serialVersionUID = 1L;

    private Long id;
	
	/**
	 * 统计日期、
	 */
	private Long statWeek;
	
	/**
	 * 合同主键、
	 */
	private Long contractId;
	
	/**
	 * 部门类型主键（走项目所属部门的部门类型）、
	 */
	private Long deptType;
	
	/**
	 * 员工主键、
	 */
	private Long userId;
	
	/**
	 * 员工编号、
	 */
	private String serialNum;
	
	/**
	 * 员工姓名、
	 */
	private String userName;
	
	/**
	 * 级别（员工信息中有）、
	 */
	private Integer grade;
	
	/**
	 * 结算成本（2.2中的小时成本）、
	 */
	private Double settlementCost;
	
	/**
	 * 项目工时（统计之前的员工所有小时成本之和，从员工日报中获取）、
	 */
	private Double projectHourCost;
	
	/**
	 * 内部采购成本（结算成本*项目工时）、
	 */
	private Double internalBudgetCost;
	
	/**
	 * 工资（从员工成本中获取统计日期时的员工工资）、
	 */
	private Double sal;
	
	/**
	 * 社保公积金（从员工成本中获取统计日期时的社保公积金）、、
	 */
	private Double socialSecurityFund;
	
	/**
	 * 其他费用（从员工成本中获取统计日期时的其他费用）、、
	 */
	private Double otherExpense;
	
	/**
	 * 单人月成本小计（工资+社保公积金+其他费用）、
	 */
	private Double userMonthCost;
	
	/**
	 * 工时成本（当人月成本小计/168）、
	 */
	private Double userHourCost;
	
	/**
	 * 生产成本合计（工时成本*项目工时）、
	 */
	private Double productCost;
	
	/**
	 * 生产毛利（内部采购成本-生成成本合计）
	 */
	private Double grossProfit;
    private String creator;
    private ZonedDateTime createTime;
    private String contractSerialNum;	//合同编号
    private String deptName;		//部门名称
    
    public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getContractSerialNum() {
		return contractSerialNum;
	}

	public void setContractSerialNum(String contractSerialNum) {
		this.contractSerialNum = contractSerialNum;
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStatWeek() {
		return statWeek;
	}

	public void setStatWeek(Long statWeek) {
		this.statWeek = statWeek;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getDeptType() {
		return deptType;
	}

	public void setDeptType(Long deptType) {
		this.deptType = deptType;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Double getSettlementCost() {
		return settlementCost;
	}

	public void setSettlementCost(Double settlementCost) {
		this.settlementCost = settlementCost;
	}

	public Double getProjectHourCost() {
		return projectHourCost;
	}

	public void setProjectHourCost(Double projectHourCost) {
		this.projectHourCost = projectHourCost;
	}

	public Double getInternalBudgetCost() {
		return internalBudgetCost;
	}

	public void setInternalBudgetCost(Double internalBudgetCost) {
		this.internalBudgetCost = internalBudgetCost;
	}

	public Double getSal() {
		return sal;
	}

	public void setSal(Double sal) {
		this.sal = sal;
	}

	public Double getSocialSecurityFund() {
		return socialSecurityFund;
	}

	public void setSocialSecurityFund(Double socialSecurityFund) {
		this.socialSecurityFund = socialSecurityFund;
	}

	public Double getOtherExpense() {
		return otherExpense;
	}

	public void setOtherExpense(Double otherExpense) {
		this.otherExpense = otherExpense;
	}

	public Double getUserMonthCost() {
		return userMonthCost;
	}

	public void setUserMonthCost(Double userMonthCost) {
		this.userMonthCost = userMonthCost;
	}

	public Double getUserHourCost() {
		return userHourCost;
	}

	public void setUserHourCost(Double userHourCost) {
		this.userHourCost = userHourCost;
	}

	public Double getProductCost() {
		return productCost;
	}

	public void setProductCost(Double productCost) {
		this.productCost = productCost;
	}

	public Double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}

	@Override
	public String toString() {
		return "ProjectSupportCostVo [id=" + id + ", statWeek=" + statWeek + ", contractId=" + contractId
				+ ", deptType=" + deptType + ", userId=" + userId + ", serialNum=" + serialNum + ", userName="
				+ userName + ", grade=" + grade + ", settlementCost=" + settlementCost + ", projectHourCost="
				+ projectHourCost + ", internalBudgetCost=" + internalBudgetCost + ", sal=" + sal
				+ ", socialSecurityFund=" + socialSecurityFund + ", otherExpense=" + otherExpense + ", userMonthCost="
				+ userMonthCost + ", userHourCost=" + userHourCost + ", productCost=" + productCost + ", grossProfit="
				+ grossProfit + ", creator=" + creator + ", createTime=" + createTime + ", contractSerialNum="
				+ contractSerialNum + "]";
	}
}
