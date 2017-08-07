package com.mondial.psap.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.UserInfo;
import com.dhb.util.HttpHelp;
import com.dhb.util.MD5;
import com.dhb.util.PropFileUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class DaoTest {
	private ApplicationContext context;
	@Before
	public void init(){
		 context = new ClassPathXmlApplicationContext("context-database.xml","context-bean.xml");
	}
	@Test
	public void testTemplateDao() throws IOException {
		 context = new ClassPathXmlApplicationContext("context-database.xml","context-bean.xml");
		
		// TemplateDao dao = context.getBean(TemplateDao.class);
		
		
	}
	@Test
	public void testMonitorInfoDao(){
		 context = new ClassPathXmlApplicationContext("context-database.xml","context-bean.xml");
		
	}
	@Test
	public void testUserInfo(){
		CommonObjectDao dao =context.getBean(CommonObjectDao.class);
		String sql = "select * from USER_INFO@dbelink where user_id=:id";
		dao.findList(sql, UserInfo.class, new Object[]{"13021985911"});
	}
	@Test
	public void testEaOrderDao(){
		CommonObjectDao dao =context.getBean(CommonObjectDao.class);
		String sql = "insert into my_bank_info(id,swiftcode, bankfullName, tel,address,idsql) values (:id, :swiftcode, :bankfullName,:tel,:address,:idSql)";
		try {
			List<String> lists=Files.readLines(new File("F:\\study\\java\\爬虫\\GuozhongCrawler-master\\src\\test\\java\\com\\guozhong\\queue\\my\\bankinfo.txt"), Charset.forName("gbk"));
			int i=49362;
			for(String line:lists){
				
				List<String> itemList =Splitter.on(CharMatcher.BREAKING_WHITESPACE).trimResults().splitToList(line);
				String querySql = "select 1 from my_bank_info where id=:id";
				String id = itemList.get(0);
				String oneVal=dao.findSingleVal(querySql, new Object[]{id});
				if(oneVal==null){
					BankCode code = new BankCode();
					code.setId(id);
					code.setSwiftcode(itemList.get(1));
					code.setBankfullName(itemList.get(2));
					code.setTel(itemList.get(3));
					code.setAddress(itemList.get(4));
					code.setIdSql(i);
					dao.saveOrUpdate(sql, code);
				}else{
					System.out.println("exist id:"+id);
				}
				i++;
			}
			System.out.println("over");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	/*	String sql = "update cgb_tran_record set respStatus=:respStatus where recordId=:recordId";
		CGBTranRecord record = new CGBTranRecord();
		record.setRecordId("e201fc6f246a47909d2712049ab5214b");
		record.setRespStatus("test1");
		dao.saveOrUpdate(sql, record);*/
		/* context = new ClassPathXmlApplicationContext("context-database.xml","context-bean.xml");
		 EaOrderDao dao = context.getBean(EaOrderDao.class);
		 EaOrder  eaOrdor =dao.getEaOrderById("2100582");
		 System.out.println(eaOrdor);*/
	}
	
	@Test
	public void testSingleCut(){
	
		String merchId ="111301000000000";
		double money =50.00;
		String accNo = "6217860100000372608";
		String trano = "12342222342423434424544";
		String key = merchId+money+accNo+trano;
		String certNo ="341281198403050497";
		String certType ="01";
		String accType ="00";
		String accName = "郑和进";
		String bankName = "中国银行";
		String channelId ="2";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/singlePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}

}
