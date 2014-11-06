drop keyspace if exists streaming;

create keyspace streaming with replication = { 'class': 'SimpleStrategy', 'replication_factor': 3 };

use streaming;

create table accounts (
    acc_id bigint primary key,
    first_name text,
    last_name text,
    date_created timestamp,
    balance float
);

create table transactions (
    id bigint primary key,
    time timestamp,
    type int,
    out boolean,
    acc1 bigint,
    acc2 bigint,
    amount float,
    descr text
);