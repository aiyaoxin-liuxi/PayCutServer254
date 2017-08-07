package com.dhb.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.nfc.service.YaColQrCodePayService;

public class NFCrefundQueryService {
	@Autowired
	private DhbBizJournalDao DhbBizJournaldao;
	@Resource
	private YaColQrCodePayService wechatPayService;
	
	private static final Log logger = LogFactory.getLog(NFCrefundQueryService.class);
	public void status(){
		logger.info("==============================进入NFC退货订单查询定时器==============================");
		try{
			//退货
			List<NfcOrderWater> list = DhbBizJournaldao.getRefundWaterList();
			logger.info("==============================NFC退货订单查询定时任务准备执行的数量："+list.size());
			for(NfcOrderWater water : list){
				if(water.getMerchChannel().equals(Constants.nfc_merch_channel_yacol)){
					wechatPayService.qrCodeRefundQuery(water);
				}else if(water.getMerchChannel().equals(Constants.nfc_merch_channel_hrt)){
					//
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


}
