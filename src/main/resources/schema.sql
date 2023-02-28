drop table if exists customers;

create table customers
(
    id         serial primary key,
    name       text               not null,
    subscribed bool default false not null
);