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
 * 项目支撑成本信息
 */
@Entity
@Table(name = "w_project_support_cost")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projectsupportcost")
public class ProjectSupportCost implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * 统计日期、
	 */
	@Column(name = "stat_week")
	private Long statWeek;
	
	/**
	 * 合同主键、
	 */
	@Column(name = "contract_id")
	private Long contractId;
	
	/**
	 * 部门类型主键（走项目所属部门的部门类型）、
	 */
	@Column(name = "dept_type")
	private Long deptType;
	
	/**
	 * 员工主键、
	 */
	@Column(name = "user_id")
	private Long userId;
	
	/**
	 * 员工编号、
	 */
	@Column(name = "serial_num")
	private String serialNum;
	
	/**
	 * 员工姓名、
	 */
	@Column(name = "user_name")
	private String userName;
	
	/**
	 * 级别（员工信息中有）、
	 */
	@Column(name = "grade_")
	private Integer grade;
	
	/**
	 * 结算成本（2.2中的小时成本）、
	 */
	@Column(name = "settlement_cost")
	private Double settlementCost;
	
	/**
	 * 项目工时（统计之前的员工所有小时成本之和，从员工日报中获取）、
	 */
	@Column(name = "project_hour_cost")
	private Double projectHourCost;
	
	/**
	 * 内部采购成本（结算成本*项目工时）、
	 */
	@Column(name = "internal_budget_cost")
	private Double internalBdgetCost;
	
	/**
	 * 工资（从员工成本中获取统计日期时的员工工资）、
	 */
	@Column(name = "sal_")
	private Double sal;
	
	/**
	 * 社保公积金（从员工成本中获取统计日期时的社保公积金）、、
	 */
	@Column(name = "social_security_fund")
	private Double socialSecurityFund;
	
	/**
	 * 其他费用（从员工成本中获取统计日期时的其他费用）、、
	 */
	@Column(name = "other_expense")
	private Double otherExpense;
	
	/**
	 * 单人月成本小计（工资+社保公积金+其他费用）、
	 */
	@Column(name = "user_month_cost")
	private Double userMonthCost;
	
	/**
	 * 工时成本（当人月成本小计/168）、
	 */
	@Column(name = "user_hour_cost")
	private Double userHourCost;
	
	/**
	 * 生产成本合计（工时成本*项目工时）、
	 */
	@Column(name = "product_cost")
	private Double productCost;
	
	/**
	 * 生产毛利（内部采购成本-生成成本合计）
	 */
	@Column(name = "gross_profit")
	private Double grossProfit;
	
	@Column(name = "creator_")
    private String creator;

    @Column(name = "create_time")
    private ZonedDateTime createTime;
    
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

	public Double getInternalBdgetCost() {
		return internalBdgetCost;
	}

	public void setInternalBdgetCost(Double internalBdgetCost) {
		this.internalBdgetCost = internalBdgetCost;
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
	
	
}
