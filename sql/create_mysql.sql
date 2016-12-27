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

ALTER TABLE jhi_user ADD (serial_num VARCHAR(10));
ALTER TABLE jhi_user ADD (dept_id BIGINT);
ALTER TABLE jhi_user ADD (is_manager INTEGER);
ALTER TABLE jhi_user ADD (duty_ VARCHAR(100));
ALTER TABLE jhi_user ADD (grade_ VARCHAR(100));
ALTER TABLE jhi_user ADD (gender_ INTEGER);
ALTER TABLE jhi_user ADD (birth_year VARCHAR(4));
ALTER TABLE jhi_user ADD (birth_day VARCHAR(10));
ALTER TABLE jhi_user ADD (telephone_ VARCHAR(20));