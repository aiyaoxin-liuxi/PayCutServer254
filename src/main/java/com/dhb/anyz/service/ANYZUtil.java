package com.dhb.anyz.service;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.dhb.anyz.entity.Constants;
import com.dhb.util.JsonUtil;
import com.jnewsdk.connection.client.HttpClient;
import com.jnewsdk.connection.client.HttpSSLClient;
import com.jnewsdk.tools.log.LogFactory;
import com.jnewsdk.util.Base64;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.SignUtil;
import com.jnewsdk.util.StringUtils;

/**
 * 爱农驿站公用方法
 * @author pyc
 */
public class ANYZUtil {
	private static final Logger logger = Logger.getLogger(ANYZUtil.class);
	// 对内容做Base64加密
	private static final String[] base64Keys = new String[] { "subject", "body", "remark" };
	// 对内容做Base64加密， 所有子域采用json数据格式
	private static final String[] base64JsonKeys = new String[] { "customerInfo", "accResv", "riskRateInfo", "billQueryInfo","billDetailInfo" };
	private static final String URL_PARAM_CONNECT_FLAG = "&";
	public static final Set<String> base64Key = new HashSet<String>();
	static {
		base64Key.add("subject");
		base64Key.add("body");
		base64Key.add("remark");
		base64Key.add("customerInfo");
		base64Key.add("accResv");
		base64Key.add("riskRateInfo");
		base64Key.add("billpQueryInfo");
		base64Key.add("billDetailInfo");
		base64Key.add("respMsg");
		base64Key.add("resv");
	}
	
	/**
	 * 转换特殊字符
	 * @param paramMap
	 */
	protected static void converData(Map paramMap) {
		for (int i = 0; i < base64Keys.length; i++) {
			String key = base64Keys[i];
			String value = (String) paramMap.get(key);
			if (StringUtils.isNotEmpty(value)) {
				try {
					String text = new String(Base64.encode(value.getBytes("UTF-8")));
					// 更新请求参数
					paramMap.put(key, text);
				} catch (Exception e) {
				}
			}
		}
		for (int i = 0; i < base64JsonKeys.length; i++) {
			String key = base64JsonKeys[i];
			String value = (String) paramMap.get(key);
			if (StringUtils.isNotEmpty(value)) {
				try {
					String text = new String(Base64.encode(value.getBytes("UTF-8")));
					// 更新请求参数
					paramMap.put(key, text);
				} catch (Exception e) {
				}
			}
		}
	}
	/**
	 * 往渠道发送数据
	 * @param url 通讯地址
	 * @param map 发送参数
	 * @return 应答消息
	 */
	public static String sendMsg(String url, Map<String, String> map) {
		try {
			HttpClient http = new HttpSSLClient(url, "60000");
			http.setRequestMethod("POST");
			http.connect();
			// 转换参数格式
			String webForm = getWebForm(map);
			http.send(webForm.getBytes());
			byte[] rspMsg = http.getRcvData();
			String msg = new String(rspMsg, "utf-8");
			LogFactory.getLog().info(ANYZUtil.class, msg);
			return msg;
		} catch (Exception e) {
			LogFactory.getLog().error(e, e.getMessage());
		}
		return null;
	}
	/**
	 * 将map转化为形如key1=value1&key2=value2...
	 * @param map
	 * @return
	 */
	public static String getWebForm(Map<String, String> map) {
		if (null == map || map.keySet().size() == 0) {
			return "";
		}
		StringBuffer url = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			String str = (value != null ? value : "");
			try {
				str = URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			url.append(entry.getKey()).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
		}
		// 最后一个键值对后面的“&”需要去掉。
		String strURL = "";
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}
	/**
	 * 验签
	 * @param paramMap
	 * @return
	 */
	public static boolean verifySign(Map paramMap,String mkey) {
		// 计算签名
		Set<String> removeKey = new HashSet<String>();
		removeKey.add("signMethod");
		removeKey.add("signature");
		String signedMsg = SignUtil.getSignMsg(paramMap, removeKey);
		String signMethod = (String) paramMap.get("signMethod");
		String signature = (String) paramMap.get("signature");
		// 密钥
		//String key = ResourceUtil.getString("anyz", mkey);
		return SignUtil.verifySign(signMethod, signedMsg, signature, mkey, "UTF-8");
	}
	/**
	 * 转换报文格式及特殊字段base64解码
	 * @param msg
	 * @return
	 */
	public static Map<String,String> parseMsg(String msg) {
		Map<String,String> map = SignUtil.parseResponse(msg);
		// 特殊字段base64解码
		for (Iterator iterator = base64Key.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = (String) map.get(key);
			if (StringUtils.isNotEmpty(value)) {
				try {
					String text = new String(Base64.decode(value.toCharArray()), "UTF-8");
					map.put(key, text);
				} catch (Exception e) {
				}
			}
		}
		return map;
	}
	/**
	 * 设置签名
	 * @param paramMap
	 */
	public static void setSignature(Map<String, String> paramMap,String key) {
		String signMethod = paramMap.get("signMethod");

		Set<String> removeKey = new HashSet<String>();
		removeKey.add("signMethod");
		removeKey.add("signature");
		String signMsg = SignUtil.getSignMsg(paramMap, removeKey);
		System.out.println("signMsg:"+signMsg);
		//转义签名后的数据
		String signature = SignUtil.sign(signMethod, signMsg, key, "UTF-8");
//		if(signature.contains("+")){
//			signature = signature.replaceAll("\\+", "%2B");
//		}
//		if(signature.contains(" ")){
//			signature = signature.replaceAll(" ","%20");
//		}
//		if(signature.contains("/")){
//			signature = signature.replaceAll("\\/","%2F");
//		}
//		if(signature.contains("?")){
//			signature = signature.replaceAll("\\?","%3F");
//		}
//		if(signature.contains("%")){
//			signature = signature.replaceAll("\\%","%25");
//		}    
//		if(signature.contains("#")){
//			signature = signature.replaceAll("\\#","%23");
//		}    
//		if(signature.contains("&")){
//			signature = signature.replaceAll("\\&","%26");
//		}  
		paramMap.put("signature", signature);
	} 
    //金额验证  
  	public static boolean isNumber(String str){   
  	     Pattern pattern=Pattern.compile("^(?!0+(?:\\.0+)?$)(?:[1-9]\\d*|0)(?:\\.\\d{1,2})?$"); // 判断小数点后2位的数字的正则表达式  ^(([1-9]+)|([0-9]+\\.[0-9]{1,2}))$
  	     Matcher match=pattern.matcher(str);   
  	     if(match.matches()==false){   
  	        return false;   
  	     }else{   
  	        return true;   
  	     }   
  	 }
    /**  
     * 功能描述：去除字符串首部为"0"字符  
     * @param str 传入需要转换的字符串  
     * @return 转换后的字符串  
     */  
    public static String removeZero(String str){     
        char  ch;    
        String result = "";  
        if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){                  
            try{              
                for(int i=0;i<str.length();i++){  
                    ch = str.charAt(i);  
                    if(ch != '0'){                        
                        result = str.substring(i);  
                        break;  
                    }  
                }  
            }catch(Exception e){  
                result = "";  
            }     
        }else{  
            result = "";  
        }  
        return result;  
    }  
    /**  
     * 功能描述：金额字符串转换：单位元转成单分  
     * @param str 传入需要转换的金额字符串  
     * @return 转换后的金额字符串  
     */       
    public static String fromYuanToFen(String s) {  
        int posIndex = -1;  
        String str = "";  
        StringBuilder sb = new StringBuilder();  
        if (s != null && s.trim().length()>0 && !s.equalsIgnoreCase("null")){  
            posIndex = s.indexOf(".");  
            if(posIndex>0){  
                int len = s.length();  
                if(len == posIndex+1){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append("00");  
                }else if(len == posIndex+2){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+2)).append("0");  
                }else if(len == posIndex+3){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+3));  
                }else{  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+3));  
                }  
            }else{  
                sb.append(s).append("00");  
            }  
        }else{  
            sb.append("0");  
        }  
        str = removeZero(sb.toString());  
        if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){  
            return str;  
        }else{  
            return "0";  
        }  
    }  
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * 单笔代收
		 */
//		Map<String,String> map = new HashMap<String, String>();
//		Map<String,String> msg_map = null;
//		map.put("signMethod","MD5");//签名方法
//		map.put("version","1.0.0");//消息版本号
//		map.put("txnType","11");//交易类型--代收
//		map.put("txnSubType","01");//交易子类型--消费
//		map.put("bizType","000501");//产品类型--代收
//		map.put("accessType","0");//接入类型--商户直接接入
//		map.put("accessMode","01");//接入方式--web
//		map.put("merId",ResourceUtil.getString("anyz", "merId"));//	商户号
//		map.put("merOrderId",System.currentTimeMillis()+"");//商户订单号
//		map.put("accType","01");//账户类型   01借记卡，03存折，04公司账号
//		map.put("accNo","6217001210059043503");//账号
//		
//		Map<String,Object> customerInfo = new HashMap<String, Object>();//银行卡验证信息及身份信息 
//		customerInfo.put("issInsProvince","");//开户行省
//		customerInfo.put("issInsCity","");//开户行市
//		customerInfo.put("iss_ins_name","");//开户支行名称
//		customerInfo.put("certifTp","01");//证件类型必填   01：身份证 02：军官证 03：护照 04：回乡证 05：台胞证 06：警官证 07：士兵证 99：其它证件
//		customerInfo.put("certify_id","310107198303312116");//证件号码必填
//		customerInfo.put("customerNm","孙俊");//姓名必填
//		customerInfo.put("phoneNo","13916856042");//手机号必填
//		customerInfo.put("expired","");//有效期
//		customerInfo.put("cvv2 ","");//CVV2
//		customerInfo.put("smsCode","");//短信验证码
//		String json = JsonUtil.getMapToJson(customerInfo);
//		map.put("customerInfo",json);
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		map.put("txnTime",sdf.format(new Date()));//订单发送时间
//		map.put("txnAmt","1");//交易金额
//		map.put("currency","CNY");//交易币种
//		map.put("backUrl","");//后台通知地址    非必填
//		map.put("payType","0501");//支付方式 0501：代扣
//		map.put("bankId","03050000");//银行编号    民生
//		map.put("subject","");//商品标题  非必填
//		map.put("body","");//商品描述  非必填
//		map.put("ppFlag","01");//00对公01对私标志  非必填
//		map.put("purpose","");//用途   非必填
//		map.put("merResv1","");//请求保留域  非必填
//		map.put("signature","");//签名信息
//		
//		// 设置签名
//		setSignature(map);
//		String plain = SignUtil.getURLParam(map, false, null);
//		// 特殊字段数据转换
//		converData(map);
//		String reqMsg = SignUtil.getURLParam(map, false, null);
//		
//		String url = ResourceUtil.getString("anyz", "url");
//		String msg = sendMsg(url, map);
//		if (StringUtils.isEmpty(msg)) {
//			System.out.println("报文发送失败或应答消息为空");
//		} else {
////			String[] msg_ret = msg.split("&");
////			msg_map = new HashMap<String,String>();
////			for(int i = 0 ; i < msg_ret.length ; i++){
////				String[] msg_ret_two = msg_ret[i].split("=");
////				msg_map.put(msg_ret_two[0], msg_ret_two[1]);
////			}
//		} 
//			System.out.println(msg_map);
//			System.out.println("明文报文：" + plain + "");
//			System.out.println("渠道方返回结果：" + msg + "");
//			System.out.println("请求报文(个别字段base64)：" + reqMsg + "");
//			Map map1 = parseMsg(msg);
//			System.out.println("map1:"+map1);
//			System.out.println("base64解码后：" + StringUtils.toString(map1) + "");
//			System.out.println("签名验证结果：" + verifySign(map1) + "");
		
			/**
			 * 单笔代收查询
			 */
//			Map<String,String> map = new HashMap<String, String>();
//			map.put("signMethod","MD5");
//			map.put("signature","");
//			map.put("version",Constants.anyz_version);
//			map.put("txnType","00");
//			map.put("txnSubType","01");
//			map.put("merId",ResourceUtil.getString("anyz", "merId"));
//			map.put("merOrderId","22016030300044820160725183617000975");
//			// 设置签名
//			setSignature(map);
//			String plain = SignUtil.getURLParam(map, false, null);
//			System.out.println("plain:"+plain);
//			// 特殊字段数据转换
//			converData(map);
//			String reqMsg = SignUtil.getURLParam(map, false, null);
//			System.out.println("reqMsg:"+reqMsg);
//			String url = ResourceUtil.getString("anyz", "url");
//			System.out.println("map:"+map);
//			String msg = sendMsg(url, map);
//			System.out.println("msg:"+msg);
//			
//			Map map1 = parseMsg(msg);
//			System.out.println("base64解码后：" + StringUtils.toString(map1) + "");
//			System.out.println("签名验证结果：" + verifySign(map1) + "");
		
		System.out.println(isNumber("0.001"));


	}
}
