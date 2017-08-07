create sequence chinaPay_seq
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;

create sequence cp_batch_seq
minvalue 1
maxvalue 9999999
start with 1
increment by 1
CYCLE
CACHE 10;

create table cp_batch_file(
batchId varchar2(36) primary key,
fileName varchar(40)
);

create sequence dhb_skip_seq
minvalue 1
maxvalue 999
start with 1
increment by 1
CYCLE
CACHE 10;
create sequence dhb_skip_batch_seq
minvalue 1
maxvalue 999
start with 1
increment by 1
CYCLE
CACHE 10;