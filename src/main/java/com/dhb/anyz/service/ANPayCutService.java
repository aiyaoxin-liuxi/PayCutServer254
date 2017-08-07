package com.dhb.anyz.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.anyz.entity.Constants;
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
import com.dhb.util.ArithUtil;
import com.dhb.util.JsonUtil;
import com.google.common.base.Strings;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.SignUtil;
import com.jnewsdk.util.StringUtils;

public class ANPayCutService implements PayCutInterface{
	public static Logger logger = Logger.getLogger(ANPayCutService.class);
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
	@Override
	public SingleResp singleCut(OutRequestInfo reqInfo) throws Exception {
		// TODO Auto-generated method stub
		SingleResp singleResp = new SingleResp();
//		Map<String,String> msg_map = null;
		Map<String,String> anyz_map_ret = null;
		Date now = new Date();
		String id = ResourceUtil.getString("anyz", "merId")
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
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
		
		//组装传递给通道方的报文
		Map<String,String> map = new HashMap<String, String>();
		map.put("signMethod",Constants.anyz_signMethod);//签名方法
		map.put("version",Constants.anyz_version);//消息版本号
		map.put("txnType",Constants.anyz_txnType_11);//交易类型--代收
		map.put("txnSubType",Constants.anyz_txnSubType);//交易子类型--消费
		map.put("bizType",Constants.anyz_bizType_cut);//产品类型--代收
		map.put("accessType",Constants.anyz_accessType_0);//接入类型--商户直接接入
		map.put("accessMode",Constants.anyz_accessMode);//接入方式--web
		map.put("merId",ResourceUtil.getString("anyz", "merId"));//	商户号
		map.put("merOrderId",journal.getId());//商户订单号
		map.put("accType",Constants.anyz_accType_01);//账户类型   01借记卡，02贷记卡，03存折，04公司账号
		map.put("accNo",reqInfo.getAccNo());//账号
		
		Map<String,Object> customerInfo = new HashMap<String, Object>();//银行卡验证信息及身份信息 
		customerInfo.put("issInsProvince",reqInfo.getBankProvince());//开户行省
		customerInfo.put("issInsCity",reqInfo.getBankCity());//开户行市
		customerInfo.put("iss_ins_name",reqInfo.getBankName());//开户支行名称
		customerInfo.put("certifTp",reqInfo.getCertType());//证件类型必填   01：身份证 02：军官证 03：护照 04：回乡证 05：台胞证 06：警官证 07：士兵证 99：其它证件
		
		customerInfo.put("certify_id",reqInfo.getCertNo());//证件号码必填
		customerInfo.put("customerNm",reqInfo.getAccName());//姓名必填
		customerInfo.put("phoneNo",reqInfo.getMobile());//手机号必填
		
		customerInfo.put("expired","");//有效期
		customerInfo.put("cvv2 ","");//CVV2
		customerInfo.put("smsCode","");//短信验证码
		String json = JsonUtil.getMapToJson(customerInfo);
		map.put("customerInfo",json);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		map.put("txnTime",sdf.format(new Date()));//订单发送时间
		map.put("txnAmt",ANYZUtil.fromYuanToFen(String.format("%.2f", reqInfo.getBanlance())));//交易金额
		map.put("currency",Constants.anyz_currency_CNY);//交易币种
		map.put("backUrl","");//后台通知地址    非必填
		map.put("payType",Constants.anyz_payType);//支付方式 0501：代扣
		map.put("bankId",reqInfo.getBankCode());//银行编号    民生
		map.put("subject","");//商品标题  非必填
		map.put("body","");//商品描述  非必填
		if(reqInfo.getAccType().equals("00")){//对私
			map.put("ppFlag",Constants.anyz_ppFlag_01);//00对公01对私标志 
		}
		if(reqInfo.getAccType().equals("01")){
			map.put("ppFlag",Constants.anyz_ppFlag_00);//00对公01对私标志 
		}
		map.put("purpose","");//用途   非必填
		map.put("merResv1","");//请求保留域  非必填
		map.put("signature","");//签名信息
		
		// 设置签名
		String key = ResourceUtil.getString("anyz", "key");
		ANYZUtil.setSignature(map,key);
		String plain = SignUtil.getURLParam(map, false, null);
		logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",组装通道方报文（明文）："+plain);
		// 特殊字段数据转换
		ANYZUtil.converData(map);
		String reqMsg = SignUtil.getURLParam(map, false, null);
		
		//调用通道
		String url = ResourceUtil.getString("anyz", "url");
		String msg = ANYZUtil.sendMsg(url, map);
		if (StringUtils.isEmpty(msg)) {
			logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",报文发送失败或应答消息为空");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		} else {
			//解析通道返回报文
//			String[] msg_ret = msg.split("&");
//			msg_map = new HashMap<String,String>();
//			for(int i = 0 ; i < msg_ret.length ; i++){
//				String[] msg_ret_two = msg_ret[i].split("=");
//				msg_map.put(msg_ret_two[0], msg_ret_two[1]);
//			}
			logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果："+msg);
			logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",请求报文(个别字段base64)："+reqMsg);
			anyz_map_ret = ANYZUtil.parseMsg(msg);
			logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",base64解码后："+StringUtils.toString(anyz_map_ret));
			
			if(ANYZUtil.verifySign(anyz_map_ret,key) == true){//验签成功
				logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",签名验证结果：true");
				if(anyz_map_ret.get("respCode").equals("1001")){//交易成功
					singleResp.setCode(DhbTranStatus.Succ.getCode());
	         		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				}else if(anyz_map_ret.get("respCode").equals("1111")){//初始状态
					singleResp.setCode(DhbTranStatus.Handling.getCode());
	         		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
				}else{//交易不成功（可能存在多种情况）
					singleResp.setCode(DhbTranStatus.Fail.getCode());
		     		singleResp.setMessage(anyz_map_ret.get("respMsg"));
				}
			}else{//验签失败
				logger.info("(ANYZ单笔代收接口：)订单号："+reqInfo.getTranNo()+",签名验证结果：false");
				singleResp.setCode(DhbTranStatus.Fail.getCode());
	     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
			}
		}
		//接收通道返回后，更新流水表的数据
		DhbBizJournal updatejournal = new DhbBizJournal();
		updatejournal.setId(journal.getId());
        updatejournal.setEndTime(new Date());
        updatejournal.setHandleRemark(singleResp.getMessage());
        updatejournal.setHandleStatus(singleResp.getCode());
        dhbBizJournalDao.updateStatusById(updatejournal);
        //*****start  大网关外放   修改商户账户(余额和可用余额均 加上交易金额 减去手续费])****************//
        //暂时只对代付交易判断金额，
//        if((DhbTranStatus.Succ.getCode()).equals(singleResp.getCode())||(DhbTranStatus.Handling.getCode()).equals(singleResp.getCode())){
//       	 	DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
//    		if(merchant != null){
//    			updateMerAccount(reqInfo,id,BizType.Cut.getCode(),merchant);
//    		}
//        }
        //*****end*********************************************************//
        singleResp.setTranNo(reqInfo.getTranNo());
		return singleResp;
	}

	@Override
	public SingleResp singlePay(OutRequestInfo reqInfo) throws Exception {
		SingleResp singleResp = new SingleResp();
		Map<String,String> anyz_map_ret = null;
		Date now = new Date();
		String id = "";
		if("2".equals(reqInfo.getBusinessType())){
			id = ResourceUtil.getString("anyz", "merId_2")
					+ df.format(now)
					+ tf.format(now)
					+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
		}else{
			id = ResourceUtil.getString("anyz", "merId")
					+ df.format(now)
					+ tf.format(now)
					+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
		}
		
		//调用通道前先保存流水表信息
		//注：代付 frombankcode  是公司的账户
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Pay.getCode());
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
//		DhbBankInfo bankInfo = null;
//		if(Constants.business_type_2.equals(reqInfo.getBusinessType())){
//			bankInfo = new DhbBankInfo();
//			bankInfo.setAcctNo("");
//			bankInfo.setAcctName("");
//			bankInfo.setBankName("");
//			bankInfo.setBankCode("");
//			bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
//		}else{
//			bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
//		}
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
		Map<String,String> map = new HashMap<String, String>();
		map.put("signMethod",Constants.anyz_signMethod);//签名方法
		map.put("version",Constants.anyz_version);//消息版本号
		map.put("txnType",Constants.anyz_txnType_12);//交易类型--代付
		map.put("txnSubType",Constants.anyz_txnSubType);//交易子类型--消费
		map.put("bizType",Constants.anyz_bizType_pay);//产品类型--代付
		map.put("accessType",Constants.anyz_accessType_0);//接入类型--商户直接接入
		map.put("accessMode",Constants.anyz_accessMode);//接入方式--web
		if("2".equals(reqInfo.getBusinessType())){
			map.put("merId",ResourceUtil.getString("anyz", "merId_2"));//	商户号
		}else{
			map.put("merId",ResourceUtil.getString("anyz", "merId"));//	商户号
		}
		map.put("merOrderId",journal.getId());//商户订单号
		map.put("accNo",reqInfo.getAccNo());//账号
		
		Map<String,Object> customerInfo = new HashMap<String, Object>();//银行卡验证信息及身份信息 
		customerInfo.put("issInsProvince",reqInfo.getBankProvince());//开户行省
		customerInfo.put("issInsCity",reqInfo.getBankCity());//开户行市
		customerInfo.put("iss_ins_name",reqInfo.getBankName());//开户支行名称
		customerInfo.put("certifTp",reqInfo.getCertType());//证件类型必填   01：身份证 02：军官证 03：护照 04：回乡证 05：台胞证 06：警官证 07：士兵证 99：其它证件
		
		customerInfo.put("certify_id",reqInfo.getCertNo());//证件号码必填
		customerInfo.put("customerNm",reqInfo.getAccName());//姓名必填
		customerInfo.put("phoneNo",reqInfo.getMobile());//手机号必填
		
		customerInfo.put("expired","");//有效期
		customerInfo.put("cvv2 ","");//CVV2
		customerInfo.put("smsCode","");//短信验证码
		String json = JsonUtil.getMapToJson(customerInfo);
		map.put("customerInfo",json);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		map.put("txnTime",sdf.format(new Date()));//订单发送时间
		map.put("txnAmt",ANYZUtil.fromYuanToFen(String.format("%.2f", reqInfo.getBanlance())));//交易金额
		map.put("currency",Constants.anyz_currency_CNY);//交易币种
		map.put("backUrl","");//后台通知地址    非必填
		map.put("payType",Constants.anyz_payType_pay);//支付方式 0401：代付
		map.put("bankId",reqInfo.getBankCode());//银行编号    民生
		map.put("subject","");//商品标题  非必填
		map.put("body","");//商品描述  非必填
		if(reqInfo.getAccType().equals("00")){//对私
			map.put("ppFlag",Constants.anyz_ppFlag_01);//00对公01对私标志 
		}
		if(reqInfo.getAccType().equals("01")){
			map.put("ppFlag",Constants.anyz_ppFlag_00);//00对公01对私标志 
		}
		map.put("purpose","");//用途   非必填
		map.put("merResv1","");//请求保留域  非必填
		map.put("signature","");//签名信息
		// 设置签名
		String key = "";
		if("2".equals(reqInfo.getBusinessType())){
			key = ResourceUtil.getString("anyz", "key_2");
		}else{
			key = ResourceUtil.getString("anyz", "key");
		}
		ANYZUtil.setSignature(map,key);
		String plain = SignUtil.getURLParam(map, false, null);
		logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",组装通道方报文（明文）："+plain);
		// 特殊字段数据转换
		ANYZUtil.converData(map);
		String reqMsg = SignUtil.getURLParam(map, false, null);
		//调用通道
		String url = ResourceUtil.getString("anyz", "url");
		String msg = ANYZUtil.sendMsg(url, map);
		if (StringUtils.isEmpty(msg)) {
			logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",报文发送失败或应答消息为空");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		} else {
			logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果："+msg);
			logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",请求报文(个别字段base64)："+reqMsg);
			anyz_map_ret = ANYZUtil.parseMsg(msg);
			logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",base64解码后："+StringUtils.toString(anyz_map_ret));
			
			if(ANYZUtil.verifySign(anyz_map_ret,key) == true){//验签成功
				logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",签名验证结果：true");
				if(anyz_map_ret.get("respCode").equals("1001")){//交易成功
					singleResp.setCode(DhbTranStatus.Succ.getCode());
	         		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
	         		
	         		
				}else if(anyz_map_ret.get("respCode").equals("1111")){//初始状态
					singleResp.setCode(DhbTranStatus.Handling.getCode());
	         		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
				}else{//交易不成功（可能存在多种情况）
					singleResp.setCode(DhbTranStatus.Fail.getCode());
		     		singleResp.setMessage(anyz_map_ret.get("respMsg"));
				}
			}else{//验签失败
				logger.info("(ANYZ单笔代付接口：)订单号："+reqInfo.getTranNo()+",签名验证结果：false");
				singleResp.setCode(DhbTranStatus.Fail.getCode());
	     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
			}
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
	}

	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
		SingleResp singleResp = new SingleResp(); 
		singleResp.setCode(DhbTranStatus.Handling.getCode());
		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		DhbBizJournal journal=dhbBizJournalDao.getBizJournalByReqInfo(info);
		if(journal==null){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
			return singleResp;
		}
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
	}

	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		// TODO Auto-generated method stub
		String batchId = journal.getBatchId();
		if(Strings.isNullOrEmpty(batchId)){
			querySingleJournalStatus(journal);
		}else{
			String merchId = journal.getMerchId();
			List<DhbBizJournal>  list=dhbBizJournalDao.getJournalByBatchId(merchId, batchId);
			for(DhbBizJournal jour:list){
				querySingleJournalStatus(jour);
			}
		}
	}
	
	private void querySingleJournalStatus(DhbBizJournal journal){
		Map<String,String> anyz_map_ret = null;
		SingleResp singleResp = new SingleResp(); 
		String tranNo = journal.getId();
		//调用通道
		Map<String,String> map = new HashMap<String, String>();
		map.put("signMethod",Constants.anyz_signMethod);
		map.put("signature","");
		map.put("version",Constants.anyz_version);
		map.put("txnType",Constants.anyz_txnType_00);
		map.put("txnSubType",Constants.anyz_txnSubType);
		map.put("merId",ResourceUtil.getString("anyz", "merId"));
		map.put("merOrderId",tranNo);
		
		// 设置签名
		String key = ResourceUtil.getString("anyz", "key");
		ANYZUtil.setSignature(map,key);
		String plain = SignUtil.getURLParam(map, false, null);
		logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",组装通道方报文（明文）："+plain);
		// 特殊字段数据转换
		ANYZUtil.converData(map);
		String reqMsg = SignUtil.getURLParam(map, false, null);
		logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",请求报文(个别字段base64)："+reqMsg);
		String url = ResourceUtil.getString("anyz", "url");
		String msg = ANYZUtil.sendMsg(url, map);
		anyz_map_ret = ANYZUtil.parseMsg(msg);
		logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",通道方返回结果："+msg);
		if (StringUtils.isEmpty(msg)) {
			logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",报文发送失败或应答消息为空");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}else{
			if(anyz_map_ret.get("respCode").equals("9999")){//系统繁忙,不进行验签判断

			}else{
				if(ANYZUtil.verifySign(anyz_map_ret,key) == true){//验签成功
					logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",签名验证结果：true");
					logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",base64解码后："+StringUtils.toString(anyz_map_ret));
					if(anyz_map_ret.get("respCode").equals("1001")){
						singleResp.setCode(DhbTranStatus.Succ.getCode());
						singleResp.setMessage(DhbTranStatus.Succ.getDescription());
					}else if(anyz_map_ret.get("respCode").equals("1111")){//初始状态
						singleResp.setCode(DhbTranStatus.Handling.getCode());
						singleResp.setMessage(DhbTranStatus.Handling.getDescription());
					}else{
						singleResp.setCode(DhbTranStatus.Fail.getCode());
						singleResp.setMessage(anyz_map_ret.get("respMsg"));
					}
					if(!singleResp.getCode().equals(journal.getHandleStatus())){
			        	journal.setEndTime(new Date());
			        	journal.setHandleRemark(singleResp.getMessage());
			        	journal.setHandleStatus(singleResp.getCode());
			        	dhbBizJournalDao.updateStatusById(journal);
			        }
				}else{//验签失败
					logger.info("(ANYZ单笔代收付查询接口：)订单号："+tranNo+",签名验证结果：false");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
		     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		     		journal.setEndTime(new Date());
		        	journal.setHandleRemark(singleResp.getMessage());
		        	journal.setHandleStatus(singleResp.getCode());
		        	dhbBizJournalDao.updateStatusById(journal);
				}
			}
		}
	}
	public void updateMerAccount(OutRequestInfo reqInfo,String id,String bizType,DhbOutMerchant merchant){
		logger.info("[代收付外放]记录商户流水ProxyBizJournal");
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
		journal.setMemo(id);
		journal.setMerch_id(reqInfo.getMerchId());
		journal.setOutid(reqInfo.getTranNo());
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
}
