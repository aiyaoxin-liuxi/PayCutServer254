package com.dhb.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.dhb.dao.service.KeyInfoDao;
import com.dhb.entity.ProxyAcpPay;
import com.dhb.entity.ProxyBatch;
import com.dhb.entity.exception.ProxyTransException;
import com.dhb.entity.form.ProxyBatchParam;
import com.dhb.entity.form.ProxyPayParam;
import com.dhb.service.ProxySendSalaryService;
import com.dhb.util.WebContextHolder;
import com.google.common.base.Strings;

@Controller
@RequestMapping("/proxySendSalary")
@SessionAttributes("isMyMerch")
public class ProxySendSalaryController {
	@Autowired
	private ProxySendSalaryService proxySendSalaryService;
	@Autowired
    private KeyInfoDao keyService;
	@RequestMapping(value = "list")
	public String list(ProxyBatchParam param, ModelMap model)
			throws ParseException {
		String token =WebContextHolder.getCurrentToken();
		if(Strings.isNullOrEmpty(token)){
			String merchId = param.getMerchId();
			String secretKey = param.getSecretKey();
			Boolean isMyMerch= (Boolean)model.get("isMyMerch");
			if(isMyMerch==null||!isMyMerch){
				
				isMyMerch=keyService.checkSecretKey(merchId, secretKey);
				if(!isMyMerch){
					return "error/nomerchid";
				}
			}
			model.addAttribute("isMyMerch", isMyMerch);
		}
		param.setMerchId("11111111111111111");
		proxySendSalaryService.queryByForm(param);
		model.addAttribute("form", param);
		return "proxySendSalary/list";
	}
	
	@RequestMapping(value = "verify")
	public String verify(ProxyBatchParam param, Model model)
			throws ParseException {
		param.setReview_status("0");
		proxySendSalaryService.queryByForm(param);
		model.addAttribute("form", param);
		return "proxySendSalary/verify";
	}

	@RequestMapping(value = "detail")
	public String detail(ProxyPayParam proxyPayParam, Model model) {
		List list = proxySendSalaryService
				.queryProxyPayBatchDetail(proxyPayParam.getBath_no());
		model.addAttribute("list", list);
		ProxyBatch proxyPayBatch = proxySendSalaryService.queryProxyBatchById(proxyPayParam.getBath_no());
		model.addAttribute("proxyBatch", proxyPayBatch);
		return "proxySendSalary/detail";
	}

	@RequestMapping(value = "view")
	public String view(@RequestParam String id, Model model) {
		ProxyAcpPay proxyPay = null;//entityDao.get(ProxyPay.class, id);//proxyPayDao.get(id);
		model.addAttribute("proxyPay", proxyPay);
		return "proxySendSalary/view";
	}
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String accountInfoImport(
			@RequestParam(value="channelId") String channelId,@RequestParam(value="merchId") String merchId,
			@RequestParam(value="file") CommonsMultipartFile file, ModelMap model)
			throws Exception {
		String token =WebContextHolder.getCurrentToken();
		if(Strings.isNullOrEmpty(token)){	
			Boolean isMyMerch= (Boolean)model.get("isMyMerch");
			if(isMyMerch==null||!isMyMerch){
				return "error/nomerchid";
			}
		}
		merchId ="11111111111111111";
		proxySendSalaryService.importBatchFile(channelId,merchId, file);
		model.addAttribute("result", "上传成功！");
		return "proxySendSalary/upload";
	}

	@RequestMapping(value = "audit", method = GET)
	public String audit(@RequestParam String id, Model model) {
		ProxyBatch proxyPayBatch = null;
		model.addAttribute("proxyPayBatch", proxyPayBatch);
		return "proxySendSalary/audit";
	}

	@RequestMapping(value = "audit", method = POST)
	public String audit(@Validated ProxyBatch proxyPayBatch, ModelMap model) throws ParseException,
			ProxyTransException {
		String token =WebContextHolder.getCurrentToken();
		if(Strings.isNullOrEmpty(token)){	
			Boolean isMyMerch= (Boolean)model.get("isMyMerch");
			if(isMyMerch==null||!isMyMerch){
				return "error/nomerchid";
			}
		}
		String batchId = proxyPayBatch.getBatchId();
		try {
			proxySendSalaryService.audit(proxyPayBatch);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ProxyTransException();
		}
		return "error/checkok";
		//return "redirect:/proxySendSalary/detail?bath_no="+batchId;
	}
	@RequestMapping(value = "uploadExcelFile", method = GET)
	public String toUploadExcel() throws IOException {
		return "proxySendSalary/upload";
	}
}
