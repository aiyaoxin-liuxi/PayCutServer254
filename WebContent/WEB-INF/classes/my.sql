-- Create table
create table DHB_BIZ_JOURNAL
(
  ID             VARCHAR2(32) not null,
  MERCHID        VARCHAR2(20),
  BIZTYPE        VARCHAR2(3),
  CHANNELID      VARCHAR2(1),
  RECORDID       VARCHAR2(32),
  FROMBANKCODE   VARCHAR2(20),
  FROMBANKNAME   VARCHAR2(50),
  FROMBANKCARDNO VARCHAR2(30),
  FROMUSERNAME   VARCHAR2(50),
  FROMIDENTITYNO VARCHAR2(30),
  TOBANKCODE     VARCHAR2(20),
  TOBANKNAME     VARCHAR2(50),
  TOBANKCARDNO   VARCHAR2(30),
  TOUSERNAME     VARCHAR2(50),
  TOIDENTITYNO   VARCHAR2(30),
  MONEY          NUMBER(12,2),
  CHARGEMODE     VARCHAR2(1),
  FEE_RATE       NUMBER(5,4),
  FEE            NUMBER(12,2),
  CURRENCY       VARCHAR2(3) default 'CNY',
  MEMO           VARCHAR2(100),
  CREATETIME     DATE,
  HANDLESTATUS   VARCHAR2(10),
  HANDLEREMARK   VARCHAR2(100),
  BATCHID        VARCHAR2(32),
  ENDTIME        DATE
);
alter table DHB_BIZ_JOURNAL
add primary key (ID);
create table DHB_PROXY_BATCH_RECORD
(
  BATCHID        VARCHAR2(32) not null,
  MERCHID        VARCHAR2(20),
  CHANNELID      VARCHAR2(2),
  BIZTYPE        VARCHAR2(1),
  TOTALNUM       NUMBER(8),
  TOTALMONEY     NUMBER(12,2),
  TOTALSUCCNUM   NUMBER(8),
  TOTALSUCCMONEY NUMBER(12,2),
  CREATETIME     DATE,
  CREATEID       VARCHAR2(10),
  REVIEWTIME     DATE,
  REVIEWID       VARCHAR2(10),
  REVIEWSTATUS   VARCHAR2(1),
  REVIEWCOMMENTS VARCHAR2(500),
  STATUS         VARCHAR2(2),
  REMARK         VARCHAR2(254),
  FILENAME       VARCHAR2(30),
  OUTBATCHID     VARCHAR2(32)
);
alter table DHB_PROXY_BATCH_RECORD
add constraint DHB_BATCH_RECORD_PK primary key (BATCHID);

create table Dhb_Proxy_pay_acp(id varchar2(32) primary key,
merchId varchar2(20),
bizType varchar2(3),
channelId varchar2(1),
contractNo varchar2(10),
fromBankCode varchar2(10),
fromBankName varchar2(50),
fromBankCardNo varchar2(30),
fromUserName varchar2(50),
fromIdentityNo varchar2(30),
toBankCode varchar2(10),
toBankName varchar2(50),
toBankCardNo varchar2(30),
toUserName varchar2(50),
toIdentityNo varchar2(30),
money number(12,2),
chargemode varchar2(1),
fee_rate number(5,4),
fee number(12,2),
currency  VARCHAR2(3) default 'CNY',
memo varchar2(100),
createTime date,
creatorId varchar2(10),
reviewTime date,
reviewerId varchar2(10),
reviewStatus varchar2(10),
reviewComments varchar2(100),
handleStatus varchar2(10),
handleRemark varchar2(100),
batchId varchar2(32))


create table cgb_batch_query_status(
code varchar2(1),
message varchar2(40)
);

create table cgb_tran_query_status(
code varchar2(1),
message varchar2(40)
);

create table dhb_pay_cut(recordId varchar2(32),
  outId varchar2(32),
  merchId varchar2(10),
  bizType varchar2(1),
  channelId varchar2(1),
  bankcode  varchar2(15),
  bankName  varchar2(50),
  accNo  varchar2(40),
  accName    varchar2(40),
  identityNo varchar2(30),
  currency   varchar2(3),
  money      number(12,2),
  memo       varchar2(50),
  reviewStatus varchar2(10),
  reviewComments varchar2(70),
  batchId varchar2(32),
  createTime       date);
  
  
create table dhb_biz_journal(
id varchar2(32) primary key,
merchId varchar2(20),
bizType varchar2(3),
channelId varchar2(1),
recordId varchar2(32),
fromBankCode varchar2(20),
fromBankName varchar2(50),
fromBankCardNo varchar2(30),
fromUserName varchar2(50),
fromIdentityNo varchar2(30),
toBankCode varchar2(20),
toBankName varchar2(50),
toBankCardNo varchar2(30),
toUserName varchar2(50),
toIdentityNo varchar2(30),
money number(12,2),
chargemode varchar2(1),
fee_rate number(5,4),
fee number(12,2),
currency  VARCHAR2(3) default 'CNY',
memo varchar2(100),
createTime date,
handleStatus varchar2(10),
handleRemark varchar2(100),
batchId varchar2(32));


create table cgb_error_info(
code varchar2(3),
message varchar2(100)
);


create sequence hyt_seq
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;