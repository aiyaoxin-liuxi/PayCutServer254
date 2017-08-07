//package com.dhb.servlet;
//
//import java.io.IOException;
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.dhb.dao.service.DhbMobileTransactionDao;
//import com.dhb.entity.DhbMobileTransaction;
//import com.dhb.mobile.entity.MobileRspInfo;
//import com.dhb.mobile.service.MobRechargeService;
//
//public class OFMobBackServlet extends HttpServlet {
//	private Logger logger = LoggerFactory.getLogger(OFMobBackServlet.class);
//	@Autowired
//	private DhbMobileTransactionDao dhbMobileTransactionDao;
//	@Resource
//	private MobRechargeService mobRechargeService;
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	/**
//     * @see HttpServlet#HttpServlet()
//     */
//    public OFMobBackServlet() {
//        super();
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doPost(request, response);
//	}
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		try {
//			logger.info("=========欧飞后台通知开始============");
//			request.setCharacterEncoding("UTF-8");
//			response.setCharacterEncoding("UTF-8");
//			response.setContentType("text/html;charset=UTF-8");
//			
//			String result = request.getReader().readLine();
//			logger.info("OF 异步通知返回结果="+result);
//			//解析欧飞返回的xml文件
//        	Document document = DocumentHelper.parseText(result);
//        	Element root = document.getRootElement();
//        	MobileRspInfo rspinfo = new MobileRspInfo();
//        	
//        	String retcode = root.element("retcode").getText();
//        	String err_msg = root.element("err_msg").getText();
//        	rspinfo.setRetcode(retcode);
//        	rspinfo.setErrmsg(err_msg);
//        	if("1".equals(retcode)){
//        		String orderid= root.element("orderid").getText();
//        		String cardids =root.element("cardid").getText();
//        		String cardnums =root.element("cardnum").getText();
//        		String ordercash =root.element("ordercash").getText();
//        		String cardname =root.element("cardname").getText();
//        		String sporder_ids =root.element("sporder_id").getText();
//        		String game_userids =root.element("game_userid").getText();
//        		String game_state =root.element("game_state").getText();
//        		
//        		rspinfo.setOrderid(orderid);
//        		rspinfo.setCardid(cardids);
//        		rspinfo.setCardnum(cardnums);
//        		rspinfo.setOrdercacsh(ordercash);
//        		rspinfo.setCardname(cardname);
//        		rspinfo.setSporderid(sporder_ids);
//        		rspinfo.setMobile(game_userids);
//        		rspinfo.setGamestate(game_state);
//        		
//        		List<DhbMobileTransaction> transList = dhbMobileTransactionDao.selectTransactionBysporderId(sporder_ids);
//        		if(transList.size()!=1){
//        			
//        		}
////        		mobRechargeService.doOperForOFBack(transList.get(0), rspinfo);
//        	}
//        	
//			logger.info("=========后台通知结束============");
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
//}
