--项目信息表
CREATE TABLE w_project_info
    (
        id bigint NOT NULL AUTO_INCREMENT,
        serial_num varchar(20),
        contract_id bigint,
        budget_id bigint,
        name_ varchar(200),
        pm_ varchar(100),
        dept_ varchar(100),
        start_day timestamp,
        end_day timestamp,
        budget_total double precision(15,2),
        status_ int,
        create_time timestamp,
        creator_ varchar(100),
        update_time timestamp,
        updator_ varchar(100),
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
