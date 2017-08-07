package com.dhb.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.entity.BaseUser;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.service.QrCodeUtil;
import com.dhb.util.MD5;
import com.dhb.util.Tools;
import com.dhb.util.WebContextHolder;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;
import com.dhb.nfc.entity.HttpClient;

@Controller
@RequestMapping()
public class UserController {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Log logger = LogFactory.getLog(UserController.class);
	public static Map<String,String> map = Maps.newConcurrentMap();

	@RequestMapping(value="/login")
	public String login(BaseUser user, Model model){
		String userName = user.getUserName();
		String password = user.getPassword();
		if("zhj".equals(userName)&&"123".equals(password)){
			HttpSession session = WebContextHolder.getCurrentSession();
			session.setAttribute(WebContextHolder.currentUser, user);
    		String token=  Tools.getUUID();
    		session.setAttribute(WebContextHolder.currentToken, token);
    		map.put(userName, token);
    		return "redirect:/index.jsp";
		}else{
			model.addAttribute("error", "用户名或密码错误");
			model.addAttribute("user", user);
			return "login";
		}
	}
	@RequestMapping(value="/logout")
	public String logout(){
		WebContextHolder.removeSession();
		return "redirect:/login.jsp";	
	}
	@RequestMapping(value="/channelPay",method=RequestMethod.POST)
	public void channelPay(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String orderId = "pan_tesi"+System.currentTimeMillis();
		int fee = 1;//分
		String time = sdf.format(new Date());
		int payType = 0;
		Map<String,Object> dataMap = new LinkedHashMap<String, Object>();
		dataMap.put("orderId", orderId);
		dataMap.put("fee", fee);
		dataMap.put("time", time);
		dataMap.put("payType", payType);
		
		String data = QrCodeUtil.getBuildPayParams(dataMap);
		logger.info("(全晶公众号支付接口：)订单号："+orderId+",data数据："+data);
		logger.info("(全晶公众号支付接口：)订单号："+orderId+",data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		logger.info("(全晶公众号支付接口：)订单号："+orderId+",sign加密数据："+MD5.encrypt(data));
		Map<String,String> map = new LinkedHashMap<String, String>();
		map.put("mId", YSBUtil.getReadProperties("nfc","qj.mId"));
		map.put("data", AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		map.put("sign", MD5.encrypt(data));
		map.put("resType", YSBUtil.getReadProperties("nfc","qj.resType"));
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("nfc","qj.channelPayUrl"), map);
//		logger.info("(全晶公众号支付接口：)订单号："+orderId+",通道返回数据："+msg);
		
		HttpClient http=new HttpClient (response);
		http.setParameter("mId", map.get("mId"));
		http.setParameter("data", map.get("data"));
		http.setParameter("sign", map.get("sign"));
		http.setParameter("resType", map.get("resType"));
		String url = YSBUtil.getReadProperties("nfc","qj.channelPayUrl");
		http.sendByPost(url);
//		request.getSession().setAttribute("map", map);
//		return "redirect:/pay.jsp";
	
	}
}
