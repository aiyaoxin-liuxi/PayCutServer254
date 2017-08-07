package com.dhb.umpay.service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.dhb.anyz.service.ANYZUtil;
import com.dhb.chinapay.entity.CpBatchFile;
import com.dhb.chinapay.service.ChinaPayDaoService;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.DhbOutMerchantDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBizJournal;
import com.dhb.entity.ProxyCarryBankacct;
import com.dhb.entity.ProxyMerchAmt;
import com.dhb.entity.SingleResp;
import com.dhb.service.PayCutInterface;
import com.dhb.umpay.entity.FtpUtil;
import com.dhb.umpay.entity.UmpayUtil;
import com.dhb.util.AmountUtil;
import com.dhb.util.ArithUtil;
import com.dhb.util.DateUtil;
import com.dhb.util.JsonUtil;
import com.google.common.base.Strings;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.StringUtils;
import com.umpay.api.common.ReqData;
import com.umpay.api.exception.ReqDataException;
import com.umpay.api.exception.RetDataException;
import com.umpay.api.exception.VerifyException;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.api.paygate.v40.Plat2Mer_v40;

public class UmpayPayCutService implements PayCutInterface{
	public static Logger logger = Logger.getLogger(UmpayPayCutService.class);
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
	@Autowired
	private ChinaPayDaoService chinaPayDaoService;
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
    
	
    /**
     * 签约协议
     * @param req
     * @return
     */
    public SingleResp cutContract(OutRequestInfo req){
    	SingleResp resp = new SingleResp();
    	resp.setTranNo(req.getTranNo());
    	String mer_id= ResourceUtil.getString("umpay", "merId");
		String accType = "0";//0对私   1对公
		if("00".equals(req.getAccType())){
			
		}else{
			accType = "1";
		}
		String certType = UmpayUtil.identity_type;//只支持身份证
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_user_reg_uecp);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", mer_id);
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("media_id", req.getMobile());//用户手机号
		map.put("media_type", UmpayUtil.media_type);//媒介类型
		map.put("pub_pri_flag", accType);// Y  银行账户  0对私  1对公
		map.put("identity_type", certType);//Y 证件类型  1身份证
		map.put("identity_code", req.getCertNo());//Y  证件号   用联动的公钥进行RSA加密后，用BASE64(GBK编码)加密该字段
		map.put("identity_name", req.getAccName());//Y  证件姓名 用联动的公钥进行RSA加密后，用BASE64(GBK编码)加密该字段
		map.put("card_id", req.getAccNo());//Y 卡号   用联动的公钥进行RSA加密该字段，然后BASE64加密  
		if("1".equals(accType)){//对公必填
			map.put("bin_bank_id", "");//发卡行
			map.put("card_name", req.getAccName());//对公账户名称
		}
		map.put("card_type", req.getCardType());//银行卡类型  0对私 借记卡     1对私信用卡   Y
		map.put("mer_cust_id", req.getSubContractId());//用户标识  N
		map.put("product_id", ResourceUtil.getString("umpay", "productId"));//业务产品号  Y
		resp = UmpayUtil.signAndVerifyForUmpay(map,req.getTranNo());
		if("0000".equals(resp.getCode())){
			resp.setCode(DhbTranStatus.Succ.getCode());
			resp.setMessage(resp.getMessage());
			return resp;
		}else{
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(resp.getMessage());
			return resp;
		}
    }
    @Override
	public SingleResp singleCut(OutRequestInfo reqInfo) throws Exception {
		String merId = ResourceUtil.getString("umpay", "merId");
		SingleResp singleResp = new SingleResp();
		singleResp.setTranNo(reqInfo.getTranNo());
		Date now = new Date();
		String id = merId
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("umpay_orderId_seq")+"",6, '0');
		
		//调用通道前先保存流水表信息
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Cut.getCode());
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =proxyBankInfoDao.getCutBankInfoByChannleId(channelId);
		journal.setFromBankCardNo(reqInfo.getAccNo());
		journal.setFromIdentityNo(reqInfo.getCertNo());
		journal.setFromBankName(reqInfo.getBankName());
		journal.setFromUserName(reqInfo.getAccName());
		journal.setFromBankCode(reqInfo.getBankCode());
		journal.setToBankCardNo(bankInfo.getAcctNo());
		journal.setToBankCode(bankInfo.getBankCode());
		journal.setToBankName(bankInfo.getBankName());
		journal.setToUserName(bankInfo.getAcctName());
		journal.setMoney(reqInfo.getBanlance());
		journal.setBatchId(reqInfo.getBatchId());
		journal.setCurrency("CNY");
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		
		//组装报文
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_pay_req_direct_uecp);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", merId);
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("media_id", reqInfo.getMobile());//Y 媒介标识
		map.put("media_type", UmpayUtil.media_type);//Y 媒介类型
		map.put("order_id", id);// Y 商户唯一订单号
		map.put("order_date", DateUtil.getDayForYYMMDD());// Y  商户上传文件日期，格式YYYYMMDD
		map.put("orig_amt",AmountUtil.parseAmountStr2Long(reqInfo.getBanlance()+"")+"");//Y 金额 分
		map.put("mer_cust_id", reqInfo.getSubContractId());//用户标识  N
		map.put("product_id", ResourceUtil.getString("umpay", "productId"));//业务产品号  Y
		map.put("is_notify", "1");//N 0：不通知，1：通知
		map.put("notify_url", ResourceUtil.getString("umpay", "notifyCutUrl"));
		
		try {
			ReqData reqDataPost = Mer2Plat_v40.makeReqDataByPost(map);
			Map<String,String> paramMap = reqDataPost.getField();
			logger.info("【umpay cut】"+reqInfo.getTranNo()+",上送报文："+paramMap);
			String url = reqDataPost.getUrl();
			String ret = ANYZUtil.sendMsg(url, paramMap);
			Map<String,String> retMap = Plat2Mer_v40.getResData(ret);
			logger.info("【umpay cut】"+reqInfo.getTranNo()+",返回报文解析后Map："+ret);
			String code = retMap.get("ret_code");
			String message = retMap.get("ret_msg");
			String trade_no = retMap.get("trade_no");
			logger.info("【umpay cut resp】"+reqInfo.getTranNo()+",code="+code+",message="+message+",trade_no="+trade_no);
			if("0000".equals(code)){
				singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(message);
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage(message);
			}
			updateDhbBizJournalById(singleResp, journal);
			return singleResp;
		} catch (ReqDataException e) {
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("签名异常");
			return singleResp;
		} catch (RetDataException e) {
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("验签异常umpayService");
			return singleResp;
		}
		
	}
	/**
	 * 代扣 支持单笔和批量
	 * @param batchReq
	 * @param recordMap
	 * @return
	 * @throws Exception
	 */
	public SingleResp batchCut(BatchTranReq batchReq,Map<String,String> recordMap) throws Exception {
		SingleResp resp = new SingleResp();
		
		Date now = new Date();
		String merId = batchReq.getMerchId();
		String transDate = DateUtil.getDayForYYMMDD(now); // 交易日期
		
		int tranCount=batchReq.getTotalNum();//总笔数
		int allMoney= (int)(batchReq.getTotalBalance()*100);//总金额  分转成元
		
		//组装txt文件内容
		StringBuilder plain = new StringBuilder();
		plain.append(tranCount).append("|").append(allMoney).append("\r\n");
		
		String batchId = batchReq.getBatchId();
		String umpayMerId = ResourceUtil.getString("umpay", "merId"); 
		String umpaySeqId = StringUtils.leftPad(sequenceDao.getNextVal("umpay_batchId_seq")+"", 3, '0');
		String fileName = "ZK_"+umpayMerId + "_" + transDate + "_" + umpaySeqId + ".txt";
		String filePath = ResourceUtil.getString("umpay", "filePath")+transDate+"/"+fileName;
		
		String seqtranno = batchId;
		if(batchReq.getInfo().size()==1){
			seqtranno = batchReq.getInfo().get(0).getTranNo();
		}
		
		//插入文件上传记录
		CpBatchFile fileInfo = new CpBatchFile();
		fileInfo.setBatchId(batchId);
		fileInfo.setFileName(fileName);
		chinaPayDaoService.insertFile(fileInfo);
		
		for(OutRequestInfo info:batchReq.getInfo()){
			String OrdId = "umpay"
					+ DateUtil.getDayForYYMMDD(now)
					+ DateUtil.getTimeForHHmmss(now)
					+ StringUtils.leftPad(sequenceDao.getNextVal("umpay_orderId_seq")+"",6, '0');
			String money = String.valueOf((int)(info.getBanlance()*100));
			plain.append(info.getMobile()).append("|")
			.append(OrdId).append("|")
			.append(money).append("|")
			.append(info.getSubContractId()).append("|")
			.append("").append("|").append(info.getRemark()).append("\r\n");
			DhbBizJournal journal = new DhbBizJournal();
	   		String channelId = batchReq.getChannelId();
	   		journal.setId(OrdId);
	   		journal.setMerchId(merId);
	   		journal.setBizType(BizType.Cut.getCode());
	   		journal.setChannelId(channelId);
	   		DhbBankInfo bankInfo =proxyBankInfoDao.getCutBankInfoByChannleId(channelId);
	   		journal.setFromBankCardNo(info.getAccNo());
	   		journal.setFromIdentityNo(info.getCertNo());
	   		journal.setFromBankName(info.getBankName());
	   		journal.setFromUserName(info.getAccName());
	   		journal.setToBankCardNo(bankInfo.getAcctNo());
	   		journal.setToBankCode(bankInfo.getBankCode());
	   		journal.setToBankName(bankInfo.getBankName());
	   		journal.setToUserName(bankInfo.getAcctName());
	   		journal.setMoney(info.getBanlance());
	   		journal.setBatchId(info.getBatchId());
	   		journal.setCurrency("CNY");
	   		journal.setMemo(fileName);
	   		journal.setCreateTime(now);
	   		journal.setRecordId(recordMap.get(info.getTranNo()));
	   		dhbBizJournalDao.insertJournal(journal);
		}
		plain.append("END");
		logger.info("【umpay cut】"+seqtranno+",TXT文件内容：\n"+plain.toString());
		boolean flag = FtpUtil.createTxt(plain.toString(), fileName,filePath);
		if(!flag){
			logger.error("【umpay cut】"+seqtranno+",生成ftp文件失败");
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage("生成代扣文件失败");
			//接收通道返回后，更新流水表的数据
			updateDhbBizJournalByBatchId(resp, batchId);
			return resp;
		}
		
		//生成文件ftp到联动优势的FTP
		logger.info("【umpay cut】"+seqtranno+",ftp上传文件开始！");
		flag = FtpUtil.upLoadFromProduction(fileName, filePath);
		if(!flag){
			logger.error("【umpay cut】"+seqtranno+",将文件ftp到联动服务器失败");
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage("FTP传到联动服务器失败");
			//接收通道返回后，更新流水表的数据
			updateDhbBizJournalByBatchId(resp, batchId);
			return resp;
		}
		
		//组装报文
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_pay_req_ftp_uecp);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", umpayMerId);
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("file_name", fileName);//Y 文件名
		map.put("busi_type", UmpayUtil.busi_type_03);//Y 03直扣
		map.put("req_date", DateUtil.getDayForYYMMDD());// Y  商户上传文件日期，格式YYYYMMDD
		
		SingleResp resResp = UmpayUtil.signAndVerifyForUmpay(map,seqtranno);
		if("0000".equals(resResp.getCode())){
			resp.setCode(DhbTranStatus.Handling.getCode());
			resp.setMessage(resResp.getMessage());
		}else{
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(resResp.getMessage());
		}
		//接收通道返回后，更新流水表的数据
		updateDhbBizJournalByBatchId(resp, batchId);
		return resp;
	}
	/**
	 * 通过批次号修改状态
	 * @param resp
	 * @param batchId
	 */
	private void updateDhbBizJournalByBatchId(SingleResp resp,String batchId){
		DhbBizJournal journal = new DhbBizJournal();
		journal.setHandleStatus(resp.getCode());
		journal.setHandleRemark(resp.getMessage());
		journal.setEndTime(new Date());
		journal.setBatchId(batchId);
		dhbBizJournalDao.updateStatusByBatchId(journal);
	}
	public static void main(String[] args) throws IOException {
		//ftp文件交易接口 组装报文
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_pay_req_ftp_uecp);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", "25019");
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("file_name", "ZK_25019_20161205_022.txt");//Y 文件名
		map.put("busi_type", UmpayUtil.busi_type_03);//Y 03直扣
		map.put("req_date", "20161205");// Y  商户上传文件日期，格式YYYYMMDD
		//订单查询接口组装报文
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("service", UmpayUtil.service_query_order_req_uecp);
//		map.put("charset", UmpayUtil.charset_u8);
//		map.put("mer_id", "25019");
//		map.put("sign_type", UmpayUtil.sign_type);
//		map.put("version", UmpayUtil.version);
//		map.put("order_id", "umpay2016120116425500000678");//订单号
//		map.put("order_date", DateUtil.getDayForYYMMDD(new Date()));
		
		SingleResp resp = UmpayUtil.signAndVerifyForUmpay(map,"");
		System.out.println(resp.getCode());
		System.out.println(resp.getTranNo());
		System.out.println(resp.getMessage());
		
		
//		String html="sign=Csv%2F51u4uIGVq%2By4kJmxxG2ugz%2FKUBKdCmD%2FCPBHPzKeOzH3OU9SynNn5%2FP8rFnL5pWImYX%2BQEPGCeewW1Mm1QErtaenCaFLCp0UEgemHzkbDghS99LBZjXOW2l8nEViPi8fTFD1oDjlb%2BL1iKOsepHH3y4%2F4mAxWLnQmzFkC8I%3D&mer_id=25019&busi_type=03&signType=RSA&service=file_result_notify_uecp&ret_msg=%25E8%258E%25B7%25E5%258F%2596%25E6%2596%2587%25E4%25BB%25B6%25E5%25A4%25B1%25E8%25B4%25A5&charset=UTF-8&file_name=ZK_25019_20161205_017.txt&order_date=20161205&batch_no=017&version=1.0";
//		UmpayPayCutService u = new UmpayPayCutService();
//		try {
//			u.umpayFileNotify(html);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	}
	/**
	 * ftp 文件上传异常的通知
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public String umpayFileNotify(String data) throws IOException{
		data = URLDecoder.decode(data,"UTF-8");
		logger.info("【umpay file notify】decode："+data);
		String[] strarray=data.split("&"); 
		
		Map ht = new HashMap();
		for (int i = 0; i < strarray.length; i++){
			String key = strarray[i].substring(0,strarray[i].indexOf("="));
			String value = strarray[i].substring(strarray[i].indexOf("=")+1);
			if("ret_msg".equals(key)){
				value = URLDecoder.decode(value,"UTF-8");
			}
			logger.info("【umpay file notify】通知参数："+key+"="+value);
		    ht.put(key , value);
		}
		Map reqData = new HashMap();
		
		String sign_msg = "";
		SingleResp singleResp = new SingleResp();
	    try {
	    	logger.info("【umpay file notify】验签参数："+JsonUtil.getMapToJson(ht));
			reqData = Plat2Mer_v40.getPlatNotifyData(ht);
		} catch (VerifyException e) {
			e.printStackTrace();
			logger.info("【umpay file notify】验签失败");
	    	singleResp.setCode(DhbTranStatus.Fail.getCode());
	    	singleResp.setMessage("验签失败");
	    	return notifyResData("1111", "验签失败", reqData);
		}
	    logger.info("【umpay file notify】返回结果："+reqData);
	    
	    if(reqData == null){
	    	return notifyResData("1111", "验签异常，未收到返回报文", reqData);
	    }
	    String filename = (String) reqData.get("file_name");//文件名 
	    String batchno = (String) reqData.get("batch_no");//批次号
	    String orderdate = (String) reqData.get("order_date");//订单日期
	    String retcode = (String) reqData.get("ret_code");
	    String retmsg = (String) reqData.get("ret_msg");
	    logger.info("【umpay file notify】文件结果通知 filename="+filename+",batchno="+batchno+",orderdate="+orderdate+",retcode="+retcode+",retmsg="+retmsg);
	    
    	logger.info("【umpay file notify】"+retmsg);
    	singleResp.setCode(DhbTranStatus.Fail.getCode());
    	singleResp.setMessage(retmsg);
	    //查出该批次文件中未成功的订单
	    List<DhbBizJournal> journals = dhbBizJournalDao.getUmpayJournalByFilenameAndDate(orderdate, filename.trim());
	    for(DhbBizJournal journal : journals){
    		if(!journal.getHandleStatus().equals(singleResp.getCode())){
    			updateDhbBizJournalById(singleResp, journal);
    		}
	    }
	    return notifyResData("0000", "平台通知数据验签成功", reqData);
	}
	public String notifyResData(String code,String message,Map reqData){
		Map resData = new HashMap();
		resData.put("ret_code",code);
		resData.put("mer_id", reqData.get("mer_id"));
		resData.put("sign_type", reqData.get("signType"));
		resData.put("version", reqData.get("version"));
		try {
			resData.put("ret_msg", URLEncoder.encode(message,"UTF-8"));
			logger.info("【通知接口返回umpay的报文】"+JsonUtil.getMapToJson(resData));
			String data = com.umpay.api.paygate.v40.Mer2Plat_v40.merNotifyResData(resData);
			return data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 代扣结果异步通知
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String umpayPayNotify(String data) throws UnsupportedEncodingException{
		data = URLDecoder.decode(data,"UTF-8");
		String[] strarray = data.split("&"); 
		
		Map ht = new HashMap();
		for (int i = 0; i < strarray.length; i++){
			String key = strarray[i].substring(0,strarray[i].indexOf("="));
			String value = strarray[i].substring(strarray[i].indexOf("=")+1);
			if("ret_msg".equals(key)){
				value = URLDecoder.decode(value,"UTF-8");
			}
			logger.info("【umpay cut notify】通知参数："+key+"="+value);
		    ht.put(key , value);
		}
		Map reqData = new HashMap();
		
		String sign_msg = "";
		
		try{
			logger.info("【umpay cut notify】验签参数："+JsonUtil.getMapToJson(ht));
		    reqData = Plat2Mer_v40.getPlatNotifyData(ht);
		    logger.info("【umpay cut notify】返回结果："+reqData);
		    
		    if(reqData == null){
		    	return notifyResData("1111", "验签异常，未收到返回报文", reqData);
		    }
		    
		    String trade_no = (String) reqData.get("trade_no");//联动交易号
		    String order_id = (String) reqData.get("order_id");//订单号
		    String orderdate = (String) reqData.get("order_date");//订单日期
		    String amount = (String) reqData.get("amount");//扣款金额
		    String orig_amt = (String) reqData.get("orig_amt");//订单金额
		    String fee_amt = (String) reqData.get("fee_amt");//用户手续费
		    String state = (String) reqData.get("state");//交易状态
		    logger.info("【umpay cut notify】扣款结果通知 order_id="+order_id+",trade_no="+trade_no+",orderdate="+orderdate+",amount="+amount+",orig_amt="+orig_amt+",fee_amt="+fee_amt+",state="+state);
		    SingleResp singleResp = new SingleResp();
//		    1	交易创建，等待买家付款
//		    2	交易成功
//		    3	交易关闭  在指定时间段内未支付时关闭的交易；
//		    4	交易撤销
//		    5	交易失败
//		    6	交易结果不明
		    if("2".equals(state)){//
		    	singleResp.setCode(DhbTranStatus.Succ.getCode());
		    	singleResp.setMessage("交易成功");
		    }else if("3".equals(state) || "5".equals(state)){
		    	singleResp.setCode(DhbTranStatus.Fail.getCode());
		    	singleResp.setMessage("交易失败");
		    }else if("4".equals(state)){
		    	singleResp.setCode(DhbTranStatus.Unkown.getCode());
		    	singleResp.setMessage(DhbTranStatus.Unkown.getDescription());
		    }else{
		    	singleResp.setCode(DhbTranStatus.Handling.getCode());
		    	singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		    }
		    //查出该文件中未成功的订单
		    List<DhbBizJournal> journals = dhbBizJournalDao.getJournalByIdAndDate(orderdate, order_id);
		    if(journals.size()>=1){
		    	DhbBizJournal journa = journals.get(0);
//			    journa.setFee(Double.parseDouble(fee_amt));
			    if(!journa.getHandleStatus().equals(DhbTranStatus.Succ.getCode())){
			    	updateDhbBizJournalById(singleResp, journa);
			    }
		    }
		    return notifyResData("0000", "平台通知数据验签成功", reqData);
		}catch(Exception e){
		    System.out.println("验证签名发生异常" + e);
			return notifyResData("1111", "验证签名发生异常", reqData);
		}
	}
	/**
	 * 外放查询接口
	 */
	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		SingleResp singleResp = new SingleResp(); 
		singleResp.setCode(DhbTranStatus.Handling.getCode());
		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		DhbBizJournal journal = dhbBizJournalDao.getBizJournalByReqInfo(info);
		if(journal==null){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription()+",该订单在大网关不存在");
			return singleResp;
		}
		logger.info("【外放交易查询接口】tranNo="+info.getTranNo()+",code"+journal.getHandleStatus()+",message"+journal.getHandleRemark());
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
	}
	/**
	 * 定时查询程序
	 */
	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		String batchId = journal.getBatchId();
		if("1".equals(journal.getBizType())||"5".equals(journal.getBizType())){
			if(Strings.isNullOrEmpty(batchId)){
				querySingleJournalStatus(journal);
			}else{
				String merchId = journal.getMerchId();
				List<DhbBizJournal>  list=dhbBizJournalDao.getJournalByBatchId(merchId, batchId);
				for(DhbBizJournal jour:list){
					querySingleJournalStatus(jour);
				}
			}
		}else {
			if(Strings.isNullOrEmpty(batchId)){
				querySingleJournalStatusForPay(journal);
			}else{
				String merchId = journal.getMerchId();
				List<DhbBizJournal>  list=dhbBizJournalDao.getJournalByBatchId(merchId, batchId);
				for(DhbBizJournal jour:list){
					querySingleJournalStatusForPay(jour);
				}
			}
		}
		
		
		
	}
	/**
	 * 代付查询接口
	 * @param journal
	 */
	private void querySingleJournalStatusForPay(DhbBizJournal journal){
		SingleResp singleResp = new SingleResp(); 
		String bankorderId = journal.getId();
		logger.info("【umpay query】orderId="+bankorderId);
		//调用通道
		String mer_id= ResourceUtil.getString("umpay", "merId");
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_transfer_query);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", mer_id);
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("order_id", bankorderId);//订单号
		map.put("mer_date", DateUtil.getDayForYYMMDD(journal.getCreateTime()));
		
		SingleResp retRsp = UmpayUtil.signAndVerifyForUmpay(map,bankorderId);
		if(!"".equals(retRsp.getCode())){
			if("0000".equals(retRsp.getCode())){
////			1-支付中
//				3-失败
//				4-成功
//				11：待确认
//				12：已冻结,待财务审核
//				13:待解冻,交易失败
//				14：财务已审核，待财务付款
//				15:财务审核失败，交易失败
//				16：受理成功，交易处理中
//				17:交易失败退单中
//				18：交易失败退单成功
				singleResp.setMessage(retRsp.getMessage());
				if("4".equals(retRsp.getTranNo())){//明确成功
					singleResp.setCode(DhbTranStatus.Succ.getCode());
				}else if("1".equals(retRsp.getTranNo())||"11".equals(retRsp.getTranNo())||"12".equals(retRsp.getTranNo())
						||"14".equals(retRsp.getTranNo())||"16".equals(retRsp.getTranNo())){//明确失败
					singleResp.setCode(DhbTranStatus.Handling.getCode());
				}else{
					singleResp.setCode(DhbTranStatus.Fail.getCode());
				}
				updateDhbBizJournalById(singleResp, journal);
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage(retRsp.getMessage());
				updateDhbBizJournalById(singleResp, journal);
			}
			
		}
	}
	
	/**
	 * 代收查询接口
	 * @param journal
	 */
	private void querySingleJournalStatus(DhbBizJournal journal){
		SingleResp singleResp = new SingleResp(); 
		String bankorderId = journal.getId();
		logger.info("【umpay query】orderId="+bankorderId);
		//调用通道
		String mer_id= ResourceUtil.getString("umpay", "merId");
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_query_order_req_uecp);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", mer_id);
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("order_id", bankorderId);//订单号
		map.put("order_date", DateUtil.getDayForYYMMDD(journal.getCreateTime()));
		
		SingleResp retRsp = UmpayUtil.signAndVerifyForUmpay(map,bankorderId);
		if(!"".equals(retRsp.getCode())){
			if("0000".equals(retRsp.getCode())){
//				1	交易创建，等待买家付款
//				2	交易成功
//				3	交易关闭 在指定时间段内未支付时关闭的交易；
//				4	交易撤销
//				5	交易失败
//				6	交易结果不明
				singleResp.setMessage(retRsp.getMessage());
				if("2".equals(retRsp.getTranNo())){//明确成功
					singleResp.setCode(DhbTranStatus.Succ.getCode());
				}else if("3".equals(retRsp.getTranNo())||"5".equals(retRsp.getTranNo())){//明确失败
					singleResp.setCode(DhbTranStatus.Fail.getCode());
				}else{
					singleResp.setCode(DhbTranStatus.Handling.getCode());
				}
				updateDhbBizJournalById(singleResp, journal);
			}else if("00040019".equals(retRsp.getTranNo())){//没有查到您的订单
				
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				updateDhbBizJournalById(singleResp, journal);
			}
			
		}
	}
	/**
	 * 通过id修改状态
	 * @param singleResp
	 * @param journal
	 */
	private void updateDhbBizJournalById(SingleResp singleResp,DhbBizJournal journal){
		DhbBizJournal update = new DhbBizJournal();
		update.setId(journal.getId());
		update.setEndTime(new Date());
		update.setHandleRemark(singleResp.getMessage());
		update.setHandleStatus(singleResp.getCode());
    	dhbBizJournalDao.updateStatusById(update);
	}
	
	

	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		return null;
	}
	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		return null;
	}
	@Override
	public SingleResp singlePay(OutRequestInfo reqInfo) throws Exception {
		SingleResp singleResp = new SingleResp();
		Date now = new Date();
		String id = ResourceUtil.getString("umpay", "merId")
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("umpay_orderId_seq")+"",6, '0');
		//调用通道前先保存流水表信息
		//注：代付 frombankcode  是公司的账户
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Pay.getCode());
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		journal.setFromBankCardNo(bankInfo.getAcctNo());
		journal.setFromBankName(bankInfo.getBankName());
		journal.setFromUserName(bankInfo.getAcctName());
		journal.setFromBankCode(bankInfo.getBankCode());
		journal.setToBankCardNo(reqInfo.getAccNo());
		journal.setToBankCode(reqInfo.getBankCode());
		journal.setToBankName(reqInfo.getBankName());
		journal.setToUserName(reqInfo.getAccName());
		journal.setToIdentityNo(reqInfo.getCertNo());
		journal.setMoney(reqInfo.getBanlance());
		journal.setBatchId(reqInfo.getBatchId());
		journal.setCurrency("CNY");
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		
		//组装传递给通道方的报文
		Map<String,String> map = new HashMap<String,String>();
		map.put("service", UmpayUtil.service_transfer_direct_req);
		map.put("charset", UmpayUtil.charset_u8);
		map.put("mer_id", ResourceUtil.getString("umpay", "merId"));
		map.put("sign_type", UmpayUtil.sign_type);
		map.put("version", UmpayUtil.version);
		map.put("notify_url", ResourceUtil.getString("umpay", "notifyUrl"));//异步通知地址
		map.put("order_id", id);//订单号
		map.put("mer_date", DateUtil.getDayForYYMMDD());//Y 订单日期   YYYYMMDD
		map.put("amount", ANYZUtil.fromYuanToFen(String.format("%.2f", reqInfo.getBanlance())));//Y  金额  分
		map.put("recv_account_type", "00");//Y  收款方账户类型 00-----银行卡     02-----U付
		if(reqInfo.getAccType().equals("00")){//对私
			map.put("recv_bank_acc_pro","0");//Y  收款方账户属性  0对私  1对公
		}
		if(reqInfo.getAccType().equals("01")){
			map.put("recv_bank_acc_pro","1");//Y  收款方账户属性  0对私  1对公
		}
		map.put("recv_account", reqInfo.getAccNo());//Y 收款方账号 
		map.put("recv_user_name", reqInfo.getAccName());//Y 收款方户名
		map.put("identity_type", reqInfo.getCertType());//Y 收款方证件类型     身份证：01
		map.put("recv_gate_id", reqInfo.getBankCode());//银行缩写 CCB
		map.put("bank_brhname", reqInfo.getBankName());
		map.put("purpose", "代付"+id);
		//调用通道
		ReqData reqDataPost = Mer2Plat_v40.makeReqDataByPost(map);
		Map<String,String> paramMap = reqDataPost.getField();
		logger.info("【umpay单笔代付上送报文】"+reqInfo.getTranNo()+",上送报文："+paramMap);
		String url = reqDataPost.getUrl();
		String ret = ANYZUtil.sendMsg(url, paramMap);
		logger.info("(umpay单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道方返回报文："+ret);
		if(StringUtils.isNotEmpty(ret)){
			Map<String,String> retMap = Plat2Mer_v40.getResData(ret);
			String ret_code = retMap.get("ret_code");
			String ret_msg = retMap.get("ret_msg");
			String trade_no = retMap.get("trade_no");
			String trade_state = retMap.get("trade_state");
			String amount = retMap.get("amount");
			String transfer_settle_date = retMap.get("transfer_settle_date");
			logger.info("(umpay单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果：ret_code:"+ret_code+", trade_state:"+trade_state+",  ret_msg"+ret_msg);
			if("0000".equals(ret_code)){//受理成功 但不代表交易成功
				if("4".equals(trade_state)){//成功
					singleResp.setCode(DhbTranStatus.Succ.getCode());
	         		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				}else if("1".equals(trade_state)){//支付中
					singleResp.setCode(DhbTranStatus.Handling.getCode());
	         		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
				}else if("3".equals(trade_state)){//失败
					singleResp.setCode(DhbTranStatus.Fail.getCode());
	         		singleResp.setMessage(ret_msg);
				}
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
         		singleResp.setMessage(ret_msg);
			}
			//接收通道返回后，更新流水表的数据
			DhbBizJournal updatejournal = new DhbBizJournal();
			updatejournal.setId(journal.getId());
	        updatejournal.setEndTime(new Date());
	        updatejournal.setHandleRemark(singleResp.getMessage());
	        updatejournal.setHandleStatus(singleResp.getCode());
			dhbBizJournalDao.updateStatusById(updatejournal);
	        singleResp.setTranNo(reqInfo.getTranNo());
	        reqInfo.setProxyBizJournalId(id);
			return singleResp;
		}else{
			logger.error("[联动代付]未收到联动报文");
			singleResp.setCode(DhbTranStatus.Exception.getCode());
     		singleResp.setMessage(DhbTranStatus.Exception.getDescription());
     		return singleResp;
		}
	}
	@Override
	public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
		return null;
	}
	
	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}
	public ProxyBankInfoDao getProxyBankInfoDao() {
		return proxyBankInfoDao;
	}
	public void setProxyBankInfoDao(ProxyBankInfoDao proxyBankInfoDao) {
		this.proxyBankInfoDao = proxyBankInfoDao;
	}
	public DhbBizJournalDao getDhbBizJournalDao() {
		return dhbBizJournalDao;
	}
	public void setDhbBizJournalDao(DhbBizJournalDao dhbBizJournalDao) {
		this.dhbBizJournalDao = dhbBizJournalDao;
	}
	public DhbOutMerchantDao getDhbOutMerchantDao() {
		return dhbOutMerchantDao;
	}
	public void setDhbOutMerchantDao(DhbOutMerchantDao dhbOutMerchantDao) {
		this.dhbOutMerchantDao = dhbOutMerchantDao;
	}
	public void updateMerAccount(OutRequestInfo reqInfo,String id,String bizType,DhbOutMerchant merchant){
		logger.info("[代收付外放]记录商户流水ProxyBizJournal");//可在商户端和运营端的代收付流水记录中查到
		ProxyBizJournal journal = new ProxyBizJournal();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat tf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		ProxyCarryBankacct proxyCarryBankacct = dhbOutMerchantDao.selectBankByType(bizType);
		journal.setAmount(reqInfo.getBanlance());
		if(BizType.Pay.getCode().equals(bizType)){//代付交易，收款人是客户，付款方是平台账户
			journal.setAcpacctname(reqInfo.getAccName());
			journal.setAcpacctno(reqInfo.getAccNo());
			journal.setPayacctname(proxyCarryBankacct.getAcctName());
			journal.setPayacctno(proxyCarryBankacct.getAcctNo());
		}else if(BizType.Cut.getCode().equals(bizType)){//代收交易，收款人是平台账户，付款人是客户
			journal.setAcpacctname(proxyCarryBankacct.getAcctName());
			journal.setAcpacctno(proxyCarryBankacct.getAcctNo());
			journal.setPayacctname(reqInfo.getAccName());
			journal.setPayacctno(reqInfo.getAccNo());
		}
		
		journal.setBiz_type(bizType);
		journal.setMemo(id);//关联dhb_biz_journal的id
		journal.setMerch_id(reqInfo.getMerchId());
		journal.setOutid(reqInfo.getTranNo());//在代收付系统上可查到
		journal.setChannelId(reqInfo.getChannelId());
		journal.setPhone(null);
		journal.setRecord_id(reqInfo.getRecordId());
		journal.setTrans_date(df.format(now));
		journal.setTrans_time(tf.format(now));
		journal.setResp_date(df.format(now));
		journal.setResp_time(tf.format(now));
		journal.setStatus(DhbTranStatus.Succ.getCode());
		journal.setRemark("交易成功");
		journal.setChargemode("0");
		journal.setFee(merchant.getMerFee());
		dhbOutMerchantDao.saveJounal(journal);
		
		logger.info("[大网关外放]修改商户账户");
		ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
		//取上次余额
		Double last_amount = proxyMerchAmt.getBalance();
		//取上次可用余额
		Double last_valid_amount= proxyMerchAmt.getValidBalance();
		
		
		/*Double currentBalance = ArithUtil.sub(last_amount, journal.getAmount());
		//可用余额变动
		Double currentValidBalance = ArithUtil.sub(last_valid_amount, journal.getAmount());
		//减手续费
		//2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		currentBalance = ArithUtil.sub(currentBalance, merchant.getMerFee());
//		currentBalance = ArithUtil.sub(currentBalance, 0);
		//end of 2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		//可用余额减手续费
		//2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		currentValidBalance = ArithUtil.sub(currentValidBalance, merchant.getMerFee());
//		currentValidBalance = ArithUtil.sub(currentValidBalance, 0);
		//end of 2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费*/
		logger.info("[代收付外放]流水号："+reqInfo.getTranNo()+",商户账户余额扣:"+journal.getAmount());
		//余额
		Double currentBalance = ArithUtil.sub(last_amount, merchant.getMerFee());
		//可用余额变动
		Double currentValidBalance = ArithUtil.sub(last_valid_amount, merchant.getMerFee());
		//减手续费
		if(BizType.Pay.getCode().equals(bizType)){//代付交易
			currentBalance = ArithUtil.sub(currentBalance, journal.getAmount());
			currentValidBalance = ArithUtil.sub(currentValidBalance, journal.getAmount());
		}else if(BizType.Cut.getCode().equals(bizType)){//代收
			currentBalance = ArithUtil.add(currentBalance, journal.getAmount());
			currentValidBalance = ArithUtil.add(currentValidBalance, journal.getAmount());
		}
		//3.1变更一下商户虚拟账户余额
		proxyMerchAmt.setBalance(currentBalance);
		//3.2变更可用余额
		proxyMerchAmt.setValidBalance(currentValidBalance);
		dhbOutMerchantDao.updateMerAmt(proxyMerchAmt);
	}
	
	
	public String umpayPayNotifyForPay(String data) throws UnsupportedEncodingException{
		data = URLDecoder.decode(data,"UTF-8");
		String[] strarray = data.split("&"); 
		
		Map ht = new HashMap();
		for (int i = 0; i < strarray.length; i++){
			String key = strarray[i].substring(0,strarray[i].indexOf("="));
			String value = strarray[i].substring(strarray[i].indexOf("=")+1);
			if("ret_msg".equals(key)){
				value = URLDecoder.decode(value,"UTF-8");
			}
			logger.info("【umpay pay notify】通知参数："+key+"="+value);
		    ht.put(key , value);
		}
		Map reqData = new HashMap();
		
		String sign_msg = "";
		
		try{
			logger.info("【umpay pay notify】验签参数："+JsonUtil.getMapToJson(ht));
		    reqData = Plat2Mer_v40.getPlatNotifyData(ht);
		    logger.info("【umpay pay notify】返回结果："+reqData);
		    
		    if(reqData == null){
		    	return notifyResDataForPay("1111", "验签异常，未收到返回报文", reqData);
		    }
		    
		    String ret_code = (String) reqData.get("ret_code");
		    String ret_msg = (String) reqData.get("ret_msg");//订单号
		    String trade_no = (String) reqData.get("trade_no");//联动交易号
		    String trade_state = (String) reqData.get("trade_state");//交易状态    3-失败    4-成功
		    String order_id = (String) reqData.get("order_id");//订单号
		    String mer_date = (String) reqData.get("mer_date");//订单日期
		    String amount = (String) reqData.get("amount");//扣款金额
		    String transfer_settle_date = (String) reqData.get("transfer_settle_date");//付款对账日期
		    String transfer_date = (String) reqData.get("transfer_date");//付款日期
		    logger.info("【umpay pay notify】扣款结果通知ret_code="+ret_code+",ret_msg="+ret_msg+",trade_state="+trade_state+",order_id="+order_id+",trade_no="+trade_no+",mer_date="+mer_date+",amount="+amount);
		    SingleResp singleResp = new SingleResp();
		    if("0000".equals(ret_code)){
		    	 if("4".equals(trade_state)){//
		    		 singleResp.setCode(DhbTranStatus.Succ.getCode());
		    		 singleResp.setMessage("交易成功");
		    	 }else if("3".equals(trade_state)){
		    		 singleResp.setCode(DhbTranStatus.Fail.getCode());
		    		 singleResp.setMessage("交易失败");
		    	 }
		    }else{
		    	singleResp.setCode(DhbTranStatus.Fail.getCode());
	    		singleResp.setMessage("交易失败");
		    }
		   
		    //查出该文件中未成功的订单
		    List<DhbBizJournal> journals = dhbBizJournalDao.getJournalByIdAndDate(mer_date, order_id);
		    if(journals.size()>1){
		    	
		    }
		    DhbBizJournal journa = journals.get(0);
		    updateDhbBizJournalById(singleResp, journa);
		    return notifyResDataForPay("0000", "平台通知数据验签成功", reqData);
		}catch(Exception e){
		    System.out.println("验证签名发生异常" + e);
			return notifyResDataForPay("1111", "验证签名发生异常", reqData);
		}
	}
	public String notifyResDataForPay(String code,String message,Map reqData){
		Map resData = new HashMap();
		resData.put("mer_id", reqData.get("mer_id"));
		resData.put("sign_type", reqData.get("sign_type"));
		resData.put("version", reqData.get("version"));
		resData.put("order_id",reqData.get("order_id"));
		resData.put("mer_date",reqData.get("mer_date"));
		resData.put("ret_code",code);
		try {
			resData.put("ret_msg", URLEncoder.encode(message,"UTF-8"));
			logger.info("【通知接口返回umpay的报文】"+JsonUtil.getMapToJson(resData));
			String data = com.umpay.api.paygate.v40.Mer2Plat_v40.merNotifyResData(resData);
			return data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
