package com.dhb.quartz;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dhb.dao.service.ChannelInfoDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.entity.ChannelInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.service.PayCutInterface;
import com.dhb.util.SpringContextHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class QueryTranStatusService {
	private static final Log logger = LogFactory.getLog(QueryTranStatusService.class);
	
	public void execute() {
		logger.info("==============================进入定时器==============================");
		try{
			List<DhbBizJournal> list=SpringContextHelper.getInstance().getBean(DhbBizJournalDao.class).getHandingJournal();
			List<String> batchIdlists = Lists.newArrayList();
			for(DhbBizJournal journal:list){
				String bigChannelId =journal.getBigChannelId(); 
				if(!Strings.isNullOrEmpty(bigChannelId)){
					ChannelInfo channelInfo=SpringContextHelper.getInstance().getBean(ChannelInfoDao.class).getBigChannelByChannelId(bigChannelId);
					handleQuery(batchIdlists,channelInfo.getBeanName(),journal);
				}else{
					String channelId = journal.getChannelId();
					if(!Strings.isNullOrEmpty(channelId)){
						String beanName =SpringContextHelper.getInstance().getBean(ChannelInfoDao.class).getBeanNameByChannelId(channelId);
						handleQuery(batchIdlists,beanName,journal);
					}
					
				}
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	
		
	}
	
	private void handleQuery(List<String> batchIdlists,String beanName,DhbBizJournal journal){
		if(!Strings.isNullOrEmpty(beanName)){
			PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
			String batchId = journal.getBatchId();
			if(!Strings.isNullOrEmpty(batchId)){
				if(batchIdlists.contains(batchId)){
					return;
				}
				batchIdlists.add(batchId);
			}
			service.queryTranStatus(journal);
		}
	}

}
