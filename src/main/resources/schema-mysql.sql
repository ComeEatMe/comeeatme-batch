-- Batch
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE;

DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP TABLE IF EXISTS BATCH_JOB_SEQ;

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID           BIGINT        NOT NULL PRIMARY KEY,
    VERSION                    BIGINT,
    JOB_INSTANCE_ID            BIGINT        NOT NULL,
    CREATE_TIME                DATETIME(6)   NOT NULL,
    START_TIME                 DATETIME(6) DEFAULT NULL,
    END_TIME                   DATETIME(6) DEFAULT NULL,
    STATUS                     VARCHAR(10),
    EXIT_CODE                  VARCHAR(2500),
    EXIT_MESSAGE               VARCHAR(2500),
    LAST_UPDATED               DATETIME(6),
    JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    TYPE_CD          VARCHAR(6)   NOT NULL,
    KEY_NAME         VARCHAR(100) NOT NULL,
    STRING_VAL       VARCHAR(250),
    DATE_VAL         DATETIME(6) DEFAULT NULL,
    LONG_VAL         BIGINT,
    DOUBLE_VAL       DOUBLE PRECISION,
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    START_TIME         DATETIME(6)  NOT NULL,
    END_TIME           DATETIME(6) DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       DATETIME(6),
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY)
select *
from (select 0 as ID, '0' as UNIQUE_KEY) as tmp
where not exists(select * from BATCH_STEP_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY)
select *
from (select 0 as ID, '0' as UNIQUE_KEY) as tmp
where not exists(select * from BATCH_JOB_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_SEQ
(
    ID         BIGINT  NOT NULL,
    UNIQUE_KEY CHAR(1) NOT NULL,
    constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY)
select *
from (select 0 as ID, '0' as UNIQUE_KEY) as tmp
where not exists(select * from BATCH_JOB_SEQ);

-- My batch domain
drop table if exists batch_skip_log;

drop table if exists juso_log;

create table batch_skip_log
(
    batch_skip_log_id     bigint      not null auto_increment,
    exception_name        varchar(255),
    exception_message     varchar(2000),
    exception_stack_trace text,
    item                  varchar(2000),
    use_yn                bit         not null,
    created_at            datetime(6) not null,
    last_modified_at      datetime(6) not null,
    primary key (batch_skip_log_id)
) engine = InnoDB;


-- JusoLog
create table juso_log
(
    juso_log_id               bigint        not null auto_increment,
    type                      varchar(15)   not null,
    keyword                   varchar(255)  not null,
    result                    varchar(2000) not null,
    local_data_management_num varchar(45)   not null,
    primary key (juso_log_id)
) engine = InnoDB;

create index IX_juso_log_local_data_management_num on juso_log (local_data_management_num);


-- Domain ----------

drop table if exists restaurant;

drop table if exists local_data;

drop table if exists address_code;

-- AddressCode
create table address_code
(
    code             varchar(15) not null,
    parent_code      varchar(15),
    name             varchar(15) not null,
    full_name        varchar(65) not null,
    depth            int         not null,
    terminal         bit         not null,
    use_yn           bit         not null,
    created_at       datetime(6) not null,
    last_modified_at datetime(6) not null,
    primary key (code)
) engine = InnoDB;

alter table address_code
    add constraint FK_address_code_parent_code
        foreign key (parent_code)
            references address_code (code);

alter table address_code
    add constraint UK_address_code_full_name unique (full_name);

create index IX_address_code_name on address_code (name);

create index IX_address_code_depth on address_code (depth);

-- Restaurant
create table restaurant
(
    restaurant_id     bigint       not null auto_increment,
    name              varchar(100) not null,
    phone             varchar(25)  not null,
    address_name      varchar(255) not null,
    road_address_name varchar(255) not null,
    address_code      varchar(15)  not null,
    use_yn            bit          not null,
    created_at        datetime(6)  not null,
    last_modified_at  datetime(6)  not null,
    primary key (restaurant_id)
) engine = InnoDB;

alter table restaurant
    add constraint FK_restaurant_address_code
        foreign key (address_code)
            references address_code (code);

create index IX_restaurant_name on restaurant (name);


-- LocalData
create table local_data
(
    management_num   varchar(45) not null,
    restaurant_id    bigint      not null,
    service_id       varchar(15) not null,
    name             varchar(15) not null,
    category         varchar(25) not null,
    permission_date  varchar(25) not null,
    closed_date      varchar(25),
    use_yn           bit         not null,
    created_at       datetime(6) not null,
    last_modified_at datetime(6) not null,
    primary key (management_num)
) engine = InnoDB;

alter table local_data
    add constraint UK_local_data_restaurant unique (restaurant_id);
