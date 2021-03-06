package com.wondertek.cpm.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {
	/**
	 * 管理权限
	 */
    public static final String ADMIN = "ROLE_ADMIN";
    /**
     * 用户权限，所有人都要赋予这个权限，一般如果所有人都有的就用这个权限
     */
    public static final String USER = "ROLE_USER";
    /**
     * 匿名权限，不登陆就可以显示的
     */
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    /**
	 * 信息维护权限
	 */
    public static final String ROLE_INFO = "ROLE_INFO";
    /**
	 * 信息维护常见权限
	 */
    public static final String ROLE_INFO_BASIC = "ROLE_INFO_BASIC";
    /**
     * 用户成本权限
     */
    public static final String ROLE_INFO_USERCOST = "ROLE_INFO_USERCOST";
    /**
	 * 日报管理权限
	 */
    public static final String ROLE_TIMESHEET = "ROLE_TIMESHEET";
    /**
	 * 日报管理-员工日报权限
	 */
    public static final String ROLE_USERCOST = "ROLE_USERCOST";
    /**
	 * 合同管理权限
	 */
    public static final String ROLE_CONTRACT = "ROLE_CONTRACT";
    /**
	 * 合同基本信息权限
	 */
    public static final String ROLE_CONTRACT_INFO = "ROLE_CONTRACT_INFO";
    /**
	 * 合同回款权限
	 */
    public static final String ROLE_CONTRACT_RECEIVE = "ROLE_CONTRACT_RECEIVE";
    /**
	 * 合同人员信息权限
	 */
    public static final String ROLE_CONTRACT_USER = "ROLE_CONTRACT_USER";
    /**
	 * 合同内部采购单权限
	 */
    public static final String ROLE_CONTRACT_BUDGET = "ROLE_CONTRACT_BUDGET";
    /**
	 * 合同内部采购单编辑权限
	 */
    public static final String ROLE_CONTRACT_BUDGET_EDIT = "ROLE_CONTRACT_BUDGET_EDIT";
    /**
	 * 合同采购子项权限
	 */
    public static final String ROLE_CONTRACT_PURCHASE = "ROLE_CONTRACT_PURCHASE";
    /**
	 * 产品定价单权限
	 */
    public static final String ROLE_CONTRACT_PRODUCTPRICE = "ROLE_CONTRACT_PRODUCTPRICE";
    /**
	 * 合同成本信息权限
	 */
    public static final String ROLE_CONTRACT_COST = "ROLE_CONTRACT_COST";
    /**
	 * 合同成本信息权限
	 */
    public static final String ROLE_CONTRACT_TIMESHEET = "ROLE_CONTRACT_TIMESHEET";
    /**
	 * 合同的立结项
	 */
    public static final String ROLE_CONTRACT_END = "ROLE_CONTRACT_END";
    /**
	 * 项目管理权限
	 */
    public static final String ROLE_PROJECT = "ROLE_PROJECT";
    /**
	 * 项目基本信息权限
	 */
    public static final String ROLE_PROJECT_INFO = "ROLE_PROJECT_INFO";
    /**
     * 项目信息-立结项
     */
    public static final String ROLE_PROJECT_INFO_END = "ROLE_PROJECT_INFO_END";
    /**
	 * 项目人员权限
	 */
    public static final String ROLE_PROJECT_USER = "ROLE_PROJECT_USER";
    /**
	 * 项目成本权限
	 */
    public static final String ROLE_PROJECT_COST = "ROLE_PROJECT_COST";
    /**
     * 项目工时权限
     */
    public static final String ROLE_PROJECT_TIMESHEET = "ROLE_PROJECT_TIMESHEET";
    /**
	 * 报表管理权限
	 */
    public static final String ROLE_STAT = "ROLE_STAT";
    /**
	 * 合同报表权限
	 */
    public static final String ROLE_STAT_CONTRACT = "ROLE_STAT_CONTRACT";
    /**
	 * 项目报表权限
	 */
    public static final String ROLE_STAT_PROJECT = "ROLE_STAT_PROJECT";
    /**
	 * 奖金总表权限
	 */
    public static final String ROLE_STAT_BONUS = "ROLE_STAT_BONUS";
    /**
     * 销售部门统计
     */
    public static final String ROLE_STAT_SALE = "ROLE_STAT_SALE";
    /**
	 * 咨询项目信息权限
	 */
    public static final String ROLE_STAT_CONSULTANT_BONUS = "ROLE_STAT_CONSULTANT_BONUS";
    /**
	 * 项目总体情况控制
	 */
    public static final String ROLE_STAT_PROJECT_OVERALL = "ROLE_STAT_PROJECT_OVERALL";
    /**
     * 项目人员工时
     */
    public static final String ROLE_STAT_PROJECT_USER_INPUT = "ROLE_STAT_PROJECT_USER_INPUT";
    /**
     * 人员项目工时
     */
    public static final String ROLE_STAT_USER_PROJECT_INPUT = "ROLE_STAT_USER_PROJECT_INPUT";
    /**
	 * 项目支撑奖金
	 */
    public static final String ROLE_STAT_SUPPORT_BONUS = "ROLE_STAT_SUPPORT_BONUS";
    /**
	 * 销售内部采购成本权限
	 */
    public static final String ROLE_STAT_INTERNAL_COST = "ROLE_STAT_INTERNAL_COST";
    /**
	 * 销售项目信息权限
	 */
    public static final String ROLE_STAT_SALES_BONUS = "ROLE_STAT_SALES_BONUS";
    
    /**
	 * 员工勤奋度信息权限
	 */
    public static final String ROLE_WORKHARDING = "ROLE_WORKHARDING";
    
    private AuthoritiesConstants() {
    }
}
