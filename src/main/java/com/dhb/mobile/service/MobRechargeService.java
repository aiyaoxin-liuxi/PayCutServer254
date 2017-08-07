package com.dhb.mobile.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbMobileOrderDao;
import com.dhb.dao.service.DhbMobileTransactionDao;
import com.dhb.entity.DhbMobileOrder;
import com.dhb.entity.DhbMobileTransaction;
import com.dhb.mobile.entity.InAccount;
import com.dhb.mobile.entity.InMsg;
import com.dhb.mobile.entity.InPurchase;
import com.dhb.mobile.entity.InQuan;
import com.dhb.mobile.entity.InQuanbody;
import com.dhb.mobile.entity.MobileResp;
import com.dhb.mobile.entity.MobileRspInfo;
import com.dhb.mobile.entity.OutAccount;
import com.dhb.mobile.entity.OutMsg;
import com.dhb.mobile.entity.OutPurchase;
import com.dhb.mobile.entity.OutQuan;
import com.dhb.mobile.entity.OutQuanbody;
import com.dhb.mobile.entity.OutResp;
import com.dhb.mobile.util.Constant;
import com.dhb.mobile.util.HttpHelper;
import com.dhb.mobile.util.MD5Util;
import com.dhb.mobile.util.SocketClient;
import com.dhb.util.AmountUtil;
import com.dhb.util.DateUtil;
import com.dhb.util.PropFileUtil;
import com.dhb.util.StringUtil;
import com.dhb.util.Tools;
import com.dhb.util.XmlUtil;
@Service
public class MobRechargeService {
	private Logger logger = Logger.getLogger(MobRechargeService.class);
	private String url = PropFileUtil.getByFileAndKey("mobile.properties", "url");
	private String ret_url = PropFileUtil.getByFileAndKey("mobile.properties", "returl");
	
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private DhbMobileOrderDao dhbMobileOrderDao;
	@Autowired
	private DhbMobileTransactionDao dhbMobileTransactionDao;
	
	
	public MobileResp mobileRecharge(String json, InQuan quan,DhbMobileOrder moborder,String cxjson){
		//1.记录流水
		//2.请求钱包后台发起扣款指令
		//3.扣款成功之后到运营商充值
		//4.根据运营商的结果做处理
		
		try {
			MobileResp mobileResp= sendToQBForCut(json, moborder);
			if(!Constant.QB_STATE_SUC.toString().equals(mobileResp.getCode())){
				logger.error("sjcz 钱包手机充值接口扣款失败");
				return mobileResp;
			}
			logger.info("sjcz 钱包手机充值接口 扣款成功");
			//记录一笔交易
			DhbMobileTransaction transaction = createTransaction(moborder,quan.getQuanbody().getPurchase());
			logger.info("sjcz 交易记录插入成功");
			//发送到欧飞做手机充值
			MobileRspInfo rspinfo = sendToOFMobPay(transaction);
			
			MobileResp rsp = doOperForOFBack(transaction, rspinfo,quan,cxjson);
//			String resxml=XmlUtil.ObjectToXml(outQuan).replace(" standalone=\"yes\"", "");
//			String enCodeXml = new String(Base64.encodeBase64(resxml.getBytes())).trim();
//			int len=enCodeXml.length()+71;
//			String baowen = addZeroForNum(len+"", 5)+addZeroForNum("", 71)+enCodeXml;
//			logger.info("返回给前置的报文："+baowen);
			return rsp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 手机充值发送到钱包后台做扣款
	 * @param json
	 * @param moborder
	 * @return
	 */
	public MobileResp sendToQBForCut(String json,DhbMobileOrder moborder){
		MobileResp mobileResp = new MobileResp();
		mobileResp.setOrderNum(moborder.getOrderNum());
		
		try {
			String qbjson = sendToPayBack(json);
			if(StringUtil.isEmpty(qbjson)){
				logger.error("sjcz 【钱包手机充值接口】返回为空");
				mobileResp.setCode(Constant.QB_STATE_FAIL.toString());
				mobileResp.setMessage("钱包手机充值接口返回为空");
				return mobileResp;
			}
			String mwxml = qbjson.substring(76);
			if(StringUtil.isEmpty(mwxml)){
				logger.error("sjcz 【钱包手机充值接口】返回为空");
				mobileResp.setCode(Constant.QB_STATE_FAIL.toString());
				mobileResp.setMessage("钱包手机充值接口返回为空");
				return mobileResp;
			}
			String resData = new String(Base64.decodeBase64(mwxml.getBytes()));
			
//			logger.info("sjcz 【钱包手机充值接口】返回报文解析成XML："+resData);
			if(StringUtil.isEmpty(resData)){
				logger.error("SJCZ 钱包手机充值接口返回为空");
				moborder.setState(Constant.QB_STATE_FAIL);//钱包后台扣钱失败
				moborder.setMemo("钱包后台返回空");
				dhbMobileOrderDao.updateStatusById(moborder);
				
				mobileResp.setCode(Constant.QB_STATE_FAIL.toString());
				mobileResp.setMessage("钱包手机充值接口返回为空");
				return mobileResp;
			}
			OutQuan outQuan = (OutQuan) XmlUtil.xmltoObject(resData,OutQuan.class);
			OutResp outresp = outQuan.getQuanbody().getResp();
			
			mobileResp.setTicket(outQuan.getQuanbody().getTicket());
			logger.info("SJCZ 【钱包手机充值接口】返回码："+outresp.getRespCode()+","+outresp.getRespInfo());
			if("000".equals(outresp.getRespCode())){
				moborder.setState(Constant.QB_STATE_SUC);//钱包后台扣钱成功
				moborder.setMemo("钱包扣款成功");
				dhbMobileOrderDao.updateStatusById(moborder);
				
				mobileResp.setCode(Constant.QB_STATE_SUC.toString());
				mobileResp.setMessage("钱包扣款成功");
				return mobileResp;
			}else{
				logger.error("sjcz 【钱包手机充值接口】扣款失败");
				moborder.setState(Constant.QB_STATE_FAIL);//钱包后台扣钱失败
				moborder.setMemo(outresp.getRespInfo());
				dhbMobileOrderDao.updateStatusById(moborder);
				
				mobileResp.setCode(outresp.getRespCode());
				mobileResp.setMessage(outresp.getRespInfo());
				return mobileResp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mobileResp.setCode(Constant.QB_STATE_FAIL.toString());
			mobileResp.setMessage("钱包手机充值接口出现异常");
			return mobileResp;
		}
	}
	private String sendToPayBack(String data){
		String ip = PropFileUtil.getByFileAndKey("mobile.properties", "ip");
		String port = PropFileUtil.getByFileAndKey("mobile.properties", "port");
		try {
			return SocketClient.sendServer(ip, Integer.parseInt(port), data);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public MobileResp doOperForOFBack(DhbMobileTransaction transaction,MobileRspInfo rspinfo,InQuan cutQuan,String cxjson){
		try {
			MobileResp mobileResp = new MobileResp();
			if(Constant.QB_STATE_SUC == transaction.getState()){
				mobileResp.setCode(Constant.QB_STATE_SUC.toString());
				mobileResp.setMessage("充值成功");
				return mobileResp;
			}
			DhbMobileTransaction update = new DhbMobileTransaction();
			update.setId(transaction.getId());
			update.setMoborderId(transaction.getMoborderId());
			update.setRetCode(rspinfo.getRetcode());
			update.setRetTime(new Date());
			update.setRetMsg(rspinfo.getErrmsg());
			
			String retcode = rspinfo.getRetcode();
			if("1".equals(rspinfo.getRetcode())){
				update.setOfTraceNum(rspinfo.getOrderid());
				update.setOfOrderCache(rspinfo.getOrdercash());
				update.setOfCardName(rspinfo.getCardname());
				update.setGameState(rspinfo.getGamestate());
				
				if("9".equals(rspinfo.getGamestate())){
					update.setState(Constant.QB_STATE_FAIL);
					
					//TODO:请求钱包后台的撤销接口
					return sendToQbForRovoke(cutQuan, update,cxjson);
					//TODO
				}else{
					update.setState(Constant.QB_STATE_SUC);
					dhbMobileTransactionDao.updateStatusById(update);
					
					mobileResp.setCode(Constant.QB_STATE_SUC.toString());
					mobileResp.setMessage(rspinfo.getErrmsg());
					return mobileResp;
				}
			}else{
				if("105".equals(retcode)||"334".equals(retcode)|| "1043".equals(retcode) || "9999".equals(retcode)){
					//发起查询接口   1充值成功，0充值中，9充值失败，-1找不到此订单
					String res = sendToOFMobQuery(transaction.getSpOrderId());
					if("9".equals(res)){
						update.setState(Constant.QB_STATE_FAIL);
						//TODO:请求钱包后台的撤销接口
						return sendToQbForRovoke(cutQuan, update,cxjson);
						//TODO
					}else if("0".equals(res)){
						update.setState(Constant.OF_STATE_IN);
						dhbMobileTransactionDao.updateStatusById(update);
						
						mobileResp.setCode(Constant.OF_STATE_IN.toString());
						mobileResp.setMessage("手机充值中");
						return mobileResp;
					}else if("1".equals(res)){
						update.setState(Constant.QB_STATE_SUC);
						dhbMobileTransactionDao.updateStatusById(update);
						
						mobileResp.setCode(Constant.QB_STATE_SUC.toString());
						mobileResp.setMessage("充值成功");
						return mobileResp;
					}
				}else{
					update.setState(Constant.QB_STATE_FAIL);
					
					//TODO:请求钱包后台的撤销接口
					return sendToQbForRovoke(cutQuan, update,cxjson);
					//TODO
					
				}
			}
			return mobileResp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public MobileResp sendToQbForRovoke(InQuan cutQuan,DhbMobileTransaction transaction,String cxjson){
		try {
			MobileResp mobileResp = new MobileResp();
			String qbjson = sendToPayBack(cxjson);
			if(StringUtil.isEmpty(qbjson)){
				dhbMobileTransactionDao.updateStatusById(transaction);
				mobileResp.setCode(Constant.QB_STATE_CANCLE_FAIL.toString());
				mobileResp.setMessage(transaction.getRetMsg());
				return mobileResp;
			}
			String mwxml = qbjson.substring(76);
			String resData = new String(Base64.decodeBase64(mwxml.getBytes()));
			logger.info("sjcz 【钱包撤销接口】后台返回的XML："+resData);
			if(StringUtil.isEmpty(resData)){
				logger.error("SJCZ 【钱包撤销接口】响应结果为空");
				dhbMobileTransactionDao.updateStatusById(transaction);
				mobileResp.setCode(Constant.QB_STATE_CANCLE_FAIL.toString());
				mobileResp.setMessage(transaction.getRetMsg());
				return mobileResp;
			}
			OutQuan outQuan = (OutQuan) XmlUtil.xmltoObject(resData,OutQuan.class);
			OutResp outresp = outQuan.getQuanbody().getResp();
			mobileResp.setTicket(outQuan.getQuanbody().getTicket());
			logger.info("SJCZ 【钱包撤销接口】返回码："+outresp.getRespCode()+","+outresp.getRespInfo());
			if(!"000".equals(outresp.getRespCode())){
				dhbMobileTransactionDao.updateStatusById(transaction);
				mobileResp.setCode(Constant.QB_STATE_CANCLE_FAIL.toString());
				mobileResp.setMessage(transaction.getRetMsg());
				return mobileResp;
			}
			transaction.setState(Constant.QB_STATE_FAIL);//手机充值失败
			DhbMobileOrder moborder = dhbMobileOrderDao.selectByOrderId(transaction.getMoborderId());
			moborder.setId(transaction.getMoborderId());
			moborder.setState(Constant.QB_STATE_CANCLE_SUC);
			moborder.setMemo("手机充值失败，撤销成功");
			dhbMobileTransactionDao.updateOrderAndTrans(moborder, transaction);
			mobileResp.setCode(Constant.QB_STATE_CANCLE_SUC.toString());
			mobileResp.setMessage(transaction.getRetMsg());
			return mobileResp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
//	public String createXML(InQuan cutQuan){
//		InQuan quan = new InQuan();
//		InQuanbody quanbody = new InQuanbody();
//		
//		InMsg msg = new InMsg();
//		msg.setVersion("1.0.0");
//		msg.setType("6003");
//		msg.setFlag("00");
//		quanbody.setMsg(msg);
//		
//		InAccount account = new InAccount();
//		InAccount cutAccount = cutQuan.getQuanbody().getAccount();
//		account.setId(cutAccount.getId());//钱包账号id
//		account.setType(cutAccount.getType());//钱包账号类型
//		quanbody.setAccount(account);
//		
//		InPurchase purchase = new InPurchase();
//		InPurchase cutPurchase = cutQuan.getQuanbody().getPurchase();
//		purchase.setAcqBIN(cutPurchase.getAcqBIN());//受理方机构代码
//		purchase.setDate(DateUtil.formatYYYYMMDDHHMMSS(new Date()));//受理日期时间
//		purchase.setCurrency("156");//币种
//		purchase.setTransAmt(cutPurchase.getTransAmt());//交易金额
//		purchase.setMerId(cutPurchase.getMerId());//商户号
//		purchase.setOrderNum(cutPurchase.getOrderNum());//订单号
//		purchase.setTraceNum(cutPurchase.getTraceNum());
//		quanbody.setPurchase(purchase);
//		
//		quanbody.setExtInfo("");
//		quanbody.setPubKeyIndex(cutQuan.getQuanbody().getPubKeyIndex());
//		quanbody.setTicket(cutQuan.getQuanbody().getTicket());
//		
//		quan.setQuanbody(quanbody);
//		String secureData= doSignForRevoke(quanbody);
//		quan.setSecureData(secureData);
//		return XmlUtil.ObjectToXml(quan,"UTF-8").replace(" standalone=\"yes\"", "");
//	}
//	private String doSignForRevoke(InQuanbody quanbody){
//		try {
//			String quanbodyXml = XmlUtil.ObjectToXml(quanbody,"UTF-8");
//			String quanbodystr = quanbodyXml.substring(quanbodyXml.indexOf("<Quanbody>"));
//			quanbodystr = quanbodystr.replaceAll("\n","").replaceAll(" ", "");
//			String sha = CGBUtil.sha1(quanbodystr);
//			String body128 = CGBUtil.addZeroForNum("", 48) + sha + CGBUtil.addZeroForNum("", 20)+CGBUtil.addZeroForNum("", 20);
//			logger.info("sjcz 【钱包充值撤销接口】报文："+body128);
//			String key = PropFileUtil.getByFileAndKey("mobile.properties", "publicKey");
//			byte[] signdata = CGBUtil.encryptByPublicKey(body128.getBytes(), key);
//			String secureData = CGBUtil.toHexString(signdata).replaceAll(" ", "");
//			return secureData;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	/**
	 * 生成一笔欧飞交易
	 * @param order
	 * @param mobile
	 * @param cardId
	 * @param ofcardNum
	 * @return
	 */
	public DhbMobileTransaction createTransaction(DhbMobileOrder order,InPurchase purchase){
		DhbMobileTransaction transaction = new DhbMobileTransaction();
		transaction.setId(Tools.getUUID());
		transaction.setMoborderId(order.getId());
		transaction.setPlatTime(new Date());
		transaction.setAmt(order.getAmt());
		transaction.setState(Constant.QB_STATE_NEW);
		transaction.setMobile(order.getMobile());
		transaction.setOfCardId("140101");//默认快充
		transaction.setOfCardNum(AmountUtil.parseAmountLong2Str(order.getAmt()));//面值
		transaction.setSpOrderId(getOFOrderId());
		logger.info("sjcz 欧飞订单号："+transaction.getSpOrderId());
		transaction.setReturl(ret_url);
		dhbMobileTransactionDao.saveDhbTransaction(transaction);
		return transaction;
	}
	private String sendToOFMobQuery(String sporderid){
		String userid = PropFileUtil.getByFileAndKey("mobile.properties", "userid");
		String param = "userid="+userid+"&spbillid="+sporderid;
		int readTimeout=10000;
	    String result = HttpHelper.doHttpGetResponse(url, HttpHelper.GET, "GBK", param, readTimeout+"");
		return result;
	}
	private MobileRspInfo sendToOFMobPayTest(DhbMobileTransaction transaction){
			MobileRspInfo rspinfo = new MobileRspInfo();
	    	rspinfo.setSporderid(transaction.getSpOrderId());
	    	rspinfo.setReturl(ret_url);
	    	rspinfo.setRetcode("1");
	    	rspinfo.setErrmsg("");
    		rspinfo.setOrderid(System.currentTimeMillis()+"");
    		rspinfo.setCardid("140101");
    		rspinfo.setCardnum(transaction.getOfCardNum());
    		rspinfo.setOrdercacsh("2");
    		rspinfo.setCardname("");
    		rspinfo.setGamestate("1");
    		return rspinfo;
	}
		//传参
	private MobileRspInfo sendToOFMobPay(DhbMobileTransaction transaction){
		String istest = PropFileUtil.getByFileAndKey("mobile.properties", "istest");
		if("test".equals(istest)){
			return sendToOFMobPayTest(transaction);
		}
        String userid = PropFileUtil.getByFileAndKey("mobile.properties", "userid");
        String pwd = PropFileUtil.getByFileAndKey("mobile.properties", "userpwd");
        String userpws= MD5Util.sign(pwd, "GBK").toLowerCase();

        //参数值为140101（快充）或者 170101（慢充）
        String cardid="140101";
        //要充值的金额
        Integer cardnum = (int) (transaction.getAmt()/100);
        
        //外部订单号，唯一性
        String sporder_id = transaction.getSpOrderId();
        //格式：年月日时分秒 如：20141119112450
        String sporder_time=DateUtil.formatYYYYMMDDHHMMSS(new Date());
        //要充值的手机号码
        String game_userid = transaction.getMobile();
        //该参数将异步返回充值结果，若不填写该地址，则不会回调
//        String ret_url = PropFileUtil.getByFileAndKey("mobile.properties", "returl");

        //版本号固定值
        String version="6.0";

        //若cardid=170101，需要加上下面的参数，不传该值默认为24
        String mctype="";

        //默认的秘钥是OFCARD，可联系商务修改，若已经修改过的，请使用修改过的。
        String keystr = PropFileUtil.getByFileAndKey("mobile.properties", "keystr");
        String md5_str_param = userid+userpws+cardid+cardnum+sporder_id+sporder_time+game_userid+keystr;
        String md5_str = MD5Util.sign(md5_str_param,"gbk").toUpperCase();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("userid="+userid+"&");
        stringBuffer.append("userpws="+userpws+"&");
        stringBuffer.append("cardid="+cardid+"&");
        stringBuffer.append("cardnum="+cardnum+"&");
        stringBuffer.append("mctype="+mctype+"&");
        stringBuffer.append("sporder_id="+sporder_id+"&");
        stringBuffer.append("sporder_time="+sporder_time+"&");
        stringBuffer.append("game_userid="+game_userid+"&");
        stringBuffer.append("md5_str="+md5_str+"&");
        stringBuffer.append("ret_url="+ret_url+"&");
        stringBuffer.append("version="+version);

        String param = stringBuffer.toString();
        logger.info("SJCZ 上送欧飞报文："+param);
        int readTimeout=10000;
        try{
        	String result = HttpHelper.doHttpGetResponse(url, HttpHelper.GET, "GBK", param, readTimeout+"");
        	logger.info("SJCZ 欧飞响应结果："+result);
        	//解析欧飞返回的xml文件
        	Document document = DocumentHelper.parseText(result);
        	Element root = document.getRootElement();
        	MobileRspInfo rspinfo = new MobileRspInfo();
        	rspinfo.setSporderid(sporder_id);
        	rspinfo.setReturl(ret_url);
        	String retcode = root.element("retcode").getText();
        	String err_msg = root.element("err_msg").getText();
        	rspinfo.setRetcode(retcode);
        	rspinfo.setErrmsg(err_msg);
        	if("1".equals(retcode)){
        		String orderid= root.element("orderid").getText();
        		String cardids =root.element("cardid").getText();
        		String cardnums =root.element("cardnum").getText();
        		String ordercash =root.element("ordercash").getText();
        		String cardname =root.element("cardname").getText();
        		String sporder_ids =root.element("sporder_id").getText();
        		String game_userids =root.element("game_userid").getText();
        		String game_state =root.element("game_state").getText();
        		
        		rspinfo.setOrderid(orderid);
        		rspinfo.setCardid(cardids);
        		rspinfo.setCardnum(cardnums);
        		rspinfo.setOrdercacsh(ordercash);
        		rspinfo.setCardname(cardname);
//        		rspinfo.setSporderid(sporder_ids);
        		rspinfo.setMobile(game_userids);
        		rspinfo.setGamestate(game_state);
        	}
        	return rspinfo;
        }catch (Exception e) {
    	  	e.printStackTrace();
    	  	return null;
        }
	}
	
	
	public boolean validateMobile(InQuan quan){
		InPurchase purchase = quan.getQuanbody().getPurchase();
		String mobile = purchase.getPhnumber();
		String tranamt = purchase.getTransAmt();
		if(StringUtil.isEmpty(mobile)||StringUtil.isEmpty(tranamt)){
			logger.error("手机号或交易金额为空,mobile="+mobile+",tranamt="+tranamt);
			return false;
		}
		return true;
	}
	/**
	 * 欧飞订单号
	 * @return
	 */
	public String getOFOrderId(){
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
	    DateFormat tf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String batch_id = df.format(now) + tf.format(now);
		int seq = (int)sequenceDao.getNextVal("DHB_OF_ORDERID_SEQ");
		String numstr = format(seq, 8);
		return batch_id+numstr;
	}
	
	public String format(int num, int width) {
		if (num<0) return "";
		StringBuffer sb = new StringBuffer();
		String s = ""+num;
		if (s.length()<width) {
			int addNum = width-s.length();
			for (int i=0;i<addNum;i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length()-width,s.length());
		}
		return sb.toString();
	}
	public OutQuan createOutQuan(String cxjson,String ticket){
		String baowen = cxjson.substring(76);
		String baowenxml = new String(Base64.decodeBase64(baowen.getBytes()));
		logger.info("SJCZ 【钱包撤销接口】报文解析成xml："+baowenxml);
		
		InQuan quan = null;
		try {
			quan = (InQuan) XmlUtil.xmltoObject(baowenxml,InQuan.class);
			if(quan == null){
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		InQuanbody body = quan.getQuanbody();
		InMsg msg = body.getMsg();
		InAccount account = body.getAccount();
		InPurchase pur = body.getPurchase();
		
		OutQuan outquan = new OutQuan();
		OutQuanbody quanbody = new OutQuanbody();
		OutMsg outmsg = new OutMsg();
		outmsg.setVersion(msg.getVersion());
		outmsg.setType(msg.getType());
		outmsg.setFlag("01");
		OutAccount outaccount = new OutAccount();
		outaccount.setId(account.getId());
		outaccount.setType(account.getType());
		OutPurchase outpur = new OutPurchase();
		outpur.setAcqBIN(pur.getAcqBIN());
		outpur.setDate(pur.getDate());
		outpur.setTraceNum(pur.getTraceNum());
		outpur.setCurrency(pur.getCurrency());
		outpur.setTransAmt(pur.getTransAmt());
		outpur.setMerId(pur.getMerId());
		outpur.setOrderNum(pur.getOrderNum());
		OutResp rsp = new OutResp();
		rsp.setRespCode("");
		rsp.setRespInfo("");
		quanbody.setTicket(ticket);
		quanbody.setMsg(outmsg);
		quanbody.setAccount(outaccount);
		quanbody.setPurchase(outpur);
		quanbody.setResp(rsp);
		outquan.setQuanbody(quanbody);
		return outquan;
	}
	
}
