package com.dhb.dao.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.util.Comparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.dhb.controller.DHBPayController;
import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.BigMerchInfo;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.entity.NfcMerchRule;
import com.dhb.nfc.entity.NfcMerchToPayMerch;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.util.EncodeUtils;
import com.dhb.util.MD5;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.base.Strings;

@Service
public class KeyInfoDao {
	public static Logger logger = Logger.getLogger(KeyInfoDao.class);
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public boolean checkSecretKey(String merchId,String secretKey){
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(secretKey)){
			return false;
		}
		String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
		String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
		if(secretKey.equals(MD5.encrypt(merchId,keySrc))){
			return true;
		}
		return false;
	}
	public boolean signQuery(String merchId,String tranNo,String secretKey){
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(secretKey)){
			return false;
		}
		String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
		String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
		logger.debug("signQuery should be："+MD5.encrypt(merchId+tranNo,keySrc));
		if(secretKey.equals(MD5.encrypt(merchId+tranNo,keySrc))){
			return true;
		}
		return false;
	}
	public boolean signQueryForMd5(String merchId,String tranNo,String secretKey){
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(tranNo)){
			return false;
		}
		String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
		String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
		String sign = MD5.encrypt(merchId+tranNo+keySrc);
		logger.info("收到请求：本地验签(" + tranNo + ")参数：secretKey="+secretKey+",本地sign="+sign+",待签名："+merchId+tranNo+keySrc);
		if(secretKey.equals(sign)){
			logger.info("收到请求：本地验签(" + tranNo + ")参数：true");
			return true;
		}
		logger.info("收到请求：本地验签(" + tranNo + ")参数：false");
		return false;
	}
	//signSrc =merchId+tranNo+acct.getAcctno()+acct.getPayerid();
	public boolean sign(String merchId,String signSrc,String sign){
			if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(signSrc)||Strings.isNullOrEmpty(sign)){
				return false;
			}
			String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
			String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
			String encrypt = MD5.encrypt(signSrc.trim(),keySrc.trim());
			logger.info(sign.trim()+"sign:2收到请求，本地验签：signSrc:"+signSrc+", keySrc:"+keySrc+", encrypt:"+encrypt);
//			logger.debug("signSrc:("+signSrc+"),"+"sign("+sign+")");
			if(sign.trim().equals(encrypt)){
				return true;
			}
			return false;
	}
	public boolean signTran(String merchId,Double money,String accNo,String tranNo,String secretKey){
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(secretKey)){
			return false;
		}
	
		String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
		String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
		String sign = MD5.encrypt(merchId+String.format("%.2f", money)+accNo+tranNo,keySrc);
		logger.info("收到请求：本地验签(" + tranNo + ")参数：secretKey="+secretKey+",本地sign="+sign+",待签名："+merchId+String.format("%.2f", money)+accNo+tranNo+",签名密钥："+keySrc);
		if(secretKey.equals(sign)){
			logger.info("收到请求：本地验签(" + tranNo + ")参数：true");
			return true;
		}
		logger.info("收到请求：本地验签(" + tranNo + ")参数：false");
		return false;
	}
	
	public boolean signTranForMd5(String merchId,Double money,String accNo,String tranNo,String secretKey){
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(tranNo)){
			return false;
		}
		String sql = "select keySrc from dhb_keyInfo where merchId=:merchId";
		String keySrc=commonObjectDao.findSingleVal(sql, new Object[]{merchId});
		String sign = MD5.encrypt(merchId+String.format("%.2f", money)+accNo+tranNo+keySrc);
		logger.info("收到请求：本地验签(" + tranNo + ")参数：secretKey="+secretKey+",本地sign="+sign+",待签名："+merchId+String.format("%.2f", money)+accNo+tranNo+keySrc);
		if(secretKey.equals(sign)){
			logger.info("收到请求：本地验签(" + tranNo + ")参数：true");
			return true;
		}
		logger.info("收到请求：本地验签(" + tranNo + ")参数：false");
		return false;
	}
	/**
	 * NFC
	 * NFC交易接口签名验证
	 * @param sign
	 * @param waitSign
	 * @param merch_no
	 * @param order_no
	 * @return
	 */
	public boolean getNFCsignValidate(String sign,String waitSign,String merch_no,String order_no){
		String sql = "select merch_key from DHB_NFC_MERCH where merch_no=:merch_no";
		String merch_key=commonObjectDao.findSingleVal(sql, new Object[]{merch_no});
		String signParam = "";
		try {
			signParam = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")),merch_key);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("收到请求：本地验签(" + order_no + ")参数：用户传入的sign="+sign+",待签名："+waitSign+",签名密钥："+merch_key+",本地signParam="+signParam);
		if(signParam.equals(sign)){
			logger.info("收到请求：本地验签(" + order_no + ")参数：true");
			return true;
		}
		logger.info("收到请求：本地验签(" + order_no + ")参数：false");
		return false;
	}
	/**
	 * NFC
	 * 新版NFC交易接口签名验证（以后统一用这种）
	 * @param sign
	 * @param waitSign
	 * @param sub_merch_no
	 * @param order_no
	 * @return
	 */
	public boolean getNewNFCsignValidate(String sign,String waitSign,String sub_merch_no,String order_no){
		/**
		 * //与posp约定的商户号
			String posp_nfc_merchno = YSBUtil.getReadProperties("sub_merch", "posp_nfc_merchno");
			//与posp约定的商户密钥
			String posp_nfc_key = YSBUtil.getReadProperties("sub_merch", "posp_nfc_key");
		 */
		
		String sql = "select merch_key from DHB_NFC_MERCH where merch_no=:sub_merch_no";
		String sub_merch_key=commonObjectDao.findSingleVal(sql, new Object[]{sub_merch_no});
		
		String signParam = "";
		if(sub_merch_key != null && !"".equals(sub_merch_key)){
			try {
				signParam = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")),sub_merch_key);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("收到请求：本地验签(" + order_no + ")参数：用户传入的sign="+sign+",待签名："+waitSign+",签名密钥："+sub_merch_key+",本地signParam="+signParam);
			if(signParam.equals(sign)){
				logger.info("收到请求：本地验签(" + order_no + ")参数：true");
				return true;
			}else{
				logger.info("收到请求：本地验签(" + order_no + ")参数：false");
				return false;
			}
		}else{
			logger.info("收到请求：本地验签(" + order_no + ")sub_merch_no is null参数：false");
			return false;
		}
	}
	
	
	/**
	 * NFC
	 * NFC获取给商户设置的手续费率
	 * @param merch_no
	 * @return
	 */
	public String getNfcMerchRate(String merch_no){
		String sql = "select to_char(m.merch_rate,'FM0.9999') as merch_rate from DHB_NFC_MERCH m where m.merch_no=:merch_no";
		String merch_rate=commonObjectDao.findSingleVal(sql, new Object[]{merch_no});
		return merch_rate;
	}
	/**
	 * NFC
	 * NFC获取给商户分配的秘钥
	 * @param merch_no
	 * @return
	 */
	public String getNfcKeySrc (String merch_no){
		String sql = "select merch_key from DHB_NFC_MERCH where merch_no=:merch_no";
		String merch_key=commonObjectDao.findSingleVal(sql, new Object[]{merch_no});
		return merch_key;
	}
	/**
	 * NFC
	 * NFC获取给商户分配的通道
	 * @param merch_no
	 * @return
	 */
	public String getNfcMerchChannel(String merch_no){
		String sql = "select channel from DHB_NFC_MERCH where merch_no=:merch_no";
		String merch_channel=commonObjectDao.findSingleVal(sql, new Object[]{merch_no});
		return merch_channel;
	}
	/**
	 * NFC
	 * NFC获取商户调用通道的规则
	 * @param merch_no
	 * @param total_fee
	 * @return
	 */
	public NfcMerchRule getNfcMerchRule(String nfc_merch,String merch_no,Double total_fee){
		NfcMerchRule nfcMerchRule = new NfcMerchRule();
		String sql = "select m.merch_no,m.merch_channel,to_char(m.merch_rate,'FM0.9999') as merch_rate,m.rule_precedence from DHB_NFC_MERCH_RULE m where 1=1 ";
			   sql+="and m.nfc_merch = :nfc_merch ";
			   sql+="and m.merch_no = :merch_no ";
			   sql+="and m.status = '1' ";
			   sql+="and m.minimum_date <= sysdate ";
			   sql+="and m.maximum_date >= sysdate ";
			   sql+="and m.minimum_amount <= :total_fee ";
			   sql+="and m.maximum_amount >= :total_fee ";
			   sql+="order by m.created_time desc";
			   
		List<NfcMerchRule> nfcMerchRuleList = commonObjectDao.findList(sql, NfcMerchRule.class, new Object[]{nfc_merch,merch_no,total_fee,total_fee});
		if(nfcMerchRuleList.size() == 0){
			nfcMerchRule = null;
		}else{//符合条件
			if(nfcMerchRuleList.size() > 1){
				Collections.sort(nfcMerchRuleList, new Comparator<NfcMerchRule>(){
					@Override
					public int compare(NfcMerchRule r1, NfcMerchRule r2) {
						// TODO Auto-generated method stub
						int seq1 = 0;  
				        int seq2 = 0;  
				        try {  
				            seq1 = Integer.parseInt(r1.getRulePrecedence());  
				            seq2 = Integer.parseInt(r2.getRulePrecedence());
				        } catch (Exception e) {  
				        }  
				        return seq2 - seq1;  
					}
		        });
				nfcMerchRule = nfcMerchRuleList.get(0);
			}
			if(nfcMerchRuleList.size() == 1){
				nfcMerchRule = nfcMerchRuleList.get(0);
			}
		}
		return nfcMerchRule;
	}
	/**
	 * NFC
	 * NFC获取商户与通道商户对应表
	 * @param merch_no
	 * @param merch_channel
	 * @return
	 */
	public NfcMerchToPayMerch getNfcMerchToPayMerch(String merch_no,String merch_channel){
		NfcMerchToPayMerch nfcMerchToPayMerch = new NfcMerchToPayMerch();
		String sql = "select * from DHB_NFC_MERCH_TO_PAYMERCH where 1=1";
		sql+=" and merch_no = :merch_no";
		sql+=" and merch_channel = :merch_channel";
		sql+=" and status = '1'";
		sql+=" order by created_time desc";
		List<NfcMerchToPayMerch> nfcMerchToPayMerchList = commonObjectDao.findList(sql, NfcMerchToPayMerch.class, new Object[]{merch_no,merch_channel});
		if(nfcMerchToPayMerchList.size() == 0){
			nfcMerchToPayMerch = null;
		}else{
			nfcMerchToPayMerch = nfcMerchToPayMerchList.get(0);
		}
		return nfcMerchToPayMerch;
	}
	/**
	 * NFC
	 * NFC根据通道商户号和渠道标识获取商户与通道商户对应表
	 * @param pay_merch_no
	 * @param merch_channel
	 * @return
	 */
	public NfcMerchToPayMerch getNfcMerchToPayMerch_(String pay_merch_no,String merch_channel){
		NfcMerchToPayMerch nfcMerchToPayMerch = new NfcMerchToPayMerch();
		String sql = "select * from DHB_NFC_MERCH_TO_PAYMERCH where 1=1";
		sql+=" and pay_merch_no = :pay_merch_no";
		sql+=" and merch_channel = :merch_channel";
		sql+=" and status = '1'";
		sql+=" order by created_time desc";
		List<NfcMerchToPayMerch> nfcMerchToPayMerchList = commonObjectDao.findList(sql, NfcMerchToPayMerch.class, new Object[]{pay_merch_no,merch_channel});
		if(nfcMerchToPayMerchList.size() == 0){
			nfcMerchToPayMerch = null;
		}else{
			nfcMerchToPayMerch = nfcMerchToPayMerchList.get(0);
		}
		return nfcMerchToPayMerch;
	}
	/**
	 * NFC
	 * NFC根据商户号和渠道标识获取交易流水表
	 * @param merch_no
	 * @param merch_channel
	 * @return
	 */
	public NfcOrderWater getNfcOrderWater(String merch_no,String merch_channel){
		NfcOrderWater nfcOrderWater = new NfcOrderWater();
		String sql = "select * from DHB_NFC_ORDER_WATER where 1=1";
		sql+=" and merch_no = :merch_no";
		sql+=" and merch_channel = :merch_channel";
		sql+=" and status = '3'";
		sql+=" order by created_time desc";
		List<NfcOrderWater> nfcOrderWaterList = commonObjectDao.findList(sql, NfcOrderWater.class, new Object[]{merch_no,merch_channel});
		if(nfcOrderWaterList.size() == 0){
			nfcOrderWater = null;
		}else{
			nfcOrderWater = nfcOrderWaterList.get(0);
		}
		return nfcOrderWater;
	}
	
	
	public String getTranSign(String merchId,Double money,String accNo,String tranNo,String keySrc){
		return MD5.encrypt(merchId+String.format("%.2f", money)+accNo+tranNo,keySrc);
	}
	public String getQuerySign(String merchId,String tranNo,String keySrc){
		return MD5.encrypt(merchId+tranNo,keySrc);
	}
	public String checkPass(String merchId,String secretKey,ModelMap model){
		Boolean isMyMerch= (Boolean)model.get("isMyMerch");
		if(isMyMerch==null||!isMyMerch){
			
			isMyMerch=checkSecretKey(merchId, secretKey);
			if(!isMyMerch){
				return "error/nomerchid";
			}
		}
		model.addAttribute("isMyMerch", isMyMerch);
		return "";
	}

	public static void main(String[] args) {
		//http://localhost:8080/merch-os-old/reportTask/download?id=b92fcd22f9e04cbcbf52976672b5358f

///{"tranNo":"290020061205154300204322","merchId":"111101000000000","channelId":"2","bankName":"中国建设银行","accNo":"6227001111111117581","accName":"张三","accType":"00","bankProvince":"","bankCity":"","banlance":"10.00","currency":"156","certType":"95bw109o0y10w0o5","protocolNo":"","bankCode":"105100000017"}
//		String merchId ="111101000000000";
//		double money =10888.10;
//		
//		System.out.println(String.format("%.2f", money));;
//		String accNo = "6227001111111117581";
//		String trano = "290020061205154300204322";
//		String key = merchId+money+accNo+trano;
//		System.out.println(key);
//		System.out.println(MD5.encrypt(key,"ykw0n5149fob59xt"));;
		List<NfcMerchRule> list = new ArrayList<NfcMerchRule>();
		NfcMerchRule a = new NfcMerchRule();
		a.setRulePrecedence("101");
		NfcMerchRule b = new NfcMerchRule();
		b.setRulePrecedence("100");
		NfcMerchRule c = new NfcMerchRule();
		c.setRulePrecedence("28");
        list.add(a);
        list.add(b);
        list.add(c);
        
        Collections.sort(list, new Comparator<NfcMerchRule>(){
			@Override
			public int compare(NfcMerchRule r1, NfcMerchRule r2) {
				// TODO Auto-generated method stub
				int seq1 = 0;  
		        int seq2 = 0;  
		        try {  
		            seq1 = Integer.parseInt(r1.getRulePrecedence());  
		            seq2 = Integer.parseInt(r2.getRulePrecedence());
		        } catch (Exception e) {  
		        }  
		        return seq2 - seq1;  
			}
        });  
        
//        for (NfcMerchRule s : list) {  
//            System.out.println(s.getRulePrecednece());  
//        }          
        System.out.println(list.get(0).getRulePrecedence());
        
	}
	
}
