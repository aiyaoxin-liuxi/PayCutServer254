package com.dhb.chinapay.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.chinapay.entity.CpBatchFile;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchResp;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.util.DateUtil;
import com.dhb.util.DigestMD5;
import com.dhb.util.HttpHelp;
import com.dhb.util.MsgUtil;
import com.dhb.util.PropFileUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
@Service
public class CpService {
	public static Logger logger = Logger.getLogger(CpService.class);
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ChinaPayDaoService chinaPayDaoService;
	@Autowired
    private DhbBizJournalDao dhbBizJournalDao;
	@Autowired	
	private ProxyBankInfoDao proxyBankInfoDao;
	public BatchResp batchPay(BatchTranReq tranReq)throws Exception
	{

		BatchResp resp = new BatchResp();
		Date now = new Date();
		String merId=PropFileUtil.getByFileAndKey("chinapay.properties", "batchPayMerchId");
		String merSeqId= StringUtils.leftPad(sequenceDao.getNextVal("cp_batch_seq")+"",6,"0"); // 批次号
		String filePrefix = PropFileUtil.getByFileAndKey("chinapay.properties", "batchFilePrefix");
		String transDate = DateUtil.getDayForYYMMDD(now); // 交易日期
		int tranCount=tranReq.getTotalNum();
		int allMoney= (int)(tranReq.getTotalBalance()*100);
		StringBuilder plain = new StringBuilder();
				
		plain.append(merId).append("|") 
		.append(merSeqId).append("|")
		.append(tranCount).append("|")
		.append(allMoney);
		String batchId = tranReq.getBatchId();
		String fileName = merId + "_" + transDate + "_" + merSeqId + ".txt";
		CpBatchFile fileInfo = new CpBatchFile();
		fileInfo.setBatchId(batchId);
		fileInfo.setFileName(fileName);
		chinaPayDaoService.insertFile(fileInfo);
		for(OutRequestInfo info:tranReq.getInfo()){
			String OrdId = StringUtils.leftPad(sequenceDao.getNextVal("chinaPay_seq")+"", 16, '0');
			String bankName = info.getBankName();
			String openBankId=chinaPayDaoService.findOpenBankIdByBankName(bankName); //开户行号
			String money = String.valueOf((int)(info.getBanlance()*100));
			String accType = info.getAccType();
	   		if(Strings.isNullOrEmpty(accType)){
	   			accType ="00";
	   		}
			plain.append("\r\n")
			.append(transDate).append("|")
			.append(OrdId).append("|")
			.append(info.getAccNo()).append("|")
			.append(info.getAccName()).append("|")
			.append(bankName).append("|")
			.append("").append("|")
			.append("").append("|")
			.append(bankName).append("|")
			.append(money).append("|")
			.append("稿费").append("|")
			.append(accType);
			DhbBizJournal journal = new DhbBizJournal();
	   		String channelId = info.getChannelId();
	   		journal.setId(OrdId);
	   		journal.setMerchId(info.getMerchId());
	   		journal.setBizType(BizType.Pay.getCode());
	   		journal.setChannelId(channelId);
	   		DhbBankInfo bankInfo =proxyBankInfoDao.getCutBankInfoByChannleId(channelId);
	   		journal.setFromBankCardNo(info.getAccNo());
	   		journal.setFromIdentityNo(info.getCertNo());
	   		journal.setFromBankName(info.getBankName());
	   		journal.setFromUserName(info.getAccName());
	   		journal.setToBankCardNo(bankInfo.getAcctNo());
	   		journal.setToBankCode(bankInfo.getBankCode());
	   		journal.setToBankName(bankInfo.getBankName());
	   		journal.setToUserName(bankInfo.getAcctName());
	   		journal.setMoney(info.getBanlance());
	   		journal.setBatchId(info.getBatchId());
	   		journal.setCurrency("CNY");
	   		journal.setMemo(info.getComments());
	   		journal.setCreateTime(now);
	   		journal.setRecordId(info.getRecordId());
	   		dhbBizJournalDao.insertJournal(journal);
		}
		String filepath = filePrefix + fileName;
		File file = new File(filepath);
		FileOutputStream fos = null;
		fos = new FileOutputStream(filepath);
		String fileContent = plain.toString();
		fos.write(fileContent.getBytes("UTF-8"));
		fos.flush();
		fos.close();
		String sign = null;
		try {
			sign= new String(MsgUtil.getBytes(file),"UTF-8");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 对需要上传的字段签名
		String chkValue2 = null;
		String merKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "batchMerKeyPath");
		chkValue2 = DigestMD5.MD5Sign(merId, fileName, fileContent.getBytes("UTF-8"), merKeyPath);
		HttpHelp http = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","batchCutUrl"));
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headParam.put("Connection" , "close");
		Map<String,String> params =Maps.newLinkedHashMap();
		param.setParams(params);
		param.setHeads(headParam);
		params.put("merId", merId);
		params.put("fileName", fileName);
		params.put("fileContent", sign);
		params.put("chkValue", chkValue2);
		logger.info("merId:("+merId+"),fileName:("+fileName+")"+",fileContent:("+sign+"),chkValue:("+chkValue2+")");
		HttpResponser response=http.postParamByHttpClient(param);
		SingleResp single = new SingleResp();
		single.setCode(DhbTranStatus.Fail.getCode());
		single.setMessage(DhbTranStatus.Fail.getDescription());
		if(response!=null){
			if(200==response.getCode()){
				String result = response.getContent();
				if(!Strings.isNullOrEmpty(result)){
					int dex = result.lastIndexOf("=");
					String tiakong = result.substring(0, dex + 1);
					System.out.println("验签明文：" + "[" + tiakong + "]");
					String ChkValue = result.substring(dex + 1);
					String str[] = result.split("&");
					int Res_Code = str[0].indexOf("=");
					int Res_message = str[1].indexOf("=");
					String responseCode = str[0].substring(Res_Code + 1);
					String message = str[1].substring(Res_message + 1);
					System.out.println("responseCode=" + responseCode);
					System.out.println("message=" + message);
					System.out.println("chkValue=" + ChkValue);

					// 对收到的ChinaPay应答传回的域段进行验签
					String PubKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "publicKey");
					boolean res = DigestMD5.MD5Verify(tiakong.getBytes("UTF-8"), ChkValue, PubKeyPath);
				    logger.info("verfiy result" +res);
					if (responseCode.equals("20FM")) {
						if("文件上传成功".equals(message)){
							single.setCode(DhbTranStatus.Handling.getCode());
							single.setMessage(DhbTranStatus.Handling.getDescription());
						}else{
							if(!Strings.isNullOrEmpty(message)){
								single.setMessage(message);
							}
							
						}
					}
				}
			}
		}
		DhbBizJournal journal = new DhbBizJournal();
		journal.setHandleStatus(single.getCode());
		journal.setHandleRemark(single.getMessage());
		journal.setEndTime(new Date());
		journal.setBatchId(batchId);
		dhbBizJournalDao.updateStatusByBatchId(journal);
        
		return resp;
	
	}

	public SingleResp batchCut(BatchTranReq tranReq)throws Exception
	{
		SingleResp resp = new SingleResp();
		Date now = new Date();
		String merId=PropFileUtil.getByFileAndKey("chinapay.properties", "batchCutMerchId");
		String merSeqId= StringUtils.leftPad(sequenceDao.getNextVal("cp_batch_seq")+"",6,"0"); // 批次号
		String filePrefix = PropFileUtil.getByFileAndKey("chinapay.properties", "batchFilePrefix");
		String transDate = DateUtil.getDayForYYMMDD(now); // 交易日期
		int tranCount=tranReq.getTotalNum();
		int allMoney= (int)(tranReq.getTotalBalance()*100);
		StringBuilder plain = new StringBuilder();
				
		plain.append(merId).append("|") 
		.append(merSeqId).append("|")
		.append(tranCount).append("|")
		.append(allMoney);
		String batchId = tranReq.getBatchId();
		String fileName = merId + "_" + transDate + "_" + merSeqId + "_Q.txt";
		CpBatchFile fileInfo = new CpBatchFile();
		fileInfo.setBatchId(batchId);
		fileInfo.setFileName(fileName);
		chinaPayDaoService.insertFile(fileInfo);
		for(OutRequestInfo info:tranReq.getInfo()){
			String OrdId = StringUtils.leftPad(sequenceDao.getNextVal("chinaPay_seq")+"", 16, '0');
			String bankName = info.getBankName();
			String openBankId=chinaPayDaoService.findOpenBankIdByBankName(bankName); //开户行号
			String money = String.valueOf((int)(info.getBanlance()*100));
			plain.append("\r\n")
			.append(transDate).append("|")
			.append(OrdId).append("|")
			.append(openBankId).append("|")
			.append("0").append("|")
			.append(info.getAccNo()).append("|")
			.append(info.getAccName()).append("|")
			.append(info.getCertType()).append("|")
			.append(info.getCertNo()).append("|")
			.append(money).append("|")
			.append("稿费").append("|")
			.append("私有");
			DhbBizJournal journal = new DhbBizJournal();
	   		String channelId = info.getChannelId();
	   		journal.setId(OrdId);
	   		journal.setMerchId(info.getMerchId());
	   		journal.setBizType(BizType.Cut.getCode());
	   		journal.setChannelId(channelId);
	   		DhbBankInfo bankInfo =proxyBankInfoDao.getCutBankInfoByChannleId(channelId);
	   		journal.setFromBankCardNo(info.getAccNo());
	   		journal.setFromIdentityNo(info.getCertNo());
	   		journal.setFromBankName(info.getBankName());
	   		journal.setFromUserName(info.getAccName());
	   		journal.setToBankCardNo(bankInfo.getAcctNo());
	   		journal.setToBankCode(bankInfo.getBankCode());
	   		journal.setToBankName(bankInfo.getBankName());
	   		journal.setToUserName(bankInfo.getAcctName());
	   		journal.setMoney(info.getBanlance());
	   		journal.setBatchId(info.getBatchId());
	   		journal.setCurrency("CNY");
	   		journal.setMemo(info.getComments());
	   		journal.setCreateTime(now);
	   		journal.setRecordId(info.getRecordId());
	   		dhbBizJournalDao.insertJournal(journal);
		}
		String filepath = filePrefix + fileName;
		File file = new File(filepath);
		FileOutputStream fos = null;
		fos = new FileOutputStream(filepath);
		String fileContent = plain.toString();
		fos.write(fileContent.getBytes("UTF-8"));
		fos.flush();
		fos.close();
		String sign = null;
		try {
			sign= new String(MsgUtil.getBytes(file),"UTF-8");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 对需要上传的字段签名
		String chkValue2 = null;
		String merKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "batchMerKeyPath");
		chkValue2 = DigestMD5.MD5Sign(merId, fileName, fileContent.getBytes("UTF-8"), merKeyPath);
		HttpHelp http = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","batchCutUrl"));
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headParam.put("Connection" , "close");
		Map<String,String> params =Maps.newLinkedHashMap();
		param.setParams(params);
		param.setHeads(headParam);
		params.put("merId", merId);
		params.put("fileName", fileName);
		params.put("fileContent", sign);
		params.put("chkValue", chkValue2);
		logger.info("merId:("+merId+"),fileName:("+fileName+")"+",fileContent:("+sign+"),chkValue:("+chkValue2+")");
		HttpResponser response=http.postParamByHttpClient(param);
		SingleResp single = new SingleResp();
		single.setCode(DhbTranStatus.Fail.getCode());
		single.setMessage(DhbTranStatus.Fail.getDescription());
		if(response!=null){
			if(200==response.getCode()){
				String result = response.getContent();
				if(!Strings.isNullOrEmpty(result)){
					int dex = result.lastIndexOf("=");
					String tiakong = result.substring(0, dex + 1);
					System.out.println("验签明文：" + "[" + tiakong + "]");
					String ChkValue = result.substring(dex + 1);
					String str[] = result.split("&");
					int Res_Code = str[0].indexOf("=");
					int Res_message = str[1].indexOf("=");
					String responseCode = str[0].substring(Res_Code + 1);
					String message = str[1].substring(Res_message + 1);
					System.out.println("responseCode=" + responseCode);
					System.out.println("message=" + message);
					System.out.println("chkValue=" + ChkValue);

					// 对收到的ChinaPay应答传回的域段进行验签
					String PubKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "publicKey");
					boolean res = DigestMD5.MD5Verify(tiakong.getBytes("UTF-8"), ChkValue, PubKeyPath);
				    logger.info("verfiy result" +res);
					if (responseCode.equals("20FM")) {
						if("文件上传成功".equals(message)){
							single.setCode(DhbTranStatus.Handling.getCode());
							single.setMessage(DhbTranStatus.Handling.getDescription());
						}else{
							if(!Strings.isNullOrEmpty(message)){
								single.setMessage(message);
							}
							
						}
					}
				}
			}
		}
		DhbBizJournal journal = new DhbBizJournal();
		journal.setHandleStatus(single.getCode());
		journal.setHandleRemark(single.getMessage());
		journal.setEndTime(new Date());
		journal.setBatchId(batchId);
		dhbBizJournalDao.updateStatusByBatchId(journal);
        
		return resp;
	}
	public SingleResp getBatchResp(String batchId,String seqId){
		SingleResp resp = new SingleResp();
		
		String orFileName=chinaPayDaoService.getFileNameByBatchId(batchId);
		if(Strings.isNullOrEmpty(orFileName)){
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage("没有找到回盘文件");
			return resp;
		}
		String fileName = orFileName.replace("Q", "P");
		String merId=PropFileUtil.getByFileAndKey("chinapay.properties", "batchCutMerchId");
		String signMsg = merId + orFileName + fileName;
		String chkValue2 = null;
		String merKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "batchMerKeyPath");
		try {
			chkValue2 = DigestMD5.MD5Sign(merId, signMsg.getBytes("UTF-8"), merKeyPath);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHelp http = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","batchQueryUrl"));
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headParam.put("Connection" , "close");
		Map<String,String> params =Maps.newLinkedHashMap();
		param.setParams(params);
		param.setHeads(headParam);
		params.put("merId", merId);
		params.put("fileName", fileName);
		params.put("orFileName", orFileName);
		params.put("chkValue", chkValue2);
		logger.info("merId:("+merId+"),fileName:("+fileName+")"+",orFileName:("+orFileName+"),chkValue:("+chkValue2+")");
		HttpResponser response=http.postParamByHttpClient(param);
		SingleResp single = new SingleResp();
		single.setCode(DhbTranStatus.Fail.getCode());
		single.setMessage(DhbTranStatus.Fail.getDescription());
		if(response!=null){
			if(200==response.getCode()){
				String result = response.getContent();
				if(!Strings.isNullOrEmpty(result)){
					logger.info("downfile resp:("+result+")");
					Map<String, String> keyValMap = ChinaPaySignUtil.parseResponse(result);
					int dex = result.lastIndexOf("=");
					String tiakong = result.substring(0, dex + 1);
					String ChkValue = result.substring(dex + 1);

					String str[] = result.split("&");
					System.out.println(str.length);
					
					// 验签明文
					String plainData = "";
					int length = keyValMap.keySet().size();
					if (length == 5) {

						// 回盘文件下载成功
						int Res_merId = str[0].indexOf("=");
						int Res_orFileName = str[1].indexOf("=");
						int Res_filename = str[2].indexOf("=");
						int Res_fileData = str[3].indexOf("=");

						String MerId = str[0].substring(Res_merId + 1);
						String OrFileName = str[1].substring(Res_orFileName + 1);
						String Filename = str[2].substring(Res_filename + 1);
						String FileData = str[3].substring(Res_fileData + 1);
						System.out.println("merId=" + MerId);
						System.out.println("orFileName=" + OrFileName);
						System.out.println("filename=" + Filename);
						System.out.println("FileData=" + FileData);

						String resultText=null;
						try {
							resultText = new String(MsgUtil.decodeInflate(FileData
									.getBytes()), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						plainData = resultText;
						System.out.println("回盘文件内容：");
						System.out.println(resultText);
					} else {
						
						// 回盘文件下载失败
						System.out.println("验签明文：");
						plainData = tiakong;
						System.out.println(plainData);
						int Res = str[0].indexOf("=");
						String ResponseCode = str[0].substring(Res + 1);
						String Message = str[1].substring(str[1].indexOf("=") + 1);
						System.out.println("responseCode = " + ResponseCode);
						
					}
					String PubKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "publicKey");
					// 对收到的ChinaPay应答传回的域段进行验签
					try {
						boolean res = DigestMD5.MD5Verify(plainData.getBytes("UTF-8"), ChkValue, PubKeyPath);
						System.out.println("vertify "+res);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		
		
		return null;
	}
	public SingleResp getFileStatus(String batchId,String seqId){
		SingleResp resp = new SingleResp();
		
		String orFileName=chinaPayDaoService.getFileNameByBatchId(batchId);
		if(Strings.isNullOrEmpty(orFileName)){
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage("没有找到回盘文件");
			return resp;
		}
	
		String merId=PropFileUtil.getByFileAndKey("chinapay.properties", "batchCutMerchId");
		String signMsg = merId + orFileName ;
		String chkValue2 = null;
		String merKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "batchMerKeyPath");
		try {
			chkValue2 = DigestMD5.MD5Sign(merId, signMsg.getBytes("UTF-8"), merKeyPath);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHelp http = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","fileStatusUrl"));
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headParam.put("Connection" , "close");
		Map<String,String> params =Maps.newLinkedHashMap();
		param.setParams(params);
		param.setHeads(headParam);
		params.put("merId", merId);
		params.put("fileName", orFileName);
		params.put("chkValue", chkValue2);
		logger.info("merId:("+merId+"),fileName:("+orFileName+"),chkValue:("+chkValue2+")");
		HttpResponser response=http.postParamByHttpClient(param);
		SingleResp single = new SingleResp();
		single.setCode(DhbTranStatus.Fail.getCode());
		single.setMessage(DhbTranStatus.Fail.getDescription());
		if(response!=null){
			if(200==response.getCode()){
				String result = response.getContent();
				if(!Strings.isNullOrEmpty(result)){
					logger.info("getFileStatus:("+result+")");
					Map<String, String> keyValMap = ChinaPaySignUtil.parseResponse(result);
					String responseCode = keyValMap.get("responseCode");
					String returnChkValue = keyValMap.get("chkValue");
					String message = keyValMap.get("message");
					int dex = result.lastIndexOf("=");
					String plainData = result.substring(0, dex + 1);
					logger.info("responseCode:(" + responseCode+"),message:("+message+")");
					String PubKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties", "publicKey");
					resp.setMessage(message);
					try {
						boolean res = DigestMD5.MD5Verify(plainData.getBytes("UTF-8"), returnChkValue, PubKeyPath);
						if(res){
							if("20GN".equals(responseCode)){
								if("回盘文件已生成".equals(message)){
									resp.setCode(DhbTranStatus.Succ.getCode());
									resp.setMessage(message);
								}
							}
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		resp.setCode(DhbTranStatus.Succ.getCode());
		
		return resp;
	}

	public void queryTranStatus(DhbBizJournal journal) {
		// TODO Auto-generated method stub
		
	}
}
