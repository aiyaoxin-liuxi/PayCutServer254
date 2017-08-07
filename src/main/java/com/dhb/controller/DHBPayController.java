package com.dhb.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhb.anyz.entity.Constants;
import com.dhb.cgb.service.CGBQueryService;
import com.dhb.dao.service.ChannelInfoDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.DhbOutMerchantDao;
import com.dhb.dao.service.OutRequestRecordDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyMerchAmt;
import com.dhb.entity.RealNameInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.jyt.service.JYTRealNameService;
import com.dhb.kl.realname.service.ZXRealNameService;
import com.dhb.service.ErrorService;
import com.dhb.service.PayCutInterface;
import com.dhb.service.ProxyBatchPayThreadService;
import com.dhb.service.ProxyBizJournalService;
import com.dhb.service.ValidateService;
import com.dhb.shande.service.ShanDePayCutService;
import com.dhb.umpay.entity.PlatType;
import com.dhb.umpay.service.UmpayPayCutService;
import com.dhb.util.ArithUtil;
import com.dhb.util.JsonUtil;
import com.dhb.util.PropFileUtil;
import com.dhb.util.SpringContextHelper;
import com.dhb.ysb.service.YSBPayCutService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.dhb.dao.service.UserInfoDao;
import com.dhb.entity.UserInfo;
@Controller
@RequestMapping(value="/dhb",produces = {"application/json;charset=UTF-8"})
public class DHBPayController {
	
	public static Logger logger = Logger.getLogger(DHBPayController.class);
	@Resource
	private ErrorService errorService;
	@Resource
	private ValidateService validateService;
	@Resource
	private ChannelInfoDao channelService;
	@Resource
	private OutRequestRecordDao outRequestRecordService;
	@Resource
	private CGBQueryService queryService;
	@Resource
	private JYTRealNameService jytRealNameService;
	@Resource
	private ZXRealNameService zxRealNameService;
	@Resource
	private ProxyBatchPayThreadService proxyBatchPayThreadService;
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Resource
	private YSBPayCutService ysbPayCutService;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private DhbOutMerchantDao dhbOutMerchantDao;
	@Resource
	private UmpayPayCutService umpayPayCutService;
	@Resource
	private ProxyBizJournalService proxyBizJournalService;
	
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/singlePay")
	public @ResponseBody SingleResp singlePay(@RequestBody String json){
		logger.info("singlePay:("+json+")");
		long l = System.currentTimeMillis();
		SingleResp singleResp =new SingleResp();
		Gson g = new Gson();
		OutRequestInfo resInfo=g.fromJson(json, OutRequestInfo.class);
		String tranNo = resInfo.getTranNo();
		singleResp.setTranNo(tranNo);
		try{
			//20160620:钱包提现金额设置       add by wxw
		    if("111301000000001".equals(resInfo.getMerchId())){
		    	if(resInfo.getBanlance()>50000){
		    		logger.error("钱包提现金额超限，amt="+resInfo.getBanlance());
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("钱包提现金额超限,限额为5W");
					return singleResp;
		    	}
		    	//以下判断作用于钱包提现
		    	if(Strings.isNullOrEmpty(resInfo.getAccNo())){
		    		logger.error("提现卡号为空="+resInfo.getAccNo());
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("卡号为空");
					return singleResp;
		    	}
		    	RealNameInfo realNameInfo = zxRealNameService.selectByAccNo(resInfo.getAccNo());
		    	if(realNameInfo==null){
		    		logger.error("该卡号未绑定，accNo="+resInfo.getAccNo());
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("该卡号未绑定");
					return singleResp;
		    	}
		    	String cardno = realNameInfo.getCertNo();//身份证号
		    	logger.info("提现身份证号："+cardno);
		    	List<UserInfo> users = userInfoDao.getUserInfo(cardno);
		    	if(users.size()<=0){
		    		logger.error("该身份证号未注册，certNo="+cardno);
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("该身份证号未注册");
					return singleResp;
		    	}else if(users.size()>1){
		    		logger.error("该身份证号有多人注册，certNo="+cardno);
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("该身份证号有多人注册");
					return singleResp;
		    	}
		    	UserInfo user = users.get(0);
		    	if(!"2".equals(user.getUSER_STAT()) && !"8".equals(user.getUSER_STAT()) && !"9".equals(user.getUSER_STAT())){
		    		logger.error("该用户未审核通过，userId="+user.getUSER_ID());
			    	singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("该用户未审核通过");
					return singleResp;
		    	}
		    	
		    }
		    validateService.validatePayReqInfoIsNull(resInfo);
			/**
			 * 验证参数  待文档确认后修改
			 */
			logger.info("收到请求(单笔代付接口)：客户端->服务端(" + tranNo + ")参数：" + json);
		    //
			if(validateService.validateTranNoIsExist(resInfo)){//验证订单号是否存在
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("trano exists");
				return singleResp;
			}
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(resInfo.getMerchId());
			if(merchant != null){//验证商户余额
				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(resInfo.getMerchId());
				Double valid_balance = proxyMerchAmt.getValidBalance();
				Double sub_result = ArithUtil.sub(valid_balance, resInfo.getBanlance());
				sub_result = ArithUtil.sub(sub_result, merchant.getMerFee());
				if(sub_result < 0){
					logger.error("流水号："+resInfo.getTranNo()+","+resInfo.getMerchId() + "账户余额不足，无法交易");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("balance is not enough");
					return singleResp;
				}
			}
			//*******************         end           *******************//
			String channelId = resInfo.getChannelId();
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
				resInfo.setBizType(BizType.Pay.getCode());
				outRequestRecordService.saveSingleReq(resInfo);
				singleResp=service.singlePay(resInfo);
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("not find this channel("+channelId+")");
			}
			//*****start  大网关外放   修改商户账户(余额和可用余额均减[交易金额+手续费])****************//
			if(merchant != null){//验证商户余额
		        if((DhbTranStatus.Succ.getCode()).equals(singleResp.getCode())||(DhbTranStatus.Handling.getCode()).equals(singleResp.getCode())){
		        	proxyBizJournalService.updateStatusForPay(resInfo, BizType.Pay.getCode(), merchant, singleResp);
		        }
			}
	        //*****end **********************************************************//
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return singlepayReqinfo:("+singleResp+")");
		logger.info("耗时：单笔代付接口("+ singleResp.getTranNo() +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(单笔代付接口)：服务端->客户端(" + singleResp.getTranNo() + ")参数：" + singleResp);
		return singleResp;
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/batchPay")
	public @ResponseBody SingleResp batchPay(@RequestBody String json){
		logger.info("batchPay:("+json+")");
		SingleResp resp =new SingleResp();
		String batchId = null;
		try{
			Gson g = new Gson();
			BatchTranReq resInfo=g.fromJson(json, BatchTranReq.class);
			validateService.validateBatchIsNull(resInfo);
			 batchId = resInfo.getBatchId();
			if(validateService.validateBatchIdIsExist(resInfo)){
				resp.setCode(DhbTranStatus.Fail.getCode());
				resp.setMessage("batchId already exists");
			}
			String channelId = resInfo.getChannelId();
			
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
				resInfo.setBizType(BizType.Pay.getCode());
				outRequestRecordService.saveBatchReq(resInfo);
				resp= service.batchPay(resInfo);
				resp.setTranNo(batchId);
			}else{
				resp.setTranNo(batchId);
				throw new CutException("not find this channel("+channelId+")");
			}
			
		}catch(JsonSyntaxException e){
			String message = "batchPay  request error";
			logger.error(message, e);
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(message);
			
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(DhbTranStatus.Fail.getDescription());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		resp.setTranNo(batchId);
		logger.info("return batchPay:("+resp+")");
		return resp;
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/batchCut")
	public @ResponseBody SingleResp batchCut(@RequestBody String json){
		logger.info("batchCut:("+json+")");
		SingleResp resp =new SingleResp();
		String batchId = null;
		try{
			Gson g = new Gson();
			BatchTranReq resInfo=g.fromJson(json, BatchTranReq.class);
			 batchId = resInfo.getBatchId();
			 String channelId = resInfo.getChannelId();
			 if(PlatType.umpay.equals(channelId)){
				 validateService.validateBatchIsNullForUmpay(resInfo);
			 }else{
				 validateService.validateBatchIsNull(resInfo);
			 }
			
			if(validateService.validateBatchIdIsExist(resInfo)){
				resp.setCode(DhbTranStatus.Fail.getCode());
				resp.setMessage("batchId exists");
			}
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
				resInfo.setBizType(BizType.Cut.getCode());
				Map<String,String> map = new HashMap<String, String>();
				if(PlatType.umpay.equals(channelId)){
					outRequestRecordService.saveBatchReq(resInfo,map);
					resp = umpayPayCutService.batchCut(resInfo,map);
				}else{
					outRequestRecordService.saveBatchReq(resInfo);
					resp= service.batchCut(resInfo);
				}
				
				//proxyBatchPayThreadService.toBatchPay(resInfo, service);
				resp.setTranNo(batchId);
			}else{
				resp.setTranNo(batchId);
				throw new CutException("not find this channel("+channelId+")");
			}
			
		}catch(JsonSyntaxException e){
			String message = "batchcut  request error";
			logger.error(message, e);
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(message);
			
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(DhbTranStatus.Fail.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		resp.setTranNo(batchId);
		logger.info("return batchCut:("+resp+")");
		return resp;
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/queryTranStatus")
	public @ResponseBody  SingleResp queryTranStatus(@RequestBody String json){
		logger.info("queryTranStatus:("+json+")");
		SingleResp singleResp =new SingleResp();
		String outId =null;
		try{
			Gson g = new Gson();
			OutRequestInfo resInfo=g.fromJson(json, OutRequestInfo.class);
			validateService.validateQueryReqInfoIsNull(resInfo);
			 outId = resInfo.getTranNo();
			
			
			if(!validateService.validateTranNoIsExist(resInfo)){
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("trano is not exist");
				return singleResp;
			}
			String channelId = resInfo.getChannelId();
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
				singleResp =service.querySingleTranStatus(resInfo);
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("not find this channel("+channelId+")");
			}
		    
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		if(singleResp!=null){
			singleResp.setTranNo(outId);
		}
		logger.info("return queryTranStatus:("+singleResp+")");
		return singleResp;
	}
	
	/**
	 * 三要素
	 * @param json
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/bankCardRealName")
	public @ResponseBody  SingleResp bankCardRealName(@RequestBody String json){
		String logStr="【三要素签约】";
		logger.info("bankCardRealName:("+json+")");
		SingleResp singleResp =new SingleResp();
		/////////////避免测试环境走考拉的正式环境//////////////////////////////
		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				return singleResp;
		}
		//////////////////////////////////////////
		String outId =null;
		try{
			Gson g = new Gson();
			OutRequestInfo reqInfo=g.fromJson(json, OutRequestInfo.class);
			 outId = reqInfo.getTranNo();
			 String channelId = reqInfo.getChannelId();
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			// 商户余额-手续费
			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
			if(merchant != null){//验证商户余额
				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
				Double valid_balance = proxyMerchAmt.getValidBalance();
				Double sub_result = ArithUtil.sub(valid_balance, merchant.getMerFee());
				if(sub_result < 0){
					logger.error(reqInfo.getMerchId() + "账户余额不足，无法交易");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("balance is not enough");
					return singleResp;
				}
			}
			//*******************         end           *******************//
			logger.info(logStr+" channelId:"+channelId);
			 if("2".equals(channelId)){
				 validateService.validateQueryReqInfoIsNull(reqInfo);
				singleResp =zxRealNameService.realName(reqInfo);
			 }else if("16".equals(channelId)){//杉德的三要素签约
				 ShanDePayCutService sd=(ShanDePayCutService) SpringContextHelper.getInstance().getBean("ShanDePayCutService");
				 reqInfo.setBizType(BizType.Bank_verify.getCode());
				 singleResp= sd.bankCardRealName(reqInfo);
			 }else{
				 validateService.validateRealNameReqInfoIsNull(reqInfo);
				 singleResp =zxRealNameService.realName(reqInfo);
			 }
			
		    
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return bankCardRealName:("+singleResp+")");
		singleResp.setTranNo(outId);
		return singleResp;
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/fourRealName")
	public @ResponseBody  SingleResp fourRealName(@RequestBody String json){
		logger.info("fourRealName:("+json+")");
		SingleResp singleResp =new SingleResp();
		/////////////避免测试环境走考拉的正式环境//////////////////////////////
		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				return singleResp;
		}
		//////////////////////////////////////////
		try{
			Gson g = new Gson();
			OutRequestInfo reqInfo=g.fromJson(json, OutRequestInfo.class);
			String outId = reqInfo.getTranNo();
			singleResp.setTranNo(outId);
			validateService.validate4RealNameReqInfoIsNull(reqInfo);
//			String channelId = reqInfo.getChannelId();
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			// 商户余额-手续费
			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
			if(merchant != null){//验证商户余额
				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
				Double valid_balance = proxyMerchAmt.getValidBalance();
				Double sub_result = ArithUtil.sub(valid_balance, merchant.getMerFee());
				if(sub_result < 0){
					logger.error(reqInfo.getMerchId() + "账户余额不足，无法交易");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("balance is not enough");
					return singleResp;
				}
			}
			//*******************         end           *******************//
			 //if("2".equals(channelId)){
				 //validateService.validateQueryReqInfoIsNull(reqInfo);
			singleResp =zxRealNameService.fourRealName(reqInfo);
			 //}else{
				 //validateService.validate4RealNameReqInfoIsNull(reqInfo);
			 //}
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return fourRealName:("+singleResp+")");
		return singleResp;
	}
	/**
	 * 二要素
	 * @param json
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/certNoRealName")
	public @ResponseBody  SingleResp CerdNoRealName(@RequestBody String json){
		logger.info("CerdNoRealName:("+json+")");
		SingleResp singleResp =new SingleResp();
		/////////////避免测试环境走考拉的正式环境//////////////////////////////
		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				return singleResp;
		}
		//////////////////////////////////////////
		try{
			Gson g = new Gson();
			OutRequestInfo reqInfo=g.fromJson(json, OutRequestInfo.class);
			String outId = reqInfo.getTranNo();
			singleResp.setTranNo(outId);
			validateService.validateQueryReqInfoIsNull(reqInfo);
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			// 商户余额-手续费
			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
			if(merchant != null){//验证商户余额
				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
				Double valid_balance = proxyMerchAmt.getValidBalance();
				Double sub_result = ArithUtil.sub(valid_balance, merchant.getMerFee());
				if(sub_result < 0){
					logger.error(reqInfo.getMerchId() + "账户余额不足，无法交易");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("balance is not enough");
					return singleResp;
				}
			}
			//*******************         end           *******************//
			singleResp =jytRealNameService.certNoRealName(reqInfo);
		    
		}catch(JsonSyntaxException e){
			String message = "translate  request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return CerdNoRealName:("+singleResp+")");
		return singleResp;
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/singleCut")
	public @ResponseBody   SingleResp  singleCut(@RequestBody String json){
		String logStr="【单笔代收】";
		long l = System.currentTimeMillis();
		SingleResp singleResp =new SingleResp();;
		try{
			Gson g = new Gson();
			OutRequestInfo resInfo=g.fromJson(json, OutRequestInfo.class);
			String tranNo = resInfo.getTranNo();
			singleResp.setTranNo(tranNo);
			/**
			 * 验证参数  待文档确认后修改
			 */
			validateService.validateCutReqInfoIsNull(resInfo);
			logger.info(logStr+"收到请求(单笔代收接口)：客户端->服务端(" + tranNo + ")参数：" + json);
			 //20160406:关闭钱包提现功能       add by wxw
//		    if("111301000000002".equals(resInfo.getMerchId())){
//		    	logger.error("钱包充值功能暂停");
//		    	singleResp.setCode(DhbTranStatus.Fail.getCode());
//				singleResp.setMessage("钱包充值功能暂停");
//				return singleResp;
//		    }
		    //
			String channelId = resInfo.getChannelId();
			if(validateService.validateTranNoIsExist(resInfo)){//验证订单号是否存在
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("traNo exists");
				return singleResp;
			}
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			// 商户余额+代扣金额-手续费
			// 暂时不判断代扣金额
//			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(resInfo.getMerchId());
//			if(merchant != null){//验证商户余额
//				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(resInfo.getMerchId());
//				Double valid_balance = proxyMerchAmt.getValidBalance();
//				Double sub_result = ArithUtil.add(valid_balance, resInfo.getBanlance());
//				sub_result = ArithUtil.sub(sub_result, merchant.getMerFee());
//				if(sub_result < 0){
//					logger.error(resInfo.getMerchId() + "账户余额不足，无法交易");
//					singleResp.setCode(DhbTranStatus.Fail.getCode());
//					singleResp.setMessage("balance is not enough");
//					return singleResp;
//				}
//			}
			//*******************         end           *******************//
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				logger.debug(logStr+"1.beanName :"+beanName);
				PayCutInterface service = (PayCutInterface) SpringContextHelper.getInstance().getBean(beanName);
				resInfo.setBizType(BizType.Cut.getCode());
				//判断业务类型
				String businessType = resInfo.getBusinessType();
				String accNo = resInfo.getAccNo();
				if(businessType != null && businessType.length() != 0 && ( !channelId.equals("16"))){//杉德的是三要素
					logger.debug(logStr+"2.businessType :"+businessType +" channelId:"+channelId);
					if(businessType.equals(Constants.business_type_1)){//POSP
						//自查四要素信息
						RealNameInfo info = dhbBizJournalDao.getFourRealNameSelect(accNo);
						if(info == null){//未签约
							logger.info("(ANYZ单笔代收接口：)订单号："+resInfo.getTranNo()+",此商户未签约，对象值为RealNameInfo="+info);
							singleResp.setCode(DhbTranStatus.Fail.getCode());
				     		singleResp.setMessage("未签约");
				     		return singleResp;
						}else{
							resInfo.setAccName(info.getUserName());
							resInfo.setCertNo(info.getCertNo());
							resInfo.setMobile(info.getTel());
						}
					}
				}
				
				outRequestRecordService.saveSingleReq(resInfo);
				singleResp=service.singleCut(resInfo);
				singleResp.setTranNo(tranNo);
			}else{
				throw new CutException("not find this channel("+channelId+")");
			}
		    
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return singleCut:("+singleResp+")");
		logger.info("耗时：单笔代收接口("+ singleResp.getTranNo() +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(单笔代收接口)：服务端->客户端(" + singleResp.getTranNo() + ")参数：" + singleResp);
		return singleResp;
	
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/singleCutContract")
	public @ResponseBody String singleCutContract(@RequestBody String json){
		long l = System.currentTimeMillis();
		Map<String,Object> map = JsonUtil.getJsonToMap(json);
		Map<String, Object> jsonRetMap = null;
		/**
		 * 验证参数 
		 */
		String ret = validateService.validateSingleCutContractIsNull(map);
		if(ret.equals("success")){
			try{
				logger.info("收到请求(单笔代扣子协议录入接口)：客户端->服务端(" + map.get("tranNo") + ")参数：" + json);
				//调用ysb服务
				String str_convert = JsonUtil.getMapToJson(map);
				
				jsonRetMap = ysbPayCutService.singleCutContract(JsonUtil.getJsonToMapStr(str_convert));
			}catch(JsonSyntaxException e){
				logger.error("单笔代扣子协议录入接口转换JSON参数异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"translate request json error");
				return JsonUtil.getMapToJson(jsonRetMap);
			}catch(Exception e){
				e.printStackTrace();
				logger.error("单笔代扣子协议录入接口接收参数异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
				return JsonUtil.getMapToJson(jsonRetMap);
			}
		}else{
			return ret;
		}
		logger.info("耗时：单笔代扣子协议录入接口("+ map.get("tranNo") +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(单笔代扣子协议录入接口)：服务端->客户端(" + map.get("tranNo") + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
		
		return JsonUtil.getMapToJson(jsonRetMap);
	}
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/subContractExtension")
	public @ResponseBody String subcontractExtension(@RequestBody String json){
		long l = System.currentTimeMillis();
		Map<String,Object> map = JsonUtil.getJsonToMap(json);
		Map<String, Object> jsonRetMap = null;
		/**
		 * 验证参数 
		 */
		String ret = validateService.validateSubContractExtensionIsNull(map);
		if(ret.equals("success")){
			try{
				logger.info("收到请求(子协议延期接口)：客户端->服务端(" + map.get("tranNo") + ")参数：" + json);
				//调用ysb服务
				String str_convert = JsonUtil.getMapToJson(map);
				
				jsonRetMap = ysbPayCutService.subConstractExtension(JsonUtil.getJsonToMapStr(str_convert));
			}catch(JsonSyntaxException e){
				logger.error("子协议延期接口转换JSON参数异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"translate request json error");
				return JsonUtil.getMapToJson(jsonRetMap);
			}catch(Exception e){
				e.printStackTrace();
				logger.error("子协议延期接口接收参数异常："+e.getMessage());
				jsonRetMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
				return JsonUtil.getMapToJson(jsonRetMap);
			}
		}else{
			return ret;
		}
		logger.info("耗时：子协议延期接口("+ map.get("tranNo") +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(子协议延期接口)：服务端->客户端(" + map.get("tranNo") + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
		
		return JsonUtil.getMapToJson(jsonRetMap);
	}
	/**
	 * 六要素验证
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json",value="/sixRealName")
	public @ResponseBody  SingleResp sixRealName(@RequestBody String json){
		logger.info("sixRealName:("+json+")");
		SingleResp singleResp =new SingleResp();
		/////////////避免测试环境走考拉的正式环境//////////////////////////////
		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				return singleResp;
		}
		//////////////////////////////////////////
		try{
			Gson g = new Gson();
			OutRequestInfo reqInfo=g.fromJson(json, OutRequestInfo.class);
			String outId = reqInfo.getTranNo();
			singleResp.setTranNo(outId);
			validateService.validate6RealNameReqInfoIsNull(reqInfo);
			//*******************   start 大网关外放：判断商户可用余额    *******************//
			// 商户余额-手续费
			DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
			if(merchant != null){//验证商户余额
				ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
				Double valid_balance = proxyMerchAmt.getValidBalance();
				Double sub_result = ArithUtil.sub(valid_balance, merchant.getMerFee());
				if(sub_result < 0){
					logger.error(reqInfo.getMerchId() + "账户余额不足，无法交易");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
					singleResp.setMessage("balance is not enough");
					return singleResp;
				}
			}
			//*******************         end           *******************//
			singleResp =zxRealNameService.sixRealName(reqInfo);
		}catch(JsonSyntaxException e){
			String message = "translate cut request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		
		} catch (CutException e) {
			String message = e.getMessage();
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return sixRealName:("+singleResp+")");
		return singleResp;
	}
	
	/**
	 * 联动签约接口
	 * @param json
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/umpayCutContract")
	public @ResponseBody SingleResp singleCutContractForUmpay(@RequestBody String json){
		logger.info("singleCutContractForUmpay:("+json+")");
		SingleResp singleResp =new SingleResp();
		Gson g = new Gson();
		OutRequestInfo resInfo = g.fromJson(json, OutRequestInfo.class);
		String tranNo = resInfo.getTranNo();
		try{
			SingleResp resp = validateService.validateCutContractUmpayIsNull(resInfo,singleResp);
			if(!DhbTranStatus.Succ.getCode().equals(resp.getCode())){
				logger.error("【umpay 用户签约】参数校验："+resp.getCode()+","+resp.getMessage());
				return resp;
			}
			/**
			 * 验证参数  待文档确认后修改
			 */
			logger.info("收到请求(umpay 用户签约)：客户端->服务端(" + tranNo + ")参数：" + json);
			
			String channelId = resInfo.getChannelId();
			String beanName=channelService.getBeanName(resInfo);
			if(!Strings.isNullOrEmpty(beanName)){
				singleResp = umpayPayCutService.cutContract(resInfo);
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage("not find this channel("+channelId+")");
			}
		    
		}catch(JsonSyntaxException e){
			String message = "translate Contract request error";
			logger.error(message, e);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}
		logger.info("return singleCutContractForUmpay:("+singleResp+")");
		logger.info("收到请求(umpay 用户签约)：服务端->客户端(" + singleResp.getTranNo() + ")参数：" + singleResp);
		singleResp.setTranNo(tranNo);
		return singleResp;
	}
	/**
	 * test
	  * queryBalance( 杉德的支持余额查询 20170606
	  * 没有具体需求先内部调用，不用签名
	  * @Title: queryBalance
	  * @Description: TODO
	  * @param @param json
	  * @param @return    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/queryBalance")
	public @ResponseBody SingleResp queryBalance(@RequestBody String json){
		logger.info("queryBalance:("+json+")");
		SingleResp singleResp =new SingleResp();
		Gson g = new Gson();
		OutRequestInfo resInfo = g.fromJson(json, OutRequestInfo.class);
		if(resInfo.getChannelId().equals("16")){//16 杉德   单位是分
			ShanDePayCutService sd=(ShanDePayCutService) SpringContextHelper.getInstance().getBean("ShanDePayCutService");
			singleResp = sd.queryBalance();
		}else{
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("您请求的通道暂不支持余额查询");
		}
		
		return singleResp;
	}
	/**
	 * test
	  * queryAgentpayFee(4.5　代付手续费查询
	  *没有具体需求先内部调用，不用签名
	  * @Title: queryAgentpayFee
	  * @Description: TODO
	  * @param @param json
	  * @param @return    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/queryAgentpayFee")
	public @ResponseBody SingleResp queryAgentpayFee(@RequestBody String json){
		logger.info("queryAgentpayFee:("+json+")");
		SingleResp singleResp =new SingleResp();
		Gson g = new Gson();
		OutRequestInfo resInfo = g.fromJson(json, OutRequestInfo.class);
		if(resInfo.getBanlance() <= (double)0 || null ==resInfo.getAccNo()){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("Banlance   AccNo 不能为空");
		}
		if(resInfo.getChannelId().equals("16")){//16 杉德   单位是分
			ShanDePayCutService sd=(ShanDePayCutService) SpringContextHelper.getInstance().getBean("ShanDePayCutService");
			
			singleResp = sd.queryAgentpayFee(resInfo);
		}else{
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("您请求的通道暂不支持代付手续费查询");
		}
		singleResp.setCode(DhbTranStatus.Succ.getCode());
		singleResp.setMessage("精确到分");
		return singleResp;
	}
	/**
	 *  杉德对账单申请
	  * getClearFileContent(
	  *没有具体需求先内部调用，不用签名
	  * @Title: getClearFileContent
	  * @Description: TODO
	  * @param @param json
	  * @param @return    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/getClearFileContent")
	public @ResponseBody SingleResp getClearFileContent(@RequestBody String json){
		logger.info("queryAgentpayFee:("+json+")");
		SingleResp singleResp =new SingleResp();
		Gson g = new Gson();
		OutRequestInfo resInfo = g.fromJson(json, OutRequestInfo.class);
		String timestamp = resInfo.getTimestamp();String bizType = resInfo.getBizType();
		if(null == timestamp || null == bizType){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("清算日期(timestamp)不能为空, 业务类型 BizType 不能为空,");
			return singleResp;
		}
		if( !(bizType.equals("1") || bizType.equals("2")) ){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("业务类型 BizType必须是1或者2   1-代付业务 2-代收业务");
			return singleResp;
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			df.parse(timestamp);
		} catch (Exception e) {
			e.printStackTrace();
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("清算日期(timestamp) 格式为 yyyyMMdd");
			return singleResp;
		}
		if(resInfo.getChannelId().equals("16")){//16 杉德   单位是分
			ShanDePayCutService sd=(ShanDePayCutService) SpringContextHelper.getInstance().getBean("ShanDePayCutService");
			
			singleResp = sd.getClearFileContent(resInfo);
		}else{
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("您请求的通道暂不支持代付手续费查询");
			return singleResp;
		}
		singleResp.setCode(DhbTranStatus.Succ.getCode());
		return singleResp;
	}
	/**
	 * 
	  * idVerifySd(
	  * 没有需求!!，这么写是测试用
	  * "商户未开通此产品" 正式环境如果用，要走商务流程
	  * @Title: idVerifySd
	  * @Description: TODO
	  * @param @param json
	  * @param @return    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/idVerifySd")
	public @ResponseBody SingleResp idVerifySd(@RequestBody String json){
		String logStr="【杉德二要素认证】";
		logger.info(logStr+":("+json+")");
		SingleResp singleResp =new SingleResp();
		try {
		Gson g = new Gson();
		OutRequestInfo resInfo = g.fromJson(json, OutRequestInfo.class);
		ShanDePayCutService sd=(ShanDePayCutService) SpringContextHelper.getInstance().getBean("ShanDePayCutService");
		singleResp = sd.idCardVerify(resInfo);
		} catch (Exception e) {
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("程序异常");
			e.printStackTrace();
			return singleResp;
		}
		return singleResp;
	}
}
