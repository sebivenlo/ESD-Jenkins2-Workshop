begin work;

drop table if exists nurishments cascade;
drop sequence if exists session_id cascade;
create sequence session_id;
alter sequence session_id owner to exam;

drop table if exists products cascade;
create table products (
       product_id bigserial primary key,
       brand varchar(20),
       description varchar(64) not null,
       production_date date default now()::date - '1 week'::interval,
       price bigint not null check (price >=0),
       vat_level char(1) check (vat_level in ('N', 'L','H')),
       product_type varchar(10) check(product_type in ('BEVERAGE','FOOD','DVD','BD','BOOK','MAGAZINE'))
) without oids;

alter table products owner to exam;

create table nurishments_attributes ( 
    product_id bigint references products(product_id),
    packaging char(10) not null check(packaging in ('BAG','CUP','BOX','TIN','BOTTLE','UNIT')),
    max_shelf_life interval  not null default '2 week',
    calories numeric
 )-- INHERITS (products);

alter table nurishments owner to exam;
drop table if exists food_attributes;

create table food_attributes  (
       product_id bigint references products(product_id),
       packaging_amount_unit  char(3) not null default 'gr' check (packaging_amount_unit in ('gr','kg')),
       packaging_amount real not null default '100' check ( packaging_amount > 0)
) -- INHERITS (nurishments);

alter table food alter column product_type set default 'FOOD';

create table beverage_attributes (
       product_id bigint references products(product_id),
       packaging_amount_unit  char(3) not null default 'l' check (packaging_amount_unit in ('ml','l')),
       packaging_amount real not null default '1' check ( packaging_amount > 0),
       minimum_age interval default '0 days'
)-- INHERITS (nurishments);

-- alter table beverage alter column product_type set default 'BEVERAGE';
-- alter table beverage alter column packaging set default 'TIN';

alter table nurishments add constraint nurishment_type_check check(product_type in ('BEVERAGE','FOOD'));
alter table beverage add constraint beverage_type_check check(product_type in ('BEVERAGE'));
alter table food add constraint food_type_check check(product_type in ('BEVERAGE'));

drop table if exists inventory cascade;
create table inventory (
    product_id bigint references products(product_id) primary key,
    quantity int NOT NULL CHECK (quantity >= 0) default 0,
    date_creation timestamp default now()::timestamp,
    date_update timestamp default now()::timestamp,
    owner_session bigint not null default 0 check (owner_session = 0)
)  without oids;
alter table inventory owner to exam;

comment on table inventory is 'The stock we want to sell. No quantity shall be less then 0';

drop table if exists cart cascade;
create table cart (
    product_id bigint references products(product_id),
    quantity int NOT NULL CHECK (quantity >= 0) default 0,
    date_creation timestamp default now()::timestamp,
    date_update timestamp default now()::timestamp,
    owner_session bigint not null default 1 check (owner_session >0),
    primary key(product_id, owner_session)
)  without oids;
alter table cart owner to exam;
comment on table cart is 'This is what the customer is potentially buying';
comment on column cart.quantity is 'non negative check';
comment on column cart.owner_session is 'owner, (web) session or customer, it all fits';

drop view if exists inventory_view;
CREATE OR REPLACE VIEW inventory_view AS 
 SELECT products.product_id, 
    products.description, 
    products.price, 
    products.vat_level, 
    inventory.quantity
   FROM products
   JOIN inventory USING (product_id);

ALTER TABLE inventory_view
  OWNER TO exam;
  
drop table if exists invoice cascade;
create table invoice(
    invoice_id bigserial primary key,
    invoice_date date default now()::date
) without oids;
alter table invoice owner to exam;

comment on table invoice is 'to keep this simple, customer details and such have been explicitly left out of invoice, it is more like a cashreceipt';

drop table if exists invoice_line cascade;
create table invoice_line (
    invoice_line_id bigserial primary key,
    invoice_id bigint references invoice(invoice_id),
    quantity integer not null check( quantity >=0 ),
    product_id bigint references products(product_id),
    sales_price bigint not null check(sales_price >= 0),
    vat_amount bigint not null check (vat_amount >= 0) 
       )   without oids;
alter table invoice_line  owner to exam;

commit;
begin work;
insert into products (description,brand, price, vat_level,product_type) 
       values('The Davinci code', 'Columbia Pictures',2500, 'H','BD');
insert into products (description, price, vat_level,product_type) 
       values('Inferno', 3000, 'H','DVD');
insert into products (description, price, vat_level,product_type) 
       values('Battleship', 2000, 'H','DVD');
insert into products (description, price, vat_level,product_type) 
       values('Crisps', 100, 'L', 'FOOD');
insert into products (description, price, vat_level,product_type) 
       values('Coca Cola', 250, 'L','BEVERAGE');
insert into products (description, price, vat_level,product_type) 
       values('Kingsman: The secret Service', 2500, 'H','BD');
insert into products (description, price, vat_level,product_type) 
       values('Popcorn', 120, 'L','FOOD');
insert into products (description, price, vat_level,product_type) 
       values('Twix', 90, 'L','FOOD');
insert into beverage(brand,description,price,vat_level,packaging,packaging_amount_unit, packaging_amount,max_shelf_life,calories,minimum_age)
       values ('Warsteiner','Premium Pilsener', 98,'H','TIN','ml',500,'1 month',225,'18 year');

insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (1, 5, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (2, 10, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (3, 12, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (4, 100, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (5, 100, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (6, 2, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (7, 45, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (8, 31, default, default, 0);
insert into inventory (product_id, quantity, date_creation, date_update, owner_session) 
       values (9, 60, default, default, 0);
commit;
