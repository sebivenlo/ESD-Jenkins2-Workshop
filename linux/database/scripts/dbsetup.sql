create user olifantys createdb password 'olifantys';
create database olifantysbank owner olifantys;
create role teller login password 'teller';

-- a postgresql enum type is very much like a Java enum. Limitted set of values and type safe.

CREATE TYPE bankevent AS ENUM('createcustomer', 'createaccount', 
    'openaccount', 'freezeaccount', 'closeaccount', 'deposit',
    'withdraw','block','unblock','endcustomer');
comment on type bankevent is 'the events that are used in the stored procedures
The name of a domain is singular, not plural because it defines a type, if not, the using tables would have
a columntype definition with an indication of plurar, which it is not.';

CREATE TYPE accountstate AS ENUM('non-existent','frozen', 'open', 'closed');
COMMENT on TYPE accountstate is 'the states of an account';

CREATE TYPE customerstate AS ENUM ('non-existent','exists', 'blocked', 'ended');
COMMENT on TYPE customerstate is 'the states of a customer';

CREATE TABLE accountstatemachine (
    startstate accountstate,
    event bankevent NOT NULL ,--default 'createcustomer' ,
    endstate accountstate NOT NULL,
    PRIMARY KEY(startstate,event)
);

COMMENT ON TABLE accountstatemachine IS 'The statmachine table for the account, with start state, event and end state
The state/event combinations define the legal combinations.
The semantics for unkown state/event combinations should be raising exceptions.
';

CREATE TABLE customerstatemachine (
    startstate customerstate,
    event bankevent NOT NULL,
    endstate customerstate NOT NULL,
		PRIMARY KEY(startstate,event)
);

INSERT INTO accountstatemachine (startstate,event,endstate) VALUES
    ('non-existent','createaccount','frozen'),
    ('frozen','deposit','frozen'),
    ('frozen','openaccount','open'),
    ('open','openaccount','open'), -- nop, but allowed
    ('frozen','closeaccount','closed'),
    ('open','freezeaccount','frozen'),
    ('frozen','freezeaccount','frozen'),
    ('open','deposit','open'),  -- nop but allowed
    ('open','withdraw','open');

INSERT INTO customerstatemachine (startstate,event,endstate) VALUES
    ('non-existent','createcustomer','exists'),
    ('exists','createaccount','exists'),
    ('exists','openaccount','exists'),
    ('exists','freezeaccount','exists'),
    ('exists','closeaccount','exists'),
    ('exists','deposit','exists'),
    ('exists','withdraw','exists'),
    ('exists','block','blocked'),
    ('blocked','block','blocked'), -- nop but allowed
    ('exists','endcustomer','ended'),
    ('blocked','closeaccount','blocked'),
    ('blocked','deposit','blocked'),
    ('blocked','freezeaccount','blocked'),
    ('blocked','unblock','exists'),
    ('exists','unblock','exists'), -- nop but allowed
    ('blocked','endcustomer','ended');


CREATE TABLE customer (
    customerid serial PRIMARY KEY,
    name text NOT NULL,
    postcode text NOT NULL,
    cstate customerstate NOT NULL
);

CREATE TABLE account (
    accountid serial PRIMARY KEY,
    balance numeric NOT NULL,
    maxdebit numeric default 0.0 not null check(maxdebit >=0),
    customerid integer NOT NULL references customer(customerid) on update cascade on delete restrict,
    astate accountstate NOT NULL,
    accountdescription text default 'Olifantys Bank Premium Account',
    check (balance + maxdebit >= 0)
);

CREATE TABLE    transactions (
     transid serial primary key,
     amount numeric,
    receiver integer references account(accountid) on update cascade on delete restrict,
    donor integer references account(accountid) on update cascade on delete restrict,
    description text,
    ts timestamp default now()
);


CREATE VIEW customeraccountallowedstateevent AS
SELECT c.customerid,c.cstate,a.accountid,a.balance,a.astate , csm.event as cevent, asm.event as aevent,
    csm.endstate as cendstate, asm.endstate as aendstate
   FROM customer c
   JOIN account  a using(customerid)
   LEFT JOIN customerstatemachine csm on c.cstate=csm.startstate
   LEFT JOIN accountstatemachine asm on a.astate=asm.startstate;
COMMENT ON VIEW customeraccountallowedstateevent is 'shows the allowed event combinations for account and customer';

CREATE VIEW customerallowedstateevent as
 SELECT c.customerid,c.cstate, csm.event as cevent,
    csm.endstate as cendstate
   FROM customer c
   JOIN customerstatemachine csm on c.cstate=csm.startstate;

COMMENT ON VIEW customerallowedstateevent IS 'shows the allowed event combinations for customer';

CREATE VIEW customerjoinaccount as select * from customer join account using(customerid);

CREATE VIEW accountevent as select a.accountid,a.balance,a.maxdebit,a.customerid,a.astate,null::bankevent as event from account a where false;

DROP VIEW if exists mytransactions;
CREATE VIEW mytransactions as 
select t1.donor as accountid, t1.receiver as otherparty,t1.transid, null as credit,t1.amount as debit,
t1.description, t1.ts as datetime, a1.balance , c1.name as otherpartyname
from transactions t1 
   join account a1 on (t1.receiver=a1.accountid)
   join customer c1 on(a1.customerid=c1.customerid)
union
select t2.receiver as accountid, t2.donor as otherparty,t2.transid, t2.amount as credit, null as debit,
t2.description, t2.ts as datetime, a2.balance  ,c2.name as otherpartyname
from transactions t2 
   join account a2 on (t2.donor=a2.accountid) 
   join customer c2 on(a2.customerid=c2.customerid)
;


grant select,references on account,customer,transactions,mytransactions to teller;

CREATE OR REPLACE FUNCTION updateaccountstate(accid integer, bankevent bankevent) RETURNS accountstate
    LANGUAGE plpgsql
    SECURITY DEFINER

    AS $updateaccountstate$
DECLARE
    staterecord customeraccountallowedstateevent%rowtype;
BEGIN
    select *
    into staterecord
    from customeraccountallowedstateevent caase
    where accountid=accid
    and bankevent= aevent and bankevent=cevent;
    if not found then
        raise exception 'account is not found or in state which does not allow the % event',bankevent;
    end if;
    raise notice 'changing account % to % state',accid, staterecord.aendstate;
    update account acc
	set astate = staterecord.aendstate
    where accountid = accid;
    return staterecord.aendstate;
END; $updateaccountstate$;

CREATE OR REPLACE FUNCTION updatecustomerstate(custid integer, bankevent bankevent) RETURNS void
    LANGUAGE plpgsql
    AS $updatecustomerstate$
DECLARE
    currentstate customer.cstate%type;
    endstate customer.cstate%type;
    staterecord customerallowedstateevent%rowtype;
BEGIN
    select *
    into staterecord
    from customerallowedstateevent
    where customerid=custid;
    if not found then
        raise exception 'customer is in state which does not allow the "%" event',bankevent;
    end if;

    raise notice 'changing customer % to % state',custid, staterecord.cendstate;
    update customer cust
    set cstate = staterecord.cendstate
where cust.customerid = custid;
END;
$updatecustomerstate$;

show application_name;

CREATE FUNCTION createcustomer(name text, postcode text) RETURNS customer
LANGUAGE plpgsql
SECURITY DEFINER

AS $createcustomer$
DECLARE
    initialstate customer.cstate%type;
    rec customer;
    cid integer;
BEGIN
    select nextval('customer_customerid_seq'::regclass) into cid;
    SELECT endstate
    INTO initialstate
    FROM customerstatemachine
    WHERE startstate='non-existent';
    INSERT INTO customer (customerid,name,postcode,cstate) VALUES (cid,name,postcode,initialstate)
    returning * into rec;
    return rec;
END;
$createcustomer$;


CREATE OR REPLACE FUNCTION block(custid integer) RETURNS customerstate
LANGUAGE plpgsql
SECURITY DEFINER

AS $block$
BEGIN
    return 'blocked';
    perform updatecustomerstate(custid,'block');
END;
$block$;

CREATE OR REPLACE FUNCTION unblock(custid integer) RETURNS customerstate
LANGUAGE plpgsql
SECURITY DEFINER

AS $unblock$
BEGIN
    perform updatecustomerstate(custid,'unblock');
    return 'exists';
END;
$unblock$;

CREATE OR REPLACE FUNCTION openaccount(accid integer) RETURNS accountstate
LANGUAGE plpgsql
SECURITY DEFINER

AS $openaccount$
BEGIN
    perform updateaccountstate(accid,'openaccount');
    return 'open';
END;
$openaccount$;


CREATE OR REPLACE FUNCTION createaccount(custid integer, initialbalance numeric, description text) RETURNS account
LANGUAGE plpgsql
SECURITY DEFINER
AS $createaccountf$
DECLARE
    initialstate account.astate%type;
    rec account;
BEGIN
    perform updatecustomerstate(custid,'createaccount'); -- ensure legality
    SELECT endstate
    INTO initialstate
    FROM accountstatemachine
    WHERE startstate='non-existent';
    IF description is null then
       INSERT INTO account (customerid,balance,astate)
       VALUES (custid,initialbalance,initialstate) returning * into rec ;
    ELSE
	INSERT INTO account (customerid,balance,astate,accountdescription)
    	VALUES (custid,initialbalance,initialstate,description ) returning * into rec ;
    END IF;
return rec;
END;
$createaccountf$;

CREATE OR REPLACE FUNCTION createaccount(custid integer, initialbalance numeric) RETURNS account
LANGUAGE plpgsql
SECURITY DEFINER

AS $createaccountf$
DECLARE
   rec account;
BEGIN
	select * from createaccount(custid, initialbalance,null::text) into rec;
	return rec;
END;
$createaccountf$;

create or replace function getaccountevent (ac integer, event bankevent, out eventresult accountevent) returns accountevent
LANGUAGE plpgsql
AS $getaccountevent$

DECLARE 
BEGIN
    select accountid , balance, maxdebit , customerid , 
            astate, event
        from account 
        where accountid = ac into eventresult for update;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'account % does not exists',ac;
    END IF;
    return;
END; $getaccountevent$;

create or replace function getaccounteventtest() returns void
language plpgsql
as $getaccounteventtest$
declare  
  tresult accountevent%rowtype;
  aresult record;
begin
    tresult := getaccountevent(100,'withdraw'::bankevent);
    select * from customer where customerid = tresult.customerid into aresult;
    raise notice 'tresult %  ',aresult;
    return;
end; $getaccounteventtest$;

create or replace rule bankingactionupdate  as
    ON UPDATE to customeraccountallowedstateevent
    DO instead (
        update account set balance= new.balance where accountid=new.accountid;
        );

create or replace function checkbankingrules(donorevent accountevent, transactionrole text) returns void
language plpgsql
SECURITY DEFINER
as $checkbankingrules$
declare
    fromstaterecord record;
begin
    with dasm as (
        select *,donorevent.accountid,donorevent.balance 
        from accountstatemachine asm 
        where startstate=donorevent.astate and asm.event=donorevent.event
    ), dcsm as (
        select customerid,cstate,event,endstate 
        from customer cust 
        left join customerstatemachine csm on csm.startstate=cust.cstate 
        where customerid=donorevent.customerid 
    ) 
    select acc.accountid, c.customerid, 
        acc.astate, 
        dasm.event as aevent, 
        dasm.endstate as aendstate, 
        c.cstate, 
        dcsm.event as cevent, 
        dcsm.endstate as cendstate, 
        acc.balance
    from account acc join customer c using(customerid)
    left join dasm on acc.astate=dasm.startstate and acc.accountid=dasm.accountid
    left join dcsm on dcsm.customerid=c.customerid
    where acc.accountid = donorevent.accountid and c.customerid=dcsm.customerid and dcsm.event=donorevent.event
    into fromstaterecord;
    
    -- raise notice 'staterecord (a,c,as,ae,aes,cs,ce,ces,bal)= %',fromstaterecord;

    IF fromstaterecord.aendstate isnull OR fromstaterecord.cendstate ISNULL then
        raise exception 'not obeying banking rules for event "%", role "%" customer (%) in state "%", account (%) in state "%"', 
        donorevent.event,transactionrole,fromstaterecord.customerid,fromstaterecord.cstate, fromstaterecord.accountid,donorevent.astate
        using errcode='dataexception';
    end if;

end; $checkbankingrules$;

CREATE OR REPLACE FUNCTION transferflockfree(froma integer, toa integer, amount  numeric, reason text) RETURNS int

LANGUAGE plpgsql
SECURITY DEFINER
AS $transfer$
DECLARE
    fromstaterecord record;--customeraccountallowedstateevent%rowtype;
    tostaterecord   record;-- customeraccountallowedstateevent%rowtype;
    donorevent    accountevent;
    receiverevent accountevent;
    trans_id int;
BEGIN
    -- check validity of amount
    if amount <= 0 then
        raise exception '% is an illegal amount for a transfer',amount;
    end if;
    -- check conditions for accounts from and to
    -- get the relevant info in a fixed order, set by accountid
    -- starting with lowest number avoids deadlocks.
    if fromA < toA then
        -- from first
        donorevent := getaccountevent(fromA, 'withdraw'::bankevent);
        receiverevent := getaccountevent(toA, 'deposit'::bankevent);
    else 
        -- to first
        receiverevent := getaccountevent(toA, 'deposit'::bankevent);
        donorevent := getaccountevent(fromA, 'withdraw'::bankevent);
    end if;
    
    if donorevent.balance + donorevent.maxdebit - amount < 0 then
        raise EXCEPTION 'from account #% payment rule violation balance = %,maxdebit = %, amount = %', froma, donorevent.balance,event.maxdebit , amount;
    end if;

    -- check donor account and customer
    perform checkbankingrules(donorevent,'donor');
    -- check receiver account and customer
    perform checkbankingrules(receiverevent,'receiver');

    -- checks if withdrawal and deposit is allowed is now complete.
    select nextval('transactions_transid_seq'::regclass) into trans_id;

    insert into transactions (transid,amount,receiver,donor,description,ts)
        values(trans_id,amount,toa,froma,reason,now()::timestamp);

    update customeraccountallowedstateevent
        set balance = balance-amount
        where accountid = froma and cevent='withdraw' and aevent='withdraw';

    update customeraccountallowedstateevent
        set balance = balance+amount
        where accountid = toa and cevent='deposit' and aevent='deposit';

    return trans_id;
END; $transfer$;

CREATE OR REPLACE FUNCTION transfer(froma integer, toa integer, amount  numeric, reason text, out transid int) RETURNS int
LANGUAGE plpgsql
SECURITY DEFINER

AS $withdraw$
BEGIN 
    transid= transferflockfree(froma, toa,  amount, reason);
END; $withdraw$;

CREATE OR REPLACE FUNCTION transferv(froma integer, toa integer, amount  numeric, reason text) RETURNS mytransactions
LANGUAGE plpgsql
SECURITY DEFINER

AS $withdraw$
DECLARE 
  rec mytransactions;
  trans_id int;
BEGIN 
    trans_id= transferflockfree(froma, toa,  amount, reason);
    SELECT * FROM mytransactions WHERE accountid=froma and transid=trans_id into rec;
    RETURN rec;
END; $withdraw$;



CREATE OR REPLACE FUNCTION withdraw(in froma integer, in amount  numeric, in reason text, out transid int ) RETURNS int
LANGUAGE plpgsql
SECURITY DEFINER

AS $withdraw$
declare 
    bankaccount integer;
    transid int;
begin
    bankaccount=99999999;
    transid=transferflockfree(froma,bankaccount,amount,reason);
end; $withdraw$;

CREATE OR REPLACE FUNCTION deposit(in toa integer, in amount  numeric, in reason text, out transid int ) RETURNS int
LANGUAGE plpgsql
SECURITY DEFINER

AS $withdraw$
declare 
   bankaccount integer;
   transid int;
begin
    bankaccount=99999999;
    transid = transferflockfree(bankaccount,toa,amount,reason);
end; $withdraw$;

select createcustomer('Oliver Olifantys, Banker','5911 CX') where not exists (select 1 from customer where name like 'Oliver%');
select createcustomer('Geert Monsieur','3630') where not exists (select 1 from customer where name like 'Geert%');
select createcustomer('Pieter van den Hombergh','5913 WH') where not exists (select 1 from customer where name like 'Pieter%');
select createcustomer('Thijs Dorssers','5913 WH') where not exists (select 1 from customer where name like 'Thijs%');
select createcustomer('Fontys Hogescholen','5410 PG') where not exists (select 1 from customer where name like 'Fontys%');
select * from customer;
-- create an account with a high number

insert into account (accountid,balance,customerid,maxdebit,astate,accountdescription) select 99999999,1000000,1,5*1000000, 'open','Bankers Master Account, caveat tax payer'
  where not exists (select 1 from account where accountid=99999999);
select * from customerjoinaccount;

select createaccount(2,1000,'salaris rekening'); -- mon starts with 1000
select createaccount(2,3000,'spaar rekening'); -- mon ac 2 starts with 3000
select createaccount(3,500,'salaris rekening'); -- hom with 500
select createaccount(3,2500,null); -- hom ac 2with 2500
select createaccount(4,500); -- dos with 500
select createaccount(5,250000, 'Business account'); -- fontys dos ac 2with 250000

select * from customerjoinaccount order by customerid,accountid;

CREATE OR REPLACE function testtransfer(mon integer,hom integer) RETURNS int
LANGUAGE plpgsql
as $testtransfer$
declare
    monaccount account%rowtype;
    homaccount account%rowtype;
    amount numeric;
    reason text;
    transid int;
begin

    amount :=500;
    reason := 'Test 1';

select a.* into monaccount
    from account a join customer c using(customerid) 
    where accountid = (select min(accountid) from account where customerid=mon) ;
    
    select a.* into homaccount
    from account a join customer c using(customerid) 
    where accountid = (select min(accountid) from account where customerid=hom) ;
    raise notice ' boe % ', monaccount;
    raise notice  'about to transfer % from % to % with reason "%"',amount,homaccount,monaccount,reason;
    update account set astate='open' where accountid in (monaccount.accountid, homaccount.accountid);
    transid := transferflockfree(homaccount.accountid,monaccount.accountid,amount,reason);
    raise notice 'transid %', transid;
    return transid;
end; $testtransfer$;
commit;

select * from customer;
select unblock( 2 );
select unblock( 3 );
update account set astate='open';
select testtransfer(2,3);
select * from mytransactions;
select * from transactions;
select testtransfer(3,2);
select * from transactions;
select * from customerjoinaccount;

insert into account(accountid,balance,maxdebit,customerid,astate,accountdescription)
  values(100,500,1500,1,'open','test account');
select a.* from account a where accountid=100;
select (getaccountevent(100,'deposit'::bankevent)).*;
select getaccounteventtest();
delete from account where accountid=100;

