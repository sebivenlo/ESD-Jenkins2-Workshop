-- this table servers for tests only.
begin work;
drop table if exists test_invoice;
drop table if exists test_invoice_line;
drop sequence if exists test_invoice_invoice_id_seq;
drop sequence if exists test_invoice_line_invoice_line_id_seq;
create sequence test_invoice_invoice_id_seq;
create sequence test_invoice_line_invoice_line_id_seq;

create table test_invoice as select * from invoice where false;
create table test_invoice_line as select * from invoice_line where false;

alter table test_invoice alter column invoice_id set default nextval('test_invoice_invoice_id_seq'::regclass);
alter table test_invoice_line alter column invoice_line_id set default nextval('test_invoice_line_invoice_line_id_seq'::regclass);

alter table test_invoice add constraint test_invoice_pk primary key(invoice_id);
alter table test_invoice_line add constraint test_invoice_line_fk1 foreign key(invoice_id) references test_invoice(invoice_id);
alter table test_invoice_line owner to exam;
alter table test_invoice owner to exam;
alter sequence test_invoice_invoice_id_seq owner to exam;
alter sequence test_invoice_line_invoice_line_id_seq owner to exam;
commit;
