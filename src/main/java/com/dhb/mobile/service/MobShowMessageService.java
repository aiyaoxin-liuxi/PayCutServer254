package com.dhb.mobile.service;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.mobile.entity.ShortMessage;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
@Service
public class MobShowMessageService {
	private Logger logger = Logger.getLogger(MobShowMessageService.class);
	private String short_message_url = PropFileUtil.getByFileAndKey("short_message.properties", "short_message_url");
	@Autowired
	private CommonObjectDao commonObjectDao;
	/**
	 * 发送短信接口
	 * @author pyc
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public String getSendShowMessage(Map<String,Object> map) throws Exception{
		String tranNo = map.get("tranNo").toString();
		map.remove("tranNo");
		Gson g = new Gson();
		String json= g.toJson(map);
		logger.info("收到请求(短信接口)：服务端->短信服务商(" + tranNo + ")参数：" + json);
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(short_message_url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		logger.info("收到请求(短信接口)：短信服务商->服务端(" + tranNo + ")参数：" + resp.getContent());
		
		return resp.getContent();
	}
	/**
	 * 生产六位随机数
	 */
	public static String getSixRandom(){
		int[] array = {0,1,2,3,4,5,6,7,8,9};
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
		    int index = rand.nextInt(i);
		    int tmp = array[index];
		    array[index] = array[i - 1];
		    array[i - 1] = tmp;
		}
		int result = 0;
		for(int i = 0; i < 6; i++){
			result = result * 10 + array[i];
		}
		return String.valueOf(result);
	}
	/**
	 * 存入业务数据 
	 */
	@Transactional
	public ShortMessage saveShortMessage(String tranNo,String mobiles,String randnum,Date createdTime){
		ShortMessage sm = new ShortMessage();
		sm.setTranNo(tranNo);
		sm.setMobiles(mobiles);
		sm.setRandnum(randnum);
		sm.setCreatedTime(createdTime);
		String insertSql ="INSERT INTO DHB_SHORT_MESSAGE T ( TRANNO, MOBILES, RANDNUM, CREATEDTIME ) VALUES(:tranNo,:mobiles,:randnum,:createdTime)";
		commonObjectDao.saveOrUpdate(insertSql, sm);
		return sm;
	} 
	
	/**
	 * 验证手机号，银行卡号及手机验证码有效期
	 */
	
}
