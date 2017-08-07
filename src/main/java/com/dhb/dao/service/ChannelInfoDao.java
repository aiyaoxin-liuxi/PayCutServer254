package com.dhb.dao.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.controller.DHBPayController;
import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.ChannelInfo;
import com.dhb.entity.OutRequestInfo;
import com.google.common.base.Strings;

@Service
public class ChannelInfoDao {
	private static final Log logger = LogFactory.getLog(DHBPayController.class);

	@Autowired
	CommonObjectDao commonObjectDao;
	
	
	
	public String getBeanNameByChannelId(String channelId){
		String sql = "select beanName from dhb_channel_info where channelId=:channelId";
		return commonObjectDao.findSingleVal(sql, new Object[]{channelId});
	}
	
	public String getBeanName(OutRequestInfo info){
		String channelId = info.getChannelId();
		String merchId = info.getMerchId();
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(channelId)){
			return null;
		}
		String checkChannel ="select c.beanName from dhb_merch_channel a,dhb_channel_info c "
				+ "where a.channelId=c.channelId and a.merchid=:merchId and a.channelId =:channelId";
		String beanName=commonObjectDao.findSingleVal(checkChannel, new Object[]{merchId,channelId});
		logger.debug("beanName:"+beanName +",merchId:"+merchId  + ", channelId:"+channelId);
	
		if(!Strings.isNullOrEmpty(beanName)){
			return beanName;
		}
		ChannelInfo channelInfo=getBigChannelByMerchId(merchId);
		if(channelInfo!=null){
			info.setBigChannelId(channelInfo.getChannelId());
			return channelInfo.getBeanName();
		}
		return null;
	}
	
	public ChannelInfo getBigChannelByMerchId(String merchId){
		if(Strings.isNullOrEmpty(merchId)){
			return null;
		}
		String sql = "select c.beanName,c.channelId from dhb_merch_channel mc,dhb_channel_info c "
				+ "where mc.channelid=c.channelid and mc.merchid=:merchId ";
		 List<ChannelInfo> info=commonObjectDao.findList(sql, ChannelInfo.class, new Object[]{merchId});
		 if(info.size()>0){
			 return info.get(0);
		 }
		return null;
	}
	//getBigChannelByMerchId在这个的基础上变化而来
	public ChannelInfo getBigChannelByMerchIdAndChannelId(String merchId){
		if(Strings.isNullOrEmpty(merchId)){
			return null;
		}
		String sql = "select c.beanName,c.channelId from dhb_merch_channel mc,dhb_channel_info c "
				+ "where mc.channelid=c.channelid and mc.merchid=:merchId ";
		 List<ChannelInfo> info=commonObjectDao.findList(sql, ChannelInfo.class, new Object[]{merchId});
		 if(info.size()>0){
			 return info.get(0);
		 }
		return null;
	}
	public ChannelInfo getBigChannelByChannelId(String channelId){
		if(Strings.isNullOrEmpty(channelId)){
			return null;
		}
		String sql = "select c.beanName,c.channelId dhb_channel_info c "
				+ "where c.channelid=:channelId ";
		
		return commonObjectDao.findOneObject(sql, ChannelInfo.class, new Object[]{channelId});
	}
	public String getBeanName(BatchTranReq resInfo) {
		String channelId = resInfo.getChannelId();
		String merchId = resInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId)||Strings.isNullOrEmpty(channelId)){
			return null;
		}
		String checkChannel ="select c.beanname from dhb_merch_channel mc,dhb_channel_info c "
				+ "where mc.channelid=c.channelid and mc.merchid=:merchId and mc.channelid =:channelid";
		String beanName=commonObjectDao.findSingleVal(checkChannel, new Object[]{merchId,channelId});
		if(!Strings.isNullOrEmpty(beanName)){
			return beanName;
		}
		ChannelInfo channelInfo=getBigChannelByMerchId(merchId);
		if(channelInfo!=null){
			resInfo.setBigChannelId(channelInfo.getChannelId());
			return channelInfo.getBeanName();
		}
		return null;
	}
}
