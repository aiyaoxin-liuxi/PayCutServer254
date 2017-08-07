package com.dhb.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dhb.cgb.service.CGBPayCutDhbService;
import com.dhb.entity.LeftTree;
import com.dhb.entity.form.ProxyBatchParam;
import com.dhb.service.ErrorService;
import com.dhb.service.ValidateService;
import com.google.common.collect.Lists;

@Controller
public class MenuController {
	
	private static final Log logger = LogFactory.getLog(MenuController.class);
	@Resource
	private ErrorService errorService;
	@Resource
	private ValidateService validateService;

	@RequestMapping(value="menu",method=RequestMethod.GET)
	public String menu(ProxyBatchParam param,Model model){
		List<LeftTree> root = Lists.newArrayList();
		LeftTree first = new LeftTree();
		root.add(first);
		Long id = 0l;
		int firstLevel = 1;
		int firstLevelOrder = 0;
		first.setId(id);
		++id;
		first.setName("代付业务");
		first.setLevel(firstLevel);
		first.setOrder(firstLevelOrder);
		++firstLevelOrder;
		List<LeftTree> childrenList = Lists.newArrayList();
		first.setChildren(childrenList);
		int secondLevel =2;
		int secondLevelOrder =0;
		LeftTree child0 = new LeftTree();
		childrenList.add(child0);
		child0.setId(id);
		++id;
		child0.setLevel(secondLevel);
		child0.setName("批量代付");
		child0.setOrder(secondLevelOrder);;
		++secondLevelOrder;
		child0.setUrl("/proxyPayBatch/list");
		
		LeftTree child1 = new LeftTree();
		childrenList.add(child1);
		child1.setId(id);
		++id;
		child1.setLevel(secondLevel);
		child1.setName("代发工资");
		child1.setOrder(secondLevelOrder);;
		++secondLevelOrder;
		child1.setUrl("/proxySendSalary/list");
		LeftTree child2 = new LeftTree();
		childrenList.add(child2);
		child2.setId(id);
		++id;
		child2.setLevel(secondLevel);
		child2.setName("交易查询");
		child2.setOrder(secondLevelOrder);;
		++secondLevelOrder;
		child2.setUrl("/proxyBizJournal/list");
		model.addAttribute("resources",root);
		return "menu";
	}
	
	public static LeftTree createLeftTree(Long id,Integer level,Integer order,String name,String url){
		LeftTree left = new LeftTree();
		left.setId(id);
		++id;
		left.setLevel(level);
		left.setName(name);
		left.setOrder(order);;
		++order;
		left.setUrl(url);
		return left;
	}
	
	public static void main(String[] args) {
		List<LeftTree> root = Lists.newArrayList();
		LeftTree first = null;
		Long id = 0l;
		Integer firstLevel = 1;
		Integer firstLevelOrder = 0;
		first=createLeftTree(id,firstLevel,firstLevelOrder,"代付业务","");
		root.add(first);
		List<LeftTree> childrenList = Lists.newArrayList();
		first.setChildren(childrenList);
		Integer secondLevel =2;
		Integer secondLevelOrder =0;
		LeftTree child0 = null;
		child0 =createLeftTree(id,secondLevel,secondLevelOrder,"批量代付","/proxyPayBatch/list");
		childrenList.add(child0);
		LeftTree child1 = null;
		child1=createLeftTree(id,secondLevel,secondLevelOrder,"代发工资","/proxySendSalary/list");
		childrenList.add(child1);
		System.out.println(first);
	}
}
