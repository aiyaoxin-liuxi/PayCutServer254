package com.dhb.ysb.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.ysb.entity.Constants;
/**
 * @author pyc
 * 公用方法
 */
public class YSBUtil {
	static Logger logger = Logger.getLogger(YSBUtil.class);
	static Properties props = new Properties();
	 //全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    public YSBUtil() {
    }
    /**
     * 返回形式为数字跟字符串
     * @param bByte
     * @return String
     */
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    /**
     * 返回形式只为数字
     * @param bByte
     * @return String
     */
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        //System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
    /**
     * 转换字节数组为16进制字串 
     * @param bByte
     * @return String
     */
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    /**
     * MD5加密 
     * @param strObj
     * @return String.toUpperCase()
     */
    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString.toUpperCase();
    }
	/**
	 * 读取配置文件方法（通用）
	 * @param fileName
	 * @param key
	 * @return value
	 */
	public static String getReadProperties(String fileName,String key){
		InputStream in = YSBUtil.class.getResourceAsStream("/"+fileName+".properties");
		String value = "";
		try{
			props.load(in);
			if (null == key) {
				return null;
			}
			value = props.getProperty(key);
		}catch(IOException e){
			logger.error("PayCutServer:获取"+fileName+".properties文件时异常：",e);
		}finally{
			try{
				if(in != null){
					in.close();
				}
			}catch(IOException e){
				logger.error("PayCutServer:关闭文件流："+fileName+".properties文件时异常：",e);
			}
		}
		return value;
	}
	/**
	 * 组装待签名数据
	 * @param map
	 * @return
	 */
	public static String getAssembleSign(Map<String,String> map){
		String str = "";
		for(Map.Entry<String,String> entry : map.entrySet()){
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			if(entry.getValue() != null && entry.getValue().length() != 0){
				str += entry.getKey() + "=" + entry.getValue() + "&";
			}
		}
		String str_ = str + "key="+YSBUtil.getReadProperties("ysb", "key");
		return str_;
	} 
	public static void main(String[] args) {
		
		//System.out.println(GetMD5Code("af20160906").toLowerCase().substring(0,16));
		System.out.println(GetMD5Code("amount=1&desc=测试微信支付被扫接口1&errcode=9005&errdesc=订单号格式错误&mid=200100850396914&orderid=79838353c54841cdb31c09cbd862d95f&payway=WXZF&qrcode=weixin://wxpay/bizpayurl?pr=cYnyxY7&status=S&subject=测试商品&unno=200202"));
		// TODO Auto-generated method stub
		//System.out.println(YSBUtil.getReadProperties("ysb", "url"));
		/**
		 * 实时代付接口
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("name","潘泳辰"); 
//		map.put("cardNo","6222020200057200491"); 
//		map.put("orderId",System.currentTimeMillis()+"");
//		map.put("purpose","测试");
//		map.put("amount","0.01");
//		map.put("responseUrl","http://www.baidu.com");
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		System.out.println(map.get("mac"));
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singlePay, map);
//		logger.info("通道方返回的数据："+msg);
		/**
		 * 实时代付交易查询接口
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("orderId","21201606071122580012016071608385");
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		System.out.println(map.get("mac"));
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singlePay_select, map);
//		logger.info("通道方返回的数据："+msg);
		/**
		 * 子协议录入接口
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("contractId",YSBUtil.getReadProperties("ysb", "contractId"));
//		map.put("name","潘泳辰");
//		map.put("phoneNo","15010001161");
//		map.put("cardNo","6222020200057200491");
//		map.put("idCardNo","230206198906121115");
//		map.put("startDate","20160629");//yyyyMMdd
//		map.put("endDate","20170629");//yyyyMMdd
//		map.put("cycle","");
//		map.put("triesLimit","");
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut_contract, map);
//		logger.info("通道方返回的数据："+msg);
		//2016-06-28 19:37:32,078 [main] INFO  待签名数据：accountId=1120160623142200001&contractId=1120160623142200001&name=潘泳辰&phoneNo=15010001161&cardNo=6222020200057200491&idCardNo=230206198906121115&startDate=20160628&endDate=20161231&key=123456
		//2016-06-28 19:37:35,136 [main] INFO  通道方返回的数据：{"result_code":"0000","result_msg":"","subContractId":"5820"}
		/**
		 * 委托代扣接口
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("subContractId","5820");
//		map.put("orderId",System.currentTimeMillis()+"");
//		map.put("purpose","测试代扣");
//		map.put("amount","0.01");
//		map.put("phoneNo","");
//		map.put("responseUrl",Constants.ysb_notifyUrl);
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut, map);
//		logger.info("通道方返回的数据："+msg);
		//2016-06-28 19:57:22,866 [main] INFO  待签名数据：accountId=1120160623142200001&subContractId=5820&orderId=1467115042866&purpose=测试代扣&amount=0.01&responseUrl=http://www.baidu.com&key=123456
		//2016-06-28 19:57:26,002 [main] INFO  通道方返回的数据：{"result_code":"0000","result_msg":""}
		/**
		 * 单笔代扣查询
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("orderId","21201606071122580012016081008611");
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut_select, map);
//		logger.info("通道方返回的数据："+msg);
		//通道方返回的数据：{"result_code":"0000","desc":"交易成功","result_msg":"查询成功","status":"00"}
		/**
		 * 子协议延期接口
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId", YSBUtil.getReadProperties("ysb", "accountId"));
//		map.put("contractId", YSBUtil.getReadProperties("ysb", "contractId"));
//		map.put("subContractId", "5841");
//		map.put("startDate", "20160711");
//		map.put("endDate", "20160811");
//		String sign = getAssembleSign(map);
//		logger.info("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac", mac);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_contract_extension, map);
//		logger.info("通道方返回的数据："+msg);
	}

}
