--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:004-create-categories-table
create table categories
(
    id        bigserial primary key,
    name      varchar(150) not null,
    parent_id bigint,
    foreign key (parent_id) references categories (id) on delete set null
);

--rollback drop table if exists categories;