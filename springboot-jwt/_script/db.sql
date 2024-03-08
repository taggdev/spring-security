/*
drop table if exists app_user;
 */
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

--
-- drop table if exists app_role;
--
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

INSERT INTO app_user (id, username, password, display_name, contact_name, contact_tel, enabled, account_non_expired, account_non_locked, credentials_non_expired, last_password_reset, create_dt, create_by, last_upd, upd_by) VALUES (1, 'admin', '{bcrypt}$2a$10$z4NgpeomTdZ32xwbaucayeGak2uI42b1jq6pBHPGki/GqiyXT3tHi', 'admin', '-', '-', 1, 1, 1, 1, null, '2024-03-08 14:50:55', '-', '2024-03-08 17:37:01', 'SYSTEM');
INSERT INTO app_user (id, username, password, display_name, contact_name, contact_tel, enabled, account_non_expired, account_non_locked, credentials_non_expired, last_password_reset, create_dt, create_by, last_upd, upd_by) VALUES (2, 'taggdev', '{bcrypt}$2a$12$BOdTG0iQCEWQEusbD4aBNOLqFRqvYb1H/31P9Ww9dTlAxNZI3rTHC', 'tagg dev', '-', '-', 1, 1, 1, 1, null, '2024-03-08 14:50:55', '-', '2024-03-08 17:37:01', 'SYSTEM');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);

INSERT INTO app_role (id, role_name, description, privileges, create_dt, create_by, last_upd, upd_by) VALUES (1, 'ADMIN', 'admin', '1', '2024-03-08 17:29:01', 'admin', null, null);
INSERT INTO app_role (id, role_name, description, privileges, create_dt, create_by, last_upd, upd_by) VALUES (2, 'USER', 'user', '', '2024-03-08 17:29:01', 'admin', null, null);


select * from app_user;

SHOW FULL COLUMNS FROM app_user;

SHOW CREATE TABLE app_user;
