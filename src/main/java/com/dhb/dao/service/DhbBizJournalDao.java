package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.RealNameInfo;
import com.dhb.nfc.entity.NfcOrderWater;
import com.google.common.base.Strings;

@Service
public class DhbBizJournalDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	public void updateStatusByBatchId(DhbBizJournal journal){
		
		String updateJournelSql = "update dhb_biz_journal set endTime=:endTime,"
				+ "handleStatus=:handleStatus,handleRemark=:handleRemark where  batchId=:batchId";
		commonObjectDao.saveOrUpdate(updateJournelSql, journal);
	}

	public void updateStatusById(DhbBizJournal journal){
		
		String updateJournelSql = "update dhb_biz_journal set endTime=:endTime,"
				+ "handleStatus=:handleStatus,handleRemark=:handleRemark where id=:id";
		commonObjectDao.saveOrUpdate(updateJournelSql, journal);
	
	}
	public List<DhbBizJournal> getJournalByBatchId(String merchId,String batchId){
		if(Strings.isNullOrEmpty(batchId)||Strings.isNullOrEmpty(merchId)){
			return null;
		}
		String sql = "select id,handleStatus,handleRemark,bizType,createTime from dhb_biz_journal where merchId=:merchId and batchId=:batchId";
		return commonObjectDao.findList(sql, DhbBizJournal.class, new Object[]{merchId,batchId});
	}
	
	public void insertJournal(DhbBizJournal journal){
		String tranJournalSql ="insert into dhb_biz_journal"
				+ "(id,merchId,bizType,channelId,recordId,batchId,fromBankCode,fromBankName,fromBankCardNo,fromUserName,"
				+ "fromIdentityNo,toBankCode,toBankName,toBankCardNo,toUserName,toIdentityNo,money,"
				+ "currency,memo,createTime,bigChannelId)"
				+ " values"
				+ "(:id,:merchId,:bizType,:channelId,:recordId,:batchId,:fromBankCode,:fromBankName,:fromBankCardNo,:fromUserName,"
				+ ":fromIdentityNo,:toBankCode,:toBankName,:toBankCardNo,:toUserName,:toIdentityNo,:money,"
				+ ":currency,:memo,:createTime,:bigChannelId)";
		commonObjectDao.saveOrUpdate(tranJournalSql, journal);
	}
	
	public DhbBizJournal getBizJournalByReqInfo(OutRequestInfo info){
		String merchId = info.getMerchId();
		String tranNo = info.getTranNo();
		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outId=:outId";
		String recordId = commonObjectDao.findSingleVal(sql, new Object[]{merchId,tranNo});
		String journalSql = "select id,bizType,handleStatus,batchId,createTime,handleRemark from dhb_biz_journal where recordId=:recordId";
		return  commonObjectDao.findOneObject(journalSql, DhbBizJournal.class, new Object[]{recordId});
	}
	public List<DhbBizJournal> getBizJournalByOutBatchId(String merchId,String outBatchId){
		if(Strings.isNullOrEmpty(outBatchId)||Strings.isNullOrEmpty(merchId)){
			return null;
		}
		String journalSql = "select dj.id,dj.bizType,dj.handleStatus,dj.batchId,dj.createTime,dj.handleRemark,dc.outid from "
						+ "dhb_biz_journal dj,dhb_pay_cut dc where dj.recordid=dc.recordid and dc.batchid=:batchId and dc.merchId=:merchId";
		return  commonObjectDao.findList(journalSql, DhbBizJournal.class, new Object[]{outBatchId,merchId});
	}
	public List<DhbBizJournal> getHandingJournal(){
		String sql = "select id,handleStatus,bizType,channelId,bigChannelId,createTime,merchId,batchId from dhb_biz_journal where createTime<sysdate and createTime>sysdate-15 and handleStatus='0'";
		return commonObjectDao.findList(sql, DhbBizJournal.class,new Object[]{});
	}
	public RealNameInfo getFourRealNameSelect(String accNo){
		String sql = "SELECT USERNAME,CERTNO,TEL FROM DHB_REALNAME WHERE ACCNO = :accNo AND BUSINESS_TYPE = '1' ORDER BY CREATED_TIME DESC";
		return commonObjectDao.findOneObject(sql,RealNameInfo.class, new Object[]{accNo});
	}
	/**
	 * 新增NFC订单日志流水表
	 * @param water
	 */
	public void addNFCorderWater(NfcOrderWater water){
		String sql = "insert into dhb_nfc_order_water(mer_order_no,order_no,refund_order_no,sub_merch_no,"
				+ "merch_no,merch_channel,nfc_type,nfc_merch,total_fee,merch_fee,refund_fee,refund_channe,currency,"
				+ "notify_url,remark,created_time,status,message,end_time) "
				+ "values(:merOrderNo,:orderNo,:refundOrderNo,:subMerchNo,:merchNo,:merchChannel,:nfcType,:nfcMerch,"
				+ ":totalFee,:merchFee,:refundFee,:refundChanne,:currency,:notifyUrl,:remark,"
				+ ":createdTime,:status,:message,:endTime)";
		commonObjectDao.saveOrUpdate(sql, water);
	}
	/**
	 * 获取扫码支付数据集
	 * @return
	 */
	public List<NfcOrderWater> getNfcOrderWaterList(){
		String sql = "select * from dhb_nfc_order_water where (nfc_type = 'nfc_passive' or nfc_type = 'nfc_active' or nfc_type = 'nfc_channelPay') and (status = '0' or status = '3')";
		List<NfcOrderWater> waterList = commonObjectDao.findList(sql, NfcOrderWater.class, new Object[]{});
		return waterList;
	}
	/**
	 * 获取退货订单数据集
	 * @return
	 */
	public List<NfcOrderWater> getRefundWaterList(){//之后要修改
		String sql = "select * from dhb_nfc_order_water where nfc_type = 'nfc_refund' and status = '2'";
		List<NfcOrderWater> waterList = commonObjectDao.findList(sql, NfcOrderWater.class, new Object[]{});
		return waterList;
	}
	/**
	 * 查询联动优势某一批次订单
	 * @param startdate
	 * @param enddate
	 * @param filename
	 * @return
	 */
	public List<DhbBizJournal> getUmpayJournalByFilenameAndDate(String orderdate,String filename){
		String sql = "select * from dhb_biz_journal where to_char(createTime,'yyyyMMdd')=:orderdate and channelId='12' and memo=:filename and handleStatus!='1'";
		return commonObjectDao.findList(sql, DhbBizJournal.class,new Object[]{orderdate,filename});
	}
	/**
	 * 通过主键查询
	 * @param id
	 * @return
	 */
	public List<DhbBizJournal> getJournalByIdAndDate(String orderdate,String id){
		if(Strings.isNullOrEmpty(id)){
			return null;
		}
		String sql = "select id,handleStatus,handleRemark,bizType,createTime from dhb_biz_journal where id=:id and to_char(createTime,'yyyyMMdd')=:orderdate";
		return commonObjectDao.findList(sql, DhbBizJournal.class, new Object[]{id,orderdate});
	}
}
