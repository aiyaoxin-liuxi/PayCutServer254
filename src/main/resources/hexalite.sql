-- Create table
create table HL_LOG
(
  TRANSACTIONID VARCHAR2(36),
  CONTEXT       CLOB,
  METHODTYPE    NUMBER(1),
  INOROUT       NUMBER(1),
  TIMESTAMP     DATE,
  ID            NUMBER(20) not null,
  TASKID        NUMBER(20)
)
tablespace TD_EMAPIII
  pctfree 10
  initrans 1
  maxtrans 255;
-- Add comments to the columns 
comment on column HL_LOG.METHODTYPE
  is '0:ValidatePolicy;1:CreateTicket;2:ReceiveService;3:UpdateService;4:AssignProvider;5:UpdateStatus';
comment on column HL_LOG.INOROUT
  is '0:in;1:out';
  
  
create sequence hl_log_SEQ
minvalue 1
maxvalue 9999999999999999
start with 1
increment by 1
cache 20;


create table HL_RECEIVE_LOG
(
  ID            NUMBER(20) not null,
  ORDERID       NUMBER(20),
  BENEFITID     NUMBER(20) not null,
  TRANSACTIONID VARCHAR2(40),
  CASEID        NUMBER(20) not null,
  ETA           VARCHAR2(10),
  COMMENTS      VARCHAR2(300),
  DISPATCHTYPE  VARCHAR2(10),
  METHODTYPE    NUMBER(1) not null,
  SERVICETYPE   VARCHAR2(10)
)
tablespace TD_EMAPIII
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    next 8
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate indexes 
create index HL_RECEVICE_BENEFIT_INDEX on HL_RECEIVE_LOG (BENEFITID)
  tablespace TD_EMAPIII
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index HL_RECEVICE_CASE_INDEX on HL_RECEIVE_LOG (CASEID)
  tablespace TD_EMAPIII
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create sequence HL_receive_log_SEQ
minvalue 1
maxvalue 9999999999999999
start with 1
increment by 1
cache 20;

create or replace view hl_to_geo_case_view as
select t.fil_id as caseId,
       t.lca_code as serieCode,
       t.lmo_id as modelCode,
       t.lmo_lmk_code as makerCode,
       lm.name as makerName,
       '' as year,
       t.vin,
       gf.a_state,
       gf.a_city as cityAndDistrict,
       gf.bd_geoinfo,
       gf.bd_x_n,
       gf.bd_y_n,
       gf.carcolor as colorName,
       gf.a_city || gf.a_address1 as bdAddress,
       hlrt.h_platformid as platformId,
       aot.ownerq as sourceCode,
       hlrt.h_transactionid as transactionId,
       na.taskid
  from TECNIK t
  left join lmaker lm
    on t.lmo_lmk_code = lm.code
  left join emapiii.geonacsfiles gf
    on t.fil_id = gf.case_id
  left join emapiii.nacsaddition na
    on t.fil_id = na.case_id
  left join emapiii.hl_rescue_ticket hlrt
    on na.taskid = hlrt.taskid
  left join emapiii.agaownertask aot
    on na.taskid = aot.taskid;

    
create or replace view geo_case_benefit as
select f.id as caseId,
       gb.secid as benefitId,
       f.secid as extId,
       gb.bex_lbe_code     as benefitCode,
       bc.ben_name         as benefitName,
       gb.bex_eax_code     as eapCode,
       ec.eap_name         as eapName,
       gb.capability_id    as capId,
       gb.capability_name  capabilityName,
       gf.owner_code as ownerCode,
       f.cox_lco_code as contractCode,
       f.lev_lpe_code as eventType,
       f.lev_code as eventDetail,
       gf.vinno as vin,
       gf.regno as regNo,
       t.lca_code as serieCode,
       t.lmo_id as modelCode,
       t.lmo_lmk_code as makerCode,
       f.caller as customerName,
       f.atel as assistancePhone,
       f.acity,
       f.lst_code_live as liveProvinceCode,
       f.lna_code_work as nationCode,
       f.lst_code_work as workProvinceCode,
       f.lst_code_ass as assProvinceCode,
       to_char(f.crdate, 'yyyy-mm-dd hh24:mi:ss') as accidentDate,
       to_char(f.mddate, 'yyyy-mm-dd hh24:mi:ss') as modifydDate,
       gf.bd_x_n as lontitude,
       gf.bd_y_n as latitude,
       gf.carcolor as color,
       gf.a_city || gf.a_address1 as dbAdress,
       gb.sup_id as supplierId,
       gb.sup_name as supplierName,
       gb.sup_repdealer as dealerNmsId,
       gb.sup_repname as dealerNmsName,
       gb.sup_rep_x_map as dealerLon,
       gb.sup_rep_y_map as dealerlat,
       gb.benefit_comments as benefitComment
       from emapiii.files f,
       emapiii.geonacsfiles gf,
       TECNIK t,
       emapiii.geobenefit gb,
       emapiii.benefitclass bc,
       emapiii.eapclass ec
       where gb.case_id = f.id
       and f.id = gf.case_id
       and gf.case_id = t.fil_id
       and gb.bex_lbe_code = bc.lbe_code
       and gb.bex_eax_code = bc.eax_code
       and bc.eax_code = ec.eap_code;
