drop table if exists app_user;

create table app_user
(
    id                      int auto_increment
        primary key,
    username                varchar(50)                              not null,
    password                varchar(256)                             not null,
    display_name            varchar(120)                             not null,
    contact_name            varchar(200) default '-'                 not null,
    contact_tel             varchar(50)  default '-'                 not null,
    enabled                 tinyint(1)   default 1                   not null,
    account_non_expired     tinyint(1)   default 1                   not null,
    account_non_locked      tinyint(1)   default 1                   not null,
    credentials_non_expired tinyint(1)   default 1                   not null,
    last_password_reset     datetime                                 null,
    create_dt               datetime     default current_timestamp() not null,
    create_by               varchar(50)                              not null,
    last_upd                datetime                                 null,
    upd_by                  varchar(50)                              null,
    constraint UQ_APP_USER_USERNAME
        unique (username)
);

drop table if exists app_role;

create table app_role
(
    id          int auto_increment
        primary key,
    role_name   varchar(100)                         not null,
    description varchar(255)                         null,
    privileges  text                                 null,
    create_dt   datetime default current_timestamp() not null,
    create_by   varchar(50)                          not null,
    last_upd    datetime                             null,
    upd_by      varchar(50)                          null,
    constraint UQ_APP_ROLE_ROLE_NAME
        unique (role_name)
);



create table login_attempts
(
    id       int         null,
    username varchar(50) null,
    attempts tinyint     null,
    last_upd datetime    null
);

create table user_role
(
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id)
);



select * from app_user;

SHOW FULL COLUMNS FROM app_user;

SHOW CREATE TABLE app_user;
