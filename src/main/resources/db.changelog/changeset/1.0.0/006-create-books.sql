--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:006-create-books-table
create table books
(
    id               bigserial primary key,
    title            varchar(255) not null,
    isbn             varchar(50)  not null unique,
    description      text,
    publication_year integer,
    category_id      bigint       not null,
    foreign key (category_id) references categories (id)
);

--rollback drop table if exists books;