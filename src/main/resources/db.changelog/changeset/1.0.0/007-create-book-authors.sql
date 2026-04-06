--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:007-create-book-authors-table
create table book_authors
(
    book_id   bigint not null,
    author_id bigint not null,
    primary key (book_id, author_id),
    foreign key (book_id) references books (id) on delete cascade,
    foreign key (author_id) references authors (id) on delete cascade
);

--rollback drop table if exists book_authors;