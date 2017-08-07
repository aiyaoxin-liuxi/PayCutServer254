package com.mondial.psap.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.kl.realname.entity.FourReqData;
import com.dhb.kl.realname.entity.KLReq;
import com.dhb.kl.realname.entity.SixReqData;
import com.dhb.kl.util.DES;
import com.dhb.kl.util.RSA;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;



public class Main {
	public static void sign(KLReq req){
		String reqData = req.getReqData();
		if(req==null){
			return;
		}
		Gson g = new Gson();
		 String key = "DHd1B4ZF";//"SD8JK29E";
		 DES des=new DES(key);
		 try {

			 String data = g.toJson(req.getMap());
			 System.out.println(data);
			 data=des.encrypt(data);
			 System.out.println(data);
			 req.setReqData(data);
			 String sign=RSA.sign(data, PropFileUtil.getByFileAndKey("lk.properties", "PRIVATE_KEY"));
			 req.setSign(sign);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	/**
//	 * 设置签名
//	 * 
//	 * @param paramMap
//	 */
//	protected static void setSignature(Map<String, String> paramMap) {
//		String key = ResourceUtil.getString("an", "key");
//		String signMethod = paramMap.get("signMethod");
//
//		Set<String> removeKey = new HashSet<String>();
//		removeKey.add("signMethod");
//		removeKey.add("signature");
//		String signMsg = SignUtil.getSignMsg(paramMap, removeKey);
//		String signature = SignUtil.sign(signMethod, signMsg, key, "UTF-8");
//		paramMap.put("signature", signature);
//	}
//	// 对内容做Base64加密
//		private static final String[] base64Keys = new String[] { "subject", "body", "remark" };
//		// 对内容做Base64加密， 所有子域采用json数据格式
//		private static final String[] base64JsonKeys = new String[] { "customerInfo", "accResv", "riskRateInfo", "billQueryInfo",
//				"billDetailInfo" };
//	protected static void converData(Map paramMap) {
//		for (int i = 0; i < base64Keys.length; i++) {
//			String key = base64Keys[i];
//			String value = (String) paramMap.get(key);
//			if (StringUtils.isNotEmpty(value)) {
//				try {
//					String text = new String(Base64.encode(value.getBytes("UTF-8")));
//					// 更新请求参数
//					paramMap.put(key, text);
//				} catch (Exception e) {
//				}
//			}
//		}
//		for (int i = 0; i < base64JsonKeys.length; i++) {
//			String key = base64JsonKeys[i];
//			String value = (String) paramMap.get(key);
//			if (StringUtils.isNotEmpty(value)) {
//				try {
//					String text = new String(Base64.encode(value.getBytes("UTF-8")));
//					// 更新请求参数
//					paramMap.put(key, text);
//				} catch (Exception e) {
//				}
//			}
//		}
//	}
//	/**
//	 * 往渠道发送数据
//	 * 
//	 * @param url
//	 *            通讯地址
//	 * @param paramMap
//	 *            发送参数
//	 * @return 应答消息
//	 */
//	protected String sendMsg(String url, Map<String, String> paramMap) {
//		try {
//			HttpClient http = new HttpSSLClient(url, "60000");
//			http.setRequestMethod("POST");
//			http.connect();
//			// 转换参数格式
//			String webForm = getWebForm(paramMap);
//			http.send(webForm.getBytes());
//			byte[] rspMsg = http.getRcvData();
//			String msg = new String(rspMsg, "utf-8");
//			LogFactory.getLog().info(this, msg);
//			return msg;
//		} catch (Exception e) {
//			LogFactory.getLog().error(e, e.getMessage());
//		}
//		return null;
//	}
//	private static final String URL_PARAM_CONNECT_FLAG = "&";
//	/**
//	 * 将map转化为形如key1=value1&key2=value2...
//	 * 
//	 * @param map
//	 * @return
//	 */
//	protected static String getWebForm(Map<String, String> map) {
//		if (null == map || map.keySet().size() == 0) {
//			return "";
//		}
//
//		StringBuffer url = new StringBuffer();
//		for (Map.Entry<String, String> entry : map.entrySet()) {
//			String value = entry.getValue();
//			String str = (value != null ? value : "");
//			try {
//				str = URLEncoder.encode(str, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			url.append(entry.getKey()).append("=").append(str).append(URL_PARAM_CONNECT_FLAG);
//		}
//
//		// 最后一个键值对后面的“&”需要去掉。
//		String strURL = "";
//		strURL = url.toString();
//		if (URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
//			strURL = strURL.substring(0, strURL.length() - 1);
//		}
//		return (strURL);
//	}
//	public Map parseMsg(String msg) {
//		Map map = SignUtil.parseResponse(msg);
//		// 特殊字段base64解码
//		for (Iterator iterator = base64Key.iterator(); iterator.hasNext();) {
//			String key = (String) iterator.next();
//			String value = (String) map.get(key);
//			if (StringUtils.isNotEmpty(value)) {
//				try {
//					String text = new String(Base64.decode(value.toCharArray()), "UTF-8");
//					map.put(key, text);
//				} catch (Exception e) {
//				}
//			}
//		}
//		return map;
//	}
//	public static final Set<String> base64Key = new HashSet<String>();
//	static {
//		base64Key.add("subject");
//		base64Key.add("body");
//		base64Key.add("remark");
//		base64Key.add("customerInfo");
//		base64Key.add("accResv");
//		base64Key.add("riskRateInfo");
//		base64Key.add("billpQueryInfo");
//		base64Key.add("billDetailInfo");
//		base64Key.add("respMsg");
//		base64Key.add("resv");
//	}
//	public boolean verifySign(Map paramMap) {
//		// 计算签名
//		Set<String> removeKey = new HashSet<String>();
//		removeKey.add("signMethod");
//		removeKey.add("signature");
//		String signedMsg = SignUtil.getSignMsg(paramMap, removeKey);
//		String signMethod = (String) paramMap.get("signMethod");
//		String signature = (String) paramMap.get("signature");
//		// 密钥
//		String key = ResourceUtil.getString("an", "key");
//		return SignUtil.verifySign(signMethod, signedMsg, signature, key, "UTF-8");
//	}
//	public void quickPay(){
//		Map<String,String> req = new HashMap<String, String>();
//		req.put("signMethod", "MD5");
//		req.put("version", "1.0.0");
//		req.put("txnType", "01");
//		req.put("txnSubType", "01");
//		req.put("bizType", "000000");
//		req.put("accessType", "0");
//		req.put("accessMode", "01");
//		req.put("merId", "200000000000001");
//		req.put("merOrderId", System.currentTimeMillis()+"");
//		req.put("accNo", "6217001210053039000");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("certifTp", "01");
//		map.put("certify_id", "310107198303312116");
//		map.put("customerNm", "孙俊");
//		map.put("phoneNo", "13916856042");
//		
//		map.put("expired", "1912");
//		map.put("cvv2", "123");
//		
//		String json = new Gson().toJson(map);
//		req.put("customerInfo", json);
//		req.put("txnTime", "20160114153312");
//		req.put("txnAmt", "1");
//		req.put("currency", "CNY");
//		req.put("frontUrl", "");
//		req.put("backUrl", "http://106.2.217.58:8080/AnBackReturnServlet");
//		req.put("payTimeOut", "");
//		req.put("payType", "0002");
//		req.put("subject", "");
//		req.put("body", "");
//		req.put("accType", "01");
//		req.put("merResv1", "");
//		System.out.println("\r\n"+req);
//		// 设置签名
//		setSignature(req);
//		// 特殊字段数据转换
//		converData(req);
//		String msg = sendMsg(PropFileUtil.getByFileAndKey("an.properties", "bas.rootUrl"), req);
//		Map rmap = parseMsg(msg);
//		System.out.println("base64解码后：" + StringUtils.toString(rmap));
//		boolean result = verifySign(rmap);
//		System.out.println("签名验证结果：" + result);
//		System.out.println(msg);
//	}
	public static void main(String[] args) {
		//六要素
		KLReq req = new KLReq();
   		SixReqData reqData = new SixReqData();
   		reqData.setIdCardCore("411123198610234511");
   		reqData.setName("李四");
   		reqData.setAccountNo("6258091370055836");
   		reqData.setBankPreMobile("15810452302");
   		reqData.setCvn2("697");
   		reqData.setValidityTerm("1804");
   		req.getMap().put("idCardCore", "411123198610234511");
   		req.getMap().put("name", "李四");
   		req.getMap().put("accountNo", "6258091370055836");
   		req.getMap().put("bankPreMobile", "15810452302");
   		req.getMap().put("cvn2", "697");
   		req.getMap().put("validityTerm", "1804");
   		
   		Gson g = new Gson();
   		String reqDataJson = g.toJson(reqData);
   		req.setReqData(reqDataJson);
   		sign(req);
   		req.setCustomerId("201512300000000001");
   		req.setPrdGrpId("bankCardQuery");
   		req.setPrdId("qryBankCardBy6Element");
   		System.out.println("req="+req);
		String context= g.toJson(req);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("https://access.kaolazhengxin.com:8453/authentication.do?_t=json");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		param.setContext(context);
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", "201512300000000001");
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId","bankCardQuery");
		params.put("prdId","qryBankCardBy6Element");
		param.setParams(params);
		System.out.println("报文"+params);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
		
		//四要素
		/*KLReq req = new KLReq();
   		FourReqData reqData = new FourReqData();
   		reqData.setIdCardCore("379002197511028029");
   		reqData.setName("曲爱芳");
   		reqData.setAccountNo("6230780100010629294");
   		reqData.setBankPreMobile("18053597768");
   		req.getMap().put("idCardCore", "379002197511028029");
   		req.getMap().put("name", "曲爱芳");
   		req.getMap().put("accountNo", "6230780100010629294");
   		req.getMap().put("bankPreMobile", "18053597768");
   		Gson g = new Gson();
   		String reqDataJson = g.toJson(reqData);
   		req.setReqData(reqDataJson);
   		sign(req);
   		req.setCustomerId("201512300000000001");
   		req.setPrdGrpId("bankCardQuery");
   		req.setPrdId("qryBankCardBy4Element");
   		System.out.println("req="+req);
		String context= g.toJson(req);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("lk.properties", "url"));
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		param.setContext(context);
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", "201603250000000002");
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId","bankCardQuery");
		params.put("prdId","qryBankCardBy4Element");
		param.setParams(params);
		System.out.println("报文"+params);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());*/
		
		
		
		//三要素
		/*KLReq req = new KLReq();
   		ThreeReqData reqData = new ThreeReqData();
   		reqData.setIdCardCore("130321198804010180");
   		reqData.setName("张三");
   		reqData.setAccountNo("6228480500861233451");
   		req.getMap().put("idCardCore", "130321198804010180");
   		req.getMap().put("name", "张三");
   		req.getMap().put("accountNo", "6228480500861233451");
   		Gson g = new Gson();
   		String reqDataJson = g.toJson(reqData);
   		req.setReqData(reqDataJson);
   		sign(req);
   		req.setCustomerId("201512300000000001");
   		req.setPrdGrpId("bankCardQuery");
   		req.setPrdId("qryBankCardBy3Element");
   	
		String context= g.toJson(req);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("lk.properties", "url"));
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		//param.setContext(context);
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", "201512300000000001");
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId","bankCardQuery");
		params.put("prdId","qryBankCardBy3Element");
		param.setParams(params);
		HttpResponser resp=send.postParamByHttpClient(param);*/
		
		
/*		String host = "http://www.lianhanghao.com/";
		try {
			//5576
			int initPage = 354;
			int oldinitPage = 354;
			int j=3;
			int i=0;
			FileWriter fw =null;
			BufferedWriter bw =null;
			while(initPage<=5576){
				if(initPage-oldinitPage==0){
					File f = new File("F:\\study\\java\\爬虫\\GuozhongCrawler-master\\src\\test\\java\\com\\guozhong\\queue\\my\\line"+j+".txt");
					fw = new FileWriter(f);
					bw = new BufferedWriter(fw);
				}
				initPage++;
				goPage(host,"index.php?bank=&key=&province=&city=&page="+initPage,bw);
				if(initPage-oldinitPage==0){
					bw.close();
					fw.close();
					j++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int initPage = 1533;
		int j=2;
		int i=0;
		i++;
		System.out.println(i);
		initPage=+i;
		System.out.println(initPage);*/
	}
	
	public static void  goPage(String host,String url ,BufferedWriter bw) throws IOException{
		HttpHelp help = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(host+url);
		HttpResponser resp = help.getByHttpClient(param);
		if(200==resp.getCode()){
			handle(host, bw, resp);
		}else{
			goPage(host,url,bw);
		}
	}

	private static void handle(String host, BufferedWriter bw,
			HttpResponser resp) throws IOException {
		String context = resp.getContent();
		if(!com.google.common.base.Strings.isNullOrEmpty(context)){
			 Document doc = Jsoup.parse(context);
			 Elements data=doc.getElementsByClass("tbdata");
			 if(data.size()>0){
				 Elements trList=data.get(0).getElementsByTag("tr");
				 for(Element trE:trList){
					 Elements tdList=trE.getElementsByTag("td");
					 if(tdList.size()==5){
						 Element id= tdList.get(0);
						 Element BankCode= tdList.get(1);
						 Element BankName= tdList.get(2);
						 Element tel= tdList.get(3);
						 Element address= tdList.get(4);
						 String line = id.html()+"\t"+BankCode.html()+"\t"+BankName.html()+"\t"+tel.html()+"\t"+address.html()+"\r";
						 bw.write(line);
						 System.out.println(line);
					 }
					 
				 }
			 }
			
			/* Elements pageList=doc.getElementsByClass("pager");
			 if(pageList.size()>0){
				 Elements aList=pageList.get(0).getElementsByTag("a");
		    	 int length =aList.size();
		    	 if(length>=2){
		    		Element aE= aList.get(length-2);
		    		if("下一页".equals(aE.html())){
		    			String nextUrl=aE.attr("href");
		    			goPage(host,nextUrl,bw);
		    		}
		    	 }
			 }*/
		}
	}
}

