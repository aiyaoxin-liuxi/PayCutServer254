package com.dhb.jyt.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhb.controller.DHBPayController;
import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.SequenceDao;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.RealNameInfo;
import com.dhb.entity.SingleResp;
import com.dhb.jyt.entity.JYTResp;
import com.dhb.jyt.entity.JYTTranType;

@Service
public class JYTRealNameService{
	private static final Log logger = LogFactory.getLog(DHBPayController.class);

	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private SequenceDao sequenceDao;


	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
    @Transactional
   	public SingleResp fourRealName(OutRequestInfo reqInfo)throws Exception{
   		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String tel = reqInfo.getTel();
   		String sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName and tel=:tel";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setAccNo(fromAccNo);
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		realName.setTel(tel);
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName,tel});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		StringBuffer xml = new StringBuffer();
   		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
   		String tranType = JYTTranType.FourRealName.getCode();
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head);
   		xml.append("<body><bank_card_no>").append(fromAccNo).append("</bank_card_no>")
   		   .append("<bank_code></bank_code>")
   		   .append("<id_num>").append(fromCertNo).append("</id_num>")
   		   .append("<id_name>").append(fromAccName).append("</id_name>")
   		   .append("<terminal_type>").append("03").append("</terminal_type>")
   		   .append("<bank_card_type>").append("A").append("</bank_card_type>")
   		   .append("<phone_no>").append(tel).append("</phone_no>")
   		   .append("</body></message>");
   		String toXml = xml.toString();
   		logger.info("JYT fourRealName xml:"+toXml);
   		String mac=JYTHelp.getInstance().signMsg(toXml);
        String respXml = JYTHelp.getInstance().sendRealNameMsg(toXml, mac);
           logger.info("JYT fourRealName resp:"+respXml);
           JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
           if(jresp!=null){
           	String respCode = jresp.getRespCode();
           	String respDesc = jresp.getRespDesc();
           	 if("S0000000".equals(respCode)){
                		String insertSql ="insert into dhb_realName(certNo,accNo,userName,tel) values(:certNo,:accNo,:userName,:tel)";
                		commonObjectDao.saveOrUpdate(insertSql, realName);
                		singleResp.setCode(DhbTranStatus.Succ.getCode());
                		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
                }else{
                	singleResp.setCode(DhbTranStatus.Fail.getCode());
                	singleResp.setMessage(respDesc);
                }
           }
   		return singleResp;
   	}

    @Transactional
   	public SingleResp bankCardRealName(OutRequestInfo reqInfo)throws Exception{
   		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setAccNo(fromAccNo);
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		StringBuffer xml = new StringBuffer();
   		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
   		String tranType = JYTTranType.BankCarRealName.getCode();
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head);
   		xml.append("<body><bank_card_no>").append(fromAccNo).append("</bank_card_no>")
   		   .append("<bank_code></bank_code>")
   		   .append("<id_num>").append(fromCertNo).append("</id_num>")
   		   .append("<id_name>").append(fromAccName).append("</id_name>")
   		   .append("<terminal_type>").append("03").append("</terminal_type>")
   		   .append("<bank_card_type>").append("A").append("</bank_card_type>")
   		   .append("<phone_no></phone_no>")
   		   .append("</body></message>");
   		String toXml = xml.toString();
   		logger.info("JYT bankCardRealName xml:"+toXml);
   		String mac=JYTHelp.getInstance().signMsg(toXml);
        String respXml = JYTHelp.getInstance().sendRealNameMsg(toXml, mac);
           logger.info("JYT bankCardRealName resp:"+respXml);
           JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
           if(jresp!=null){
           	String respCode = jresp.getRespCode();
           	String respDesc = jresp.getRespDesc();
           	 if("S0000000".equals(respCode)){
                		String insertSql ="insert into dhb_realName(certNo,accNo,userName) values(:certNo,:accNo,:userName)";
                		commonObjectDao.saveOrUpdate(insertSql, realName);
                		singleResp.setCode(DhbTranStatus.Succ.getCode());
                		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
                }else{
                	singleResp.setCode(DhbTranStatus.Fail.getCode());
                	singleResp.setMessage(respDesc);
                }
           }
   		return singleResp;
   	}

    @Transactional
   	public SingleResp certNoRealName(OutRequestInfo reqInfo)throws Exception{
   		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String sql = "select 1 from dhb_realName where certNo=:certNo and userName=:userName";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccName});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		StringBuffer xml = new StringBuffer();
   		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",6, '0');
   		String tranType = JYTTranType.CerdNoRealName.getCode();
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head);
   		xml.append("<body>")
   		   .append("<id_num>").append(fromCertNo).append("</id_num>")
   		   .append("<id_name>").append(fromAccName).append("</id_name>")
   		   .append("</body></message>");
   		String toXml = xml.toString();
   		logger.info("JYT realName xml:"+toXml);
   		String mac=JYTHelp.getInstance().signMsg(toXml);
        String respXml = JYTHelp.getInstance().sendRealNameMsg(toXml, mac);
           logger.info("JYT realName resp:"+respXml);
           JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
           if(jresp!=null){
           	String respCode = jresp.getRespCode();
           	String respDesc = jresp.getRespDesc();
           	 if("S0000000".equals(respCode)){
           		
                		String insertSql ="insert into dhb_realName(certNo,userName) values(:certNo,:userName)";
                		commonObjectDao.saveOrUpdate(insertSql, realName);
                		singleResp.setCode(DhbTranStatus.Succ.getCode());
                		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
                }else{
                	singleResp.setCode(DhbTranStatus.Fail.getCode());
                	singleResp.setMessage(respDesc);
                }
           }
   		return singleResp;
   	}

	public CommonObjectDao getCommonObjectDao() {
		return commonObjectDao;
	}
}
