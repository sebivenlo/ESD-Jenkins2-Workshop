-- this table servers for tests only.
begin work;
drop table if exists test_invoices cascade;
drop table if exists test_invoice_lines cascade;
drop sequence if exists test_invoices_invoice_id_seq;
drop sequence if exists test_invoice_lines_invoice_line_id_seq;
create sequence test_invoices_invoice_id_seq;
create sequence test_invoice_lines_invoice_line_id_seq;

create table test_invoices as select * from invoices where false;
create table test_invoice_lines as select * from invoice_lines where false;

alter table test_invoices alter column invoice_id set default nextval('test_invoices_invoice_id_seq'::regclass);
alter table test_invoice_lines alter column invoice_line_id set default nextval('test_invoice_lines_invoice_line_id_seq'::regclass);

alter table test_invoices add constraint test_invoice_pk primary key(invoice_id);
alter table test_invoice_lines add constraint test_invoice_line_fk1 foreign key(invoice_id) references test_invoices(invoice_id);
alter table test_invoice_lines owner to exam;
alter table test_invoices owner to exam;
alter sequence test_invoices_invoice_id_seq owner to exam;
alter sequence test_invoice_lines_invoice_line_id_seq owner to exam;
commit;
