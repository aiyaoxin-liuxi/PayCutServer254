package com.dhb.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dhb.dao.service.KeyInfoDao;
import com.dhb.entity.form.ProxyBizJournalParam;
import com.dhb.service.ProxyBizJournalService;
import com.dhb.util.WebContextHolder;
import com.google.common.base.Strings;

@Controller
@RequestMapping("/proxyBizJournal")
public class ProxyBizJournalController {
	@Autowired
	private ProxyBizJournalService proxyBizJournalService;

	@Autowired
    private KeyInfoDao keyService;
	@RequestMapping(value = "list")
	public String list(ProxyBizJournalParam param, ModelMap model)
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
		//param.setMerchId("11111111111111111");
		proxyBizJournalService.queryByForm(param);
		model.addAttribute("form", param);
		return "proxyBizJournal/list";
	}
}
