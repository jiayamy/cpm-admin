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
	 * 合同预算信息权限
	 */
    public static final String ROLE_CONTRACT_BUDGET = "ROLE_CONTRACT_BUDGET";
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
	 * 项目管理权限
	 */
    public static final String ROLE_PROJECT = "ROLE_PROJECT";
    /**
	 * 项目基本信息权限
	 */
    public static final String ROLE_PROJECT_INFO = "ROLE_PROJECT_INFO";
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
    
    private AuthoritiesConstants() {
    }
}
