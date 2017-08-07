package com.dhb.cgb.service;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import com.dhb.cgb.entity.BankOutResp;
import com.dhb.cgb.entity.BatchPayResp;
import com.dhb.cgb.entity.BatchPayRespItem;
import com.dhb.cgb.entity.SingleQueryResp;
import com.google.common.base.Strings;

public class CGBParseXml {
	private static CGBParseXml cgbParseXml = new CGBParseXml();
	private CGBParseXml(){
		
	}

	public static CGBParseXml getInstance(){
		return cgbParseXml;
	}
	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"gbk\" ?><BEDC><Message><commHead><tranCode>0034</tranCode><cifMaster>1000002822</cifMaster><entSeqNo>cgb20151124103441065</entSeqNo><tranDate>20151124</tranDate><tranTime>103441</tranTime><retCode>000</retCode><entUserId>100001</entUserId><password>50377bca1a01cd9398b84120fb2e2a00d19716485424041889df521e8b821d293d337f16527209f99cdf5d6673ddd2434f72732a78e04ef694b47f5e741af214daec3035fb746b7e5aa0bdf6d5691783459f5f4c359abe19d55b524edc80992ca07cb98934d086680491cbacf04471d43d5515165f3ec2750613425f2b30ac839d19dbb98adeb2643ce1da04abd5ed6a917fedb2dad86bc2a4491cb772445f94fd4582f62478964dfe39509ce04d2774e6713cc478f59ef3050c2c3f852dcfc732484f0863a2c28c6352e122121bc8b328b51d7fe08e7a9ca545cef051cca4f49ec257fe03c850e7168f17173a3bc0f2395346be5c567dc73b2d11c90aa4a286</password></commHead><Body><customerBatchNo>batch151123174449030</customerBatchNo><accountNo>101001513010006954</accountNo><allCount>2</allCount><allSalary>2.00</allSalary><allHandlefee>0.00</allHandlefee><count>0</count><errCount>0</errCount><unknowCount>2</unknowCount><records><record><customerSalarySeq>cgb20151123174449058</customerSalarySeq><fee>0.00</fee><bankstatus>8</bankstatus><ewpSequence/><ewpCheckcode/><errorreason/></record><record><customerSalarySeq>cgb20151123174449059</customerSalarySeq><fee>0.00</fee><bankstatus>A</bankstatus><ewpSequence/><ewpCheckcode/><errorreason/></record></records></Body></Message></BEDC>";
		BatchPayResp resp =CGBParseXml.getInstance().getBacthPayResp(xml);
		System.out.println(resp.getItems().size());
	}
	public  BatchPayResp getBacthPayResp(String respMsg){
		if(Strings.isNullOrEmpty(respMsg)){
			return null;
		}
		Digester digester=new Digester();
		digester.addObjectCreate("BEDC", BatchPayResp.class);
		digester.addBeanPropertySetter("BEDC/Message/commHead/entSeqNo", "entSeqNo");
		digester.addBeanPropertySetter("BEDC/Message/commHead/retCode", "retCode");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/customerBatchNo", "customerBatchNo");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/accountNo", "accountNo");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/allCount", "allCount");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/allSalary", "allSalary");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/allErrCount", "allErrCount");
		digester.addObjectCreate("BEDC/Message/Body/records/record", BatchPayRespItem.class);
		digester.addBeanPropertySetter("BEDC/Message/Body/records/record/customerSalarySeq", "customerSalarySeq");
		digester.addBeanPropertySetter("BEDC/Message/Body/records/record/retMes", "retMes");
		digester.addBeanPropertySetter("BEDC/Message/Body/records/record/bankstatus", "bankstatus");
		digester.addBeanPropertySetter("BEDC/Message/Body/records/record/flag", "flag");
		digester.addBeanPropertySetter("BEDC/Message/Body/records/record/salary", "salary");
		digester.addSetNext("BEDC/Message/Body/records/record", "addItems");
		

		StringReader reader = new StringReader(respMsg.trim());
		BatchPayResp parse =null;
		try {
			parse = (BatchPayResp) digester.parse(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parse;
	}
	public  BankOutResp getBankOutResp(String respMsg){
		if(Strings.isNullOrEmpty(respMsg)){
			return null;
		}
		Digester digester=new Digester();
		digester.addObjectCreate("BEDC", BankOutResp.class);
		digester.addBeanPropertySetter("BEDC/Message/commHead/entSeqNo", "entSeqNo");
		digester.addBeanPropertySetter("BEDC/Message/commHead/retCode", "retCode");
		digester.addBeanPropertySetter("BEDC/Message/commHead/Body/traceNo", "traceNo");
		StringReader reader = new StringReader(respMsg.trim());
		BankOutResp parse =null;
		try {
			parse = (BankOutResp) digester.parse(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parse;
	}
	public SingleQueryResp getSingleQueryResp(String xml){
		if(Strings.isNullOrEmpty(xml)){
			return null;
		}
		Digester digester=new Digester();
		digester.addObjectCreate("BEDC", SingleQueryResp.class);
		digester.addBeanPropertySetter("BEDC/Message/commHead/entSeqNo", "entSeqNo");
		digester.addBeanPropertySetter("BEDC/Message/commHead/retCode", "retCode");
		digester.addBeanPropertySetter("BEDC/Message/Body/hostStatus", "hostStatus");
		StringReader reader = new StringReader(xml.trim());
		SingleQueryResp parse =null;
		try {
			parse = (SingleQueryResp) digester.parse(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parse;
		
	}
}
