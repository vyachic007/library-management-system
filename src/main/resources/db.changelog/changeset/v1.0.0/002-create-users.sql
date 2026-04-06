--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:002-create-users-table
create table users
(
    id         bigserial primary key,
    username   varchar(100) not null unique,
    password   varchar(255) not null,
    email      varchar(150) not null unique,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    phone      varchar(30),
    is_active  boolean      not null default true
);

--rollback drop table if exists users;