package com.dhb.quartz;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcQjWater;
import com.dhb.nfc.service.QrCodeUtil;
import com.dhb.nfc.service.YaColQrCodePayService;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.util.MD5;
import com.dhb.util.Tools;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;

public class NFCQjTransBillService {
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private DhbBizJournalDao DhbBizJournaldao;
	@Resource
	private YaColQrCodePayService wechatPayService;
	
	private static final Log logger = LogFactory.getLog(NFCQjTransBillService.class);
	public String status(String dateTime){
		logger.info("==============================进入NFC全通支付对账查询定时器==============================");
		logger.info("dateTime="+dateTime);
		String returnString = "";
		String date = "";
		try{
			if(dateTime.equals("quartz")){//说明这是定时任务执行传入的
				//计算需要查询的日期
				Date beginDate = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(beginDate);
				calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
				date = DateUtil.getDayForYYMMDD(calendar.getTime());
				logger.info("date="+date);
			}else{//人工调用传入
				date = dateTime; 
				logger.info("date="+date);
			}
			//组装请求报文
			Map<String,Object> dataMap = new HashMap<String, Object>();
			dataMap.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));
//			dataMap.put("date","20170119");
			dataMap.put("date",date);
			String sign = dataMap.get("cooperatorId").toString() + dataMap.get("date").toString();
			dataMap.put("sign", MD5.encrypt(sign));
			dataMap.put("resType", YSBUtil.getReadProperties("nfc","qj.resType"));
			String json = JsonUtil.getMapToJson(dataMap);
			logger.info("(全通支付对账查询接口：),发送给通道的数据："+json);
			HttpHelp send = new HttpHelp();
			HttpRequestParam param = new HttpRequestParam();
			param.setUrl(YSBUtil.getReadProperties("nfc","qj.transBill"));
			Map<String,String> heads = Maps.newHashMap();
			heads.put("Content-Type", "text/json;charset=UTF-8");
			param.setContext(json);
			param.setHeads(heads);
			HttpResponser resp=send.postParamByHttpClient(param);
			logger.info("(全通支付对账查询接口：),通道返回结果："+resp.getContent());
			if(resp.getContent() != null && !"".equals(resp.getContent())){
				//如果返回的是json格式，说明无对账数据，对账数据的格式应为简报文格式
				if(QrCodeUtil.isGoodJson(resp.getContent()) == true){
					logger.info("(全通支付对账查询接口：),通道返回结果："+resp.getContent()+",对账数据不是简报文格式，无对账数据！！！");
					Map<String,Object> jsonMap = JsonUtil.getJsonToMap(resp.getContent());
					returnString = "{\"result_code\":\"9\",\"message\":"+jsonMap.get("msg")+"}";
				}else{
					//预防数据重复，先删除再插入
					NfcQjWater delQjWater = new NfcQjWater();
//					delQjWater.setSettleDate("20170119");
					delQjWater.setSettleDate(date);
					String deleteSql = "DELETE FROM DHB_NFC_QJ_WATER WHERE settleDate=:settleDate";
					commonObjectDao.saveOrUpdate(deleteSql, delQjWater);
					String[] transBillRet = resp.getContent().split("########");
					for(int i = 0 ; i < transBillRet.length ; i++){
						String[] retValue = transBillRet[i].split("\\|");
						NfcQjWater qjWater = new NfcQjWater();
						qjWater.setId(Tools.getUUID());
						qjWater.setCooperatorId(retValue[0]);
						qjWater.setmId(retValue[1]);
						qjWater.setQtMsgId(retValue[2]);
						qjWater.setAmount(new BigDecimal(retValue[3]));
						qjWater.setSettleDate(retValue[4]);
						qjWater.setRespType(retValue[5]);
						qjWater.setShfee(new BigDecimal(retValue[6]));
						String insertSql = "INSERT INTO DHB_NFC_QJ_WATER (id,cooperatorId,mId,qtMsgId,amount,settleDate,respType,shfee) VALUES(:id,:cooperatorId,:mId,:qtMsgId,:amount,:settleDate,:respType,:shfee)";
			   			commonObjectDao.saveOrUpdate(insertSql, qjWater);
					}
					returnString = "{\"result_code\":\"1\",\"message\":\"导入成功\"}";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnString;
	}
}
