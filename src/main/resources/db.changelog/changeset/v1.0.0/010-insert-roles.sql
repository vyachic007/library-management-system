--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:010-insert-roles
insert into roles (name)
values ('ROLE_USER');
insert into roles (name)
values ('ROLE_ADMIN');

--rollback delete from roles where name in ('ROLE_USER', 'ROLE_ADMIN');