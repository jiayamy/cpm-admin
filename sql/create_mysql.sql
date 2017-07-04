CREATE
    TABLE w_work_area
    (
        id bigint NOT NULL AUTO_INCREMENT,
        name_ VARCHAR(100),
        PRIMARY KEY (id),
        CONSTRAINT idx_workarea_name_u UNIQUE (name_)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_dept_type
    (
        id bigint NOT NULL AUTO_INCREMENT,
        name_ VARCHAR(100) NOT NULL COMMENT '类型名称 (管理/销售/产品咨询/产品研发中心/项目实施/采购/行政/财务/质量管理/人力资源）',
        PRIMARY KEY (id),
        CONSTRAINT idx_depttype_name UNIQUE (name_)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_dept_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        name_ VARCHAR(200) NOT NULL COMMENT '部门名称',
        parent_id bigint COMMENT '上级部门主键',
        id_path VARCHAR(100) COMMENT '父级ID路径，到“父IDPATH/父ID/”。。顶层默认是“/”',
        type_ bigint COMMENT '部门类型',
        status_ INT COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_deptinfo_ FOREIGN KEY (type_) REFERENCES w_dept_type (id),
        CONSTRAINT idx_deptinfo_unique1 UNIQUE (name_, parent_id),
        INDEX fk_deptinfo_ (type_)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE databasechangelog
    (
        ID VARCHAR(255) COLLATE utf8_bin NOT NULL,
        AUTHOR VARCHAR(255) COLLATE utf8_bin NOT NULL,
        FILENAME VARCHAR(255) COLLATE utf8_bin NOT NULL,
        DATEEXECUTED DATETIME NOT NULL,
        ORDEREXECUTED INT NOT NULL,
        EXECTYPE VARCHAR(10) COLLATE utf8_bin NOT NULL,
        MD5SUM VARCHAR(35) COLLATE utf8_bin,
        DESCRIPTION VARCHAR(255) COLLATE utf8_bin,
        COMMENTS VARCHAR(255) COLLATE utf8_bin,
        TAG VARCHAR(255) COLLATE utf8_bin,
        LIQUIBASE VARCHAR(20) COLLATE utf8_bin,
        CONTEXTS VARCHAR(255) COLLATE utf8_bin,
        LABELS VARCHAR(255) COLLATE utf8_bin,
        DEPLOYMENT_ID VARCHAR(10) COLLATE utf8_bin
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE databasechangeloglock
    (
        ID INT NOT NULL,
        LOCKED bit NOT NULL,
        LOCKGRANTED DATETIME,
        LOCKEDBY VARCHAR(255) COLLATE utf8_bin,
        PRIMARY KEY (ID)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE jhi_authority
    (
        name VARCHAR(50) COLLATE utf8_bin NOT NULL,
        detail_ VARCHAR(50) COLLATE utf8_bin,
        PRIMARY KEY (name)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE jhi_persistent_audit_event
    (
        event_id bigint NOT NULL AUTO_INCREMENT,
        principal VARCHAR(50) COLLATE utf8_bin NOT NULL,
        event_date TIMESTAMP NULL,
        event_type VARCHAR(255) COLLATE utf8_bin,
        PRIMARY KEY (event_id),
        INDEX idx_persistent_audit_event (principal, event_date)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE jhi_persistent_audit_evt_data
    (
        event_id bigint NOT NULL,
        name VARCHAR(150) COLLATE utf8_bin NOT NULL,
        value VARCHAR(255) COLLATE utf8_bin,
        PRIMARY KEY (event_id, name),
        CONSTRAINT fk_evt_pers_audit_evt_data FOREIGN KEY (event_id) REFERENCES
        jhi_persistent_audit_event (event_id),
        INDEX idx_persistent_audit_evt_data (event_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE jhi_user
    (
        id bigint NOT NULL AUTO_INCREMENT,
        login VARCHAR(50) COLLATE utf8_bin NOT NULL,
        password_hash VARCHAR(60) COLLATE utf8_bin,
        first_name VARCHAR(50) COLLATE utf8_bin,
        last_name VARCHAR(50) COLLATE utf8_bin NOT NULL,
        email VARCHAR(100) COLLATE utf8_bin,
        activated bit NOT NULL,
        lang_key VARCHAR(5) COLLATE utf8_bin,
        activation_key VARCHAR(20) COLLATE utf8_bin,
        reset_key VARCHAR(20) COLLATE utf8_bin,
    	CREATEd_by VARCHAR(50) COLLATE utf8_bin NOT NULL,
    	CREATEd_date TIMESTAMP,
        reset_date TIMESTAMP NULL,
        last_modified_by VARCHAR(50) COLLATE utf8_bin,
        last_modified_date TIMESTAMP NULL,
        dept_id bigint NOT NULL,
        is_manager bit NOT NULL,
        duty_ VARCHAR(100) COLLATE utf8_bin,
        grade_ VARCHAR(100) COLLATE utf8_bin,
        gender_ INT,
        birth_year VARCHAR(4) COLLATE utf8_bin,
        birth_day VARCHAR(10) COLLATE utf8_bin,
        telephone_ VARCHAR(20) COLLATE utf8_bin,
        serial_num VARCHAR(10) COLLATE utf8_bin NOT NULL,
        work_area VARCHAR(100) COLLATE utf8_bin,
        PRIMARY KEY (id),
        CONSTRAINT fk_user_dept FOREIGN KEY (dept_id) REFERENCES w_dept_info (id),
        CONSTRAINT idx_user_login UNIQUE (login),
        CONSTRAINT idx_user_serialNum UNIQUE (serial_num)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE jhi_persistent_token
    (
        series VARCHAR(76) COLLATE utf8_bin NOT NULL,
        user_id bigint,
        token_value VARCHAR(76) COLLATE utf8_bin NOT NULL,
        token_date DATE,
        ip_address VARCHAR(39) COLLATE utf8_bin,
        user_agent VARCHAR(255) COLLATE utf8_bin,
        PRIMARY KEY (series),
        CONSTRAINT fk_user_persistent_token FOREIGN KEY (user_id) REFERENCES jhi_user (id),
        INDEX fk_user_persistent_token (user_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    
CREATE
    TABLE jhi_user_authority
    (
        user_id bigint NOT NULL,
        authority_name VARCHAR(50) COLLATE utf8_bin NOT NULL,
        PRIMARY KEY (user_id, authority_name),
        CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES jhi_user (id) ,
        CONSTRAINT fk_authority_name FOREIGN KEY (authority_name) REFERENCES jhi_authority (name),
        INDEX fk_authority_name (authority_name)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE
    TABLE w_contract_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        serial_num VARCHAR(100) COLLATE utf8_bin NOT NULL COMMENT '合同编号',
        name_ VARCHAR(200) COLLATE utf8_bin NOT NULL COMMENT '合同名称',
        amount_ DOUBLE(15,2) NOT NULL COMMENT '合同金额',
        type_ INT NOT NULL COMMENT '合同类型（1=产品/2=外包/3=硬件）',
        is_prepared bit NOT NULL COMMENT '是否预立（正式合同/预立合同），预立合同可以转正式合同',
        is_epibolic bit NOT NULL COMMENT '是否外包（内部合同/外包合同）--- ',
        sales_man_id bigint COMMENT '销售人员ID（界面选择员工信息）',
        sales_man VARCHAR(100) COMMENT '销售人员名称',
        dept_id bigint COMMENT '销售所属部门ID',
        dept_ VARCHAR(200) COMMENT '销售所属部门',
        consultants_id bigint,
        consultants_ VARCHAR(100) COLLATE utf8_bin,
        consultants_dept_id bigint COMMENT '咨询所属部门ID',
        consultants_dept VARCHAR(200) COMMENT '咨询所属部门',
        start_day TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始日期,页面格式20161227',
        end_day TIMESTAMP NULL COMMENT '结束日期',
        tax_rate DOUBLE(15,2) COMMENT '率,精确小数点后2位',
        taxes_ DOUBLE(15,2) COMMENT '税费（元）,精确小数点后2位',
        share_rate DOUBLE(15,2) COMMENT '公摊比例',
        share_cost DOUBLE(15,2) COMMENT '公摊成本（合同金额*公摊比例）',
        payment_way VARCHAR(20) COMMENT '付款方式（比如“3,6,1”）',
        contractor_ VARCHAR(200) COMMENT '合同方（公司名称）',
        address_ VARCHAR(1000) COMMENT '合同方通信地址',
        postcode_ VARCHAR(20) COMMENT '合同方邮编',
        linkman_ VARCHAR(100) COMMENT '合同方联系人',
        contact_dept VARCHAR(200) COMMENT '合同方联系部门',
        telephone_ VARCHAR(50) COMMENT '合同方电话',
        receive_total DOUBLE(15,2) COMMENT '收款总金额（不展示）',
        finish_total DOUBLE(15,2) COMMENT '合同累计完成金额（不展示）',
        finish_rate DOUBLE(15,2) COMMENT '完成率（只展示）',
        status_ INT COMMENT '状态（1=开发中，2=结项，3=删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT idx_contractinfo_serialnum UNIQUE (serial_num)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_budget
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint NOT NULL COMMENT '合同主键',
        type_ INT NOT NULL COMMENT '预算类型（销售/咨询/内部采购单）',
        user_id bigint NOT NULL COMMENT '实施者ID（对应一个具体的员工，弹窗选择员工）',
        user_name VARCHAR(100) COLLATE utf8_bin NOT NULL COMMENT '实施者名称',
        dept_id bigint NOT NULL COMMENT '所属部门ID（跟着实施者走，用户所属部门，只展示，不可更改）',
        dept_ VARCHAR(200) COLLATE utf8_bin NOT NULL COMMENT '所属部门',
        purchase_type INT NOT NULL COMMENT '采购单类型（预算类型为内部采购单时填写，硬件/软件/服务---服务可以创建项目，其他的不可以）',
        budget_total DOUBLE(15,2) NOT NULL COMMENT '预算金额',
        status_ INT NOT NULL COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
        update_time TIMESTAMP NULL,
        name_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_c_cb_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (id),
        INDEX fk_c_cb_contractid (contract_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_cost
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint NOT NULL COMMENT '合同主键',
        budget_id bigint COMMENT '合同预算主键（可能为空）',
        dept_id bigint NOT NULL COMMENT '所属部门ID（这个是部门，跟着输入人员的部门走）',
        dept_ VARCHAR(200) COLLATE utf8_bin NOT NULL COMMENT '所属部门（这个是部门，跟着输入人员的部门走）',
        name_ VARCHAR(100) COLLATE utf8_bin NOT NULL COMMENT '名称',
        type_ INT NOT NULL COMMENT '成本类型（1=工时、2=差旅、3=采购、4=商务）(工时不可输入，是统计新增的。其他可新增)',
        total_ DOUBLE(15,2) NOT NULL COMMENT '金额',
        cost_desc VARCHAR(1000) COMMENT '描述',
        status_ INT NOT NULL COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
        update_time TIMESTAMP NULL,
        COST_DAY bigint NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_c_cc_budgetid FOREIGN KEY (budget_id) REFERENCES w_contract_budget (id) ,
        CONSTRAINT fk_c_cc_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (id),
        INDEX fk_c_cc_contractid (contract_id),
        INDEX fk_c_cc_budgetid (budget_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_finish_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint NOT NULL COMMENT '合同主键',
        finish_rate DOUBLE(15,2) NOT NULL COMMENT '完成率，实时更新到合同信息中',
        creator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
    	CREATE_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_c_fi_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (id),
        INDEX fk_c_fi_contractid (contract_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE
    TABLE w_contract_monthly_stat
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint COMMENT '合同主键',
        finish_rate DOUBLE(15,2) COMMENT '完成率',
        receive_total DOUBLE(15,2) COMMENT '合同回款总额',
        cost_total DOUBLE(15,2) COMMENT '所有成本',
        gross_profit DOUBLE(15,2) COMMENT '合同毛利',
        sales_human_cost DOUBLE(15,2) COMMENT '销售人工成本',
        sales_payment DOUBLE(15,2) COMMENT '销售报销成本',
        consult_human_cost DOUBLE(15,2) COMMENT '咨询人工成本',
        consult_payment DOUBLE(15,2) COMMENT '咨询报销成本',
        hardware_purchase DOUBLE(15,2) COMMENT '硬件采购成本',
        external_software DOUBLE(15,2) COMMENT '外部软件采购成本',
        internal_software DOUBLE(15,2) COMMENT '内容软件采购成本',
        project_human_cost DOUBLE(15,2) COMMENT '项目人工成本',
        project_payment DOUBLE(15,2) COMMENT '项目报销成本',
        stat_week bigint COMMENT '统计月(或周),格式:201612',
    	CREATE_time TIMESTAMP NULL COMMENT '统计日期',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_receive
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint COMMENT '合同主键',
        receive_total DOUBLE(15,2) COMMENT '收款额---实时更新合同信息中的收款金额（根据状态为可用或删除来操作）',
        receive_day bigint COMMENT '收款时间',
        receiver_ VARCHAR(100) COMMENT '收款人',
        status_ INT COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_c_cr_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (id),
        INDEX fk_c_cr_contractid (contract_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_user
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint COMMENT '合同主键',
        user_id bigint COMMENT '人员ID',
        user_name VARCHAR(100) COMMENT '人员名称',
        dept_id bigint COMMENT '所属部门ID（跟着人员ID的所属部门走，方便后面填写工时详情分数据权限）',
        dept_ VARCHAR(200) COMMENT '所属部门（跟着人员ID的所属部门走，方便后面填写工时详情分数据权限）',
        join_day bigint COMMENT '加盟日',
        leave_day bigint COMMENT '离开日',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_c_cu_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (id),
        INDEX fk_c_cu_contractid (contract_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_contract_weekly_stat
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint,
        finish_rate DOUBLE(15,2),
        receive_total DOUBLE(15,2),
        cost_total DOUBLE(15,2),
        gross_profit DOUBLE(15,2),
        sales_human_cost DOUBLE(15,2),
        sales_payment DOUBLE(15,2),
        consult_human_cost DOUBLE(15,2),
        consult_payment DOUBLE(15,2),
        hardware_purchase DOUBLE(15,2),
        external_software DOUBLE(15,2),
        internal_software DOUBLE(15,2),
        project_human_cost DOUBLE(15,2),
        project_payment DOUBLE(15,2),
        stat_week bigint,
    CREATE_time TIMESTAMP NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_holiday_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        curr_day bigint NOT NULL COMMENT '当前天',
        type_ INT COMMENT '类型（正常工作日/正常假日/年假/国家假日）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT idx_holidayinfo_currday UNIQUE (curr_day)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_product_price
    (
        id bigint NOT NULL AUTO_INCREMENT,
        name_ VARCHAR(200) COMMENT '产品名称',
        type_ INT COMMENT '产品类型（硬件/软件）',
        units_ VARCHAR(20) COMMENT '产品单位（同采购子项）',
        price_ DOUBLE(15,2) COMMENT '产品单价（元）',
        source_ INT COMMENT '产品来源（内部/外部）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE
    TABLE w_project_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        serial_num VARCHAR(20) NOT NULL COMMENT '项目编号',
        contract_id bigint NOT NULL COMMENT '合同主键',
        budget_id bigint NOT NULL COMMENT '合同预算主键',
        name_ VARCHAR(200) NOT NULL COMMENT '项目名称',
        pm_id bigint NOT NULL COMMENT '项目经理ID（对应一个具体的员工，弹窗选择员工）',
        pm_ VARCHAR(100) NOT NULL COMMENT '项目经理（对应一个具体的员工，弹窗选择员工）',
        dept_id bigint NOT NULL COMMENT '所属部门（跟着项目经理走，用户所属部门，只展示，不可更改）',
        dept_ VARCHAR(100) NOT NULL COMMENT '所属部门（跟着项目经理走，用户所属部门，只展示，不可更改）',
        start_day TIMESTAMP NULL COMMENT '开始日期',
        end_day TIMESTAMP NULL COMMENT '结束日期',
        budget_total DOUBLE(15,2) NOT NULL COMMENT '预算总额',
        status_ INT COMMENT '状态（1=开发中，2=结项，3=删除）',
        finish_rate DOUBLE(15,2) COMMENT '完成率（只展示）',
    	CREATE_time TIMESTAMP NULL,
        creator_ VARCHAR(100),
        update_time TIMESTAMP NULL,
        updator_ VARCHAR(100),
        PRIMARY KEY (id),
        CONSTRAINT fk_projectinfo_budgetid FOREIGN KEY (budget_id) REFERENCES w_contract_budget (id
        ) ,
        CONSTRAINT fk_projectinfo_contractid FOREIGN KEY (contract_id) REFERENCES w_contract_info (
        id),
        CONSTRAINT idx_projectinfo_serialnum UNIQUE (serial_num),
        CONSTRAINT idx_projectinfo_u_budget_id UNIQUE (budget_id),
        INDEX fk_projectinfo_contractid (contract_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_project_cost
    (
        id bigint NOT NULL AUTO_INCREMENT,
        project_id bigint NOT NULL COMMENT '项目主键',
        name_ VARCHAR(100) COLLATE utf8_bin NOT NULL COMMENT '名称',
        type_ INT NOT NULL COMMENT '成本类型（1=工时、2=差旅、3=采购、4=商务）(工时不可输入，是统计新增的。其他可新增)',
        total_ DOUBLE(15,2) NOT NULL COMMENT '金额',
        cost_desc VARCHAR(1000) COMMENT '描述',
        status_ INT NOT NULL COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
    	CREATE_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
        update_time TIMESTAMP DEFAULT '0000-00-00 00:00:00',
        cost_day bigint NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_p_pc_projectid FOREIGN KEY (project_id) REFERENCES w_project_info (id),
        INDEX fk_p_pc_projectid (project_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_project_finish_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        project_id bigint COMMENT '项目主键',
        finish_rate DOUBLE(15,2) COMMENT '完成率',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_p_pfi_projectid FOREIGN KEY (project_id) REFERENCES w_project_info (id),
        INDEX fk_p_pfi_projectid (project_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_project_monthly_stat
    (
        id bigint NOT NULL AUTO_INCREMENT,
        project_id bigint,
        finish_rate DOUBLE(15,2),
        human_cost DOUBLE(15,2),
        payment_ DOUBLE(15,2),
        stat_week bigint,
    	CREATE_time TIMESTAMP NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_project_user
    (
        id bigint NOT NULL AUTO_INCREMENT,
        project_id bigint NOT NULL COMMENT '项目主键',
        user_id bigint NOT NULL COMMENT '项目人员ID',
        user_name VARCHAR(100) COLLATE utf8_bin NOT NULL COMMENT '项目人员名称',
        user_role VARCHAR(50) COMMENT '人员角色（需求、开发、测试、研发、项目经理）',
        join_day bigint NOT NULL COMMENT '加盟日',
        leave_day bigint COMMENT '离开日',
        creator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin NOT NULL,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_p_pu_projectid FOREIGN KEY (project_id) REFERENCES w_project_info (id),
        INDEX fk_p_pu_projectid (project_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_project_weekly_stat
    (
        id bigint NOT NULL AUTO_INCREMENT,
        project_id bigint COMMENT '项目主键',
        finish_rate DOUBLE(15,2) COMMENT '完成率',
        human_cost DOUBLE(15,2) COMMENT '项目人工成本',
        payment_ DOUBLE(15,2) COMMENT '项目报销成本',
        stat_week bigint COMMENT '统计月(或周)',
    	CREATE_time TIMESTAMP NULL COMMENT '统计日期',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_purchase_item
    (
        id bigint NOT NULL AUTO_INCREMENT,
        contract_id bigint COMMENT '合同主键',
        budget_id bigint COMMENT '合同预算主键',
        name_ VARCHAR(100) COMMENT '采购项目----采购的是什么？用户填写后，可以点击“参考价”显示该采购项目的产品定价单',
        quantity_ INT COMMENT '采购数量',
        price_ DOUBLE COMMENT '采购单价',
        units_ VARCHAR(20) COMMENT '采购单位',
        type_ INT COMMENT '采购类型（1=硬件/2=软件）',
        source_ INT COMMENT '采购来源（1=内部采购/2=外部采购）',
        purchaser_ VARCHAR(200) COMMENT '采购方（从哪里采购的）',
        total_amount DOUBLE(15,2) COMMENT '采购总金额（可以填写，也可以通过修改采购数量和采购单价相乘）',
        status_ INT COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_stat_identify
    (
        id bigint NOT NULL AUTO_INCREMENT,
        obj_id bigint NOT NULL COMMENT '对象id',
        type_ INT(5) NOT NULL COMMENT '对象类型(1=contract/2=project)',
        status_ INT(5) NOT NULL COMMENT '状态(0=可使用/1=正在被使用)',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='生成月报周报时初始化cost运行状态标识符';
    
CREATE
    TABLE w_user_cost
    (
        id bigint NOT NULL AUTO_INCREMENT,
        user_id bigint COMMENT '员工主键',
        user_name VARCHAR(100) COMMENT '员工名字',
        cost_month bigint COMMENT '所属年月',
        internal_cost DOUBLE(15,2) COMMENT '内部成本',
        external_cost DOUBLE(15,2) COMMENT '外部成本',
        status_ INT COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE
    TABLE w_user_timesheet
    (
        id bigint NOT NULL AUTO_INCREMENT,
        work_day bigint COMMENT '日期',
        user_id bigint COMMENT '员工ID',
        user_name VARCHAR(100) COMMENT '员工名字',
        type_ INT COMMENT '类型（2=合同、3=项目、1=公共成本）',
        obj_id bigint COMMENT '对象ID',
        obj_name VARCHAR(100) COMMENT '对象名称',
        real_input DOUBLE(15,2) COMMENT '工时投入（实际投入工时，项目成本需要）',
        accept_input DOUBLE(15,2) COMMENT '工时产出（认可工时，统计员工贡献度需要）',
        status_ INT COMMENT '状态（可用，删除）',
        creator_ VARCHAR(100) COLLATE utf8_bin,
    	CREATE_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        work_area VARCHAR(100) COLLATE utf8_bin,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
    insert into jhi_authority (name, detail_) values ('ROLE_ADMIN', '管理');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT', '合同管理');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_BUDGET', '合同管理-内部采购单');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_COST', '合同管理-合同成本');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_INFO', '合同管理-合同信息');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_PRODUCTPRICE', '合同管理-产品定价单');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_PURCHASE', '合同管理-采购子项');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_RECEIVE', '合同管理-回款信息');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_TIMESHEET', '合同管理-合同工时');
	insert into jhi_authority (name, detail_) values ('ROLE_CONTRACT_USER', '合同管理-合同人员');
	insert into jhi_authority (name, detail_) values ('ROLE_INFO', '系统设置');
	insert into jhi_authority (name, detail_) values ('ROLE_INFO_BASIC', '系统设置-基本功能');
	insert into jhi_authority (name, detail_) values ('ROLE_INFO_USERCOST', '系统设置-员工成本');
	insert into jhi_authority (name, detail_) values ('ROLE_PROJECT', '项目管理');
	insert into jhi_authority (name, detail_) values ('ROLE_PROJECT_COST', '项目管理-项目报销');
	insert into jhi_authority (name, detail_) values ('ROLE_PROJECT_INFO', '项目管理-项目信息');
	insert into jhi_authority (name, detail_) values ('ROLE_PROJECT_TIMESHEET', '项目管理-项目工时');
	insert into jhi_authority (name, detail_) values ('ROLE_PROJECT_USER', '项目管理-项目人员');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT', '统计报表');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_CONTRACT', '统计报表-合同相关');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_PROJECT', '统计报表-项目相关');
	insert into jhi_authority (name, detail_) values ('ROLE_TIMESHEET', '日报管理');
	insert into jhi_authority (name, detail_) values ('ROLE_USER', '用户');
	insert into jhi_authority (name, detail_) values ('ROLE_USERCOST', '日报管理-员工日报');
	
	insert into w_dept_type (id, name_) values (1, '管理');
	insert into w_dept_type (id, name_) values (2, '销售');
	insert into w_dept_type (id, name_) values (3, '产品咨询');
	insert into w_dept_type (id, name_) values (4, '产品研发中心');
	insert into w_dept_type (id, name_) values (5, '项目实施');
	insert into w_dept_type (id, name_) values (6, '采购');
	insert into w_dept_type (id, name_) values (7, '行政');
	insert into w_dept_type (id, name_) values (8, '财务');
	insert into w_dept_type (id, name_) values (9, '质量管理');
	insert into w_dept_type (id, name_) values (10, '人力资源');

	insert into w_dept_info (id, name_, parent_id, id_path, type_, status_, creator_, create_time, updator_, update_time) values (1, '上海网达软件股份有限公司', null, '/', 1, 1, 'admin', '2017-01-01 00:00:00', 'admin', '2017-01-01 00:00:00');

	insert into jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date, dept_id, is_manager, duty_, grade_, gender_, birth_year, birth_day, telephone_, serial_num, work_area) values 
	(1, 'admin', '$2a$10$UCjAhdE2Qrskpck7/FoDGOJFTEiAGSBr6hpe95ndfLJk9f1cpxtMK', null, '管理人员', 'admin@localhost', true, 'zh-cn', null, null, 'system', '2017-01-01 00:00:00', null, 'admin', '2017-01-01 00:00:00', 1, true, null, null, 1, null, null, null, '0', '上海');

	insert into jhi_user_authority (user_id, authority_name) values (1, 'ROLE_USER');
	insert into jhi_user_authority (user_id, authority_name) values (1, 'ROLE_INFO');
	insert into jhi_user_authority (user_id, authority_name) values (1, 'ROLE_INFO_BASIC');
	
	--20170125
	ALTER TABLE w_purchase_item ADD (product_price_id BIGINT);
	
	--20170207
	ALTER TABLE w_user_cost ADD sal_ DOUBLE(15,2) COMMENT '工资';
	ALTER TABLE w_user_cost ADD social_security_fund DOUBLE(15,2) COMMENT '社保公积金';
	ALTER TABLE w_user_cost ADD other_expense DOUBLE(15,2) COMMENT '其它费用';
	--20170227
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_CONSULTANT_BONUS', '统计报表-咨询项目信息');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_BONUS', '统计报表-奖金总表');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_INTERNAL_COST', '统计报表-销售内部采购成本');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_SALES_BONUS', '统计报表-销售项目信息');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_PROJECT_OVERALL', '统计报表-项目总体情况控制');
	insert into jhi_authority (name, detail_) values ('ROLE_STAT_SUPPORT_BONUS', '统计报表-项目支撑奖金');
	
	--20170301
	ALTER TABLE jhi_user MODIFY COLUMN grade_ INT DEFAULT '1';
	ALTER TABLE w_contract_info ADD COLUMN `consultants_share_rate` double(15,2) DEFAULT NULL COMMENT '咨询分润比率';

	CREATE 
	TABLE `w_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同主键、',
	  `contract_amount` double(15,2) DEFAULT NULL COMMENT '合同金额、',
	  `sales_bonus` double(15,2) DEFAULT NULL COMMENT '当期销售奖金(2.10的本期奖金)、',
	  `project_bonus` double(15,2) DEFAULT NULL COMMENT '当期项目奖金（2.12之和）、',
	  `consultants_bonus` double(15,2) DEFAULT NULL COMMENT '当期业务咨询奖金(2.11的本期奖金)、',
	  `bonus_total` double(15,2) DEFAULT NULL COMMENT '奖金合计（销售+项目+咨询奖金）',
	  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='奖金总表';

CREATE 
	TABLE `w_bonus_rate` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `dept_type` bigint(20) NOT NULL COMMENT '部门类型',
	  `contract_type` int(11) NOT NULL COMMENT '合同类型',
	  `rate_` double(15,2) NOT NULL COMMENT '提成比率',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='奖金提成比率';
	
CREATE 
	TABLE `w_consultants_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同主键、',
	  `contract_amount` double(15,2) DEFAULT NULL COMMENT '合同金额、',
	  `consultants_id` bigint(20) DEFAULT NULL COMMENT '咨询负责人主键、',
	  `consultants_` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '咨询负责人名称、',
	  `bonus_basis` double(15,2) DEFAULT NULL COMMENT '奖金基数（同销售奖金中的奖金基数）、',
	  `bonus_rate` double(15,2) DEFAULT NULL COMMENT '奖金比例（2.3中的咨询）、',
	  `consultants_share_rate` double(15,2) DEFAULT NULL COMMENT '项目分润比率（合同上的字段）、',
	  `current_bonus` double(15,2) DEFAULT NULL COMMENT '本期奖金（奖金基数*奖金比例*分润比例）',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='咨询奖金';

CREATE 
	TABLE `w_project_advance_schedule_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `min_schedule` double(15,2) NOT NULL COMMENT '进度最小值',
	  `bonus_rate` double(15,2) NOT NULL COMMENT '奖金加成比率',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='项目提前进度奖金加成信息';

CREATE 
	TABLE `w_external_quotation` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `grade_` int(11) NOT NULL COMMENT '级别',
	  `external_quotation` double(15,2) NOT NULL COMMENT '对外报价',
	  `social_security_fund` double(15,2) NOT NULL COMMENT '社保公积金',
	  `other_expense` double(15,2) NOT NULL COMMENT '其他费用',
	  `cost_basis` double(15,2) NOT NULL COMMENT '成本依据',
	  `hour_cost` double(15,2) NOT NULL COMMENT '小时成本',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='外部报价';

CREATE 
	TABLE `w_project_support_cost` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `project_id` bigint(20) DEFAULT NULL,
	  `dept_type` bigint(20) NOT NULL COMMENT '部门类型主键（走项目所属部门的部门类型）、',
	  `user_id` bigint(20) NOT NULL COMMENT '员工主键、',
	  `serial_num` varchar(10) CHARACTER SET utf8 DEFAULT NULL COMMENT '员工编号、',
	  `user_name` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '员工姓名、',
	  `grade_` int(11) DEFAULT NULL COMMENT '级别（员工信息中有）、',
	  `settlement_cost` double(15,2) DEFAULT NULL COMMENT '结算成本（2.2中的小时成本）、',
	  `project_hour_cost` double(15,2) DEFAULT NULL COMMENT '项目工时（统计之前的员工所有小时成本之和，从员工日报中获取）、',
	  `internal_budget_cost` double(15,2) DEFAULT NULL COMMENT '内部采购成本（结算成本*项目工时）、',
	  `sal_` double(15,2) DEFAULT NULL COMMENT '工资（从员工成本中获取统计日期时的员工工资）、',
	  `social_security_fund` double(15,2) DEFAULT NULL COMMENT '社保公积金（从员工成本中获取统计日期时的社保公积金）、、',
	  `other_expense` double(15,2) DEFAULT NULL COMMENT '其他费用（从员工成本中获取统计日期时的其他费用）、、',
	  `user_month_cost` double(15,2) DEFAULT NULL COMMENT '单人月成本小计（工资+社保公积金+其他费用）、',
	  `user_hour_cost` double(15,2) DEFAULT NULL COMMENT '工时成本（当人月成本小计/168）、',
	  `product_cost` double(15,2) DEFAULT NULL COMMENT '生产成本合计（工时成本*项目工时）、',
	  `gross_profit` double(15,2) DEFAULT NULL COMMENT '生产毛利（内部采购成本-生成成本合计）',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='项目支撑成本信息';	

CREATE 
	TABLE `w_project_support_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `project_id` bigint(20) DEFAULT NULL,
	  `dept_type` bigint(20) NOT NULL COMMENT '部门类型主键、',
	  `pm_id` bigint(20) NOT NULL COMMENT '项目经理主键（项目上的）、',
	  `pm_name` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '项目经理姓名、',
	  `delivery_time` int(11) DEFAULT NULL COMMENT '项目确认交付时间（项目的结束到开始日期）、',
	  `acceptance_rate` double(15,2) DEFAULT NULL COMMENT '验收节点（走合同的完成率）、',
	  `plan_days` double(15,2) DEFAULT NULL COMMENT '计划天数（项目确认交付时间*验收节点）、',
	  `real_days` int(11) DEFAULT NULL COMMENT '实际使用天数（项目结项日期（状态为已结项的更新时间）或统计时间-项目开始日期）、',
	  `bonus_adjust_rate` double(15,2) DEFAULT NULL COMMENT '奖金调节比率（计划天数/实际使用天数-1）、',
	  `bonus_rate` double(15,2) DEFAULT NULL COMMENT '奖金比率（2.3中对应部门类型的提成比率）、',
	  `bonus_acceptance_rate` double(15,2) DEFAULT NULL COMMENT '奖金确认比例（奖金比例*(1+奖金调节比例)*验收节点）、',
	  `contract_amount` double(15,2) DEFAULT NULL COMMENT '合同金额',
	  `tax_rate` double(15,2) DEFAULT NULL COMMENT '税率',
	  `bonus_basis` double(15,2) DEFAULT NULL COMMENT '奖金基数（走2.4里面的生产毛利）、',
	  `current_bonus` double(15,2) DEFAULT NULL COMMENT '当期奖金（奖金确认比例*奖金基数）',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='项目支撑奖金';	
	
CREATE 
	TABLE `w_product_sales_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `dept_type` bigint(20) NOT NULL COMMENT '部门类型主键、',
	  `delivery_time` int(11) DEFAULT NULL COMMENT '合同确认交付时间（合同结束日期-开始日期的天数）、',
	  `acceptance_rate` double(15,2) DEFAULT NULL COMMENT '验收节点（合同的完成率）、',
	  `plan_days` double(15,2) DEFAULT NULL COMMENT '计划天数（合同确认交付时间*验收节点）、',
	  `real_days` int(11) DEFAULT NULL COMMENT '实际使用天数（合同结项日期（状态为已结项的更新时间）或统计日期-合同开始日期）、',
	  `bonus_adjust_rate` double(15,2) DEFAULT NULL COMMENT '奖金调节比率（计划天数/实际使用天数-1）、',
	  `bonus_rate` double(15,2) DEFAULT NULL COMMENT '奖金比率（2.3中对应部门类型的提成比率）、',
	  `bonus_acceptance_rate` double(15,2) DEFAULT NULL COMMENT '奖金确认比例（奖金比例*(1+奖金调节比例)*验收节点）、',
	  `bonus_basis` double(15,2) DEFAULT NULL COMMENT '奖金基数（走内部采购单中的来源部门类型的所有（采购成本*部门类型分成比率） 合计）、',
	  `current_bonus` double(15,2) DEFAULT NULL COMMENT '当期奖金（奖金确认比例*奖金基数）',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='产品销售奖金';	

CREATE 
	TABLE `w_contract_internal_purchase` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `project_overall_id` bigint(20) NOT NULL COMMENT '项目总体控制表主键（2.14）、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `dept_type` bigint(20) DEFAULT NULL COMMENT '部门类型主键、',
	  `total_amount` double(15,2) DEFAULT NULL COMMENT '总金额',
	  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='合同内部采购信息';	

CREATE 
	TABLE `w_sales_annual_index` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `user_id` bigint(20) NOT NULL COMMENT '员工主键、',
	  `user_name` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '员工姓名、',
	  `stat_year` bigint(20) DEFAULT NULL COMMENT '所属年份、',
	  `annual_index` double(15,2) DEFAULT NULL COMMENT '年指标',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='销售年指标信息';	
	
CREATE 
	TABLE `w_sales_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `sales_man_id` bigint(20) NOT NULL COMMENT '销售主键、',
	  `sales_man` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '销售姓名、',
	  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同主键、',
	  `origin_year` bigint(20) DEFAULT NULL COMMENT '所属年份',
	  `contract_amount` double(15,2) DEFAULT NULL COMMENT '合同金额、',
	  `tax_rate` double(15,2) DEFAULT NULL COMMENT '税率（合同上的）、',
	  `receive_total` double(15,2) DEFAULT NULL COMMENT '收款金额(收款记录相加总额)、',
	  `taxes_` double(15,2) DEFAULT NULL COMMENT '税收(合同金额*税率 )、',
	  `share_cost` double(15,2) DEFAULT NULL COMMENT '公摊成本（合同金额*公摊比例）、',
	  `third_party_purchase` double(15,2) DEFAULT NULL COMMENT '第三方采购（外部采购成本之和）、',
	  `bonus_basis` double(15,2) DEFAULT NULL COMMENT '奖金基数（收款金额-税收-公摊成本-第三方采购-内部采购总额）、',
	  `bonus_rate` double(15,2) DEFAULT NULL COMMENT '奖金比例（2.3中销售提成比率）、',
	  `current_bonus` double(15,2) DEFAULT NULL COMMENT '本期奖金（奖金基数*奖金比例）、',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='销售奖金';	

CREATE 
	TABLE `w_contract_project_bonus` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `bonus_id` bigint(20) NOT NULL COMMENT '奖金总表主键(2.14)、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `dept_type` bigint(20) NOT NULL COMMENT '部门类型主键、',
	  `bonus_` double(15,2) DEFAULT NULL COMMENT '奖金合计（2.6+2.7之和）',
	  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='合同的项目奖金信息';	
	
CREATE 
	TABLE `w_project_overall` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
	  `stat_week` bigint(20) NOT NULL COMMENT '统计日期、',
	  `contract_response` bigint(20) NOT NULL COMMENT '合同负责人（有销售就是销售，没销售就是咨询）、',
	  `contract_id` bigint(20) NOT NULL COMMENT '合同主键、',
	  `contract_amount` double(15,2) DEFAULT NULL COMMENT '合同金额、',
	  `tax_rate` double(15,2) DEFAULT NULL COMMENT '税率（合同上的）、',
	  `identifiable_income` double(15,2) DEFAULT NULL COMMENT '可确认收入（合同金额*（1-税率））、',
	  `contract_finish_rate` double(15,2) DEFAULT NULL COMMENT '合同完成节点（合同上的完成率）、',
	  `acceptance_income` double(15,2) DEFAULT NULL COMMENT '收入确认（可确认收入*合同完成节点）、',
	  `receive_total` double(15,2) DEFAULT NULL COMMENT '收款金额(收款记录相加总额，同2.10)、',
	  `receivable_account` double(15,2) DEFAULT NULL COMMENT '应收账款（合同金额*合同完成节点-收款金额）、',
	  `share_cost` double(15,2) DEFAULT NULL COMMENT '公摊成本（收款金额*合同上的公摊比例）、',
	  `third_party_purchase` double(15,2) DEFAULT NULL COMMENT '第三方采购（外部采购记录之和、同2.10）、',
	  `internal_purchase` double(15,2) DEFAULT NULL COMMENT '内部采购总额（2.8的记录之和、同2.10）、',
	  `bonus_` double(15,2) DEFAULT NULL COMMENT '奖金(2.13奖金合计)、',
	  `gross_profit` double(15,2) DEFAULT NULL COMMENT '毛利（可确认收入*合同完成节点-公摊成本-第三方采购-内部采购总额-奖金）、',
	  `gross_profit_rate` double(15,2) DEFAULT NULL COMMENT '毛利率（毛利/（可确认收入*合同完成节点））',
	  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='项目总体情况控制表';
	
CREATE 
	TABLE `w_share_info` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `product_price_id` bigint(20) NOT NULL COMMENT '产品定价单主键',
	  `dept_id` bigint(20) NOT NULL COMMENT '部门主键',
	  `dept_name` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '部门名称(除了实施部门外，有可能会有咨询的分成)',
	  `share_rate` double(15,2) DEFAULT NULL COMMENT '分成比例（所有记录之和一定要是100%）',
	  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='分成对象信息表';	
	
CREATE 
	TABLE `w_share_cost_rate` (
	  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	  `dept_type` bigint(20) DEFAULT NULL COMMENT '部门类型ID',
	  `dept_` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '部门类型名称',
	  `contract_type` int(11) DEFAULT NULL COMMENT '合同类型',
	  `share_rate` double(15,2) DEFAULT NULL COMMENT '公摊比例',
	  `creator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	  `updator_` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
	  `update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='奖金公摊成本比例';
	
--20170327
ALTER TABLE w_user_cost ADD (social_security DOUBLE(15,2) DEFAULT '0');
ALTER TABLE w_user_cost ADD (fund_ DOUBLE(15,2) DEFAULT '0');

--UPDATE W_USER_COST SET social_security = ( social_security_fund * 37 /44 );
--UPDATE W_USER_COST SET FUND_ = ( social_security_fund * 7 /44 );

--20170407
CREATE TABLE `w_outsourcing_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键、',
  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同主键、',
  `rank_` varchar(100) DEFAULT NULL COMMENT '员工级别',
  `offer_` double(15,2) DEFAULT NULL COMMENT '报价',
  `target_amount` int(11) DEFAULT NULL COMMENT '目标数量',
  `mark_` varchar(100) DEFAULT NULL COMMENT '唯一标识',
  `updator_` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `creator_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1426 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='外包人员信息表';

ALTER TABLE w_project_user ADD (rank_ varchar(100) DEFAULT NULL);

--20170413
CREATE TABLE
    w_sale_weekly_stat
    (
        id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
        origin_year bigint COMMENT '年份',
        dept_id bigint COMMENT '销售部门',
        annual_index DOUBLE(15,2) COMMENT '合同年指标（该销售部门（包括子部门）该年的所有销售年指标之和）、',
        finish_total DOUBLE(15,2) COMMENT '合同累计完成金额（该销售部门（包括子部门）该年新增的合同金额总和）、',
        receive_total DOUBLE(15,2) COMMENT '当年收款金额（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年收款的金额总和）',
        cost_total DOUBLE(15,2) COMMENT ' 当年新增所有成本、（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年以下所有成本之和）',
        sales_human_cost DOUBLE(15,2) COMMENT '当年销售人工成本、（归属于该销售部门（包括子部门）的所有合同（包括历年合同）的该年人工成本之和、以下雷同）',
        sales_payment DOUBLE(15,2) COMMENT '当年销售报销成本',
        consult_human_cost DOUBLE(15,2) COMMENT '当年咨询人工成本',
        consult_payment DOUBLE(15,2) COMMENT '当年咨询报销成本',
        hardware_purchase DOUBLE(15,2) COMMENT '当年硬件成本',
        external_software DOUBLE(15,2) COMMENT '当年外部软件成本',
        internal_software DOUBLE(15,2) COMMENT '当年内部软件成本',
        project_human_cost DOUBLE(15,2) COMMENT '当年项目人工成本',
        project_payment DOUBLE(15,2) COMMENT '当年项目报销成本',
        stat_week bigint COMMENT '统计周',
        CREATE_time TIMESTAMP NULL COMMENT '统计日期',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
--20170420
    insert into jhi_authority (name, detail_) values ('ROLE_STAT_SALE', '统计报表-销售部门统计');
    --20170420
    CREATE
    TABLE w_system_config
    (
        id bigint NOT NULL AUTO_INCREMENT,
        key_ VARCHAR(200) NOT NULL COMMENT '参数名称',
        value_ VARCHAR(200) NOT NULL COMMENT '参数值,是以逗号分隔的数字',
        description_ VARCHAR(200) NOT NULL COMMENT '父级ID路径，到“父IDPATH/父ID/”。。顶层默认是“/”',
        creator_ VARCHAR(100) COLLATE utf8_bin,
        create_time TIMESTAMP NULL,
        updator_ VARCHAR(100) COLLATE utf8_bin,
        update_time TIMESTAMP NULL,
        PRIMARY KEY (id, key_)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
    insert into w_system_config (id, key_, value_, description_, creator_, create_time, updator_, update_time) values (1, 'dept.sale.topId', '36', '顶级销售部门ID', null, null, 'zhangjun', '2017-04-26 17:48:24');
insert into w_system_config (id, key_, value_, description_, creator_, create_time, updator_, update_time) values (22, 'contract.external.month.day', '22', '外包合同一个月有效工作日', null, null, 'admin', '2017-05-02 15:48:37');

--20170517
	insert into jhi_authority (name, detail_) values ('ROLE_WORKHARDING', '统计报表-员工勤奋度');
	
	alter table w_user_timesheet add extra_input double(15,2) DEFAULT '0' comment '加班工时';
	alter table w_user_timesheet add accept_extra_input double(15,2) DEFAULT '0' comment '认可加班工时';
	alter table w_project_weekly_stat add total_input double(15,2) DEFAULT '0' comment '项目总工时';
	alter table w_project_monthly_stat add total_input double(15,2) DEFAULT '0' comment '项目总工时';
--20170527
	CREATE TABLE w_role_hardworking
    (
        id bigint NOT NULL AUTO_INCREMENT,
        user_id bigint,
        serial_num VARCHAR(10) COLLATE utf8_bin NOT NULL,
        last_name VARCHAR(20) COLLATE utf8_bin NOT NULL,
        hardworking DOUBLE(15,2) NOT NULL,
        origin_month bigint NOT NULL,
        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
--20170605
ALTER TABLE w_contract_cost ADD (input_ DOUBLE(15,2) DEFAULT '0');
ALTER TABLE w_project_cost ADD (input_ DOUBLE(15,2) DEFAULT '0');

--20170614
INSERT INTO jhi_authority (name, detail_) VALUES ('ROLE_PROJECT_INFO_END', '项目信息-立结项');
ALTER TABLE w_user_timesheet ADD character_ int(11) DEFAULT '0' comment '工时成本统计状态(0-没统计前,1-统计后),统计后的记录值不能修改';

--20170628
delete from w_user_timesheet where status_ = 2;
ALTER TABLE w_user_timesheet ADD CONSTRAINT idx_user_timesheet_u UNIQUE (work_day, user_id, type_, obj_id);
ALTER TABLE w_user_timesheet DROP INDEX idx_user_timesheet_u;

--20170629
INSERT INTO jhi_authority (name, detail_) VALUES ('ROLE_STAT_PROJECT_USER_INPUT', '统计报表-项目人员工时');
INSERT INTO jhi_authority (name, detail_) VALUES ('ROLE_STAT_USER_PROJECT_INPUT', '统计报表-人员项目工时');

--20170703
INSERT INTO jhi_authority (name, detail_) VALUES ('ROLE_CONTRACT_END', '合同信息-立结项');
INSERT INTO jhi_authority (name, detail_) VALUES ('ROLE_CONTRACT_BUDGET_EDIT', '合同管理-内部采购单-新增修改');
--20170704
insert into w_system_config (id, key_, value_, description_, updator_, update_time, creator_, create_time) values (2, 'usertimesheet.depttype.transform', '7=1;2=14', '日报填写时，需要转换的用户部门类型到部门公共项目所属部门类型，格式  type1=type2;type3=type2;...', 'admin', '2017-07-04 15:48:37', 'admin', '2017-07-04 15:48:37');
