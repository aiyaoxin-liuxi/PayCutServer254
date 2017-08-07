package com.dhb.quartz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.nfc.service.HrtQrCodePayService;
import com.dhb.nfc.service.YaColQrCodePayService;
import com.dhb.service.ValidateService;
import com.dhb.util.SpringContextHelper;

public class NFCqueryService {
	@Autowired
	private DhbBizJournalDao DhbBizJournaldao;
	@Resource
	private YaColQrCodePayService wechatPayService;
	@Resource
	private HrtQrCodePayService hrtWechatPayService;
	@Resource
	private ValidateService validateService;
	
	private static final Log logger = LogFactory.getLog(NFCqueryService.class);
	SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void status(){
		logger.info("==============================进入NFC交易查询定时器==============================");
		try{
			//主扫被扫支付
			List<NfcOrderWater> list = DhbBizJournaldao.getNfcOrderWaterList();
			logger.info("==============================NFC交易查询定时任务准备执行的数量："+list.size());
			for(NfcOrderWater water : list){
				//未支付订单暂定5分钟超时
//				if(water.getStatus().equals(Constants.nfc_pay_status_3)){//未支付
//					Date date = new Date();
//					String start_Time = sdf.format(date); 
//					Date startTime =  sdf.parse(start_Time);
//					Date endTime = water.getCreatedTime();
//					long time = (startTime.getTime() - endTime.getTime())/(1000);
//					if(time > 300){//超时
//						water.setStatus(Constants.nfc_pay_status_19);
//						water.setMessage(Constants.nfc_pay_status_19_context);
//						validateService.getUpateNfcOrderWater(water);
//					}else{
//						wechatPayService.wechatTradeQuery(water);
//					}
//				}else{
//					wechatPayService.wechatTradeQuery(water);
//				}
				//未支付订单暂定半小时超时
				Date date = new Date();
				String start_Time = sdf.format(date); 
				Date startTime =  sdf.parse(start_Time);
				Date endTime = water.getCreatedTime();
				long time = (startTime.getTime() - endTime.getTime())/(1000);
				if(time > 1800){//中互联自动改为超时
					water.setEndTime(startTime);
					water.setStatus(Constants.nfc_pay_status_19);
					water.setMessage(Constants.nfc_pay_status_19_context_ZHL);
					validateService.getUpateNfcOrderWater(water,null);
				}else{
					if(water.getMerchChannel() != null){
						if(water.getMerchChannel().equals(Constants.nfc_merch_channel_hrt)){
							hrtWechatPayService.qrCodeQuery(water);
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) throws ParseException {
//		NFCqueryService nc = new NFCqueryService();
//		nc.status();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = new Date();
//		String start_Time = sdf.format(date); 
//		Date startTime =  sdf.parse(start_Time);
		
		String  a = "2016-08-09 15:28:00";
		String  b = "2016-08-09 15:28:30";
		Date aa = sdf.parse(a);
		Date bb = sdf.parse(b);
		long l = (bb.getTime() - aa.getTime())/(1000);
		System.out.println(l);
		
	}

}
