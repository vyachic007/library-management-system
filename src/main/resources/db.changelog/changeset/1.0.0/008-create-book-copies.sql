--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:008-create-book-copies-table
create table book_copies
(
    id                    bigserial primary key,
    book_id               bigint       not null,
    inventory_number      varchar(100) not null unique,
    status                varchar(30)  not null,
    condition_description text,
    foreign key (book_id) references books (id) on delete cascade
);

--rollback drop table if exists book_copies;