--liquibase formatted sql

--changeset vyacheslav_borisov_06_04_2026:009-create-borrow-records-table
create table borrow_records
(
    id           bigserial primary key,
    user_id      bigint      not null,
    book_copy_id bigint      not null,
    borrowed_at  date        not null,
    due_date     date        not null,
    returned_at  date,
    status       varchar(30) not null,
    foreign key (user_id) references users (id),
    foreign key (book_copy_id) references book_copies (id)
);

--rollback drop table if exists borrow_records;