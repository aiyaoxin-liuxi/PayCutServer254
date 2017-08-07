-- Create table
drop table DHB_BIZ_JOURNAL;
create table DHB_BIZ_JOURNAL
(
  ID             VARCHAR2(36) not null,
  MERCHID        VARCHAR2(20),
  BIZTYPE        VARCHAR2(3),
  CHANNELID      VARCHAR2(1),
  RECORDID       VARCHAR2(36),
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
  ENDTIME        DATE,
  BIGCHANNELID   VARCHAR2(2)
)
alter table DHB_BIZ_JOURNAL
add primary key (ID);
drop table DHB_PROXY_BATCH_RECORD;
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
drop table DHB_PAY_CUT;
create table DHB_PAY_CUT
(
  RECORDID       VARCHAR2(36),
  OUTID          VARCHAR2(36),
  MERCHID        VARCHAR2(20),
  BIZTYPE        VARCHAR2(1),
  CHANNELID      VARCHAR2(1),
  BANKCODE       VARCHAR2(15),
  BANKNAME       VARCHAR2(50),
  ACCNO          VARCHAR2(40),
  ACCNAME        VARCHAR2(40),
  IDENTITYNO     VARCHAR2(30),
  CURRENCY       VARCHAR2(3),
  MONEY          NUMBER(12,2),
  MEMO           VARCHAR2(50),
  REVIEWSTATUS   VARCHAR2(10),
  REVIEWCOMMENTS VARCHAR2(70),
  BATCHID        VARCHAR2(32),
  CREATETIME     DATE,
  REVIEWTIME     DATE
)
drop table DHB_REALNAME;
create table DHB_REALNAME
(
  CERTNO   VARCHAR2(20),
  ACCNO    VARCHAR2(36),
  USERNAME VARCHAR2(100),
  TEL      VARCHAR2(20)
  createTime date default sysdate
);
insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('340521199111185414', '6222620210006503857', '王先斌', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('340521199111185414', '6236681370001807178', '王先斌', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('32092119920917464X', '6217730500645245', '缪咏雪', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('341281198403050497', '6212260200057627768', '郑和进', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6212264301009152420', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6230580000034982590', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6214830255803271', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '622908406721953712', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('23232419800407361X', '6228481278902517375', '王钊', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210781198803084837', '6217560500017488797', '王鹏', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('152630198502103010', '6217905000004193280', '陈维明', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('211481197706294031', '6217992350003855155', '刘绍仁', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('372826197203201510', '6217856000042304328', '张长朋', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320103198805150010', '6217001370022239446', '郭欣', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('640102198912191529', '6212264301002193298', '黑鸣慧', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('640102198912191529', '6228480392680292712', '黑鸣慧', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('420202196810181229', '6217857600018944616', '刘林青', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370687198607095712', '6228480300629193115', '王宁', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('150104198401203010', '6226985600186800', '邢文广', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('350322196511080025', '6227001935640526819', '傅丽丹', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370623196601070011', '6228480268351505576', '崔玉安', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('37022219510101001X', '6228480248032631678', '周敦礼', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('120105196704073614', '6222601110007864808', '倪志忠', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370681197902154823', '6227002191134677028', '刘少妮', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320404195612181012', '5324268653403015', '李顺兴', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('120222198305284626', '6228480028022354378', '程秀芝', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370632197202132423', '6222081614000346803', '于萍', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320219196902193263', '4563516102007195948', '陈红娟', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('321002196712253010', '4367421331200334992', '陈文军', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('21052119901221003X', '6212263301015335796', '张学鑫', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320105196701081632', '6227001375140048032', '刘长春', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320321198408201658', '6212264301011518816', '唐文涛', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('341227197402101077', '6222801375151090496', '夏广涛', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('341122197912151421', '6222801375151063360', '章世仙', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('152324197902196318', '6210810620002809733', '李坤', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('21050319840210151X', '6217000600000083155', '史井权', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210283198804133114', '6212263301015725186', '孙忠宁', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210225197604180356', '6217000780013494084', '于庆河', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210502198112160611', '6217000600000557414', '董庭刚', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210211198701123157', '6217000780014176425', '王文东', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('422822199409224038', '6217002750005918596', '谭威', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('21011419891220361X', '6236680730000280126', '贝冬生', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('321182198609292237', '6236681300001468796', '王诚', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('220381197910313012', '6013820500987388158', '张建伟', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('239004196502050053', '6228481396268284172', '姜伟全', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('51022719751120050X', '6228480878139850571', '邓兰', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6217993000142942450', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6228480395183821473', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320924199302020863', '6214623621000040403', '孙苏阳', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370226195704296614', '6228480248109024377', '卢升茂', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('120112197909140460', '6228480028474589877', '张学丽', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('132930197501162623', '6217000060003335963', '杨军娜', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370105195408201126', '6217002340017071988', '禹秀华', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('150102198207220623', '6226905601468999', '武晓丽', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370681196809106822', '6228410260403102713', '朱春燕', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('43060219630515504X', '6236682920003319595', '卢垂香', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('440803199001060730', '6228480089226743477', '张振辉', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370623195306024819', '6217002190014516372', '张仁智', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('22020419810624362X', '6222623140001610901', '霍云峰', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('321019197009193927', '6227001331560081110', '蒋宝霞', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320102197907173247', '6222024301017479914', '胡蓉', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320106196912311633', '4367421375760416517', '宗元', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320107196411031314', '6212264301003031075', '张正平', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320107197407143414', '6222024301071788010', '谢海林', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320107198311051894', '6217001370011787595', '陈伟', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('330382198701023138', '6217001370016946733', '陈学斌', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320105196901231017', '6217001370023294838', '史滨', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320103198007240011', '6227001376220071795', '严谭亮', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('32010419820525041X', '6212264301008759910', '孙亚威', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('510108198808273910', '6216613100008145537', '何全勇', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('320113198710122028', '6217856100048896077', '杭贤文', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210782198604060820', '6210810620002812505', '王春颖', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('130826198702175634', '6217850500004918771', '刘鹏飞', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('422802198804122613', '6217002750005482460', '张波', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210204198505102195', '6217000780024208945', '常源华', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210204198712250975', '6217000780001991828', '刘殿龙', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('232102198104221238', '6217001140006573916', '姚龙江', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('232321198706015618', '6217001140020855919', '韩金辉', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('321322198410196808', '4367421331130055923', '马艳红', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('220381199003106035', '6212263301015118200', '李春宇', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('230623198801210232', '6217001020005575304', '卢振兴', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('432922196506160017', '6228270129919748575', '彭可军', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210724197205305415', '6228482208778571072', '徐英杰', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('370302197210061712', '6228480288632451879', '郑毅', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('340603197903150619', '6228483178072604870', '段坤', '');

insert into DHB_REALNAME (CERTNO, ACCNO, USERNAME, TEL)
values ('210113198009023210', '6210810730025961864', '郭君', '');


drop table DHB_BANK_INFO;
create table DHB_BANK_INFO
(
  
  ACCTTYPE  VARCHAR2(1) not null,
  ACCTNO    VARCHAR2(30),
  ACCTNAME  VARCHAR2(80),
  BANKNAME  VARCHAR2(80),
  BANKCODE  VARCHAR2(30),
  CHANNELID VARCHAR2(1)
);
insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('0', '11014740786007', '东汇宝支付有限公司', '平安银行北京东城崇文门支行', '307584007998', '1');

insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('2', '11014720253888', '东汇宝支付有限公司', '平安银行深圳分行新闻路支行', '307584007998', '1');

insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('1', '136001512010007819', '东汇宝支付有限公司', '广发银行南京分行营业部', '306581000003', '1');

insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('0', '11014740786007', '东汇宝支付有限公司', '平安银行北京东城崇文门支行', '307584007998', '2');

insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('2', '11014720253888', '东汇宝支付有限公司', '平安银行深圳分行新闻路支行', '307584007998', '2');

insert into dhb_bank_info (ACCTTYPE, ACCTNO, ACCTNAME, BANKNAME, BANKCODE, CHANNELID)
values ('1', '136001512010007819', '东汇宝支付有限公司', '广发银行南京分行营业部', '306581000003', '2');
drop table CGB_ERROR_INFO;
create table CGB_ERROR_INFO
(
  CODE    VARCHAR2(3),
  MESSAGE VARCHAR2(100)
);
insert into cgb_error_info (CODE, MESSAGE)
values ('R43', '年度合作金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R44', '合作年限必须为8位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('R45', '最近交易日期格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R46', '目前应付帐款金额最大长度16位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R47', '目前应付帐款金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R48', '转让编号必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R49', '回执日期最大长度8位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R50', '回执日期格式不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('R51', '交易日期必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R52', '交易日期格式不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('R53', '开始记录数必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R54', '最大条数必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R55', '凭证号码只允许英文字母和数字以及/-_三个特殊字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('R56', '明细序号排列有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('R57', '明细序号开始值有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('R58', '　输入的托收类型与内管维护的托收类型不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('R59', '　该协议已在内管关闭');

insert into cgb_error_info (CODE, MESSAGE)
values ('R60', '　协议号与账号信息不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('R61', '　  托收类型不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('R62', '　交易金额不允许为0');

insert into cgb_error_info (CODE, MESSAGE)
values ('R63', '　交易金额不能超过9999999999.99元');

insert into cgb_error_info (CODE, MESSAGE)
values ('R64', '　托收类型未启用或已经关闭');

insert into cgb_error_info (CODE, MESSAGE)
values ('R65', '　协议号对应的托收类型未启用或已经关闭');

insert into cgb_error_info (CODE, MESSAGE)
values ('I01', '流水编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I02', '查询类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I03', '协议号（内部）格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I04', '业务类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I05', '通知书类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I06', '输入的状态不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I07', '通知书编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I08', '输入的客户类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I09', '输入的角色类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I10', '日期格式不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('I11', '类型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I12', '输入的对账结果不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I13', '原因不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('I14', '对账编号不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('I16', '发货编号不能为空，');

insert into cgb_error_info (CODE, MESSAGE)
values ('I17', '本次传输笔数不能为空，');

insert into cgb_error_info (CODE, MESSAGE)
values ('I18', '发货批次格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I19', '输入的预警与提示类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I20', '本次传输笔数格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I21', '总笔数格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I22', '押品编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I23', '追加押品数量格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I24', '押品名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I25', '押品一级编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I26', '押品二级编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I27', '规格/型号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I28', '押品单位编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I29', '品牌/厂家/产地格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I30', '货物存放地点格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I31', '押品三级编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I32', '审批状态输入格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I50', '客户类型不能为空，');

insert into cgb_error_info (CODE, MESSAGE)
values ('I51', '查询类型不能为空，且长度不能大于2');

insert into cgb_error_info (CODE, MESSAGE)
values ('I52', '出入库类型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I53', '发货方类型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I54', '成本价格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I55', '货权凭证号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I56', '是否入库数据录入完毕长度不能大于1');

insert into cgb_error_info (CODE, MESSAGE)
values ('I57', '发货日期格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('I58', '发货日期为必填字段');

insert into cgb_error_info (CODE, MESSAGE)
values ('J01', '收款账号不能是信用卡');

insert into cgb_error_info (CODE, MESSAGE)
values ('J02', '非行内代付交易贷方账号不能为信用卡');

insert into cgb_error_info (CODE, MESSAGE)
values ('J03', '行内代付交易贷方账号不能同时含有信用卡与非信用卡账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('J04', '代发工资明细为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('J05', '代发工资交易不能使用归集账号进行对公转信用卡交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('J06', '代发工资交易暂不支持信用卡交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('L01', '出票人名称不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L02', '出票人组织机构代码证格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L03', '出票人开户账户格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L04', '出票人行号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L05', '承兑人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L06', '承兑人账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L07', '承兑人行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L08', '票据金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L09', '收款人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L10', '收款人开户账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L11', '收款人开户行行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L12', '票据类型为空或类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L13', '撤销人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L14', '撤销人组织机构代码证格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L15', '撤销人开户账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L16', '撤销人行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L17', '签收人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L18', '签收人组织机构代码证格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L19', '签收人开户账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L20', '出质人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L21', '出质人组织机构代码格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L22', '出质人账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L23', '出质人开户行行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L24', '质权人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L25', '质权人账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L26', '质权人开户行行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L27', '该票据在电票系统中不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('L28', '该票据未签收');

insert into cgb_error_info (CODE, MESSAGE)
values ('L29', '质权人名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L30', '质权人组织机构代码格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L31', '质权人账号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L32', '质权人开户行行号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L33', '允许背书的输入为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L34', '业务类型不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L35', '签收结果不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L36', '签收人地址为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L37', '拒付理由代码不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L38', '拒付备注为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('L39', '贴现类型不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L40', '贴现利率不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L41', '付息方式不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L42', '实付金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L43', '入账账号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L44', '入账行号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L45', '签收人开户行行号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L46', '清算方式不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L47', '贴出人名称不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L48', '贴出人组织机构代码证号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L49', '贴出人账户不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L50', '贴出人开户行行号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L51', '贴入人名称不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L52', '贴入人账户不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L53', '贴入人开户行行号不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L54', '回购起始日为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L55', '回购到期日为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L56', '回购利率为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L57', '回购实付金额为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L58', '质权人类别不能为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('L59', '出票日或到期日格式不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('L60', '出票日大于到期日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L61', '出票日跟到日期之间间隔超过1年');

insert into cgb_error_info (CODE, MESSAGE)
values ('L62', '票据状态不是"出票已登记"');

insert into cgb_error_info (CODE, MESSAGE)
values ('L63', '票据状态不是"提示承兑已签收"');

insert into cgb_error_info (CODE, MESSAGE)
values ('L64', '发起方组织机构代码证不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('L65', '备注信息长度不大于256');

insert into cgb_error_info (CODE, MESSAGE)
values ('L66', '批次号长度不大于10');

insert into cgb_error_info (CODE, MESSAGE)
values ('L67', '信用等级长度不大于3');

insert into cgb_error_info (CODE, MESSAGE)
values ('L68', '评级机构长度不大于60');

insert into cgb_error_info (CODE, MESSAGE)
values ('L69', '出票登记备注长度不大于256');

insert into cgb_error_info (CODE, MESSAGE)
values ('L70', '合同编号长度不大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('L71', '发票号码长度不大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('L72', '协议号长度不大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('L73', '出票日跟到期日是同一天');

insert into cgb_error_info (CODE, MESSAGE)
values ('L74', '回购式时不得转让标记不得填"EM01"');

insert into cgb_error_info (CODE, MESSAGE)
values ('L75', '回购起始日大于回购到期日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L76', '票据出票日大于回购式起始日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L77', '回购起始日大于票据到期日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L78', '回购到期日小于票据出票日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L79', '回购到期日大于票据到期日');

insert into cgb_error_info (CODE, MESSAGE)
values ('L80', '逾期说明长度不能大于256');

insert into cgb_error_info (CODE, MESSAGE)
values ('N01', '协议类型必填，且只能为05,06');

insert into cgb_error_info (CODE, MESSAGE)
values ('N02', '查询类型必填，且只能为01,03');

insert into cgb_error_info (CODE, MESSAGE)
values ('N03', '客户类型必填，且只能为04');

insert into cgb_error_info (CODE, MESSAGE)
values ('N04', '对账结果必填，且只能为01,02');

insert into cgb_error_info (CODE, MESSAGE)
values ('N05', '对账确认人必填，且长度不能大于20位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N06', '购销合同号必填，且长度不能大于10位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N07', '买方名称必填，且长度不能大于80位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N08', '合同签订日为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N09', '合同到期日为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N10', '合同金额为空或者格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N11', '货物名称格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N12', '押品数量格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N13', '出厂价格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N14', '押品总价格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N15', '借款人长度不能大于80位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N16', '供应链产品种类ID必填，切只能为3001,3002');

insert into cgb_error_info (CODE, MESSAGE)
values ('N17', '支付货款总额为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N18', '已签发提货总额/发货总额为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N19', '厂商收到款项总额为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N20', '提货总额/未发货总额为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N21', '货物验收总额为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N22', '对账类型必填，且只能为01,02');

insert into cgb_error_info (CODE, MESSAGE)
values ('N23', '对账不平原因(对账结果为对账不符时为必输)长度不能超过500');

insert into cgb_error_info (CODE, MESSAGE)
values ('N24', '对账不符内容(对账结果为对账不符时为必输)长度不能超过500');

insert into cgb_error_info (CODE, MESSAGE)
values ('N25', '评估价格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N26', '核定价格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N27', '客户名称必填，且长度不能大于80位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N28', '所在省长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N29', '所在市长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('N30', '合作时间不能为空，且长度不能大于3位的整数');

insert into cgb_error_info (CODE, MESSAGE)
values ('N31', '上一自然年交易额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N32', '上一次货款支付日期格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N33', '上一次货款交易金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('N34', '客户类型只能为04');

insert into cgb_error_info (CODE, MESSAGE)
values ('N35', '出入库类型取值只能为01的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('P01', '借款人名称必填，且长度不能大于80位');

insert into cgb_error_info (CODE, MESSAGE)
values ('P02', '购销合同（内部ID）不能为空，且长度不能大于20位');

insert into cgb_error_info (CODE, MESSAGE)
values ('P03', '被推荐的客户名称长度不能大于80位');

insert into cgb_error_info (CODE, MESSAGE)
values ('P04', '押品备注长度不能大于200位');

insert into cgb_error_info (CODE, MESSAGE)
values ('P05', '开始日期、结束日期跨度超过一个月');

insert into cgb_error_info (CODE, MESSAGE)
values ('P06', '不支持查询6个月外的数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('P07', '结束日期必须小于当前日期，且大于等于开始日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('P08', '结束日期+结束时间要大于等于起始日期+开始时间');

insert into cgb_error_info (CODE, MESSAGE)
values ('P09', '查询账单明细0045异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('P10', '商户号不能为空，且长度不能大于30位字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('P11', 'POS终端号长度不能大于8位字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('P12', '商户收款账号长度不能大于32位字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('P13', '业务类型必须在40,43,44,46,47,49,EE中');

insert into cgb_error_info (CODE, MESSAGE)
values ('P14', '开始、结束时间必须是范围在00-23之间的2位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('P15', '每页笔数不能为空，且长度必须是最长为3位的大于0小于等于1000的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('P16', '商户号或商户收款账号非法');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z01', '主账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z02', '流水号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z03', '有效期格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z04', '虚拟子账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z05', '车型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z06', '合格证编号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z07', '发动机号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z08', '车辆识别号VIN（车架号）格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z09', '发证日期/报关日期不能为空,发证日期/报关日期格式为yyyy-MM-dd');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z10', '出库动态输入格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z11', '车型不能为空，且长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z12', '合格证编号不能为空，且长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z13', '发动机号不能为空，且长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z14', '车辆识别号VIN（车架号）不能为空，且长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z15', '业务类型不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z16', '　交易失败，资金联动下拨失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z17', '归集账号余额不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z18', '　不支持向NRA或OSA账号进行转账，交易失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z19', '　接口异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z20', '　文件名称必填，且长度不能大于20位，只允许英文字母和数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z21', '　文件类型必填，且只能为00,01');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z22', '　文件尚未生成');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z23', '　文件已删除');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z24', '　未签约对账功能');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z25', '文件名称不正确　');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z26', '重传文件已发起，请稍等15分钟后系统自动上送文件');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z27', '　确认标志取值只能为0、1,或不填');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z28', '　客户类型只能输入01或04');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z29', '协议号输入长度不能超过20　');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z30', '车型长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z31', '合格证编号长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z32', '发动机号长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z33', '车辆识别号VIN（车架号）长度不能大于100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z34', '发证日期/报关日期格式为yyyy-MM-dd');

insert into cgb_error_info (CODE, MESSAGE)
values ('Z35', '客户类型不能为空，且取值只能为04,01的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('101', '企业代码错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('102', '没有开通银企直联或代理行支付服务');

insert into cgb_error_info (CODE, MESSAGE)
values ('103', '企业网银帐号不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('104', '操作员错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('105', '操作员类型错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('106', '操作员密码错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('107', '操作员类型错误，不能转帐');

insert into cgb_error_info (CODE, MESSAGE)
values ('108', '操作员没有此帐号帐号权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('109', '转帐类型错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('198', '主机返回空数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('199', '网银其它错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('200', '交易成功');

insert into cgb_error_info (CODE, MESSAGE)
values ('201', '无此帐号或存折');

insert into cgb_error_info (CODE, MESSAGE)
values ('202', '户况不正常或已结清');

insert into cgb_error_info (CODE, MESSAGE)
values ('203', '该帐户有事故或质权设定，不得存入/支取/结清');

insert into cgb_error_info (CODE, MESSAGE)
values ('204', '帐户密码错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('205', '交易金额应大于零');

insert into cgb_error_info (CODE, MESSAGE)
values ('206', '原交易已充正');

insert into cgb_error_info (CODE, MESSAGE)
values ('207', '无此交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('208', '外汇会计只做本所转帐');

insert into cgb_error_info (CODE, MESSAGE)
values ('209', '帐号正在使用');

insert into cgb_error_info (CODE, MESSAGE)
values ('210', '金额被冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('211', '有效金额不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('212', '此帐号不是密码提款，不能转出');

insert into cgb_error_info (CODE, MESSAGE)
values ('213', '转入转出标志不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('214', '转入转出帐号的业务品种不能互转');

insert into cgb_error_info (CODE, MESSAGE)
values ('215', '转出帐号的币别与转入帐号的币别不相同');

insert into cgb_error_info (CODE, MESSAGE)
values ('216', '非实名制帐户，不得存入');

insert into cgb_error_info (CODE, MESSAGE)
values ('217', '信用卡转帐币别定义不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('218', '重复的流水号');

insert into cgb_error_info (CODE, MESSAGE)
values ('219', '外汇会计，附加档中找不到此帐号');

insert into cgb_error_info (CODE, MESSAGE)
values ('220', '外汇会计，余额档中无此帐号');

insert into cgb_error_info (CODE, MESSAGE)
values ('221', '无此客户资料');

insert into cgb_error_info (CODE, MESSAGE)
values ('222', '转帐不成功');

insert into cgb_error_info (CODE, MESSAGE)
values ('223', '此交易不成功，无需冲数');

insert into cgb_error_info (CODE, MESSAGE)
values ('224', '无此报文类型');

insert into cgb_error_info (CODE, MESSAGE)
values ('225', '不是一本通存折');

insert into cgb_error_info (CODE, MESSAGE)
values ('226', '无转出行所');

insert into cgb_error_info (CODE, MESSAGE)
values ('227', '无转入行所');

insert into cgb_error_info (CODE, MESSAGE)
values ('228', '该笔定期不得进行通存通兑');

insert into cgb_error_info (CODE, MESSAGE)
values ('229', '帐号与存折号不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('230', '该笔定期的资料不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('231', '处理码与转帐帐号不对应');

insert into cgb_error_info (CODE, MESSAGE)
values ('232', '一本通不接受此业务品种');

insert into cgb_error_info (CODE, MESSAGE)
values ('233', '禁制户或死亡户不得做此交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('234', '此业务品种不得约定存期');

insert into cgb_error_info (CODE, MESSAGE)
values ('235', '此业务品种、币别下无此存期档次');

insert into cgb_error_info (CODE, MESSAGE)
values ('236', '该存期档次尚未开放');

insert into cgb_error_info (CODE, MESSAGE)
values ('237', '开户金额低于最低开户金额');

insert into cgb_error_info (CODE, MESSAGE)
values ('238', '取不到该笔定期的利率');

insert into cgb_error_info (CODE, MESSAGE)
values ('239', '此交易记录所对应的存折已不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('240', '此笔定期无历史资料，不得做冲数');

insert into cgb_error_info (CODE, MESSAGE)
values ('241', '存折号码为零');

insert into cgb_error_info (CODE, MESSAGE)
values ('242', '无符合条件的交易记录');

insert into cgb_error_info (CODE, MESSAGE)
values ('243', '此交易不允许冲数');

insert into cgb_error_info (CODE, MESSAGE)
values ('244', '查询流水不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('245', '此业务尚未开放');

insert into cgb_error_info (CODE, MESSAGE)
values ('246', '对公交易，不可为非营业日');

insert into cgb_error_info (CODE, MESSAGE)
values ('247', '网上缴费只可作人民币交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('248', '数据错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('249', '身份证与账号不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('250', '此客户下无账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('251', '此段时间内无交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('252', '起息日不得小于开户日');

insert into cgb_error_info (CODE, MESSAGE)
values ('253', '存折、存单号码错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('254', '金融卡不可用');

insert into cgb_error_info (CODE, MESSAGE)
values ('255', '已经存在事故');

insert into cgb_error_info (CODE, MESSAGE)
values ('256', '不存在此事故');

insert into cgb_error_info (CODE, MESSAGE)
values ('257', '此账号不能做本交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('258', '已到第一条纪录，不能向上翻页');

insert into cgb_error_info (CODE, MESSAGE)
values ('259', '第一次查询不能向上翻页');

insert into cgb_error_info (CODE, MESSAGE)
values ('260', '金融卡有其他事故');

insert into cgb_error_info (CODE, MESSAGE)
values ('261', '主机系统忙，请稍候再试');

insert into cgb_error_info (CODE, MESSAGE)
values ('262', '储蓄账号不能做转账交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('294', '银行系统正在升级，请稍后再试');

insert into cgb_error_info (CODE, MESSAGE)
values ('295', '银行系统升级，请稍后再试');

insert into cgb_error_info (CODE, MESSAGE)
values ('299', '主机返回其他错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('400', '系统故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('401', '系统配置错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('402', '前置机IP地址错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('403', '前置机通讯配置错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('404', '通讯故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('405', '通讯故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('406', '系统故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('407', '取证书故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('408', 'IP地址不符（后台）');

insert into cgb_error_info (CODE, MESSAGE)
values ('409', '客户端读错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('410', '证书验证错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('411', '超过最多发包数');

insert into cgb_error_info (CODE, MESSAGE)
values ('412', '接收超时');

insert into cgb_error_info (CODE, MESSAGE)
values ('416', 'IC卡设备出错');

insert into cgb_error_info (CODE, MESSAGE)
values ('419', '签名数据出错');

insert into cgb_error_info (CODE, MESSAGE)
values ('428', '发送报文有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('429', '企业财务系统IP地址错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('430', '预收日期不单独存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('485', '银行卡在申请中');

insert into cgb_error_info (CODE, MESSAGE)
values ('486', '银行卡正在制卡中');

insert into cgb_error_info (CODE, MESSAGE)
values ('487', '银行卡正暂时挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('488', '银行卡已挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('489', '单折未登折次数超限额，请先补登');

insert into cgb_error_info (CODE, MESSAGE)
values ('501', '只能在柜台结清');

insert into cgb_error_info (CODE, MESSAGE)
values ('502', '附属卡不能做本交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('503', 'SASB存单帐务行与新理财通卡号帐务行不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('504', '身份证号码与卡号有效证件号不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('505', '通讯失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('506', '数据库无对应记录');

insert into cgb_error_info (CODE, MESSAGE)
values ('507', '数据操作异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('508', '外币大额、通知存款不支持通存通兑交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('509', '本行或对方行所不允许做此币种业务');

insert into cgb_error_info (CODE, MESSAGE)
values ('510', '本行所不允许做此业务品种');

insert into cgb_error_info (CODE, MESSAGE)
values ('511', '本行所对此交易有限制，请确认');

insert into cgb_error_info (CODE, MESSAGE)
values ('512', '联名卡只允许在柜台做取款和转帐交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('513', '查找上级行失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('514', '未结清的取款金额必须是100的倍数');

insert into cgb_error_info (CODE, MESSAGE)
values ('515', '金额小于零');

insert into cgb_error_info (CODE, MESSAGE)
values ('516', '本交易必须在开户行进行');

insert into cgb_error_info (CODE, MESSAGE)
values ('517', '数据库操作出错');

insert into cgb_error_info (CODE, MESSAGE)
values ('518', '客户资料不存在！');

insert into cgb_error_info (CODE, MESSAGE)
values ('519', '客户属性不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('520', '更新数据失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('521', '该卡无对应帐务行所号');

insert into cgb_error_info (CODE, MESSAGE)
values ('522', '密码错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('523', '卡正暂时挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('524', '此新理财通卡帐户号不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('525', '此新理财通卡不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('526', '此活期帐户不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('527', '此定期帐户不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('528', '此贷款帐户不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('529', '此卡已冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('530', '此卡已挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('531', '此帐户已冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('532', '此定期帐户已冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('533', '输入卡号中的行号非法');

insert into cgb_error_info (CODE, MESSAGE)
values ('534', '输入的交易分类数据错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('535', '已暂禁');

insert into cgb_error_info (CODE, MESSAGE)
values ('536', '已冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('537', '已销户');

insert into cgb_error_info (CODE, MESSAGE)
values ('538', '无此卡号');

insert into cgb_error_info (CODE, MESSAGE)
values ('539', '找不到帐户序号');

insert into cgb_error_info (CODE, MESSAGE)
values ('540', '卡号与证件号不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('541', '输入证件类型有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('542', '输入证件号码有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('543', '已销卡');

insert into cgb_error_info (CODE, MESSAGE)
values ('544', 'ATRC4P检查失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('545', '不能输入本币代码');

insert into cgb_error_info (CODE, MESSAGE)
values ('546', '可以输入的科目代码：999,998,101');

insert into cgb_error_info (CODE, MESSAGE)
values ('547', '此贷币号不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('548', '此业务品种码不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('549', '此存期码不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('550', '此操作码不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('551', '可用额度不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('552', '可用余额不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('553', '输入的日期不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('554', '行号检查出错');

insert into cgb_error_info (CODE, MESSAGE)
values ('555', '输入的身份证件号不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('556', '输入的身份证件类别不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('557', '输入的请求系统代号无效');

insert into cgb_error_info (CODE, MESSAGE)
values ('558', '输入的交易渠道代号无效');

insert into cgb_error_info (CODE, MESSAGE)
values ('559', '此货币号和业务品种码都不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('560', '冲帐利息积数不够');

insert into cgb_error_info (CODE, MESSAGE)
values ('561', '隔年不能做冲帐！');

insert into cgb_error_info (CODE, MESSAGE)
values ('562', '更新额度文件失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('563', '无相应余额控制记录');

insert into cgb_error_info (CODE, MESSAGE)
values ('564', '余额不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('565', '更新余额表失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('566', '找不到系统参数值');

insert into cgb_error_info (CODE, MESSAGE)
values ('567', '获取主机序列号失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('568', '刷新额度失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('569', '输入不合该域要求');

insert into cgb_error_info (CODE, MESSAGE)
values ('570', '银行卡正暂时挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('571', '银行卡未申请');

insert into cgb_error_info (CODE, MESSAGE)
values ('572', '银行卡正在制卡中');

insert into cgb_error_info (CODE, MESSAGE)
values ('573', '银行卡在申请中');

insert into cgb_error_info (CODE, MESSAGE)
values ('574', '银行卡未启用');

insert into cgb_error_info (CODE, MESSAGE)
values ('575', '银行卡已挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('576', '银行卡状态不正常，暂时不可用');

insert into cgb_error_info (CODE, MESSAGE)
values ('577', '其他错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('578', '卡片已损毁');

insert into cgb_error_info (CODE, MESSAGE)
values ('579', '卡片已密码挂失');

insert into cgb_error_info (CODE, MESSAGE)
values ('580', '卡片已注销');

insert into cgb_error_info (CODE, MESSAGE)
values ('581', '卡片已停止使用');

insert into cgb_error_info (CODE, MESSAGE)
values ('582', '卡片正在补发申请、未重新启用');

insert into cgb_error_info (CODE, MESSAGE)
values ('583', '卡片已事故登录');

insert into cgb_error_info (CODE, MESSAGE)
values ('584', '银行卡未申请');

insert into cgb_error_info (CODE, MESSAGE)
values ('601', '企业网银未开通代发工资业务');

insert into cgb_error_info (CODE, MESSAGE)
values ('602', '操作员没有代发工资操作权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('603', '企业在网银的手续费设置参数不全');

insert into cgb_error_info (CODE, MESSAGE)
values ('605', '总金额与实际金额不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('606', '该批次已经提交，不能重复提交！');

insert into cgb_error_info (CODE, MESSAGE)
values ('607', '交易总笔数不能大于2000笔！');

insert into cgb_error_info (CODE, MESSAGE)
values ('608', '交易总笔数不能大于所配置的最大笔数');

insert into cgb_error_info (CODE, MESSAGE)
values ('609', '工资明细信息有错');

insert into cgb_error_info (CODE, MESSAGE)
values ('610', '行内月交易次数已达到当月交易次数限制，不能再交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('611', '行外月交易次数已达到当月交易次数限制，不能再交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('612', '行内交易金额大于行内每次交易总额上限');

insert into cgb_error_info (CODE, MESSAGE)
values ('613', '行外交易金额大于行外每次交易总额上限');

insert into cgb_error_info (CODE, MESSAGE)
values ('614', '查询账号余额出错');

insert into cgb_error_info (CODE, MESSAGE)
values ('615', '客户批次号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('616', '存在重复的客户批次子流水号');

insert into cgb_error_info (CODE, MESSAGE)
values ('617', '总金额有误，必须大于0的数字，必须是两位小数，长度不能大于16');

insert into cgb_error_info (CODE, MESSAGE)
values ('618', '转出账号必须是不带符号数字，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('619', '没有正确交易类型0/1的数据，至少需要有一条交易类型是正确的数据，否                 则返回619');

insert into cgb_error_info (CODE, MESSAGE)
values ('620', '客户批次号和操作员不匹配或者客户批次号不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('621', '客户批次子流水号和客户批次号不匹配或者客户批次子流水号不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('622', '操作员不可用');

insert into cgb_error_info (CODE, MESSAGE)
values ('623', '账号名称长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('624', '存在重复的员工编号');

insert into cgb_error_info (CODE, MESSAGE)
values ('625', '存在重复的员工账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('626', '无联行号信息');

insert into cgb_error_info (CODE, MESSAGE)
values ('627', '重发交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('628', '账号类型和批次类型不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('700', '解析客户上送的XML报文出错(JDOMException)');

insert into cgb_error_info (CODE, MESSAGE)
values ('701', '解析客户上送的XML报文出错(IOException)');

insert into cgb_error_info (CODE, MESSAGE)
values ('702', '加载系统配置文件');

insert into cgb_error_info (CODE, MESSAGE)
values ('703', '未成功加载系统配置文件');

insert into cgb_error_info (CODE, MESSAGE)
values ('704', '未找到任何IO表配置文件');

insert into cgb_error_info (CODE, MESSAGE)
values ('705', 'IO表配置文件非法');

insert into cgb_error_info (CODE, MESSAGE)
values ('706', '读取文件发生异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('707', '上送XML报文非法');

insert into cgb_error_info (CODE, MESSAGE)
values ('708', '加密失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('709', '获取配置文件数据失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('710', '读取客户上送的报文发生异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('711', '上送报文格式非法、读取报文发生异常，请确认上送报文格式的正确性，并检验报文是否包含特殊字符%和&符号');

insert into cgb_error_info (CODE, MESSAGE)
values ('712', '在GDBSocketHandler/GDBLocalSocketHandler发生未捕获的异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('751', '仅支持查询最大笔数为150');

insert into cgb_error_info (CODE, MESSAGE)
values ('752', '未找到该账号的相应记录');

insert into cgb_error_info (CODE, MESSAGE)
values ('753', '查询方向的值仅为0或1');

insert into cgb_error_info (CODE, MESSAGE)
values ('754', '笔数只能为0表示全部下载');

insert into cgb_error_info (CODE, MESSAGE)
values ('755', '开始与结束日期跨度不能超过90天');

insert into cgb_error_info (CODE, MESSAGE)
values ('756', '开始日期不能大于结束日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('757', '交易受限，交易控制已关闭');

insert into cgb_error_info (CODE, MESSAGE)
values ('758', '结束日期不能大于等于当前日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('887', '没有收到交易请求');

insert into cgb_error_info (CODE, MESSAGE)
values ('888', '没有收到主机返回数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('889', '交易失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('901', '下载IO表交易码为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('902', '下载IO表交易码格式错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('903', '网银服务器不存在此交易码的IO表');

insert into cgb_error_info (CODE, MESSAGE)
values ('904', '交易码对应的IO表内容为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('905', 'IO表读写异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('906', '通讯前置生成IO表路径错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('907', '通讯前置未找到IO表');

insert into cgb_error_info (CODE, MESSAGE)
values ('908', '通讯前置生成IO表失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('909', 'cgb_data参数获取失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('910', '不支持GET方式访问');

insert into cgb_error_info (CODE, MESSAGE)
values ('911', '下载IO表通讯故障');

insert into cgb_error_info (CODE, MESSAGE)
values ('912', '下载IO表返回报文不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('913', '下载IO表返回码格式错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('914', '下载IO表网银系统其它错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('915', '连接服务器超时');

insert into cgb_error_info (CODE, MESSAGE)
values ('920', '托收类型不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('921', '未开通代收业务');

insert into cgb_error_info (CODE, MESSAGE)
values ('922', '入账账号不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('923', '不支持信用卡付款');

insert into cgb_error_info (CODE, MESSAGE)
values ('924', '不支持此账号作为付款账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('925', '付款账号不符合');

insert into cgb_error_info (CODE, MESSAGE)
values ('926', '交易时间逾期');

insert into cgb_error_info (CODE, MESSAGE)
values ('950', '总笔数至少1笔，最多200笔');

insert into cgb_error_info (CODE, MESSAGE)
values ('951', '上主机取付款账号账户名称,返回空数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('952', '批次明细有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('953', '必输项不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('998', '(ibs805)系统繁忙，请稍等两分钟后再试');

insert into cgb_error_info (CODE, MESSAGE)
values ('999', '发生未捕获的异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('A01', '企业客户号不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('A02', '企业财务系统流水号不能为空,且长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('A03', '上送账号格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('A04', '被查询或转出账号未加挂银企直联');

insert into cgb_error_info (CODE, MESSAGE)
values ('A05', '操作员不能为空，且长度不能大于大于12');

insert into cgb_error_info (CODE, MESSAGE)
values ('A06', '操作员密码不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('A07', '交易总笔数不能小于1笔');

insert into cgb_error_info (CODE, MESSAGE)
values ('A08', '交易总笔数不能为空，长度不能大于5，且必须为数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('A09', '交易总笔数与明细不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('A10', '数据编码错误，报文编码格式应为GBK编码');

insert into cgb_error_info (CODE, MESSAGE)
values ('A12', '账户无转账权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('A16', '计算手续费异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('A21', '账户无查询权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('A22', '账户无转账权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('A23', '服务器收到密码格式不正确，转加密后密码必须为256位或512位');

insert into cgb_error_info (CODE, MESSAGE)
values ('A24', '结束日期不能大于等于营业日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('A25', '交易总笔数不能为空，长度不能大于4，且必须为数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('A26', '交易总金额不能为空，长度不能大于16，必须为数字，且不能超过两位小数');

insert into cgb_error_info (CODE, MESSAGE)
values ('A27', '入账方式不合规则');

insert into cgb_error_info (CODE, MESSAGE)
values ('A28', '子交易编号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('A99', '主机处理业务异常，具体原因请查看返回报文的错误码跟错误信息');

insert into cgb_error_info (CODE, MESSAGE)
values ('B02', '开始日期或结束日期格式不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('B03', '开始日期与结束日期跨度不能超过30天');

insert into cgb_error_info (CODE, MESSAGE)
values ('B04', '开始笔数大于0，开始笔数不能大于结束笔数,(结束笔数-开始笔数)小于等于49');

insert into cgb_error_info (CODE, MESSAGE)
values ('B05', '调用TBB013接口查询对公定期存单交易失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('B06', '实际查询交易明细数量超过最大返回数量4999');

insert into cgb_error_info (CODE, MESSAGE)
values ('B07', '开始时间超过4个月');

insert into cgb_error_info (CODE, MESSAGE)
values ('B08', '下载结果文件失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('B10', '开始笔数必须为1-4位数字,且开始笔数大于0');

insert into cgb_error_info (CODE, MESSAGE)
values ('B12', '结束笔数必须为1-4位数字,且结束笔数大于0');

insert into cgb_error_info (CODE, MESSAGE)
values ('B11', '输入数据校验配置文件异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('B15', '开始笔数不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B16', '开始笔数长度超出最大长度4');

insert into cgb_error_info (CODE, MESSAGE)
values ('B17', '结束笔数不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B18', '结束笔数长度超出最大长度4');

insert into cgb_error_info (CODE, MESSAGE)
values ('B19', '账号数量不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B20', '账号数量必须为2位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B21', '开始日期不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B22', '开始日期长度必须为8位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B23', '结束日期不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B24', '结束日期长度必须为8位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B25', '查询类型不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B26', '查询类型取值只能为0、1、2');

insert into cgb_error_info (CODE, MESSAGE)
values ('B27', '开始笔数必须为1~4位的大于0数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B28', '结束笔数必须为1~4位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B29', '开始笔数不能大于结束笔数');

insert into cgb_error_info (CODE, MESSAGE)
values ('B30', '账号必须是最长为20位的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B31', '帐号数量必须在[1,50]之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('B32', '给定账号数量与实际账号不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('B33', '开始日期不能早于当前日期1年');

insert into cgb_error_info (CODE, MESSAGE)
values ('B34', '开始日期与结束日期跨度不能超过90天');

insert into cgb_error_info (CODE, MESSAGE)
values ('B35', '币别长度最大为2位的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B36', '明细列表托收类型不一致，或签约协议号与托收类型同时存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('B37', '签约协议号不存在或托收类型不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('B39', '查询笔数不能为空，长度不大于6，且为数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B40', '产品代码不能为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B57', '查询起始位置不能为空，长度不大于10，且为数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B58', '票据号码格式不能为空或格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('B59', '保证类型不能为空，取值只能为01,02,03');

insert into cgb_error_info (CODE, MESSAGE)
values ('B60', '名称不能为空，长度不大于60');

insert into cgb_error_info (CODE, MESSAGE)
values ('B61', '组织机构代码证不能为空，且为不大于20位的数字或-组合');

insert into cgb_error_info (CODE, MESSAGE)
values ('B62', '账号不能为空，且为不大于30位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B63', '提示付款类型不能为空，取值只能为01,02');

insert into cgb_error_info (CODE, MESSAGE)
values ('B64', '线上清算标志不能为空，取值只能为SM00,');

insert into cgb_error_info (CODE, MESSAGE)
values ('B65', '追索类型不能为空，取值只能为RT00,RT01');

insert into cgb_error_info (CODE, MESSAGE)
values ('B66', '提示付款类型为02逾期时不得为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('B67', '金额不能为空，且为长度不大于18带2位小数的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('B68', '查询内容或者撤销类型不能为空，取值只能为[01-21]之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('B69', '交易状态不能为空，取值只能为0,1');

insert into cgb_error_info (CODE, MESSAGE)
values ('B70', '发送方标志不能为空，取值只能为01,02，默认为01');

insert into cgb_error_info (CODE, MESSAGE)
values ('B71', '起始记录数不能为空，取值在[1,999]之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('B72', '线上清算标志不符，我行为承兑人的只能选择线下清算');

insert into cgb_error_info (CODE, MESSAGE)
values ('B73', '非拒付追索时,追索理由代码不得为空,只能为RC00、RC01');

insert into cgb_error_info (CODE, MESSAGE)
values ('B74', '不得转让标记不得为空，只能为EM00,EM01');

insert into cgb_error_info (CODE, MESSAGE)
values ('B75', '查询记录数不能为空，取值在[2,20]之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('B76', '供应商组织机构代码证长度不大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('B77', '供应商审核状态长度不大于1');

insert into cgb_error_info (CODE, MESSAGE)
values ('B78', '开始记录数取值必须大于等于1,且8位长度之内');

insert into cgb_error_info (CODE, MESSAGE)
values ('B79', '最大条数取值必须大于等于1,且8位长度之内');

insert into cgb_error_info (CODE, MESSAGE)
values ('C01', '该账号没有加挂到对应的客户号下面');

insert into cgb_error_info (CODE, MESSAGE)
values ('C02', '该账户是非对公账户');

insert into cgb_error_info (CODE, MESSAGE)
values ('C03', '该账号没有开通付款权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('C04', '转出转入账号不能为同一账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('C05', '企业未指定收费账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('C98', '账号与币种不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('C99', '主机业务处理异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('C11', '被查询账号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D09', '交易时间格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D10', '交易代码格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D11', '付款账号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D12', '收款账号不能为空，长度不能大于32');

insert into cgb_error_info (CODE, MESSAGE)
values ('D13', '收款银行不能为空，长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D14', '金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D15', '摘要长度不能大于32');

insert into cgb_error_info (CODE, MESSAGE)
values ('D16', '附言长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D17', '付款人不能为空，长度不能大于50');

insert into cgb_error_info (CODE, MESSAGE)
values ('D18', '收款人不能为空，长度不能大于50');

insert into cgb_error_info (CODE, MESSAGE)
values ('D19', '收款银行地址不能为空，长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D20', '联行号不能为空，长度不能大于12');

insert into cgb_error_info (CODE, MESSAGE)
values ('D21', '网银流水号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D22', '查询流水号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D23', '日期不能为空，且为数字，长度为8');

insert into cgb_error_info (CODE, MESSAGE)
values ('D24', '转出账号不是对公账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('D25', '付款账号收款账号不能相同');

insert into cgb_error_info (CODE, MESSAGE)
values ('D26', '日期或时间格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D27', '大小额网关异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('D28', '账号无转账权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('D29', '账号无公转私权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('D30', '客户流水号长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D31', '交易成功，但生成电子回单失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('D32', '付款账号不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('D33', '收款人填写错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('D34', '收款银行地址错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('D35', '付款账号不存在或没有加挂网银');

insert into cgb_error_info (CODE, MESSAGE)
values ('D36', '收款人与收款账号不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('D37', '转账金额大于单笔限额');

insert into cgb_error_info (CODE, MESSAGE)
values ('D38', '转账金额大于日累计限额');

insert into cgb_error_info (CODE, MESSAGE)
values ('D39', '账户没有进行限额设置');

insert into cgb_error_info (CODE, MESSAGE)
values ('D40', '收款银行地址长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D41', '付款人填写错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('D42', '交易账号必须为人民币活期账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('D43', '银行处理中');

insert into cgb_error_info (CODE, MESSAGE)
values ('D44', '手续费扣除失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('D45', '交易成功，但余额查询异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('D46', '没有使用该接口的权限');

insert into cgb_error_info (CODE, MESSAGE)
values ('D47', '没有进行收费项目设置');

insert into cgb_error_info (CODE, MESSAGE)
values ('D48', '手续费计算失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('D49', '附言不能为空，长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D50', '名义付款人不能为空，长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D51', '名义付款账号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D52', '名义付款人不能为空，长度不能大于60');

insert into cgb_error_info (CODE, MESSAGE)
values ('D53', '名义付款账号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('D54', '附言不能含特殊字符“;”');

insert into cgb_error_info (CODE, MESSAGE)
values ('D55', '名义付款人不能含特殊字符“;”');

insert into cgb_error_info (CODE, MESSAGE)
values ('D56', '名义付款人不能与实际付款人相同');

insert into cgb_error_info (CODE, MESSAGE)
values ('D57', '请检查转入转出账号是否正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D58', '名义付款账号不能含特殊字符“;”');

insert into cgb_error_info (CODE, MESSAGE)
values ('D59', '摘要长度不能大于22');

insert into cgb_error_info (CODE, MESSAGE)
values ('D60', '银企直联功能已关闭');

insert into cgb_error_info (CODE, MESSAGE)
values ('D61', '交易类型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D62', '收款银行长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('D63', '联行号长度不能大于12');

insert into cgb_error_info (CODE, MESSAGE)
values ('D64', '该账号没有加挂到对应的企业网银客户号下面');

insert into cgb_error_info (CODE, MESSAGE)
values ('D65', '付款人与付款账号不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('D66', '包年包月手续费未扣收');

insert into cgb_error_info (CODE, MESSAGE)
values ('D67', '是否网银授权格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D68', '账户未做身份核实，账户交易受限');

insert into cgb_error_info (CODE, MESSAGE)
values ('D69', '证书DN格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D70', '查询类型格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D71', '日期格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('D72', '查询笔数格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('E01', '证书有效期已过');

insert into cgb_error_info (CODE, MESSAGE)
values ('E02', '该用户没有绑定相关证书');

insert into cgb_error_info (CODE, MESSAGE)
values ('E03', '验签失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('E04', '操作员状态非正常');

insert into cgb_error_info (CODE, MESSAGE)
values ('F01', '查询账号数量大于10');

insert into cgb_error_info (CODE, MESSAGE)
values ('F02', '账号数量与账号列表内数量不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('F03', '该账号非归集账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('F04', '非母子关系');

insert into cgb_error_info (CODE, MESSAGE)
values ('F05', '子公司未授权');

insert into cgb_error_info (CODE, MESSAGE)
values ('F06', '查询类型只能为0、1');

insert into cgb_error_info (CODE, MESSAGE)
values ('F07', '查询行所号失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('F08', '结束日期大于当前日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('F09', '归集账号不能发起信用卡交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('F10', '开始日期须大于当前日期前推4个月日期');

insert into cgb_error_info (CODE, MESSAGE)
values ('F11', '信用卡名长度超过30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F12', '收款账号与收款户名不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('F13', '有效余额不足');

insert into cgb_error_info (CODE, MESSAGE)
values ('F14', '无此卡号或存折');

insert into cgb_error_info (CODE, MESSAGE)
values ('F15', '交易金额为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F16', '摘要长度不能大于22');

insert into cgb_error_info (CODE, MESSAGE)
values ('F17', '非基本户');

insert into cgb_error_info (CODE, MESSAGE)
values ('F18', '操作员非银企用户');

insert into cgb_error_info (CODE, MESSAGE)
values ('F19', '收款账户不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('F20', '客户名称为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F21', '客户名称超过120位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F22', '总笔数为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F23', '总笔数填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F24', '凭证开立日为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F25', '凭证开立日格式须为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F26', '凭证号码为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F27', '凭证号码长度超过35位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F28', '凭证种类为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F29', '凭证种类填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F30', '凭证币种为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F31', '凭证币种超过10位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F32', '票面金额为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F33', '票面金额格式须为19位，两位小数');

insert into cgb_error_info (CODE, MESSAGE)
values ('F34', '应付账款金额为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F35', '应付账款金额格式须为19位，两位小数');

insert into cgb_error_info (CODE, MESSAGE)
values ('F36', '发票生效日为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F37', '发票生效日格式须为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F38', '发票到期日为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F39', '发票到期日格式须为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F40', '卖方证件类型为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F41', '卖方证件类型超过5位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F42', '卖方证件号码为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F43', '卖方证件号码超过30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F44', '卖方客户名称为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F45', '卖方客户名称超过120位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F46', '供应商名称超过120位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F47', '发票开票日起始日格式需为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F48', '发票开票日截止日格式需为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F49', '应付账款到期起始日格式需为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F50', '应付账款到期截止日格式需为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F51', '收款账号有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F52', '重复的批次号');

insert into cgb_error_info (CODE, MESSAGE)
values ('F53', '信用卡转入失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('F54', '对公核心扣帐失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('F55', '卡已过期');

insert into cgb_error_info (CODE, MESSAGE)
values ('F56', '卡暂停使用');

insert into cgb_error_info (CODE, MESSAGE)
values ('F57', '卡受限制');

insert into cgb_error_info (CODE, MESSAGE)
values ('F58', '总笔数为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F59', '总交易金额为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F60', '客户批次子流水号为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F61', '客户批次子流水号长度超过20位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F62', '交易类型为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F63', '交易类型不为0或1');

insert into cgb_error_info (CODE, MESSAGE)
values ('F64', '收款银行地址长度超过100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F65', '交易金额填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F66', '摘要长度超过22位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F67', '附言长度超过100位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F68', '交易超时');

insert into cgb_error_info (CODE, MESSAGE)
values ('F69', '卡户人状态无效，拒绝交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('F70', '发票状态填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F71', '买方证件类型为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F72', '买方证件类型超过5位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F73', '买方证件号码为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F74', '买方证件号码超过30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F75', '卖方审核状态不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('F76', '开始记录数填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F77', '最大条数填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F78', '备注长度超过200');

insert into cgb_error_info (CODE, MESSAGE)
values ('F79', '备注长度超过50');

insert into cgb_error_info (CODE, MESSAGE)
values ('F80', '明细序号为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F81', '应付账款余额为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F82', '应付账款余额格式须为19位，两位小数');

insert into cgb_error_info (CODE, MESSAGE)
values ('F83', '买方客户名称长度超过120位');

insert into cgb_error_info (CODE, MESSAGE)
values ('F84', '明细序号填写有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('F85', '卖方名称长度超过120');

insert into cgb_error_info (CODE, MESSAGE)
values ('F86', '转让编号长度超过16');

insert into cgb_error_info (CODE, MESSAGE)
values ('F87', '付款流水号长度超过30');

insert into cgb_error_info (CODE, MESSAGE)
values ('F88', '卖方收款账号为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F89', '卖方收款账号长度超过30');

insert into cgb_error_info (CODE, MESSAGE)
values ('F90', '起始交易日期为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F91', '起始交易日期格式须为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F92', '截止交易日期为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('F93', '截止交易日期格式须为yyyyMMdd');

insert into cgb_error_info (CODE, MESSAGE)
values ('F94', '转让日期起始格式须为YYYYMMDD');

insert into cgb_error_info (CODE, MESSAGE)
values ('F95', '转让日期截止格式须为YYYYMMDD');

insert into cgb_error_info (CODE, MESSAGE)
values ('F96', '批次流水号长度超过30');

insert into cgb_error_info (CODE, MESSAGE)
values ('F97', '明细备注长度超过200');

insert into cgb_error_info (CODE, MESSAGE)
values ('F98', '明细备注长度超过50');

insert into cgb_error_info (CODE, MESSAGE)
values ('F99', '查询供应商信息失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M00', '保理系统通信异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('M01', '批次类型为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('M02', '批次类型输入有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('M03', '批次号为空');

insert into cgb_error_info (CODE, MESSAGE)
values ('M04', '批次号超过30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('M05', '全部失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M06', '批次流水号已经存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('M07', '核销失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M08', '请求方证件有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('M09', '合作客户推介失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M10', '交易请求方不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('M11', '应付账款核销查询失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M12', '修改应付账款信息失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M13', '日期格式不为YYYYMMDD');

insert into cgb_error_info (CODE, MESSAGE)
values ('M14', '应付账款信息查询失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('M15', '明细序号有重复值');

insert into cgb_error_info (CODE, MESSAGE)
values ('M16', '查询转让通知书失败');

insert into cgb_error_info (CODE, MESSAGE)
values ('K01', '该机器没有插入Key盾');

insert into cgb_error_info (CODE, MESSAGE)
values ('K02', '操作员密码加密算法目前仅支持RSA1024、RSA2048两种模式');

insert into cgb_error_info (CODE, MESSAGE)
values ('K03', '操作员密码长度只能是8-20位，且必须同时包含字母和数字,不能包含除字母                和数字以外的字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('K04', '2次输入的操作员密码不相符');

insert into cgb_error_info (CODE, MESSAGE)
values ('K05', '当前密码已过有效期');

insert into cgb_error_info (CODE, MESSAGE)
values ('K06', '每页数目输入的格式不正确或超出指定范围');

insert into cgb_error_info (CODE, MESSAGE)
values ('K07', '当前页码输入的格式不正确或超出指定范围');

insert into cgb_error_info (CODE, MESSAGE)
values ('K08', '该账户是非对公定期账户');

insert into cgb_error_info (CODE, MESSAGE)
values ('K09', '理财产品账户信息查询无数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('K10', '理财产品信息查询无数据');

insert into cgb_error_info (CODE, MESSAGE)
values ('K11', '操作员原密码长度必须少于20位，且必须包含字母和数字,不能包含除字母和                 数字以外的字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('K12', '操作员新密码长度必须是8-12位，且必须包含字母和数字,不能包含除字母和                数字以外的字符');

insert into cgb_error_info (CODE, MESSAGE)
values ('K13', '该客户已被永久冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('K14', '该客户今日已被临时冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('K15', '当日第三次输错密码');

insert into cgb_error_info (CODE, MESSAGE)
values ('K16', '当日第四次输错密码');

insert into cgb_error_info (CODE, MESSAGE)
values ('K17', '当日五次输错密码，该客户今日已被临时冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('K18', '连续10次输错密码，改客户已被永久冻结');

insert into cgb_error_info (CODE, MESSAGE)
values ('K19', '当日第一次输错密码');

insert into cgb_error_info (CODE, MESSAGE)
values ('K20', '当日第二次输错密码');

insert into cgb_error_info (CODE, MESSAGE)
values ('K21', '新密码与原密码一致,不可修改');

insert into cgb_error_info (CODE, MESSAGE)
values ('G01', '重复的收款帐号');

insert into cgb_error_info (CODE, MESSAGE)
values ('G02', '收款金额必须是长度小于16位的数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('G03', '总交易金额和实际金额不符');

insert into cgb_error_info (CODE, MESSAGE)
values ('G04', '收款金额有误，必须大于0的数字，必须是两位小数，长度不能大于16');

insert into cgb_error_info (CODE, MESSAGE)
values ('G08', '查询标志值有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('G09', '查询子交易数必填，且长度不能大于4');

insert into cgb_error_info (CODE, MESSAGE)
values ('G10', '子交易编号必填，且长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('G11', '查询标志为汇总，子交易数须为0');

insert into cgb_error_info (CODE, MESSAGE)
values ('G12', '查询子交易数与记录行数不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('G13', '查询账号不能为空，长度不能大于32');

insert into cgb_error_info (CODE, MESSAGE)
values ('G14', '日期不能为空，格式为YYYYMMDD');

insert into cgb_error_info (CODE, MESSAGE)
values ('G15', '查询类型取值只能为0');

insert into cgb_error_info (CODE, MESSAGE)
values ('G16', '查询笔数不能为空，且取值在1到1000之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('G17', '查询单笔时，只能有一条记录');

insert into cgb_error_info (CODE, MESSAGE)
values ('G18', '查询类型必填，且只能为1,2');

insert into cgb_error_info (CODE, MESSAGE)
values ('G19', '查询类型必填，且只能为1,2,3');

insert into cgb_error_info (CODE, MESSAGE)
values ('G20', '查询笔数不能为空，且取值在1到2000之间');

insert into cgb_error_info (CODE, MESSAGE)
values ('G21', '签约协议号不能为空，长度不能大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('G22', '付款户名不能为空，长度不能大于50');

insert into cgb_error_info (CODE, MESSAGE)
values ('G23', '付款银行名不能为空，长度不能大于100');

insert into cgb_error_info (CODE, MESSAGE)
values ('G24', '付款账号不能为空，长度不能大于20');

insert into cgb_error_info (CODE, MESSAGE)
values ('G25', '证件类型不合法');

insert into cgb_error_info (CODE, MESSAGE)
values ('G26', '证件号不能为空，长度不能大于30');

insert into cgb_error_info (CODE, MESSAGE)
values ('G27', '代扣种类不合规则');

insert into cgb_error_info (CODE, MESSAGE)
values ('G28', '操作类型不合规则');

insert into cgb_error_info (CODE, MESSAGE)
values ('G29', 'G29         子交易编号该批次中不存在　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G30', '付款方银行行号长度超长　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G31', '付款方银行账号不能为空，长度不能大于32　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G32', '收款方银行账号不能为空，长度不能大于32　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G33', '托收类型长度不能大于20位　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G34', '备注类型长度不能大于30位　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G35', '签约协议号长度不能大于30位　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G36', '预留字段长度不能大于100位　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G37', '客户流水号不能为空　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G38', '签约协议号填写有误　');

insert into cgb_error_info (CODE, MESSAGE)
values ('G39', '子交易编号重复　');

insert into cgb_error_info (CODE, MESSAGE)
values ('H01', '普通定期存款的起存金额不能低于100,000.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H02', '通知存款的起存金额不能低于500,000.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H03', '业务类型不能为空，且取值只能为0、1');

insert into cgb_error_info (CODE, MESSAGE)
values ('H04', '活期账号不能为空，且长度不能大于32位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('H05', '定期账号不能为空，且长度不能大于32位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('H06', '存期输入不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('H07', '到期自动续存标志不能为空，且取值只能为B、P、A');

insert into cgb_error_info (CODE, MESSAGE)
values ('H08', '交易金额不能为空，且必须大于0,长度不能大于20,整数位不能超过17，小数                 位不能超过2');

insert into cgb_error_info (CODE, MESSAGE)
values ('H09', '转出户名长度不能大于50位');

insert into cgb_error_info (CODE, MESSAGE)
values ('H10', '转入户名长度不能大于50位');

insert into cgb_error_info (CODE, MESSAGE)
values ('H11', '转出户名有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('H12', '存单号码不能为空，且长度不能大于20位数字');

insert into cgb_error_info (CODE, MESSAGE)
values ('H13', '通知存款的最低留存金额不能低于500,000.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H14', '通知存款的最低支取金额不能低于100,000.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H15', '普通定期存款的最低支取金额为0.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H16', '普通定期存款的最低留存金额不能低于10,000.00元');

insert into cgb_error_info (CODE, MESSAGE)
values ('H17', '支取类型不能为空，且取值只能为C、P');

insert into cgb_error_info (CODE, MESSAGE)
values ('H18', '该账户无此凭证');

insert into cgb_error_info (CODE, MESSAGE)
values ('H19', '定期账号和存单号在银企直联系统中不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('H20', '账户不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('H21', '该账号不是通知存款');

insert into cgb_error_info (CODE, MESSAGE)
values ('H22', '该账号不是人民币定期账号');

insert into cgb_error_info (CODE, MESSAGE)
values ('H23', '可用余额等于0');

insert into cgb_error_info (CODE, MESSAGE)
values ('H24', '该账号无此凭证');

insert into cgb_error_info (CODE, MESSAGE)
values ('H25', '部提剩余本金必须大于留存金额');

insert into cgb_error_info (CODE, MESSAGE)
values ('H26', '不可跨所交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('H27', '账户必须全部结清');

insert into cgb_error_info (CODE, MESSAGE)
values ('H28', '转入户名有误');

insert into cgb_error_info (CODE, MESSAGE)
values ('H29', '输入的金额不是账户全部余额');

insert into cgb_error_info (CODE, MESSAGE)
values ('H30', '金额必须大于0');

insert into cgb_error_info (CODE, MESSAGE)
values ('H31', '单笔购买金额必须不大于2亿');

insert into cgb_error_info (CODE, MESSAGE)
values ('H32', '超出可用份额');

insert into cgb_error_info (CODE, MESSAGE)
values ('R03', '付款信息与签约时不一致');

insert into cgb_error_info (CODE, MESSAGE)
values ('R04', '核心主机业务处理异常');

insert into cgb_error_info (CODE, MESSAGE)
values ('R05', '代收款账号没注册');

insert into cgb_error_info (CODE, MESSAGE)
values ('R06', '既没提供托收类型，又没提供客户协议号');

insert into cgb_error_info (CODE, MESSAGE)
values ('R07', '客户协议号不存在');

insert into cgb_error_info (CODE, MESSAGE)
values ('R08', '该协议的托收类型为免签约,不能送签约协议号做交易');

insert into cgb_error_info (CODE, MESSAGE)
values ('R09', '既提供托收类型，又提供客户协议号');

insert into cgb_error_info (CODE, MESSAGE)
values ('R10', '客户上送了托收类型,但客户的收款账户其实并非免签');

insert into cgb_error_info (CODE, MESSAGE)
values ('R11', '不受理其他代收的类型');

insert into cgb_error_info (CODE, MESSAGE)
values ('R12', '子公司账号还没授权给母公司');

insert into cgb_error_info (CODE, MESSAGE)
values ('R13', '被操作账号不属于自己');

insert into cgb_error_info (CODE, MESSAGE)
values ('R14', '根据账号找不到对应的账号信息');

insert into cgb_error_info (CODE, MESSAGE)
values ('R15', '根据网银客户号找不到对应的核心客户号');

insert into cgb_error_info (CODE, MESSAGE)
values ('R16', '未建立母子关系');

insert into cgb_error_info (CODE, MESSAGE)
values ('R17', '查询类型错误');

insert into cgb_error_info (CODE, MESSAGE)
values ('R18', '核销流水号必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R19', '核销流水号最大长度为30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R20', '付款金额必填,且最大长度为19位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R21', '付款金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R22', '核销总金额必填,且最大长度为19位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R23', '核销总金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R24', '供应商收款账号必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R25', '供应商收款账号最长30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R26', '供应商证件类型必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R27', '供应商证件类型最长5位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R28', '供应商证件号码必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R29', '供应商证件号码最长30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R30', '核销金额必填,且最大长度19位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R31', '核销金额格式不正确');

insert into cgb_error_info (CODE, MESSAGE)
values ('R32', '核心企业类型');

insert into cgb_error_info (CODE, MESSAGE)
values ('R33', '核心企业类型填1或2');

insert into cgb_error_info (CODE, MESSAGE)
values ('R34', '推荐客户证件类型必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R35', '推荐客户证件类型最大长度5位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R36', '推荐客户证件号码必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R37', '推荐客户证件号码最大长度30位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R38', '推荐客户名称必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R39', '推荐客户名称最大长度120位');

insert into cgb_error_info (CODE, MESSAGE)
values ('R40', '推荐客户类型必填');

insert into cgb_error_info (CODE, MESSAGE)
values ('R41', '推荐客户类型填1或2');

insert into cgb_error_info (CODE, MESSAGE)
values ('R42', '年度合作金额最大长度16位');

create sequence hyt_seq
minvalue 1
maxvalue 999999999999999
start with 1
increment by 1;

create table DHB_KEYINFO
(
  MERCHID   VARCHAR2(32) not null,
  KEYSRC    VARCHAR2(16),
  SECRETKEY VARCHAR2(32)
);

alter table DHB_KEYINFO
  add constraint DHB_KEYINFO_PK primary key (MERCHID);
  
 create table DHB_CHANNEL_INFO
(
  CHANNELID VARCHAR2(2) not null,
  NAME      VARCHAR2(20),
  BEANNAME  VARCHAR2(20)
);

alter table DHB_CHANNEL_INFO
  add primary key (CHANNELID);
  
  insert into dhb_channel_info (CHANNELID, NAME, BEANNAME)
values ('2', '金运通', 'JYTPayCutService');

insert into dhb_channel_info (CHANNELID, NAME, BEANNAME)
values ('1', '广发', 'CGBPayService');

create table dhb_channel_choose(
bizType number(1),
channelId number(1),
bankName varchar2(40),
maxMoney number(11,2),
fee number(10,2)
);
create table dhb_pay_cut_channel(
bizType number(1),
channelId number(1)
);
insert into dhb_pay_cut_channel (BIZTYPE, CHANNELID)
values ('1', '2');

insert into dhb_pay_cut_channel (BIZTYPE, CHANNELID)
values ('2', '2');

insert into dhb_pay_cut_channel (BIZTYPE, CHANNELID)
values ('3', '2');

create table dhb_proxy_carry_bankacct
(
  id      VARCHAR2(1) not null,
  ACCT_NO        VARCHAR2(30),
  ACCT_NAME      VARCHAR2(80),
  BANK_NAME      VARCHAR2(80),
  BANK_CODE      VARCHAR2(30),
  BANK_CITY_CODE VARCHAR2(8),
  LAST_UPD_DATE  DATE not null,
  OPERATOR_ID    VARCHAR2(10) not null,
  REVIEWER_ID    VARCHAR2(10),
  AREA_NAME      VARCHAR2(100),
  EXECUTION_ID   VARCHAR2(255),
  CHANNELID      VARCHAR2(2),
  BIZTYPE        VARCHAR2(2)
);

-- Add comments to the columns 
comment on column dhb_proxy_carry_bankacct.id
  is '账户类型：对公账户，借记卡';
comment on column dhb_proxy_carry_bankacct.ACCT_NO
  is '账号';
comment on column dhb_proxy_carry_bankacct.ACCT_NAME
  is '开户行名称';
comment on column dhb_proxy_carry_bankacct.BANK_NAME
  is '账户名称';
comment on column dhb_proxy_carry_bankacct.BANK_CODE
  is '清算代码';
comment on column dhb_proxy_carry_bankacct.BANK_CITY_CODE
  is '城市代码';
comment on column dhb_proxy_carry_bankacct.OPERATOR_ID
  is '操作员';
comment on column dhb_proxy_carry_bankacct.REVIEWER_ID
  is '复核员';
comment on column dhb_proxy_carry_bankacct.EXECUTION_ID
  is '流程实例编号,由于管理jbpm处理流程';
  
  insert into dhb_proxy_carry_bankacct (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME)
values ('2', '11014720253888', '东汇宝支付有限公司', '平安银行深圳分行新闻路支行', '307584007998', '', to_date('10-05-2015 15:42:15', 'dd-mm-yyyy hh24:mi:ss'), '100005', '100005', '', '', '2', '2', '金运通');

insert into dhb_proxy_carry_bankacct (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME)
values ('1', '136001512010007819', '东汇宝支付有限公司', '广发银行南京分行营业部', '306581000003', '', to_date('11-11-2015 16:41:49', 'dd-mm-yyyy hh24:mi:ss'), '100027', '100027', '', '', '2', '1', '金运通');

insert into dhb_proxy_carry_bankacct (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME)
values ('3', '101001513010006954', '银企客户接入专用四十', '广发银行南京分行营业部', '306581000003', '', to_date('11-11-2015 16:41:49', 'dd-mm-yyyy hh24:mi:ss'), '100027', '100027', '', '', '1', '2', '广发');

insert into dhb_proxy_carry_bankacct (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME)
values ('4', '136001512010007819', '东汇宝支付有限公司', '广发银行南京分行营业部', '306581000003', '', to_date('11-11-2015 16:41:49', 'dd-mm-yyyy hh24:mi:ss'), '100027', '100027', '', '', '3', '1', 'ChinaPay');

insert into dhb_proxy_carry_bankacct (ID, ACCT_NO, ACCT_NAME, BANK_NAME, BANK_CODE, BANK_CITY_CODE, LAST_UPD_DATE, OPERATOR_ID, REVIEWER_ID, AREA_NAME, EXECUTION_ID, CHANNELID, BIZTYPE, CHANNELNAME)
values ('5', '136001512010007819', '东汇宝支付有限公司', '广发银行南京分行营业部', '306581000003', '', to_date('11-11-2015 16:41:49', 'dd-mm-yyyy hh24:mi:ss'), '100027', '100027', '', '', '3', '2', 'ChinaPay');


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
create sequence cgb_dhb_seq
minvalue 1
maxvalue 999
start with 1
increment by 1
CYCLE
CACHE 10;
create table DHB_MERCH_CHANNEL
(
  MERCHID   VARCHAR2(30) not null,
  CHANNELID VARCHAR2(2) not null
);
alter table DHB_MERCH_CHANNEL
  add constraint DHB_MERCH_CHANNEL_KP primary key (MERCHID, CHANNELID);

alter table DHB_MERCH_CHANNEL
  add constraint DHB_MERCH_CHANNEL_FP1 foreign key (MERCHID)
  references DHB_KEYINFO (MERCHID);
alter table DHB_MERCH_CHANNEL
  add constraint DHB_MERCH_CHANNEL_FP2 foreign key (CHANNELID)
  references DHB_CHANNEL_INFO (CHANNELID);
  
create table dhb_bankname_list(
bankId number(3) primary key,
bankName varchar2(30),
bankcode varchar2(4)
);
insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (1, '广发银行', '0306');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (2, '中国工商银行', '0102');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (3, '中国农业银行', '0103');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (4, '中国建设银行', '0105');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (5, '中国光大银行', '0303');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (6, '华夏银行', '0304');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (7, '邯郸银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (8, '兰州银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (9, '中国民生银行', '0305');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (10, '中国银行', '0104');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (26, '江苏长江商业银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (12, '交通银行', '0301');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (13, '中国邮政储蓄银行', '0100');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (14, '招商银行', '0308');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (15, '光大银行', '0303');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (16, '中信银行', '0302');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (17, '浦发银行', '0310');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (18, '平安银行', '0410');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (19, '兴业银行', '0309');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (20, '北京银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (21, '上海银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (22, '厦门银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (23, '东莞银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (24, '杭州银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (25, '宁波银行', '0313');

insert into dhb_bankname_list (BANKID, BANKNAME, BANKCODE)
values (27, '武汉农村商业银行', '0402');

