package com.dhb.controller;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dhb.umpay.service.UmpayPayCutService;
/**
 * 联动异步通知处理器
 * 接收报文：先对整个报文做URLDecoder,再对ret_msg做URLDecoder
 * 返回给umpay的报文：先对ret_msg做URLEncoder
 * @author wxw
 *
 */
@Controller
@RequestMapping(value="/dhb")
public class DHBUmpayNotifyController {
	public static Logger logger = Logger.getLogger(DHBUmpayNotifyController.class);
	@Resource
	private UmpayPayCutService umpayPayCutService;
	/**
	 * 联动文件结果通知（文件名有误或未接收到文件 才通知）
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/umpayFileNotify")
	public @ResponseBody String umpayFileNotify(HttpServletRequest request) {
		logger.info("【umpay file notify】报文："+request.getQueryString());
		String html=request.getQueryString();
		try {
			return umpayPayCutService.umpayFileNotify(html);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 扣款结果通知
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/umpayPayNotify")
	public @ResponseBody String umpayPayNotify(HttpServletRequest request){
		logger.info("联动优势扣款结果异步通知html："+request.getQueryString());
		String html = request.getQueryString();
		try {
			return umpayPayCutService.umpayPayNotify(html);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 代付结果通知
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/umpayForPayNotify")
	public @ResponseBody String umpayForPayNotify(HttpServletRequest request){
		logger.info("联动优势代付结果异步通知html："+request.getQueryString());
		String html = request.getQueryString();
		try {
			return umpayPayCutService.umpayPayNotifyForPay(html);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 直扣异步通知
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/umpayForCutNotify")
	public @ResponseBody String umpayCutNotify(HttpServletRequest request){
		logger.info("联动优势扣款结果异步通知html："+request.getQueryString());
		String html = request.getQueryString();
		try {
			return umpayPayCutService.umpayPayNotify(html);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
