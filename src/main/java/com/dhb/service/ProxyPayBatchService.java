package com.dhb.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.PageDao;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.ChannelInfoDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbPayCut;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBatch;
import com.dhb.entity.ReviewType;
import com.dhb.entity.exception.ProxyBatchValidationException;
import com.dhb.entity.form.ProxyBatchParam;
import com.dhb.util.PropFileUtil;
import com.dhb.util.SpringContextHelper;
import com.dhb.util.Tools;
import com.dhb.util.excell.ExcelReaderUtil;
import com.dhb.util.excell.IRowReader;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Service("proxyPayBatchService")
@Transactional
public class ProxyPayBatchService {
	private static final Log logger = LogFactory.getLog(ProxySendSalaryService.class);
	@Autowired
	private PageDao pageDao;
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private ProxyBatchPayThreadService proxyBatchPayThreadService;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoService;
	@Autowired
	private ChannelInfoDao channelService;
	
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
    private final static String merchId = PropFileUtil.getByFileAndKey("cgb.properties", "merchId");
    
    
     
	class ProxyPayBatchExcelFileReader implements IRowReader{
		//记录数
		private Integer total_num = 0;
		//总金额
		private Double total_money = 0.0;
		//记录
		private List<List<String>> records = new ArrayList<List<String>>();
		//错误
		private List<Map<String,String>> errors = new ArrayList<Map<String,String>>();
		@Override
		public void getRows(int sheetIndex, int curRow, List<String> rowlist)
				throws Exception {
			if (curRow == 0) {
				return;
			}
			if(rowlist.size() == 0){
				return;
			}
			validateRow(sheetIndex, curRow,rowlist, errors);
			ArrayList<String> list = new ArrayList<String>();
			for(String str:rowlist){
				list.add(str);
			}
			records.add(list);
			//交易笔数累加
			total_num ++;
			//交易金额累加
			total_money += Double.valueOf(rowlist.get(4));
			
		}
		private void   validateRow(int sheetIndex, int curRow,List<String> rowlist,List<Map<String,String>> errors){
			Map<String,String> errMap = new HashMap<String,String>();
			if(rowlist.size() <5){
				errMap.put("cols_count", "列数不够，请检查第"+curRow+"行数据是否正确");
				errMap.put("sheetIndex",sheetIndex+"");
				errMap.put("curRow", curRow+"");
				errMap.put("content", rowlist.toString());
				errors.add(errMap);
				return;
			}
			//户名(必填)	账号(必填)	银行名(必填)	联行号(必填)	交易金额(必填)		备注

			//户名
			String acct_name = rowlist.get(0);
			//账号
			String acct_no = rowlist.get(1);
			String bankName = rowlist.get(2);
			//清算行号
			String bank_no = rowlist.get(3);
			//交易金额
			String money = rowlist.get(4);
			//附言信息
			String memo = "";
			if(rowlist.size()==6){
				
				 memo = rowlist.get(5);
			}
			 
			if(StringUtils.isEmpty(acct_name)){
				errMap.put("acct_name", "户名为空");
			}
			if(StringUtils.isEmpty(acct_no)){
				errMap.put("acct_no", "账号为空");
			}
			if(StringUtils.isEmpty(bank_no)){
				errMap.put("bank_no", "清算行行号为空");
			}
			if(StringUtils.isEmpty(money)){
				errMap.put("money", "代付金额为空 ");
			}else{
				try {
					Double.valueOf(money);
				} catch (Exception e) {
					errMap.put("money","交易金额必须为数字");
				}
			}
			if(StringUtils.length(memo) > 127){
				errMap.put("memo", "附言过长，最大127个字符（包含中文）");
			}
			if(errMap.size() >0 ){
				errMap.put("sheetIndex",sheetIndex+"");
				errMap.put("curRow", curRow+"");
				errors.add(errMap);
			}
		}
		public Integer getTotal_num() {
			return total_num;
		}
		public void setTotal_num(Integer total_num) {
			this.total_num = total_num;
		}
		public Double getTotal_money() {
			return total_money;
		}
		public void setTotal_money(Double total_money) {
			this.total_money = total_money;
		}
		public List<List<String>> getRecords() {
			return records;
		}
		public void setRecords(List<List<String>> records) {
			this.records = records;
		}
		public List<Map<String, String>> getErrors() {
			return errors;
		}
		public void setErrors(List<Map<String, String>> errors) {
			this.errors = errors;
		}
		
		
	}
	/**
	 * 导入上传的excel文件中的内容到数据库，分别到批次表和明细表（代收付表）
	 * @param fee_item_code 费项代码
	 * @param file 上传组件的文件对象
	 * @throws Exception
	 */
	@Transactional
	public void importBatchFile(String channelId,String merchId,CommonsMultipartFile file) throws Exception{
		String filename = file.getFileItem().getName();
		ProxyPayBatchExcelFileReader reader = new ProxyPayBatchExcelFileReader();
		ExcelReaderUtil.readExcel(reader, file.getInputStream(), filename.substring(filename.indexOf(".")));
		if(reader.getErrors().size() > 0){
			throw new ProxyBatchValidationException("批量代付文件上传内容校验失败，请检查", reader.getErrors());
		}
		ProxyBatch proxyPayBatch = new ProxyBatch();
		Date now = new Date();	
		String batch_id = "batchpay"
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("proxy_biz_journal_seq")+"",6, '0');
		proxyPayBatch.setBatchId(batch_id);
		proxyPayBatch.setMerchId(merchId);
		proxyPayBatch.setBizType(BizType.Pay.getCode());
		proxyPayBatch.setChannelId(channelId);
		proxyPayBatch.setFilename(filename);
		proxyPayBatch.setTotalNum(reader.getTotal_num());
		proxyPayBatch.setTotalMoney(reader.getTotal_money());
		//创建上传日期
		proxyPayBatch.setCreateTime(new Date());
		//已经提交
		proxyPayBatch.setReviewStatus("0");
		String sql = "insert into Dhb_Proxy_Batch_Record(batchId,bizType,merchId,filename,"
				      + "channelId,totalNum,totalMoney,totalSuccNum,totalSuccMoney,"
				      + "createTime,reviewTime,reviewStatus,"
				      + "reviewComments,remark)"
				      + " values(:batchId,:bizType,:merchId,:filename,"
				      + ":channelId,:totalNum,:totalMoney,:totalSuccNum,:totalSuccMoney,"
				      + ":createTime,:reviewTime,:reviewStatus,"
				      + ":reviewComments,:remark)";
		commonObjectDao.saveOrUpdate(sql,proxyPayBatch);
		importDetails(channelId,reader.getRecords(), proxyPayBatch.getBatchId(),merchId);
	}
	@Transactional
	public void importDetails(String channelId,List<List<String>> records, String batch_id,String merchId){
		Date now = new Date();
		for (int index = 0, len = records.size(); index < len; index++) {
			List<String> rowlist = records.get(index);
			//户名
			String acct_name = rowlist.get(0);
			//账号
			String acct_no = rowlist.get(1);
			String bankName = rowlist.get(2);
			//清算行号
			String bank_no = rowlist.get(3);
			//交易金额
			String money = rowlist.get(4);
			//附言信息
			String memo ="";
			if(rowlist.size()>=6){
				 memo = rowlist.get(5);
				 if(memo.length()>50){
					 memo = memo.substring(0, 50);
				 }
			}
			DhbPayCut payCut = new DhbPayCut();
			
			payCut.setAccName(acct_name);
			payCut.setAccNo(acct_no);
			payCut.setBankcode(bank_no);
			payCut.setBankName(bankName);
			payCut.setBatchId(batch_id);
			payCut.setBizType(BizType.Pay.getCode());
			payCut.setChannelId(channelId);
			payCut.setCreateTime(now);
			payCut.setCurrency("CNY");
			payCut.setMemo(memo);
			payCut.setMoney(Double.valueOf(money));
			payCut.setMerchId(merchId);
			payCut.setRecordId(Tools.getUUID());
			payCut.setReviewStatus(ReviewType.Commit.getCode());
			
		
			String sql = "insert into dhb_pay_cut(recordId,outId,merchId,bizType,channelId,"
					+ "bankcode,bankName,accNo,accName,identityNo,currency,money,memo,"
					+ "reviewStatus,createTime,batchId) "
					
					+ "values (:recordId,:outId,:merchId,:bizType,:channelId,"
					+ ":bankcode,:bankName,:accNo,:accName,:identityNo,:currency,:money,:memo,"
					+ ":reviewStatus,:createTime,:batchId)";
			commonObjectDao.saveOrUpdate(sql, payCut);
		}
	}
	public void queryByForm(ProxyBatchParam proxyPayBatchParam) throws ParseException{
		
		List<Object> list = Lists.newLinkedList();
		StringBuilder sb = new StringBuilder();
		String startTime = proxyPayBatchParam.getStartDate();
		String endTime = proxyPayBatchParam.getEndDate();
		String status = proxyPayBatchParam.getReview_status();
		String merchId = proxyPayBatchParam.getMerchId();
		sb.append("select b.* from Dhb_Proxy_Batch_Record b ")
		  .append("where 1=1 ");
		
		if(!Strings.isNullOrEmpty(merchId)){
			sb.append("and merchId=:merchId ");
			list.add(merchId);
		}
		if(!Strings.isNullOrEmpty(startTime)){
			sb.append("and createTime>to_date(:createTime,'yyyy-MM-dd HH24:mi:ss') ");
			list.add(startTime);
		}
		if(!Strings.isNullOrEmpty(endTime)){
			sb.append("and createTime<to_date(:createTime,'yyyy-MM-dd HH24:mi:ss') ");
			list.add(endTime);
		}
		if(!Strings.isNullOrEmpty(status)){
			sb.append("and status=:status ");
			list.add(status);
		}
		sb.append("and biztype ='").append(BizType.Pay.getCode()).append("' ");
		sb.append("order by createTime desc ");
		String sql = sb.toString();
		logger.debug("sql:("+sql+")");
		pageDao.getPage(sql, list.toArray(), proxyPayBatchParam.getPager());
	}
	public List<DhbPayCut> queryProxyPayBatchDetail(String batchId){

		
		String sql = "select merchId, bankName,accNo,accName,bankcode,channelId,money,reviewStatus"
				+ " from dhb_pay_cut where batchId=:batchId order by createTime desc";
		List<DhbPayCut> list=commonObjectDao.findList(sql, DhbPayCut.class, new Object[]{batchId});
		 return list;//entityDao.createWhere(ProxyPay.class).eq("batch_id",batchId.trim() ).orderByAsc("create_datetime").toList();
	}
	public ProxyBatch queryProxyBatchById(String batchId){
		
		
		String sql = "select bizType,batchId,reviewStatus,reviewComments,totalNum,totalMoney,channelId from Dhb_Proxy_Batch_Record where batchId=:batchId";
		List<ProxyBatch> list=commonObjectDao.findList(sql, ProxyBatch.class, new Object[]{batchId});
		if(list.size()>0){
			return list.get(0);
		}
		 return null;
	}
	@Transactional
	public void audit(ProxyBatch proxyPayBatch) throws Exception{
		ProxyBatch proxyPayBatchInDb =queryProxyBatchById(proxyPayBatch.getBatchId());
		String status = proxyPayBatch.getReviewStatus();
		proxyPayBatchInDb.setReviewStatus(proxyPayBatch.getReviewStatus());
		String reviewComment = proxyPayBatch.getReviewComments();
		proxyPayBatchInDb.setReviewComments(proxyPayBatch.getReviewComments());
		Date now=	new Date();
		proxyPayBatchInDb.setReviewTime(now);
		String batchId = proxyPayBatch.getBatchId();
	    String batchSql = "update Dhb_Proxy_Batch_Record set reviewStatus=:reviewStatus,reviewComments=:reviewComments , reviewTime=:reviewTime where batchId=:batchId";
	    commonObjectDao.saveOrUpdate(batchSql, proxyPayBatchInDb);
	    DhbPayCut proxy = new DhbPayCut();
	    proxy.setReviewStatus(status);
	    proxy.setReviewTime(now);
	    proxy.setBatchId(batchId);
	    if(reviewComment!=null){
	    	 if(reviewComment.length()>70){
	    		 reviewComment = reviewComment.substring(0, 70);
			 }
	    }
	    proxy.setReviewComments(reviewComment);
	    String praysql = "update dhb_pay_cut set reviewStatus=:reviewStatus,reviewComments=:reviewComments , reviewTime=:reviewTime where batchId=:batchId ";
	    commonObjectDao.saveOrUpdate(praysql, proxy);	
		if ("1".equals(status)) {
			batchPay(proxyPayBatchInDb);
		}
	}
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void batchPay(ProxyBatch batch) throws Exception{
		String batchId = batch.getBatchId();
		String paySql = "select * from dhb_pay_cut where  batchId=:batchId ";
		List<DhbPayCut> payList = commonObjectDao.findList(paySql, DhbPayCut.class, new Object[]{batchId});
		
		BatchTranReq tranReq = new BatchTranReq();
		tranReq.setBizType(batch.getBizType());
		tranReq.setBatchId(batchId);
		tranReq.setMerchId(batch.getMerchId());
		tranReq.setTotalBalance(batch.getTotalMoney());
		tranReq.setTotalNum(batch.getTotalNum());
		tranReq.setChannelId(batch.getChannelId());
		List<OutRequestInfo> payInfo = Lists.newArrayList();
		tranReq.setInfo(payInfo);
		for(DhbPayCut pay:payList){
			OutRequestInfo toPay = new OutRequestInfo();
			toPay.setTranNo(pay.getRecordId());
			toPay.setMerchId(pay.getMerchId());
			toPay.setAccName(pay.getAccName());
			toPay.setAccNo(pay.getAccNo());
			toPay.setBankName(pay.getBankName());
			toPay.setBankCode(pay.getBankcode());
			toPay.setBanlance(pay.getMoney());
			toPay.setComments(pay.getMemo());
			payInfo.add(toPay);
		}
		String channelId = tranReq.getChannelId();
		String beanName=channelService.getBeanNameByChannelId(channelId);
		if(!Strings.isNullOrEmpty(beanName)){
			PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
			proxyBatchPayThreadService.toBatchPay(tranReq,service);
		}
	}
	
}
