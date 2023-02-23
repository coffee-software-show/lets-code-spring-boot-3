#!/usr/bin/env bash

echo "create table if not exists customers(id serial primary key, name text unique not null ,subscribed bool default false not null);" | PGPASSWORD=postgres psql -U postgres -h localhost postgres
echo "insert into customers (name) values('Josh'); " |PGPASSWORD=postgres psql -U postgres -h localhost postgres
echo "insert into customers(name) values( 'Ashkan') " | PGPASSWORD=postgres psql -U postgres -h localhost postgres
echo "insert into customers(name) values('Deepak')" | PGPASSWORD=postgres psql -U postgres -h localhost postgres
echo "insert into customers(name) values('Kunal') " | PGPASSWORD=postgres psql -U postgres -h localhost postgres
echo "insert into customers(name) values('Amit') " | PGPASSWORD=postgres psql -U postgres -h localhost postgres  
