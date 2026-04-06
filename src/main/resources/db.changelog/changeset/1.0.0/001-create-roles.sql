--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:001-create-roles-table
create table roles
(
    id   bigserial primary key,
    name varchar(50) not null unique
);

--rollback drop table if exists roles;