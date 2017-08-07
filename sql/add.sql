alter table dhb_pay_cut add outBatchId varchar2(32);


alter table DHB_REALNAME add CVN2 varchar2(32);
alter table DHB_REALNAME add VALIDITYTERM varchar2(32);

alter table DHB_REALNAME_FAIL add CVN2 varchar2(32);
alter table DHB_REALNAME_FAIL add VALIDITYTERM varchar2(32);



create table DHB_OUTMERCHANT  (
   MERCHANTID           VARCHAR2(64)                    not null,
   MERNAME              VARCHAR2(128),
   MERFEE               NUMBER(12,2),
   constraint PK_DHB_OUTMERCHANT primary key (MERCHANTID)
);

comment on column DHB_OUTMERCHANT.MERCHANTID is
'商户号，在acqsys做商户入网';

comment on column DHB_OUTMERCHANT.MERNAME is
'商户名称';

comment on column DHB_OUTMERCHANT.MERFEE is
'商户手续费';



--联动优势代扣更新
CREATE TABLECP_BATCH_FILE(BATCHID VARCHAR2(64),FILENAME VARCHAR2(64));

insert into DHB_CHANNEL_INFO (CHANNELID, NAME, BEANNAME) values ('12', '通道六', 'UmpayPayCutService');
insert into DHB_MERCH_CHANNEL (MERCHID, CHANNELID) values ('111301000000000', '12');

CREATE SEQUENCE  "UMPAY_BATCHID_SEQ"  MINVALUE 0 MAXVALUE 999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
CREATE SEQUENCE  "UMPAY_ORDERID_SEQ"  MINVALUE 0 MAXVALUE 999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
   
insert into DHB_PROXY_CARRY_BANKACCT (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME) values ('8', '136001512010007877', '北京中互联科技有限公司', '广发银行股份有限公司南京分行营业部', '306301004007', null, TIMESTAMP '2016-08-31 16:07:07', '100005', '100005', null, null, '12', '1', '联动优势');
 

















