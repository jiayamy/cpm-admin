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
        PRIMARY KEY (id),
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
---20170119
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
    
    insert into jhi_authority (name) values ('ROLE_ADMIN');
	insert into jhi_authority (name) values ('ROLE_CONTRACT');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_BUDGET');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_COST');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_FINISH');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_INFO');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_PRODUCTPRICE');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_PURCHASE');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_RECEIVE');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_TIMESHEET');
	insert into jhi_authority (name) values ('ROLE_CONTRACT_USER');
	insert into jhi_authority (name) values ('ROLE_INFO');
	insert into jhi_authority (name) values ('ROLE_INFO_BASIC');
	insert into jhi_authority (name) values ('ROLE_INFO_USERCOST');
	insert into jhi_authority (name) values ('ROLE_PROJECT');
	insert into jhi_authority (name) values ('ROLE_PROJECT_COST');
	insert into jhi_authority (name) values ('ROLE_PROJECT_FINISH');
	insert into jhi_authority (name) values ('ROLE_PROJECT_INFO');
	insert into jhi_authority (name) values ('ROLE_PROJECT_TIMESHEET');
	insert into jhi_authority (name) values ('ROLE_PROJECT_USER');
	insert into jhi_authority (name) values ('ROLE_STAT');
	insert into jhi_authority (name) values ('ROLE_STAT_CONTRACT');
	insert into jhi_authority (name) values ('ROLE_STAT_PROJECT');
	insert into jhi_authority (name) values ('ROLE_TIMESHEET');
	insert into jhi_authority (name) values ('ROLE_USER');
	insert into jhi_authority (name) values ('ROLE_USERCOST');

	insert into jhi_user (id, login, password_hash, first_name, last_name, email, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date, dept_id, is_manager, duty_, grade_, gender_, birth_year, birth_day, telephone_, serial_num) values (3, 'admin', '$2a$10$NCJxXpFlvH9wBz56sUBCFeKSA9oseH7/YTsLfplfkQfZUClIBfGdq', 'Administrator', '员工3', 'admin@localhost', '1', 'zh-cn', null, null, 'system', '2016-12-19 10:29:53', null, 'admin', '2016-12-19 18:53:24', 16, '0', null, null, null, null, null, null, '3');

	insert into jhi_user_authority (user_id, authority_name) values (1, 'ROLE_ADMIN');
	insert into jhi_user_authority (user_id, authority_name) values (1, 'ROLE_USER');

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
	insert into w_dept_type (id, name_) values (11, '测试一下');
	
--20170119
	ALTER TABLE jhi_user ADD (work_area VARCHAR(100))