--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:003-create-user-roles-table
create table user_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id) on delete cascade,
    foreign key (role_id) references roles (id) on delete cascade
);

--rollback drop table if exists user_roles;