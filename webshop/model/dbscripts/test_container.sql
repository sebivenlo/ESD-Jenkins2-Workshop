-- this table servers for tests only.

drop table if exists test_inventory cascade;
drop table if exists test_carts cascade;
drop table if exists test_invoice cascade;

create table test_inventory  (
    product_id bigint references products(product_id) primary key,
    quantity int NOT NULL CHECK (quantity >= 0) default 0,
    date_creation timestamp default now()::timestamp,
    date_update timestamp default now()::timestamp,
    owner_session bigint not null default 0
)  without oids;
alter table test_inventory owner to exam;

insert into test_inventory 
       (product_id, quantity, date_creation, date_update,owner_session)
       values
       (1,0,default,default,0),
       (2,5,default,default,0),
       (3,100,default,default,0);

create table test_carts (
    product_id bigint references products(product_id),
    quantity int NOT NULL CHECK (quantity >= 0) default 0,
    date_creation timestamp default now()::timestamp,
    date_update timestamp default now()::timestamp,
    owner_session bigint not null default 0,
    primary key(product_id, owner_session)
);
alter table test_inventory owner to exam;
alter table test_carts owner to exam;
