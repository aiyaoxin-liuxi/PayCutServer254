package com.dhb.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.service.DhbMobileOrderDao;
import com.dhb.dao.service.UserInfoDao;
import com.dhb.entity.DhbMobileOrder;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.UserInfo;
import com.dhb.entity.exception.CutException;
import com.dhb.mobile.entity.InQuan;
import com.dhb.mobile.entity.MobileResp;
import com.dhb.mobile.entity.OutQuan;
import com.dhb.mobile.entity.ShortMessage;
import com.dhb.mobile.service.MobRechargeService;
import com.dhb.mobile.service.MobShowMessageService;
import com.dhb.service.ErrorService;
import com.dhb.service.ValidateService;
import com.dhb.util.DateUtil;
import com.dhb.util.JsonUtil;
import com.dhb.util.StringUtil;
import com.dhb.util.XmlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
/**
 * 手机充值控制器
 * @author wxw
 *
 */
@Controller
@RequestMapping(value="/dhbmobile",produces = {"application/json;charset=UTF-8"})
public class DHBMobileRechController {
	
	public static Logger logger = Logger.getLogger(DHBMobileRechController.class);
	@Resource
	private ErrorService errorService;
	@Resource
	private DhbMobileOrderDao dhbMobileOrderDao;
	@Resource
	private MobRechargeService mobRechargeService;
	@Resource
	private ValidateService validateService;
	@Resource
	private MobShowMessageService mobShowMessageService;
	@Resource
	private UserInfoDao userInfoDao;
	
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/mobileRecharge")
	public @ResponseBody String mobileRecharge(@RequestBody String json){
		logger.info("SJCZ 钱包上送的报文："+json);
		try{
			Gson g = new Gson();
			OutRequestInfo resInfo=g.fromJson(json, OutRequestInfo.class);
			json = resInfo.getComments();
			String cxjson = resInfo.getBatchId();
			if(StringUtil.isEmpty(json)||StringUtil.isEmpty(cxjson)){
				logger.error("上送报文为空");
				return null;
			}
			String baowen = json.substring(76);
			String baowenxml = new String(Base64.decodeBase64(baowen.getBytes()));
			logger.info("SJCZ 【钱包手机充值接口】报文解析成xml："+baowenxml);
			
			InQuan quan = (InQuan) XmlUtil.xmltoObject(baowenxml,InQuan.class);
			
			///////////////////////////
			String accId = quan.getQuanbody().getAccount().getId();
			logger.info("提现acctId："+accId);
	    	List<UserInfo> users = userInfoDao.getUserInfoByaccId(accId);
	    	if(users.size()<=0){
	    		logger.error("该用户未注册，accId="+accId);
	    		return null;
	    	}else if(users.size()>1){
	    		logger.error("该用户有多人注册，accId="+accId);
	    		return null;
	    	}
	    	UserInfo user = users.get(0);
	    	if(!"2".equals(user.getUSER_STAT())){
	    		logger.error("该用户未审核通过，userId="+user.getUSER_ID());
	    		return null;
	    	}
			//////////////////////////////
	    	
			if(!mobRechargeService.validateMobile(quan)){
				logger.error("手机号为空");
				return null;
			}
			DhbMobileOrder moborder = dhbMobileOrderDao.saveMobileOrder(quan);
			MobileResp rsp = mobRechargeService.mobileRecharge(json,quan,moborder,cxjson);
		    return zuzhuangBackbw(cxjson,rsp);
		}catch(JsonSyntaxException e){
			e.printStackTrace();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public String zuzhuangBackbw(String cxjson,MobileResp rsp){
		OutQuan outquan = mobRechargeService.createOutQuan(cxjson,rsp.getTicket());
		
		outquan.getQuanbody().getResp().setRespCode(rsp.getCode());
		outquan.getQuanbody().getResp().setRespInfo(rsp.getMessage());
		String resxml=XmlUtil.ObjectToXml(outquan).replace(" standalone=\"yes\"", "");
		resxml = resxml.replaceAll("\n", "").replaceAll(" ", "").trim();
		resxml = resxml.replaceFirst("version", " version");
		resxml = resxml.replaceFirst("encoding", " encoding");
		logger.info("SJCZ 返回给钱包客户端的XML："+resxml);
		String enCodeXml = "";
		try {
			enCodeXml = new String(Base64.encodeBase64(resxml.getBytes("UTF-8"))).trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int len=enCodeXml.length()+71;
		String baowen = addZeroForNum(len+"", 5)+addZeroForNum("", 71)+enCodeXml;
		logger.info("SJCZ 返回给钱包客户端的报文："+baowen);
		return baowen;
	}
	public static String addZeroForNum(String str, int strLength) {
	int strLen = str.length();
	if (strLen < strLength) {
		while (strLen < strLength) {
			StringBuffer sb = new StringBuffer();
			sb.append(str).append(" ");//左补0
//    		sb.append(str).append(" ");//右补0
			str = sb.toString();
			strLen = str.length();
		}
	}
	return str;
 }
	/**
	 * @author pyc
	 * @param mobiles
	 * @param flag
	 * @param randnum
	 * @return json 
	 * @throws Exception 
	 * @throws CutException 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/shortMessage")
	public @ResponseBody String shortMessage(@RequestBody String json) throws Exception{
		long l = System.currentTimeMillis();
		Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
		Map<String,Object> jsonRetMap = null;
		//验证接收参数
		String ret = validateService.validateShowMessageIsNull(jsonMap);
		if(ret.equals("success")){
			String tranNo = jsonMap.get("tranNo").toString();
			String mobiles = jsonMap.get("mobiles").toString();
			String strDate = DateUtil.format(new Date());
			Date createdTime = DateUtil.strToDate(strDate);
			String randnum = mobShowMessageService.getSixRandom();
			String type = jsonMap.get("type").toString();
			logger.info("收到请求(短信接口)：客户端->服务端(" + tranNo + ")参数：" + json);
			try{
				String jsonRet = mobShowMessageService.getSendShowMessage(jsonMap);
				jsonRetMap = JsonUtil.getJsonToMap(jsonRet);
				if(jsonRetMap.get("retCode").toString().equals("0000")){
					//增加标识区分不同业务（纯短信、代扣、其他）---以后做
					if("0".equals(type)){//手机验证码业务
						//保存业务数据 
						ShortMessage sm = mobShowMessageService.saveShortMessage(tranNo, mobiles, randnum, createdTime);
						if(sm != null){
							logger.info("短信接口保存数据成功");
						}
					}
		    		//发送成功
					jsonRetMap = JsonUtil.getReturnMessageHead(tranNo,DhbTranStatus.Succ.getCode(),DhbTranStatus.Succ.getDescription());
					logger.info("收到请求(短信接口)：服务端->客户端(" + tranNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				    return JsonUtil.getMapToJson(jsonRetMap);
				}else{
					//发送失败
					jsonRetMap = JsonUtil.getReturnMessageHead(tranNo,DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
					logger.info("收到请求(短信接口)：服务端->客户端(" + tranNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				    return JsonUtil.getMapToJson(jsonRetMap);
				}
				
			}catch(JsonSyntaxException e){
				logger.error("发送短信接口转换JSON异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(tranNo,DhbTranStatus.Fail.getCode(),"translate request json error");
				return JsonUtil.getMapToJson(jsonRetMap);
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				logger.error("发送短信接口接收参数异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(tranNo,DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
				return JsonUtil.getMapToJson(jsonRetMap);
			}
		}
		logger.info("耗时：短信接口("+ jsonMap.get("tranNo").toString() +"):"+(System.currentTimeMillis()-l));
		return ret;
	}
}
