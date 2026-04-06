--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:005-create-authors-table
create table authors
(
    id         bigserial primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null
);

--rollback drop table if exists authors;