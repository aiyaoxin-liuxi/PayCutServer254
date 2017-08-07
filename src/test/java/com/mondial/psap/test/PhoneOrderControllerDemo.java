package com.mondial.psap.test;

import java.util.Date;
import java.util.Map;

import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.mobile.util.HttpHelper;
import com.dhb.mobile.util.MD5Util;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.google.common.collect.Maps;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by admin on 14-11-17.
 */
public class PhoneOrderControllerDemo {

    public static void main(String[] args) {
    	String s= "PD94bWx2ZXJzaW9uPSIxLjAiZW5jb2Rpbmc9IlVURi04Ij8+PFF1YW4+PFF1YW5ib2R5PjxNc2c+PHZlcnNpb24+MS4wLjA8L3ZlcnNpb24+PHR5cGU+MTAwMzwvdHlwZT48ZmxhZz4wMDwvZmxhZz48L01zZz48QWNjb3VudD48aWQ+NjIyNzAwMDAwMDAwMDAyMzI0MzwvaWQ+PHR5cGU+MDAwMTwvdHlwZT48YXV0aE1vZGU+MTwvYXV0aE1vZGU+PC9BY2NvdW50PjxQdXJjaGFzZT48YWNxQklOPjwvYWNxQklOPjxkYXRlPjIwMTYwMjAxMDU1NDU5PC9kYXRlPjx0cmFjZU51bT4xMjM0NTY8L3RyYWNlTnVtPjxjdXJyZW5jeT4xNTY8L2N1cnJlbmN5Pjx0cmFuc0FtdD4wMDAwMDAwMDAwMTA8L3RyYW5zQW10PjxvcmRlckluZm8+PC9vcmRlckluZm8+PG1lcklkPjkzOTMxMDAxMDAzMDAwMTwvbWVySWQ+PG9yZGVyTnVtPjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDE8L29yZGVyTnVtPjwvUHVyY2hhc2U+PFJlc3A+PHJlc3BDb2RlPjkwMTwvcmVzcENvZGU+PHJlc3BJbmZvPumSseWMheWQjuWPsOi/lOWbnue7k+aenOS4uuepujwvcmVzcEluZm8+PC9SZXNwPjwvUXVhbmJvZHk+PC9RdWFuPg==";
		System.out.println(new String(Base64.decodeBase64(s.getBytes())));
    	String xml="01311                                                                       PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48UXVhbj48UXVhbmJvZHk+PE1zZz48dmVyc2lvbj4xLjAuMDwvdmVyc2lvbj48dHlwZT4xMDAzPC90eXBlPjxmbGFnPjAwPC9mbGFnPjwvTXNnPjxBY2NvdW50Pjx0eXBlPjAwMDE8L3R5cGU+PGlkPjYyMjcwMDAwMDAwMDAwMjMyMjQ8L2lkPjxhdXRoTW9kZT4xPC9hdXRoTW9kZT48L0FjY291bnQ+PENoSW5mbz48dXNlcklkPjEyMzQ1Njc4OTE8L3VzZXJJZD48L0NoSW5mbz48UHVyY2hhc2U+PG9yZGVyTnVtPjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDE8L29yZGVyTnVtPjx0cmFuc0FtdD4wMDAwMDAwMDA1MDA8L3RyYW5zQW10PjxhY3FCSU4+MjkwMDIwMDM8L2FjcUJJTj48bWVySWQ+OTM5MzEwMDEwMDMwMDAxPC9tZXJJZD48ZGF0ZT4yMDE2MDEzMTA4MzQyMTwvZGF0ZT48dHJhY2VOdW0+MTIzNDU2PC90cmFjZU51bT48Y3VycmVuY3k+MTU2PC9jdXJyZW5jeT48L1B1cmNoYXNlPjxleHRJbmZvPlNKQ1osMTM3NjE1MzQ3MjcsMTQwMTAxLDU8L2V4dEluZm8+PFB1YktleUluZGV4PjAyMDwvUHViS2V5SW5kZXg+PFRpY2tldD44MDQ0MzMxMzExMTExMTExMzI5MDAyMDAzMjAxNjAxMzEyMDMzMjk8L1RpY2tldD48L1F1YW5ib2R5PjxTZWN1cmVEYXRhPkJGRkI2NDU2NDU1REIxMkVFNTFDN0YyMkFGNUNERDk3NzhFQ0I5RUFDQzc0OEEzMzM3MEM0NTAxODZFNDU2QUU5REQ1Rjk1MUNFMjBDNDQxMjIzQUY1ODYwMDc5RjUwNDUyMTQwNjFFRjMwNjM2RkE3MkU2NDBCNDg1QzA3MjFDRkI2RkNEQjI2NURCRTAyQzZFM0VDQUM3NDkxRThCRDlBN0Y0QTQ0QkEyNzBCNTJDRTJCODZDRkQ5RTdDNTg3MzI2N0QxQjAzNjNDODIyQkRERDJBOTI1MTgyOEJBQjlBRDFCMjA1M0E5MDgzMzhFN0IyMTI1M0RFNzA2QzExN0E8L1NlY3VyZURhdGE+PC9RdWFuPg==";
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Quan><Quanbody><Msg><version>1.0.0</version><type>1003</type><flag>00</flag></Msg><Account><type>0001</type><id>6227000000000023224</id><authMode>1</authMode></Account><ChInfo><userId>1234567891</userId></ChInfo><Purchase><orderNum>0000000000000000000000000000000000000001</orderNum><transAmt>000000000500</transAmt><acqBIN>29002003</acqBIN><merId>939310010030001</merId><date>20160131062332</date><traceNum>123456</traceNum><currency>156</currency></Purchase><extInfo>SJCZ,13761534727,140101,5</extInfo><PubKeyIndex>020</PubKeyIndex><Ticket>804361131111111132900200320160131182237</Ticket></Quanbody><SecureData></SecureData></Quan>";
    	HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhbmobile/mobileRecharge");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(xml);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
//    	
     
    	
    	top_up_phone();
       
       
       
//    	String param = "userid=A1221997&spbillid=Sp000001";
//        String url="http://api2.ofpay.com/api/query.do";
//        int readTimeout=10000;
//    	String result = HttpHelper.doHttpGetResponse(url, HttpHelper.GET, "GBK", param, readTimeout+"");
//    	System.out.println("\r\n"+result);
    }

    /**
     * 话费充值java demo
     */
    public static void top_up_phone(){

        //传参
        String userid="A1221997";
        String userpws= MD5Util.sign("donghuibao001", "GBK").toLowerCase();

        //参数值为140101（快充）或者 170101（慢充）
        String cardid="140101";

        //要充值的金额
        String cardnum="50";

        //外部订单号，唯一性
        String sporder_id=System.currentTimeMillis()+"";

        //格式：年月日时分秒 如：20141119112450
        String sporder_time=DateUtil.formatYYYYMMDDHHMMSS(new Date());

        //要充值的手机号码
        String game_userid="13021985911";

        //该参数将异步返回充值结果，若不填写该地址，则不会回调
        String ret_url="http://106.2.217.58:8080/PayCutServer/OFMobBackServlet";

        //版本号固定值
        String version="6.0";

        //若cardid=170101，需要加上下面的参数，不传该值默认为24
        String mctype="";

        //默认的秘钥是OFCARD，可联系商务修改，若已经修改过的，请使用修改过的。
        String keystr = "OFCARD";

        String md5_str_param = userid+userpws+cardid+cardnum+sporder_id+sporder_time+game_userid+keystr;
        System.out.println("MD5前："+md5_str_param);
        String md5_str = MD5Util.sign(md5_str_param,"gbk").toUpperCase();
        System.out.println("MD5后："+md5_str);
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
        String url="http://api2.ofpay.com/onlineorder.do";
        int readTimeout=10000;
        int connectTimeout=10000;

        try{
//            String result = HttpClientUtil.sendGetRequest(url,param,readTimeout,connectTimeout);
        	String result = HttpHelper.doHttpGetResponse(url, HttpHelper.GET, "GBK", param, readTimeout+"");
        	System.out.println("\r\n"+result);
            //解析欧飞返回的xml文件
            Document document = DocumentHelper.parseText(result);
            Element root = document.getRootElement();
            String retcode = root.element("retcode").getText();
            String err_msg = root.element("err_msg").getText();

            if("1".equals(retcode)){
                String orderid= root.element("orderid").getText();
                String cardids =root.element("cardid").getText();
                String cardnums =root.element("cardnum").getText();
                String ordercash =root.element("ordercash").getText();
                String cardname =root.element("cardname").getText();
                String sporder_ids =root.element("sporder_id").getText();
                String game_userids =root.element("game_userid").getText();
                String game_state =root.element("game_state").getText();

                //输出返回的xml结果
                System.out.println(retcode);
                System.out.println(err_msg);
                System.out.println(orderid);
                System.out.println(cardids);
                System.out.println(cardnums);
                System.out.println(ordercash);
                System.out.println(cardname);
                System.out.println(sporder_ids);
                System.out.println(game_userids);
                System.out.println(game_state);
            }else {
                System.out.println(retcode);
                System.out.println(err_msg);
            }

        }catch (Exception e) {
        }
    }
}
